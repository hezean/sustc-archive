import java.io.BufferedReader;
import java.util.StringTokenizer;
import java.io.*;
import java.util.*;

public class F {
    static Plant[] tarr;

    public static void main(String[] args) {
        QReader sc = new QReader();
        int n = sc.nextInt();
        int p = sc.nextInt();
        int q = sc.nextInt();
        Plant[] ps = new Plant[n];
        tarr = new Plant[n];
        long ans = 0;
        int dfc = 0;
        for (int i = 0; i < n; i++) {
            ps[i] = new Plant(sc.nextLong(), sc.nextLong());
            ans += ps[i].st;
            if (ps[i].df > 0)
                dfc++;
        }

        if (q == 0) {
            System.out.println(ans);
            return;
        }
        ms(ps, 0, n - 1);
        if (p == 0) {
            for (int t = 0; t < Math.min(dfc, q); t++)
                ans += ps[t].df;
            System.out.println(ans);
            return;
        }

        if (q > dfc) {
            long maxAdv = 0;
            for (int i = 0; i < n; i++)
                maxAdv = Math.max(maxAdv, (long) Math.pow(2, p) * ps[i].ht - ps[i].st - Math.max(0, ps[i].df));
            for (int i = 0; i < dfc; i++)
                ans += ps[i].df;
            ans += maxAdv;
            System.out.println(ans);
            return;
        }

        long maxAdv = 0;
        for (int i = 0; i < q; i++)
            maxAdv = Math.max(maxAdv, (long) Math.pow(2, p) * ps[i].ht - ps[i].st - ps[i].df);
        for (int i = q; i < n; i++)
            maxAdv = Math.max(maxAdv, (long) Math.pow(2, p) * ps[i].ht - ps[i].st - ps[q - 1].df);
        for (int i = 0; i < q; i++)
            ans += ps[i].df;
        ans += maxAdv;
        System.out.println(ans);
    }

    static void mer(Plant[] arr, Plant[] tmp, int lo, int mi, int hi) {
        int i = lo, j = mi + 1, f = 0;
        while (i <= mi && j <= hi) {
            if (arr[i].df > arr[j].df)
                tmp[f++] = arr[i++];
            else
                tmp[f++] = arr[j++];
        }
        while (i <= mi)
            tmp[f++] = arr[i++];
        while (j <= hi)
            tmp[f++] = arr[j++];
        for (i = lo; i <= hi; i++)
            arr[i] = tmp[i - lo];
    }

    static void ms(Plant[] arr, int lo, int hi) {
        if (lo >= hi)
            return;
        int mi = (lo + hi) / 2;
        ms(arr, lo, mi);
        ms(arr, mi + 1, hi);
        mer(arr, tarr, lo, mi, hi);
    }
}

class Plant {
    long ht, st, df;

    Plant(long ht, long st) {
        this.ht = ht;
        this.st = st;
        this.df = ht - st;
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
