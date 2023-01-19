#include <regex>

#include "fib.hpp"

using namespace std;

const regex pattern("^[1-9]+[0-9]*");

int main() {
    string num;
    int n, cnt = 0;
    int64_t* arr = new int64_t[3];
    do {
        cout << "Please input a positive integer: ";
        cin >> num;
        try {
            n = stoi(num);
        } catch (...) {
            continue;
        }
    } while (!regex_match(num, pattern));
    arr[0] = 1;
    arr[1] = 1;
    arr[2] = 2;
    if (n <= 3) {
        for (size_t i = 0; i < n; i++) printf("%lld\t", arr[i]);
        goto end;
    }

    while (n--) fib(arr, cnt);
end:
    delete[] arr;
    return 0;
}