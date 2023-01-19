import java.io.*;
import java.util.*;

public class B {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static int[] songs = new int[1001];

    public static void main(String[] args) {
        int n = sc.nextInt(), fi = 1;

        // squish all neighbourhoods with the same color into one big item
        songs[0] = Integer.MIN_VALUE;
        for (int i = 1; i <= n; i++) {
            int c = sc.nextInt();
            if (songs[fi - 1] != c) songs[fi++] = c;
        }
        fi--;

        int[][] opt = new int[fi + 10][fi + 10];
        for (int cb = 2; cb <= fi; cb++) {  // column bias
            for (int c = cb; c <= fi; c++) {  // col, opt[col+cb][col]
                int r = c - cb + 1;
                if (songs[r] == songs[c]) {
                    set(opt, r, c, get(opt, r + 1, c - 1) + 1);
                } else {
                    set(opt, r, c, Math.min(get(opt, r + 1, c), get(opt, r, c - 1)) + 1);
                }
            }
        }

//        for (int[] c : opt) {
//            System.out.println(Arrays.toString(c));
//        }

        out.print(opt[1][fi]);
        out.close();
    }

    static void set(int[][] opt, int i, int j, int val) {
        if (i >= opt.length || j < 0 || j >= opt.length) return;
        opt[i][j] = val;
    }

    static int get(int[][] opt, int i, int j) {
        if (i >= opt.length || j < 0 || j >= opt.length) return 0;
        return opt[i][j];
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
