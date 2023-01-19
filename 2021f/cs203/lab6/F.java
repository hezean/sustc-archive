import java.io.*;
import java.util.StringTokenizer;

public class Main {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        char[] decodeMap = new char[26];
        for (int i = 0; i < 26; i++) decodeMap[sc.next().charAt(0) - 'a'] = (char) ('a' + i);
        String s = sc.next();
        char[] decoded = new char[s.length()];
        for (int i = 0; i < s.length(); i++)
            decoded[i] = decodeMap[s.charAt(i) - 'a'];

        out.print(s.length() - chkPattern(s, new String(decoded)));
        out.close();
    }

static int chkPattern(String a, String b) {
    int[] next = genNext(b);
    int i = 0, j = 0;
    while (i < a.length() && j < b.length()) {
        while (j > 0 && a.charAt(i) != b.charAt(j)) j = next[j - 1];
        if (a.charAt(i++) == b.charAt(j)) j++;
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
