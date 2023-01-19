#pragma GCC optimize("O3")

#include <iostream>

using namespace std;

static int_fast64_t sz;
static int tmp;
static int* arr;

inline static int read() {
    int s = 0, w = 1;
    char ch = getchar();
    while (ch < '0' || ch > '9') {
        if (ch == '-') w = -1;
        ch = getchar();
    }
    while (ch >= '0' && ch <= '9') s = s * 10 + ch - '0', ch = getchar();
    return s * w;
}

int main() {
    sz = read();
    arr = new int[sz];

#pragma unroll
    for (size_t i = 1; i < sz; ++i) {
        tmp = read() - 1;
        ++arr[tmp];
        tmp = read() - 1;
        ++arr[tmp];
    }

    int_least64_t ans = 1;
    int* ptr = arr;

    int ss = *(ptr++);
#pragma unroll
    for (int j = 1; j <= ss; ++j) {
        ans *= j;
        ans %= 998244353;
    }

#pragma unroll
    for (size_t i = 1; i < sz; ++i) {
        ss = *(ptr++) - 1;

#pragma unroll
        for (int j = 1; j <= ss; ++j) {
            ans *= j;
            ans %= 998244353;
        }
    }
    printf("%lld", ans);
}
