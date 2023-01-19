#pragma once

#include <exception>
#include <initializer_list>
#include <sstream>
#include <string>

namespace mat {

class MatException : public std::exception {
protected:
    std::string msg;

    MatException() = default;

public:
    explicit MatException(const char *m) : msg(m) {}

    [[nodiscard]] const char *what() const noexcept override {
        return msg.c_str();
    }
};

class InvalidArgs : public MatException {
public:
    explicit InvalidArgs(const char *msg) : MatException(msg) {}

    InvalidArgs(std::initializer_list<int> args);
};

class MatSizeMismatch : public MatException {
public:
    MatSizeMismatch(const char *file, int line, int r1, int c1, int ch1, int r2,
                    int c2, int ch2, char op);
};

class MatIndexOutOfBound : public MatException {
public:
    MatIndexOutOfBound(const char *file, int line, int idx);

    MatIndexOutOfBound(const char *file, int line, int idx, int size);
};

}  // namespace mat
