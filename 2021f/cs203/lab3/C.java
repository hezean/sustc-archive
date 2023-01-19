import java.io.BufferedReader;
import java.util.StringTokenizer;
import java.io.*;
import java.util.*;

public class C {
    static long insc, selc;

    public static void main(String[] args) {
        QReader sc = new QReader();
        QWriter out = new QWriter();
        int T = sc.nextInt();
        while (T-- > 0) {
            insc = 0;
            selc = 0;
            int n = sc.nextInt();
            int arr1[] = new int[n];
            for (int i = 0; i < n; i++)
                arr1[i] = sc.nextInt();
            int arr2[] = Arrays.copyOf(arr1, n);
            ins(arr1);
            sel(arr2);
            for (int i = 0; i < n - 1; i++)
                out.print(arr1[i] + " ");
            out.println(arr1[n - 1]);
            out.print(insc < selc ? "Insertion Sort wins!\n" : "Selection Sort wins!\n");
        }
        out.close();
    }

    static void ins(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i; j > 0; j--) {
                insc++;
                if (arr[j] < arr[j - 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                    insc++;
                } else
                    break;
            }
        }
    }

    static void sel(int[] arr) {
        int minp;
        for (int i = 0; i < arr.length - 1; i++) {
            minp = i;
            for (int j = i + 1; j < arr.length; j++) {
                selc++;
                if (arr[j] < arr[minp])
                    minp = j;
            }
            int temp = arr[i];
            arr[i] = arr[minp];
            arr[minp] = temp;
            selc++;
        }
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
