#include <stdbool.h>
#include <stdio.h>

int main() {
    printf("\nPrint size of the fundamental types:\n");
    printf("-----------------------------------------");
    printf("The sizeof(char) is:%lu bytes.\n", sizeof(char));
    printf("The sizeof(short) is:%lu bytes.\n", sizeof(short));
    printf("The sizeof(int) is: %lu bytes.\n", sizeof(int));
    printf("The sizeof(long) is: %lu bytes.\n", sizeof(long));
    printf("The sizeof(long long) is: %lu bytes.\n", sizeof(long long));
    printf("The sizeof(float) is: %lu bytes.\n", sizeof(float));
    printf("The sizeof(double) is: %lu bytes.\n", sizeof(double));
    printf("The sizeof(long double) is: %lu bytes.\n", sizeof(long double));
    printf("The sizeof(bool) is: %lu byte.\n", sizeof(bool));
    return 0;
}
