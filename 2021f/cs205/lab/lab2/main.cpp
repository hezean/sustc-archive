#include <iostream>

#include "mul.hpp"

using namespace std;

int main() {
    int a, b;
    int64_t result;
    cout << "Pick two integers:";
    cin >> a;
    cin >> b;  // * FIXED
    result = mul(a, b);
    // cout << "The result is " << result << endl;
    printf("%d * %d = %lld", a, b, result);
    return 0;
}
