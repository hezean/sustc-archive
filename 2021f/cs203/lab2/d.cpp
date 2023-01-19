#include <iostream>
#include <math.h>

using namespace std;

void bs(const int *arr, int lo, int hi, int tar, int &cnt, const int &st,
        const int &len) {
    if (lo > hi) return;
    int mid = lo + (hi - lo) / 2;
    if (*(arr + mid) < tar)
        bs(arr, mid + 1, hi, tar, cnt, st, len);
    else if (*(arr + mid) > tar)
        bs(arr, lo, mid - 1, tar, cnt, st, len);
    else {
        cnt++;
        int bias = -1;
        while (bias > -mid + st && *(arr + mid + bias--) == tar) cnt++;
        bias = 1;
        while (bias < len - mid && *(arr + mid + bias++) == tar) cnt++;
    }
}

int main() {
    int n, cnt = 0;
    scanf("%d", &n);
    int *arr = new int[n];
    for (int i = 0; i < n; i++) scanf("%d", &arr[i]);
    int max_p = log2(arr[n - 1]) + 1;
    for (int j = 1 + log2(arr[0]); j <= max_p; j++)
        for (int i = 0; i < n - 1; i++)
            bs(arr, i + 1, n - 1, pow(2, j) - arr[i], cnt, i, n);
    printf("%d", cnt);
    delete [] arr;
    return 0;
}
