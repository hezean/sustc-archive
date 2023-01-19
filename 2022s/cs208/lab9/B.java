// import java.io.*;
// import java.util.*;

// public class B {
//    static QReader sc = new QReader();
//    static QWriter out = new QWriter();
//    static final int MOD = (int) (1e9 + 7);

//    public static void main(String[] args) {
//        int n = sc.nextInt(), m = sc.nextInt();
//        Matrix helpOPT = new Matrix(m);
//        helpOPT.data[0][0] = 1;
//        for (int i = 1; i < m; i++) {
//            helpOPT.data[i][0] = 1;
//            helpOPT.data[i - 1][i] = 1;
//        }
//        Matrix res = helpOPT.powOf(n);

//        long sum = 0;
//        for (int i = 0; i < m; i++) {
//            sum += res.data[0][i];
//            sum %= MOD;
//        }

//        out.print(sum);
//        out.close();
//    }
// }

// class Matrix {
//    static final long MOD = (long) (1e9 + 7);
//    long[][] data;

//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        for (long[] r : this.data) {
//            sb.append(Arrays.toString(r))
//                    .append('\n');
//        }
//        return sb.toString();
//    }

//    Matrix(int n) {
//        this.data = new long[n][n];
//    }

//    void clear() {
//        for (long[] r : data) Arrays.fill(r, 0);
//    }

//    Matrix mulBy(Matrix op) {
//        int n = this.data.length;
//        Matrix res = new Matrix(n);
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < n; j++) {
//                long tmp = 0;
//                for (int k = 0; k < n; k++) {
//                    tmp += (this.data[i][k] * op.data[k][j]) % MOD;
//                    tmp %= MOD;
//                }
//                res.data[i][j] = tmp;
//            }
//        }
//        return res;
//    }

//    /**
//     * Can only call once for a matrix, since it changes the original data.
//     */
//    Matrix powOf(int n) {
//        Matrix res = new Matrix(this.data.length);
//        for (int i = 0; i < this.data.length; i++) {
//            res.data[i][i] = 1;
//        }
//        while (n > 0) {
//            if ((n & 1) == 1) {
//                res = this.mulBy(res);
//            }
//            this.data = this.mulBy(this).data;
//            n >>= 1;
//        }
//        return res;
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

//    public double nextDouble() {
//        return Double.parseDouble(next());
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
//        } catch (IOException ignored) {
//        }
//    }

//    public void println(Object object) {
//        try {
//            writer.write(object.toString());
//            writer.write("\n");
//        } catch (IOException ignored) {
//        }
//    }

//    @Override
//    public void close() {
//        try {
//            writer.close();
//        } catch (IOException ignored) {
//        }
//    }
// }
