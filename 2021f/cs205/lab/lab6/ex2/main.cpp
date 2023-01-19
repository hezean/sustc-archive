#include <iostream>

#include "match.h"

using namespace std;

int main() {
    char str[] = {"Enjoy the holiday"};
    char tar = 'h';
    auto res = match(str, tar);
    if (res)
        cout << res;
    else
        cout << "Not Found";
}
