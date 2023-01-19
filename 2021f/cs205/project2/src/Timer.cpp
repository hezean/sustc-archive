#include "Timer.hpp"

Timer::Timer() {
    this->start_ = chrono::system_clock::now();
}

Timer::~Timer() {
    auto end = chrono::system_clock::now();
    cout << "Time spent: " << setiosflags(ios::fixed) << setprecision(8)
         << static_cast<double>((end - this->start_).count()) * unit_
         << " sec" << endl;
}
