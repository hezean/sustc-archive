#include <iostream>

#include "leak.hpp"
#include "matrix.hpp"

using namespace std;

int main() {
    {
        Matrix a(3, 4);
        a.fillOne();
        Matrix b(3, 4);
        b.fillOne();
        cout << "Mat a:" << endl << a;
        cout << "Mat b:" << endl << b;

        Matrix c = a + b;
        cout << "Mat c:" << endl << c;

        Matrix d = a * 4.f;
        cout << "Mat d:" << endl << d;
    }
    check_leak();
}
