import java.io.*;
import java.util.*;

public class B {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        int n = sc.nextInt(), m = sc.nextInt(), c = sc.nextInt(), t = sc.nextInt();
        int[] p = new int[n], q = new int[m], used = new int[m];

        for (int i = 0; i < n; i++) p[i] = sc.nextInt();
        for (int i = 0; i < m; i++) q[i] = sc.nextInt();
        Arrays.sort(p);
        Arrays.sort(q);

        int posHole = 0;
        for (int i = 0; i < n; i++) {
            if (posHole >= m) break;
            while (posHole < m) {
                if (q[posHole] < p[i] - t) {  // still cannot reach, too left
                    ++posHole;
                } else if (q[posHole] > p[i] + t) {
                    break;  // next bunny
                } else {
                    if (used[posHole] >= c) {
                        ++posHole;
                        continue;
                    }
                    used[posHole]++;  // pull the damn bunny into hole
                    break;
                }
            }
        }

        int alive = 0;
        for (int r : used) {
            alive += r;
        }
        out.print(alive);
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

    public long nextLong() {
        return Long.parseLong(next());
    }
}

class QWriter implements Closeable {
    private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

    public void print(Object object) {
        try {
            writer.write(object.toString());
        } catch (IOException e) {
            return;
        }
    }

    public void println(Object object) {
        try {
            writer.write(object.toString());
            writer.write("\n");
        } catch (IOException e) {
            return;
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            return;
        }
    }
}
