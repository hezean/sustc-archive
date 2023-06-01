#include <stdio.h>
#include <stdlib.h>

typedef struct Char {
    char chr;
    struct Char *left;
    struct Char *right;
} Char;

Char EOL = {'\0', NULL, NULL};
Char *cur = &EOL;

void ins_left(Char *p, char val) {
    Char *c = (Char *) malloc(sizeof(Char));
    c->chr = val;
    c->left = p->left;
    c->right = p;
    if (p->left) p->left->right = c;
    p->left = c;
}

int main() {
    int n;
    scanf("%d", &n);

    char op;
    scanf("%c", &op);  // \n
    while (n--) {
        scanf("%c", &op);

        switch (op) {
            case 'r': {
                n--;
                scanf("%c", &op);

                if (cur == &EOL) {
                    ins_left(cur, op);
                    cur = cur->left;
                } else {
                    cur->chr = op;
                }
                break;
            }

            case 'I': {
                while (cur->left) cur = cur->left;
                break;
            }

            case 'L': {
                if (cur->left) cur = cur->left;
                break;
            }

            case 'R': {
                if (cur->right) cur = cur->right;
                break;
            }

            case 'd': {
                if (cur == &EOL) break;
                Char *left = cur->left;
                Char *right = cur->right;
                if (left) left->right = right;
                if (right) right->left = left;
                if (right) cur = right;
                break;
            }

            default:
                ins_left(cur, op);
        }
    }

    cur = &EOL;
    while (cur->left) cur = cur->left;
    while (cur && cur->right) {
        printf("%c", cur->chr);
        cur = cur->right;
    }
    printf("\n");
}
