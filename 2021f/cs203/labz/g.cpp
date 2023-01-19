#include <iostream>

using namespace std;

struct Node {
    int id;
    Node *prev, *next;

    explicit Node(int i) : id(i), prev(nullptr), next(nullptr) {}
};

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(0), cout.tie(0);
    int n, m, k, x;
    cin >> n >> m >> k >> x;
    Node *nds = (Node *)malloc(sizeof(Node) * n * m);

    for (int i = 0; i < n; ++i) {
        nds[i * m] = Node(i * m);
        for (int j = 1; j < m; ++j) {
            nds[i * m + j] = Node(i * m + j);
            nds[i * m + j].prev = nds + i * m + j - 1;
            nds[i * m + j - 1].next = nds + i * m + j;
        }
    }

    int a, b;
    for (int i = 0; i < k; ++i) {
        cin >> a >> b;

        if (nds[a].next) nds[a].next->prev = nds[b].prev;
        if (nds[b].prev) nds[b].prev->next = nds[a].next;

        nds[a].next = nds + b;
        nds[b].prev = nds + a;
    }

    Node *cur = nds + x;
    while (cur->prev && cur->prev->prev) cur = cur->prev;

    while (cur) {
        cout << cur->id << " ";
        cur = cur->next;
    }
}
