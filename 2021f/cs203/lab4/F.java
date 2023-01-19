import java.io.*;
import java.util.StringTokenizer;

public class F {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    final static Lump head = new Lump();

    public static void main(String[] args) {
        String s = sc.next();
        int sqrtLen = (int) Math.sqrt(s.length());
        head.next = new Lump();
        Lump cur = head.next;
        for (int i = 0; i < s.length(); i++) {
            if (i % sqrtLen == 0 && i != 0) {
                cur.next = new Lump();
                cur = cur.next;
            }
            cur.addChar(s.charAt(i));
        }
        int n = sc.nextInt();
        while (n-- > 0) {
            cur = head.next;
            switch (sc.nextInt()) {
                case 1:
                    ins(sc.next().charAt(0), sc.nextInt() - 1, cur, sqrtLen);
                    break;
                case 2:
                    find(sc.nextInt() - 1, cur);
                    break;
                case 3:
                    trans(sc.nextInt() - 1, sc.nextInt() - 1, cur);
            }
        }
        out.close();
    }

    static void ins(char c, int pos, Lump lh, int sqrtLen) {
        while (pos >= lh.len) { // fixme
            pos -= lh.len;
            lh = lh.next;
        }
        if (pos == 0) {
            Node nh = new Node(lh.trans ? (char) ('a' + 'z' - c) : c);
            nh.next = lh.chs;
            lh.chs = nh;
            lh.len++;
        } else {
            Node hc = lh.chs;
            while (--pos > 0)
                hc = hc.next;
            Node nc = new Node(lh.trans ? (char) ('a' + 'z' - c) : c);
            nc.next = hc.next;
            hc.next = nc;
            lh.len++;
        }
        if (lh.len > 2 * sqrtLen) {
            Node cur = lh.chs;
            for (int i = 0; i < sqrtLen; i++)
                cur = cur.next; // FIXME
            Lump ln = new Lump();
            ln.chs = cur.next;
            cur.next = null;
            ln.len = sqrtLen;
            lh.len -= sqrtLen;
            ln.trans = lh.trans;
            ln.next = lh.next;
            lh.next = ln;
        }
    }

    static void find(int pos, Lump lh) {
        while (pos >= lh.len) {
            pos -= lh.len;
            lh = lh.next;
        }
        Node cur = lh.chs;
        while (pos-- > 0)
            cur = cur.next;
        if (lh.trans)
            out.println((char) ('a' + 'z' - cur.ch));
        else
            out.println(cur.ch);
    }

    static void trans(int l, int r, Lump lh) {
        int len = r - l + 1;
        if (len <= 0)
            return;
        while (l > lh.len) {
            l -= lh.len;
            lh = lh.next;
        }
        if (l > 0) {
            Node cur = lh.chs;
            int tmp = l;
            while (l-- > 0)
                cur = cur.next;
            int i = 0;
            for (i = 0; i < len && cur != null; i++) {
                cur.ch = (char) ('a' + 'z' - cur.ch);
                cur = cur.next;
            }
            len -= i;
            len = Math.max(len, 0);
            lh = lh.next;
        }
        while (lh != null && len >= lh.len) {
            len -= lh.len;
            lh.trans = !lh.trans;
            lh = lh.next;
        }
        if (lh == null)
            return;
        Node cur = lh.chs;
        while (len-- > 0) {
            cur.ch = (char) ('a' + 'z' - cur.ch);
            cur = cur.next;
        }
    }
}

class Lump {
    Node chs;
    private Node ccur;
    int len = 0;
    boolean trans = false;
    Lump next;

    void addChar(char c) {
        if (chs == null) {
            chs = new Node(c);
            ccur = chs;
            len++;
            return;
        }
        ccur.next = new Node(c);
        ccur = ccur.next;
        len++;
    }
}

class Node {
    char ch;
    Node next;

    Node(char c) {
        ch = c;
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