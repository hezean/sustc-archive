import copy
import math
import random
import time
import warnings
from argparse import ArgumentParser
from dataclasses import dataclass
from itertools import product
from multiprocessing import Pool
from typing import List, Tuple

import numpy as np

# misc #################################################################################################################

warnings.filterwarnings('ignore')

_arg_parser = ArgumentParser()
_arg_parser.add_argument('problem')  # path to carp.dat
_arg_parser.add_argument('--time', '-t', type=int)  # terminate in seconds [60, 600]
_arg_parser.add_argument('--seed', '-s', type=int)  # random seed

# constants ############################################################################################################

ARGS = _arg_parser.parse_args()
PROGRAM_DDL = time.time() + ARGS.time - 2
RAND_INIT_DDL = time.time() + ARGS.time / 2.5

CARP_DEPOT: int
CARP_CAPACITY: int
CARP_DEMANDS: np.array
CARP_COSTS: np.array
CARP_DISTANCES: np.array
CARP_EDGE_PPR: np.array

SA_HP_T = 10000
SA_HP_ALPHA = 0.999
SA_RESTART_COST_RT = 1.2
SA_DELTA_E_PENALTY_RNG = (8, 12)
SA_DELTA_E_PENALTY_SCL = 1000
SA_STABLE_LIMIT = 100

# typings ##############################################################################################################

edge_t = Tuple[int, int]


class Route:
    def __init__(self):
        self.edges: List[edge_t] = []
        self.cap_left = CARP_CAPACITY
        self.cost = 0.
        self._pos = CARP_DEPOT  # only used during init constructing

    def c3_candidates(self, tasks: np.array) -> List[edge_t]:
        candidates = list(zip(*np.where(np.logical_and(tasks > 0, tasks <= self.cap_left))))
        c3, c3dist = None, np.inf
        for edge in candidates:
            dist = CARP_DISTANCES[self._pos, edge[0]]
            if dist < c3dist:
                c3, c3dist = [edge], dist
            elif dist == c3dist:
                c3.append(edge)
        return c3  # noqa: typing

    def walk(self, edge: edge_t):
        """! only used during init constructing"""
        self.edges.append(edge)
        self.cap_left -= CARP_DEMANDS[edge]
        self.cost += CARP_DISTANCES[self._pos, edge[0]] + CARP_COSTS[edge]
        self._pos = edge[1]

    def remove_task(self, idx: int) -> edge_t:
        task = self.edges[idx]
        conn_last = CARP_DEPOT if idx == 0 else self.edges[idx - 1][1]
        conn_next = CARP_DEPOT if idx in [len(self.edges) - 1, -1] else self.edges[idx + 1][0]
        edge_from, edge_to = self.edges[idx]
        self.cost += (CARP_DISTANCES[conn_last, conn_next]
                      - (CARP_COSTS[task] + CARP_DISTANCES[conn_last, edge_from] + CARP_DISTANCES[edge_to, conn_next]))
        self.cap_left += CARP_DEMANDS[task]
        self.edges.pop(idx)
        return task

    def insert_task(self, task: edge_t, idx: int) -> bool:
        if CARP_DEMANDS[task] > self.cap_left:
            return False
        conn_last = CARP_DEPOT if idx == 0 else self.edges[idx - 1][1]
        conn_next = CARP_DEPOT if idx == len(self.edges) else self.edges[idx][0]
        edge_from, edge_to = task
        diff_pos = CARP_COSTS[task] + CARP_DISTANCES[conn_last, edge_from] + CARP_DISTANCES[edge_to, conn_next]
        diff_rev = CARP_COSTS[task] + CARP_DISTANCES[conn_last, edge_to] + CARP_DISTANCES[edge_from, conn_next]
        if diff_pos <= diff_rev:
            self.cost += diff_pos - CARP_DISTANCES[conn_last, conn_next]
            self.edges.insert(idx, task)
        else:
            self.cost += diff_rev - CARP_DISTANCES[conn_last, conn_next]
            self.edges.insert(idx, task[::-1])
        self.cap_left -= CARP_DEMANDS[task]
        return True

    def reverse_diff_cost(self, start: int, end: int) -> float:
        """if result < 0, optim it"""
        conn_last = CARP_DEPOT if start == 0 else self.edges[start - 1][1]
        conn_next = CARP_DEPOT if end in [-1, len(self.edges) - 1] else self.edges[end + 1][0]
        edge_from, edge_to = self.edges[start][0], self.edges[end][1]
        return -CARP_DISTANCES[conn_last, edge_from] + CARP_DISTANCES[conn_last, edge_to] \
               - CARP_DISTANCES[edge_to, conn_next] + CARP_DISTANCES[edge_from, conn_next]

    def flip_relax(self) -> float:
        for i in range(len(self.edges)):
            if (diff := self.reverse_diff_cost(i, i)) < 0:
                self.edges[i] = self.edges[i][::-1]
                self.cost += diff
        return self.cost

    def costs(self) -> float:
        cost = 0
        for i, edge in enumerate(self.edges):
            if i == 0:
                cost += CARP_DISTANCES[CARP_DEPOT, edge[0]]
            if i == len(self.edges) - 1:
                cost += CARP_DISTANCES[edge[1], CARP_DEPOT]
            else:
                cost += CARP_DISTANCES[edge[1], self.edges[i + 1][0]]
            cost += CARP_COSTS[edge]
        return cost

    def __repr__(self):
        _repr = f'Route[cost={self.cost}, cap_left={self.cap_left}, path={{'
        _repr += ' -> '.join(map(str, self.edges))
        _repr += '}]\n'
        return _repr

    def __str__(self):
        return '0,' + ','.join(map(lambda e: f'({e[0]},{e[1]})', self.edges)) + ',0'


