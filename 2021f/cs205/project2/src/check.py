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
    mat1 = txt2arr('mat-A-2048.txt')
    mat2 = txt2arr('mat-B-2048.txt')
    my_prod = txt2arr('out2048.txt')
    print(np.dot(mat1, mat2))  # to check if the answer is generally correct
    print('RMSE = ', RMSE(np.dot(mat1, mat2), my_prod))
