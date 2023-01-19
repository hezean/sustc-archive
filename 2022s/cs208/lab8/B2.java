import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

class TreeArray {
    long[] c;
    long[] a;

    int length;

    public TreeArray(int n) {
        this.c = new long[n + 1];
        this.a = new long[n + 1];
        this.length = n;
    }

    public void init(long[] a) {
        this.a = a;
        for (int i = 1; i <= length; i++) {
            c[i] = a[i];
            for (int j = 1; j < lowBit(i); j <<= 1) {
                c[i] = Math.min(c[i], c[i - j]);
            }
        }
    }

    public void update(int pos, long val) {
        if (a[pos] < val) return;
        a[pos] = val;
        for (int i = pos; i <= length; i += lowBit(i)) {
            c[pos] = val;
            for (int j = 1; j < lowBit(i); j <<= 1) {
                c[i] = Math.min(c[i], c[i - j]);
            }
        }
    }

    public long query(int l, int r) {
        long res = Long.MAX_VALUE;
        while (true) {
            res = Math.min(res, a[r]);
            if (r == l) break;
            for (r -= 1; r - l >= lowBit(r); r -= lowBit(r)) {
                res = Math.min(res, c[r]);
            }
        }
        return res;
    }

    public static int lowBit(int x) {
        return x & (-x);
    }
}

class Point {
    int x;
    int y;
    int id;
    boolean side;

    public Point(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public static long dis(Point a, Point b) {
        return (long) Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}

public class B2 {
    static long[] opt;
    static long[] prefixSum;
    static Point[] points;
    static TreeArray ta;

    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        int n = sc.nextInt();
        prefixSum = new long[n + 1];
        points = new Point[n + 1];
        opt = new long[n + 1];
        ta = new TreeArray(n);

        int x = sc.nextInt();
        int y = sc.nextInt();
        points[1] = new Point(x, y, 1);

        for (int i = 1; i < n; i++) {
            int xx = sc.nextInt();
            int yy = sc.nextInt();
            points[i + 1] = new Point(xx, yy, i + 1);
            prefixSum[i + 1] = prefixSum[i] + (long) Math.abs(xx - x) + Math.abs(yy - y);
            x = xx;
            y = yy;
            opt[i + 1] = Long.MAX_VALUE;
        }

        core(1, n);
        out.println(opt[n]);
        out.close();
    }

    public static void core(int l, int r) {
        if (l == r) {
            return;
        }
        int mid = l + (r - l) / 2;
        core(l, mid);
        updateRight(l, mid, r);
        core(mid + 1, r);
    }

    public static void updateRight(int l, int mid, int r) {
        if (l == r) {
            return;
        }
        for (int i = l; i <= mid; i++) {
            points[i].side = false;
        }
        for (int i = mid + 1; i <= r; i++) {
            points[i].side = true;
        }
        Arrays.sort(points, l, r, new Comparator<>() {
            public int compare(Point a, Point b) {
                return a.x - b.x;
            }
        });
        Point[] yPoints;
        yPoints = Arrays.copyOfRange(points, l, r + 1, Point[].class);
        Arrays.sort(yPoints, new Comparator<>() {
            public int compare(Point a, Point b) {
                return a.y - b.y;
            }
        });
        int[] yRank = new int[r - l + 2];
        for (int i = 0; i < yRank.length; i++) {
            yRank[yPoints[i].id - l + 1] = i;
        }
        long[] v = new long[r - l + 2];
        TreeArray ta = new TreeArray(r - l + 1);
        ta.init(v);

        for (int i = l; i <= r; i++) {
            Point p = points[i];
            Point xi = points[p.id];
            int k = p.id;
            int caseNo;
            if (xi.x >= p.x) {
                if (xi.y >= p.y)
                    caseNo = 1;
                else
                    caseNo = 3;
            } else {
                if (xi.y >= p.y)
                    caseNo = 2;
                else
                    caseNo = 4;
            }

            if (!p.side) {
                long val;
                if (caseNo == 1)
                    val = opt[k] - prefixSum[k] - points[k - 1].x - points[k - 1].y;
                else if (caseNo == 2)
                    val = opt[k] - prefixSum[k] + points[k - 1].x - points[k - 1].y;
                else if (caseNo == 3)
                    val = opt[k] - prefixSum[k] - points[k - 1].x + points[k - 1].y;
                else
                    val = opt[k] - prefixSum[k] + points[k - 1].x + points[k - 1].y;
                ta.update(yRank[k], val);
            } else {
                if (caseNo == 1)
                    opt[p.id] = Math.min(opt[p.id], ta.query(l, yRank[p.id] - 1) + prefixSum[p.id - 1] + p.x + p.y);
                else if (caseNo == 2)
                    opt[p.id] = Math.min(opt[p.id], ta.query(l, yRank[p.id] - 1) + prefixSum[p.id - 1] - p.x + p.y);
                else if (caseNo == 3)
                    opt[p.id] = Math.min(opt[p.id], ta.query(l, yRank[p.id] - 1) + prefixSum[p.id - 1] + p.x - p.y);
                else
                    opt[p.id] = Math.min(opt[p.id], ta.query(l, yRank[p.id] - 1) + prefixSum[p.id - 1] - p.x - p.y);
            }
        }

        Arrays.sort(points, l, r, new Comparator<>() {
            public int compare(Point a, Point b) {
                return a.id - b.id;
            }
        });

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


