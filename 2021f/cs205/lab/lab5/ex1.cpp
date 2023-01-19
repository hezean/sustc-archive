#include <iostream>

using namespace std;

int main() {
    auto arr = new int[5];
    cout << "Enter 5 ints below:" << endl;
    for (int i = 0; i < 5; i++) scanf("%d", arr++);
    cout << "The reversed array:" << endl;
    for (int i = 0; i < 5; i++) printf("%d ", *(--arr));
    delete[] arr;
    arr = nullptr;
    return 0;
}
