#include <math.h>
#include <iostream>
#define LIM 0.01

using namespace std;

double f(double x, int b) { return x * exp(x / 20) - b; }

double fprime(double x, int b) { return exp(x / 20) * (x / 20 + 1); }

void newton(double x, int b) {
    if (f(x, b) < LIM && f(x, b) > -LIM) {
        printf("%.10f\n", x);
        return;
    }
    newton(x - f(x, b) / fprime(x, b), b);
}

int main() {
    int edge[]{0, 7, 26, 57, 93, 132, 173, 214, 257};
    int T, b;
    scanf("%d", &T);
    while (T--) {
        scanf("%d", &b);
        newton(edge[(int)log10(b)], b);
    }
}
