import java.io.*;
import java.util.StringTokenizer;

class Node {
    short x, y;
    boolean vis;
    long val;

    static Node[][] mp;
    static short row = 0;
    static short col = 0;
    static long max = 0;

    public Node(short x, short y) {
        this.x = x;
        this.y = y;
    }

    boolean reachable() {
        if (vis || x >= row || y >= col)
            return false;
        if (x > 0 && (mp[x - 1][y].vis
                || y > 0 && mp[x - 1][y - 1].vis
                || y < col - 1 && mp[x - 1][y + 1].vis))
            return false;
        if (y > 0 && mp[x][y - 1].vis
                || y < col - 1 && mp[x][y + 1].vis)
            return false;
        if (x < row - 1 && (mp[x + 1][y].vis
                || y > 0 && mp[x + 1][y - 1].vis
                || y < col - 1 && mp[x + 1][y + 1].vis))
            return false;
        return true;
    }

    static void rst(short r, short c) {
        for (short _r = 0; _r < r; _r++) {
            for (short _c = 0; _c < c; _c++) {
                mp[_r][_c].vis = false;
            }
        }
        row = r;
        col = c;
        max = 0;
    }

    static void dfs(Node node, long sum) {
        Node next = null;
        if (node.x < row && node.y < col - 1) {
            next = mp[node.x][node.y + 1];
        } else if (node.x < row - 1 && node.y == col - 1) {
            next = mp[node.x + 1][0];
        }
        if (node.reachable()) {
            node.vis = true;
            max = Math.max(max, sum + node.val);
            if (next != null)
                dfs(next, sum + node.val);
            node.vis = false;
        }
        if (next != null) {
            dfs(next, sum);
        }
    }
}

public class D {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        Node.mp = new Node[6][6];
        for (short i = 0; i < 6; i++) {
            for (short j = 0; j < 6; j++) {
                Node.mp[i][j] = new Node(i, j);
            }
        }
        int T = sc.nextInt();
        while (T-- > 0) {
            int n = sc.nextInt(), m = sc.nextInt();
            Node.rst((short) n, (short) m);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    Node.mp[i][j].val = sc.nextLong();
                }
            }
            Node.dfs(Node.mp[0][0], 0);
            out.println(Node.max);
        }
        out.close();
    }
}

class QReader {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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
