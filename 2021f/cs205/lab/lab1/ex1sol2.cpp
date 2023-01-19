#include <iostream>

using namespace std;

int main() {
    float foo = 0.1, bar = 0.2, baz = 0.3;
    cout.precision(2);
    cout.setf(ios::showpoint);
    cout << foo << endl << bar << endl << baz;
    return 0;
}