import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

class Node {
    static boolean[][] vis;
    int x, y;
    int height;
    double time = Double.MAX_VALUE, v;

    Node(int x, int y, int h, int v) {
        this.v = v;
        this.x = x;
        this.y = y;
        height = h;
    }
}

class Proxy {
    int x, y;
    double time, v;

    Proxy(int x, int y, double t, double v) {
        this.x = x;
        this.y = y;
        time = t;
        this.v = v;
    }
}

class MinPQ {
    Proxy[] pq = new Proxy[Main.n * Main.m * 4 + 100];
    int n = 1;

    void add(Proxy nd) {
        pq[n++] = nd;
        swim(n - 1);
    }

    void swim(int i) {
        while (i > 1 && pq[i].time < pq[i / 2].time) {
            Proxy tmp = pq[i];
            pq[i] = pq[i / 2];
            pq[i / 2] = tmp;
            i /= 2;
        }
    }

    void sink(int i) {
        while (2 * i < n) {
            int j = 2 * i;
            if (j < n - 1 && pq[j].time > pq[j + 1].time)
                j++;
            if (pq[i].time <= pq[j].time)
                break;
            Proxy tmp = pq[i];
            pq[i] = pq[j];
            pq[j] = tmp;
            i = j;
        }
    }

    Proxy __pop() {
        Proxy min = pq[1];
        pq[1] = pq[n - 1];
        pq[n - 1] = null;
        n--;
        sink(1);
        return min;
    }

    Proxy pop() {
        Proxy tmp = __pop();
        while (tmp != null && Node.vis[tmp.x][tmp.y]) {
            tmp = __pop();
        }
        if (tmp != null)
            Node.vis[tmp.x][tmp.y] = true;
        return tmp;
    }
}

public class Main {
    static QReader sc = new QReader();
    static int n, m;

    public static void main(String[] args) {
        n = sc.nextInt();
        m = sc.nextInt();
        Node[][] msp = new Node[n][m];
        Node.vis = new boolean[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                msp[i][j] = new Node(i, j, sc.nextInt(), 1);
            }
        }
        msp[0][0].time = 0;

        MinPQ pq = new MinPQ();
        pq.add(new Proxy(0, 0, 0, 1));
        while (pq.n > 1) {
            Proxy h = pq.pop();
            if (h == null)
                break;
            if (h.x > 0 && !Node.vis[h.x - 1][h.y] && msp[h.x - 1][h.y].time > (h.time + 1.0 / h.v)) {
                msp[h.x - 1][h.y].time = h.time + 1.0 / h.v;
                pq.add(new Proxy(h.x - 1, h.y, msp[h.x - 1][h.y].time,
                        h.v * Math.pow(2, msp[h.x][h.y].height - msp[h.x - 1][h.y].height)));
            }
            if (h.x < n - 1 && !Node.vis[h.x + 1][h.y] && msp[h.x + 1][h.y].time > (h.time + 1.0 / h.v)) {
                msp[h.x + 1][h.y].time = h.time + 1.0 / h.v;
                pq.add(new Proxy(h.x + 1, h.y, msp[h.x + 1][h.y].time,
                        h.v * Math.pow(2, msp[h.x][h.y].height - msp[h.x + 1][h.y].height)));
            }
            if (h.y > 0 && !Node.vis[h.x][h.y - 1] && msp[h.x][h.y - 1].time > (h.time + 1.0 / h.v)) {
                msp[h.x][h.y - 1].time = h.time + 1.0 / h.v;
                pq.add(new Proxy(h.x, h.y - 1, msp[h.x][h.y - 1].time,
                        h.v * Math.pow(2, msp[h.x][h.y].height - msp[h.x][h.y - 1].height)));
            }
            if (h.y < m - 1 && !Node.vis[h.x][h.y + 1] && msp[h.x][h.y + 1].time > (h.time + 1.0 / h.v)) {
                msp[h.x][h.y + 1].time = h.time + 1.0 / h.v;
                pq.add(new Proxy(h.x, h.y + 1, msp[h.x][h.y + 1].time,
                        h.v * Math.pow(2, msp[h.x][h.y].height - msp[h.x][h.y + 1].height)));
            }
        }
        System.out.printf("%.2f", msp[n - 1][m - 1].time);
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
