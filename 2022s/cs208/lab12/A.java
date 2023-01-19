import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class A {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    static int[] node_lv = new int[100];
    static boolean[] node_vis = new boolean[100];
    static List<Integer>[] node_conn = new List[100];  // edge idx

    static int[] edge_to = new int[5000];
    static long[] edge_cap = new long[5000];
    static long[] edge_flow = new long[5000];

    static int edge_fi = 0;

    static {
        for (int i = 0; i < 100; i++) {
            node_conn[i] = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        int n = sc.nextInt(), m = sc.nextInt(), s = sc.nextInt() - 1, t = sc.nextInt() - 1;

        for (int i = 0; i < m; i++) {
            int u = sc.nextInt() - 1, v = sc.nextInt() - 1;
            long c = sc.nextLong();

            edge_cap[edge_fi] = c;
            edge_to[edge_fi] = v;
            node_conn[u].add(edge_fi++);
        }

        out.print(dinic(s, t));
        out.close();
    }

    static boolean bfs(int s, int t) {
        Arrays.fill(node_vis, false);
        Queue<Integer> q = new LinkedList<>();
        q.add(s);
        node_vis[s] = true;
        node_lv[s] = 0;
        while (!q.isEmpty()) {
            int n = q.poll();
            for (int e : node_conn[n]) {
                int to = edge_to[e];
                if (edge_flow[e] < edge_cap[e] && !node_vis[to]) {
                    node_lv[to] = node_lv[n] + 1;
                    q.add(to);
                    node_vis[to] = true;
                }
            }
        }
        return node_vis[t];
    }

    static long dfs(int s, int t, long currBottleNeck) {
        if (s == t) return currBottleNeck;
        for (int e : node_conn[s]) {
            int to = edge_to[e];
            if (edge_flow[e] < edge_cap[e] && node_lv[s] + 1 == node_lv[to]) {
                long nextButtonNeck = dfs(to, t, Math.min(currBottleNeck, edge_cap[e] - edge_flow[e]));
                if (nextButtonNeck > 0) {
                    edge_flow[e] += nextButtonNeck;
                    return nextButtonNeck;
                }
            }
        }
        return 0;
    }

    static long dinic(int s, int t) {
        long f = 0;
        while (bfs(s, t)) {
            while (true) {
                long tmp = dfs(s, t, Long.MAX_VALUE);
                if (tmp <= 0) break;
                f += tmp;
            }
        }
        return f;
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
