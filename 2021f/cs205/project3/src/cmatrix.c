#include "cmatrix.h"

CMatrix *MatEmpty(size_t row, size_t col) {
    CMatrix *mat = (CMatrix *) malloc(sizeof(CMatrix));
    mat->m_row = row;
    mat->m_col = col;
    mat->m_storage = RowMajor;
    mat->m_data = (float *) malloc(sizeof(float) * row * col);
    return mat;
}

CMatrix *MatFromFile(size_t row, size_t col, FILE *f) {
    TIMER_START
    CMatrix *mat = MatEmpty(row, col);
    float tmp;
    float *cur = mat->m_data;
    for (register size_t i = 0; i < row; i++) {
        for (register size_t j = 0; j < col; j++) {
            if (fscanf(f, "%f", &tmp) == EOF) break;
            *(cur++) = tmp;
        }
    }
    printf("CMat (%lu*%lu) initialized. ", row, col);
    fclose(f);
    TIMER_END
    return mat;
}

CMatrix *MatFromFileTrans(size_t ori_row, size_t ori_col, FILE *f) {
    TIMER_START
    CMatrix *mat = MatEmpty(ori_col, ori_row);
    mat->m_storage = ColMajor;
    float tmp;
    float *data = mat->m_data;
    for (register size_t i = 0; i < ori_row; i++) {
        for (register size_t j = 0; j < ori_col; j++) {
            if (fscanf(f, "%f", &tmp) == EOF) break;
            data[j * ori_col + i] = tmp;
        }
    }
    printf("Transposed CMat (%lu*%lu) initialized. ", ori_col, ori_row);
    fclose(f);
    TIMER_END
    return mat;
}

CMatrix *MatClone(ReadOnlyMat ori) {
    CMatrix *new = MatEmpty(ori->m_row, ori->m_col);
    new->m_storage = ori->m_storage;
    memcpy(new->m_data, ori->m_data, sizeof(float) * ori->m_row * ori->m_col);
    return new;
}

CMatrix *MatZeros(size_t row, size_t col) {
    CMatrix *mat = MatEmpty(row, col);
    MatFill(mat, 0.f);
    return mat;
}

CMatrix *MatOnes(size_t row, size_t col) {
    CMatrix *mat = MatEmpty(row, col);
    MatFill(mat, 1.f);
    return mat;
}

CMatrix *MatEye(size_t size) {
    CMatrix *mat = MatZeros(size, size);
    for (register size_t i = 0; i < size; i++)
        MatSet(mat, i, i, 1.f);
    return mat;
}

CMatrix *MatDiag(size_t size, const float *diag_elems) {
    CMatrix *mat = MatZeros(size, size);
    for (register size_t i = 0; i < size; i++)
        MatSet(mat, i, i, diag_elems[i]);
    return mat;
}

CMatrix *MatRand(size_t row, size_t col, float min, float max) {
    CMatrix *mat = MatEmpty(row, col);
    register size_t cnt = mat->m_row * mat->m_col;
    float *cur = mat->m_data;
    while (cnt--)
        *(cur++) = (rand() % (int) ((max - min) * 10000.f)) / 10000.f + min;
    return mat;
}

bool MatCpy(ReadOnlyMat src, CMatrix *dst) {
    if (src->m_row > dst->m_row || src->m_col > dst->m_col) {
        printf("Cannot copy matrix: src(%lu*%lu) -> dst(%lu*%lu)\n",
               src->m_row, src->m_col,
               dst->m_row, dst->m_col);
        return false;
    }
    if (src->m_row * src->m_col == dst->m_row * dst->m_col)
        memcpy(dst->m_data, src->m_data, sizeof(float) * src->m_row * src->m_col);
    else {
        for (int r = 0; r < src->m_row; r++) {
            memcpy(dst->m_data + r * dst->m_col,
                   src->m_data + r * src->m_col,
                   sizeof(float) * src->m_col);
            memset(dst->m_data + r * dst->m_col + src->m_col,
                   0, sizeof(float) * (dst->m_col - src->m_col));
        }
        printf("Copying matrix (%lu*%lu) -> (%lu*%lu), the blanks are set zero\n",
               src->m_row, src->m_col,
               dst->m_row, dst->m_col);
    }
    return true;
}

void MatFill(CMatrix *mat, float elem) {
    float *cur = mat->m_data;
    register size_t cnt = mat->m_row * mat->m_col;
    while (cnt--) *(cur++) = elem;
}

bool MatResize(CMatrix *mat, size_t nrow, size_t ncol) {
    if (mat->m_row * mat->m_col != nrow * ncol) return false;
    mat->m_row = nrow;
    mat->m_col = ncol;
    return true;
}

bool MatEquals(ReadOnlyMat self, ReadOnlyMat oppo) {
    if (self->m_row != oppo->m_row ||
        self->m_col != oppo->m_col)
        return false;
    float *curSelf = self->m_data;
    float *curOppo = oppo->m_data;
    register size_t cnt = self->m_row * self->m_col;
    while (cnt--)
        if (!FLOAT_EQL(*(curSelf++), *(curOppo++))) return false;
    return true;
}

