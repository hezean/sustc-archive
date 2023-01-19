import java.io.*;
import java.util.*;

public class B {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    static long[] vst;
    static Node[] seg;

    public static void main(String[] args) {
        int n = sc.nextInt();
        vst = new long[n];
        for (int i = 0; i < n; i++) {
            vst[i] = sc.nextInt();
        }
        seg = new Node[n * 4];
        gen(0, n - 1, 1);  // 2-ary tree

        int q = sc.nextInt();
        for (int i = 0; i < q; i++) {
            out.println(query(sc.nextInt() - 1, sc.nextInt() - 1));
        }
        out.close();
    }

    static void gen(int l, int r, int f) {
        if (l == r) {
            seg[f] = new Node(vst[l]);
            return;
        }
        int mi = (l + r) >> 1;
        gen(l, mi, f * 2);
        gen(mi + 1, r, f * 2 + 1);
        seg[f] = new Node(seg[f * 2], seg[f * 2 + 1]);
    }

    static long query(int l, int r) {
        return query(l, r, 0, vst.length - 1, 1).max;
    }

    static Node query(int ql, int qr, int wl, int wr, int ni) {
        if (ql <= wl && wr <= qr) return seg[ni];
        int mi = (wl + wr) >> 1;
        if (mi < ql) {
            return query(ql, qr, mi + 1, wr, ni * 2 + 1);  // r-son
        } else if (mi >= qr) {
            return query(ql, qr, wl, mi, ni * 2);  // l-son
        } else {
            return new Node(query(ql, qr, wl, mi, ni * 2), query(ql, qr, mi + 1, wr, ni * 2 + 1));
        }
    }
}

class Node {
    long sum;
    long l, r, max;

    Node(long v) {
        this.sum = v;
        this.l = v;
        this.r = v;
        this.max = v;
    }

    Node(Node l, Node r) {
        this.sum = l.sum + r.sum;
        this.l = Math.max(l.l, l.sum + r.l);
        this.r = Math.max(r.r, r.sum + l.r);
        this.max = Math.max(Math.max(l.max, r.max), l.r + r.l);
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
