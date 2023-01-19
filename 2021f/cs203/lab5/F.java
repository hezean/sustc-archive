import java.io.*;
import java.util.StringTokenizer;

class Queue {
    int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
    int[] ele = new int[Main.n];
    int l, r;

    void enqueue(int x) {
        ele[r++] = x;
        max = Math.max(max, ele[r - 1]);
        min = Math.min(min, ele[r - 1]);
        if (Math.abs(max - min) > Main.k) {
            l++;
            min = Integer.MAX_VALUE;
            for (int i = l; i < r; i++)
                min = Math.min(min, ele[i]);
            max = Integer.MIN_VALUE;
            for (int i = l; i < r; i++)
                max = Math.max(max, ele[i]);
        }
    }
}

public class Main {
    static int k, n;
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        k = sc.nextInt();
        n = sc.nextInt();
        Queue window = new Queue();
        while (n-- > 0)
            window.enqueue(sc.nextInt());
        out.print(window.r - window.l);
        out.close();
    }
}

class QReader {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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
    private final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

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
