#include <bitset>
#include <iostream>

using namespace std;

int t, a, b;
bitset<32> binA, binB, binAns;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(0), cout.tie(0);
    cin >> t;
    while (t--) {
        cin >> a >> b;
        if (a > b) {
            cout << 0 << endl;
            continue;
        }

        binA = a;
        binB = ++b;
        binAns = 0;

        for (int i = 30; i >= 0; i--) {
            if (binA[i] && !binB[i]) break;
            if (!binA[i] && binB[i]) binAns[i] = true;
        }
        cout << binAns.to_ulong() << endl;
    }
}
