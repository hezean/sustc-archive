import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Lab11_2 {
    public static int mod = (int) (1e9 + 7);

    public static void main(String[] args) {
        QReader in = new QReader();
        QWriter out = new QWriter();

        int N = in.nextInt();
        int M = in.nextInt();

        int u = 0;
        int v = 0;
        int[][] relation = new int[N + 1][N + 1];
        for (int i = 0; i < M; i++) {
            u = in.nextInt();
            v = in.nextInt();

            relation[v][u] = 1;
        }
        int length = 1 << N;
        long[] res = new long[length];
        res[0] = 1;

        u = 1;
        while (u < length) {
            res[u] = 1;
            u = u << 1;
        }


        int[] record_1 = new int[N + 1];
        int index_1 = 0;
        int[] record_0 = new int[N + 1];
        int index_0 = 0;

        for (int i = 1; i < length - 1; i++) {  // 2^n
            for (int j = 0; j < N; j++) {  // n
                if (((i >> j) & 1) == 0)
                    record_0[index_0++] = j + 1;
                else
                    record_1[index_1++] = j + 1;
            }

            int re = 0;
            int temp = 0;

            // check all possible new combinations

            for (int k = 0; k < index_0; k++) {
                re = i + (1 << (record_0[k] - 1));
                temp = 0;
                for (int j = 0; j < index_1; j++) {
                    if (relation[record_1[j]][record_0[k]] == 1) {
                        temp++;
                    }
                }
                res[re] += res[i] * (1 << temp);
                res[re] %= mod;
            }
            index_0 = 0;
            index_1 = 0;

        }
        out.println(res[length - 1]);

        out.close();
    }

}
