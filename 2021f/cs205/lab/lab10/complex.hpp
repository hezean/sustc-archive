#pragma once

#include <iostream>

class Complex {
private:
    double real;
    double imag;

public:
    Complex() : real(0), imag(0) {}
    Complex(double r, double i) : real(r), imag(i) {}
    Complex(const Complex& c) : real(c.real), imag(c.imag) {}

    double getReal() const { return real; }
    double getImag() const { return imag; }
    void setReal(double r) { real = r; }
    void setImag(double i) { imag = i; }

    Complex& operator=(const Complex& c) {
        real = c.real;
        imag = c.imag;
        return *this;
    }
    Complex operator+(const Complex& c) const {
        return Complex(real + c.real, imag + c.imag);
    }
    Complex operator-(const Complex& c) const {
        return Complex(real - c.real, imag - c.imag);
    }
    Complex operator*(const Complex& c) const {
        return Complex(real * c.real - imag * c.imag,
                       real * c.imag + imag * c.real);
    }
    bool operator==(const Complex& c) const {
        return real == c.real && imag == c.imag;
    }
    friend std::ostream& operator<<(std::ostream& os, const Complex& c) {
        os << "Comp(" << c.real << (c.imag < 0 ? " - " : " + ") << abs(c.imag)
           << "i)" << std::endl;
        return os;
    }
    friend std::istream& operator>>(std::istream& is, Complex& c) {
        is >> c.real >> c.imag;
        std::cout << std::endl << c;
        return is;
    }
};
