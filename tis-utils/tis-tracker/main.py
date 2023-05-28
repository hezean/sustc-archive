import contextlib
import json
import logging
import re
from urllib.parse import quote

import requests
from flask import Flask
from flask_apscheduler import APScheduler
from requests import Session

app = Flask(__name__)
app.config.update({'SCHEDULER_API_ENABLED': True})

scheduler = APScheduler()
users = []

logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s ~ \033[96m%(name)s~%(funcName)s\033[0m: %(message)s',
                    datefmt='%H:%M:%S')


def notify_error(func):
    def wrapper(self, *args, **kwargs):
        try:
            return func(self, *args, **kwargs)
        except Exception as e:
            self.logger.warning(e)
            self.message(f'An error occurred: {func.__name__}', str(e))

    return wrapper


def noexcept(func):
    def wrapper(*args, **kwargs):
        with contextlib.suppress(Exception):
            return func(*args, **kwargs)

    return wrapper


def retry_login(func):
    @notify_error
    def wrapper(self, *args, **kwargs):
        with contextlib.suppress(Exception):
            return func(self, *args, **kwargs)
        self.login()
        try:
            return func(self, *args, **kwargs)
        except Exception as e:
            raise ValueError(f'Retried login but still met an error: {e}') from e

    return wrapper


class User:
    def __init__(self, sid, passwd, bark):
        self._sid = sid
        self._passwd = passwd
        self._bark = bark
        self._session = None
        self._qry_params = {'pageSize': 100}
        self.logger = logging.getLogger(str(sid))
        self.last = {}
        self.now = {}

        self.message('Tis Tracker', 'Start tracking remaining capacities')
        self.login()
        self.update()

    @notify_error
    def login(self):
        self._session = Session()
        execution = self._session.get('https://cas.sustech.edu.cn/cas/login',
                                      params={'service': 'https://tis.sustech.edu.cn/cas'}).text
        execution = re.findall('name="execution" value="(.+?)"', execution)[0]
        resp = self._session.post('https://cas.sustech.edu.cn/cas/login',
                                  {
                                      'username': self._sid,
                                      'password': self._passwd,
                                      'execution': execution,
                                      '_eventId': 'submit',
                                      'geolocation': ''
                                  }).text
        if '南方科技大学教学管理与服务平台' not in resp:
            with contextlib.suppress(ValueError):
                global users
                users.remove(self)
            raise ValueError(f'Wrong password for {self._sid}')
        self._qry_params.update(self._session.post('https://tis.sustech.edu.cn/Xsxk/queryXkdqXnxq',
                                                   data={'p_pylx': 1, 'mxpylx': 1, 'p_sfxsgwckb': 1}).json())

    @noexcept
    def message(self, title, body=None, params=None):
        self.logger.info(f'title="{title}", body="{body}"')
        url = f'https://api.day.app/{self._bark}/{quote(title, safe="")}/{quote(body or "", safe="")}'
        base_params = {
            'isArchive': 0,
            'icon': 'https://i.imgur.com/a61hBq8.png',
        }
        base_params |= params or {}
        requests.get(url, params=base_params)

    @notify_error
    @retry_login
    def update(self):
        self.last = self.now
        selected = self._session.post('https://tis.sustech.edu.cn/Xsxk/queryYxkc', params=self._qry_params)
        selected = selected.json()['yxkcList']
        self.now = {s['kcmc']: (int(s['zrl']), int(s['yxzrs']), s['xkxs']) for s in selected}
        self.summary()

    @notify_error
    def summary(self):
        res = list(filter(lambda i: i[1][1] >= i[1][0] * 0.95, self.now.items()))
        delta = [(c, c[1][1] - self.last.get(c[0], (0, 0))[1]) for c in res]
        delta = list(filter(lambda i: i[1] != 0, delta))
        for c in delta:
            self.message(f'{c[0][0]} 选课人数{"增加" if c[1] > 0 else "减少"} {abs(c[1])}',
                         f'已选/容量: {c[0][1][1]}/{c[0][1][0]}    积分: {c[0][1][2]}')


@scheduler.task('interval', seconds=30)
def daemon():
    for u in users:
        u.update()


if __name__ == '__main__':
    # users.extend([
    #     User('12010000', 'cas-password', '8eAZktGfoobarbaz'),
    # ])

    with contextlib.suppress(IOError):
        with open('user.json', 'r') as f:
            info = json.load(f)
            users.extend([User(**kwargs) for kwargs in info])

    scheduler.init_app(app)
    scheduler.start()
    app.run(debug=False)
