#pragma once

#include <assert.h>
#include <immintrin.h>
#include <iostream>
#include <fstream>
#include <random>

#include "Timer.hpp"

using namespace std;

class Mat {
public:
    int row, col;
    float* data;
    Mat(int r, int c, const char* filename);
    ~Mat() { delete[] data; }
    void save(const char* filename);
    void rand();

    float& operator()(int r, int c) { return data[r * col + c]; }
    void addBy( Mat& by);
    void addAvx512(const Mat& by);
};
