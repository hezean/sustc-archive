import java.io.*;
import java.util.StringTokenizer;

public class C {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        Stack s = new Stack(30000);
        int T = sc.nextInt();
        loop:
        while (T-- > 0) {
            s.clear();
            int n = sc.nextInt();
            if (n % 2 != 0) {
                out.println("NO");
                sc.next();
                continue;
            }
            String str = sc.next();
            for (int i = 0; i < n; i++) {
                char c = str.charAt(i);
                switch (c) {
                    case '(':
                    case '[':
                    case '{':
                        s.push(c);
                        break;
                    case ')':
                        if (s.pop() != '(') {
                            out.println("NO");
                            continue loop;
                        }
                        break;
                    case ']':
                        if (s.pop() != '[') {
                            out.println("NO");
                            continue loop;
                        }
                        break;
                    case '}':
                        if (s.pop() != '{') {
                            out.println("NO");
                            continue loop;
                        }
                        break;
                }
            }
            out.println(s.empty() ? "YES" : "NO");
        }
        out.close();
    }
}

class Stack {
    char[] cs;
    int len = 0;

    Stack(int n) {
        this.cs = new char[n];
    }

    void push(char c) {
        cs[len++] = c;
    }

    boolean empty() {
        return len == 0;
    }

    char pop() {
        if (empty()) return 0;
        return cs[--len];
    }

    void clear() {
        this.len = 0;
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
