#include <math.h>

#include <iostream>

struct Node {
    int id;
    int val;
    Node *prev;
    Node *next;

    Node(int i, int v, Node *pv) : id(i), val(v), prev(pv), next(nullptr) {}
};

void mer(Node *l, Node *m, Node *r) {
    Node *ppv = l->prev;
    Node *enn = r->next;
    auto i = l, j = m;
    while (i != m || j != enn) {
        if (i == m) {
            ppv->next = j;
            j = j->next;
        } else if (j == enn) {
            ppv->next = i;
            i = i->next;
        } else if (i->val <= j->val) {
            ppv->next = i;
            i = i->next;
        } else {
            ppv->next = j;
            j = j->next;
        }
        ppv->next->prev = ppv;
        ppv = ppv->next;
    }
    ppv->next = enn;
    enn->prev = ppv;
}

Node *nnptr(Node *cur, int n, Node *end) {
    if (!cur) return end;
    for (int i = 0; i < n; ++i) {
        if (cur == end) break;
        cur = cur->next;
    }
    return cur;
}

void ms(Node *lo, Node *hi, int len) {
    Node *cur, *mi, *end, *enxn;
    Node *ppv = lo->prev;
    Node *enn = hi->next;
    for (int i = 1; i <= len; i *= 2) {
        cur = ppv->next;
        mi = nnptr(cur, i - 1, enn->prev);
        while (mi != enn->prev) {
            end = nnptr(mi, i, enn->prev);
            enxn = (end != enn->prev) ? end->next : nullptr;
            if (mi->val > mi->next->val) mer(cur, mi->next, end);
            cur = enxn;
            mi = nnptr(cur, i - 1, enn->prev);
        }
    }
}

inline int qread() {
    int s = 0, w = 1;
    char c = getchar();
    while (c < '0' || c > '9') {
        if (c == '-') w = -1;
        c = getchar();
    }
    while (c >= '0' && c <= '9') {
        s = s * 10 + c - '0';
        c = getchar();
    }
    return s * w;
}

inline void qwrite(int x) {
    if (x == 0) {
        putchar('0');
        return;
    }
    if (x < 0) {
        putchar('-');
        x = -x;
    }
    int s[20];
    int k = 0;
    while (x) {
        s[k++] = x % 10;
        x /= 10;
    }
    while (k--) putchar('0' + s[k]);
}

int main() {
    int n;
    scanf("%d", &n);
    Node head(-1, -1, nullptr);
    Node tail(-1, 2000000001, nullptr);
    Node *cur = &head;

    for (int i = 1; i <= n; i++) {
        cur->next = new Node(i, qread(), cur);
        cur = cur->next;
    }
    cur->next = &tail;
    tail.prev = cur;

    ms(head.next, tail.prev, n);

    cur = head.next;
    Node *fwd, *nxt;
    int *ans = new int[n - 1];
    while (cur && cur->id > 0) {
        if (cur->id == n) {
            cur = cur->next;
            continue;
        }
        fwd = cur->prev;
        nxt = cur->next;
        int tmp;
        while (fwd) {
            if (fwd->id > cur->id) {
                tmp = abs(fwd->val - cur->val);
                break;
            }
            if (fwd->id < 0) {
                tmp = 2000000001;
                break;
            }
            fwd = fwd->prev;
        }
        while (nxt) {
            if (nxt->id > cur->id) {
                tmp = std::min(tmp, abs(nxt->val - cur->val));
                break;
            }
            if (nxt->id < 0) break;
            nxt = nxt->next;
        }
        ans[cur->id - 1] = tmp;
        cur = cur->next;
    }
    for (int i = 0; i < n - 1; i++) {
        qwrite(ans[i]);
        printf(" ");
    }
}