void MatTrans(CMatrix **mat) {
    CMatrix *trans = MatEmpty((*mat)->m_col, (*mat)->m_row);
    trans->m_storage = (*mat)->m_storage;
    size_t row = (*mat)->m_row, col = (*mat)->m_col;
    for (int i = 0; i < row; ++i)
        for (int j = 0; j < col; ++j)
            MatSet(trans, j, i, MatGet(*mat, i, j));
    MatDelete(*mat);
    *mat = trans;
}

CMatrix *MatSubCopy(ReadOnlyMat mat, size_t rowst, size_t srow_len, size_t colst, size_t scol_len) {
    CMatrix *sub = MatEmpty(srow_len, scol_len);
    sub->m_storage = mat->m_storage;
    for (int i = 0; i < srow_len; ++i)
        for (int j = 0; j < scol_len; ++j)
            MatSet(sub, i, j, MatGet(mat, rowst + i, colst + j));
    return sub;
}

CMatrixView *MatViewSub(ReadOnlyMat mat, size_t rowst, size_t srow_len, size_t colst, size_t scol_len) {
    if (mat->m_storage == ColMajor) return NULL;
    CMatrixView *view = (CMatrixView *) malloc(sizeof(CMatrixView));
    view->m_row = srow_len;
    view->m_col = scol_len;
    view->m_data_ptr = (float **) malloc(sizeof(float *) * srow_len);
    for (int i = 0; i < srow_len; ++i)
        view->m_data_ptr[i] = mat->m_data + (i * mat->m_col + colst);
    return view;
}

void MatViewDel(CMatrixView *mv) {
    free(mv->m_data_ptr);
    free(mv);
}

CMatrix *MatConcat(ReadOnlyMat mat1, ReadOnlyMat mat2, ReadOnlyMat mat3, ReadOnlyMat mat4) {
    size_t row = mat1->m_row, col = mat1->m_col;
    CMatrix *mer = MatEmpty(2 * row, 2 * col);
    for (register size_t i = 0; i < row; i++) {
        for (register size_t j = 0; j < col; j++) {
            MatSet(mer, i, j, MatGet(mat1, i, j));
            MatSet(mer, i, j + col, MatGet(mat2, i, j));
            MatSet(mer, i + row, j, MatGet(mat3, i, j));
            MatSet(mer, i + row, j + col, MatGet(mat4, i, j));
        }
    }
    return mer;
}

void MatAdd(ReadOnlyMat it, CMatrix *to) {
    if (it->m_row != to->m_row ||
        it->m_col != to->m_col) {
        printf("MathErr: Mat(%lu*%lu) + Mat(%lu,%lu) is invalid\n",
               it->m_row, it->m_col, to->m_row, to->m_col);
        return;
    }
    float *curit = it->m_data;
    float *curto = to->m_data;
    register size_t items = it->m_row * it->m_col;
    while (items--) *(curto++) += *(curit++);
}

void MatSub(const CMatrix *by, CMatrix *dst) {
    if (by->m_row != dst->m_row ||
        by->m_col != dst->m_col) {
        printf("MathErr: Mat(%lu*%lu) - Mat(%lu,%lu) is invalid\n",
               by->m_row, by->m_col, dst->m_row, dst->m_col);
        return;
    }
    float *curby = by->m_data;
    float *curdst = dst->m_data;
    register size_t items = by->m_row * by->m_col;
    while (items--) *(curdst++) -= *(curby++);
}

void MatShowRow(ReadOnlyMat mat, size_t r) {
    if (r == 0) printf("[[");
    else printf(" [");
    if (mat->m_col > 6) {
        for (size_t i = 0; i < 3; ++i)
            printf("%.4f  ", MatGet(mat, r, i));
        printf("...");
        for (size_t i = mat->m_row - 3; i < mat->m_row; ++i)
            printf("  %.4f", MatGet(mat, r, i));
    } else {
        for (size_t i = 0; i < mat->m_col - 1; ++i)
            printf("%.4f  ", MatGet(mat, r, i));
        printf("%.4f", MatGet(mat, r, mat->m_col - 1));
    }
    if (r == mat->m_row - 1) printf("]]\n");
    else printf("],\n");
}

void MatRepr(ReadOnlyMat mat) {
    printf("CMatrix (dtype: float, size: %lu*%lu)\n", mat->m_row, mat->m_col);
    if (mat->m_row > 6) {
        for (size_t i = 0; i < 3; ++i)
            MatShowRow(mat, i);
        printf(" ...\n");
        for (size_t i = mat->m_row - 3; i < mat->m_row; ++i)
            MatShowRow(mat, i);
    } else {
        for (size_t i = 0; i < mat->m_row; ++i)
            MatShowRow(mat, i);
    }
}

void MatSave(ReadOnlyMat mat, const char *filename) {
    TIMER_START
    FILE *f = fopen(filename, "w+");
    size_t row = mat->m_row, col = mat->m_col;
    for (register int i = 0; i < row; i++) {
        for (register int j = 0; j < col; j++)
            fprintf(f, "%.4f ", MatGet(mat, i, j));
        fprintf(f, "\n");
    }
    if (fclose(f) != 0) {
        printf("Error saving CMat to <%s>: %s", filename, strerror(errno));
        return;
    }
    printf("Answer saved to <%s>\t", filename);
    TIMER_END
}
