#include <iostream>
//#include <opencv2/opencv.hpp>
#define __DISABLE_OMP
//#define __DISABLE_SIMD
#define NDEBUG

//#include "debug_new.hpp"
#include "util.hpp"
#include "matrix.hpp"

using namespace std;
using namespace mat;

int main() {
    init();
    try {
        MatF32 m1(2048,2048);
        MatF32 m2(2048,2048);
        MatF32 m3 = m1 * m2;

//        MatUC8 m4(2048,2048);
//        MatUC8 m5(2048,2048);
//        MatUC8 m6 = m4 * m5;
    } catch (exception &e) { cout << e.what() << endl; }
}
