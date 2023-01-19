import java.io.*;
import java.util.ArrayList;

class Node {
    boolean vis;
    int weight;
    ArrayList<Edge> conn = new ArrayList<>(6);

    Node(int w) {
        weight = w;
    }
}

class Edge {
    Node from, to;
    long weight;

    Edge(Node f, Node t) {
        from = f;
        to = t;
        weight = (long) f.weight * t.weight;
    }
}

class MinPQ {
    Edge[] pq;
    int n;

    MinPQ(int cap) {
        pq = new Edge[cap * 5];
        n = 1;
    }

    void add(Edge nd) {
        pq[n++] = nd;
        int i = n - 1;
        while (i > 1 && pq[i].weight < pq[i / 2].weight) {
            Edge tmp = pq[i];
            pq[i] = pq[i / 2];
            pq[i / 2] = tmp;
            i = i / 2;
        }
    }

    Edge __pop() {
        if (n < 1) {
            return null;
        }
        Edge tmp = pq[1];
        pq[1] = pq[--n];
        int i = 1;
        while (i * 2 < n) {
            int j = i * 2;
            if (j + 1 < n && pq[j + 1].weight < pq[j].weight) {
                j++;
            }
            if (pq[j].weight >= pq[i].weight) {
                break;
            }
            Edge tmp2 = pq[j];
            pq[j] = pq[i];
            pq[i] = tmp2;
            i = j;
        }
        return tmp;
    }

    Edge pop() {
        Edge tmp = __pop();
        while (tmp != null && tmp.from.vis && tmp.to.vis) {
            tmp = __pop();
        }
        if (tmp == null) {
            return null;
        }
        tmp.from.vis = tmp.to.vis = true;
        return tmp;
    }
}

public class Main {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static long sum = 0;

    public static void main(String[] args) throws IOException {
        int n = sc.nextInt();
        Node[][] msp = new Node[2 * n + 1][];
        for (int i = 0; i < 2 * n + 1; i++) {
            int sz = 2 * n + 1 - Math.abs(i - n);
            msp[i] = new Node[sz];
            msp[i][0] = new Node(sc.nextInt());
            for (int j = 1; j < sz; j++) {
                msp[i][j] = new Node(sc.nextInt());
                Edge e = new Edge(msp[i][j - 1], msp[i][j]);
                msp[i][j - 1].conn.add(e);
                msp[i][j].conn.add(e);
            }
            if (i > 0 && i <= n) { // 上半部分 跨行连接
                for (int j = 1; j < sz; j++) {
                    Edge e = new Edge(msp[i][j], msp[i - 1][j - 1]);
                    msp[i][j].conn.add(e);
                    msp[i - 1][j - 1].conn.add(e);
                }
                for (int j = 0; j < sz - 1; j++) {
                    Edge e = new Edge(msp[i][j], msp[i - 1][j]);
                    msp[i][j].conn.add(e);
                    msp[i - 1][j].conn.add(e);
                }
            } else if (i > n) {
                for (int j = 0; j < sz; j++) {
                    Edge e1 = new Edge(msp[i][j], msp[i - 1][j]);
                    msp[i][j].conn.add(e1);
                    msp[i - 1][j].conn.add(e1);
                    Edge e2 = new Edge(msp[i][j], msp[i - 1][j + 1]);
                    msp[i][j].conn.add(e2);
                    msp[i - 1][j + 1].conn.add(e2);
                }
            }
        } // 建图 -> Prim
        MinPQ pq = new MinPQ(9 * n * n + 3 * n);
        for (Edge e : msp[0][0].conn) {
            pq.add(e);
        }
        msp[0][0].vis = true;

        for (int i = 0; i < 3 * n * n + 3 * n; i++) {
            Edge e = pq.pop();

            sum += e.weight;
            for (Edge et : e.from.conn) {
                if (!et.from.vis || !et.to.vis) {
                    pq.add(et);
                }
            }
            for (Edge et : e.to.conn) {
                if (!et.from.vis || !et.to.vis) {
                    pq.add(et);
                }
            }
        }
        out.print(sum);
        out.close();
    }
}

class QReader {
    StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));

    int nextInt() throws IOException {
        st.nextToken();
        return (int) st.nval;
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
