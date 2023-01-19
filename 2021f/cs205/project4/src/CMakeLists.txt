cmake_minimum_required(VERSION 3.12.1)
project(proj4)
set(CMAKE_CXX_STANDARD 20)
find_package(OpenMP REQUIRED)

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -openmp")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -mavx512f -mavx512vl -mavx512bw")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -mavx2 -mavx512vl -mavx512bw")
# -mavx512f for @AVX512, gcc will add macros including <__AVX512F__>
# -mavx2    for @AVX2  , gcc will add macros including <__AVX2__>
# <NONE>    for @NEON  , gcc will add macros including <__ARM_NEON>

set(SRC main.cpp
        matrix.hpp
        matrix.tpp
        matexcept.cpp
        util.cpp)

#add_compile_definitions(__DISABLE_OMP)
#add_compile_definitions(__DISABLE_SIMD)

add_executable(proj4 ${SRC})
#if (OpenMP_CXX_FOUND)
    target_link_libraries(proj4 OpenMP::OpenMP_CXX)
#else ()
#    add_compile_definitions(__DISABLE_OMP)
#endif ()
