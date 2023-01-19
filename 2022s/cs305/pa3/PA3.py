import math
import re
import sys
from queue import PriorityQueue
from typing import TextIO, List, Optional, Dict

ip_device_idx = {}


class IPv4:

    def __init__(self, cidr: str):
        self.cidr = cidr
        self.addr, self.net_prefix = cidr.split('/')
        self.net_prefix = int(self.net_prefix)
        assert 0 <= self.net_prefix <= 32

        p32 = []
        for p in self.addr.split('.'):
            p = int(p)
            p8 = [0] * 8
            for fb in range(8):
                p8[7 - fb] = p % 2
                p //= 2
            p32.extend(p8)
        for msk in range(self.net_prefix, len(p32)):
            p32[msk] = 0

        self.subnet32 = p32
        self.subnet = '.'.join([str(sum((p32[p * 8 + b] * 2 ** (7 - b) for b in range(8))))
                                for p in range(4)]) + f'/{self.net_prefix}'

    def __eq__(self, other):
        return False if type(other) != type(self) else self.addr == other.addr

    def __lt__(self, other):
        assert type(other) == IPv4
        return self.subnet < other.subnet

    def __hash__(self):
        return hash(self.addr)

    def __str__(self):
        return self.addr

    def agg(self, other):
        max_same = 0
        while max_same < min(self.net_prefix, other.net_prefix) and self.subnet32[max_same] == other.subnet32[max_same]:
            max_same += 1
        return IPv4(f'{self.addr}/{max_same}')


class Device:

    def __init__(self, ints, override=True):
        global ip_device_idx

        if type(ints) == str:
            int_ips = re.findall('\'[\\d.]+/\\d+\'', ints)
            self.interfaces = [IPv4(i.strip("'")) for i in int_ips]
        elif type(ints) == IPv4:
            self.interfaces = [ints]

        self.conn = {}
        self.__dist = math.inf

        if override:
            for i in self.interfaces:
                ip_device_idx[i] = self

    def __eq__(self, other):
        if type(other) != Device:
            return False
        return set(self.interfaces) == set(other.interfaces)

    def __lt__(self, other):
        assert type(other) == Device
        return self.__dist < other.__dist

    def __hash__(self):
        return sum(hash(i) for i in self.interfaces)

    def __str__(self):
        return f'<Device {", ".join(str(i) for i in self.interfaces)}>'


class Edge:

    def __init__(self, info: str):
        src, dst, cost = info.strip('()').split(',')
        self.src = IPv4(src.strip("'"))
        self.dst = IPv4(dst.strip("'"))
        self.cost = int(cost)

        ip_device_idx[self.src].conn[self.src] = self
        ip_device_idx[self.dst].conn[self.dst] = self

    def __str__(self):
        return f'{str(self.src)} -> {str(self.dst)}: {self.cost}'


