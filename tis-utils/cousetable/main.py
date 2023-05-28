from contextlib import suppress

with suppress(Exception):
    import os

    print('Auto installing dependencies')
    os.system('python3 -m pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple')
    # ng for windows?
    # os.system('python3 -m pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple | tail -n 1')

import asyncio
import json
import re
from concurrent.futures import ThreadPoolExecutor
from inspect import iscoroutinefunction
from typing import IO
from datetime import datetime, timedelta

from icalendar import Calendar, Event, vText
import pytz

from requests import Session
from rich.console import Console
from rich.table import Table

console = Console()
session = Session()
meta = {}
table = {}  # { (name_cn, name_en, location, week): { (dow, sod): [ week ] } }
alias_cn = {}
alias_en = {}
alias_loc = {}

COURSE_FMT = re.compile(r'(?P<name_cn>.+)\n?\[.*]\n?\[(?P<name_en>.+)]\n?\[(?P<wk>.*)]\n?\[(?P<location>.*)]\n?\[.*].*')
TIME_FMT = re.compile(r'xq(?P<dow>\d+)_jc(?P<sod>\d+)')
DATE_FMT = re.compile(r'(?P<y>\d+)-0?(?P<m>\d+)-0?(?P<d>\d+)')

course_starts = [
    (8, 0, 9, 50),
    (10, 20, 12, 10),
    (14, 0, 15, 50),
    (16, 20, 18, 10),
    (19, 0, 20, 50),
    (21, 0, 21, 50),
]


def status(prompt):
    def wrapper(func):
        def inner(*args, **kwargs):
            with console.status(prompt):
                if iscoroutinefunction(func):
                    return asyncio.run(func(*args, **kwargs))
                return func(*args, **kwargs)

        return inner

    return wrapper


@status('Logging in')
def login(sid, pwd):
    execution = session.get('https://cas.sustech.edu.cn/cas/login',
                            params={'service': 'https://tis.sustech.edu.cn/cas'}).text
    execution = re.findall('name="execution" value="(.+?)"', execution)[0]
    resp = session.post('https://cas.sustech.edu.cn/cas/login', {
        'username': sid,
        'password': pwd,
        'execution': execution,
        '_eventId': 'submit',
        'geolocation': ''
    }).text
    if '南方科技大学教学管理与服务平台' not in resp:
        console.print(f'[red]Wrong password for [b]{sid}[/b], please check [b]user.json[/b][/red]')
        exit(1)
    name = session.post('https://tis.sustech.edu.cn/UserManager/queryxsxx').json()['XM']
    console.print(f'> Successfully logged in as [cyan]{sid} [b]{name}[/b][/cyan]')


def conf_semester(year, semester):
    sem = {'1': 'Fall', '2': 'Spring', '3': 'Summer'}
    meta['year'] = year
    meta['semester'] = semester
    if not (year and semester):
        resp = session.post('https://tis.sustech.edu.cn/Xsxk/queryXkdqXnxq', {
            'p_pylx': 1,
            'mxpylx': 1,
            'p_sfxsgwckb': 1
        }).json()
        meta['year'] = resp['p_dqxn']
        meta['semester'] = resp['p_dqxq']
    console.print(f'> Ready to fetch data in [cyan]{meta["year"]} {sem[meta["semester"]]}[/cyan]')


@status('Fetching semester metadata')
def fetch_meta():
    weeks = session.post('https://tis.sustech.edu.cn/component/queryzclist', {
        'xn': meta['year'],
        'xq': meta['semester'],
    }).json()
    weeks = list(filter(lambda w: w <= 20, ([w['ZC'] for w in weeks])))

    mondays = {}
    with ThreadPoolExecutor() as executor:
        def inner(i):
            meta_head = session.post('https://tis.sustech.edu.cn/component/queryRlZcSj', {
                'xn': meta['year'],
                'xq': meta['semester'],
                'djz': i,
            }).json()
            mondays[i] = DATE_FMT.match(meta_head.get('content')[0].get('rq')).groupdict()

        executor.map(inner, weeks)
    meta['mondays'] = mondays
    wk1 = meta["mondays"][1]
    console.print(f'> [cyan]{max(meta["mondays"].keys())}[/cyan] weeks in this semester, '
                  f'starting from [cyan]{wk1["y"]}-{wk1["m"]}-{wk1["d"]}[/cyan]')


