#include <iostream>

using namespace std;

int main(int argc, char **argv) {
    char sp = 35;
    unsigned char foo = 177;
    printf("%c", 0x1F415);
    cout << "\U+1F415";
    printf("%c\n", sp);
    printf("%c\n", foo);
    printf("%d", 'c');
    return 0;
}