#include "util.hpp"

int file_rows(std::fstream &f) {
    int row_cnt = 0;
    std::string tmp;
    while (getline(f, tmp, '\n')) row_cnt++;
    f.clear();
    f.seekg(0L, std::ios_base::beg);
    return row_cnt;
}

int file_cols(std::fstream &f) {
    int col_cnt = 0;
    std::string line, tmp;
    std::getline(f, line, '\n');
    std::stringstream ssl(line);
    while (ssl >> tmp) col_cnt++;
    f.clear();
    f.seekg(0L, std::ios_base::beg);
    return col_cnt;
}

void init() {
    static auto init_once = []() -> int {
        std::ios_base::sync_with_stdio(false);
        std::cin.tie(nullptr), std::cout.tie(nullptr);
        srand(time(nullptr));
#if not defined(__DISABLE_OMP)
        omp_set_num_threads(omp_get_max_threads());
#endif
        return 0;
    }();
}
