#include <iostream>

#include "swap.h"

using namespace std;

int main() {
    int a = 1, b = 2;
    Swapped tmp = swap_i(a, b);
    a = tmp.newA;
    b = tmp.newB;
    cout << a << " " << b << endl;

    int c = 3, d = 4;
    swap_p(&c, &d);
    cout << c << " " << d << endl;

    int e = 5, f = 6;
    swap_r(e, f);
    cout << e << " " << f << endl;
}
