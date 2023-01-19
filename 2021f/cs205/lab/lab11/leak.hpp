#pragma once

#include <vector>
#include <iostream>

#if not defined(NDEBUG)

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
    ptr = nullptr;
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

#endif  // NDEBUG
