#include <iostream>

using namespace std;

class A {
public:
    virtual void func() = 0;
};

class AImpl : public A {
public:
    void func() override { cout << "AImpl.func()" << endl; }
};

class APImpl : private A {
public:
    void func() override { cout << "APImpl.func()" << endl; }
};

int main() {
    A* a = new AImpl();
    // A* ap = new APImpl();
    // conversion to inaccessible base class "A" is not allowed
    a->func();
    APImpl ap;
    ap.func();
}
