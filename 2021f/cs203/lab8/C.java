import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

class nd {
    int ai, bi;
    long val;

    public nd(long val, int ai, int bi) {
        this.ai = ai;
        this.bi = bi;
        this.val = val;
    }
}

class MinPQ {
    nd[] pq;
    int n;

    public MinPQ(int sz) {
        pq = new nd[sz + 1];
        n = 0;
    }

    void swim(int k) {
        while (k > 1 && pq[k / 2].val > pq[k].val) {
            nd tmp = pq[k / 2];
            pq[k / 2] = pq[k];
            pq[k] = tmp;
            k /= 2;
        }
    }

    void sink(int idx) {
        while (idx * 2 <= n) {
            int j = idx * 2;
            if (j < n && pq[j].val > pq[j + 1].val)
                j++;
            if (pq[idx].val <= pq[j].val)
                break;
            nd tmp = pq[idx];
            pq[idx] = pq[j];
            pq[j] = tmp;
            idx = j;
        }
    }

    void push(nd x) {
        pq[++n] = x;
        swim(n);
    }

    nd pop() {
        nd top = pq[1];
        pq[1] = pq[n--];
        sink(1);
        return top;
    }
}

public class C {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        int n = sc.nextInt();
        int m = sc.nextInt();
        long k = sc.nextLong();
        if (k <= 0)
            return;
        int[] a = new int[n];
        for (int i = 0; i < n; i++)
            a[i] = sc.nextInt();
        int[] b = new int[m];
        for (int i = 0; i < m; i++)
            b[i] = sc.nextInt();
        Arrays.sort(a);
        Arrays.sort(b);
        MinPQ pq = new MinPQ(m);
        for (int i = 0; i < m; i++)
            pq.push(new nd(((long) a[0]) * b[i], 0, i));
        while (k-- > 0) {
            nd tmp = pq.pop();
            out.print(tmp.val + " ");
            if (tmp.ai < n - 1)
                pq.push(new nd(((long) a[tmp.ai + 1]) * b[tmp.bi], tmp.ai + 1, tmp.bi));
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