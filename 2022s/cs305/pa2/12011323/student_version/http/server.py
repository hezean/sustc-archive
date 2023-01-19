import json
import os
import threading
import pickle
import asyncio
import time
import logging

from datetime import datetime
from flask import Flask, render_template, request, jsonify, Response

read_log = True  # if you don't want to load the saved history nor save the message into file, set it to False

app = Flask(__name__, template_folder='.')
app.logger.disabled = True
logging.getLogger('werkzeug').disabled = True

jdec = json.JSONDecoder()
lock = threading.Lock()
loop = asyncio.get_event_loop()

'''
since we have multiple videos, each video should have its own danmakus' lib
key: the id of video
val: danmakus, each danmaku is stored as:
Tuple(timestamp_in_video: int, client_id: int, danmaku_content: str, send_absolute_time_ts: int)
'''
danmakus = {int: []}

'''
when a new client is connected (is should first load the content via localhost:8765)
the function {@link get_page} listening route '/' will send the html to the user,
in which the server assigns the client a unique id
note that since we will test the PA in one computer, I choose not to identify the client by IP or else
aka any request to the server (including refreshing) will be considered as a new client is joined
'''
cid = 0


@app.after_request
def allow_cors(resp):
    """Enables Cross Origin Resource Sharing for ajax requests
    since the frontend sends request to 'localhost' instead of '127.0.0.1' specified in app.run(#host)
    Otherwise, the XMLHttpRequest will be blocked by the default CORS policy
    """
    resp.headers.add('Access-Control-Allow-Origin', '*')
    resp.headers.add('Access-Control-Allow-Methods', 'GET,PUT,POST')
    return resp


@app.route('/', methods=['GET'])
def get_page():
    """After launching the python server, you can open several webpages via http://localhost:8765.
    Then a request to the route / will be sent, and be caught by flask.

    This function will be called to return the html file, note that we manage the client ID in this function,
    the render template receives variable 'cid', which is assigned by server to indicate the sender.
    """
    global cid
    cid += 1
    return render_template('danmu.html', cid=cid)


@app.route('/vs/<vid>', methods=['GET'])
def get_video_part(vid):
    """Sends part of video file in a <206 Partial Content>.

    The webpage uses video in html5 to display the video,
    after receiving a partial video, it will automatically send a request with the header 'Range'
    to get the next part of file, and stops at EOF.

    Args:
        vid: the id of the playing video
    """
    rng = request.headers.get('Range')
    # if the client is not requesting the first part of file,
    # it will set the Range header to indicate how many bytes it previously received
    start = int(rng.split('=')[1].split('-')[0]) if rng else 0
    # 'else 0' means than when rng is None (say, the header is not set, aka this is the first request of file)

    with open(f'./res/{vid}.mp4', 'rb') as f:
        sz = os.path.getsize(f'./res/{vid}.mp4')
        f.seek(start)  # move to the start byte
        part = f.read(1024 * 64)  # read part of file
        end = start + len(part) - 1
    # see https://datatracker.ietf.org/doc/html/rfc7233
    return Response(part, status=206, headers={
        'Accept-Ranges': 'bytes',
        'Content-Type': 'application/octet-stream',
        'Content-Range': f'bytes {start}-{end}/{sz}',
    })


@app.route('/danmaku/<vid>', methods=['GET'])
def get_danmakus(vid):
    """Returns danmakus in a short video interval.

    The client requests for the danmakus' list while playing videos per 100ms,
    the server will fetch the danmakus in the required 100ms interval for video <vid>.

    Args:
        vid: the id of the playing video
    Returns:
        A HTTP response that contains a json string in the body (its content-type header is set as application/json)
        The json contains danmakus' contents string for the interval.
    """
    if vid not in danmakus.keys():  # no previous danmaku is sent
        return jsonify({'dm': []})
    form = request.args
    ts = form.get('last', type=int)
    # we have interval when the video is playing for each 100ms, thus we fetch these msg
    # since the client put all danmakus into a queue, the function will be executed very fast
    # we say taking this timestamp interval will not overlap or miss some part
    dms = [d[2] for d in filter(lambda dm: ts < dm[0] <= ts + 100, danmakus[vid])]
    return jsonify({'dm': dms})


@app.route('/chat/<vid>', methods=['GET'])
def get_chat_history(vid):
    """Returns the chat history of the specified video.
    All the history chats will be returned. Note that the definition of 'history' is 'sent before this GET request'.

    Args:
         vid: the id of the playing video
    Returns:
        A HTTP response that contains a json string in the body.
        The value of 'dm' key is a list of Tuple(str, bool).
        The str has been concat as html, the bool means whether this chat was sent from 'my' client or other clients.
    """
    if vid not in danmakus.keys():  # no chat history for this video
        return jsonify({'dm': []})
    form = request.args
    # everytime the client expects to receive chats newer that the last query
    # and for the first query after loading the webpage or switching the video
    # 'last' is set to -1 s.t. all chats are fetched
    ts = form.get('last', type=int)
    dms = [(f'{d[1]} at {datetime.fromtimestamp(d[3] / 1000.0).strftime("%H:%M:%S")} said:<br>{d[2]}',
            int(d[1]) == form.get('cid', type=int), d[3])
           for d in filter(lambda dm: dm[3] > ts, danmakus[vid])]
    return jsonify({'dm': dms})


@app.route('/danmaku/<vid>', methods=['POST'])
def post_danmaku(vid):
    """Add new danmakus sent by clients.

    The client will not send a danmaku in the real time,
    instead, it will directly display the danmaku, but collect all the danmakus in 100ms
    and pack them into a list (to json string), and send them to the server,
    which can ease the workload of server when many danmakus are sent at a very short time period.
    """
    form = request.form
    if vid not in danmakus.keys():
        danmakus[vid] = []
    new_dms = form.get('dms', type=str)
    dms = jdec.decode(new_dms)
    danmakus[vid].extend([(d['ts'], d['cid'], d['val'], d['at']) for d in dms])
    return ''


class DaemonHistory(threading.Thread):
    def run(self):
        """Save (backup) the danmakus into file pre second."""
        if not read_log:
            return
        try:
            while True:
                with lock:
                    with open('res/danmakus.rec', 'wb') as ff:
                        pickle.dump(danmakus, ff)
                        # note that the .rec file is not directly readable,
                        # but you can uncomment the below line to print the danmakus in the memory
                        # print(danmakus)
                    with open('res/cid.rec', 'w') as ff:
                        ff.write(str(cid))
                time.sleep(1)
        except:
            try:  # when trying to exit the program, do the last attempt to save the danmakus history
                pickle.dump(danmakus, ff)
            except:
                pass
        finally:  # don't care about fails, just losing all the history
            exit(0)


if __name__ == '__main__':
    # first try to load the history, if no history, 'danmakus' is inited as an empty dict
    # ! if you want to check the program without the previous danmakus I left
    # ! please delete ./res/danmakus.rec and ./res/cid.rec
    if read_log:
        if os.path.isfile('res/danmakus.rec'):
            with open('res/danmakus.rec', 'rb') as f:
                try:
                    danmakus = pickle.load(f)
                    print('\033[31m * History danmakus loaded\033[0m')
                except:
                    danmakus = {}
        if os.path.isfile('res/cid.rec'):
            with open('res/cid.rec', 'r') as f:
                cid = int(f.read())

    daemon = DaemonHistory()
    daemon.start()

    print('\033[31m * Running on http://127.0.0.1:8765 (Press CTRL+C to quit)\033[0m')
    app.run(host='127.0.0.1', port=8765, debug=False)
    daemon.join()
