#include <iostream>

#include "point.h"

using namespace std;

int main() {
    point p1(1, 2);
    point p2, p3;

    setPtrs(p1, p2, p3);
    cout << "P2(" << p2.x << "," << p2.y << ")" << endl;
    cout << "P3(" << p3.x << "," << p3.y << ")" << endl;

    point p4(1, 2), p5(0, 0);
    auto mid = middle(&p4, &p5);
    cout << "Mid(" << mid.x << "," << mid.y << ")" << endl;
}
