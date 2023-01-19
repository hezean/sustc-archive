#include "cmatrix.h"
#include "cmat_mul.h"
#include "util.h"

#define MODE Simd

void benchmark(const char *f1, const char *f2, size_t sz, kMatMulMode m, int t) {
    CMatrix *ma = MatFromFile(sz, sz, OpenFile(f1));
    CMatrix *mb = MatFromFile(sz, sz, OpenFile(f2));
    if (m == Order) {
        MatMulChangeOrd(ma, mb, t);
        return;
    }
    CMatrix *ans = MatMul(ma, mb, m, t);
//    MatRepr(ans);
//    MatSave(ans, "out2048.txt");
    MatDelete(ans);
    MatDelete(ma);
    MatDelete(mb);
    printf("\n");
}

int main(int argc, const char **argv) {
    benchmark("mat-A-32.txt", "mat-B-32.txt", 32, MODE, 20);
    benchmark("mat-A-64.txt", "mat-B-64.txt", 64, MODE, 20);
    benchmark("mat-A-128.txt", "mat-B-128.txt", 128, MODE, 20);
    benchmark("mat-A-256.txt", "mat-B-256.txt", 256, MODE, 20);
    benchmark("mat-A-512.txt", "mat-B-512.txt", 512, MODE, 20);
    benchmark("mat-A-1024.txt", "mat-B-1024.txt", 1024, MODE, 3);
//    benchmark("mat-A-1500.txt", "mat-B-1500.txt", 1500, MODE, 3);
    benchmark("mat-A-2048.txt", "mat-B-2048.txt", 2048, MODE, 3);
//    benchmark("mat-A-2500.txt", "mat-B-2500.txt", 2500, MODE, 3);
//    benchmark("mat-A-3000.txt", "mat-B-3000.txt", 3000, MODE, 3);
//    benchmark("mat-A-3500.txt", "mat-B-3500.txt", 3500, MODE, 3);
    benchmark("mat-A-4096.txt", "mat-B-4096.txt", 4096, MODE, 1);

//    CMatrix* m1=MatRand(3500,3500,-10,10);
//    MatSave(m1,"mat-A-3500.txt");
//    MatDelete(m1);
//    CMatrix* m2=MatRand(3500,3500,-10,10);
//    MatSave(m2,"mat-B-3500.txt");
//    MatDelete(m2);
}
