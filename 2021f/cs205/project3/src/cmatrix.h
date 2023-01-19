#ifndef MATMUL_CMATRIX_H
#define MATMUL_CMATRIX_H

#include <omp.h>
#include <opencv2/core/core_c.h>
#include <math.h>
#include <stdbool.h>
#include <stdio.h>

#include "util.h"

#define FLOAT_EQL(f1, f2) (fabsf((f1) - (f2)) > 1e-5)

typedef enum {
    RowMajor, ColMajor,
} kMatData;

typedef struct {
    size_t m_row;
    size_t m_col;
    kMatData m_storage;
    float *m_data;
} CMatrix;

typedef const CMatrix *ReadOnlyMat;

/**
 * To simplify the implementation, only row-major matrices are supported
 */
typedef struct {
    size_t m_row;
    size_t m_col;
    kMatData m_storage;
    float **m_data_ptr;
} CMatrixView;

CMatrix *MatEmpty(size_t row, size_t col);

CMatrix *MatFromFile(size_t row, size_t col, FILE *f);

CMatrix *MatFromFileTrans(size_t ori_row, size_t ori_col, FILE *f);

CMatrix *MatClone(ReadOnlyMat ori);

CMatrix *MatZeros(size_t row, size_t col);

CMatrix *MatOnes(size_t row, size_t col);

/**
 * Creates an identify matrix (size*size)
 */
CMatrix *MatEye(size_t size);

/**
 * Fill all the elems in the array to a diagonal matrix
 * While the other elements of such (size*size) matrix are set zeros
 */
CMatrix *MatDiag(size_t size, const float *diag_elems);

CMatrix *MatRand(size_t row, size_t col, float min, float max);

static inline void MatDelete(CMatrix *mat) {
    free(mat->m_data);
    free(mat);
}

/**
 * Getter of a CMatrix (m*n)
 * @param mat can be either row-maj or col-maj
 * @param row [0..m-1]
 * @param col [0..n-1]
 * @returns the value of mat[row][col]
 *          if access out of bound, returns NAN
 */
static inline float MatGet(ReadOnlyMat mat, size_t row, size_t col) {
    if (row > mat->m_row || col > mat->m_col) return NAN;
    if (mat->m_storage == RowMajor)
        return mat->m_data[row * mat->m_col + col];
    else return mat->m_data[col * mat->m_row + row];
}

/**
 * If the index out of bounds, do nothing
 */
static inline void MatSet(CMatrix *mat, size_t row, size_t col, float val) {
    if (row >= mat->m_row || col >= mat->m_col) return;
    if (mat->m_storage == RowMajor)
        mat->m_data[row * mat->m_col + col] = val;
    else mat->m_data[col * mat->m_row + row] = val;
}

/**
 * Copy the data from matrix <src> to <dst>
 * without changing the size of <dst>
 * To simplify the implementation, only row-major matrix is supported
 *
 * If <src> is bigger(row or col) than <dst> then
 *      to avoid data loss, will not do anything
 *      also prompt the user
 * If <src> is smaller(row or col) than <dst> then
 *      copy the available data, set the blank as zeros
 *
 * @param src of size (m*n), with m_data[m*n]
 * @param dst of size (x*y)
 * @return false if it is possible to loss data,
 *              caller shall check the return value
 *              to avoid further exceptions
 *         true otherwise
 */
bool MatCpy(ReadOnlyMat src, CMatrix *dst);

/**
 * Fill (overwrite) the data of a mat and fill them all with <elem>
 */
void MatFill(CMatrix *mat, float elem);

/**
 * Make a matrix in an original size m*n to behave as nrow*ncol
 *
 * @param mat the matrix to be resized
 * @param nrow the new size of row it should be
 * @param ncol should satisfies that nrow*ncol=m*n
 * @returns false if the new size mismatch, true otherwise
 */
bool MatResize(CMatrix *mat, size_t nrow, size_t ncol);

bool MatEquals(ReadOnlyMat self, ReadOnlyMat oppo);

void MatTrans(CMatrix **mat);

/**
 * Deep clone the sub-matrix (srow_len*scol_len)
 * It is equivalent to mat[rowst:rowst+srow_len][colst:colst+scol_len]
 */
CMatrix *MatSubCopy(ReadOnlyMat mat, size_t rowst, size_t srow_len, size_t colst, size_t scol_len);

CMatrixView *MatViewSub(ReadOnlyMat mat, size_t rowst, size_t srow_len, size_t colst, size_t scol_len);

void MatViewDel(CMatrixView *mv);

/**
 * mat1,2,3,4 must be the same size
 * @returns [[mat1] [mat2]
 *           [mat3] [mat4]]
 */
CMatrix *MatConcat(ReadOnlyMat mat1, ReadOnlyMat mat2, ReadOnlyMat mat3, ReadOnlyMat mat4);

/**
 * This function won't create any new CMatrix instance,
 * but updates the data of <to>
 * <p>
 * Aka. m1=a, m2=b; MatAdd(m1,m2); m1==a && m2==b+a;
 *
 * @param it a CMatrix instance, its data won't be affected
 * @param to a CMatrix instance, must have the same size as <this>
 */
void MatAdd(ReadOnlyMat it, CMatrix *to);

/**
 * This function won't create any new CMatrix instance,
 * but updates the data of <dst>
 * <p>
 * Aka. m1=a, m2=b; MatSub(m1,m2); m1==a && m2==b-a;
 *
 * @param by a CMatrix instance, its data won't be affected
 * @param dst a CMatrix instance, must have the same size as <this>
 */
void MatSub(ReadOnlyMat by, CMatrix *dst);

void MatShowRow(ReadOnlyMat mat, size_t r);

void MatRepr(ReadOnlyMat mat);

void MatSave(ReadOnlyMat mat, const char *filename);

#endif //MATMUL_CMATRIX_H
