//import java.io.*;
//import java.util.StringTokenizer;
//
//public class B3 {
//    static QReader sc = new QReader();
//    static QWriter out = new QWriter();
//
//    public static void main(String[] args) {
//
//
//        out.close();
//    }
//
//    static void updateTheFuckingRightPartOfYourAss(Bitch theFemale, int leftButt, int rightButt) {
//        for (int i = leftButt; i <= rightButt; i++) {
//            theFemale.update(i, Long.MAX_VALUE);
//
//        }
//    }
//}
//
//class Bitch {
//    long[] c;
//    long[] a;
//
//    int length;
//
//    public Bitch(int n) {
//        this.c = new long[n + 1];
//        this.a = new long[n + 1];
//        this.length = n;
//    }
//
//    public void init() {
//        for (int i = 1; i <= length; i++) {
//            c[i] = a[i];
//            for (int j = 1; j < lowBit(i); j <<= 1) {
//                c[i] = Math.min(c[i], c[i - j]);
//            }
//        }
//    }
//
//    public void update(int pos, long val) {
//        a[pos] = val;
//        for (int i = pos; i <= length; i += lowBit(i)) {
//            c[pos] = val;
//            for (int j = 1; j < lowBit(i); j <<= 1) {
//                c[i] = Math.min(c[i], c[i - j]);
//            }
//        }
//    }
//
//    public long query(int l, int r) {
//        long res = Long.MAX_VALUE;
//        while (true) {
//            res = Math.min(res, a[r]);
//            if (r == l) break;
//            for (r -= 1; r - l >= lowBit(r); r -= lowBit(r)) {
//                res = Math.min(res, c[r]);
//            }
//        }
//        return res;
//    }
//
//    public static int lowBit(int x) {
//        return x & (-x);
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
