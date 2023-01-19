#include <cstdint>
#include <iostream>

#define i64 int64_t
#define i32 int32_t

using namespace std;

constexpr inline i64 A(i64 x, i64 y) {
    return (x * x + 12345ll * x + y * y - 12345ll * y + x * y);
}

i32 n;
i64 m, minv, maxv;

void init_est(i32 size) {
    if (size > 6172)
        minv = A(1, 6172);
    else
        minv = A(1, size);
    maxv = max(A(size, size), A(size, 1));
}

i32 find_x(const i32 y, const i64 &tar, i32 lo, i32 hi) {
    if (hi <= lo) return lo;
    i32 mi = (lo + hi) / 2;
    i64 f = A(mi, y);
    if (f < tar)
        return find_x(y, tar, mi + 1, hi);
    else if (f > tar)
        return find_x(y, tar, lo, mi - 1);
    else
        return mi;
}

i64 calc_rk(const i64 &tar) {
    i32 x = find_x(1, tar, 1, n);
    i64 sum = 0;
    for (i32 i = 1; i <= n; i++) {
        while (!(A(x, i) <= tar && A(x + 1, i) > tar)) {
            if (x <= 0 || x > n) break;
            if (A(x, i) <= tar)
                x++;
            else
                x--;
        }
        if (x <= 0) {
            x = 1;
            continue;
        } else if (x >= n) {
            sum += n;
            x = n;
            continue;
        }
        sum += x;
    }
    return sum;
}

// returns the val that exactly ranks below the tar_rk
i64 about_val(i64 lov, i64 hiv, i64 tar_rk) {
    i64 miv;
    while (lov <= hiv) {
        miv = lov + (hiv - lov) / 2;
        i64 mi_rk = calc_rk(miv);
        if (mi_rk < tar_rk)
            lov = miv + 1;
        else
            hiv = miv - 1;
    }
    return lov;
}

int main() {
    int T;
    scanf("%d", &T);
    while (T--) {
        scanf("%d %lld", &n, &m);
        init_est(n);
        printf("%lld\n", about_val(minv, maxv, m));
    }
}
