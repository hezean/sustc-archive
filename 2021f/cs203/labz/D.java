import java.io.*;

public class D {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static int[] arr = new int[200000];
    static int t, n, q, st, tmp,tar;

    public static void main(String[] args) throws IOException {
        t = sc.nextInt();
        while (t-- > 0) {
            n = sc.nextInt();
            for (int i = 0; i < n; ++i)
                arr[i] = sc.nextInt();
            q = sc.nextInt();
            for (int i = 0; i < q; ++i) {
                st = sc.nextInt();
                tmp = st--;
                tar = arr[st];
                while (tmp < n && arr[tmp] <= tar)
                    tmp++;
                out.println((tmp >= n) ? -1 : tmp - st);
            }
        }

        out.close();
    }
}

class QReader {
    StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));

    int nextInt() throws IOException {
        st.nextToken();
        return (int) st.nval;
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