#include <math.h>

#include <iostream>
#include <vector>

using namespace std;

int n;

struct sol {
    vector<int> r, c;

    friend ostream& operator<<(ostream& os, const sol& self) {
        os << self.r.size() << " ";
        for (auto& i : self.r) os << i << " ";
        os << endl;
        os << self.c.size() << " ";
        for (auto& i : self.c) os << i << " ";
        os << endl;
        return os;
    }
};

vector<sol> res;

void fill(int st) {
    sol s1, s2;
    st >>= 1;
    int r = st + 1, c = 1;
    while (1) {
        if (r > n || c > n) break;
        for (int i = 0; i < st; i++) {
            if (r <= n) {
                s1.r.push_back(r);
                s2.c.push_back(r);
                r++;
            }
            if (c <= n) {
                s1.c.push_back(c);
                s2.r.push_back(c);
                c++;
            }
        }
        r += st;
        c += st;
    }
    res.push_back(s1);
    res.push_back(s2);
}

int main() {
    ios_base::sync_with_stdio(0), cin.tie(0), cout.tie(0);

    cin >> n;
    int lblen = ceil(log2(n));

    for (int i = 1; i <= lblen; ++i) {
        fill(1 << i);
    }

    cout << res.size() << endl;
    for (auto& s : res) {
        cout << s;
    }
}
