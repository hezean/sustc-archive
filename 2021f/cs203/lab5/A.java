import java.io.*;
import java.util.StringTokenizer;

public class A {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        int T = sc.nextInt();
        Stack food = new Stack(T);
        Queue comer = new Queue(T * T);
        String sel;
        while (T-- > 0) {
            sel = sc.next();
            if (sel.equals("NewComer")) {
                comer.enqueue(sc.next());
            } else if (sel.equals("NewFood")) {
                food.push(sc.next());
            } else {
                if (food.empty() || comer.empty())
                    continue;
                if (food.top().equals(comer.head())) {
                    food.pop();
                    comer.dequeue();
                } else
                    comer.enqueue(comer.dequeue());
            }
        }
        boolean flag = true;
        while (flag) {
            int nSize = comer.size();
            flag = false;
            for (int i = 0; i < nSize; i++) {
                if (food.top().equals(comer.head())) {
                    food.pop();
                    comer.dequeue();
                    flag = true;
                } else
                    comer.enqueue(comer.dequeue());
            }
        }
        if (comer.empty())
            out.println("Qi Fei!");
        else
            out.println(comer.size());
        out.close();
    }
}

class Stack {
    String[] cs;
    int len = 0;
    Stack(int n) { this.cs = new String[n]; }

    void push(String s) { cs[len++] = s; }
    String pop() {
        if (empty()) return null;
        return cs[--len];
    }

    boolean empty() { return len == 0; }
    String top() {
        if (empty()) return null;
        return cs[len - 1];
    }
}

class Queue {
    String[] ss;
    int front = 0, rear = 0;

    Queue(int n) {
        this.ss = new String[n];
    }

    void enqueue(String s) {
        ss[rear++] = s;
    }

    String dequeue() {
        if (!empty())
            return ss[front++];
        else
            return null;
    }

    boolean empty() {
        return size() <= 0;
    }

    String head() {
        return ss[front];
    }

    int size() {
        return rear - front;
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
