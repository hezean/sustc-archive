import java.io.*;
import java.util.*;

public class A {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static final long MOD = (long) (1e9 + 7);

    public static void main(String[] args) {
        int n = sc.nextInt(), m = sc.nextInt();
        if (n <= 1 || m <= 1) {
            out.print(1);
            out.close();
            return;
        }

        int nestO3 = (Math.min(m, n) + 1) / 2;
        long[][] opt = new long[nestO3 + 1][Math.max(m, n) + 1];

        long[] oneRoundPrefSum = new long[Math.max(m, n) + 1];
        long[] oneRoundPSumMul = new long[Math.max(m, n) + 1];

        int innerJ = Math.max(m, n) + 1;
        Arrays.fill(opt[1], 1);

        for (int i = 2; i < opt.length; i++) {
            long sum = opt[i - 1][1] % MOD, sumMul = opt[i - 1][1] % MOD;
            oneRoundPrefSum[1] = sum;
            oneRoundPSumMul[1] = sumMul;

            for (int j = 2; j < innerJ; j++) {
                sum += opt[i - 1][j];
                sumMul += opt[i - 1][j] * j % MOD;
                sum %= MOD;
                sumMul %= MOD;

                oneRoundPrefSum[j] = sum;
                oneRoundPSumMul[j] = sumMul;

                opt[i][j] = ((j - 1) * oneRoundPrefSum[j - 2] - oneRoundPSumMul[j - 2]) % MOD;
            }
        }

        long res = 0;
        for (int i = 1; i <= nestO3; i++) {
            res += opt[i][n] * opt[i][m] % MOD;
            res %= MOD;
        }
        if (res < 0) {
            res += MOD;
        }
        out.print(res);
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
