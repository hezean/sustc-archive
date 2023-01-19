#include "swap.h"

Swapped swap_i(int a, int b) { return Swapped(b, a); }

void swap_p(int *a, int *b) {
    auto tmp = *a;
    *a = *b;
    *b = tmp;
}

void swap_r(int &a, int &b) {
    auto tmp = a;
    a = b;
    b = tmp;
}