@status('Fetching raw course table')
def fetch_raw_table():
    with ThreadPoolExecutor() as executor:
        def inner(i):
            resp = session.post('https://tis.sustech.edu.cn/xszykb/queryxszykbzhou', {
                'xn': meta['year'],
                'xq': meta['semester'],
                'zc': i,
            }).json()
            for c in resp:
                # TODO: amend this pile of poop
                if c['XB'] == 0:
                    continue
                ctime = TIME_FMT.match(c['KEY']).groupdict()
                data = COURSE_FMT.match(c['SKSJ_EN']).groupdict()
                cid = (data['name_cn'].strip(), data['name_en'].strip(), data['location'].strip(), data['wk'].strip())
                tid = (int(ctime['dow']), int(ctime['sod']))
                res = table.get(cid) or {}
                weeks = res.get(tid) or []
                weeks.append(i)
                res[tid] = weeks
                table[cid] = res

                alias_cn[data['name_cn'].strip()] = data['name_cn'].strip()
                alias_en[data['name_en'].strip()] = data['name_en'].strip()
                alias_loc[data['location'].strip()] = data['location'].strip()

        executor.map(inner, meta["mondays"].keys())


def lang():
    while True:
        console.print('[blue]> Display the course names in which language? '
                      '[cyan][[i][b][u]c[/u]hinese[/b][/i]|[u]e[/u]nglish][/cyan]', end=' ')
        lang = console.input().strip().lower()
        if len(lang) == 0:
            return True
        if 'c' in lang or 'e' in lang:
            return 'c' in lang


def config_alias(cn):
    # TODO: amend the dup code
    while True:
        console.print('[blue]> Use the raw [u]course[/u] names fetched from tis or config aliases for them? '
                      '[grey62](You can make advanced modifications after this step) '
                      '[cyan][[b][i][u]d[/u]efault[/i][/b]|[u]c[/u]onfig][/cyan]', end=' ')
        conf = console.input().strip().lower()
        if len(conf) == 0 or 'd' in conf:
            break
        if 'c' in conf:
            config_course_alias(cn)
            break

    while True:
        console.print('[blue]> Use the raw [u]location[/u] names fetched from tis or config aliases for them? '
                      '[grey62](You can make advanced modifications after this step) '
                      '[cyan][[b][i][u]d[/u]efault[/i][/b]|[u]c[/u]onfig][/cyan]', end=' ')
        conf = console.input().strip().lower()
        if len(conf) == 0 or 'd' in conf:
            break
        if 'c' in conf:
            config_location_alias()
            break

    global table
    name_alias = alias_cn if cn else alias_en
    table_new = {
        (name_alias[k[0] if cn else k[1]], alias_loc[k[2]], k[3]): {kk: sorted(vv) for kk, vv in v.items()}
        for k, v in table.items()
    }
    table = table_new


def config_course_alias(cn):
    console.print('[blue]You will check the aliases term by term, '
                  '[i]directly press [u]enter[/u] to skip')
    alias = alias_cn if cn else alias_en
    for i, name in enumerate(alias.keys()):
        console.print(f'[gray58]({i + 1}/{len(alias)})[/] [default]{name}[/default] \u21E8', end=' ')
        res = console.input()  # sourcery skip: use-named-expression
        if res:
            alias[name] = res


