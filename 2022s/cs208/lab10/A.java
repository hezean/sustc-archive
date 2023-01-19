//import java.io.*;
//import java.util.*;
//
//public class A {
//    static QReader sc = new QReader();
//    static QWriter out = new QWriter();
//    final static long MOD = (long) 1e9 + 7;
//    final static int[] colA = new int[5001];
//
//    final static long[][] opt = new long[2][5001];
//    static int idxIsNow = 0;
//
//    public static void main(String[] args) {
//        int n = sc.nextInt();
//        int l = sc.nextInt(), r = sc.nextInt();
//
//        int sum = 0;  // max(sum-r, l) <= x1 <= min(sum-l, r)
//        for (int i = 0; i < n; i++) {
//            int num = sc.nextInt();
//            colA[num]++;
//            sum += num;
//        }
//        int lo = Math.max(sum - r, l), hi = Math.min(sum - l, r);
//
//        opt[idxIsNow++][0] = 1;
//
//        for (int i = 1; i <= hi; i++) {
//            long[] thisOPT = opt[idxIsNow];
//            long[] lastOPT = opt[1 - idxIsNow];
//            idxIsNow = (idxIsNow + 1) % 2;
//
//            for (int w = 0; w <= 5000; w++) {
//                if (w < i) {
//                    thisOPT[w] = lastOPT[w];
//                } else {
//                    long tmp = lastOPT[w];
//                    for (int k = 1; k <= colA[i]; k++) {
//                        int q = w - k * i;
//                        if (q >= 0) {
//                            tmp = (tmp + lastOPT[q]) % MOD;
//                        }
//                    }
//                    thisOPT[w] = tmp;
//                }
//            }
//        }
//
//        long res = 0;
//        for (int i = lo; i <= hi; i++) {
//            res += opt[idxIsNow][i];
//            res %= MOD;
//        }
//
//        out.print(res);
//        out.close();
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
