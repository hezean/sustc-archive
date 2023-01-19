import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

class Node {
    int id;
    boolean out;
    long dist = Long.MAX_VALUE;
    ArrayList<Node> conn = new ArrayList<>();
    ArrayList<Integer> weight = new ArrayList<>();

    Node(int i) {
        id = i;
    }
}

class MinPQ {
    ArrayList<Node> pq;
    int n;

    MinPQ(int cap) {
        pq = new ArrayList<>(cap + 1);
        pq.add(new Node(-1));
        n = 1;
    }

    void add(Node nd) {
        pq.add(n++, nd);
        int i = n - 1;
        while (i > 1 && pq.get(i).dist < pq.get(i / 2).dist) {
            Node tmp = pq.get(i);
            pq.set(i, pq.get(i / 2));
            pq.set(i / 2, tmp);
            i = i / 2;
        }
    }

    Node __pop() {
        if (n < 1) {
            return null;
        }
        Node tmp = pq.get(1);
        pq.set(1, pq.get(--n));
        int i = 1;
        while (i * 2 < n) {
            int j = i * 2;
            if (j + 1 < n && pq.get(j + 1).dist < pq.get(j).dist) {
                j++;
            }
            if (pq.get(j).dist >= pq.get(i).dist) {
                break;
            }
            Node tmp2 = pq.get(j);
            pq.set(j, pq.get(i));
            pq.set(i, tmp2);
            i = j;
        }
        return tmp;
    }

    Node pop() {
        Node tmp = __pop();
        while (tmp != null && tmp.out) {
            tmp = __pop();
        }
        if (tmp == null) {
            return null;
        }
        tmp.out = true;
        return tmp;
    }
}

public class Main {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static Node[] msp;

    public static void main(String[] args) {
        int n = sc.nextInt(), m = sc.nextInt();
        msp = new Node[n];
        for (int i = 0; i < n; ++i) {
            msp[i] = new Node(i);
        }

        for (int i = 0; i < m; ++i) {
            int u = sc.nextInt(), v = sc.nextInt(), w = sc.nextInt();
            msp[u - 1].conn.add(msp[v - 1]);
            msp[u - 1].weight.add(w);
        }
        dijkstra();
        if (msp[n - 1].dist == Long.MAX_VALUE) {
            out.println(-1);
        } else {
            out.println(msp[n - 1].dist);
        }
        out.close();

    }

    static void dijkstra() {
        MinPQ pq = new MinPQ(msp.length);
        msp[0].dist = 0;
        pq.add(msp[0]);

        while (pq.n > 1) {
            Node tmp = pq.pop();
            if (tmp == null) {
                continue;
            }
            Node cur = msp[tmp.id];
            for (int i = 0; i < cur.conn.size(); i++) {
                Node tar = cur.conn.get(i);
                if (!tar.out && tar.dist > tmp.dist + cur.weight.get(i)) {
                    tar.dist = tmp.dist + cur.weight.get(i);
                    pq.add(tar);
                }
            }
        }

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
