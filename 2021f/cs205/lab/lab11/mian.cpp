#include <iostream>

#include "leak.hpp"
#include "mat.hpp"

using namespace std;

int main() {
    {
        Mat a(3, 4);
        Mat b(3, 4);
        // cout << "Mat a:" << endl << a;
        // cout << "Mat b:" << endl << b;
        Mat c = a;

        // Matrix c = a + b;
        // cout << "Mat c:" << endl << c;

        // Matrix d = a * 4.f;
        // cout << "Mat d:" << endl << d;
    }
    check_leak();
}
