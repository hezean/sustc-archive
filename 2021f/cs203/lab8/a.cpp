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

void swim(int* hp, int* ans, int k) {
    ans[k] = 0;
    int idx = k + 1;
    while (idx > 1 && hp[idx] > hp[idx / 2]) {
        swap(hp[idx], hp[idx / 2]);
        ans[k]++;
        idx /= 2;
    }
}

int main() {
    int n = qread();
    int* hp = new int[n + 1];
    int* ans = new int[n];
    for (int i = 0; i < n; i++) {
        hp[i + 1] = qread();
        swim(hp, ans, i);
    }
    for (int i = 0; i < n; i++) {
        qwrite(ans[i]);
        putchar(' ');
    }
}
