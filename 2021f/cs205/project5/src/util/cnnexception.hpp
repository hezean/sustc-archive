#pragma once

#include <exception>
#include <initializer_list>
#include <sstream>
#include <string>
#include <utility>

using std::initializer_list;
using std::string;
using std::stringstream;

class CnnException : public std::exception {
protected:
    string msg;

public:
    CnnException() = default;
    explicit CnnException(const string &msg) : msg(msg) {}

    [[nodiscard]] const char *what() const noexcept override {
        try {
            return msg.c_str();
        } catch (...) {
            return nullptr;
        }
    }
};

class InvalidArgs : public CnnException {
    template <typename T, typename... Ts>
    void msgBuilder(stringstream &ss, T arg0, Ts... args) {
        ss << arg0 << ", ";
        msgBuilder(ss, args...);
    }
    void msgBuilder(stringstream &ss) { this->msg = ss.str(); }

public:
    InvalidArgs() = default;
    explicit InvalidArgs(const string &msg) : CnnException(msg) {}

    template <typename T, typename... Ts>
    InvalidArgs(const string &prompt, T arg0, Ts... args) {
        stringstream ss;
        ss << prompt << ": ";
        msgBuilder(ss, arg0, args...);
    }
};

class Fatal : public CnnException {
public:
    explicit Fatal(const string &msg) : CnnException(msg) {}
};
