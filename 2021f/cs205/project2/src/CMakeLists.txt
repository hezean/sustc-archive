cmake_minimum_required(VERSION 3.20)
project(matmul1)

set(CMAKE_CXX_STANDARD 20)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -mavx -I/usr/local/opt/openblas/include")
set(LDFLAGS "-L/usr/local/opt/openblas/lib")

#set(USE_BLAS OpenBLAS)
#set(BLAS_LIBRARY /usr/local/opt/openblas/lib/libblas.dylib)

add_executable(main.cpp Timer.cpp Mat.tpp util.cpp ThreadPool.hpp)

#target_link_libraries(matmul1 ${BLAS_LIBRARY})
