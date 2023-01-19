#include "mat.hpp"

int main() {
    Mat m1 = Mat(100, 100, "mat1.in");
    Mat m2 = Mat(100, 100, "mat2.in");
    // m1.addBy(m2);
    m1.addAvx512(m2);
}
