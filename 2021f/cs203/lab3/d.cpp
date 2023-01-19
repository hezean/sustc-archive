#include <iostream>

using namespace std;

void permute(int *arr, int len) {
    for (int i = len - 1; i >= 0; i--)
        swap(*(arr + i), *(arr + random() % len));
}

int partition(int *arr, int lo, int hi) {
    int i = lo, j = hi + 1;
    while (true) {
        while (arr[++i] < arr[lo])
            if (i == hi) break;
        while (arr[--j] > arr[lo])
            if (j == lo) break;
        if (i >= j) break;
        swap(*(arr + i), *(arr + j));
    }
    swap(*(arr + lo), *(arr + j));
    return j;
}

void qs(int *arr, int lo, int hi) {
    if (hi <= lo) return;
    int mi = partition(arr, lo, hi);
    qs(arr, lo, mi - 1);
    qs(arr, mi + 1, hi);
}

void chknavg(int *arr, int mid) {
    if (2 * arr[mid] == arr[mid - 1] + arr[mid + 1]) {
        int t = arr[mid + 1];
        arr[mid + 1] = arr[mid + 2];
        arr[mid + 2] = t;
    }
}

int main() {
    int n;
    scanf("%d", &n);
    int *arr = new int[n];
    for (int i = 0; i < n; i++) scanf("%d", &arr[i]);
    permute(arr, n);
    qs(arr, 0, n - 1);
    for (int i = 0; i < n / 2 - 1; i++)
        printf("%d %d ", arr[i], arr[i + n / 2]);
    printf("%d %d", arr[n / 2 - 1], arr[n - 1]);
    delete[] arr;
}