import _thread
import logging
import socket
import sys
import threading
import time
from datetime import datetime
from typing import Dict, List, Optional

import dns.exception
from dns import resolver, rdatatype
from dns.rrset import RRset
from dnslib import DNSRecord, QTYPE, DNSHeader, RR, A, SOA, CNAME, DNSError

# logging.logger is more elegant than print
logger = logging.getLogger('dns.server')
logging.basicConfig(level=logging.INFO, datefmt='%H:%M:%S',
                    format='%(asctime)s ~ \033[96m%(funcName)s\033[0m: %(message)s')


class CacheManager:
    """CacheManager saves info of root servers and a list of RRset (CNAME and A) for each query_name.

    Since the 13 root servers never changes, we tend to save them permanently:
    after the program starts, it only queries the list of root servers once and saves this for use.

    Since the TTL of CNAME and A records need not be the same, we use the min TTL to represent the list's TTL,
    otherwise, there's a dilemma that we may have the CNAME in the cache, but A records are already deleted. Also,
    the docs didn't specify that, the template only provided us a dict, we follow this and do little modifications.

    Attributes:
        root: A dict of 13 root servers. Key = server name, Value: ip address
        cache: Regular queries will save their info. Key: domain, Value: [[RRset], min_TTL]
    """

    def __init__(self):
        self.root: Dict[str, str] = {}
        self.cache: Dict[str: [List[RRset], int]] = {}

        # stand-alone thread for checking and deleting the cache that passes TTL
        _thread.start_new_thread(self.daemon_ttl, ())

    def __str__(self):
        s = 'type=CacheManager\n'
        if self.root:
            s += 'Permanent cache: root servers\n'
            for k, v in self.root.items():
                s += f'{k}\t(inf)\tIN\tA\t{v}\n'
        s += 'Dynamic caches: TTL maintained\n'
        for k, v in self.cache.items():
            s += f';; {k}: (cache will expire at {v[1]})\n'
            for rr in v[0]:
                s += f'{str(rr)}\n'
        return s

    def read_cache(self, domain_name) -> Optional[List[RRset]]:
        """Check if the dict contains cache for the specified domain

        It also checks the TTL before returning RRsets, to prevent issues caused by multithreading,
        though we all know that the GIL should be able to help to avoid threading-unsafe operations.

        Args:
            domain_name: The query name, passed in by dig.

        Returns:
            The list of relevant RRsets, containing necessary CNAME and A records,
            if the target domain was just queried and the records do not pass the min TTL,
            otherwise, return None.
        """
        return rec[0] \
            if ((rec := self.cache.get(domain_name))
                and rec[1] >= round(datetime.now().timestamp())) \
            else None

    def write_cache(self, domain_name, response: List[RRset]):
        """Update (store) a pile of RRset for a domain.

        All RRset related to such domain will be stored within a list,
        both to better manage the TTL and to minimize the size of dict.

        We use the min TTL among all the RRsets to represent the list's TTL.

        Args:
            domain_name: The domain name when dig sends the query.
            response: All the related RRsets (CNAME, A) in the query chain.
        """
        ttl = sys.maxsize
        for rrs in response:
            ttl = min(ttl, rrs.ttl)
        self.cache[domain_name] = [response, round(datetime.now().timestamp()) + ttl]

    def daemon_ttl(self):
        """Always runs in a stand-alone thread, check the cache dict once per second."""
        while True:
            timestamp = round(datetime.now().timestamp())
            for domain in list(self.cache.keys()):
                if timestamp > self.cache[domain][1]:  # cache[domain][1] is the timestamp when the min TTL passes
                    del self.cache[domain]
            time.sleep(1)  # ttl manager updates pre second


