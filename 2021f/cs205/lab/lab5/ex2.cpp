#include <iostream>

using namespace std;

int main() {
    auto str = new char[6];
    str[0] = 65;
    str[1] = 112;
    str[2] = 80;
    str[3] = 108;
    str[4] = 101;
    str[5] = 0;
    printf("%s", str);

    str[11] = 0;
    str[932] = 0;
    str[912332] = 0;
    str[92] = 0;
}
