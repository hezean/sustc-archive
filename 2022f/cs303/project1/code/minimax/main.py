import itertools
import time
import numpy as np
from queue import Queue
from typing import List, Tuple, Optional
from numba import njit

COLOR_WHITE, COLOR_BLACK, COLOR_NONE = 1, -1, 0
board_size, _ = 8, 0
directions = {(x, y) for x in range(-1, 2) for y in range(-1, 2)} - {(0, 0)}

Coordinate = Tuple[int, int]
O3 = njit(cache=True, fastmath=True, inline='always')

def generate_full_weight(lower_tri):
    lower_tri = np.array(lower_tri)
    lower_tri *= np.tril(np.ones_like(lower_tri))
    res = (lower_tri + lower_tri.T - np.diag(np.diag(lower_tri)))
    res = np.hstack((res, np.fliplr(res)))
    return np.vstack((res, np.flipud(res)))

# params =========================================================================================

stage_div = (8, 20)

w_position = generate_full_weight([
    [     -1000,         _,         _,         _],
    [       200,       100,         _,         _],
    [       -40,       -30,        -5,         _],
    [       -50,       -25,         0,         5],
])

w_util_punish = 100000000000000

ws_stable = (-100, -150, -150)
ws_mobility = (50, 100, 80)

w_drop_par = -60
w_clusting = -10

w_stable = None
w_revert = None
w_mobility = None

def update_weight(step):
    if step < stage_div[0]:
        stage = 0
    elif step < stage_div[1]:
        stage = 1
    else:
        stage = 2
    global w_stable, w_revert, w_mobility
    w_stable = ws_stable[stage]
    w_mobility = ws_mobility[stage]

# params =========================================================================================


@O3
def _walk_straight(chessboard: np.array, color: int, start: Coordinate, direct: Coordinate) -> Optional[Coordinate]:
    dx, dy = direct
    x, y, dist = start[0] + dx, start[1] + dy, 1
    while 0 <= x < board_size and 0 <= y < board_size:
        if chessboard[x][y] == -color:
            x, y, dist = x + dx, y + dy, dist + 1
        elif chessboard[x][y] == COLOR_NONE:
            return (x, y) if dist > 1 else (-1, -1)
        else:
            return (-1, -1)
    return (-1, -1)

def candidates(chessboard: np.array, color: int):
    sx, sy = np.where(chessboard == color)
    drops = list(zip(sx, sy))
    res = []
    for drop, direct in itertools.product(drops, directions):
        tar = _walk_straight(chessboard, color, drop, direct)
        if tar[0] >= 0:
            res.append(tar)
    return list(set(res))


corners = ((0, 0), (0, 7), (7, 7), (7, 0))
pos_dir = ((0, 1), (1, 0), (0, -1), (-1, 0))
rev_dir = ((1, 0), (0, -1), (-1, 0), (0, 1))

def stable_cnt(chessboard: np.array, color: int):
    cnt = 0
    stop = [0, 0, 0, 0]
    q, vis = Queue(), set()
    stable_board = np.zeros((board_size, board_size), dtype=bool)
    for i in range(4):
        if chessboard[corners[i]] != color:
            continue
        q.put(corners[i])
        stable_board[corners[i]] = True
        vis.add(corners[i])
        cx, cy = corners[i]
        px, py = pos_dir[i]
        for d in range(1, 7):
            ep = cx + px * d, cy + py * d
            if chessboard[ep] != color:
                break
            stop[i] = d + 1
            cnt += 1
            vis.add(ep)
            stable_board[ep] = True

    for i in range(4):
        if chessboard[corners[i]] != color:
            continue
        cx, cy = corners[i]
        rx, ry = rev_dir[i]
        for d in range(1, 7 - stop[i - 1]):
            ep = cx + rx * d, cy + ry * d
            if chessboard[ep] != color:
                break
            cnt += 1
            vis.add(ep)
            stable_board[ep] = True

    while not q.empty():
        pos = q.get()
        if not is_stable(pos, chessboard, stable_board, color):
            print(pos, 'is not stable')
            continue
        cnt += 1
        stable_board[pos] = True
        for dx, dy in directions:
            pos_new = pos[0] + dx, pos[1] + dy
            if 0 <= pos_new[0] < board_size and 0 <= pos_new[1] < board_size and chessboard[pos_new] == color and pos_new not in vis:
                vis.add(pos_new)
                q.put(pos_new)
    return cnt

@O3
def is_stable(pos: Coordinate, chessboard: np.array, stable_board: np.array, color: int) -> bool:
    nx, ny = pos
    for dx, dy in ((0, 1), (1, 0), (1, 1), (1, -1)):
        px1, py1 = nx + dx, ny + dy
        px2, py2 = nx - dx, ny - dy
        if 0 <= px1 <= 7 and 0 <= py1 <= 7 and 0 <= px2 <= 7 and 0 <= py2 <= 7 \
                and stable_board[px1, py1] and stable_board[px2, py2] \
                and chessboard[px1, py1] != color and chessboard[px2, py2] != color:
            return False
    return True