def config_location_alias():
    console.print('[blue]You will check the aliases term by term, '
                  '[i]directly press [u]enter[/u] to skip')
    for i, name in enumerate(alias_loc.keys()):
        console.print(f'[gray58]({i + 1}/{len(alias_loc)})[/] [default]{name}[/default] \u21E8', end=' ')
        res = console.input()  # sourcery skip: use-named-expression
        if res:
            alias_loc[name] = res


def preview():
    console.clear()
    prev = Table(show_header=True, header_style='bold magenta', show_lines=True)
    prev.add_column('#', style='dim', width=2, justify='center')
    weekdays = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']
    day_width = (console.width - 20) // 7
    for day in weekdays:
        prev.add_column(day, justify='center', width=day_width)

    def course_str(dow, cod):
        course = list(filter(lambda i: (dow, cod) in i[1].keys(), table.items()))
        return '\n---\n'.join(f'{c[0][0]} <{c[0][1]}>\n{c[0][2]}' for c in course)

    for d in range(1, 7):
        prev.add_row(str(d), *[course_str(c, d) for c in range(1, 7)])

    with(open('table.json', 'w', encoding='utf-8')) as f:
        to_json(f)
    console.print(prev)

    console.print('> Please confirm the preview, [i]press [u]enter[/u] to export the calendar[/i]')
    console.print('  You can also do some advanced modifications (e.g. add/delete course) '
                  'via editing [blue][u]table.json[/u][/blue]')
    console.input()

    with(open('table.json', 'r', encoding='utf-8')) as f:
        global table
        t = from_json(f)
        if t == table:
            return
        table = t
        preview()


def export():
    cal = Calendar()
    for c in table.items():
        # { (name, location, week): { (dow, sod): [ week ] } }
        for (dow, sod), week in c[1].items():
            for wk in week:
                event = Event()
                event['summary'] = c[0][0]
                event['location'] = vText(c[0][1])
                mon = meta['mondays'][wk]
                res = course_starts[sod - 1]
                event.add('dtstart', datetime(int(mon['y']), int(mon['m']), int(mon['d']),
                                              res[0], res[1], tzinfo=pytz.timezone('Asia/Shanghai')) + timedelta(
                    dow - 1))
                event.add('dtend', datetime(int(mon['y']), int(mon['m']), int(mon['d']),
                                            res[2], res[3], tzinfo=pytz.timezone('Asia/Shanghai')) + timedelta(dow - 1))
                cal.add_component(event)

    with open('schedule.ics', 'wb') as f:
        f.write(cal.to_ical())


def to_json(f: IO):
    # FIXME: why didn't I decide the data structure at start
    tab = [{
        'name': c[0][0],
        'location': c[0][1],
        'weekForCheck': c[0][2],
        'sections': [
            {
                'dayOfWeek': s[0][0],
                'sectionOfDay': s[0][1],
                'weeks': s[1],
            }
            for s in c[1].items()
        ]
    } for c in table.items()]
    json.dump(tab, f, indent=2, ensure_ascii=False)


def from_json(f: IO) -> dict:
    raw = json.load(f)
    return {(c['name'], c['location'], c['weekForCheck']):
                {(s['dayOfWeek'], s['sectionOfDay']): s['weeks']
                 for s in c['sections']}
            for c in raw}


if __name__ == '__main__':
    console.clear()
    try:
        with open('user.json', 'r', encoding='utf-8') as f:
            info = json.load(f)

        login(info['sid'], info['pwd'])
        conf_semester(info.get('year'), info.get('semester'))
        fetch_meta()
        fetch_raw_table()
        console.print()

        config_alias(lang())

        preview()
        export()
        console.print(':cake: Successfully exported to [blue][u]schedule.ics[/u][/blue]')

    except KeyboardInterrupt:
        console.print('\n[red]Canceled[default]')
    except Exception:  # noqa
        console.print_exception(show_locals=True)
        console.print('[red]An error occurred, please re-run the script or contact the auther for help: '
                      '[orange]mailto:heza2020@mail.sustech.edu.cn[default]')
