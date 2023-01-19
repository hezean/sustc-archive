#include <iostream>
#include <regex>
#include <string>

#define MAX_SIZE 100

using namespace std;

struct big_num;
int input_checker(const string &);
int input_checker_2(const string &);
big_num input_checker_b(const string &);

// an integer should at least looks like this
const regex pattern("^[+-]?[0-9]*$");

int main(int argc, char **argv) {
    int x, y;
    string temp;
    long long product;
    bool has_invalid = false;
    do {
        try {
            if (argc == 1) {  // user should input two numbers now
                cout << "Please input two integers" << endl;
                cin >> temp;
                x = input_checker(temp);
                cin >> temp;
                y = input_checker(temp);
            } else if (argc ==
                       2) {  // one number is passed by command line args,
                // another one is now to be input
                x = input_checker(string(argv[1]));
                // If x is invalid, the program will quit now,
                // otherwise it will ask the user to input another one
                cout << "You input " << x
                     << " by command line args, please input another integer"
                     << endl;
                cin >> temp;
                y = input_checker(temp);
            } else {  // there are two or more inputs passed by command line
                x = input_checker(string(argv[1]));
                y = input_checker(string(argv[2]));
                if (argc > 3) {
                    cerr << "You input more arguments than expected, the rest "
                            "ones "
                            "are ignored"
                         << endl;
                }
            }
            has_invalid = false;
        } catch (invalid_argument &) {
            cerr << "Seems like you input something other than a number\n";
            argc = 1;  // it forces the program to go into the first if block
                       // and receive two nums
            has_invalid = true;
            cin.ignore(INT_MAX, '\n');
        } catch (out_of_range &) {
            cerr << "The number you input is too large or small that exceeds "
                    "the range of int\n";
            argc = 1;
            has_invalid = true;
            cin.ignore(INT_MAX, '\n');
        }
    } while (has_invalid);

    product = static_cast<long long>(x) * y;
    cout << x << " * " << y << " = " << product;
    return 0;
}

typedef struct big_num {
private:
    int *d_;
    int len_;
    bool positive_;

public:
    big_num() {
        d_ = new int[MAX_SIZE];
        len_ = 0;
        positive_ = true;
    }

    ~big_num() {
        delete[] d_;
        d_ = nullptr;
    }

    big_num bias(int bias) {
        big_num temp;
        memset(temp.d_, 0, sizeof(int) * bias);
        memcpy(temp.d_ + bias, this->d_,
               static_cast<int>(sizeof(int)) * this->len_);
        temp.len_ = this->len_ + bias;
        temp.positive_ = this->positive_;
        return temp;
    }

    /**
     * @param str the string has passed the check by <regex pattern>
     */
    big_num &operator=(const string &str) {
        bool has_flag = str[0] == '-' || str[0] == '+';
        int str_len = has_flag ? str.length() - 1 : str.length();
        this->len_ = 0;
        this->positive_ = str[0] != '-';
        for (int i = 0; i < str.length(); i++)
            if (str[str_len - this->len_ - 1] != '0') {
                this->len_++;
                this->d_[this->len_] = str[str_len - this->len_ - 1] - '0';
            }
        return *this;
    }

    big_num operator+(const big_num &op) {
        big_num sum;
        int temp, carry = 0;
        for (int i = 0; i < this->len_ || i < op.len_; i++) {
            temp = this->d_[i] + op.d_[i] + carry;
            sum.d_[sum.len_++] = temp % 10;
            carry = temp / 10;
        }
        if (carry) sum.d_[sum.len_++] = carry;
        return sum;
    }

    big_num operator*(int64_t op) {
        big_num ans;
        ans.positive_ = this->positive_ ^ (op < 0);
        int temp, carry = 0;
        for (int i = 0; i < this->len_; i++) {
            temp = this->d_[i] * op + carry;
            ans.d_[ans.len_++] = temp % 10;
            carry = temp / 10;
        }
        while (carry) {
            ans.d_[ans.len_++] = carry % 10;
            carry = carry / 10;
        }
        return ans;
    }

    big_num operator*(big_num &op) {
        big_num prod;
        prod = "0";
        // prevent overflow
        if (this->len_ + op.len_ > MAX_SIZE) {
            delete[] prod.d_;
            prod.d_ = nullptr;
            prod.d_ = new int[2 * MAX_SIZE];
        }
        for (int i = 0; i < this->len_; i++)
            prod = prod + (op * this->d_[i]).bias(i);
        return prod;
    }

    string show() {
        string s;
        if (!this->positive_) cout << "-";
        for (int i = this->len_ - 1; i >= 0; i--)
            s.push_back(this->d_[i] + '0');
        return s;
    }
} big_num;

/**
 * Check if the input is a valid int
 *
 * @param s (maybe) int-like string
 * @return the integer notated by s
 * @throws invalid_argument if the input is not purely numeric
 * @throws out_of_range if the input exceeds the range of int
 */
int input_checker(const string &s) {
    if (!regex_match(s, pattern)) {
        throw invalid_argument("Input is not an integer");
    }
    try {
        return stoi(s);
    } catch (out_of_range &e) {
        /*
         * though stoi may mainly throws exception
         * <out_of_range> and <invalid_argument>, we have
         * used regex to make sure that any string passed to
         * this function is number-like, thus it's only possible
         * for stoi to throw <out_of_range>
         */
        throw e;
    }
}

int input_checker_2(const string &s) {
    if (!(s[0] == '+' || s[0] == '-' || (s[0] >= '0' && s[0] <= '9'))) {
        throw invalid_argument("Input is not an integer");
    }
    for (int i = 0; i < s.length(); i++) {
        if (s[0] > '9' || s[i] < '0') {
            throw invalid_argument("Input is not an integer");
        }
    }
    try {
        return stoi(s);
    } catch (out_of_range &e) {
        throw e;
    }
}

big_num input_checker_b(const string &s) {
    if (!regex_match(s, pattern))
        throw invalid_argument("Input is not an integer");
    big_num temp;
    temp = s;
    return temp;
}
