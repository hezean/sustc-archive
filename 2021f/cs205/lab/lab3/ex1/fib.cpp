#include "fib.hpp"

void fib(int64_t* const arr, int& cnt) {
    printf("%lld\t", arr[0]);
    if (!(++cnt % 10)) printf("\n");
    arr[0] = arr[1];
    arr[1] = arr[2];
    arr[2] = arr[0] + arr[1];
}

void print_left(int64_t* const arr, int& cnt) {
    if (!(cnt++ % 10)) printf("\n");
    printf("%lld\t", arr[1]);
    if (!(cnt % 10)) printf("\n");
    printf("%lld\t", arr[2]);
}