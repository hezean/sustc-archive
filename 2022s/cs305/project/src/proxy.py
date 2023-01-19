import atexit
import json
import logging
import math
import os
import signal
import socket
import sys
import threading
import time
import xml.dom.minidom
from datetime import datetime

import requests
from flask import Flask, Response, request, jsonify
from requests.adapters import HTTPAdapter, Retry

# STATIC FINAL FIELDS ###################################

timeout = 30

web_protocol = 'http'
server_ip = '127.0.0.1'
server_port = '8080'

dns_ip = '127.0.0.1'

# UTIL GLOBAL OBJECTS ###################################

app = Flask(__name__)
app.logger.setLevel(logging.INFO)

app.config['SECRET_KEY'] = 'cs305'

jdec = json.JSONDecoder()

retries = Retry(total=5,
                backoff_factor=0.1,
                status_forcelist=[500, 502, 503, 504])
ses = requests.Session()
ses.mount('http://', HTTPAdapter(max_retries=retries))

# GLOBAL VARIABLES ######################################

danmakus_lock = threading.Lock()
danmakus = []  # Tuple(timestamp_in_video: int, client_id: int, danmaku_content: str, send_absolute_time_ts: int)

# if the client (browser) stop sending requests to proxy for {TIMEOUT} sec, shutdown the proxy
countdown_lock = threading.Lock()
countdown = timeout

throughput_lock = threading.Lock()
throughput = 0
available_bitrate = {}
vid_duration: float


# @app.before_request
# def refresh_suicide_countdown():
#     global countdown
#     with countdown_lock:
#         countdown = timeout
#     app.logger.info('Refresh countdown due to new request')


def suicide_daemon():
    if app.debug:
        return

    global countdown
    if countdown <= 0:
        log(f'{datetime.now()}  Proxy is inactivate: timeout 30s without request | 10s after the video is finished')
        os.kill(os.getpid(), signal.SIGINT)
    else:
        with countdown_lock:
            countdown -= 1
        threading.Timer(1, suicide_daemon).start()


def adjust_throughput(func):
    def wrapper(*arg, **kw):
        global throughput

        stt = datetime.timestamp(datetime.now())
        t1 = time.time()
        res = func(*arg, **kw)
        t2 = time.time()

        if type(res) == requests.models.Response:
            return Response(res)

        th1 = (res[0] / (t2 - t1)) * 8 / 1000  # Bps -> bps -> kbps
        with throughput_lock:
            throughput = (1 - alpha) * throughput + alpha * th1
        log(f'{stt:.6f} {t2 - t1:.3f} {th1:.2f} {throughput:.2f} {res[1]} {server_port} {res[2]}')

        return Response(res[3])

    return wrapper


# def access_danmakus(func):
#     def wrapper(*arg, **kw):
#         global danmakus
#         with danmakus_lock:
#             danmakus = jdec.decode(ses.get(f'{web_protocol}://{dns_ip}:{dns_port}/danmaku').text)
#         return func(*arg, **kw)
#
#     return wrapper


def parse_f4m(f4m):
    global available_bitrate, throughput, vid_duration
    f4m = xml.dom.minidom.parseString(f4m)
    manifest = f4m.documentElement
    vid_duration = float(manifest.getElementsByTagName('duration')[0].firstChild.nodeValue)
    available_bitrate = {int(br.getAttribute('streamId')): br.getAttribute('url')
                         for br in manifest.getElementsByTagName('media')}
    throughput = min(available_bitrate.keys())


@app.route('/<uri>', methods=['GET'])
def forwarding(uri):
    return Response(ses.get(f'{web_protocol}://{server_ip}:{server_port}/{uri}'))


@app.route('/live2d/<r>/<f>', methods=['GET'])
def live2d_forwarding(r, f):
    return Response(ses.get(f'{web_protocol}://{server_ip}:{server_port}/live2d/{r}/{f}'))


@app.route('/live2d/model/<m>/<f>', methods=['GET'])
def live2d_model_forwarding(m, f):
    return Response(ses.get(f'{web_protocol}://{server_ip}:{server_port}/live2d/model/{m}/{f}'))


@app.route('/live2d/model/<m>/<r>/<f>', methods=['GET'])
def live2d_texture_forwarding(m, r, f):
    return Response(ses.get(f'{web_protocol}://{server_ip}:{server_port}/live2d/model/{m}/{r}/{f}'))


