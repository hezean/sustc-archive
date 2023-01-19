# -*- coding:UTF-8 -*-

import sys
import time

import pandas as pd
import psycopg2 as psql
import yaml
from psycopg2.extras import execute_batch

from util import timer, clear_data


class DB:
    def __init__(self, conf: str = './user.yml', data='./data/clear_course_info.json'):
        with open(conf, 'r', encoding='utf-8') as cfg:
            conf = yaml.safe_load(cfg)
        try:
            self.conn = psql.connect(host=conf['host'], port=conf['port'],
                                     user=conf['user'], password=conf['pwd'],
                                     database=conf['db'])
            self.cur = self.conn.cursor()
            self.conn.autocommit = False
        except Exception as e:
            print(e)
            sys.exit(1)
        with open(data, 'r') as dat:
            self.cdata = pd.read_json(dat)
        print('Database connected && Data loaded to RAM.')

    def __del__(self):
        self.conn.commit()
        self.conn.close()

    def submitter(self, time_start, sql, data):
        print(f'Preparing data in py ({time.perf_counter() - time_start:.4f}s)', end=' >>> ')
        try:
            tm = time.perf_counter()
            execute_batch(self.cur, sql, data)
            self.conn.commit()
            print(f'Submitted {len(data)} requests '
                  f'({time.perf_counter() - tm:.4f}s | avg={len(data) / (time.perf_counter() - tm):.4f}i/s)')
        except Exception as e:
            print(e)

    @timer
    def create_tables(self, ddl_path):
        with open(ddl_path, 'r') as dl:
            sql_list = dl.read().split(';')[:-1]
            # try:

            for sql_item in sql_list:
                self.cur.execute(sql_item)
            self.conn.commit()
        # except Exception as e:
        #     print(repr(e))

    @timer
    def ins_dept(self):
        sql = '''
                INSERT INTO project1.department (name)
                VALUES (%s)
                ON CONFLICT DO NOTHING;
              '''
        tm = time.perf_counter()
        dept = [(x,) for x in self.cdata['courseDept']]
        self.submitter(tm, sql, dept)

    @timer
    def ins_teacher(self):
        sql = '''
                INSERT INTO project1.teacher(name, dept)
                VALUES (%s, (SELECT id
                             FROM project1.department
                             WHERE name = %s))
                ON CONFLICT DO NOTHING;
              '''
        tm = time.perf_counter()
        teacher = []
        for index, item in self.cdata.iterrows():
            if item['teacher'] is None:
                continue
            for t in item['teacher']:
                teacher.append((t, item['courseDept']))
        self.submitter(tm, sql, teacher)

    @timer
    def ins_course(self):
        sql = '''
                INSERT INTO project1.course
                VALUES (%s, %s, %s, %s, (SELECT id
                                         FROM project1.department
                                         WHERE name = %s))
                ON CONFLICT DO NOTHING;
              '''
        tm = time.perf_counter()
        course = [(item['courseId'], item['courseName'], item['courseHour'],
                   item['courseCredit'], item['courseDept'])
                  for index, item in self.cdata.iterrows()]
        self.submitter(tm, sql, course)

    @timer
    def ins_pre(self):
        sql = '''
                WITH preid AS (SELECT cid
                               FROM project1.course
                               WHERE name = %s)
                INSERT INTO project1.prerequisite (course, pre, pre_rk)
                    (SELECT %s, (SELECT cid FROM preid LIMIT 1), %s
                     WHERE (SELECT COUNT(cid) FROM preid) > 0)
                ON CONFLICT DO NOTHING;
              '''
        tm = time.perf_counter()
        pres = []
        for index, item in self.cdata.iterrows():
            if item['prerequisite'] is None:
                continue
            for rk, ands in enumerate(item['prerequisite'], start=1):
                for ors in ands:
                    pres.append((ors, item['courseId'], rk))
        self.submitter(tm, sql, pres)

    @timer
    def ins_class_te_sc(self):
        sql_cls = '''
                    INSERT INTO project1.class
                    VALUES (%s, %s, %s, %s);
                  '''
        sql_tec = '''
                    WITH tid AS (
                    SELECT id
                    FROM project1.teacher
                    WHERE name = %s
                        AND dept = (SELECT id FROM project1.department WHERE name = %s)
                    )
                    INSERT INTO project1.class_teacher (class, teacher)
                    VALUES (%s, (SELECT id FROM tid));
                  '''
        sql_sch = '''
                    INSERT INTO project1.schedule (class, week_list, location, starting, ending, weekday)
                    VALUES (%s, %s::int[], %s, %s, %s, %s);
                  '''
        tm = time.perf_counter()
        cls = []
        tec = []
        sch = []
        self.cur.execute('''
                           SELECT GREATEST((SELECT last_value - 1
                                            FROM project1.class_id_seq),
                                           (SELECT MAX(id)
                                            FROM project1.class));
                         ''')
        for idx, (index, item) in enumerate(self.cdata.iterrows(), start=self.cur.fetchone()[0] + 1):
            cls.append((idx, item['courseId'], item['className'], item['totalCapacity']))
            if item['teacher'] is not None:
                for x in iter((t, item['courseDept'], idx) for t in item['teacher']):
                    tec.append(x)
            for x in iter((idx,
                           str(cl.get('weekList')).replace('[', '{').replace(']', '}'),
                           cl.get('location'),
                           int(cl.get('classTime').split('-')[0]),
                           int(cl.get('classTime').split('-')[1]),
                           cl.get('weekday'))
                          for cl in item['classList']):
                sch.append(x)
        print(f'Preparing data in py ({time.perf_counter() - tm:.4f}s)', end=' >>> ')
        try:
            tm = time.perf_counter()
            execute_batch(self.cur, sql_cls, cls)
            execute_batch(self.cur, sql_tec, tec)
            execute_batch(self.cur, sql_sch, sch)
            self.cur.execute('SELECT max(id) FROM project1.class')
            self.cur.execute('ALTER SEQUENCE project1.class_id_seq RESTART WITH %s;',
                             self.cur.fetchone()[0])
            self.conn.commit()
            print(f'Submitted {len(cls) + len(tec) + len(sch)} requests '
                  f'({time.perf_counter() - tm:.4f}s | '
                  f'avg={(len(cls) + len(tec) + len(sch)) / (time.perf_counter() - tm):.4f}i/s)')
        except Exception as e:
            print(e)


if __name__ == '__main__':
    clear_data('./data/course_info.json', './data/clear_course_info.json')
    db = DB()
    # db.create_tables('./ddl.sql')
    db.ins_dept()
    db.ins_teacher()
    db.ins_course()
    db.ins_pre()
    db.ins_class_te_sc()
