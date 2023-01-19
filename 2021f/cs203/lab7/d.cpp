#include <string.h>

#include <iostream>

char *pre = new char[30];
char *post = new char[30];

char tmp;
int sz, caseCnt;

inline void sw(char &a, char &b) {
    tmp = a;
    a = b;
    b = tmp;
}

void buildSub(int prel, int prer, int postl, int postr) {
    if (prel >= prer || postl >= postr || prel >= sz || prer >= sz ||
        postl >= sz || postr >= sz)
        return;
    int leftRootPre = prel + 1, leftRootPost = postl + 1;
    while (leftRootPost < sz && leftRootPre < sz &&
           post[leftRootPost] != pre[leftRootPre])
        leftRootPost++;
    if (leftRootPost == postl + 1 && leftRootPost < sz) caseCnt *= 2;
    int lsz = postr - leftRootPost + 1;
    buildSub(leftRootPre, leftRootPre + lsz - 1, leftRootPost, postr);
    buildSub(leftRootPre + lsz, prer, postl + 1, leftRootPost - 1);
}

int main() {
    int T;
    scanf("%d", &T);
    while (T--) {
        caseCnt = 1;
        scanf("%s", pre);
        scanf("%s", post);
        sz = strlen(pre);
        for (int i = 0; i < sz / 2; i++) sw(post[i], post[sz - i - 1]);
        buildSub(0, sz - 1, 0, sz - 1);
        printf("%d\n", caseCnt);
    }
}
