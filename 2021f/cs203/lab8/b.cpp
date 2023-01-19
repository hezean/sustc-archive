#include <iostream>

using namespace std;

inline int qread() {
    int s = 0, w = 1;
    char c = getchar();
    while (c < '0' || c > '9') {
        if (c == '-') w = -1;
        c = getchar();
    }
    while (c >= '0' && c <= '9') {
        s = s * 10 + c - '0';
        c = getchar();
    }
    return s * w;
}

inline void qwrite(int x) {
    if (x == 0) {
        putchar('0');
        return;
    }
    if (x < 0) {
        putchar('-');
        x = -x;
    }
    int s[20];
    int k = 0;
    while (x) {
        s[k++] = x % 10;
        x /= 10;
    }
    while (k--) putchar('0' + s[k]);
}

int main() {
    int n = qread();
    int* hp = new int[n + 1];
    for (int i = 0; i < n; i++) hp[i + 1] = qread();
    bool isMax = true, isMin = true;
    for (int i = 2; i <= n - 2; i += 2) {
        if (hp[i] > hp[i / 2] || hp[i + 1] > hp[i / 2]) isMax = false;
        if (hp[i] < hp[i / 2] || hp[i + 1] < hp[i / 2]) isMin = false;
        if (!isMax && !isMin) break;
    }
    if (n % 2) {
        if (hp[n - 1] > hp[(n - 1) / 2] || hp[n] > hp[(n - 1) / 2])
            isMax = false;
        if (hp[n - 1] < hp[(n - 1) / 2] || hp[n] < hp[(n - 1) / 2])
            isMin = false;
    } else {
        if (hp[n - 1] > hp[(n - 1) / 2]) isMax = false;
        if (hp[n - 1] < hp[(n - 1) / 2]) isMin = false;
    }
    if (isMax)
        printf("Max");
    else if (isMin)
        printf("Min");
    else
        printf("Neither");
}
