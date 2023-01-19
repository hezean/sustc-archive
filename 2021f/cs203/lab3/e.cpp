#include <iostream>

using namespace std;

long long pay = 0;
int* hel;

void mer(int* arr, int lo, int mi, int hi) {
    int li = lo, ri = mi + 1, fi = 0;
    while (li <= mi && ri <= hi)
        if (arr[li] <= arr[ri])
            hel[fi++] = arr[li++];
        else {
            pay += (long long)(mi - li + 1) * arr[ri];
            hel[fi++] = arr[ri++];
        }
    while (li <= mi) hel[fi++] = arr[li++];
    while (ri <= hi) hel[fi++] = arr[ri++];
    for (int i = lo; i <= hi; i++) arr[i] = hel[i - lo];
}

void ms(int* arr, int lo, int hi) {
    if (lo >= hi) return;
    int mi = (lo + hi) / 2;
    ms(arr, lo, mi);
    ms(arr, mi + 1, hi);
    mer(arr, lo, mi, hi);
}

int main() {
    int n;
    scanf("%d", &n);
    int* arr = new int[n];
    hel = new int[n];
    for (int i = 0; i < n; i++) scanf("%d", &arr[i]);
    ms(arr, 0, n - 1);
    printf("%lld", pay);
    delete[] arr;
    delete[] hel;
}
