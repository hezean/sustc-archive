import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

class Node {
    static boolean[][] vis;

    int id;
    long dist = Long.MAX_VALUE;
    int usedPortals;
    ArrayList<Node> conn = new ArrayList<>();
    ArrayList<Integer> weight = new ArrayList<>();

    Node(int i) {
        id = i;
    }

    Node(Node tar, int p, long dis) {
        this.id = tar.id;
        this.dist = dis;
        this.usedPortals = p;
        this.conn = tar.conn;
        this.weight = tar.weight;
    }
}

class MinPQ {
    Node[] pq = new Node[E.n * (E.k + 1) + 100];
    int n = 1;

    void add(Node nd) {
        pq[n++] = nd;
        swim(n - 1);
    }

    void swim(int i) {
        while (i > 1 && pq[i].dist < pq[i / 2].dist) {
            Node tmp = pq[i];
            pq[i] = pq[i / 2];
            pq[i / 2] = tmp;
            i /= 2;
        }
    }

    void sink(int i) {
        while (2 * i < n) {
            int j = 2 * i;
            if (j < n - 1 && pq[j].dist > pq[j + 1].dist)
                j++;
            if (pq[i].dist <= pq[j].dist)
                break;
            Node tmp = pq[i];
            pq[i] = pq[j];
            pq[j] = tmp;
            i = j;
        }
    }

    Node __pop() {
        Node min = pq[1];
        pq[1] = pq[n - 1];
        pq[n - 1] = null;
        n--;
        sink(1);
        return min;
    }

    Node pop() {
        Node tmp = __pop();
        while (tmp != null && Node.vis[tmp.id][tmp.usedPortals]) {
            tmp = __pop();
        }
        if (tmp != null)
            Node.vis[tmp.id][tmp.usedPortals] = true;
        return tmp;
    }
}

public class E {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static int n, m, p, k, s, t;
    static Node[] msp;

    public static void main(String[] args) {
        n = sc.nextInt();
        m = sc.nextInt();
        p = sc.nextInt();
        k = sc.nextInt();
        msp = new Node[n + 1];
        Node.vis = new boolean[n + 1][k + 1];

        for (int i = 1; i <= n; ++i) {
            msp[i] = new Node(i);
        }
        for (int i = 0; i < m; ++i) {
            int u = sc.nextInt(), v = sc.nextInt(), w = sc.nextInt();
            msp[u].conn.add(msp[v]);
            msp[u].weight.add(w);
        }
        for (int i = 0; i < p; ++i) {
            int u = sc.nextInt(), v = sc.nextInt();
            msp[u].conn.add(msp[v]);
            msp[u].weight.add(0);
        }
        s = sc.nextInt();
        t = sc.nextInt();

        dijkstra();
        out.close();
    }

    static void dijkstra() {
        MinPQ pq = new MinPQ();
        msp[s].dist = 0;
        pq.add(msp[s]);
        long res = Long.MAX_VALUE;
        while (pq.n > 1) {
            Node cur = pq.pop();
            if (cur == null)
                break;
            if (cur.id == t) {
                res = Math.min(res, cur.dist);
                continue;
            }
            for (int i = 0; i < cur.conn.size(); ++i) {
                Node nd = cur.conn.get(i);
                if (cur.weight.get(i) == 0 && cur.usedPortals < k) {
                    pq.add(new Node(nd, cur.usedPortals + 1, cur.dist));
                } else if (cur.weight.get(i) != 0 && cur.weight.get(i) + cur.dist < nd.dist) {
                    pq.add(new Node(nd, cur.usedPortals, cur.weight.get(i) + cur.dist));
                }
            }
        }

        out.println(res);
    }
}

class QReader {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private StringTokenizer tokenizer = new StringTokenizer("");

    private String innerNextLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean hasNext() {
        while (!tokenizer.hasMoreTokens()) {
            String nextLine = innerNextLine();
            if (nextLine == null) {
                return false;
            }
            tokenizer = new StringTokenizer(nextLine);
        }
        return true;
    }

    public String nextLine() {
        tokenizer = new StringTokenizer("");
        return innerNextLine();
    }

    public String next() {
        hasNext();
        return tokenizer.nextToken();
    }

    public int nextInt() {
        return Integer.parseInt(next());
    }

    public long nextLong() {
        return Long.parseLong(next());
    }
}

class QWriter implements Closeable {
    private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

    public void print(Object object) {
        try {
            writer.write(object.toString());
        } catch (IOException e) {
            return;
        }
    }

    public void println(Object object) {
        try {
            writer.write(object.toString());
            writer.write("\n");
        } catch (IOException e) {
            return;
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            return;
        }
    }
}
