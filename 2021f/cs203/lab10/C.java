import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

class Edge {
    Node from, to;
    int weight;

    Edge(Node f, Node t, int w) {
        from = f;
        to = t;
        weight = w;
    }
}

class Node {
    boolean vis;
    ArrayList<Edge> conn = new ArrayList<>();
}

class MinPQ {
    Edge[] pq;
    int n;

    MinPQ(int cap) {
        pq = new Edge[cap * 3];
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


public class C {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    static Node[] nds;
    static long sum = 0;

    public static void main(String[] args) throws IOException {
        int n = sc.nextInt(), m = sc.nextInt();
        MinPQ pq = new MinPQ(m);
        nds = new Node[n];
        for (int i = 0; i < n; i++) {
            nds[i] = new Node();
        }
        for (int i = 0; i < m; i++) {
            Edge e = new Edge(nds[sc.nextInt() - 1], nds[sc.nextInt() - 1], sc.nextInt());
            e.from.conn.add(e);
            e.to.conn.add(e);
        }
        for (Edge e : nds[0].conn) {
            pq.add(e);
        }

        for (int i = 0; i < n - 1; i++) {
            Edge e = pq.pop();
            if (e == null) {
                break;
            }
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
            sum += e.weight;
        }
        out.print(sum);
        out.close();
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
