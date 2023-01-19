#include <regex>

#include "fac.hpp"

using namespace std;

const regex pattern("^[1-9]+[0-9]*");

int main() {
    string num;
    int tar = 0;
    int64_t f = 1;
    do {
        cout << "Please input a positive integer: ";
        cin >> num;
        try {
            tar = stoi(num);
        } catch (...) {
            continue;
        }
    } while (!regex_match(num, pattern));
    if (tar > 20) {
        cerr << "frac > 20! is not supported yet" << endl;
        return -1;
    }

    for (int i = 1; i <= tar; i++) fac(f, i);
    return 0;
}
