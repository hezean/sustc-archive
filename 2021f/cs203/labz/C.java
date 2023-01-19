import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

class QRCode {
    boolean[] data;
    int missCnt;
}

public class C {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static int n, k;
    static Set<QRCode> cache = new HashSet<>();

    public static void main(String[] args) {
        n = sc.nextInt();
        k = sc.nextInt();
        for (int i = 0; i < n; i++) {
            Set<QRCode> term = new HashSet<>(cache);
            boolean[] tmp = new boolean[256];
            for (int j = 0; j < 256; j++) {
                boolean x = sc.nextInt() == 1;
                int finalJ = j;
                term.removeIf((QRCode c) -> c.data[finalJ] != x);
                tmp[j] = x;
            }

            for (QRCode c : cache)
                ++c.missCnt;

            if (!term.isEmpty()) {
                out.println("hit");
            } else {
                out.println("miss");
                QRCode qr = new QRCode();
                qr.data = tmp;
                cache.add(qr);
            }

            for (QRCode c : term)
                c.missCnt = 0;

            while (cache.size() > k) {
                int maxMiss = Integer.MIN_VALUE;
                QRCode mostMiss = null;
                for (QRCode c : cache) {
                    if (c.missCnt > maxMiss) {
                        mostMiss = c;
                        maxMiss = c.missCnt;
                    }
                }
                cache.remove(mostMiss);
            }
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
