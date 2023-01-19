#include <iostream>

struct Node {
    long coe;
    int exp;
    Node* next;
};

int main() {
    int n, m;
    scanf("%d %d", &n, &m);
    Node head{0, INT32_MAX, nullptr};
    Node* cur = &head;
    while (n--) {
        int coe, exp;
        scanf("%d %d", &coe, &exp);
        Node* node = new Node{coe, exp, nullptr};
        cur->next = node;
        cur = cur->next;
    }
    cur->next = new Node{0, INT32_MIN, nullptr};
    cur = &head;
    while (m--) {
        int coe, exp;
        scanf("%d %d", &coe, &exp);
        while (cur->next && cur->next->exp >= exp) cur = cur->next;
        if (cur->exp == exp)
            cur->coe += coe;
        else
            cur->next = new Node{coe, exp, cur->next};
    }
    cur = head.next;
    int cnt = 0;
    while (cur->next) {
        if (cur->coe) cnt++;
        cur = cur->next;
    }
    printf("%d\n", cnt);

    cur = head.next;
    while (cur->next) {
        if (cur->coe) printf("%ld %d\n", cur->coe, cur->exp);
        cur = cur->next;
    }
}
