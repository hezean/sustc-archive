from cmath import inf
from queue import Queue
import numpy as np
import random
import time

COLOR_BLACK=-1
COLOR_WHITE=1
COLOR_NONE=0
random.seed(0)
from numba import njit
O3 = njit(cache=True, fastmath=True, inline='always')

chessboard_size = 8
direction = [(-1, 0), (0, 1), (1, 0), (0, -1), (1, 1), (1, -1), (-1, 1), (-1, -1)]

def get_candidate(chessboard, color):
    none_x, none_y = np.where(chessboard == COLOR_NONE)
    idx = list(zip(none_x, none_y))
    candidate_list = []
    for i in idx:
        for d in direction:
            ix = i[0]
            iy = i[1]
            dx, dy = d
            tmp = res(chessboard, color, ix, iy, dx, dy)
            if tmp is not None:
                candidate_list.append(tmp)
    return list(set(candidate_list))

@O3
def res(chessboard, color, ix, iy, dx, dy):
    cur_x = ix + dx
    cur_y = iy + dy

    cnt = 0
    flag = False
    while cur_x >= 0 and cur_x < chessboard_size and cur_y >= 0 and cur_y < chessboard_size:
        if chessboard[cur_x, cur_y] == color:
            if cnt == 0:
                break
            flag = True
            break
        elif chessboard[cur_x, cur_y] == -color:
            cnt += 1
        else:
            break
        cur_x += dx
        cur_y += dy
    if flag == True:
        return (ix, iy)
    else:
        return None


class AI(object):
    board_weight = np.array([
        [-300, 65, -20, -15, -15, -20, 65, -300],
        [65, 25, -10, -8, -8, -10, 25, 65],
        [-20, -10, -9, -6, -6, -9, -15, -20],
        [-15, -8, -6, -3, -3, -6, -8, -15],
        [-15, -8, -6, -3, -3, -6, -8, -15],
        [-20, -10, -9, -2, -2, -9, -15, -20],
        [65, 35, -10, -8, -8, -10, 35, 65],
        [-300, 65, -20, -15, -15, -20, 65, -300]]
    )
    corners = [(0, 0), (7, 0), (0, 7), (7, 7)]
    mobility_weight = 15
    chess_weight = 20
    stable_weight = 30
    dir1 = [(1, 0), (1, 1), (0, 1), (-1, 1)]
    dir2 = [(-1, 0), (-1, -1), (0, -1), (1, -1)]

    def __init__(self, chessboard_size, color, time_out):
        self.chessboard_size = chessboard_size
        #You are white or black
        self.color = color
        #the max time you should use, your algorithm's run time must not exceed the time limit.
        self.time_out = time_out
        # You need to add your decision to your candidate_list. The system will get the end of your candidate_list as your decision.
        self.candidate_list = []

    def count_stable(self, board):
        stable = np.zeros((self.chessboard_size, self.chessboard_size), dtype=bool)
        q = Queue()
        for i in range(4):
            if board[self.corners[i]] != 0:
                q.put(self.corners[i])
                stable[self.corners[i]] = True
        while not q.empty():
            pos = q.get()
            for i in range(8):
                cur = (pos[0] + direction[i][0], pos[1] + direction[i][1])
                if not (0 <= cur[0] < self.chessboard_size and 0 <= cur[1] < self.chessboard_size) or board[cur] != board[pos] or stable[cur]:
                    continue
                flag = True
                for j in range(4):
                    adj1 = (cur[0] + self.dir1[j][0], cur[1] + self.dir1[j][1])
                    adj2 = (cur[0] + self.dir2[j][0], cur[1] + self.dir2[j][1])
                    if not (0 <= adj1[0] < self.chessboard_size and 0 <= adj1[1] < self.chessboard_size) or not (0 <= adj2[0] < self.chessboard_size and 0 <= adj2[1] < self.chessboard_size) or stable[adj1] == True or stable[adj2] == True:
                        continue
                    else:
                        flag = False
                        break
                if flag is True:
                    q.put(cur)
                    stable[cur] = True
        for i in range(self.chessboard_size):
            l = 0
            r = self.chessboard_size - 1
            if stable[i][l] == True and stable[i][r] == True and board[i][l] == board[i][r]:
                r -= 1
                l += 1
                while l < self.chessboard_size and stable[i][l] == True and board[i][l-1] == board[i][l]:
                    l += 1
                while r >= 0 and stable[i][r] == True and board[i][r + 1] == board[i][r]:
                    r -= 1
                if l <= r:
                    flag = True
                    for j in range(l, r + 1):
                        if(board[i][j] != -board[i][0]):
                            flag = False
                            break
                    if flag:
                        for j in range(l, r + 1):
                            stable[i][j] = True
        for i in range(self.chessboard_size):
            l = 0
            r = self.chessboard_size - 1
            if stable[l][i] == True and stable[r][i] == True and board[l][i] == board[r][i]:
                r -= 1
                l += 1
                while l < self.chessboard_size and stable[l][i] == True and board[l - 1][i] == board[l][i]:
                    l += 1
                while r >= 0 and stable[r][i] == True and board[r + 1][i] == board[r][i]:
                    r -= 1
                if l <= r:
                    flag = True
                    for j in range(l, r + 1):
                        if(board[j][i] != -board[0][i]):
                            flag = False
                            break
                    if flag:
                        for j in range(l, r + 1):
                            stable[j][i] = True
        return self.color * np.sum(stable * board)

    def go(self, chessboard):
        self.candidate_list.clear()
        self.candidate_list = get_candidate(chessboard, self.color)
        if len(self.candidate_list) == 0:
            return
        chess_num = self.chess_count(chessboard)
        stage = np.sum(chess_num)
        if stage < 52:
            self.alpha_beta(chessboard, 4)
        elif stage < 56:
            self.alpha_beta(chessboard, 6)
        else:
            self.alpha_beta(chessboard, 8)

    def chess_count(self, chessboard):
        return np.array(np.where(chessboard == self.color)).shape[1], np.array(np.where(chessboard == -self.color)).shape[1]
    
    def place_chess(self, chessboard, color, pos):
        board = np.copy(chessboard)
        board[pos] = color
        for d in direction:
            flag = False
            cur = (pos[0] + d[0], pos[1] + d[1])
            list = []
            while cur[0] >= 0 and cur[0] < self.chessboard_size and cur[1] >= 0 and cur[1] < self.chessboard_size:
                if board[cur] == color:
                    flag = True
                    break
                elif board[cur] == COLOR_NONE:
                    break
                else:
                    list.append(cur)
                    cur = (cur[0] + d[0], cur[1] + d[1])
            if flag:
                for idx in list:
                    board[idx] = color
        return board
    
    def game_end(self, board):
        return len(get_candidate(board, COLOR_BLACK)) == 0 and len(get_candidate(board, COLOR_WHITE)) == 0

    def calculate_score(self, board):
        num = self.chess_count(board)
        stage = np.sum(num)
        if stage >= 20:
            self_mobility = len(get_candidate(board, self.color))
            oppo_mobility = len(get_candidate(board, -self.color))
        if stage >= 35 and self_mobility == 0 and oppo_mobility == 0:
            if num[0] < num[1]:
                return 99999999999999999
            elif num[0] > num[1]:
                return -99999999999999999
        weight = self.color * np.sum(board * self.board_weight)
        if stage >= 20:
            weight += self.mobility_weight * (self_mobility - oppo_mobility)
        if stage >= 50:
            weight += self.chess_weight * (num[1] - num[0])
        if stage >= 30:
            weight -= self.stable_weight * self.count_stable(board)
        return weight

    def alpha_beta(self, board, depth):
        def max_value(chessboard, alpha, beta, depth, returnMove = False):
            list = get_candidate(chessboard, self.color)
            if depth == 0 or self.game_end(chessboard):
                return self.calculate_score(chessboard)
            if len(list) == 0:
                return min_value(chessboard, alpha, beta, depth - 1) + 200
            v = -inf
            for a in list:
                v2 = min_value(self.place_chess(chessboard, self.color, a), alpha, beta, depth - 1)
                if v2 > v:
                    v = v2
                    if returnMove:
                        self.candidate_list.append(a)
                if v >= beta:
                    return v
                alpha = max(alpha, v)
            return v
        def min_value(chessboard, alpha, beta, depth):
            list = get_candidate(chessboard, -self.color)
            if depth == 0 or self.game_end(chessboard):
                return self.calculate_score(chessboard)
            if len(list) == 0:
                return max_value(chessboard, alpha, beta, depth - 1) - 200
            v = inf
            for a in list:
                v2 = max_value(self.place_chess(chessboard, -self.color, a), alpha, beta, depth - 1)
                v = min(v, v2)
                if v <= alpha:
                    return v
                beta = min(beta, v)
            return v
        max_value(board, -inf, inf, depth, True)

