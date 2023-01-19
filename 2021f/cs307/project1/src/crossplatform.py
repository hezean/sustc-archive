import time
import logging

import pandas as pd

cif = pd.read_json('data/course_info.json')
logging.basicConfig(level = logging.INFO,format = '%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def timer(func):
    def tm(*args, **kwargs):
        t = time.perf_counter()
        result = func(*args, **kwargs)
        logger.info(f'{func.__name__} cost {time.perf_counter() - t:.6f} sec')
        return result

    return tm


@timer
def fs_query() -> set:
    result_set = set()
    for _ in range(10):
        result_set.clear()
        for index, c in cif.iterrows():
            if c['courseCredit'] > 2:
                result_set.add(c['courseId'])
    return result_set


if __name__ == '__main__':
    fs_query()
