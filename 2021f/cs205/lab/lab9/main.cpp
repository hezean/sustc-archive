#include "complex.hpp"

using namespace std;

int main() {
    Complex c1(1, 2);
    Complex c2(3, 4);
    // (c1 + c2).show();
    // (c1 - c2).show();
    c1.show();
    cout << "-";
    c2.show();
    cout << "=";
    (c1 - c2).show();
    // std::cout << c1.i;
    // c1 += Complex(4, 5);
    // c1.show();
}
