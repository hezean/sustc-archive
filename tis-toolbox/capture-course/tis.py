import atexit
import datetime
import json
import logging
import re
import time

import requests

logger = logging.getLogger('tis.info')
logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s ~ \033[96m%(funcName)s\033[0m: %(message)s',
                    datefmt='%H:%M:%S')

cas_url = 'https://cas.sustech.edu.cn/cas/login?service=https%3A%2F%2Ftis.sustech.edu.cn%2Fcas'
ele_url = 'https://tis.sustech.edu.cn/Xsxk/addGouwuche'

session = requests.Session()
try:
    tis = session.get(cas_url)
except:
    logger.fatal('Internet connection error')

headers = {
    'username': '',
    'password': '',
    'execution': re.findall('on" value="(.+?)"', tis.text)[0],
    '_eventId': 'submit',
    'geolocation': ''
}

ele_head = {
    "p_pylx": "1",
    "mxpylx": "1",
    "p_sfgldjr": "0",
    "p_sfredis": "0",
    "p_sfsyxkgwc": "0",
    "p_xktjz": "rwtjzyx",
    "p_xkfsdm": "bxxk"
}

sem = {'1': 'Fall', '2': 'Spring', '3': 'Summer'}


def login():
    fail_to_login = True
    retry_time = 10
    while fail_to_login and retry_time >= 0:
        try:
            tis = session.post(cas_url, headers)
            fail_to_login = False
        except:  # socket FIN
            retry_time -= 1
            logger.warning('Failed to login, retrying...')
    if fail_to_login:
        logger.fatal('Failed to login after retrying, CAS server is being fxxked')
        exit(1)

    if str(tis.content, 'utf-8').startswith('<!DOCTYPE html><html>'):
        logger.fatal('incorrect username or password')
        exit(1)

    sif = session.post('https://tis.sustech.edu.cn/UserManager/queryxsxx').json()['XM']
    logger.info(f'{sif} ({headers["username"]}) successfully logged in')


def confsem():
    resp = session.post('https://tis.sustech.edu.cn/Xsxk/queryXkdqXnxq',
                        data={'p_pylx': 1, 'mxpylx': 1, 'p_sfxsgwckb': 1}).json()
    ele_head.update(resp)
    logger.info(f'[need confirm] tis config -> {resp["p_xn"]} {sem[resp["p_xq"]]}\n')


time_fmt = '%Y-%m-%d%H:%M:%S'
today = str(datetime.date.today())


def fetch_cs(courses, since='12:57:00', pat='12:59:35', until='13:01:30'):
    trial_cnt = 0
    start_batch = datetime.datetime.strptime(today + since, time_fmt)
    pat_batch = datetime.datetime.strptime(today + pat, time_fmt)
    end_batch = datetime.datetime.strptime(today + until, time_fmt)

    # should not try too freq: socket may be closed, TAO may wanna fuck you
    try:
        while True:
            trial_cnt += 1
            for course in courses:
                ele_head['p_id'] = course
                _tis = session.post(ele_url, ele_head)
                logger.info(f'#{trial_cnt}  {_tis.json()["message"]}')
                time.sleep(0.05)

            if datetime.datetime.now() < start_batch:
                logger.info('too early, freq = 1 trial/min')
                time.sleep(60)

            elif datetime.datetime.now() < pat_batch:
                logger.info('too early, freq = 1 trial/sec')
                time.sleep(1)
            elif datetime.datetime.now() > end_batch:
                break

    except KeyboardInterrupt:
        return
    except:
        logger.warning(_tis.content)


def confirm():
    csi = session.post('https://tis.sustech.edu.cn/Xsxktz/queryRwxxcxList',
                       data={'p_xn': ele_head['p_xn'],
                             'p_xq': ele_head['p_xq'],
                             'p_xnxq': ele_head['p_xnxq'],
                             'p_chaxunpylx': 3,
                             'mxpylx': 3,
                             'pageSize': 5000
                             }).json()['rwList']['list']

    dim1 = {c['id']: (c['kcdm'], c['rwmc'], c['dgjsmc']) for c in csi}
    dim2 = {c['rwmc']: (c['id'], c['kcdm'], c['dgjsmc']) for c in csi}

    for c in courses_self_conf:
        try:
            logger.info(f'[M] {c}   {dim1[c][0]:<8} {dim1[c][1]} - {dim1[c][2]}')
        except:
            pass
    for c in courses_fetch_info:
        try:
            logger.info(f'[A] {dim2[c][0]}   {dim2[c][1]:<8} {c} - {dim2[c][2]}')
            courses_self_conf.append(dim2[c][0])
        except:
            logger.error(f'{c}: none matched in tis course list, please manually configure -> course1')

    logger.info(f'{len(courses_self_conf)} fetch tasks imported\n')


def tcp_fin():
    try:
        session.close()
    except:
        pass


if __name__ == '__main__':
    atexit.register(tcp_fin)

    try:
        with open('user.json') as f:
            info = json.load(f)
            headers.update({'username': info.get('sid'),
                            'password': info.get('pwd')})
            courses_self_conf = info.get('courses1') or []
            courses_fetch_info = info.get('courses') or []

        login()
        confsem()
        confirm()
        time.sleep(0.5)
        fetch_cs(courses_self_conf)

        logger.info('all done, have fun!')
        session.close()

    except Exception as e:
        logger.error(e)
