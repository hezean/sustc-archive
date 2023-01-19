#include <iostream>

#include "oor_except.hpp"

using namespace std;

float calculateAverage(int a, int b, int c, int d) {
    if (a < 0 || b < 0 || c < 0 || d < 0 || a > 100 || b > 100 || c > 100 ||
        d > 100)
        throw OutOfRange({a, b, c, d});
    return (a + b + c + d) / 4.0;
}

int main() {
    char ctl;
    do {
        int a, b, c, d;
        cout << "Enter four scores: ";
        cin >> a >> b >> c >> d;
        try {
            float ans = calculateAverage(a, b, c, d);
            cout << "The average is " << ans << endl;
        } catch (const OutOfRange &e) {
            cout << e.what() << endl;
        }
        cout << "Continue? (y/n) ";
        cin >> ctl;
    } while (ctl == 'y' || ctl == 'Y');
}
