import numpy as np

class LR:
    def __init__(self, d, lr=0.01, epochs=5000):
        self.w = np.ones(d)
        self.r = np.zeros(d)
        self.lr = lr
        self.epochs = epochs

    def train(self, X, y):
        for _ in range(self.epochs):
            y_pred = sigmoid(np.dot(X, self.w))
            err = y_pred - y
            grad = np.dot(X.T, err) / X.shape[0]  # 1?
            self.r += np.multiply(grad, grad)
            self.w -= np.multiply(self.lr / np.sqrt(1e-8 + self.r), grad)
        print(round(loss(sigmoid(np.dot(X, self.w)), y), 6))

    def predict(self, X):
        y = sigmoid(np.dot(X, self.w))
        res = np.zeros(y.shape)
        res[y >= 0.5] = 1
        return res

norm = lambda x: (x - np.mean(x)) / np.std(x)
sigmoid = lambda x: 1 / (1 + np.exp(-x))

loss = lambda y, t: -np.mean(t.T * np.log(y) + (1 - t).T * np.log(1 - y))
accuracy = lambda y, t: np.sum(y == t) / len(t)

if __name__ == '__main__':
    n, m, d = map(int, input().split())
    X_train, y_train, X_test, y_test = [], [], [], []
    for _ in range(n):
        res = list(map(float, input().split()))
        X_train.append(res[:-1])
        y_train.append(int(res[-1]))
    for _ in range(m):
        res = list(map(float, input().split()))
        X_test.append(res[:-1])
        y_test.append(int(res[-1]))

    X_train = np.array(X_train)
    X_test = np.array(X_test)

    mean, std = np.mean(X_train, axis=0), np.std(X_train, axis=0)
    std[std == 0] = 1
    X_train = (X_train - mean) / std
    X_test = (X_test - mean) / std

    y_train = np.array(y_train)
    y_test = np.array(y_test)

    lr = LR(d)
    lr.train(X_train, y_train)
    print(accuracy(lr.predict(X_test), y_test))
