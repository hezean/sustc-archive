#include "ccube.hpp"
#include "csphere.hpp"
#include "cstereoshape.hpp"

int main(int argc, char const* argv[]) {
    CCube a_cube(4.0, 5.0, 6.0);
    CSphere c_sphere(7.9);
    CStereoShape* p = &a_cube;
    p->show();

    p = &c_sphere;
    p->show();

    cout << CStereoShape::getNumOfObject();
    return 0;
}
