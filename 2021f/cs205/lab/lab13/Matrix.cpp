#include <iostream>

using namespace std;

template <class T>
class Matrix {
    size_t row;
    size_t col;
    T* data;

public:
    Matrix(size_t row, size_t col) : row(row), col(col), data(nullptr) {
        try {
            data = new T[row * col];
        } catch (...) {
            delete[] data;
            throw;
        }
    }

    ~Matrix() {
        delete[] data;
        data = nullptr;
    }

    Matrix operator+(const Matrix& op) const {
        if (row != op.row || col != op.col) {
            throw "Matrix size mismatch";
        }
        Matrix res(row, col);
        for (size_t i = 0; i < row * col; ++i) {
            res.data[i] = data[i] + op.data[i];
        }
        return res;
    }

    Matrix& operator+=(const Matrix& op) {
        if (row != op.row || col != op.col) {
            throw "Matrix size mismatch";
        }
        for (size_t i = 0; i < row * col; ++i) {
            data[i] += op.data[i];
        }
        return *this;
    }

    Matrix operator*(const Matrix& op) const {
        if (col != row) {
            throw "Matrix size mismatch";
        }
        Matrix res(row, op.col);
        for (size_t i = 0; i < row; ++i) {
            for (size_t j = 0; j < op.col; ++j) {
                res.data[i * op.col + j] = static_cast<T>(0);  // FIXME: right?
                for (size_t k = 0; k < col; ++k) {
                    res.data[i * op.col + j] +=
                        data[i * col + k] * op.data[k * op.col + j];
                }
            }
        }
        return res;
    }

    Matrix& operator*=(const Matrix& op) {
        if (col != row) {
            throw "Matrix size mismatch";
        }
        Matrix res(row, op.col);
        for (size_t i = 0; i < row; ++i) {
            for (size_t j = 0; j < op.col; ++j) {
                res.data[i * op.col + j] = static_cast<T>(0);  // FIXME: right?
                for (size_t k = 0; k < col; ++k) {
                    res.data[i * op.col + j] +=
                        data[i * col + k] * op.data[k * op.col + j];
                }
            }
        }
        return *this = res;
    }

    T& operator()(int row, int column) {
        if (row < 0 || row >= this->row || column < 0 || column >= this->col) {
            throw "Index out of range";
        }
        return data[row * col + column];
    }

    T operator()(int row, int column) const {
        if (row < 0 || row >= this->row || column < 0 || column >= this->col) {
            throw "Index out of range";
        }
        return data[row * col + column];
    }

    friend ostream& operator<<(ostream& os, const Matrix& m) {
        for (size_t i = 0; i < m.row; ++i) {
            for (size_t j = 0; j < m.col; ++j) {
                os << m.data[i * m.col + j] << " ";
            }
            os << endl;
        }
        return os;
    }

    void print() const { cout << (*this); }
};

int main() {
    Matrix<int> m1(2, 3);
    m1(0, 0) = 1;
    m1(0, 1) = 2;
    Matrix<int> m2(2, 3);
    m2(0, 0) = 1;
    m2(0, 1) = 2;
    m1 += m2;
    (m1).print();
}
