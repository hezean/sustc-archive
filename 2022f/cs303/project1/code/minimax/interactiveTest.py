from main import AI
from milestones.ms30 import AI as AI2
from typing import Tuple, Optional

import numpy as np
from colorama import Fore

COLOR_WHITE = 1
COLOR_BLACK = -1
COLOR_NONE = 0

fwd = {(x, y) for x in range(-1, 2) for y in range(-1, 2)} - {(0, 0)}

white_flip = 0
black_flip = 0


def end(chessboard: np.array):
    return len(np.where(chessboard == COLOR_NONE)) == 0


def win(chessboard: np.array):
    black = np.where(chessboard == COLOR_BLACK)
    white = np.where(chessboard == COLOR_WHITE)
    return COLOR_BLACK if len(black) < len(white) else COLOR_WHITE


def update(cb, x, y, col):
    def _forward(step: Tuple[int, int]) -> Optional[Tuple[int, int]]:

        _x, _y = x + step[0], y + step[1]
        while 0 <= _x < len(cb) and 0 <= _y < len(cb):
            if cb[_x][_y] == COLOR_NONE:
                return None
            elif cb[_x][_y] == -col:
                _x, _y = _x + step[0], _y + step[1]
            else:
                return _x, _y
        return None

    global black_flip
    global white_flip
    for f in fwd:
        if dest := _forward(f):
            _x, _y = x, y
            while (_x, _y) != dest:
                cb[_x][_y] = col
                if col == COLOR_WHITE:
                    black_flip += 1
                else:
                    white_flip += 1
                _x, _y = _x + f[0], _y + f[1]

    print(f'{Fore.LIGHTWHITE_EX}>> white_flip = {white_flip}      black_flip = {black_flip}')
    return cb


def add_and_print(cb, x, y, col):
    cb[x][y] = col
    update(cb, x, y, col)

    _str = f'{Fore.LIGHTWHITE_EX}>> white\n' if col == COLOR_WHITE else f'{Fore.LIGHTWHITE_EX}>> black\n'
    _str += '  0 1 2 3 4 5 6 7\n'
    for _x, r in enumerate(cb):
        _str += f'{str(_x)} '
        for _y, c in enumerate(r):
            if cb[_x][_y] == COLOR_NONE:
                _str += f'{Fore.WHITE}○ '
            elif (x, y) == (_x, _y):
                _str += f'{Fore.YELLOW if col == COLOR_WHITE else Fore.BLUE}● '
            else:
                _str += f'{Fore.LIGHTWHITE_EX if cb[_x][_y] == COLOR_WHITE else Fore.BLACK}● '
        _str += '\n'
    print(_str)
    ws = len(np.where(cb == COLOR_WHITE)[0])
    bs = len(np.where(cb == COLOR_BLACK)[0])
    print(f'{Fore.WHITE}white = {ws}      black = {bs}')
    # time.sleep(0.5)


def add_only(cb, x, y, col):
    cb[x][y] = col
    return update(cb, x, y, col)


def play(white: AI, black: AI):
    cb = np.array([[0] * 8] * 8)
    cb[3][3] = cb[4][4] = 1
    cb[3][4] = cb[4][3] = -1

    col = COLOR_BLACK
    while not end(cb):
        if col == COLOR_BLACK:
            black.go(cb)
            if not black.candidate_list:
                continue
            x, y = black.candidate_list[-1]
            add_only(cb, x, y, COLOR_BLACK)
        else:
            white.go(cb)
            if not white.candidate_list:
                continue
            x, y = white.candidate_list[-1]
            add_only(cb, x, y, COLOR_WHITE)
        col *= -1
    return cb


#
if __name__ == '__main__':
    cb = np.array([[0] * 8] * 8)
    cb[3][3] = cb[4][4] = 1
    cb[3][4] = cb[4][3] = -1

    b = AI(8, COLOR_BLACK, 5)
    w = AI2(8, COLOR_WHITE, 5)

    col = COLOR_WHITE
    while not end(cb):
        col *= -1
        if col == COLOR_BLACK:
            b.go(cb)
            if not b.candidate_list:
                continue
            x, y = b.candidate_list[-1]
            add_and_print(cb, x, y, COLOR_BLACK)
        else:
            w.go(cb)
            if not w.candidate_list:
                continue
            x, y = w.candidate_list[-1]
            add_and_print(cb, x, y, COLOR_WHITE)
    print(1)
    print('>>> end' if end(cb) else '>>> unfinished')
    print('Black win' if win(cb) == COLOR_BLACK else 'White win')
