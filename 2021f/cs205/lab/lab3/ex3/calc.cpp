#include "calc.hpp"

void add(int a, int b) {
    std::cout << a << " + " << b << " = " << (long)a + b << std::endl;
}

void sub(int a, int b) {
    std::cout << a << " - " << b << " = " << (long)a - b << std::endl;
}

void prod(int a, int b) {
    std::cout << a << " * " << b << " = " << (long long)a * b << std::endl;
}

void dive(int a, int b) {
    while (!b) {
        std::cout << "The divident cannot be zero, please try again."
                  << std::endl;
        std::cin >> b;
    }
    std::cout << a << " / " << b << " = " << a / b << std::endl;
}

void mod(int a, int b) {
    while (!b) {
        std::cout << "The divident cannot be zero, please try again."
                  << std::endl;
        std::cin >> b;
    }
    std::cout << a << " % " << b << " = " << a % b << std::endl;
}
