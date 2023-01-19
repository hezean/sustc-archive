import java.io.*;
import java.util.StringTokenizer;

public class B {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        String pat = sc.next();
        int[][] fsa = new int[26][pat.length() + 1];
        fsa[pat.charAt(0) - 'a'][0] = 1;
        for (int x = 0, j = 1; j < pat.length(); j++) {
            for (int c = 0; c < 26; c++)
                fsa[c][j] = fsa[c][x];
            fsa[pat.charAt(j) - 'a'][j] = j + 1;
            x = fsa[pat.charAt(j) - 'a'][x]; // update the restart state
        }
        for (int i = 0; i < pat.length(); i++) {
            for (int c = 0; c < 26; c++)
                out.print(fsa[c][i] + " ");
            out.println("");
        }
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
