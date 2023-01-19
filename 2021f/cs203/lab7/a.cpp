#include <iostream>
#include <queue>
#include <vector>

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

int n;
long tar;

struct node {
    std::vector<node *> filial;
    std::vector<int> weight;
    long pl = 0;
    bool vis = false;
};

void dfs(node *tree) {
    std::queue<node *> q;
    q.push(tree);
    while (!q.empty()) {
        for (int i = 0; i < q.front()->filial.size(); i++)
            if (!q.front()->filial.at(i)->vis) {
                q.push(q.front()->filial.at(i));
                q.front()->filial.at(i)->pl =
                    q.front()->pl + q.front()->weight.at(i);
            }
        q.front()->vis = true;
        q.pop();
    }
}

int main() {
    n = qread();
    scanf("%ld", &tar);
    node *tree = new node[n];
    int tp, ts, tw;
    for (int i = 0; i < n - 1; i++) {
        tp = qread();
        ts = qread();
        tw = qread();
        tree[ts - 1].filial.push_back(tree + tp - 1);
        tree[tp - 1].filial.push_back(tree + ts - 1);
        tree[ts - 1].weight.push_back(tw);
        tree[tp - 1].weight.push_back(tw);
    }
    dfs(tree);
    int cnt = 0;
    for (int i = 0; i < n; i++)
        if (tree[i].pl == tar && tree[i].filial.size() == 1) cnt++;
    qwrite(cnt);
}
