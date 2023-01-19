import java.io.*;
import java.util.StringTokenizer;

class MinPQ {
    long[] pq;
    int n; // valid

    MinPQ(int cap) {
        pq = new long[cap + 1];
    }

    static void swap(long[] arr, int i1, int i2) {
        long tmp = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = tmp;
    }

    void swim(int idx) {
        while (idx > 1 && pq[idx] < pq[idx / 2]) {
            swap(pq, idx, idx / 2);
            idx /= 2;
        }
    }

    void sink(int idx) {
        while (idx * 2 <= n) {
            int j = idx * 2;
            if (j < n && pq[j] > pq[j + 1]) {
                j++;
            }
            if (pq[idx] <= pq[j]) {
                break;
            }
            swap(pq, idx, j);
            idx = j;
        }
    }

    void push(long x) {
        pq[++n] = x;
        swim(n);
    }

    long pop() {
        long ans = pq[1];
        swap(pq, 1, n--);
        sink(1);
        return ans;
    }

    int size() {
        return n;
    }
}

public class D {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        int n = sc.nextInt();
        MinPQ pq = new MinPQ(n + 2);
        while (n-- > 0) {
            pq.push(sc.nextInt());
        }
        long ans = 0, tmp;
        while (pq.size() > 1) {
            tmp = pq.pop() + pq.pop();
            ans += tmp;
            pq.push(tmp);
        }
        out.println(ans);
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
