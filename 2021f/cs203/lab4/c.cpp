#include <iostream>

int n, p, q, tmp, a, b;

struct Node {
    int w;
    bool hasPrev;
    Node* n;

    explicit Node(int i) : w(i), hasPrev(false), n(nullptr) {}
};

int main() {
    scanf("%d %d %d", &n, &p, &q);
    Node* ns = (Node*)malloc(sizeof(Node) * n);

    for (int i = 0; i < n; i++) {
        scanf("%d", &tmp);
        ns[i] = Node(tmp);
    }
    while (p--) {
        scanf("%d %d", &a, &b);
        Node* cur = ns + a - 1;
        while (cur->n) cur = cur->n;
        ns[b - 1].hasPrev = true;
        cur->n = ns + b - 1;
    }
    for (int i = 0; i < n; i++) {
        Node* cur = ns + i;
        if (cur->hasPrev) continue;

        int cnt = 1;
        while (cur) {
            if (cnt == q) {
                printf("%d ", cur->w);
                break;
            }
            cur = cur->n;
            cnt++;
        }
    }
}
