#include <iostream>
using namespace std;

struct MinPQ {
    int* pq;
    int n;

    explicit MinPQ(int sz) : pq(new int[sz + 1]), n(0) {}

    inline void swim(int k) {
        while (k > 1 && pq[k] < pq[k / 2]) {
            swap(pq[k], pq[k / 2]);
            k /= 2;
        }
    }

    inline void sink(int k) {
        while (2 * k <= n) {
            int j = 2 * k;
            if (j < n && pq[j] > pq[j + 1]) j++;
            if (pq[k] <= pq[j]) break;
            swap(pq[k], pq[j]);
            k = j;
        }
    }

    inline void push(int x) {
        pq[++n] = x;
        swim(n);
    }

    inline int pop() {
        int x = pq[1];
        swap(pq[1], pq[n--]);
        sink(1);
        return x;
    }

    inline int top() const { return pq[1]; }

    inline int size() const { return n; }
};

struct MaxPQ {
    int* pq;
    int n;

    explicit MaxPQ(int sz) : pq(new int[sz + 1]), n(0) {}

    inline void swim(int k) {
        while (k > 1 && pq[k] > pq[k / 2]) {
            swap(pq[k], pq[k / 2]);
            k /= 2;
        }
    }

    inline void sink(int k) {
        while (2 * k <= n) {
            int j = 2 * k;
            if (j < n && pq[j] < pq[j + 1]) j++;
            if (pq[k] >= pq[j]) break;
            swap(pq[k], pq[j]);
            k = j;
        }
    }

    inline void push(int x) {
        pq[++n] = x;
        swim(n);
    }

    inline int pop() {
        int x = pq[1];
        swap(pq[1], pq[n--]);
        sink(1);
        return x;
    }

    inline int top() const { return pq[1]; }

    inline int size() const { return n; }
};

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr), cout.tie(nullptr);
    int n;
    cin >> n;
    int* arr = new int[n];
    for (int i = 0; i < n; i++) cin >> arr[i];
    MinPQ bg(n);
    MaxPQ sm(n);
    cout << arr[0] << " ";
    if (n == 1) return 0;

    sm.push(min(arr[0], arr[1]));
    bg.push(max(arr[0], arr[1]));
    cout << sm.top() << " ";

    for (int i = 2; i < n; i++) {
        if (arr[i] < sm.top())
            sm.push(arr[i]);
        else
            bg.push(arr[i]);
        while (bg.size() > sm.size()) sm.push(bg.pop());
        while (sm.size() > bg.size() + 1) bg.push(sm.pop());
        cout << sm.top() << " ";
    }
}
