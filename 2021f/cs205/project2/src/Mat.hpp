#pragma once

#include <cblas.h>
#include <immintrin.h>
#include <iostream>
#include <fstream>
#include <random>
#include <sstream>
#include <string>
#include <thread>

#include "Timer.hpp"
#include "ThreadPool.hpp"

#define THREADS thread::hardware_concurrency()

template<typename T>
class Mat {
private:
    size_t col_{};
    size_t row_{};
    T **data_;

    void show_ln(size_t);
    static Mat<T> strassen(const Mat<T> &, const Mat<T> &);

public:
    [[nodiscard]] size_t getCol() const;
    [[nodiscard]] size_t getRow() const;
    T **getData() const;

    Mat(size_t, size_t);
    Mat(size_t, size_t, ifstream &);
    ~Mat();

    Mat<T> operator+(const Mat<T> &);
    Mat<T> operator-(const Mat<T> &);

    static Mat<T> sub_mat(const Mat<T> &, size_t, size_t, size_t, size_t);
    static Mat<T> merge(Mat<T> &, Mat<T> &, Mat<T> &, Mat<T> &);

    static Mat<T> dot_n3(const Mat<T> &, const Mat<T> &);
    static Mat<T> dot_strassen(const Mat<T> &, const Mat<T> &);
    static Mat<T> dot_change_ord(const Mat<T> &, const Mat<T> &);
    static Mat<T> _mm_dot_float(const Mat<T> &, const Mat<T> &);
    static Mat<T> dot_mul_threads(const Mat<T> &, const Mat<T> &);
    static Mat<T> dot_blas(const Mat<T> &, const Mat<T> &);

    static void rand(size_t, size_t, const string &);

    void show();
    void save(const string &);
};

#include "Mat.tpp"