class Network:

    def __init__(self, _ips: List[IPv4], _routers: List[Device], _hosts: List[Device], _edges: List[Edge]):
        self.ips = _ips
        self.routers = _routers
        self.hosts = _hosts
        self.edges = _edges

        self.subnets = {}
        for i in self.ips:
            if i.subnet not in self.subnets.keys():
                self.subnets[i.subnet] = []
            self.subnets[i.subnet].append(i)

    @staticmethod
    def dijkstra(src: Device, dst: Optional[Device]):
        dist = {d: math.inf for d in ip_device_idx.values()}
        subgraph = set()
        last = {}

        pq = PriorityQueue()
        dist[src] = 0
        src.__dist = 0
        pq.put((0, src))

        while not pq.empty():
            _, node = pq.get()
            subgraph.add(node)

            for edge in node.conn.values():
                op = ip_device_idx[edge.dst if ip_device_idx[edge.dst] != node else edge.src]
                if op not in subgraph and dist[node] + edge.cost < dist[op]:
                    dist[op] = dist[node] + edge.cost
                    op.__dist = dist[op]
                    last[op] = node
                    pq.put((dist[op], op))

        if not dst:
            return dist, last

        path = [dst]
        while True:
            path.insert(0, last[path[0]])
            if path[0] == src:
                break
        return path

    @staticmethod
    def path(src: IPv4, dst: IPv4):
        pth = Network.dijkstra(ip_device_idx[src], ip_device_idx[dst])
        for i, d in enumerate(pth[:-1]):
            for interface, edge in d.conn.items():
                if ip_device_idx[edge.src] == pth[i + 1] or ip_device_idx[edge.dst] == pth[i + 1]:
                    print(edge.src if d == ip_device_idx[edge.src] else edge.dst, end=' ')
                    print(edge.src if d == ip_device_idx[edge.dst] else edge.dst, end=' ')
        print()

    @staticmethod
    def aggregate(conn: Dict[Optional[IPv4], List[str]]):
        agg = {}

        if None in conn.keys():
            agg = {v: None for v in conn[None]}
            conn.pop(None)

        for interface, subnets in conn.items():
            subnets = [IPv4(i) for i in subnets]
            subnets.sort()

            if len(subnets) == 1:
                agg[subnets[0].subnet] = interface
                continue

            while subnets:
                tmp_agg, subnets = subnets[0], subnets[1:]
                while tmp_agg.net_prefix > 16 and subnets:
                    g = tmp_agg.agg(subnets[0])
                    if g.net_prefix >= 16:
                        tmp_agg = g
                        subnets = subnets[1:]
                    else:
                        break
                agg[tmp_agg.subnet] = interface

        disp = sorted(agg.keys())
        for i in disp:
            if agg[i]:
                print(f'{i} via {agg[i]}')
            else:
                print(f'{i} is directly connected')

    def table(self, router: Device):
        dist, last = Network.dijkstra(router, None)
        rts = {}

        # each subnet should be reached by the node in it that has the min cost
        # after checking its cost by dijkstra, revert the route and get the interface
        for sn, snd in self.subnets.items():
            d3, via = math.inf, None
            for dip in snd:
                d = ip_device_idx[dip]
                if dist[d] < d3:
                    d3 = dist[d]
                    via = dip
            rts[sn] = None if d3 == 0 else via

        interface_subnet = {}
        __opt = sorted(rts.keys())
        for sn in __opt:
            if not rts[sn]:
                print(f'{sn} is directly connected')
                if None not in interface_subnet.keys():
                    interface_subnet[None] = []
                interface_subnet[None].append(sn)
            else:
                ptr = ip_device_idx[rts[sn]]
                while last[ptr] != router:
                    ptr = last[ptr]

                o3cost, o3inter = math.inf, None
                for inter, edge in router.conn.items():
                    if (ip_device_idx[edge.src] == ptr or ip_device_idx[edge.dst] == ptr) and edge.cost < o3cost:
                        o3cost, o3inter = edge.cost, inter
                print(f'{sn} via {o3inter}')
                if o3inter not in interface_subnet.keys():
                    interface_subnet[o3inter] = []
                interface_subnet[o3inter].append(sn)

        print('After')
        Network.aggregate(interface_subnet)


def main(f: TextIO):
    # read input
    ips = [IPv4(cidr) for cidr in f.readline().split()]
    routers = [Device(rfs) for rfs in f.readline().split()]
    hosts = [Device(ip) for ip in ips if ip not in ip_device_idx.keys()]
    edges = [Edge(_e) for _e in f.readline().split()]

    # also build index for subnet
    net = Network(ips, routers, hosts, edges)

    test_num = int(f.readline())
    for _ in range(test_num):
        cmd = f.readline().split()
        if not cmd:
            continue
        if cmd[0] == 'PATH':
            net.path(IPv4(cmd[1]), IPv4(cmd[2]))
        else:
            # create helper Device, should not override the device table that contains the connection info
            tar_router = Device(cmd[1], override=False)
            inner = tar_router.interfaces[0]
            net.table(ip_device_idx[inner])


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('Should specify the input file, got no args')
        exit(1)

    try:
        with open(sys.argv[1], 'r') as _f:
            main(_f)
    except FileNotFoundError as e:
        print(e)
        exit(1)
