#include "match.h"

char* match(char* s, char ch) {
    while (*s != '\0') {
        if (*s == ch) return s;
        s++;
    }
    return nullptr;
}
