// import java.io.*;
// import java.util.*;

// public class Main {
//     static QReader sc = new QReader();
//     static QWriter out = new QWriter();
//     static final String SPC = " ";

//     public static void main(String[] args) {
//         int n = sc.nextInt();
//         int[] dist = new int[n + 2];  // guard
//         Arrays.fill(dist, Integer.MAX_VALUE);
//         dist[1] = 0;

//         int[] altConn = new int[n + 1];  // idx~idx, no last guard
//         for (int i = 1; i <= n; i++) {
//             altConn[i] = sc.nextInt();
//         }

//         Queue<Integer> q = new LinkedList<>();
//         q.add(1);

//         while (!q.isEmpty()) {
//             int now = q.poll();
//             if (now == 0 || now == n + 1) continue;
//             if (dist[now - 1] > dist[now]) {
//                 dist[now - 1] = dist[now] + 1;
//                 q.add(now - 1);
//             }
//             if (dist[now + 1] > dist[now]) {
//                 dist[now + 1] = dist[now] + 1;
//                 q.add(now + 1);
//             }
//             if (dist[altConn[now]] > dist[now]) {
//                 dist[altConn[now]] = dist[now] + 1;
//                 q.add(altConn[now]);
//             }
//         }

//         for (int i = 1; i <= n; i++) {
//             out.print(dist[i]);
//             out.print(SPC);
//         }
//         out.close();
//     }
// }

// class QReader {
//     private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//     private StringTokenizer tokenizer = new StringTokenizer("");

//     private String innerNextLine() {
//         try {
//             return reader.readLine();
//         } catch (IOException e) {
//             return null;
//         }
//     }

//     public boolean hasNext() {
//         while (!tokenizer.hasMoreTokens()) {
//             String nextLine = innerNextLine();
//             if (nextLine == null) {
//                 return false;
//             }
//             tokenizer = new StringTokenizer(nextLine);
//         }
//         return true;
//     }

//     public String nextLine() {
//         tokenizer = new StringTokenizer("");
//         return innerNextLine();
//     }

//     public String next() {
//         hasNext();
//         return tokenizer.nextToken();
//     }

//     public int nextInt() {
//         return Integer.parseInt(next());
//     }

//     public long nextLong() {
//         return Long.parseLong(next());
//     }
// }

// class QWriter implements Closeable {
//     private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

//     public void print(Object object) {
//         try {
//             writer.write(object.toString());
//         } catch (IOException e) {
//             return;
//         }
//     }

//     public void println(Object object) {
//         try {
//             writer.write(object.toString());
//             writer.write("\n");
//         } catch (IOException e) {
//             return;
//         }
//     }

//     @Override
//     public void close() {
//         try {
//             writer.close();
//         } catch (IOException e) {
//             return;
//         }
//     }
// }
