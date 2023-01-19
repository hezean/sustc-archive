#include <math.h>

#include <iostream>

using namespace std;

void vabs(int* p, int n) {
    for (int i = 0; i < n; i++) {
        p[i] = abs(p[i]);
        cout << p[i] << " ";
    }
    cout << endl;
}
void vabs(float* p, int n) {
    for (int i = 0; i < n; i++) {
        p[i] = abs(p[i]);
        cout << p[i] << " ";
    }
    cout << endl;
}
void vabs(double* p, int n) {
    for (int i = 0; i < n; i++) {
        p[i] = abs(p[i]);
        cout << p[i] << " ";
    }
    cout << endl;
}

// void vabs(int* p, size_t n) {
//     for (size_t i = 0; i < n; i++) {
//         p[i] = abs(p[i]);
//         cout << p[i] << " ";
//     }
//     cout << endl;
// }

int main() {
    int ia[] = {-1, 2, -3, -4, -5};
    float fa[] = {-1.1f, 2.2f, -3.f, 4.4f, -5.5f};
    double da[] = {-1.11, 2.22, -3.33, -4.44, -5.55};
    vabs(ia, 5);
    vabs(fa, 5);
    vabs(da, 5);
}
