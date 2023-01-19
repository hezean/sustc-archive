#include <iostream>
#include <queue>
#include <vector>

using namespace std;

int T, n, k, ti, tj, tmp;

struct node {
    vector<node *> conn;
    bool vis{false};
    int len{0};
};

void dfs(node *root) {
    queue<node *> q;
    q.push(root);
    while (!q.empty()) {
        for (int i = 0; i < q.front()->conn.size(); i++)
            if (!q.front()->conn.at(i)->vis) {
                q.front()->conn.at(i)->len = q.front()->len + 1;
                q.push(q.front()->conn.at(i));
            }
        q.front()->vis = true;
        q.pop();
    }
}

void cls(node *tree, int sz) {
    for (int i = 0; i < sz; i++) {
        tree[i].len = 0;
        tree[i].vis = false;
    }
}

node *findL3(node **fri, int sz) {
    node *mxNode = nullptr;
    int mxLen = -1;
    for (int i = 0; i < sz; i++) {
        if (fri[i]->len > mxLen) {
            mxLen = fri[i]->len;
            mxNode = fri[i];
        }
    }
    return mxNode;
}

int main() {
    ios_base::sync_with_stdio(0), cin.tie(0), cout.tie(0);
    cin >> T;
    while (T--) {
        cin >> n >> k;
        node *tree = new node[n];
        node **fri = new node *[k];
        for (int i = 0; i < n - 1; ++i) {
            cin >> ti >> tj;
            tree[ti - 1].conn.push_back(&tree[tj - 1]);
            tree[tj - 1].conn.push_back(&tree[ti - 1]);
        }
        for (int i = 0; i < k; ++i) {
            cin >> tmp;
            fri[i] = &tree[tmp - 1];
        }
        dfs(tree);
        node *b = findL3(fri, k);
        cls(tree, n);
        dfs(b);
        node *c = findL3(fri, k);
        cout << (c->len + 1) / 2 << endl;
        delete[] tree;
    }
}
