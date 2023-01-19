#pragma once
#include "cstereoshape.hpp"

class CSphere : public CStereoShape {
    double radius;

public:
    CSphere() : CStereoShape() {}
    CSphere(double rad) : CStereoShape(), radius(rad) {}
    CSphere(const CSphere& op) : CStereoShape(), radius(op.radius) {}

    double getArea() { return 4 * 3.14 * radius * radius; }
    double getVolume() { return 4 * 3.14 * radius * radius * radius / 3; }
    void Show() {
        cout << "@CSphere\nRadius=" << radius << "\tArea=" << getArea()
             << "\tVolume=" << getVolume() << endl;
    }
};
