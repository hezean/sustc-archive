#include <iostream>

void chk(int n, const int* a) {
    int x;
    scanf("%d", &x);
    for (size_t i = 0; i < n; i++) {
        if (a[i] == x) {
            printf("yes\n");
            return;
        }
    }
    printf("no\n");
}

int main(int argc, char **argv) {
    int n, T;
    scanf("%d", &n);
    int a[n];
    for (size_t i = 0; i < n; i++) scanf("%d", &a[i]);
    scanf("%d", &T);

    while (T--) {
        chk(n, a);
    }
    return 0;
}