//import java.io.*;
//import java.util.*;
//
//public class B {
//    static QReader sc = new QReader();
//    static QWriter out = new QWriter();
//
//    public static void main(String[] args) {
//        int n = sc.nextInt(), q = sc.nextInt();
//        int lob = Integer.MAX_VALUE, hib = Integer.MIN_VALUE;
//        int[] a = new int[n];
//        for (int i = 0; i < n; i++) {
//            a[i] = sc.nextInt();
//            lob = Math.min(lob, a[i]);
//            hib = Math.max(hib, a[i]);
//        }
//
//        Node[] nds = new Node[n + 1];
//        nds[0] = new Node(lob, hib, 0);
//        nds[0].left = nds[0];
//        nds[0].right = nds[0];
//        for (int i = 1; i <= n; i++) {
//            nds[i] = Node.build(nds[i - 1], lob, hib, a[i - 1]);
//        }
//
//        for (int i = 0; i < q; i++) {
//            int l = sc.nextInt(), r = sc.nextInt();
//            out.println(Node.query(nds[l-1], nds[r], lob, hib, (r - l + 2) / 2));
//        }
//
//        out.close();
//    }
//
//}
//
//class Node {
//    int lo, hi;
//    int cnt;
//    Node left, right;
//
//    public Node(int lo, int hi, int cnt) {
//        this.lo = lo;
//        this.hi = hi;
//        this.cnt = cnt;
//        this.left = this.right = null;
//    }
//
//    public Node(Node src) {
//        if (src == null) return;
//        this.lo = src.lo;
//        this.hi = src.hi;
//        this.cnt = src.cnt;
//        this.left = src.left;
//        this.right = src.right;
//    }
//
//    static Node build(Node pre, long lo, long hi, int val) {
//        Node nd = new Node(pre);
//        nd.lo = (int) lo;
//        nd.hi = (int) hi;
//        ++nd.cnt;
//        if (lo == hi) return nd;
//
//        long mi = (lo + hi) / 2;
//        if (val <= mi) nd.left = build(pre.left, lo, mi, val);
//        else nd.right = build(pre.right, mi + 1, hi, val);
//
//        return nd;
//    }
//
//    static int query(Node leftRoot, Node rightRoot, long lo, long hi, int rk) {
//        if (lo == hi) return (int) lo;
//        long mi = (lo + hi) / 2;
//        int diff = rightRoot.left.cnt - leftRoot.left.cnt;
//
//        if (rk <= diff) return query(leftRoot.left, rightRoot.left, lo, mi, rk);
//        else return query(leftRoot.right, rightRoot.right, mi + 1, hi, rk - diff);
//    }
//
//    @Override
//    public String toString() {
//        return "Node{" +
//                "lo=" + lo +
//                ", hi=" + hi +
//                ", cnt=" + cnt +
//                '}';
//    }
//}
//
//class QReader {
//    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//    private StringTokenizer tokenizer = new StringTokenizer("");
//
//    private String innerNextLine() {
//        try {
//            return reader.readLine();
//        } catch (IOException e) {
//            return null;
//        }
//    }
//
//    public boolean hasNext() {
//        while (!tokenizer.hasMoreTokens()) {
//            String nextLine = innerNextLine();
//            if (nextLine == null) {
//                return false;
//            }
//            tokenizer = new StringTokenizer(nextLine);
//        }
//        return true;
//    }
//
//    public String nextLine() {
//        tokenizer = new StringTokenizer("");
//        return innerNextLine();
//    }
//
//    public String next() {
//        hasNext();
//        return tokenizer.nextToken();
//    }
//
//    public int nextInt() {
//        return Integer.parseInt(next());
//    }
//
//    public double nextDouble() {
//        return Double.parseDouble(next());
//    }
//
//    public long nextLong() {
//        return Long.parseLong(next());
//    }
//}
//
//class QWriter implements Closeable {
//    private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
//
//    public void print(Object object) {
//        try {
//            writer.write(object.toString());
//        } catch (IOException ignored) {
//        }
//    }
//
//    public void println(Object object) {
//        try {
//            writer.write(object.toString());
//            writer.write("\n");
//        } catch (IOException ignored) {
//        }
//    }
//
//    @Override
//    public void close() {
//        try {
//            writer.close();
//        } catch (IOException ignored) {
//        }
//    }
//}
