#include <iostream>
using namespace std;

class A {
public:
    A operator+(const A& a) const { cout << "A + A" << endl; }
};

class B {};

template <typename T>
class C {
public:
    T func(const T& a, const T& b) const { return a + b; }
};

int main() {
    A a;
    B b;
    C<A> ca;
    ca.func(a, a);
    C<B> cb;
    cb.func(b, b);
    return 0;
}