class ReplyGenerator:
    """ReplyGenerator provides two util functions for generating DNSRecord from RRsets or None."""

    @staticmethod
    def reply_not_found(income_record: DNSRecord) -> DNSRecord:
        """Reply when cannot resolve the corresponding ip or domain name is not found.

        Args:
            income_record: The income DNS record from dig.

        Returns:
            The reply record.
        """
        header = DNSHeader(id=income_record.header.id, bitmap=income_record.header.bitmap, qr=1)
        header.set_rcode(3)  # 3 DNS_R_NXDOMAIN, 2 DNS_R_SERVFAIL, 0 DNS_R_NOERROR
        return DNSRecord(header, q=income_record.q)

    @staticmethod
    def reply(income_record: DNSRecord, res: List[RRset]) -> DNSRecord:
        """General reply generator when a list of RRset is provided.

        This function is called by DNSHandler#handle,
        it generates a DNSRecord to send back with necessary CNAME and A records.

        Args:
            income_record: The income DNS record from dig, including necessary info e.g. id and bitmap.
            res: All CNAME and A records needed by the query.
                 The list is either provided by CacheManager#read_cache or DNSHandler#query.

        Returns:
            The reply record.
        """
        header = DNSHeader(id=income_record.header.id, bitmap=income_record.header.bitmap, qr=1)
        record = DNSRecord(header, q=income_record.q)
        for rrs in res:
            for itm in rrs:
                record.add_answer(RR(
                    rrs.name.to_text(),
                    rrs.rdtype,
                    rdata=A(itm.to_text()) if rrs.rdtype == 1 else CNAME(itm.to_text()),  # only contains A or CNAME
                    ttl=rrs.ttl
                ))
        return record

    @staticmethod
    def reply_a(income_record: DNSRecord, ip: str, ttl: int = None) -> DNSRecord:
        """The demo function for replying an A record.

        Args:
            income_record: The income dns record from dig.
            ip: The founded domain ip.
            ttl: Time to live for this record.

        Returns:
            The reply record.
        """
        r_data = A(ip)
        header = DNSHeader(id=income_record.header.id, bitmap=income_record.header.bitmap, qr=1)
        domain = income_record.q.qname
        query_type_int = QTYPE.reverse.get('A') or income_record.q.qtype
        return DNSRecord(
            header,
            q=income_record.q,
            a=RR(domain, query_type_int, rdata=r_data, ttl=ttl),
        )


class DNSServer:
    """The server listens port 5533 of localhost, which dig sends queries to.

    Supports multithreading. With a thread-safe socket (a lock is used) to receive the query
    and reply the answer, each process of iteratively query itself is a thread, thus is non-blocking.

    Attributes:
         ip: The ip which the dns server provides service to, should be localhost here.
         port: Since localhost isn't a real dns server after all, we need to specify the listening port here and in dig.
         socket: Listening the port and receive/send the queries/answer, runs over UDP.
         lock: Thread lock for socket.
         last_query: Note that dig sends several UDP requests if not receive a response in a time-range,
                     but we should not open multiply threads for the same query (with same message, or same ID).
    """

    def __init__(self, source_ip: str, source_port: int,
                 ip='127.0.0.1', port=5533, loc_dns='8.8.8.8',
                 cache: Optional[CacheManager] = None):
        self.source_ip = source_ip
        self.source_port = source_port
        self.ip = ip
        self.port = port
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.socket.bind((self.ip, self.port))
        self.lock = threading.Lock()
        self.cache_manager = cache or CacheManager()
        self.dns_handler = DNSHandler(self.source_ip, self.source_port, self.cache_manager, loc_dns)
        self.last_query = b''

    def start(self):
        try:
            while True:
                # it first gets a query request blocking (socket#receive)
                message, address = self.receive()
                if message == self.last_query:
                    continue

                self.last_query = message
                # print(str(self.cache_manager))

                # then will not wait the dns server to do the query. it just starts a thread and lets it go
                thread = threading.Thread(target=self.dns_handler.handle,
                                          args=(message, address, self.socket, self.lock))
                thread.daemon = False
                thread.start()
        except KeyboardInterrupt:
            logger.info('bye')
            exit(0)

    def receive(self):
        return self.socket.recvfrom(8192)

    @staticmethod
    def reply(address, response: DNSRecord, sock: socket, lock: threading.Lock):
        # in some system, socket is not thread-safe
        # we use a lock to ensure that one thread will wait for another to complete sending first
        with lock:
            sock.sendto(response.pack(), address)


