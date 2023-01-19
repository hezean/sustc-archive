#include <iostream>
#include <vector>

using namespace std;

template <typename T>
void binsearch(vector<T> &a, int l, int r, int t) {
    if (l > r) {
        printf("no\n");
        return;
    }

    int m = (l + r) / 2;
    if (a[m] == t) {
        printf("yes\n");
        return;
    } else if (a[m] < t) {
        binsearch(a, m + 1, r, t);
    } else {
        binsearch(a, l, m - 1, t);
    }
}

template <typename T>
int partition(vector<T> &V, int lo, int hi) {
    int i = lo, j = hi + 1;
    while (true) {
        while (V[++i] < V[lo])
            if (i == hi) break;
        while (V[--j] > V[lo])
            if (j == lo) break;
        if (i >= j) break;
        swap(V[i], V[j]);
    }
    swap(V[lo], V[j]);
    return j;
}

template <typename T>
void qui_sort(vector<T> &V, int lo, int hi) {
    if (lo >= hi) return;
    int j = partition(V, lo, hi);
    qui_sort(V, lo, j - 1);
    qui_sort(V, j + 1, hi);
}

int main() {
    int n, T, tar;
    scanf("%d", &n);
    vector<int> a(n);
    for (size_t i = 0; i < n; i++) scanf("%d", &a[i]);
    qui_sort(a, 0, n - 1);
    scanf("%d", &T);
    while (T--) {
        scanf("%d", &tar);
        binsearch(a, 0, n - 1, tar);
    }
}