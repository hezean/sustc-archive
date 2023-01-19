#include "cmat_mul.h"

#define CALL(func, prompt) for (int t = 0; t < times - 1; t++) MatDelete(func(mat1, mat2)); \
                           ans = func(mat1, mat2);                                          \
                           printf("Calling "prompt" %d times: (%lu*%lu)x(%lu*%lu) >>> ",    \
                           times, mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);      \
                           break;

CMatrix *MatMul(ReadOnlyMat mat1, ReadOnlyMat mat2, kMatMulMode mode, int times) {
    CMatrix *ans = NULL;
    TIMER_START
    switch (mode) {
        case Naive:
        CALL(MatMulO3, "Naive Multiplication")
        case Blas:
        CALL(MatMulBlas, "OpenBLAS")
        case OpenCv:
        CALL(MatMulCV, "OpenCV")
        case Simd:
        CALL(MatMulSimd, "SIMD")
        case OpenMp:
        CALL(MatMulMP, "OpenMP")
        case Unroll:
        CALL(MatMulUnroll, "Unroll")
        case Block:
        CALL(MatMulBlock, "Block")
        case Threads:
        CALL(MatMulThreads, "Threads")
    }
    TIMER_END
    return ans;
}


CMatrix *MatMulO3(ReadOnlyMat mat1, ReadOnlyMat mat2) {
    if (mat1->m_col != mat2->m_row) {
        printf("MathErr: Mat(%lu*%lu) * Mat(%lu,%lu) is invalid\n",
               mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
        return NULL;
    }
    CMatrix *prod = MatEmpty(mat1->m_row, mat2->m_col);

    size_t row = mat1->m_row;
    size_t col2 = mat2->m_col;
    size_t col1 = mat1->m_col;
    for (register size_t i = 0; i < row; i++) {
        for (register size_t j = 0; j < col2; j++) {
            register float tmp = 0.f;
            for (register size_t k = 0; k < col1; k++)
                tmp += MatGet(mat1, i, k) * MatGet(mat2, k, j);
            MatSet(prod, i, j, tmp);
        }
    }
    return prod;
}

void MatMulChangeOrd(ReadOnlyMat mat1, ReadOnlyMat mat2, int times) {
    if (mat1->m_col != mat2->m_row) {
        printf("MathErr: Mat(%lu*%lu) * Mat(%lu,%lu) is invalid\n",
               mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
        return;
    }
    CMatrix *prod = MatZeros(mat1->m_row, mat2->m_col);

    size_t row = mat1->m_row;
    size_t col2 = mat2->m_col;
    size_t col1 = mat1->m_col;
    TIMER_START
    for (register int t = 0; t < times; t++) {
        for (register size_t i = 0; i < row; i++) {
            for (register size_t k = 0; k < col1; k++) {
                float s = mat1->m_data[i * mat1->m_col + k];
                for (register size_t j = 0; j < col2; j++) {
                    prod->m_data[i * col2 + j] += s * mat2->m_data[k * col2 + j];
                }
            }
        }
    }
    printf("Calling [ikj] %d times: (%lu*%lu)x(%lu*%lu) >>> ",
           times, mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
    TIMER_END
    MatFill(prod, 0.f);
    TIMER_START
    for (register int t = 0; t < times; t++) {
        for (register size_t k = 0; k < col1; k++) {
            for (register size_t i = 0; i < row; i++) {
                float s = mat1->m_data[i * mat1->m_col + k];
                for (register size_t j = 0; j < col2; j++) {
                    prod->m_data[i * col2 + j] += s * mat2->m_data[k * col2 + j];
                }
            }
        }
    }
    printf("Calling [kij] %d times: (%lu*%lu)x(%lu*%lu) >>> ",
           times, mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
    TIMER_END
    MatFill(prod, 0.f);
    TIMER_START
    for (register int t = 0; t < times; t++) {
        for (register size_t j = 0; j < col2; j++) {
            for (register size_t i = 0; i < row; i++) {
                float s = mat1->m_data[i * mat1->m_col + j];
                for (register size_t k = 0; k < col1; k++) {
                    prod->m_data[i * col2 + j] += s * mat2->m_data[k * col2 + j];
                }
            }
        }
    }
    printf("Calling [jik] %d times: (%lu*%lu)x(%lu*%lu) >>> ",
           times, mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
    TIMER_END
    MatFill(prod, 0.f);
    TIMER_START
    for (register int t = 0; t < times; t++) {
        for (register size_t k = 0; k < col1; k++) {
            for (register size_t j = 0; j < col2; j++) {
                for (register size_t i = 0; i < row; i++) {
                    float s = mat1->m_data[i * mat1->m_col + j];
                    prod->m_data[i * col2 + j] += s * mat2->m_data[k * col2 + j];
                }
            }
        }
    }
    printf("Calling [kji] %d times: (%lu*%lu)x(%lu*%lu) >>> ",
           times, mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
    TIMER_END
    TIMER_START
    for (register int t = 0; t < times; t++) {
        for (register size_t j = 0; j < col2; j++) {
            for (register size_t k = 0; k < col1; k++) {
                for (register size_t i = 0; i < row; i++) {
                    float s = mat1->m_data[i * mat1->m_col + j];
                    prod->m_data[i * col2 + j] += s * mat2->m_data[k * col2 + j];
                }
            }
        }
    }
    printf("Calling [jki] %d times: (%lu*%lu)x(%lu*%lu) >>> ",
           times, mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
    TIMER_END
}

CMatrix *MatMulBlas(ReadOnlyMat mat1, ReadOnlyMat mat2) {
    if (mat1->m_col != mat2->m_row) {
        printf("MathErr: Mat(%lu*%lu) * Mat(%lu,%lu) is invalid\n",
               mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
        return NULL;
    }
    CMatrix *prod = MatEmpty(mat1->m_row, mat2->m_col);

    cblas_sgemm(CblasRowMajor, CblasNoTrans, CblasNoTrans,
                mat1->m_row, mat2->m_col, mat1->m_col,
                1.f,
                mat1->m_data, mat1->m_col,
                mat2->m_data, mat2->m_col,
                0.f,
                prod->m_data, mat2->m_col);
    return prod;
}

// TODO: implementation
//CMatrix *CoreStrassen(CMatrix *mat1, CMatrix *mat2, int edge) {
//    size_t n = mat1->m_col;
//    if (n <= edge) {
//        CMatrix *prod = MatMulO3(mat1, mat2);
//        MatDelete(mat1);
//        MatDelete(mat2);
//        return prod;
//    }
//    n /= 2;
//    CMatrix *A11 = MatSubCopy(mat1, 0, n, 0, n);
//    CMatrix *A12 = MatSubCopy(mat1, 0, n, n, n);
//    CMatrix *A21 = MatSubCopy(mat1, n, n, 0, n);
//    CMatrix *A22 = MatSubCopy(mat1, n, n, n, n);
//    CMatrix *B11 = MatSubCopy(mat2, 0, n, 0, n);
//    CMatrix *B12 = MatSubCopy(mat2, 0, n, n, n);
//    CMatrix *B21 = MatSubCopy(mat2, n, n, 0, n);
//    CMatrix *B22 = MatSubCopy(mat2, n, n, n, n);
//
//    CMatrix *A11p = MatClone(A11);
//    CMatrix *A22p = MatClone(A22);
//    CMatrix *B11p = MatClone(B11);
//    CMatrix *B22p = MatClone(B22);
//}

//CMatrix *MatMulStrassen(ReadOnlyMat mat1, ReadOnlyMat mat2, int edge) {
//    if (mat1->m_row != mat1->m_col || mat2->m_row != mat2->m_col) {
//        printf("Strassen algo can only handle square matrices\n");
//        return NULL;
//    }
//    size_t n = mat1->m_row;
//    if (n & (n - 1)) {  // a method to check whether n is 2^x
//        printf("Matrices' size must be a power of two\n");
//        return NULL;
//    }
//    return CoreStrassen(MatViewSub(mat1, 0, n, 0, n),
//                        MatViewSub(mat2, 0, n, 0, n));
//}

CMatrix *MatMulCV(ReadOnlyMat mat1, ReadOnlyMat mat2) {
    if (mat1->m_col != mat2->m_row) {
        printf("MathErr: Mat(%lu*%lu) * Mat(%lu,%lu) is invalid\n",
               mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
        return NULL;
    }
    CMatrix *prod = MatEmpty(mat1->m_row, mat2->m_col);
    CvMat cvm1 = cvMat(mat1->m_row, mat1->m_col, CV_32F, mat1->m_data);
    CvMat cvm2 = cvMat(mat2->m_row, mat2->m_col, CV_32F, mat2->m_data);
    cvm2 = cvm1;
    CvMat cvm3 = cvMat(mat1->m_row, mat2->m_col, CV_32F, prod->m_data);
    cvGEMM(&cvm1, &cvm2, 1, 0, 0, &cvm3, 0);

//    cvMatMul(&cvm1, &cvm2, &cvm3);
    return prod;
}

CMatrix *MatMulMP(ReadOnlyMat mat1, ReadOnlyMat mat2) {
    if (mat1->m_col != mat2->m_row) {
        printf("MathErr: Mat(%lu*%lu) * Mat(%lu,%lu) is invalid\n",
               mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
        return NULL;
    }
    CMatrix *prod = MatEmpty(mat1->m_row, mat2->m_col);

    size_t row = mat1->m_row;
    size_t col2 = mat2->m_col;
    size_t col1 = mat1->m_col;
#pragma omp parallel for
    for (register size_t i = 0; i < row; i++) {
#pragma omp parallel for
        for (register size_t k = 0; k < col1; k++) {
            float s = mat1->m_data[i * mat1->m_col + k];
            for (register size_t j = 0; j < col2; j++) {
                prod->m_data[i * col2 + j] += s * mat2->m_data[k * col2 + j];
            }
        }
    }
    return prod;
}

// FIXME: (10.23) _mm256_load_ps(m1_dat + n * k + x * 8 + i)) -> MEM_BAD_ACCESS
//// Acknowledgement: Computer Organization and Design RISC-V edition
//// See: https://gitee.com/optimization/dgemm
//void block(int n, int si, int sj, int sk, float *m1_dat, float *m2_dat, float *pd_dat) {
//    for (int i = si; i < si + BLOCKSIZE; i += UNROLL * 8) {
//        for (int j = sj; j < sj + BLOCKSIZE; j++) {
//            __m256 c[UNROLL];
//            for (int x = 0; x < UNROLL; x++)
//                c[x] = _mm256_setzero_ps();
//            for (int k = sk; k < sk + BLOCKSIZE; k++) {
//                __m256 b = _mm256_load_ps(m2_dat + k + j * n);
////                __m256 b = _mm256_broadcast_ps(m2_dat + k + j * n);
//                for (int x = 0; x < UNROLL; x++)
//                    c[x] = _mm256_add_ps(_mm256_mul_ps(b,
//                                                       _mm256_load_ps(m1_dat + n * k + x * 8 + i)),
//                                         c[x]);
//            }
//            for (int x = 0; x < UNROLL; x++)
//                _mm256_store_ps(pd_dat + i + x * 4 + j * n, c[x]);
//        }
//    }
//}
//
//CMatrix *MatMulUnrollMp(ReadOnlyMat mat1, ReadOnlyMat mat2) {
//    size_t sz = mat1->m_row;  // Simply let mat1(n*n), mat2(n*n)
//    CMatrix *prod = MatEmpty(sz, sz);
//    float *dat1 = mat1->m_data;
//    float *dat2 = mat2->m_data;
//    float *dat3 = prod->m_data;
//
//#pragma omp parallel for
//    for (int sj = 0; sj < sz; sj += BLOCKSIZE) {
//        for (int si = 0; si < sz; si += BLOCKSIZE) {
//            for (int sk = 0; sk < sz; sk += BLOCKSIZE) {
//                block(sz, si, sj, sk, dat1, dat2, dat3);
//            }
//        }
//    }
//    return prod;
//}

CMatrix *MatMulSimd(ReadOnlyMat mat1, ReadOnlyMat mat2) {
    CMatrix *m2trans = MatClone(mat2);
    MatTrans(&m2trans);

    size_t sz = mat1->m_row;
    CMatrix *prod = MatEmpty(sz, sz);
    float *data1 = mat1->m_data;
    float *data2 = m2trans->m_data;

#if defined(_AVX512)
    float *tmp = aligned_alloc(sizeof(float) * 16, 512);
    for (register size_t i = 0; i < sz; i++) {
        for (register size_t j = 0; j < sz; j++) {
            __m512 c = _mm512_setzero_ps();
            for (register size_t k = 0; k < sz; k += 16) {
                c = _mm512_add_ps(_mm512_mul_ps(
                                          _mm512_load_ps(data1 + i * sz + k),
                                          _mm512_load_ps(data2 + j * sz + k)),
                                  c);
            }
            _mm512_store_ps(tmp, c);
            MatSet(prod, i, j,
                   tmp[0] + tmp[1] + tmp[2] + tmp[3]
                   + tmp[4] + tmp[5] + tmp[6] + tmp[7]
                   + tmp[8] + tmp[9] + tmp[10] + tmp[11]
                   + tmp[12] + tmp[13] + tmp[14] + tmp[15]);
        }
    }
#elif defined(_AVX2)
    float *tmp = aligned_alloc(sizeof(float) * 8, 256);
    for (register size_t i = 0; i < sz; i++) {
        for (register size_t j = 0; j < sz; j++) {
            __m256 c = _mm256_setzero_ps();
            for (register size_t k = 0; k < sz; k += 8) {
                c = _mm256_add_ps(_mm256_mul_ps(
                                          _mm256_load_ps(data1 + i * sz + k),
                                          _mm256_load_ps(data2 + j * sz + k)),
                                  c);
            }
            _mm256_store_ps(tmp, c);
            MatSet(prod, i, j, tmp[0] + tmp[1] + tmp[2] + tmp[3]
                               + tmp[4] + tmp[5] + tmp[6] + tmp[7]);
        }
    }
#elif defined(_NEON)  // Acknowledgement: ShiqiYu@libfacedetection
    for (register size_t i = 0; i < sz; i++) {
        for (register size_t j = 0; j < sz; j++) {
            float32x4_t sum_float_x4 = vdupq_n_f32(0);
            for (register size_t k = 0; k < sz; k += 4)
                sum_float_x4 = vaddq_f32(sum_float_x4,
                                         vmulq_f32(vld1q_f32(data1 + i * sz + k),
                                                   vld1q_f32(data2 + j * sz + k)));
            float sum = 0.f;
            sum += vgetq_lane_f32(sum_float_x4, 0);
            sum += vgetq_lane_f32(sum_float_x4, 1);
            sum += vgetq_lane_f32(sum_float_x4, 2);
            sum += vgetq_lane_f32(sum_float_x4, 3);
            MatSet(prod, i, j, sum);
        }
    }
#else
    printf("Must select one from AVX2, AVX512 and NEON. quitting...\n");
    exit(-1);
#endif

#if defined(_AVX512) || defined(_AVX2)
    free(tmp);
#endif
    MatDelete(m2trans);
    return prod;
}

CMatrix *MatMulUnroll(ReadOnlyMat mat1, ReadOnlyMat mat2) {
    if (mat1->m_col != mat2->m_row) {
        printf("MathErr: Mat(%lu*%lu) * Mat(%lu,%lu) is invalid\n",
               mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
        return NULL;
    }
    CMatrix *prod = MatZeros(mat1->m_row, mat2->m_col);

    size_t row = mat1->m_row;
    size_t col2 = mat2->m_col;
    size_t col1 = mat1->m_col;
    for (register size_t i = 0; i < row; i++) {
        for (register size_t k = 0; k < col1; k++) {
            float s = mat1->m_data[i * mat1->m_col + k];
//            for (register size_t j = 0; j < col2; j += 8) {
//                prod->m_data[i * col2 + j + 0] += s * mat2->m_data[k * col2 + j + 0];
//                prod->m_data[i * col2 + j + 1] += s * mat2->m_data[k * col2 + j + 1];
//                prod->m_data[i * col2 + j + 2] += s * mat2->m_data[k * col2 + j + 2];
//                prod->m_data[i * col2 + j + 3] += s * mat2->m_data[k * col2 + j + 3];
//                prod->m_data[i * col2 + j + 4] += s * mat2->m_data[k * col2 + j + 4];
//                prod->m_data[i * col2 + j + 5] += s * mat2->m_data[k * col2 + j + 5];
//                prod->m_data[i * col2 + j + 6] += s * mat2->m_data[k * col2 + j + 6];
//                prod->m_data[i * col2 + j + 7] += s * mat2->m_data[k * col2 + j + 7];
//            }
#pragma UNROLL(8)
            for (register size_t j = 0; j < col2; j++) {
                prod->m_data[i * col2 + j] += s * mat2->m_data[k * col2 + j];
            }
        }
    }
    return prod;
}

void do_block(size_t n, const float *m1dat, const float *m2dat, float *prodat) {
    for (int i = 0; i < BLOCKSIZE; i++) {
        for (int j = 0; j < BLOCKSIZE; j++) {
            float tmp = prodat[i * n + j];
            for (int k = 0; k < BLOCKSIZE; k++)
                tmp += m1dat[i * n + k] * m2dat[k * n + j];
            prodat[i * n + j] = tmp;
        }
    }
}

CMatrix *MatMulBlock(ReadOnlyMat mat1, ReadOnlyMat mat2) {
    if (mat1->m_col != mat2->m_row) {
        printf("MathErr: Mat(%lu*%lu) * Mat(%lu,%lu) is invalid\n",
               mat1->m_row, mat1->m_col, mat2->m_row, mat2->m_col);
        return NULL;
    }
    size_t sz = mat1->m_row;
    CMatrix *prod = MatZeros(sz, sz);

    float *dat1 = mat1->m_data;
    float *dat2 = mat2->m_data;
    float *dat3 = prod->m_data;
    for (size_t i = 0; i < sz; i += BLOCKSIZE) {
        for (size_t k = 0; k < sz; k += BLOCKSIZE) {
            for (size_t j = 0; j < sz; j += BLOCKSIZE) {
                do_block(sz, dat1 + i * sz + k,
                         dat2 + k * sz + j,
                         dat3 + i * sz + j);
            }
        }
    }
    return prod;
}

typedef struct {
    ReadOnlyMat mat1, mat2;
    CMatrix *prod;
    size_t i, sz;
} Params;

void subMul(Params *p) {
    size_t i = p->i;
    size_t sz = p->sz;
    const float *data1 = p->mat1->m_data;
    const float *data2 = p->mat2->m_data;
    float *data3 = p->prod->m_data;
    for (register size_t k = 0; k < sz; k++) {
        float s = data1[i * sz + k];
        for (register size_t j = 0; j < sz; j++)
            data3[i * sz + j] += s * data2[k * sz + j];
    }
}

CMatrix *MatMulThreads(ReadOnlyMat mat1, ReadOnlyMat mat2) {
    CMatrix *m2trans = MatClone(mat2);
    MatTrans(&m2trans);

    size_t sz = mat1->m_row;
    CMatrix *prod = MatZeros(sz, sz);
    int threadNum = omp_get_max_threads();
    pthread_t *threads = (pthread_t *) malloc(threadNum * sizeof(pthread_t));
    for (int i = 0; i < sz / threadNum; ++i) {
        for (int t = 0; t < threadNum; ++t) {
            Params *arg = (Params *) malloc(sizeof(Params));
            arg->mat1 = mat1;
            arg->mat2 = mat2;
            arg->prod = prod;
            arg->sz = sz;
            arg->i = i * threadNum + t;
            pthread_create(&threads[i], NULL, subMul, arg);
        }
        for (int i = 0; i < threadNum; ++i)
            pthread_join(threads[i], NULL);
    }

    MatDelete(m2trans);
    return prod;
}
