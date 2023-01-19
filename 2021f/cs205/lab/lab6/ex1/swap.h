#pragma once

struct Swapped {
    int newA;
    int newB;
    Swapped(int a, int b) : newA(a), newB(b) {}
};

Swapped swap_i(int a, int b);
void swap_p(int *a, int *b);
void swap_r(int &a, int &b);
