// import java.util.Scanner;

// public class B2 {
//     public static int mod = (int) (1e9 + 7);

//     public static void main(String[] args) {
//         Scanner in = new Scanner(System.in);
//         int n = in.nextInt();
//         int m = in.nextInt();
//         long[][] matrix = new long[m][m];
//         for (int i = 0; i < m; i++) {
//             matrix[i][0] = 1;
//         }
//         for (int i = 1; i < m; i++) {
//             matrix[i - 1][i] = 1;
//         }
//         int times = n;
//         long[][] res = new long[m][m];
//         for (int i = 0; i < m; i++) {
//             res[i][i] = 1;
//         }
//         while (times != 0) {
//             if (times % 2 == 1)
//                 res = multi(matrix, res);
//             matrix = multi(matrix, matrix);
//             times /= 2;
//         }
//         long result = 0;
//         for (int i = 0; i < m; i++) {
//             result += res[0][i];
//             result %= mod;
//         }
//         System.out.println(result);
//     }

//     public static long[][] multi(long[][] temp, long[][] res) {
//         long res_ = 0;
//         long temp_ = 0;
//         long[][] curr = new long[temp.length][temp.length];
//         for (int i = 0; i < temp.length; i++) {
//             for (int j = 0; j < temp.length; j++) {
//                 res_ = 0;
//                 for (int k = 0; k < temp.length; k++) {
//                     temp_ = (temp[i][k] * res[k][j]) % mod;
//                     res_ += temp_;
//                     res_ %= mod;
//                 }
//                 curr[i][j] = res_;
//             }
//         }
//         return curr;
//     }
// }
