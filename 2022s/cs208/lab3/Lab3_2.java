import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Lab3_2 {
    public static void main(String[] args) {
        QReader in = new QReader();
        QWriter out = new QWriter();
        int N = in.nextInt(); // N bunnies p bunnies position
        int M = in.nextInt(); // M nests q nests position
        int C = in.nextInt(); // each nest hold numbers
        int T = in.nextInt(); // T charge time
        int[] bunnies = new int[N];
        int[] nests = new int[M];
        int[] num_of_nests = new int[M];
        for (int i = 0; i < N; i++) {
            bunnies[i] = in.nextInt();
        }
        for (int i = 0; i < M; i++) {
            nests[i] = in.nextInt();
        }
        Arrays.sort(bunnies);
        Arrays.sort(nests);
        int j = 0;
        int left = 0;
        int right = 0;
        for (int i = 0; i < N; i++) { // i is the index of bunnies
            left = bunnies[i] - T;
            right = bunnies[i] + T;
            while (j < M) {
                if (left <= nests[j] && nests[j] <= right) {
                    if (num_of_nests[j] < C) {
                        num_of_nests[j]++;
                        break;
                    } else {
                        j++;
                        continue;
                    }
                }
                if (nests[j] > left) {
                    break;
                }
                j++;
            }
        }
        int res = 0;
        for (int i = 0; i < M; i++) {
            res += num_of_nests[i];
        }
        out.print(res);
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
