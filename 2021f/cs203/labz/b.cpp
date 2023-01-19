#include <math.h>

#include <iostream>

using namespace std;

#define TBSM 998244353
typedef uint64_t ll;

static ll* res;
static ll** combres;

ll comb(int n, int m) {
    if (m <= 0 || n <= 0) return 0;
    if (m == n) return 1;
    if (m == 1) return n;
    if (combres[n][m]) return combres[n][m];

    combres[n][m] = (comb(n - 1, m - 1) % TBSM + comb(n - 1, m) % TBSM) % TBSM;
    return combres[n][m];
}

inline int ls(int tot) {
    if (tot == 1) return 0;
    int ltop = pow(2, static_cast<int>(log2(tot)) - 1);
    if (3 * ltop > tot + 1)
        return tot - ltop;
    else
        return 2 * ltop - 1;
}

ll cntMinPQ(int sz) {
    if (sz <= 1) return 1;
    if (res[sz]) return res[sz];
    int left = ls(sz);
    res[sz] = ((comb(sz - 1, left) * cntMinPQ(left)) % TBSM) *
              cntMinPQ(sz - left - 1) % TBSM;
    return res[sz];
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(0), cout.tie(0);
    int n;
    cin >> n;

    res = new ll[n];
    combres = new ll*[n + 1];
    for (int i = 1; i <= n; ++i) {
        combres[i] = new ll[i];
    }

    cout << cntMinPQ(n) << endl;
}
