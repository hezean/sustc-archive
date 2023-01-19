#pragma once
#include <iomanip>
#include <iostream>
using namespace std;

class CStereoShape {
    static int numberOfObject;

public:
    virtual double getArea() const {
        cout << "CStereoShape::getArea()" << endl;
        return 0.0f;
    }
    virtual double getVolume() const {
        cout << "CStereoShape::getVolume()" << endl;
        return 0.0f;
    }
    virtual void show() const { cout << "CStereoShape::show()" << endl; }

    static int getNumOfObject() { return numberOfObject; }

    CStereoShape() { numberOfObject++; }
};

int CStereoShape::numberOfObject = 0;
