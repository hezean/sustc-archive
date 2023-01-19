import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

class Mp {
    char[] roads;
    int sz;

    Mp(int sz) {
        this.sz = sz;
        roads = new char[sz * sz];
    }

    void addRoad(int a, int b) { // a -> b, idx-1
        roads[a * sz + b]++;
    }

    void show(QWriter opt) {
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                opt.print((char)(roads[i * sz + j] + '0'));
                opt.print(' ');
            }
            opt.println("");
        }
    }
}

public class A {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        int t = sc.nextInt();
        while (t-- > 0) {
            int n = sc.nextInt();
            int m = sc.nextInt();

            Mp cty = new Mp(n);
            for (int i = 0; i < m; i++) {
                int a = sc.nextInt() - 1;
                int b = sc.nextInt() - 1;
                cty.addRoad(a, b);
            }
            cty.show(out);
        }
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
