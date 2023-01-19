cmake_minimum_required(VERSION 3.18)
project(matmul C)

set(CMAKE_C_STANDARD 11)
set(CMAKE_C_COMPILER /usr/bin/gcc)

#add_definitions(c_plus_plus)

find_package(OpenMP REQUIRED)
if(NOT TARGET OpenMP::OpenMP_C)
    add_library(OpenMP_TARGET INTERFACE)
    add_library(OpenMP::OpenMP_C ALIAS OpenMP_TARGET)
    target_compile_options(OpenMP_TARGET INTERFACE ${OpenMP_C_FLAGS})
    find_package(Threads REQUIRED)
    target_link_libraries(OpenMP_TARGET INTERFACE Threads::Threads)
    target_link_libraries(OpenMP_TARGET INTERFACE ${OpenMP_C_FLAGS})
endif()

#set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -mavx -I/usr/local/opt/openblas/include")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -mavx512f -I/usr/local/opt/openblas/include")

set(LDFLAGS "-L/usr/local/opt/openblas/lib")
set(USE_BLAS OpenBLAS)
set(BLAS_LIBRARY /usr/local/opt/openblas/lib/libblas.dylib)

set(OpenCV_DIR /usr/local/Cellar/opencv@3/3.4.15/share/OpenCV)
find_package(OpenCV REQUIRED)
include_directories(/usr/local/Cellar/opencv@3/3.4.15/include)

set(SRC benchmark.c util.c cmatrix.c cmat_mul.c)
add_executable(matmul ${SRC})

target_link_libraries(matmul OpenMP::OpenMP_C)
target_link_libraries(matmul ${OpenCV_LIBS})
target_link_libraries(matmul ${BLAS_LIBRARY})
