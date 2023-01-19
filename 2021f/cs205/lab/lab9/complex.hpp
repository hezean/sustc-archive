#pragma once
#include <cmath>
#include <iostream>

class Complex {
    // friend int main();

private:
    double i, r;

public:
    Complex() : i(0), r(0) {}
    Complex(double r, double i) : i(i), r(r) {}

    Complex operator+(const Complex& op) const {
        return Complex(this->r + op.r, this->i + op.i);
    }

    Complex operator-(const Complex& op) const {
        return Complex(this->r - op.r, this->i - op.i);
    }

    const Complex& operator+=(const Complex& op) {
        this->r += op.r;
        this->i += op.i;
        return *this;
    }

    void show() {
        std::cout << "Complex(" << r << (i > 0 ? " + " : " - ") << abs(i)
                  << "i)\n";
    }
};