@dataclass
class Solution:
    routing: List[Route]
    total_cost: int

    def snapshot(self) -> "Solution":
        return copy.deepcopy(self)

    def __lt__(self, other):
        return self.total_cost < other.total_cost

    def __repr__(self):
        return "  &&  ".join(map(lambda r: f'{str(r)} [[cost={r.cost}]] [[task={len(r.edges)}]]', self.routing)) + \
               f'\n>>> total={self.total_cost}'

    def __str__(self):
        return f's {",".join(map(str, self.routing))}\nq {self.total_cost:.0f}'


# carp solver ##########################################################################################################

def parse_problem(filename: str):
    global CARP_DEPOT, CARP_CAPACITY, CARP_DEMANDS, CARP_COSTS, CARP_DISTANCES, CARP_EDGE_PPR

    with open(filename, 'r') as f:
        f.readline()  # name
        vertices = int(f.readline().split()[-1])
        CARP_DEPOT = int(f.readline().split()[-1])
        edges = int(f.readline().split()[-1]) + int(f.readline().split()[-1])
        f.readline()  # vehicles
        CARP_CAPACITY = int(f.readline().split()[-1])
        f.readline()  # total cost
        f.readline()  # table header

        demands = np.zeros((vertices + 1, vertices + 1), dtype=int)
        costs = np.full((vertices + 1, vertices + 1), np.inf, dtype=float)
        np.fill_diagonal(costs, 0)

        for _ in range(edges):
            n1, n2, cost, demand = map(int, f.readline().split())
            demands[n1, n2] = demands[n2, n1] = demand
            costs[n1, n2] = costs[n2, n1] = cost

        CARP_DEMANDS, CARP_COSTS = demands, costs
        CARP_EDGE_PPR = demands / costs
        CARP_DISTANCES = floyd(costs)


def floyd(costs: np.array) -> np.array:
    n = costs.shape[0]
    distances = costs.copy()
    for k, i, j in product(range(n), range(n), range(n)):
        dis = distances[i, k] + distances[k, j]
        if distances[i, j] > dis:
            distances[i, j] = dis
    return distances


def path_scanning(rule) -> Solution:
    routes, total_cost = [], 0
    todos_symmat = CARP_DEMANDS.copy()

    while todos_symmat.any():
        route = Route()
        while candidates := route.c3_candidates(todos_symmat):
            selected: edge_t = rule(candidates, route)
            route.walk(selected)
            todos_symmat[selected] = todos_symmat[selected[::-1]] = 0
        route.cost += CARP_DISTANCES[route._pos, CARP_DEPOT]  # noqa
        routes.append(route)
        total_cost += route.cost
    return Solution(routing=routes, total_cost=total_cost)


# strategies ###########################################################################################################

def psr5__payload_dym_dist(candidates: List[edge_t], state: Route) -> edge_t:
    if state.cap_left < (CARP_CAPACITY >> 1):
        return PATH_SCANNING_RULES[0](candidates, state)
    else:
        return PATH_SCANNING_RULES[1](candidates, state)


def psr6__random(candidates: List[edge_t], state: Route) -> edge_t:
    return random.choice(candidates)


