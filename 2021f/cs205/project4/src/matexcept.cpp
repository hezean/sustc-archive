#include "matexcept.hpp"

mat::MatSizeMismatch::MatSizeMismatch(const char *file, int line, int r1,
                                      int c1, int ch1, int r2, int c2, int ch2,
                                      char op) {
    std::stringstream ss;
    ss << file << " : " << line << "  <<  ";
    switch (op) {
        case '+':
        case '-':
            ss << "operator" << op
               << " requires two matrices in same size: lhs ("
               << "channel=" << ch1 << "  size=[" << r1 << "*" << c1 << "])"
               << "  <->  rhs ("
               << "channel=" << ch2 << "  size=[" << r2 << "*" << c2 << "])";
            break;
        case '*':
            ss << "operator*"
               << " requires two mat1.col == mat2.row: lhs ("
               << "channel=" << ch1 << "  size=[" << r1 << "*" << c1 << "])"
               << "  <->  rhs (dim="
               << "channel=" << ch2 << "  size=[" << r2 << "*" << c2 << "])";
            break;
        default:
            ss << "undefined operator" << op;
    }
    msg = ss.str();
}

mat::InvalidArgs::InvalidArgs(std::initializer_list<int> args) {
    std::stringstream ss;
    ss << "Arguments invalid: check [";
    for (auto i : args) ss << i << ", ";
    ss << "]";
    msg = ss.str();
}

mat::MatIndexOutOfBound::MatIndexOutOfBound(const char *file, int line,
                                            int idx) {
    std::stringstream ss;
    ss << file << " : " << line << "  <<  "
       << "index below zero is invalid: " << idx;
    msg = ss.str();
}

mat::MatIndexOutOfBound::MatIndexOutOfBound(const char *file, int line, int idx,
                                            int size) {
    std::stringstream ss;
    ss << file << " : " << line << "  <<  "
       << "index " << idx << " out of bound (size=" << size << ")";
    msg = ss.str();
}
