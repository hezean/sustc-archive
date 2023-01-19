#include "fac.hpp"

void fac(int64_t& fac, int now) {
    fac *= now;
    printf("%d! = %lld\n", now, fac);
}
