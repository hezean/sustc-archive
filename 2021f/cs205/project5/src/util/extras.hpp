#pragma once

#include <chrono>
#include <iomanip>
#include <iostream>
#include <vector>

using std::cout;
using std::endl;

std::chrono::time_point<std::chrono::steady_clock,
        std::chrono::steady_clock::duration> start;

auto unit_ = static_cast<double>(std::chrono::microseconds::period::num) /
             std::chrono::microseconds::period::den;

#define TIME_START start = std::chrono::steady_clock::now();
#define TIME_END(NAME) cout << (NAME) << ": " << std::setiosflags(std::ios::fixed)                     \
                            << std::setprecision(6)                                                    \
                            << static_cast<float>((std::chrono::steady_clock::now() - start).count())  \
                               * unit_ << " ms" << endl;

#if defined(DEBUG)

struct new_ptr_info {
    static std::vector<new_ptr_info> new_ptr_pool;
    std::string call_file;
    int call_line;
    void *where;
    size_t size;
};

std::vector<new_ptr_info> new_ptr_info::new_ptr_pool;

void *operator new(size_t size, const char *file, int line) {
    void *ptr = malloc(size);
    new_ptr_info::new_ptr_pool.push_back(new_ptr_info{file, line, ptr, size});
    std::cerr << "(" << file << ": " << line << ") Allocate " << size
    << " byte(s)" << std::endl;
    return ptr;
}

inline void *operator new[](size_t size, const char *file, int line) {
    return operator new(size, file, line);
}

void operator delete(void *ptr, const char *file, int line) {
    std::cerr << "(" << file << ": " << line
    << ") Constructor failed, deallocating by C++ Runtime System."
    << std::endl;
    erase_if(new_ptr_info::new_ptr_pool,
             [ptr](new_ptr_info &p) { return p.where == ptr; });
    free(ptr);
}

inline void operator delete[](void *ptr, const char *file, int line) {
    operator delete(ptr, file, line);
}

void operator delete(void *ptr) {
    if (ptr == nullptr) {
        return;
    }
    erase_if(new_ptr_info::new_ptr_pool,
             [ptr](new_ptr_info &p) { return p.where == ptr; });
    free(ptr);
}

void operator delete[](void *ptr) { operator delete(ptr); }

void check_leak() {
    std::cerr << std::endl
    << std::endl
    << "****** MEMORY REPORT *****" << std::endl;
    if (!new_ptr_info::new_ptr_pool.empty()) {
        int leak_cnt = 0;
        size_t leak_spc = 0;
        for (auto &p : new_ptr_info::new_ptr_pool) {
            ++leak_cnt;
            leak_spc += p.size;
            std::cerr << "Leaked obj at " << p.where << " (" << p.call_file
            << ": " << p.call_line << ")" << std::endl;
        }
        std::cerr << std::endl
        << "*** " << leak_cnt << " leak(s) found, size = " << leak_spc
        << std::endl;
    } else {
        std::cerr << "No leak detected, well done!" << std::endl;
    }
}

#define new new (__FILE__, __LINE__)

#else

void check_leak() {
    cout << "Didn't record memory usages.";
}

#endif  // DEBUG

#ifdef __AVX512F__
void matmulABT(const float *lhs, const Img &rhs, Img &res) {
    size_t channel = rhs.data()->channels;
    size_t row = res.data()->height;
    size_t col = res.data()->width;
    size_t veclen = rhs.data()->width;
    size_t vecsimd = veclen / 16;
#pragma omp parallel for
    for (size_t i = 0; i < channel; ++i) {
        for (size_t k = 0; k < col; ++k) {
            for (size_t j = 0; j < row; ++j) {
                float tmp = 0.f;
                for (size_t l = 0; l < vecsimd; ++l) {
                    tmp += _mm512_reduce_add_ps(
                            _mm512_mul_ps(_mm512_loadu_ps(lhs + l * 16), _mm512_loadu_ps(rhs.ptr() + l * 16)));
                }
                for (size_t l = vecsimd * 16; l < veclen; ++l) {
                    tmp += lhs[l] * rhs.ptr()[l];
                }
                res.ptr()[i * row * col + j * col + k] = tmp;
            }
        }
    }
}
#endif
