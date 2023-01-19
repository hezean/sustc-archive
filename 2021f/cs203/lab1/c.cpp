#include <cmath>
#include <iostream>
#include <stack>

#define CONV(x)          \
    switch (x) {         \
        case 0:          \
            printf("2"); \
            break;       \
        case 1:          \
            printf("3"); \
            break;       \
        case 2:          \
            printf("6"); \
    }

using namespace std;

int tps[]{3,        9,        27,        81,        243,       729,     2187,
          6561,     19683,    59049,     177147,    531441,    1594323, 4782969,
          14348907, 43046721, 129140163, 387420489, 1162261467};

int main(int argc, char **argv) {
    int T, n, len = 0;
    scanf("%d", &T);
    while (T--) {
        scanf("%d", &n);
        n--;
        while ((n -= tps[len++]) >= 0)
            ;
        if (n < 0) n += tps[len - 1];

        stack<int> s;
        while (n > 0) {
            s.push(n % 3);
            n /= 3;
        }
        while ((len--) > s.size()) printf("2");
        while (!s.empty()) {
            CONV(s.top())
            s.pop();
        }
        len = 0;
        printf("\n");
    }
}