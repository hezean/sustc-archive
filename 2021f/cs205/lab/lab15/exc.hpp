#include <iostream>

using namespace std;
#define ON(x) ((x == Car::On) ? "On" : "Off")

class Car {
    friend class Driver;

    enum { Off, On };
    enum { Minvel, Maxval = 200 };
    int mode;
    int velocity;

public:
    Car(int m = On, int v = 50) : mode(m), velocity(v) {}

    bool velup(int v) {
        if (velocity + v > Maxval) {
            cout << "Cannot increase vel" << endl;
            return false;
        }
        cout << "Car: Mode = " << mode << ", Velocity = " << velocity << " -> "
             << velocity + v << endl;
        velocity += v;
        return true;
    }

    bool veldown(int v) {
        if (velocity - v < Minvel) {
            cout << "Cannot decrease vel" << endl;
            return false;
        }
        cout << "Car: Mode = " << ON(mode) << ", Velocity = " << velocity << " -> "
             << velocity - v << endl;
        velocity -= v;
        return true;
    }

    bool isOn() const { return mode == On; }

    int getVel() const { return velocity; }

    void showInfo() const {
        cout << "Car: Mode = " << ON(mode) << ", Velocity = " << velocity << endl;
    }
};

class Driver {
public:
    // bool velUp(Car& c, int v) { return c.velup(v); }
    bool velUp(Car& c, int v) {
        if (c.velocity + v > c.Maxval) {
            cout << "Cannot increase vel" << endl;
            return false;
        }
        cout << "Car: Mode = " << ON(c.mode) << ", Velocity = " << c.velocity
             << " -> " << c.velocity + v << endl;
        c.velocity += v;
        return true;
    }

    bool velDown(Car& c, int v) { return c.veldown(v); }

    bool isOn(const Car& c) { return c.isOn(); }

    void setMode(Car& c) {
        cout << "Car mode: " << ON(c.mode) << " -> " << ON(!c.mode) << endl;
        c.mode = !c.mode;
    }
};
