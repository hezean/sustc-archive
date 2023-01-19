import java.io.*;
import java.util.*;

public class A {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static int MOD = 1000000007;

    public static void main(String[] args) {
        int n = sc.nextInt(), m = sc.nextInt();

        if (n == 1) {
            out.print(1);
            out.close();
            return;
        }

        Node[] nds = new Node[n];
        for (int i = 0; i < n; i++) {
            nds[i] = new Node();
        }

        for (int i = 0; i < m; i++) {
            int x = sc.nextInt() - 1, y = sc.nextInt() - 1;
            nds[x].out.add(nds[y]);
            nds[y].in.add(nds[x]);
        }

        for (int i = 0; i < n; i++) {
            nds[i].mkIn = nds[i].in.size();
            nds[i].mkOut = nds[i].out.size();
        }

        Queue<Node> q = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (nds[i].mkIn == 0) {
                q.add(nds[i]);
                nds[i].inv = 1;
            }
        }

        while (!q.isEmpty()) {
            Node nd = q.poll();
            for (Node d : nd.out) {
                --d.mkIn;
                d.inv = (d.inv + (nd.inv % MOD)) % MOD;
                if (d.mkIn == 0) {
                    q.add(d);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (nds[i].mkOut == 0) {
                q.add(nds[i]);
                nds[i].rev = 1;
            }
        }

        while (!q.isEmpty()) {
            Node nd = q.poll();
            for (Node d : nd.in) {
                --d.mkOut;
                d.rev = (d.rev + (nd.rev % MOD)) % MOD;
                if (d.mkOut == 0) {
                    q.add(d);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            out.print(((long) nds[i].inv * nds[i].rev) % MOD);
            out.print(" ");
        }
        out.close();
    }
}

class Node {
    int inv, rev;
    int mkIn, mkOut;
    List<Node> in = new LinkedList<>();
    List<Node> out = new LinkedList<>();
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