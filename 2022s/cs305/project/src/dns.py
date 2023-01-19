import argparse
import difflib
import json
import logging
import threading
import pickle
import time
import re
import requests
from flask import Flask, make_response, request, jsonify

from proxy import test_ports_open

# UTIL GLOBAL OBJECTS ###################################

app = Flask(__name__)
app.logger.setLevel(logging.INFO)

jdec = json.JSONDecoder()

# STATIC FINAL FIELDS ###################################

strategies = ('Round Robin', 'Weighted Round Robin', 'IP Hash',  # static
              'Minimal Connections', 'Weighted Minimal Connections',  # dynamic
              'Resource Based', 'Weighted Response Time')

# ROUND ROBIN ###########################################

rr_round_id = 0

lock = threading.Lock()


@app.route('/')
def dispatch():
    global rr_round_id
    if strategy == 'Round Robin':
        prt = server_list[rr_round_id]
        rr_round_id += 1
        rr_round_id %= len(server_list)

    elif strategy == 'Weighted Round Robin':
        tmp = list(filter(lambda w: w[1] > rr_round_id, wrr_prefix_sum))
        mn = tmp[0]
        for t in tmp:
            if t[1] < mn[1]:
                mn = t
        prt = mn[0]
        rr_round_id += 1
        rr_round_id %= wrr_sum - 1

    elif strategy == 'IP Hash':
        prt = server_list[hash(request.environ.get("REMOTE_PORT", "?")) % len(server_list)]

    elif strategy == 'Minimal Connections':
        server, load = 0, 9999999999
        for s in server_list:
            stat = requests.get(f'http://127.0.0.1:{s}/server-status').text
            conn = re.findall('<dt>.* requests currently being processed', stat)[0]
            conn = int(conn.split('<dt>')[1].split('request')[0])
            app.logger.info(f'Server {s}: conn {conn}')
            if conn < load:
                server, load = s, conn
        prt = server

    app.logger.info(f'\x1b[1;36mAssign {request.remote_addr}:{request.environ.get("REMOTE_PORT", "?")}'
                    f' with server {prt}\x1b[0m')
    return make_response(str(prt))


@app.route('/danmaku', methods=['POST'])
def receive_new():
    global danmakus
    form = request.form
    new_dms = form.get('dms', type=str)
    dms = jdec.decode(new_dms)
    np_dms = [(d['ts'], d['cid'], d['val'], d['at']) for d in dms]
    danmakus.extend(np_dms)
    return ''


@app.route('/danmaku', methods=['GET'])
def sync_danmakus():
    return make_response(jsonify(danmakus))


class DaemonHistory(threading.Thread):
    def run(self):
        """Save (backup) the danmakus into file pre second."""
        if not args.loaddanmaku:
            return
        try:
            while True:
                with lock:
                    with open(args.loaddanmaku, 'wb') as ff:
                        pickle.dump(danmakus, ff)
                time.sleep(1)
                if lock.locked():
                    lock.release()
        except KeyboardInterrupt:
            with open(args.loaddanmaku, 'wb') as ff:
                pickle.dump(danmakus, ff)
                ff.flush()
            ff.close()


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--port', '-p', required=True, help='DNS server will listen on this port')
    parser.add_argument('--loaddanmaku', '-l', help='Load danmakus saved in a file')
    parser.add_argument('--servers', '-s', required=True, help='Path to the netsim config file')
    parser.add_argument('--weights', '-w',
                        help='Path to the file containing weights for the servers, may not used in every strategy')
    parser.add_argument('--strategy', '-g', default='Round Robin',
                        help='The algorithm applied to balance the load of servers')
    args = parser.parse_args()
    try:
        dns_port = int(args.port)
        with open(args.servers, 'r', encoding='utf-8') as f:
            server_list = [int(s) for s in f.read().split()]
        spair = difflib.get_close_matches(args.strategy, strategies, 1, cutoff=0.3)
        if not spair:
            raise BaseException('Invalid strategy name: none paired, should be one of (Round Robin '
                                '| Weighted Round Robin | IP Hash | Minimal Connections '
                                '| Weighted Minimal Connections | Resource Based | Weighted Response Time)')
        strategy = spair[0]
        if 'Weighted' in strategy and not args.weights:
            raise BaseException('Must provide weight config file for \'weight\' strategies')
        if args.weights:
            with open(args.weights, 'r', encoding='utf-8') as f:
                weights = {int(s.split()[0]): int(float(s.split()[1])) for s in f.read().split('\n')}
                if server_list - weights.keys():
                    raise BaseException('Some servers have not weight config')
                wrr_sum = sum(weights.values())
                wrr_prefix_sum = [[s, weights[s]] for s in server_list]
                for i in range(1, len(wrr_prefix_sum)):
                    wrr_prefix_sum[i][1] += wrr_prefix_sum[i - 1][1]
        else:
            weights = {s: 1 for s in server_list}
        if args.loaddanmaku:
            try:
                with open(args.loaddanmaku, 'rb') as f:
                    danmakus = pickle.load(f)
                    app.logger.info('\033[31mHistory danmakus loaded\033[0m')
            except:
                danmakus = []
                app.logger.warning('\033[31mHistory failed to load\033[0m')
        else:
            danmakus = []

    except Exception as e:
        app.logger.warning(e)
        exit(1)

    if not test_ports_open(server_list):
        app.logger.warning('There exists not opened server port(s)')
        exit(1)

    daemon = DaemonHistory()
    daemon.daemon = True
    daemon.start()

    app.logger.info('''
  ____ ____ _____  ___  ____     ____ ____  _   _ 
 / ___/ ___|___ / / _ \| ___|   / ___|  _ \| \ | |
| |   \___ \ |_ \| | | |___ \  | |   | | | |  \| |
| |___ ___) |__) | |_| |___) | | |___| |_| | |\  |
 \____|____/____/ \___/|____/   \____|____/|_| \_| S22 Final Project - DNS''')
    app.logger.info(f'Startup DNS at {dns_port}')
    app.logger.info(f'Strategy: {strategy}')
    app.logger.info(f'Server list: {server_list}\n')
    app.run(port=dns_port, debug=False)
