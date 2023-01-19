#pragma once

#include <chrono>
#include <iomanip>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <unistd.h>
#include <vector>
#include <omp.h>

class Timer {
private:
   std::chrono::time_point<std::chrono::system_clock> start_;
   constexpr static auto unit_ =
           static_cast<double>(std::chrono::microseconds::period::num)
           / std::chrono::microseconds::period::den;

public:
   Timer() { this->start_ = std::chrono::system_clock::now(); }

   ~Timer() {
       auto end = std::chrono::system_clock::now();
       std::cout << "Time spent: " << std::setiosflags(std::ios::fixed) << std::setprecision(8)
                 << static_cast<double>((end - this->start_).count()) * unit_
                 << " sec" << std::endl;
   }
};

#define TIMER Timer stopwatch;


int file_rows(std::fstream &);

int file_cols(std::fstream &);

/**
 * can be called once (and should be only once) at the start <main> function
 * - enhance iostream speed
 * - initialize seed of random number generator
 * - set number of threads
 */
void init();
