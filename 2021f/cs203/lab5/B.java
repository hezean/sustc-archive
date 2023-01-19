import java.io.*;
import java.util.StringTokenizer;

public class Main {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static int[] groupTime;
    static int time = 0;

    public static void main(String[] args) {
        int n = sc.nextInt(), p = sc.nextInt(), q = sc.nextInt();
        groupTime = new int[n];
        Queue q1 = new Queue(p);
        Queue q2 = new Queue(q);
        while (p-- > 0)
            q1.enqueue(sc.nextInt());
        while (q-- > 0)
            q2.enqueue(sc.nextInt());

        while (!q1.empty() || !q2.empty()) {
            time++;
            while (!q1.empty() && groupTime[q1.head() - 1] != 0)
                q1.dequeue();
            while (!q2.empty() && groupTime[q2.head() - 1] != 0)
                q2.dequeue();
            if (q1.head() == q2.head() && !q1.empty()) {
                groupTime[q1.head() - 1] = time;
                q1.dequeue();
                q2.dequeue();
                continue;
            }
            if (!q1.empty())
                groupTime[q1.head() - 1] = time;
            if (!q2.empty())
                groupTime[q2.head() - 1] = time;
        }
        for (int i = 0; i < n; i++)
            out.print(groupTime[i] + " ");
        out.close();
    }
}

class Queue {
    int[] line;
    int front, rear;
    Queue(int l) { line = new int[l]; }
    
    void enqueue(int id) { line[rear++] = id; }
    void dequeue() { if (!empty()) front++;}
    
    boolean empty() { return front >= rear; }
    int head() {
        if (empty()) return -1;
        return line[front];
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
