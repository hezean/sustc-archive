#include <math.h>

#include <iostream>
using namespace std;

int *arr1 = 0, *arr2 = 0, l, r, ans;

void bs(int l1, int h1, int l2, int h2, int k) {
    int len1 = h1 - l1 + 1, len2 = h2 - l2 + 1;
    if (!len1 || !len2) {
        ans = len1 ? arr1[l1 + k - 1] : arr2[l2 + k - 1];
        return;
    }
    if (k == 1) {
        ans = min(arr1[l1], arr2[l2]);
        return;
    }
    int hh1 = min(h1, l1 + k / 2-1), hh2 = min(h2, l2 + k / 2-1);  // jump to mid, trial
    if (arr1[hh1] < arr2[hh2])
        bs(hh1+1, h1, l2, h2, k - hh1+l1-1);//step,st least -1
    else
        bs(l1, h1, hh2+1, h2, k - hh2+l2-1);
}
int main() {
    int n, T;
    scanf("%d %d", &n, &T);
    arr1 = new int[n];
    arr2 = new int[n];
    for (int i = 0; i < n; i++) scanf("%d", &arr1[i]);
    for (int i = 0; i < n; i++) scanf("%d", &arr2[i]);
    while (T--) {
        scanf("%d %d", &l, &r);
        l--;
        r--;  // index
        bs(l, r, l, r, r - l + 1);
        printf("%d\n", ans);
    }
}