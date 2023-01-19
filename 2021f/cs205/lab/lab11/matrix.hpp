#pragma once

class Matrix {
    struct mat_elem {
        int refcnt;
        int row;
        int col;
        float* elem;
    };
    mat_elem* m_data;

public:
    Matrix(int row, int col) : m_data(nullptr) {
        assert(row > 0 && col > 0);
        try {
            m_data = new mat_elem{1, row, col, new float[row * col]};
        } catch (...) {
            try {
                delete m_data->elem;
            } catch (...) {
            }
            try {
                delete m_data;
            } catch (...) {
            }
            throw;
        }
    }

    ~Matrix() {
        try {
            if (--m_data->refcnt == 0) {
                delete[] m_data->elem;
                delete m_data;
            }
        } catch (...) {
        }
    }

    Matrix(const Matrix& op) : m_data(nullptr) {
        m_data = op.m_data;
        m_data->refcnt++;
    }

    Matrix& operator=(const Matrix& op) {
        if (this != &op) {
            if (--m_data->refcnt == 0) {
                delete m_data->elem;
                delete m_data;
            }
            m_data = op.m_data;
            m_data->refcnt++;
        }
        return *this;
    }

    Matrix operator+(const Matrix& op) {
        assert(m_data->row == op.m_data->row && m_data->col == op.m_data->col);
        Matrix ret(m_data->row, m_data->col);
        for (int i = 0; i < m_data->row * m_data->col; i++) {
            ret.m_data->elem[i] = m_data->elem[i] + op.m_data->elem[i];
        }
        return ret;
    }

    Matrix operator-(const Matrix& op) {
        assert(m_data->row == op.m_data->row && m_data->col == op.m_data->col);
        Matrix ret(m_data->row, m_data->col);
        for (int i = 0; i < m_data->row * m_data->col; i++) {
            ret.m_data->elem[i] = m_data->elem[i] - op.m_data->elem[i];
        }
        return ret;
    }
    Matrix operator*(const Matrix& op) {
        assert(m_data->col == op.m_data->row);
        Matrix ret(m_data->row, op.m_data->col);
        for (int i = 0; i < m_data->row; i++) {
            for (int j = 0; j < op.m_data->col; j++) {
                ret.m_data->elem[i * op.m_data->col + j] = 0;
                for (int k = 0; k < m_data->col; k++) {
                    ret.m_data->elem[i * op.m_data->col + j] +=
                        m_data->elem[i * m_data->col + k] *
                        op.m_data->elem[k * op.m_data->col + j];
                }
            }
        }
        return ret;
    }

    Matrix operator*(float op) {
        Matrix ret(m_data->row, m_data->col);
        for (int i = 0; i < m_data->row * m_data->col; i++) {
            ret.m_data->elem[i] = m_data->elem[i] * op;
        }
        return ret;
    }

    friend std::ostream& operator<<(std::ostream& os, const Matrix& op) {
        for (int i = 0; i < op.m_data->row; i++) {
            for (int j = 0; j < op.m_data->col; j++) {
                os << op.m_data->elem[i * op.m_data->col + j] << " ";
            }
            os << std::endl;
        }
        return os;
    }

    void fillOne() {
        for (int i = 0; i < m_data->row * m_data->col; i++) {
            m_data->elem[i] = 1;
        }
    }
};