PATH_SCANNING_RULES = (
    lambda candidates, state: max(candidates, key=lambda edge: CARP_DISTANCES[CARP_DEPOT, edge[0]]),  # tang2009, psr1
    lambda candidates, state: min(candidates, key=lambda edge: CARP_DISTANCES[CARP_DEPOT, edge[0]]),  # tang2009, psr2
    lambda candidates, state: max(candidates, key=lambda edge: CARP_EDGE_PPR[edge]),  # tang2009, psr3
    lambda candidates, state: min(candidates, key=lambda edge: CARP_EDGE_PPR[edge]),  # tang2009, psr4
    psr5__payload_dym_dist,  # tang2009, psr5
)


def strategy_single_insertion__random(solution: Solution,
                                      create_new_route_proba) -> Tuple[Tuple[int, int], Tuple[int, int]]:
    # solution.routing[rid].edges[eid]
    # if choose to insert to new route, push Route() to solution and (set to_rid, to_eid) = (-1, 0)
    from_rid = random.randrange(0, len(solution.routing))
    from_eid = random.randrange(0, len(solution.routing[from_rid].edges))

    task_cost = CARP_COSTS[solution.routing[from_rid].edges[from_eid]]
    if len(solution.routing[from_rid].edges) > 1 and random.random() < create_new_route_proba:
        solution.routing.append(Route())
        return (from_rid, from_eid), (-1, 0)

    to_rid = random.randrange(0, len(solution.routing))
    while to_rid != from_rid and solution.routing[to_rid].cap_left < task_cost:
        to_rid = random.randrange(0, len(solution.routing))

    if to_rid != from_rid:
        to_eid = random.randint(0, len(solution.routing[to_rid].edges))
    elif (lr := len(solution.routing[from_rid].edges)) <= 2:
        to_eid = lr - from_eid - 1
    else:
        to_eid = random.randrange(0, len(solution.routing[to_rid].edges))
        while to_eid == from_eid:
            to_eid = random.randrange(0, len(solution.routing[to_rid].edges))
    return (from_rid, from_eid), (to_rid, to_eid)


def strategy_swap__random(solution: Solution) -> Tuple[Tuple[Route, int], Tuple[Route, int]]:
    safe_routes = list(filter(lambda rt: len(rt.edges) > 1, solution.routing))
    if len(safe_routes) < 2:
        return (None, None), (None, None)  # noqa
    r1, r2 = random.sample(safe_routes, 2)
    e1, e2 = random.randrange(0, len(r1.edges)), random.randrange(0, len(r2.edges))
    return (r1, e1), (r2, e2)


# operators ############################################################################################################

def op_pipeline_flip(solution: Solution) -> Solution:
    total_cost = 0.
    for route in solution.routing:
        total_cost += route.flip_relax()
    solution.total_cost = total_cost
    return solution


def op_single_insertion(solution: Solution,
                        sel_strategy=strategy_single_insertion__random,
                        create_new_route_proba=0.005) -> Solution:
    (from_rid, from_eid), (to_rid, to_eid) = sel_strategy(solution, create_new_route_proba)
    if from_rid == to_rid:
        solution.total_cost -= solution.routing[from_rid].cost
    else:
        solution.total_cost -= solution.routing[from_rid].cost + solution.routing[to_rid].cost
    task = solution.routing[from_rid].remove_task(from_eid)
    if not solution.routing[to_rid].insert_task(task, to_eid):
        solution.routing[from_rid].insert_task(task, from_eid)
        return solution
    if from_rid == to_rid:
        solution.total_cost += solution.routing[from_rid].cost
    else:
        solution.total_cost += solution.routing[from_rid].cost + solution.routing[to_rid].cost
    if not solution.routing[from_rid].edges:
        solution.routing.pop(from_rid)
    return solution


def op_swap(solution: Solution, sel_strategy=strategy_swap__random) -> Solution:
    (r1, e1), (r2, e2) = sel_strategy(solution)
    if r1 is None:
        return solution
    solution.total_cost -= r1.cost + r2.cost
    task1 = r1.remove_task(e1)
    task2 = r2.remove_task(e2)
    if r2.insert_task(task1, e2):
        if not r1.insert_task(task2, e1):
            r2.remove_task(e2)
            r1.insert_task(task1, e1)
            r2.insert_task(task2, e2)
    else:
        r1.insert_task(task1, e1)
        r2.insert_task(task2, e2)
    solution.total_cost += r1.cost + r2.cost
    return solution


def op_2opt_single(solution: Solution) -> Solution:
    total_cost = 0
    for route in solution.routing:
        n_edges = len(route.edges)
        for start in range(n_edges - 1):
            for end in range(start + 2, n_edges):
                if (diff := route.reverse_diff_cost(start, end)) < 0:
                    route.edges[start:end + 1] = list(map(lambda e: e[::-1], reversed(route.edges[start:end + 1])))
                    route.cost += diff
        total_cost += route.cost
    solution.total_cost = total_cost
    return solution


