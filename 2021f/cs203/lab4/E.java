import java.io.*;
import java.util.StringTokenizer;

public class E {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        int T = sc.nextInt();
        while (T-- > 0) {
            Node head = new Node(Integer.MIN_VALUE, null);
            head.next = new Node(-1, head);
            Node cur = head.next;
            int n = sc.nextInt();
            // stack for first turn
            Node[] fP = new Node[n];
            int fCur = 0;
            Queue toProc = new Queue();
            while (n-- >= 0) {
                if (n >= 0)
                    cur.next = new Node(sc.nextInt(), cur);
                if (n == 0)
                    cur.next.next = new Node(Integer.MAX_VALUE, cur.next);
                if (cur.val >= cur.prev.val && cur.val > cur.next.val // first term of desc seq
                        || cur.val < cur.prev.val && cur.val <= cur.next.val) // last term of desc seq
                    fP[fCur++] = cur;
                cur = cur.next;
            }
            for (int i = 0; i < fCur; i += 2) {
                fP[i].prev.next = fP[i + 1].next;
                fP[i + 1].next.prev = fP[i].prev;
                toProc.enqueue(fP[i].prev);
            }
            while (!toProc.empty()) {
                if (toProc.head().removed) {
                    toProc.dequeue();
                    continue;
                }
                Node cur1 = toProc.head();
                while (cur1.next.val < Integer.MAX_VALUE && cur1.val > cur1.next.val) {
                    cur1.removed = true;
                    cur1 = cur1.next; // fixme
                }
                if (cur1 == toProc.head()) {
                    toProc.dequeue();
                    continue;
                }
                toProc.head().prev.next = cur1.next;
                cur1.next.prev = toProc.head().prev;
                if (toProc.head().prev.val > 0)
                    toProc.enqueue(toProc.head().prev);
                toProc.dequeue();
            }
            cur = head.next.next;
            while (cur != null && cur.val < Integer.MAX_VALUE) {
                out.print(cur.val + " ");
                cur = cur.next;
            }
            out.println("");
        }
        out.close();
    }
}

class Queue {
    Node[] transP = new Node[150000];
    int front, rear;

    boolean empty() {
        return front >= rear;
    }

    void enqueue(Node n) {
        transP[rear++] = n;
    }

    Node dequeue() {
        if (front > rear)
            return null;
        return transP[front++];
    }

    Node head() {
        if (front > rear)
            return null;
        return transP[front];
    }
}

class Node {
    int val;
    Node next;
    Node prev;
    boolean removed = false;

    Node(int i, Node p) {
        val = i;
        prev = p;
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
