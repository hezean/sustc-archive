#ifndef MATMUL_CMAT_MUL_H
#define MATMUL_CMAT_MUL_H

#include <cblas.h>
#include <pthread.h>

#include "cmatrix.h"

#define _AVX512
//#define _AVX2
//#define _NEON

#if defined(_NEON)
#include "arm_neon.h"
#elif defined(_AVX512) || defined(_AVX2)

#include <immintrin.h>

#endif

#define BLOCKSIZE 1
#define UNROLL 4

typedef enum {
    Naive,
    Blas,
    Order,
    Simd,
    OpenCv,
    Threads,
    OpenMp,
    Unroll,
    Block,
//    UnrollMp,
} kMatMulMode;

CMatrix *MatMul(ReadOnlyMat mat1, ReadOnlyMat mat2, kMatMulMode mode, int times);

CMatrix *MatMulO3(ReadOnlyMat mat1, ReadOnlyMat mat2);

void MatMulChangeOrd(ReadOnlyMat mat1, ReadOnlyMat mat2, int times);

CMatrix *MatMulBlas(ReadOnlyMat mat1, ReadOnlyMat mat2);

//CMatrix *MatMulStrassen(ReadOnlyMat mat1, ReadOnlyMat mat2, int edge);

CMatrix *MatMulCV(ReadOnlyMat mat1, ReadOnlyMat mat2);

CMatrix *MatMulMP(ReadOnlyMat mat1, ReadOnlyMat mat2);

CMatrix *MatMulSimd(ReadOnlyMat mat1, ReadOnlyMat mat2);

CMatrix *MatMulUnroll(ReadOnlyMat mat1, ReadOnlyMat mat2);

CMatrix *MatMulBlock(ReadOnlyMat mat1, ReadOnlyMat mat2);

CMatrix *MatMulThreads(ReadOnlyMat mat1, ReadOnlyMat mat2);

//CMatrix *MatMulUnrollMp(ReadOnlyMat mat1, ReadOnlyMat mat2);

#endif //MATMUL_CMAT_MUL_H
