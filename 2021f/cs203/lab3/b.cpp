#include <iostream>

using namespace std;

int *tmp = new int[same_as_arr_len];
void mer(int *arr, int lo, int mi, int hi) {
    int i = lo, j = mi + 1, f = 0;
    while (i <= mi && j <= hi) {
        if (arr[i] < arr[j])
            tmp[f++] = arr[j++];
        else
            tmp[f++] = arr[i++];
    }
    while (i <= mi) tmp[f++] = arr[i++];
    while (j <= hi) tmp[f++] = arr[j++];
    for (int n = lo; n <= hi; n++) arr[n] = tmp[n - lo];
}

void ms(int *arr, int lo, int hi) {
    if (lo >= hi) return;
    int mi = (lo + hi) / 2;
    ms(arr, lo, mi);
    ms(arr, mi + 1, hi);
    mer(arr, lo, mi, hi);
}

int main() {
    int n;
    scanf("%d", &n);
    int *arr = new int[n];
    for (int i = 0; i < n; i++) scanf("%d", &arr[i]);
    permute(arr, n);
    qs(arr, 0, n - 1);
    if (n % 2)
        printf("%ld", 2l * arr[n / 2]);
    else
        printf("%ld", (long)arr[n / 2 - 1] + arr[n / 2]);
    delete[] arr;
}