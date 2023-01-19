//import java.io.*;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.StringTokenizer;
//
//public class A {
//    static QReader sc = new QReader();
//    static QWriter out = new QWriter();
//    static Node[] nds = new Node[300001];
//    static long[][] opt = new long[300001][2];
//
//    static {
//        for (int i = 0; i < nds.length; i++) {
//            nds[i] = new Node(i);
//        }
//    }
//
//    public static void main(String[] args) {
//        int n = sc.nextInt();
//        for (int i = 1; i < n; i++) {
//            int u = sc.nextInt(), v = sc.nextInt();
//            long w = sc.nextInt();
//            nds[u].conn.add(nds[v]);
//            nds[v].conn.add(nds[u]);
//            nds[u].weight.add(w);
//            nds[v].weight.add(w);
//        }
//
//        nds[1].vis = true;
//        dfs(nds[1]);
//
//        out.print(opt[1][1]);
//        out.close();
//    }
//
//    static void dfs(Node n) {
//        long res = 0;
//        for (int i = 0, size = n.conn.size(); i < size; i++) {
//            Node tar = n.conn.get(i);
//            if (tar.vis) continue;
//            tar.vis = true;
//            dfs(tar);
//            opt[n.id][0] += opt[tar.id][1];
//            res = Math.max(res, n.weight.get(i) + opt[tar.id][0] - opt[tar.id][1]);
//        }
//        opt[n.id][1] = opt[n.id][0] + res;
//    }
//}
//
//class Node {
//    List<Node> conn = new LinkedList<>();
//    List<Long> weight = new LinkedList<>();
//
//    int id;
//    boolean vis;
//
//    public Node(int id) {
//        this.id = id;
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
