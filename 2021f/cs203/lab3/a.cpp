#include <iostream>

using namespace std;

long cnt;
int n, m;

void mer(int *arr1, int *arr2, int len1, int len2, int *tmp) {
    int li = 0, ri = 0, fi = 0;
    while (li < len1 && ri < len2)
        if (arr1[li] <= arr2[ri])
            tmp[fi++] = arr1[li++];
        else {
            tmp[fi++] = arr2[ri++];
            cnt += len1 - li;
        }
    if (li >= len1)
        for (; ri < len2; ri++) tmp[fi++] = arr2[ri];
    else if (ri >= len2)
        for (; li < len1; li++) tmp[fi++] = arr1[li];
}

int main() {
    int T;
    scanf("%d", &T);
    while (T--) {
        cnt = 0;
        scanf("%d %d", &n, &m);
        int *arr1 = new int[n];
        int *arr2 = new int[m];
        for (int i = 0; i < n; i++) scanf("%d", &arr1[i]);
        for (int i = 0; i < m; i++) scanf("%d", &arr2[i]);
        int *arr3 = new int[m + n];
        mer(arr1, arr2, n, m, arr3);
        printf("%ld\n", cnt);
        for (int i = 0; i < n + m - 1; i++) printf("%d ", arr3[i]);
        printf("%d", arr3[n + m - 1]);
        if (T) printf("\n");
        delete[] arr1;
        delete[] arr2;
        delete[] arr3;
    }
}