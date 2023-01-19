import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

class Node {
    long len;
    boolean vis;
    ArrayList<Integer> conn = new ArrayList<>();
}

public class Main {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    static Node[] nds;

    public static void main(String[] args) {
        int n = sc.nextInt(), m = sc.nextInt();
        nds = new Node[n + m + 100];
        for (int i = 0; i < n + m + 100; ++i) {
            nds[i] = new Node();
        }
        int nu = n;
        for (int i = 0; i < m; ++i) {
            int x = sc.nextInt() - 1, y = sc.nextInt() - 1, z = sc.nextInt();
            if (z == 1) {
                nds[x].conn.add(y);
            } else {
                nds[x].conn.add(nu);
                nds[nu].conn.add(y);
                nu++;
            }
        }
        bfs();
        out.print(nds[n - 1].len == 0 ? "-1" : nds[n - 1].len);
        out.close();
    }

    static void bfs() {
        Queue<Node> q = new LinkedList<>();
        q.add(nds[0]);
        nds[0].vis = true;
        while (!q.isEmpty()) {
            Node cur = q.poll();
            for (int c : cur.conn) {
                if (!nds[c].vis) {
                    nds[c].vis = true;
                    nds[c].len = cur.len + 1;
                    q.add(nds[c]);
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
