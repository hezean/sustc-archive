import numpy as np


def txt2arr(path: str, delimiter: str = ' ') -> np.array:
    with open(path, 'r', encoding='utf-8') as f:
        mat = f.read()
    row_list = mat.splitlines()
    data_list = [[float(i)
                  for i in row.strip().split(delimiter)]
                 for row in row_list]
    return np.array(data_list)


def RMSE(x: np.array, y: np.array) -> float:
    return (np.sum((x - y)**2))**0.5 / len(x)


if __name__ == '__main__':
    mat1 = txt2arr('mat1.txt')
    mat2 = txt2arr('mat2.txt')
    sum = txt2arr('ans11.txt')
    print(mat1+mat2)
    print('RMSE = ', RMSE(mat1+mat2, sum))
