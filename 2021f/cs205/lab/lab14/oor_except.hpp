#pragma once

#include <exception>
#include <initializer_list>
#include <sstream>
#include <string>
#include <utility>

using std::initializer_list;
using std::string;
using std::stringstream;

class OutOfRange : public std::exception {
    string msg;

public:
    explicit OutOfRange(initializer_list<int> scs) {
        if (scs.size() == 0) return;
        const int* sc = scs.begin();
        stringstream ss;
        for (int i = 0; i < scs.size(); ++i) {
            if (sc[i] < 0 || sc[i] > 100)
                ss << "Para. " << i + 1 << ": " << sc[i]
                   << " out of range [0,100]\n";
        }
        msg = ss.str();
    }

    const char* what() const noexcept override { return msg.c_str(); }
};
