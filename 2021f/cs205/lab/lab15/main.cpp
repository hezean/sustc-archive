#include "exc.hpp"

int main() {
    Car c;
    c.showInfo();

    c.velup(1000);

    Driver d;
    d.setMode(c);
    d.setMode(c);
    d.setMode(c);

    d.velDown(c, 20);
}
