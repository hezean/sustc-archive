#pragma once

#include <unistd.h>

#include <chrono>
#include <iomanip>
#include <iostream>

#define TIMER Timer stopwatch;

using namespace std;

class Timer {
private:
    chrono::time_point<chrono::system_clock> start_;
    constexpr static auto unit_ =
        static_cast<double>(chrono::microseconds::period::num) /
        chrono::microseconds::period::den;

public:
    Timer();

    ~Timer();
};
