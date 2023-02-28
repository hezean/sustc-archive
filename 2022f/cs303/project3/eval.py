import torch
from tqdm import tqdm

from project3 import generate_game, N_CTPS, evaluate, compute_traj, RADIUS, Agent  # noqa


N_EVALS = 500


if __name__ == "__main__":
    n_targets = 40
    agent = Agent()

    data = torch.load("data.pth")
    label = data["label"]
    feature = data["feature"]

    test = tqdm(range(N_EVALS))
    scores = []
    for game in test:
        # the class information is unavailable at test time.
        target_pos, target_features, target_cls, class_scores = generate_game(n_targets, N_CTPS, feature, label)
        ctps_inter = agent.get_action(target_pos, target_features, class_scores)
        score = evaluate(compute_traj(ctps_inter), target_pos, class_scores[target_cls], RADIUS)
        scores.append(score)
        test.set_description(f"Test: {game}, Score: {torch.stack(scores).float().mean()}")

    print(torch.stack(scores).float().mean())
