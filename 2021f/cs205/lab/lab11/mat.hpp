#pragma once

class Mat {
    int* refcnt;
    float* data;
    int row;
    int col;

public:
    Mat(int r, int c) : row(r), col(c) {
        refcnt = new int;
        data = new float[row * col];
        *refcnt = 1;
    }

    Mat(const Mat& op)
        : row(op.row), col(op.col), data(op.data), refcnt(op.refcnt) {
        (*refcnt)++;
    }

    ~Mat() {
        if (--(*refcnt) == 0) {
            delete[] data;
            delete refcnt;
        }
    }

    Mat operator=(const Mat& op) {
        if (this != &op) {
            if (--refcnt == 0) {
                delete[] data;
                delete refcnt;
            }
            data = op.data;
            refcnt = op.refcnt;
            (*refcnt)++;
        }
        return *this;
    }
};