@app.route('/vod/<uri>', methods=['GET'])
@adjust_throughput
def modify_request(uri):
    """Changes the requested bit rate according to your computation of throughput.

    If the request is for big_buck_bunny.f4m, it will be parsed in the proxy
    and return big_buck_bunny_nolist.f4m to the client.
    """
    global countdown
    with countdown_lock:
        countdown = timeout

    if '.f4m' in uri:
        f4m = ses.get(f'{web_protocol}://{server_ip}:{server_port}/vod/{uri}')
        parse_f4m(f4m.text)
        return ses.get(f'{web_protocol}://{server_ip}:{server_port}/vod/{"_nolist.".join(uri.split("."))}')

    valid = list(filter(lambda r: r <= throughput / 1.5, available_bitrate.keys()))
    bitrate = max(valid) if valid else min(available_bitrate.keys())
    uri = str(bitrate) + uri.lstrip('0123456789')
    resp = ses.get(f'{web_protocol}://{server_ip}:{server_port}/vod/{uri}')

    frag = int(uri.split('Frag')[1])
    if frag >= math.floor(vid_duration):
        with countdown_lock:
            countdown = 10

    return int(resp.headers['Content-Length']), bitrate, uri, resp


@app.route('/chat', methods=['GET'])
# @access_danmakus
def get_chat_history():
    global danmakus
    with danmakus_lock:
        danmakus = jdec.decode(ses.get(f'{web_protocol}://{dns_ip}:{dns_port}/danmaku').text)

    form = request.args
    ts = form.get('last', type=int)
    dms = [(f'[{datetime.fromtimestamp(d[3] / 1000.0).strftime("%H:%M:%S")}] usr{d[1]} said:<br>{d[2]}',
            int(d[1]) == form.get('cid', type=int), d[3])
           for d in filter(lambda dm: dm[3] > ts, danmakus)]
    return jsonify({'dm': dms})


@app.route('/danmaku', methods=['GET'])
# @access_danmakus
def get_danmakus():
    global danmakus
    with danmakus_lock:
        danmakus = jdec.decode(ses.get(f'{web_protocol}://{dns_ip}:{dns_port}/danmaku').text)

    form = request.args
    ts = form.get('last', type=int)
    dms = [d[2] for d in filter(lambda dm: ts < dm[0] <= ts + 100, danmakus)]
    return jsonify({'dm': dms})


@app.route('/danmaku', methods=['POST'])
def post_danmaku():
    ses.post(f'{web_protocol}://{dns_ip}:{dns_port}/danmaku', request.form)
    return ''


@app.before_first_request
def request_dns():
    """
    Request dns server here.
    """
    global server_port
    server_port = default_port or ses.get(f'{web_protocol}://{dns_ip}:{dns_port}').text
    app.logger.info(f'Apache server address {web_protocol}://{server_ip}:{server_port}')


def test_ports_open(ports):
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as soc:
            for p in ports:
                if p and soc.connect_ex(('127.0.0.1', p)) == 1:
                    return False
    except OverflowError:
        return False
    return True


def log(msg):
    # app.logger.info(msg)
    log_file.write(msg)
    log_file.write('\n')
    log_file.flush()


def release_res():
    try:
        log(f'{datetime.now()}  Proxy shut down')
        log_file.close()
    except:
        pass


if __name__ == '__main__':
    if not 5 <= len(sys.argv) <= 6:
        app.logger.warning('expect 5 or 6 args')
        exit(1)

    try:
        log_file = open(sys.argv[1], 'a+', encoding='utf-8')
    except Exception as e:
        app.logger.warning(e)
        exit(1)

    atexit.register(release_res)

    try:
        alpha = float(sys.argv[2])
        assert 0 <= alpha <= 1
        listen_port = int(sys.argv[3])
        dns_port = int(sys.argv[4])
        default_port = int(sys.argv[5]) if len(sys.argv) == 6 else None
    except AssertionError:
        app.logger.warning('alpha must in range [0, 1]')
        exit(1)
    except Exception as e:
        app.logger.warning(e)
        exit(1)

    if not test_ports_open((dns_port, default_port)):
        app.logger.warning('Listen port is not opened')
        exit(1)

    log('''
  ____ ____ _____  ___  ____     ____ ____  _   _ 
 / ___/ ___|___ / / _ \| ___|   / ___|  _ \| \ | |
| |   \___ \ |_ \| | | |___ \  | |   | | | |  \| |
| |___ ___) |__) | |_| |___) | | |___| |_| | |\  |
 \____|____/____/ \___/|____/   \____|____/|_| \_| S22 Final Project - Proxy''')
    log(f'{datetime.now()}  Proxy started: port {listen_port}\n')

    threading.Timer.daemon = True
    suicide_daemon()
    app.run(port=listen_port, debug=False)
