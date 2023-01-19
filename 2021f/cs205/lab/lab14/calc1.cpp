#include <cassert>
#include <iostream>

using namespace std;

float calculateAverage(int a, int b, int c, int d) {
    assert(a >= 0 && a <= 100);
    assert(b >= 0 && b <= 100);
    assert(c >= 0 && c <= 100);
    assert(d >= 0 && d <= 100);
    return (a + b + c + d) / 4.0;
}

int main() {
    char ctl;
    do {
        int a, b, c, d;
        cout << "Enter four scores: ";
        cin >> a >> b >> c >> d;
        // try {
        float ans = calculateAverage(a, b, c, d);
        cout << "The average is " << ans << endl;
        // } catch (const OutOfRange &e) {
        //     cout << e.what() << endl;
        // }
        cout << "Continue? (y/n) ";
        cin >> ctl;
    } while (ctl == 'y' || ctl == 'Y');
}
