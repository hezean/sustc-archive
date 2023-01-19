import java.io.*;
import java.util.*;

public class B {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static int[][] as;
    static int usedTime = 0;
    static long ans = 0;
    static double log2 = Math.log(2);

    public static void main(String[] args) {
        int n = sc.nextInt(), m = sc.nextInt();  // m sec max
        int logN = (int) (Math.log(n) / log2);

        as = new int[n + 1][logN + 1];  // st, i-th computer max time for use [0,e9] unique, 1 indexed
        Map<Integer, Integer> a2idx = new HashMap<>(n);

        for (int i = 1; i <= n; i++) {
            int val = sc.nextInt();
            as[i][0] = val;
            a2idx.put(val, i);
        }
        for (int j = 1; j <= logN; j++) {
            for (int i = 1; i + (1 << j) - 1 <= n; i++) {
                as[i][j] = Math.min(as[i][j - 1], as[i + (1 << (j - 1))][j - 1]);
            }
        }

        long[] bs = new long[n + 1];  // prefix sum of income/s [1,e4], 1 indexed
        bs[1] = sc.nextInt();
        for (int i = 2; i <= n; i++) bs[i] = bs[i - 1] + sc.nextInt();

        // each term, choose mini_a, set it as the property "base" and ready to dec
        PriorityQueue<Inv> pq = new PriorityQueue<>(Comparator.comparingLong(e -> -e.sum));
        pq.add(new Inv(1, n, bs[n], 0));


        while (!pq.isEmpty()) {
            if (usedTime >= m) break;
            Inv inv = pq.poll();

            int minTime = query(inv.left, inv.right);
            if (minTime < inv.baseTime) continue;

            long canUseTime = Math.min(m - usedTime, minTime - inv.baseTime);
            usedTime += canUseTime;
            ans += canUseTime * inv.sum;

            int idx = a2idx.get(minTime);
            if (idx > inv.left) pq.add(new Inv(inv.left, idx - 1, bs[idx - 1] - bs[inv.left - 1], minTime));
            if (idx < inv.right) pq.add(new Inv(idx + 1, inv.right, bs[inv.right] - bs[idx], minTime));
        }

        out.print(ans);
        out.close();
    }

    static int query(int from, int to) {
        int s = (int) (Math.log(to - from + 1) / log2);
        return Math.min(as[from][s], as[to - (1 << s) + 1][s]);
    }
}

class Inv {
    int left, right, baseTime;
    long sum;

    public Inv(int left, int right, long sum, int baseTime) {
        this.left = left;
        this.right = right;
        this.sum = sum;
        this.baseTime = baseTime;
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
