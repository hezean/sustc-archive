import contextlib
import math
import itertools
import random
import numpy as np
from typing import Dict, List, Tuple, Optional
from numba import njit
from timeout_decorator import timeout, TimeoutError

COLOR_WHITE, COLOR_BLACK, COLOR_NONE = 1, -1, 0
board_size, _ = 8, 0
directions = {(x, y) for x in range(-1, 2) for y in range(-1, 2)} - {(0, 0)}

Coordinate = Tuple[int, int]
O3 = njit(cache=True, fastmath=True, inline='always')

# params ==========================================================================================

# random.seed(42)
ucb_c = 1 / (2 * math.sqrt(2))

# utilities =======================================================================================

# @O3
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


def child_state(chessboard: np.array, drop: Coordinate, color: int):
    board = chessboard.copy()
    revert_cnt = 0
    for direct in directions:
        end = cs_forward(board, drop, direct, color)
        if end[0] >= 0:
            revert_cnt += cs_fill(board, drop, end, direct, color)
    return board, revert_cnt

# @O3
def cs_forward(chessboard: np.array, drop: Coordinate, direct: Coordinate, color: int) -> Optional[Coordinate]:
    x, y = drop[0] + direct[0], drop[1] + direct[1]
    while 0 <= x < board_size and 0 <= y < board_size:
        if chessboard[x][y] == color:
            return x, y
        elif chessboard[x][y] == COLOR_NONE:
            return -1, -1
        x, y = x + direct[0], y + direct[1]
    return -1, -1

# @O3
def cs_fill(chessboard: np.array, start: Coordinate, end: Coordinate, direct: Coordinate, color: int) -> int:
    x, y = start
    revert_cnt = 0
    while (x, y) != end:
        chessboard[x][y] = color
        revert_cnt += 1
        x, y = x + direct[0], y + direct[1]
    return revert_cnt

# core ============================================================================================

class Node:
    def __init__(self, parent: "Node", state: np.array, color: int):
        self.parent = parent
        self.children = []
        self.color = color
        self.state = state
        self.w = 0
        self.n = 0
        if parent is not None:
            parent.children.append(self)


class AI:
    def __init__(self, chessboard_size, color, timeout):
        self.color = color
        self.candidate_list = []
        self.timeout = timeout

    def go(self, chessboard: np.array):
        self.candidate_list = candidates(chessboard, self.color)
        if not self.candidate_list:
            return
        actions = {}
        with contextlib.suppress(TimeoutError):
            mcts_build(chessboard, self.candidate_list, self.color, actions)
        if actions:
            self.candidate_list.append(max(actions, key= lambda x: win_rate(actions[x])))


            res = max(actions, key= lambda x: win_rate(actions[x]))
            print('total sim time:', actions[res].parent.n)
            for k, v in actions.items():
                print(f'{k}  simulated {v.n} times, win rate {win_rate(v)}')

def win_rate(node: Node) -> float:
    return node.w / node.n

def simulate(node: Node) -> int:
    color = node.color
    chessboard = node.state
    cand = candidates(chessboard, color)
    while cand:
        while cand:
            chessboard, _ = child_state(chessboard, random.choice(cand), color)  # fixme: huristic?
            color *= -1
            cand = candidates(chessboard, color)
        color *= -1
        cand = candidates(chessboard, color)
    return len(np.where(chessboard == -node.color)[0]) - len(np.where(chessboard == node.color)[0])

def back_propagation(node: Node, is_win: int):
    if is_win > 0:
        is_win = 1
    elif is_win < 0:
        is_win = -1

    while node is not None:
        node.n += 1
        node.w += is_win
        is_win *= -1
        node = node.parent

def ucb(node: Node) -> float:
    return node.w / node.n + ucb_c * math.sqrt(math.log(node.parent.n) / node.n)

def select(node: Node) -> Node:
    child = max(node.children, key=ucb)
    while child.children:
        child = max(child.children, key=ucb)
    return child

def expand(node: Node):
    cand = candidates(node.state, node.color)
    if not cand:
        node.children.append(Node(node, node.state.copy(), -node.color))
        return
    for c in cand:
        cs, _ = child_state(node.state, c, node.color)
        node.children.append(Node(node, cs, -node.color))

@timeout(seconds=4.9)
def mcts_build(chessboard: np.array, init_cand: List[Coordinate], color: int, actions: Dict[Coordinate, Node]):
    root = Node(None, chessboard, color)
    for cand in init_cand:
        cs, _ = child_state(chessboard, cand, color)
        cs_node = Node(root, cs, -color)
        actions[cand] = cs_node
        back_propagation(cs_node, simulate(cs_node))

    while True:
        node = select(root)
        expand(node)
        for c in node.children:
            back_propagation(c, simulate(c))

if __name__ == '__main__':
    brd = np.zeros((8, 8))
    brd[3, 3] = brd[4, 4] = 1
    brd[3, 4] = brd[4, 3] = -1
    AI(8, 1, 2).go(brd)
