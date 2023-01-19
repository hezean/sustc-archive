# -*- coding:UTF-8 -*-
import time

import pandas as pd


def timer(func):
    def tm(*args, **kwargs):
        t = time.perf_counter()
        result = func(*args, **kwargs)
        print(f'{func.__name__} cost {time.perf_counter() - t:.6f} sec')
        return result

    return tm


def clear_str(s):
    if s is None:
        return None
    return s.strip() \
        .replace('（', '(') \
        .replace('）', ')') \
        .replace('，', ',')


def clear_teacher(t):
    if t is None:
        return None
    return [t.strip() for t in clear_str(t).split(',')]


def clear_pre(pre):
    if pre is None:
        return None
    return [[clear_str(ors.strip('() ') + (')' if ors.strip('() ').endswith(('上', '下')) else ''))
             for ors in ands.split('或者')]
            for ands in pre.split('并且')]


def clear_class_list(df):
    class_list = []
    for cls in df:
        cls_items = []
        for item in cls:
            item_cls = {}
            item_cls.update({'weekList': [int(x) for x in item.get('weekList')]})
            item_cls.update({'location': clear_str(item.get('location'))})
            item_cls.update({'classTime': item.get('classTime')})
            item_cls.update({'weekday': item.get('weekday')})
            cls_items.append(item_cls)
        class_list.append(cls_items)
    return class_list


def clear_data(ori: str, out: str):
    with open(ori) as f:
        cif = pd.read_json(f)

    nif = pd.DataFrame()
    nif['totalCapacity'] = cif['totalCapacity']
    nif['courseId'] = cif['courseId']
    nif['prerequisite'] = [clear_pre(pre) for pre in cif['prerequisite']]
    nif['courseHour'] = cif['courseHour']
    nif['courseCredit'] = cif['courseCredit']
    nif['courseName'] = [clear_str(c) for c in cif['courseName']]
    nif['className'] = [clear_str(c) for c in cif['className']]
    nif['courseDept'] = cif['courseDept']
    nif['teacher'] = [clear_teacher(t) for t in cif['teacher']]
    nif['classList'] = clear_class_list(cif['classList'])

    with open(out, 'w+', encoding='utf-8') as f:
        f.write(nif.to_json(orient='records', force_ascii=False))
