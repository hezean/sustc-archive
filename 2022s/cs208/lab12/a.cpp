#include <algorithm>
#include <iostream>
#include <vector>

using namespace std;

int* node_lv = new int[100];
bool* node_vis = new bool[100];
vector<int>* node_conn = new vector<int>[100];

int* edge_to = new int[5000];
int64_t* edge_cap = new int64_t[5000];
int64_t* edge_flow = new int64_t[5000];

int* q = new int[100];

size_t edge_fi = 0;

inline int read() {
    int x = 0;
    char ch = 0;
    while (ch < '0' || ch > '9') {
        ch = getchar();
    }
    while (ch >= '0' && ch <= '9') {
        x = x * 10 + (ch - '0');
        ch = getchar();
    }
    return x;
}

inline int64_t readL() {
    int64_t x = 0;
    char ch = 0;
    while (ch < '0' || ch > '9') {
        ch = getchar();
    }
    while (ch >= '0' && ch <= '9') {
        x = x * 10L + (ch - '0');
        ch = getchar();
    }
    return x;
}

inline void writeL(int64_t x) {
    if (x > 9) writeL(x / 10);
    putchar(x % 10 + '0');
}

inline bool bfs(int s, int t) {
    memset(node_vis, 0, sizeof(bool) * 100);

    node_vis[s] = true;
    node_lv[s] = 0;

    int q_head = 0, q_tail = 0;
    q[q_tail++] = s;
    while (q_head < q_tail) {
        int n = q[q_head++];
        auto& conn = node_conn[n];
        for (size_t i = 0; i < conn.size(); i++) {
            int e = conn[i];
            int to = edge_to[e];
            if (!node_vis[to] && edge_flow[e] < edge_cap[e]) {
                node_vis[to] = true;
                node_lv[to] = node_lv[n] + 1;
                q[q_tail++] = to;
            }
        }
    }
    return node_vis[t];
}

inline int64_t dfs(int s, int t, int64_t bottleNeck = __LONG_LONG_MAX__) {
    if (s == t) return bottleNeck;

    auto& conn = node_conn[s];
    for (size_t i = 0; i < conn.size(); i++) {
        int e = conn[i];
        int to = edge_to[e];
        if (edge_flow[e] < edge_cap[e] && node_lv[s] + 1 == node_lv[to]) {
            int64_t flow = dfs(to, t, min(bottleNeck, edge_cap[e] - edge_flow[e]));
            if (flow > 0) {
                edge_flow[e] += flow;
                return flow;
            }
        }
    }
    return 0;
}

inline int64_t dinic(int s, int t) {
    int64_t flow = 0, tmp;
    while (bfs(s, t)) {
        tmp = dfs(s, t);
        while (tmp > 0) {
            flow += tmp;
            tmp = dfs(s, t);
        }
    }
    return flow;
}

int main() {
    size_t n = read(), m = read(), s = read(), t = read();
    for (size_t i = 0; i < m; ++i) {
        int u = read() - 1, v = read() - 1;
        int64_t c = readL();

        edge_cap[edge_fi] = c;
        edge_to[edge_fi] = v;
        node_conn[u].push_back(edge_fi++);
    }
    writeL(dinic(s, t));
}
