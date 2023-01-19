#include <iostream>

int main() {
    int cnt;
    scanf("%d", &cnt);
    int* lk = new int[cnt];
    int par, son;

    for (int i = 0; i < cnt - 1; i++) {
        scanf("%d %d", &par, &son);
        lk[par - 1]++;
        lk[son - 1]++;
    }

    for (int i = 0; i < cnt; i++)
        if (lk[i] == 1) printf("%d ", i + 1);
}
