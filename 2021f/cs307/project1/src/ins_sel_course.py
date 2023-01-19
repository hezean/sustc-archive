# -*- coding:UTF-8 -*-
import asyncio
import csv
import sys

import asyncpg
import psycopg2 as psql
import yaml
from psycopg2.extras import execute_batch
from tqdm import tqdm
from tqdm.asyncio import tqdm as atqdm

from util import timer


class DBStu:
    def __init__(self, conf: str = './user.yml', data='./data/small_select_course.csv'):
        with open(conf, 'r', encoding='utf-8') as cfg:
            conf = yaml.safe_load(cfg)
        try:
            self.conn = psql.connect(host=conf['host'], port=conf['port'],
                                     user=conf['user'], password=conf['pwd'],
                                     database=conf['db'])
            self.cur = self.conn.cursor()
            self.conn.autocommit = False

            self.dat = open(data, 'r')
            self.cdata = csv.reader(self.dat)
        except Exception as e:
            print(e)
            sys.exit(1)
        print('Database connected && Data loaded to RAM.')

    def __del__(self):
        self.conn.commit()
        self.conn.close()
        self.dat.close()

    @timer
    def bad_loader(self):
        for stu in self.cdata:
            try:
                self.cur.execute('''INSERT INTO project1.college(name, eng_name)
                                    SELECT %s, %s
                                    WHERE (SELECT COUNT(*)
                                           FROM project1.college
                                           WHERE name = %s) = 0;''',
                                 (stu[2].split('(')[0],
                                  stu[2].split('(')[1].replace(')', ''),
                                  stu[2].split('(')[0]))
                self.cur.execute('''INSERT INTO project1.student
                                    VALUES (%s, %s, %s, (SELECT id
                                                         FROM project1.college
                                                         WHERE name = %s))
                                    ON CONFLICT DO NOTHING;''',
                                 (stu[3], stu[0], stu[1], stu[2].split('(')[0]))
                for sel in stu[4:]:
                    self.cur.execute('''INSERT INTO project1.learnt
                                        (SELECT %s, %s
                                         WHERE EXISTS(SELECT id FROM project1.student WHERE id = %s)
                                           AND EXISTS(SELECT cid FROM project1.course WHERE cid = %s))
                                         ON CONFLICT DO NOTHING;''',
                                     (stu[3], sel, stu[3], sel))
            except Exception as e:
                print(e)

    @timer
    def batch_loader(self, pg: int):
        college = []
        stu_info = []
        stu_sel = []
        for stu in self.cdata:
            college.append((stu[2].split('(')[0],
                            stu[2].split('(')[1].replace(')', '')))
            stu_info.append((stu[3], stu[0], stu[1], stu[2].split('(')[0]))
            for sel in stu[4:]:
                stu_sel.append((stu[3], sel))
        try:
            execute_batch(self.cur,
                          '''INSERT INTO project1.college(name, eng_name)
                             SELECT %s, %s
                             ON CONFLICT DO NOTHING;''',
                          college,
                          page_size=pg)
            execute_batch(self.cur,
                          '''INSERT INTO project1.student
                             VALUES (%s, %s, %s, (SELECT id
                                                  FROM project1.college
                                                  WHERE name = %s))
                             ON CONFLICT DO NOTHING;''',
                          stu_info,
                          page_size=pg)
            execute_batch(self.cur,
                          '''INSERT INTO project1.learnt
                             VALUES (%s, %s)
                             ON CONFLICT DO NOTHING;''',
                          stu_sel,
                          page_size=pg)
        except Exception as e:
            print(e)

    @timer
    def defer_loader(self, pg: int):
        college = []
        stu_info = []
        stu_sel = []
        for stu in tqdm(self.cdata):
            college.append((stu[2].split('(')[0],
                            stu[2].split('(')[1].replace(')', '')))
            stu_info.append((stu[3], stu[0], stu[1], stu[2].split('(')[0]))
            for sel in stu[4:]:
                stu_sel.append((stu[3], sel))
        try:
            self.cur.execute('BEGIN TRANSACTION;')
            self.cur.execute('SET CONSTRAINTS ALL DEFERRED;')
            execute_batch(self.cur,
                          '''INSERT INTO project1.college(name, eng_name)
                             SELECT %s, %s
                             ON CONFLICT DO NOTHING;''',
                          tqdm(college),
                          page_size=pg)
            execute_batch(self.cur,
                          '''INSERT INTO project1.student
                             VALUES (%s, %s, %s, (SELECT id
                                                  FROM project1.college
                                                  WHERE name = %s))
                             ON CONFLICT DO NOTHING;''',
                          tqdm(stu_info),
                          page_size=pg)
            execute_batch(self.cur,
                          '''INSERT INTO project1.learnt
                             VALUES (%s, %s)
                             ON CONFLICT DO NOTHING;''',
                          tqdm(stu_sel),
                          page_size=pg)
        except Exception as e:
            print(e)

    async def async_loader(self, conf='./user.yml'):
        with open(conf, 'r', encoding='utf-8') as cfg:
            conf = yaml.safe_load(cfg)
        conn = await asyncpg.connect(host=conf['host'], port=conf['port'],
                                     user=conf['user'], password=conf['pwd'],
                                     database=conf['db'])
        college = []
        stu_info = []
        stu_sel = []
        for stu in atqdm(self.cdata):
            college.append((stu[2].split('(')[0],
                            stu[2].split('(')[1].replace(')', '')))
            stu_info.append((int(stu[3]), stu[0], stu[1], stu[2].split('(')[0]))
            for sel in stu[4:]:
                stu_sel.append((int(stu[3]), sel))
        await conn.execute('BEGIN TRANSACTION;')
        await conn.execute('SET CONSTRAINTS ALL DEFERRED;')
        await conn.executemany('''INSERT INTO project1.college(name, eng_name)
                            SELECT $1, $2
                            ON CONFLICT DO NOTHING;''', atqdm(college))
        await conn.executemany('''INSERT INTO project1.student
                            VALUES ($1, $2, $3, (SELECT id
                                                 FROM project1.college
                                                 WHERE name = $4))
                            ON CONFLICT DO NOTHING;''', atqdm(stu_info))
        await conn.executemany('''INSERT INTO project1.learnt
                             VALUES ($1, $2)
                             ON CONFLICT DO NOTHING;''', atqdm(stu_sel))
        await conn.execute('COMMIT;')

    async def pool_loader(self, conf='./user.yml'):
        college = []
        stu_info = []
        stu_sel = []
        college_seq = []
        stu_info_seq = []
        stu_sel_seq = []
        seq = 0
        for stu in tqdm(self.cdata):
            if seq == 500000:
                seq = 0
                college.append(college_seq.copy())
                stu_info.append(stu_info_seq)
                stu_sel.append(stu_sel_seq)
                college_seq.clear()
                stu_info_seq.clear()
                stu_sel_seq.clear()
            college_seq.append((stu[2].split('(')[0],
                                stu[2].split('(')[1].replace(')', '')))
            stu_info_seq.append(
                (int(stu[3]), stu[0], stu[1], stu[2].split('(')[0]))
            for sel in stu[4:]:
                stu_sel_seq.append((int(stu[3]), sel))
            seq += 1
        college.append(college_seq)
        stu_info.append(stu_info_seq)
        stu_sel.append(stu_sel_seq)
        self.cur.execute('BEGIN TRANSACTION;')
        self.cur.execute('SET CONSTRAINTS ALL DEFERRED;')

        conf = yaml.safe_load(open(conf, 'r', encoding='utf-8'))
        async with asyncpg.create_pool(host=conf['host'], port=conf['port'],
                                       user=conf['user'], password=conf['pwd'],
                                       database=conf['db']) as pool:
            async with pool.acquire() as cur:
                async for seq in atqdm(college):
                    await cur.executemany('''INSERT INTO project1.college(name, eng_name)
                                        SELECT $1, $2
                                        ON CONFLICT DO NOTHING;''', seq)
                async for stu in atqdm(stu_info):
                    await cur.executemany('''INSERT INTO project1.student
                                        VALUES ($1, $2, $3, (SELECT id
                                                             FROM project1.college
                                                             WHERE name = $4))
                                        ON CONFLICT DO NOTHING;''', stu)
                async for sel in atqdm(stu_sel):
                    await cur.executemany('''INSERT INTO project1.learnt
                                         VALUES ($1, $2)
                                         ON CONFLICT DO NOTHING;''', sel)


if __name__ == '__main__':
    dbs = DBStu()
    # dbs.batch_loader()
    asyncio.run(dbs.async_loader())
