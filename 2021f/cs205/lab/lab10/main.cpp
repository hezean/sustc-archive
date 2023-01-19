#include "complex.hpp"

using namespace std;

int main() {
    Complex c1(1, 2), c2(3, 4);
    cout << c1 + c2;
    cout << c1 - c2;
    cout << c1 * c2;

    Complex c3;
    c3 = c1;
    cout << c3;
    // cout << (c1 == c3) << endl << (c1 == c1) << endl;

    cout << (c1 != c1) << endl;

    cin >> c3;
}

/**
 * Complex(int i);
 * Complex c = 1;  // implicate constructor
 *
 * Complex& operator=(const Complex& c);
 * Complex c2 = c; // assignment operator
 *
 * Complex(const Complex& c);
 * Complex c3(c);  // copy constructor
 */
