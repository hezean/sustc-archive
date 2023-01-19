#include "dayinfo.hpp"

bool canTravel(DayInfo day) {
    return (day.d == 0 || day.d == 6) && (day.w == 0 || day.w == 2);
}
