#include "mat.hpp"

Mat::Mat(int r, int c, const char *filename) {
    this->row = r;
    this->col = c;
    this->data = new float[r * c];
    fstream f(filename, ios::in);
    assert(f.good());
    for (int i = 0; i < r * c; i++) f >> data[i];
    f.close();
}

void Mat::save(const char *filename) {
    int col = this->col;
    fstream f(filename, ios::out);
    for (int i = 0; i < this->row; i++) {
        for (int j = 0; j < this->col; j++) {
            f << data[i * col + j] << " ";
        }
        f.close();
    }
}

void Mat::rand() {
    for (int i = 0; i < this->row * this->col; i++) {
        data[i] = static_cast<float>(random() % 10000) / 100;
    }
}

void Mat::addBy(Mat &by) {
    TIMER
    assert(this->row == by.row && this->col == by.col);
    for (int i = 0; i < this->row; i++)
        for (int j = 0; j < this->col; j++) (*this)(i, j) += by(i, j);
    //    for (int i = 0; i < this->row * this->col; i++) this->data[i] +=
    //    by.data[i];
}

void Mat::addAvx512(const Mat &by) {
    TIMER
    int cnt = this->col * this->row;
    for (int i = 0; i < cnt; i += 16)
        _mm512_store_ps(this->data + i,
                        _mm512_add_ps(_mm512_load_ps(this->data + i),
                                      _mm512_load_ps(by.data + i)));
    for (int j = cnt - cnt % 16; j < cnt; j++) this->data[j] += by.data[j];
}
