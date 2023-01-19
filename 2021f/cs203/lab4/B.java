import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class B {
    static int m, n;
    static Node head = new Node(-1);
    static Node tail = new Node(3000000);
    static Node cur = head;
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        n = sc.nextInt();
        m = sc.nextInt();
        for (int i = 1; i <= n; i++) {
            cur.next = new Node(i);
            cur = cur.next;
        }
        cur.next = tail;

        out.print("1 ");
        head.next.isOut = true;

        cur = head.next;
        while (--n > 0)
            cur = Node.out(cur, m);

        out.close();
    }
}

class Node {
    int val;
    boolean isOut = false;
    Node next;

    Node(int v) {
        this.val = v;
    }

    static Node out(Node cur, int step) {
        while (step > 0) {
            if (cur.next.val > 2900000) {
                cur = B.head.next;
                while (cur.isOut)
                    cur = cur.next;
                break;
            }
            cur = cur.next;
            if (!cur.isOut)
                step--;
        }
        cur.isOut = true;
        B.out.print(cur.val);
        B.out.print(" ");
        return cur;
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