def op_ms(solution: Solution) -> Solution:
    if len(solution.routing) < 2:
        return solution
    r1, r2 = random.sample(solution.routing, 2)
    l1, l2 = len(r1.edges), len(r2.edges)

    b1, b2, ok = -1, -1, False
    cl1, cl2 = -1, -1
    for _ in range(l1 + l2):
        b1, b2 = random.randrange(l1), random.randrange(l2)
        d1 = sum(CARP_DEMANDS[e] for e in r1.edges[:b1])
        d2 = sum(CARP_DEMANDS[e] for e in r2.edges[b2:])
        cl1, cl2 = CARP_CAPACITY - d1 - d2, r1.cap_left + r2.cap_left + d1 + d2 - CARP_CAPACITY
        if (cl1 >= 0) and (cl2 >= 0):
            ok = True
            break
    if ok:
        solution.total_cost -= r1.cost + r2.cost
        or1l, or1r = r1.edges[:b1], r1.edges[b1:]
        or2l, or2r = r2.edges[:b2], r2.edges[b2:]
        r1.edges, r2.edges = or1l + or2r, or2l + or1r
        r1.cap_left, r2.cap_left = cl1, cl2
        r1.cost = r1.costs()
        r2.cost = r2.costs()
        solution.total_cost += r1.cost + r2.cost
    return solution


# framework ############################################################################################################

def sim_anneal(init_solution: Solution, operators, delta_e_penalty: float) -> Solution:
    last, o3 = init_solution, init_solution
    t = SA_HP_T
    stable_cnt = 0

    while time.time() < PROGRAM_DDL:
        op = random.choice(operators)
        res = op_pipeline_flip(op(last.snapshot()))
        if res < last or random.random() < math.exp((last.total_cost - res.total_cost) * delta_e_penalty / t):
            stable_cnt = 0
            last = res
            if res < o3:
                o3 = res
            elif res.total_cost > o3.total_cost * SA_RESTART_COST_RT:
                t, last = SA_HP_T, o3
        else:
            stable_cnt += 1
        if stable_cnt >= SA_STABLE_LIMIT:
            t, last = SA_HP_T, o3
            stable_cnt = 0
        t *= SA_HP_ALPHA
    return o3


def initialize_solution(force_rand):
    o2 = o3 = Solution(routing=[], total_cost=9223372036854775807)
    if not force_rand:
        for rule in PATH_SCANNING_RULES:
            if (ns := path_scanning(rule)) < o3:
                o2, o3 = o3, ns
    else:
        while time.time() < RAND_INIT_DDL:
            if (ns := path_scanning(psr6__random)) < o3:
                o2, o3 = o3, ns
    o3 = op_pipeline_flip(op_2opt_single(o3))
    return min(o3, o2)


def sa_proc(hyperparams) -> Solution:
    rand_seed, force_rand_init, op_population = hyperparams
    random.seed(rand_seed)
    parse_problem(ARGS.problem)
    init_solution = initialize_solution(force_rand_init)
    return sim_anneal(init_solution, op_population, random.randint(*SA_DELTA_E_PENALTY_RNG) * SA_DELTA_E_PENALTY_SCL)


if __name__ == '__main__':
    random.seed(ARGS.seed)
    hyperparams_procs: List[Tuple[int, bool, list]] = [
        (random.randint(1000, 2000), False, [op_single_insertion, op_swap, op_2opt_single, op_ms]),
        (random.randint(2000, 3000), False, [op_single_insertion, op_swap, op_2opt_single, op_ms]),
        (random.randint(3000, 4000), False, [op_single_insertion, op_swap, op_2opt_single, op_ms]),
        (random.randint(4000, 5000), False, [op_single_insertion, op_single_insertion, op_swap, op_2opt_single, op_ms]),
        (random.randint(5000, 6000), False, [op_single_insertion, op_swap, op_swap, op_2opt_single, op_ms]),
        (random.randint(6000, 7000), False, [op_single_insertion, op_swap, op_2opt_single, op_2opt_single, op_ms]),
        (random.randint(7000, 8000), True, [op_single_insertion, op_swap, op_2opt_single, op_ms]),
        (random.randint(8000, 9000), True, [op_single_insertion, op_swap, op_2opt_single, op_ms, op_ms]),
    ]

    with Pool(8) as mp:
        print(min(mp.map(sa_proc, hyperparams_procs)))
