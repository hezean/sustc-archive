import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

class CBD {
    class Node {
        boolean vis;
        int len;
        int x, y;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    Node[][] nodes;

    CBD() {
        nodes = new Node[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                nodes[i][j] = new Node(i, j);
            }
        }
    }

    int bfs(String st, String tem) {
        if (st.equals(tem))
            return 0;
        Queue<Node> q = new LinkedList<>();
        int sx = st.charAt(0) - 'a', sy = st.charAt(1) - '1';
        int tx = tem.charAt(0) - 'a', ty = tem.charAt(1) - '1';
        q.add(nodes[sx][sy]);
        while (!q.isEmpty()) {
            Node cur = q.poll();
            cur.vis = true;
            int i = cur.x, j = cur.y, len = cur.len + 1;
            if (i < 6 && j < 7 && !nodes[i + 2][j + 1].vis) {
                if (tx == i + 2 && ty == j + 1)
                    return len;
                q.add(nodes[i + 2][j + 1]);
                nodes[i + 2][j + 1].vis = true;
                nodes[i + 2][j + 1].len = len;
            }
            if (i < 6 && j > 0 && !nodes[i + 2][j - 1].vis) {
                if (tx == i + 2 && ty == j - 1)
                    return len;
                q.add(nodes[i + 2][j - 1]);
                nodes[i + 2][j - 1].vis = true;
                nodes[i + 2][j - 1].len = len;
            }
            if (i > 1 && j < 7 && !nodes[i - 2][j + 1].vis) {
                if (tx == i - 2 && ty == j + 1)
                    return len;
                q.add(nodes[i - 2][j + 1]);
                nodes[i - 2][j + 1].vis = true;
                nodes[i - 2][j + 1].len = len;
            }
            if (i > 1 && j > 0 && !nodes[i - 2][j - 1].vis) {
                if (tx == i - 2 && ty == j - 1)
                    return len;
                q.add(nodes[i - 2][j - 1]);
                nodes[i - 2][j - 1].vis = true;
                nodes[i - 2][j - 1].len = len;
            }
            if (i < 7 && j < 6 && !nodes[i + 1][j + 2].vis) {
                if (tx == i + 1 && ty == j + 2)
                    return len;
                q.add(nodes[i + 1][j + 2]);
                nodes[i + 1][j + 2].vis = true;
                nodes[i + 1][j + 2].len = len;
            }
            if (i < 7 && j > 1 && !nodes[i + 1][j - 2].vis) {
                if (tx == i + 1 && ty == j - 2)
                    return len;
                q.add(nodes[i + 1][j - 2]);
                nodes[i + 1][j - 2].vis = true;
                nodes[i + 1][j - 2].len = len;
            }
            if (i > 0 && j < 6 && !nodes[i - 1][j + 2].vis) {
                if (tx == i - 1 && ty == j + 2)
                    return len;
                q.add(nodes[i - 1][j + 2]);
                nodes[i - 1][j + 2].vis = true;
                nodes[i - 1][j + 2].len = len;
            }
            if (i > 0 && j > 1 && !nodes[i - 1][j - 2].vis) {
                if (tx == i - 1 && ty == j - 2)
                    return len;
                q.add(nodes[i - 1][j - 2]);
                nodes[i - 1][j - 2].vis = true;
                nodes[i - 1][j - 2].len = len;
            }
        }
        return nodes[tx][ty].len;
    }

    void clear() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                nodes[i][j].vis = false;
                nodes[i][j].len = 0;
            }
        }
    }
}

public class B {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        int t = sc.nextInt();
        CBD cbd = new CBD();
        while (t-- > 0) {
            out.println(cbd.bfs(sc.next(), sc.next()));
            cbd.clear();
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
