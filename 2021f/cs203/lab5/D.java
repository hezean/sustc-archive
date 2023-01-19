import java.io.*;
import java.util.StringTokenizer;

public class Main {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        String s = sc.next();
        Stack ans = new Stack(s.length() / 2);
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(')
                ans.push();
            else
                ans.pop();
        }
        out.print(ans.score[0] % 514329L);
        out.close();
    }
}

class Stack {
    long[] score;
    int depth = 1;

    Stack(int sz) {
        score = new long[sz + 1];
    }

    void push() {
        score[depth++] = 0;
    }

    void pop() {
        depth--;
        if (score[depth] == 0L)
            score[depth - 1] = (score[depth - 1] + 1) % 514329L;
        else
            score[depth - 1] += (2L * score[depth]) % 514329L;
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
