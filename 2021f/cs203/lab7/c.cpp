#include <math.h>

#include <iostream>

int main() {
    int T;
    scanf("%d", &T);
    long long tmp;
    while (T--) {
        scanf("%lld", &tmp);
        if (tmp <= 0) {
            printf("0\n");
            continue;
        }
        printf("%lld\n", (long long)ceil(log2(tmp + 1)));
    }
}
