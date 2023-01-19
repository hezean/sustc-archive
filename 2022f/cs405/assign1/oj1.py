import itertools
import functools
import numpy as np


class PolynomialFeature(object):
    def __init__(self, degree=2):
        assert isinstance(degree, int)
        self.degree = degree

    def transform(self, x):
        if x.ndim == 1:
            x = x[:, None]
        x_t = x.transpose()
        features = [np.ones(len(x))]
        for degree in range(1, self.degree + 1):
            for items in itertools.combinations_with_replacement(x_t, degree):
                features.append(functools.reduce(lambda x, y: x * y, items))
        return np.asarray(features).transpose()


class Regression(object):
    pass


class LinearRegression(Regression):
    def fit(self, X: np.ndarray, t: np.ndarray):
        self.w = np.linalg.pinv(X) @ t
        self.var = np.mean(np.square(X @ self.w - t))

    def predict(self, X: np.ndarray, return_std: bool = False):
        y = X @ self.w
        if return_std:
            y_std = np.sqrt(self.var) + np.zeros_like(y)
            return y, y_std
        return y


def rmse(a, b):
    mse = np.mean(np.square(a - b))
    return np.sqrt(mse)


def read_input():
    n, m = map(int, input().split())
    train_x, train_y = [], []
    test_x, test_y = [], []
    for _ in range(n):
        x, y = map(float, input().split())
        train_x.append(x)
        train_y.append(y)
    for _ in range(m):
        x, y = map(float, input().split())
        test_x.append(x)
        test_y.append(y)
    return np.array(train_x), np.array(train_y), np.array(test_x), np.array(test_y)


def pred(train_x, train_y, test_x, test_y, degree):
    pf = PolynomialFeature(degree)
    train_x, test_x = pf.transform(train_x), pf.transform(test_x)

    model = LinearRegression()
    model.fit(train_x, train_y)
    pred_y, std = model.predict(test_x, return_std=True)
    return rmse(pred_y, test_y), std


if __name__ == "__main__":
    train_x, train_y, test_x, test_y = read_input()
    res = {i: pred(train_x, train_y, test_x, test_y, i) for i in range(1, 11)}
    degree = min(res, key=lambda x: res[x][0])
    print(degree)
    print(f'{res[degree][1][0]:.6f}')
