// import java.io.*;
// import java.util.*;

// public class Main {
//    static QReader sc = new QReader();
//    static QWriter out = new QWriter();

//    public static void main(String[] args) {
//        int sz = sc.nextInt();
//        Tree t = new Tree(sz);
//        out.print(t.bfs());
//        out.close();
//    }
// }

// class Tree {
//    int[] conn;

//    Tree(int sz) {
//        conn = new int[sz];
//        for (int i = 0; i < sz - 1; i++) {
//            int x = Main.sc.nextInt() - 1;
//            int y = Main.sc.nextInt() - 1;
//            conn[x]++;
//            conn[y]++;
//        }
//    }

//    long bfs() {
//        long ans = 1;
//        for (int j = 1; j <= conn[0]; j++) {
//            ans *= j;
//            ans %= 998244353;
//        }
//        for (int i = 1; i < conn.length; i++) {
//            int sz = conn[i] - 1;
//            for (int j = 1; j <= sz; j++) {
//                ans *= j;
//                ans %= 998244353;
//            }
//        }
//        return ans;
//    }
// }

// class QReader {
//    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//    private StringTokenizer tokenizer = new StringTokenizer("");

//    private String innerNextLine() {
//        try {
//            return reader.readLine();
//        } catch (IOException e) {
//            return null;
//        }
//    }

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

//    public String nextLine() {
//        tokenizer = new StringTokenizer("");
//        return innerNextLine();
//    }

//    public String next() {
//        hasNext();
//        return tokenizer.nextToken();
//    }

//    public int nextInt() {
//        return Integer.parseInt(next());
//    }

//    public long nextLong() {
//        return Long.parseLong(next());
//    }
// }

// class QWriter implements Closeable {
//    private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

//    public void print(Object object) {
//        try {
//            writer.write(object.toString());
//        } catch (IOException e) {
//            return;
//        }
//    }

//    public void println(Object object) {
//        try {
//            writer.write(object.toString());
//            writer.write("\n");
//        } catch (IOException e) {
//            return;
//        }
//    }

//    @Override
//    public void close() {
//        try {
//            writer.close();
//        } catch (IOException e) {
//            return;
//        }
//    }
// }
