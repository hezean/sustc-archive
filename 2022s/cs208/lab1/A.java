// import java.io.*;
// import java.util.*;

// public class Main {
//     static QReader sc = new QReader();
//     static QWriter out = new QWriter();

//     public static void main(String[] args) {
//         int n = sc.nextInt();
//         Queue<Integer> qMan = new LinkedList<>();

//         Map<String, Integer> man2id = new HashMap<>(n);
//         Map<String, Integer> woman2id = new HashMap<>(n);

//         String[] id2man = new String[n];
//         String[] id2woman = new String[n];

//         int[][] man2woman = new int[n][n]; // man2woman[i][j]=x: man(i)'s j-th lover is woman(x)
//         int[][] woman2manRev = new int[n][n]; // woman2manRev[i][j]=x: man(j) is the x-th lover for woman(i)

//         int[] manReadyToProposeIdx = new int[n]; // manReadyToProposeIdx[i]=x: man(i) WILL propose his x's lover
//         int[] womanLovedBy = new int[n]; // womanLovedBy[i]=x: woman(i) is mating with man(x)

//         for (int i = 0; i < n; i++) {
//             womanLovedBy[i] = -1;
//         }

//         for (int i = 0; i < n; i++) {
//             String name = sc.next();
//             man2id.put(name, i);
//             id2man[i] = name;
//             qMan.add(i);
//         }

//         for (int i = 0; i < n; i++) {
//             String name = sc.next();
//             woman2id.put(name, i);
//             id2woman[i] = name;
//         }

//         for (int i = 0; i < n; i++) {
//             for (int j = 0; j < n; j++) {
//                 man2woman[i][j] = woman2id.get(sc.next());
//             }
//         }

//         for (int i = 0; i < n; i++) {
//             for (int j = 0; j < n; j++) {
//                 int man = man2id.get(sc.next());
//                 woman2manRev[i][man] = j;
//             }
//         }

//         // start proposing

//         while (!qMan.isEmpty()) {
//             int man = qMan.poll();
//             if (manReadyToProposeIdx[man] >= n)
//                 continue;
//             int woman2bProposed = man2woman[man][manReadyToProposeIdx[man]];
//             if (womanLovedBy[woman2bProposed] == -1) {
//                 womanLovedBy[woman2bProposed] = man;
//             } else if (woman2manRev[woman2bProposed][man] < woman2manRev[woman2bProposed][womanLovedBy[woman2bProposed]]) {
//                 qMan.add(womanLovedBy[woman2bProposed]);
//                 womanLovedBy[woman2bProposed] = man;
//             } else {
//                 qMan.add(man);
//             }
//             ++manReadyToProposeIdx[man];
//         }

//         // output ans

//         for (int w = 0; w < n; w++) {
//             out.print(id2man[womanLovedBy[w]]);
//             out.print(' ');
//             out.print(id2woman[w]);
//             out.print('\n');
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