if __name__ == '__main__':
    board = \
        [[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 1, 1, -1, 0, 0, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 1, 0, -1, 0, 1, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, -1, -1, -1, 1, 0, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 0, 0, 1, -1, -1, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]]
    board2 = \
        [[0, 0, 0, 0, 0, 0, 0, 0], [0, 1, 0, 1, 0, 1, 0, 1], [-1, 0, -1, 0, -1, 0, -1, 0],
         [0, 1, 0, 1, 0, 1, 0, 1], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0]]

    board3 = \
    [
        [1, 0, 0, 0, 0, 0, 0, -1], 
        [0, 1, 0, 1, 0, 1, 0, 1], 
        [-1, 0, -1, 0, -1, 0, -1, 0], 
        [0, 1, 0, 1, 0, 1, 0, 1],
        [1, 0, 0, 0, 0, 0, 0, 0], 
        [0, 1, 0, 0, 0, 0, 0,0], 
        [1, 1, 1, 1, 1, 0, 0, 0], 
        [1, 1, 1, 1, 1, -1, 0, 0]]

    board4 = \
    [[1, 1, 1, 1, -1, -1, 0, 0], [1, 1, 1, 1, -1, 0, 0, 0], [1, 1, 1, -1, 0, -1, 0, 0], [-1, -1, -1, -1, 0, 0, 0, 0],
     [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0]]

    board5 = np.array([[-1, -1, -1,  1,  0,  1,  1, -1],[ 0 , 1,  1,  1,  1,  1,  1, -1], [-1,  1,  1,  1, -1,  1,  1, -1],[-1, -1,  1,  1,  1,  1,  1, -1],[-1,  1, -1,  1, -1,  1,  1, -1],[-1,  1, -1, -1,  1,  1,  1, -1],[-1,  1,  1,  1, -1,  1,  1,  0],[-1,  1,  1, -1, -1, -1,  0,  1]])
    board6 = np.array(
        [[ -1,  1,  1,  1,  1,  1,  1,  0],
        [ -1,  1,  1,  1,  1,  1,  1,  0],
        [ 1,  1,  1 , 1,  1,  1,  0,  1],
        [ 1,  1,  1,  1,  1,  1,  1,  0],
        [ 1, -1,  1,  1,  1,  1, -1,  0],
        [ 1,  1,  1,  1, -1, -1, -1, -1],
        [ 1,  1,  1,  1, -1, -1, -1, -1],
        [ -1, -1,  1,  1, 1, 1, -1, -1]]
    )
    ai = AI(8, 1, 5000)
    ai.go(board6)
    print(ai.candidate_list)