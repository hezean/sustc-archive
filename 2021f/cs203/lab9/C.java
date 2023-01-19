import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

class Node {
    char vis = 0;
    ArrayList<Node> conn = new ArrayList<>();
}

public class C {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    static Node[] nds;
    static boolean cycle = false;

    public static void main(String[] args) {
        int n = sc.nextInt(), m = sc.nextInt();
        nds = new Node[n];
        for (int i = 0; i < n; ++i) {
            nds[i] = new Node();
        }
        for (int i = 0; i < m; ++i) {
            int x = sc.nextInt() - 1, y = sc.nextInt() - 1;
            nds[x].conn.add(nds[y]);
            nds[y].conn.add(nds[x]);
        }
        for (int i = 0; !cycle && i < n; ++i) {
            dfs(nds[i], null);
        }
        out.print(cycle ? "Bad" : "Good");
        out.close();
    }

    static void dfs(Node st, Node prev) {
        if (st.vis == 2) {
            return;
        }
        st.vis = 1;
        for (Node c : st.conn) {
            if (c.vis == 0) {
                c.vis = 1;
                dfs(c, st);
            } else if (c != prev) {
                cycle = true;
                return;
            }
        }
        st.vis = 2;
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
