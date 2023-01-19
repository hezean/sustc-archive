from collections import Counter
from itertools import product
import numpy as np
import math

# from sklearn.metrics import accuracy_score


def distanceFunc(metric_type, vec1, vec2):
    if metric_type == "L1":
        distance = np.sum(np.abs(vec1 - vec2))

    if metric_type == "L2":
        distance = np.sqrt(np.sum(np.square(vec1 - vec2)))

    if metric_type == "L-inf":
        distance = np.max(np.abs(vec1 - vec2))

    return distance


def computeDistancesNeighbors(K, metric_type, X_train, y_train, sample):
    dist = [distanceFunc(metric_type, sample, X_train[i]) for i in range(len(X_train))]
    return [y_train[idx] for idx in np.argsort(dist, kind='stable')[:K]]


def Majority(neighbors):
    cnt = Counter(neighbors)
    common = cnt.most_common(1)[0][1]
    res = [k for k, v in cnt.items() if v == common]
    return min(res)


def KNN(K, metric_type, X_train, y_train, X_val):
    return np.array([Majority(computeDistancesNeighbors(K, metric_type, X_train, y_train, sample)) for sample in X_val])

def evaluation(predicted_values, actual_values):
    # return accuracy_score(predicted_values, actual_values)
    return len(list(filter(lambda x: x, predicted_values == actual_values))) / len(predicted_values)


def read():
    n, m, d = map(int, input().split())
    X_train, y_train, X_val, y_val = [], [], [], []
    for _ in range(n):
        res = list(map(float, input().split()))
        X_train.append(res[:-1])
        y_train.append(int(res[-1]))
    for _ in range(m):
        res = list(map(float, input().split()))
        X_val.append(res[:-1])
        y_val.append(int(res[-1]))
    return np.array(X_train), np.array(y_train), np.array(X_val), np.array(y_val)


if __name__ == '__main__':
    X_train, y_train, X_val, y_val = read()

    K = [1, 2, 3, 4, 5]
    norm = ["L1", "L2", "L-inf"]

    best_acc, best_hp = -math.inf, []

    for k, n in product(K, norm):
        acc = evaluation(KNN(k, n, X_train, y_train, X_val), y_val)
        if acc > best_acc:
            best_acc = acc
            best_hp.clear()
        if acc == best_acc:
            best_hp.append((k, n))

    for k, n in best_hp:
        print(k, n)