def child_state(chessboard: np.array, drop: Coordinate, color: int):
    board = chessboard.copy()
    revert_cnt = 0
    for direct in directions:
        end = cs_forward(board, drop, direct, color)
        if end[0] >= 0:
            revert_cnt += cs_fill(board, drop, end, direct, color)
    return board, revert_cnt

@O3
def cs_forward(chessboard: np.array, drop: Coordinate, direct: Coordinate, color: int) -> Optional[Coordinate]:
    x, y = drop[0] + direct[0], drop[1] + direct[1]
    while 0 <= x < board_size and 0 <= y < board_size:
        if chessboard[x][y] == color:
            return x, y
        elif chessboard[x][y] == COLOR_NONE:
            return -1, -1
        x, y = x + direct[0], y + direct[1]
    return -1, -1

@O3
def cs_fill(chessboard: np.array, start: Coordinate, end: Coordinate, direct: Coordinate, color: int) -> int:
    x, y = start
    revert_cnt = 0
    while (x, y) != end:
        chessboard[x][y] = color
        revert_cnt += 1
        x, y = x + direct[0], y + direct[1]
    return revert_cnt


def alphabeta(chessboard: np.array, color: int, max_depth: int, candidate_list: List[Coordinate]) -> Optional[Coordinate]:
    return maximize(chessboard, color, 0, max_depth, -np.inf, np.inf, candidate_list, 0)[-1]

def maximize(chessboard: np.array, color: int, depth: int, max_depth: int, alpha: float, beta: float, candidate_list: List[Coordinate], revert_cnt: int) -> Tuple[float, Optional[Coordinate]]:
    if depth >= max_depth or np.count_nonzero(chessboard == COLOR_NONE) == 0:
        return utility(chessboard, color, revert_cnt), None
    cand = candidate_list or candidates(chessboard, color)
    if len(cand) == 0:
        if len(candidates(chessboard, -color)) == 0:
            return len(np.where(chessboard == color)[0]) - len(np.where(chessboard == -color)[0]) * w_util_punish, None
        return utility(chessboard, color, revert_cnt), None

    v, drop = -np.inf, None
    for pdrop in sorted(cand, key=lambda pos: w_position[pos]):
        cs, rev = child_state(chessboard, pdrop, color)
        v2, _ = minimize(cs, -color, depth + 1, max_depth, alpha, beta, candidate_list, rev)
        if v2 > v:
            v, drop = v2, pdrop
            if depth == 0:
                candidate_list.append(pdrop)
        alpha = max(alpha, v)
        if beta <= alpha:
            return v2, pdrop
    return v, drop

def minimize(chessboard: np.array, color: int, depth: int, max_depth: int, alpha: float, beta: float, candidate_list: List[Coordinate], revert_cnt: int) -> Tuple[float, Optional[Coordinate]]:
    if depth >= max_depth or np.count_nonzero(chessboard == COLOR_NONE) == 0:
        return utility(chessboard, color, revert_cnt), None
    cand = candidates(chessboard, color)
    if len(cand) == 0:
        if len(candidates(chessboard, -color)) == 0:
            return len(np.where(chessboard == -color)[0]) - len(np.where(chessboard == color)[0]) * w_util_punish, None
        return utility(chessboard, color, revert_cnt), None

    v, drop = np.inf, None
    for pdrop in sorted(cand, key=lambda pos: w_position[pos]):
        cs, rev = child_state(chessboard, pdrop, color)
        v2, _ = maximize(cs, -color, depth + 1, max_depth, alpha, beta, candidate_list, rev)
        if v2 < v:
            v, drop = v2, pdrop
        beta = min(beta, v)
        if beta <= alpha:
            return v2, pdrop
    return v, drop


def utility(chessboard: np.array, color: int, revert_cnt: int) -> float:
    # position weight
    res = np.sum(w_position * chessboard) * color

    # clusting
    self_drops_x, self_drops_y = np.where(chessboard == color)
    res += w_clusting * np.mean(np.sqrt(np.square(self_drops_x - np.mean(self_drops_x)) + np.square(self_drops_y - np.mean(self_drops_y))))

    # drops parity
    self_drops, op_drops = len(self_drops_x), len(np.where(chessboard == -color)[0])
    res += w_drop_par * (self_drops - op_drops)

    # mobility: cost too much time
    self_cands, op_cands = len(candidates(chessboard, color)), len(candidates(chessboard, -color))
    if self_cands or op_cands:
        res += w_mobility * (self_cands - op_cands)

    # stable count
    res += w_stable * stable_cnt(chessboard, color)

    return res


class AI:
    def __init__(self, chessboard_size, color, timeout):
        self.color = color
        self.candidate_list = []
        self.step = 0

    def go(self, chessboard: np.array):
        self.step += 1
        update_weight(self.step)
        self.candidate_list = candidates(chessboard, self.color)
        res = alphabeta(chessboard, self.color, 4, self.candidate_list)
        if res is not None:
            self.candidate_list.append(res)
