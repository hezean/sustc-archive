#include <iostream>

using namespace std;

void bs(const int* arr, int lo, int hi, int tar) {
    if (lo > hi) {
        printf("NO\n");
        return;
    }
    int mid = lo + (hi - lo) / 2;
    if (*(arr + mid) < tar)
        bs(arr, mid + 1, hi, tar);
    else if (*(arr + mid) > tar)
        bs(arr, lo, mid - 1, tar);
    else
        printf("YES\n");
}


int bss(const int* arr, int lo, int hi, int tar) {
    if (lo > hi)
        return -1;
    int mid = lo + (hi - lo) / 2;
    if (*(arr + mid) < tar)
        return mid+1;
    else if (*(arr + mid) > tar)
        return mid-1;
    else
        return ;
}

int main(int argc, char** argv) {
    int n, T, tar;
    scanf("%d", &n);
    int* arr = new int[n];
    for (int i = 0; i < n; i++) scanf("%d", (arr + i));
    scanf("%d", &T);
    while (T--) {
        scanf("%d", &tar);
        bs(arr, 0, n - 1, tar);
    }
    delete[] arr;
}