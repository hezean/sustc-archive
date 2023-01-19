import java.io.*;
import java.util.StringTokenizer;

public class E {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        while (sc.hasNext()) {
            int n = sc.nextInt(), q = sc.nextInt();
            Deque[] deq = new Deque[n];
            for (int i = 0; i < n; i++)
                deq[i] = new Deque();
            while (q-- > 0) {
                switch (sc.nextInt()) {
                    case 1:
                        int u1 = sc.nextInt();
                        int w1 = sc.nextInt();
                        int val1 = sc.nextInt();
                        if (w1 == 0)
                            deq[u1 - 1].enHead(new Node(val1));
                        else
                            deq[u1 - 1].enRear(new Node(val1));
                        break;
                    case 2:
                        int u2 = sc.nextInt();
                        int w2 = sc.nextInt();
                        if (w2 == 0)
                            out.println(deq[u2 - 1].deHead());
                        else
                            out.println(deq[u2 - 1].deRear());
                        break;
                    case 3:
                        int u3 = sc.nextInt();
                        int v3 = sc.nextInt();
                        int w3 = sc.nextInt();
                        if (w3 == 0)
                            deq[v3 - 1].dirConcatTo(deq[u3 - 1]);
                        else
                            deq[v3 - 1].transConcatTo(deq[u3 - 1]);
                }
            }
        }
        out.close();
    }
}

class Deque {
    int len;
    Node head;
    Node rear;

    Deque() {
        head = new Node(-1);
        rear = new Node(-1);
        head.next = rear;
        rear.prev = head;
    }

    void enHead(Node h) {
        h.prev = head;
        h.next = head.next;
        head.next.prev = h;
        head.next = h;
        len++;
    }

    void enRear(Node r) {
        r.prev = rear.prev;
        r.next = rear;
        rear.prev.next = r;
        rear.prev = r;
        len++;
    }

    int deHead() {
        if (len == 0)
            return -1;
        int val = head.next.val;
        head.next = head.next.next;
        head.next.prev = head;
        len--;
        return val;
    }

    int deRear() {
        if (len == 0)
            return -1;
        int val = rear.prev.val;
        rear.prev = rear.prev.prev;
        rear.prev.next = rear;
        len--;
        return val;
    }

    Node deRearNode() {
        if (len == 0)
            return null;
        Node n = rear.prev;
        rear.prev = rear.prev.prev;
        rear.prev.next = rear;
        len--;
        return n;
    }

    void dirConcatTo(Deque tar) {
        if (this.len == 0)
            return;
        this.head.next.prev = tar.rear.prev;
        tar.rear.prev.next = this.head.next;
        tar.rear.prev = this.rear.prev;
        this.rear.prev.next = tar.rear;
        tar.len += this.len;
        clear();
    }

    void transConcatTo(Deque tar) {
        if (this.len == 0)
            return;
        while (true) {
            Node n = deRearNode();
            if (n == null)
                break;
            tar.enRear(n);
        }
    }

    void clear() {
        this.len = 0;
        this.head.next = rear;
        this.rear.prev = head;
    }
}

class Node {
    int val;
    Node prev;
    Node next;

    Node(int v) {
        val = v;
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
