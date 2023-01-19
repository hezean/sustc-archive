#include <iostream>

#include "dayinfo.hpp"

using namespace std;

int main() {
    DayInfo day;
    auto d = DayInfo::Day::Monday;
    auto w = DayInfo::Weather::Sunny;
    day.d = d;
    day.w = w;
    cout << (canTravel(day) ? "Ok to travel" : "Can't travel") << endl;
}
