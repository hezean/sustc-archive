import java.io.*;
import java.util.StringTokenizer;

public class D {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        String a = sc.next(), b = sc.next();
        if (a.length() != b.length()) {
            out.print("No");
            out.close();
            return;
        } else if (a.equals(b)) {
            out.print("Yes");
            out.close();
            return;
        }
        int rightPaired = chkPattern(a, b);
        int leftPaired = chkPattern(b, a);
        out.print(leftPaired + rightPaired == a.length() ? "Yes" : "No");
        out.close();
    }

    static int chkPattern(String a, String b) {
        int[] next = genNext(b);
        int i = 0, j = 0;
        while (i < a.length() && j < b.length()) {
            if (a.charAt(i++) == b.charAt(j)) {
                j++;
            } else
                j = next[j];
        }
        return j; // j == b.length();
    }

    static int[] genNext(String pat) {
        int[] next = new int[pat.length()];
        int j = 1, k = 0;
        while (j < pat.length()) {
            if (pat.charAt(j) == pat.charAt(k))
                next[j++] = ++k;
            else if (k == 0)
                next[j++] = 0;
            else
                k = next[k - 1];
        }
        return next;
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
