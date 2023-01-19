#include <math.h>

#include <iostream>

using namespace std;

struct plant {
    long hi;
    long st;
    long df;  // hi-st
};

plant *tmp = nullptr;

void mer(plant *arr, int lo, int mi, int hi) {
    int i = lo, j = mi + 1, f = 0;
    while (i <= mi && j <= hi) {
        if (arr[i].df < arr[j].df)
            tmp[f++] = arr[j++];
        else
            tmp[f++] = arr[i++];
    }
    while (i <= mi) tmp[f++] = arr[i++];
    while (j <= hi) tmp[f++] = arr[j++];
    for (int n = lo; n <= hi; n++) arr[n] = tmp[n - lo];
}

void msp(plant *arr, int lo, int hi) {
    if (lo >= hi) return;
    int mi = (lo + hi) / 2;
    msp(arr, lo, mi);
    msp(arr, mi + 1, hi);
    mer(arr, lo, mi, hi);
}

int main() {
    int n, p, q, shuBc = 0;
    scanf("%d %d %d", &n, &p, &q);
    plant *ps = new plant[n];
    tmp = new plant[n];
    for (int i = 0; i < n; i++) {
        scanf("%ld %ld", &ps[i].hi, &ps[i].st);
        ps[i].df = ps[i].hi - ps[i].st;
        if (ps[i].df > 0) shuBc++;
    }
    if (!q) {
        unsigned long long ans = 0;
        for (int i = 0; i < n; i++) ans += ps[i].st;
        printf("%llu\n", ans);
        return 0;
    }
    unsigned long long mx = 0, ans = 0;
    msp(ps, 0, n - 1);
    if (!p) {
        if (q > shuBc) {
            for (int i = 0; i < n; i++) ans += ps[i].st + max(0l, ps[i].df);
        } else {  // big df, &max del df.ndf
            for (int i = 0; i < n; i++) ans += ps[i].st;
            for (int i = 0; i < q; i++) ans += ps[i].df;
        }
        printf("%llu\n", ans);
        return 0;
    }
    if (q > shuBc) {
        for (int i = 0; i < n; i++) {
            mx = max(mx,  // origin
                     ps[i].hi * (unsigned long long)pow(2, p) -
                         (ps[i].st + max(0l, ps[i].df)));  // delta: ndf, df
            ans += ps[i].st + max(0l, ps[i].df);
        }
        ans += mx;
        printf("%llu\n", ans);
        return 0;
    } else {  // big df, &max del df.ndf
        for (int i = 0; i < q; i++)
            mx = max(mx, (unsigned long long)ps[i].hi *
                                 (unsigned long long)pow(2, p) -
                             ps[i].st - ps[i].df);
        // diff contribution
        for (int i = q; i < n; i++)
            mx = max(mx, (unsigned long long)ps[i].hi *
                                 (unsigned long long)pow(2, p) -
                             ps[i].st - ps[q - 1].df);
        // if add this, ndf=nhi-st-oldf // must 0..q-2 df & 1 ndf
        for (int i = 0; i < n; i++) ans += ps[i].st;
        for (int i = 0; i < q; i++) ans += ps[i].df;
        ans += mx;
        printf("%llu\n", ans);
        return 0;
    }
}
