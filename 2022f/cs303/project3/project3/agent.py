import time
from typing import Tuple

import torch
import torch.nn as nn
from functorch import vmap

from project3.src import RADIUS, N_CTPS, compute_traj


class MLP(nn.Module):
    def __init__(self):
        super(MLP, self).__init__()
        self.flt = nn.Flatten()
        self.relu = nn.ReLU()
        self.lin1 = nn.Linear(256, 128)
        self.lin2 = nn.Linear(128, 64)
        self.lin3 = nn.Linear(64, 10)

    def forward(self, x):
        return self.lin3(
            self.relu(self.lin2(
                self.relu(self.lin1(
                    self.flt(x)
                ))
            ))
        )


class Agent:
    def __init__(self) -> None:
        self.cls = torch.load("project3/model.pth")
        self.random_start = 25
        self.lr = 0.6
        self.softmax = nn.Softmax(1)
        self.target_pos = None
        self.target_scores = None
        self.radius = RADIUS

    def evaluate(self, ctps_inter) -> torch.Tensor:
        traj = compute_traj(ctps_inter)
        cdist = torch.cdist(self.target_pos, traj)
        d = cdist.min(-1).values
        hit = (d < self.radius)
        return torch.sum(hit * self.target_scores, dim=-1)

    def get_action(
            self,
            target_pos: torch.Tensor,
            target_features: torch.Tensor,
            class_scores: torch.Tensor,
    ) -> Tuple[torch.Tensor, torch.Tensor]:
        ddl = time.time() + 0.29
        self.target_pos = target_pos
        pred = self.softmax(self.cls(target_features)).argmax(dim=1)
        self.target_scores = class_scores[pred]
        max_score, max_sol = -100000000, None

        while time.time() < ddl:
            inters = torch.rand((1000, N_CTPS - 2, 2)) * torch.tensor([6, 8.]) + torch.tensor([-1., -5.])
            scores = vmap(self.evaluate)(inters)
            sc, idx = scores.max(dim=0)
            if sc > max_score:
                max_score, max_sol = sc, inters[idx]

        return max_sol
