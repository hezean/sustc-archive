import os
from tqdm import tqdm

for i in tqdm(range(200)):
    os.system('python3 gen.py')
    os.system('python3 PA3.py 10router.txt > 1.out')
    os.system('python3 dog.py 10router.txt > 2.out')
    os.system('python3 lmq.py 10router.txt > 3.out')

    # with open('1.out') as f1, open('2.out') as f2:
    with open('1.out') as f1, open('2.out') as f2, open('3.out') as f3:
        t1 = f1.read()
        t2 = f2.read()
        t3 = f3.read()

        if t1 != t2:
            print('t1 != t2', i)
            break

        if t1 != t3:
            print('t1 != t3', i)
            break
