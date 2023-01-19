#include "util.hpp"

void check_input(std::ifstream &file1, std::ifstream &file2) {
    if (!file1.is_open() || !file2.is_open()) {
        if (!file1.good())
            std::cerr << "Error on "
                      << "Mat 1 (argv[1]) : "
                      << strerror(errno) << std::endl;
        if (!file2.good())
            std::cerr << "Error on "
                      << "Mat 2 (argv[2]) : "
                      << strerror(errno) << std::endl;
        throw std::invalid_argument("Invalid input file name");
    }
}

size_t file_rows(std::ifstream &f) {
    size_t row_cnt = 0;
    std::string tmp;
    while (getline(f, tmp, '\n')) row_cnt++;
    f.clear();
    f.seekg(0L, std::ios_base::beg);
    return row_cnt;
}

size_t file_cols(std::ifstream &f) {
    size_t col_cnt = 0;
    std::string line, tmp;
    std::getline(f, line, '\n');
    std::stringstream ssl(line);
    while (ssl >> tmp) col_cnt++;
    f.clear();
    f.seekg(0L, std::ios_base::beg);
    return col_cnt;
}
