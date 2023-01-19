#pragma once
#include "cstereoshape.hpp"

class CCube : public CStereoShape {
    double length, width, height;

public:
    CCube() : CStereoShape(), length(0), width(0), height(0) {}
    CCube(float length, float width, float height)
        : CStereoShape(), length(length), width(width), height(height) {}

    CCube(const CCube& other)
        : CStereoShape(),
          length(other.length),
          width(other.width),
          height(other.height) {}

    double getArea() const override { return length * width; }
    double getVolume() const override { return getArea() * height; }
    void show() const override {
        cout << "@CCube\nLength" << setw(2) << length << "\tWidth: " << setw(2)
             << width << "\tHeight" << setw(2) << height << endl
             << "Area: " << setw(2) << getArea() << "\tVolume: " << setw(2)
             << getVolume() << endl;
    }
};
