#include "cmatrix.h"
#include "cmat_mul.h"
#include "util.h"

int main(int argc, const char **argv) {
    if (argc != 4) {
        printf("3 arguments expected (input 1, input 2, output), got %d\n", argc - 1);
        return -1;
    }
    char *in_buf = (char *) malloc(sizeof(char) * 40960);

    FILE *f1 = OpenFile(argv[1]);
    FILE *f2 = OpenFile(argv[2]);
    size_t r1 = CheckRow(f1, in_buf);
    size_t r2 = CheckRow(f2, in_buf);
    size_t c1 = CheckCol(f1, in_buf);
    size_t c2 = CheckCol(f2, in_buf);
    if (c1 != r2) {
        printf("The input two matrices cannot be multiplied: "
               "Mat 1 (%lu*%lu)\tMat 2 (%lu*%lu)\n",
               r1, c1, r2, c2);
        return -1;
    }

    CMatrix *mat1 = MatFromFile(r1, c1, f1);
    CMatrix *mat2 = MatFromFile(r2, c2, f2);
    printf("\n");

    TIMER_START
    CMatrix *prod = MatMul(mat1, mat2, Naive);
    TIMER_END

    MatRepr(prod);
    MatSave(prod, argv[3]);
    MatDelete(mat1);
    MatDelete(mat2);
    MatDelete(prod);
    return 0;
}
