#include "inter.hpp"

using namespace std;

void tip() {
    int a, b;
sel:
    cout << "Please select an operation:" << endl
         << "+" << endl
         << "-" << endl
         << "*" << endl
         << "/" << endl;
    char op;
    cin >> op;
    cin.ignore(INT_MAX, '\n');

    switch (op) {
        case '+':
            get_int(a, b);
            add(a, b);
            break;
        case '-':
            get_int(a, b);
            sub(a, b);
            break;
        case '*':
            get_int(a, b);
            prod(a, b);
            break;
        case '/':
            get_int(a, b);
            dive(a, b);
            break;
        case '%':
            get_int(a, b);
            mod(a, b);
            break;

        default:
            cout << "Invalid operation" << endl;
            goto sel;
    }

    cout << "Do you want to continue? (Y/N)";
    cin >> op;
    cin.ignore(INT_MAX, '\n');
    switch (op) {
        case 'Y':
        case 'y':
            goto sel;
            break;
        case 'N':
        case 'n':
            cout << "The operation is over";
            return;
        default:
            cout << "Invalid option" << endl;
            return;
    }
}

void get_int(int& a, int& b) { cin >> a >> b; }
