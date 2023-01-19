#include <iostream>
using namespace std;

int main(int argc, char **argv) {
    int T;
    long long n;
    scanf("%d", &T);
    while (T--) {
        scanf("%lld", &n);
        printf("%lld\n", (n * n * n + 3 * n * n + 2 * n) / 2);
    }
    return 0;
}