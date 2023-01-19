import java.io.*;
import java.util.*;

public class B {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static Node[] msp;
    static int[] cnt;

    public static void main(String[] args) {
        int n = sc.nextInt(), m = sc.nextInt();
        msp = new Node[n];
        cnt = new int[m];
        for (int i = 0; i < n; ++i) {
            msp[i] = new Node(i);
        }

        for (int i = 0; i < m; ++i) {
            int u = sc.nextInt() - 1, v = sc.nextInt() - 1, w = sc.nextInt();
            msp[u].conn.add(msp[v]);
            msp[u].weight.add(w);
            msp[u].edgeId.add(i);
            msp[v].conn.add(msp[u]);
            msp[v].weight.add(w);
            msp[v].edgeId.add(i);
        }

        int p = sc.nextInt();
        for (int i = 0; i < p; i++) {
            Node st = msp[sc.nextInt() - 1], dst = msp[sc.nextInt() - 1];
            dijkstra(st);
            bfs(dst);

            for (int j = 0; j < n; j++) {
                msp[j].dist = Long.MAX_VALUE;
                msp[j].out = false;
                msp[j].vis = false;
            }
        }

        for (int c : cnt) out.println(c);
        out.close();
    }

    static void dijkstra(Node st) {
        PriorityQueue<Node> pq=new PriorityQueue<>(Comparator.comparingLong(o -> o.dist));
        st.dist = 0;
        pq.add(st);
        st.vis = true;

        while (!pq.isEmpty()) {
            Node tmp = pq.poll();
            tmp.vis = true;
            Node cur = msp[tmp.id];
            for (int i = 0; i < cur.conn.size(); i++) {
                Node tar = cur.conn.get(i);
                if (!tar.vis && tar.dist > tmp.dist + cur.weight.get(i)) {
                    pq.remove(tar);
                    tar.dist = tmp.dist + cur.weight.get(i);
                    pq.add(tar);
                }
            }
        }
    }

    static void bfs(Node dst) {
        for (Node n : msp) n.vis = false;
        Queue<Node> q = new LinkedList<>();
        q.add(dst);
        dst.vis = true;

        while (!q.isEmpty()) {
            Node h = q.poll();
            for (int i = 0; i < h.conn.size(); i++) {
                if (h.conn.get(i).dist + h.weight.get(i) == h.dist) {
                    cnt[h.edgeId.get(i)]++;
                    if (!h.conn.get(i).vis) {
                        h.conn.get(i).vis = true;
                        q.add(h.conn.get(i));
                    }
                }
            }
        }
    }
}

class Node {
    int id;
    boolean out, vis;
    long dist = Long.MAX_VALUE;
    List<Node> conn = new LinkedList<>();
    List<Integer> weight = new LinkedList<>();
    List<Integer> edgeId = new LinkedList<>();

    Node(int i) {
        id = i;
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
        } catch (IOException ignored) {
        }
    }

    public void println(Object object) {
        try {
            writer.write(object.toString());
            writer.write("\n");
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException ignored) {
        }
    }
}
