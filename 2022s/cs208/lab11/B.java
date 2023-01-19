import java.io.*;
import java.util.StringTokenizer;

public class B {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static final long MOD = (long) (1e9 + 7);
    static boolean[][] dep = new boolean[20][20];
    static long[] dp = new long[1 << 20 + 1];

    public static void main(String[] args) {
        int n = sc.nextInt(), m = sc.nextInt(), len = 1 << n;
        for (int i = 0; i < m; i++) dep[sc.nextInt() - 1][sc.nextInt() - 1] = true;

        dp[0] = 1;
        for (int i = 1; i < len; i <<= 1) dp[i] = 1;

        // iter2 --> iter(2^n - 1), each iter takes O(n), i = # -1
        for (int i = 1; i < len - 1; i++) {
            for (int b = 0; b < n; b++) {
                if (((i >> b) & 1) == 1) continue;  // first find a place to insert
                int comb = 0;
                for (int p = 0; p < n; p++) {
                    if (((i >> p) & 1) == 1 && dep[b][p]) comb++;
                }
                dp[i + (1 << b)] += dp[i] * (1L << comb);
                dp[i + (1 << b)] %= MOD;
            }
        }

        out.print(dp[len - 1]);
        out.close();
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

    public double nextDouble() {
        return Double.parseDouble(next());
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
