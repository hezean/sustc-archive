from random import random


with open('mat2.txt', 'w+') as f:
    for _ in range(100):
        for _ in range(100):
            f.write(f'{random()*100:.2f} ')
        f.write('\n')