class DNSHandler(threading.Thread):
    def __init__(self, source_ip, source_port,
                 cache_manager: Optional[CacheManager],
                 loc_dns='8.8.8.8', dns_prt=53):
        super().__init__()
        self.source_ip = source_ip
        self.source_port = source_port
        self.LOCAL_DNS_SERVER_IP = loc_dns
        self.DNS_SERVER_PORT = dns_prt
        self.cache_manager = cache_manager or CacheManager()
        self.lock = threading.Lock()
        # Instantiate dns.resolver.Resolver and set flag (value of rd) as 0.
        # inject to ctor to prevent unnecessary cost
        # self.dns_resolver = resolver.Resolver()
        # self.dns_resolver.flags = 0x0000

    def handle(self, message: bytes,
               address, sock: socket, lock: threading.Lock):
        resp = self.msg2rec(message)
        DNSServer.reply(address, resp, sock, lock)

    def msg2rec(self, message: bytes) -> Optional[DNSRecord]:
        """Interact with message sent by dig.

        First parse the query name, then handles the cache manager:
        if there exists a cache, directly return the cache
        otherwise, do the query and add it to the cache (if no error).

        Args:
            message: Query message sent by dig.

        Returns:
            A DNSRecord, which contains all the CNAME and A records in the answer section.
            If the query name is wrong, returning a DNSRecord with no answer, but indicates the error.
        """
        try:
            income_record = DNSRecord.parse(message)
        except DNSError:
            return None
        domain_name = str(income_record.q.qname).strip('.')

        if query_cache := self.cache_manager.read_cache(domain_name):
            # print('read from cache')
            logger.info('read from cache')
            return ReplyGenerator.reply(income_record, query_cache)
        else:
            if resp := self.query(domain_name, self.source_ip, self.source_port):
                self.cache_manager.write_cache(domain_name, resp)
                return ReplyGenerator.reply(income_record, resp)
            else:
                return ReplyGenerator.reply_not_found(income_record)

    def query(self, query_name, source_ip, source_port, retry=10) -> Optional[List[RRset]]:
        """Whole iterative process of dns.

        First get the IP address of root server,
        then enter into a loop until get response whose answer type is A.

        Args:
            query_name: Target domain name to get the ip.
            source_ip: Source IP address of query, aka. local dns server.
            source_port: Source port number of query.
            retry: If one attempt of querying timeout, retry several times, then return not found.

        Returns:
            If the query succeeded, return a list of RRset containing all true A type answers and CNAME (if exists).
            If the query_name is not a valid domain, or timeout after retrying several times, return None.
        """
        # Get IP address of root server, normally should just read from cache instead of query again
        root_servers = self.cache_manager.root or self.query_root(source_ip, source_port)
        server_ip = self.LOCAL_DNS_SERVER_IP
        server_port = self.DNS_SERVER_PORT

        # if one query timeout, keep retrying
        # but if is other errors that seems not able to fix, stop trying and return None
        retrying = retry
        ans = []
        while retrying > 0:
            try:
                self.ans_handler(query_name, source_ip, source_port,
                                 list(root_servers.values()), ans)
                break
            except dns.exception.Timeout:
                retrying -= 1
            except:  # other exceptions, such as SyntaxError
                return None
        return ans

    def ans_handler(self, query_name, source_ip, source_port,
                    server_ip: List[str], answers: List[RRset],
                    timeout=5):
        dns_resolver = resolver.Resolver()
        dns_resolver.flags = 0x0000

        dns_resolver.nameservers = server_ip

        with self.lock:
            resp = dns_resolver.resolve(qname=query_name, rdtype=rdatatype.A,
                                        source=source_ip,
                                        raise_on_no_answer=False, source_port=source_port)
        # answer only contains CNAME or A
        if len(resp.response.answer) > 0:
            # if is A, return the answer is okay
            answers.extend(resp.response.answer)
            for r in resp.response.answer:
                if r.rdtype == 1:
                    return
            # if is CNAME, ans_handler again to the CNAME, ask the root again
            if resp.response.answer[0].rdtype == 5:  # CNAME
                root_servers = self.cache_manager.root or self.query_root(source_ip, source_port)
                self.ans_handler(resp.response.answer[0][0].to_text(),
                                 source_ip, source_port,
                                 list(root_servers.values()),
                                 answers, timeout)
            return

        else:
            if len(resp.response.additional) > 0:  # using ip in additional
                dns_ip = [i.to_text()
                          for rrs in resp.response.additional
                          for i in rrs.items]
            else:  # using NS in authority, ask iteratively to get the nameservers' ip
                dns_ns = [i.to_text()
                          for rrs in resp.response.authority if rrs.rdtype == 2
                          for i in rrs.items]
                tmp = []
                for ns in dns_ns:
                    tmp.extend(self.query(ns, source_ip, source_port))
                dns_ip = [i.to_text()
                          for rrs in tmp
                          for i in rrs.items]

            # in either case, we need to use a new nameserver to query the same query_name
            self.ans_handler(query_name, source_ip, source_port,
                             dns_ip, answers, timeout)

    def query_root(self, source_ip, source_port) -> Dict[str, str]:
        """Query IP address and name of root DNS server, also update the cache manager.

        Args:
            source_ip: Source IP address of query.
            source_port: Source port number of query.

        Returns:
            A dict of (13) root servers. Key: server_name: str, Value: server_ip: str
        """
        dns_resolver = resolver.Resolver()
        dns_resolver.flags = 0x0000

        # Set initial IP name, address and port number.
        server_name = 'Local DNS Server'
        server_ip = self.LOCAL_DNS_SERVER_IP
        server_port = self.DNS_SERVER_PORT

        # Set nameservers of dns_resolver as list of IP address of server.
        dns_resolver.nameservers = [server_ip]

        # Use dns_resolver to query name of root server and receive response.
        with self.lock:
            answer = dns_resolver.resolve(qname='', rdtype=rdatatype.NS,
                                          source=source_ip, source_port=source_port,
                                          raise_on_no_answer=False)
        root_servers = {n.to_text(): None
                        for n in answer.response.answer[0]}
        # now gets the dict contains 13 items, key = server domain, value = None

        # Use dns_resolver to query address of root server and receive response.
        for rt in root_servers:
            with self.lock:
                answer = dns_resolver.resolve(qname=rt, rdtype=rdatatype.A,
                                              source=source_ip, source_port=source_port,
                                              raise_on_no_answer=False)
            root_servers[rt] = answer.response.answer[0][0].to_text()  # update the corresponding ip address
        self.cache_manager.root = root_servers.copy()  # also handles the cache
        return root_servers


if __name__ == '__main__':
    source_ip = input('Enter your ip: ')
    source_port = input('Enter your port: ')
    source_ip = str(source_ip)
    source_port = int(source_port) if source_port else 50000  # uses port 50000 if didn't set the port

    # test network condition, lets also reuse the cache of root server
    cm = CacheManager()
    dns_handler = DNSHandler(None, None, cm, '114.114.115.115')
    # for my network condition, 8.8.8.8 often get stuck, but you can delete the last param to use 8.8.8.8
    root_severs = dns_handler.query_root(source_ip=source_ip, source_port=source_port)
    for k, v in root_severs.items():
        print(f'{k}\t\t{v}')
    print()

    local_dns_server = DNSServer(source_ip, source_port, loc_dns='114.114.115.115', cache=cm)
    logger.info('start running local dns server')
    local_dns_server.start()
