#include <iostream>

using namespace std;

template <typename T>
T minimum(T a, T b) {
    return a < b ? a : b;
}

int main() {
    cout << minimum(1, 2) << endl
         << minimum(2.f, 1.2f) << endl
         << minimum(0.01, 2.011) << endl;
}
