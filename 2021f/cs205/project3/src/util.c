#include "util.h"

FILE *OpenFile(const char *filename) {
    FILE *f = fopen(filename, "r");
    if (f == NULL) {
        printf("Failed to open <%s>: %s", filename, strerror(errno));
        exit(1);
    }
    return f;
}

size_t CheckCol(FILE *f, char *buf) {
    int col = 0;
    fscanf(f, "%[^\n]", buf);
    for (int i = 0; i < strlen(buf); i++)
        if (buf[i] != ' ' &&
            (buf[i + 1] == ' ' ||
             buf[i + 1] == '\0'))
            col++;
    rewind(f);
    return col;
}

size_t CheckRow(FILE *f, char *buf) {
    int row = 0;
    while (!feof(f)) {
        if (fgets(buf, MAX_LEN, f) != NULL)
            row++;
    }
    rewind(f);
    return row;
}
