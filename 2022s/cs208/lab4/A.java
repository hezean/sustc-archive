import java.io.*;
import java.util.*;

public class A {
   static QReader sc = new QReader();
   static QWriter out = new QWriter();
   static final int MOD = 1000000007;

   public static void main(String[] args) {
       int n = sc.nextInt(), m = sc.nextInt();
       long c = sc.nextInt();

       Node[] nds = new Node[n];
       for (int i = 0; i < n; i++) nds[i] = new Node(sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt());
       for (int i = 0; i < m; i++) {
           int x = sc.nextInt() - 1, y = sc.nextInt() - 1;
           nds[x].conn.add(nds[y]);
           ++nds[y].inDeg;
       }

       if (c < nds[0].h) {
           out.print(-1);
           out.close();
           return;
       }

       update(c, 0, nds[0]);
       Queue<Node> q = new LinkedList<>();
       q.add(nds[0]);
       while (!q.isEmpty()) {
           Node h = q.poll();
           for (Node nd : h.conn) {
               --nd.inDeg;
               update(h.mod, h.log, nd);
               if (nd.inDeg == 0) q.add(nd);
           }
       }

       double lC = 0;
       long mC = -1;

       for (Node h : nds) {
           if (!h.conn.isEmpty()) continue;
           if (h.log > lC) {
               lC = h.log;
               mC = h.mod;
           } else if (lC == 0) {
               mC = Math.max(mC, h.mod);
           }
       }

       out.print(mC);
       out.close();
   }

   static void update(long mod, double log, Node to) {
       if (log <= 0 && mod < to.h) return;
       if (log > 0) {
           double rw = log + Math.log(to.b);
           if (rw > to.log) {
               to.log = rw;
               to.mod = mod * to.b % MOD;
           }
       } else {
           long rw1 = mod + to.a;
           long rw2 = mod * to.b;
           long rw3 = to.c;
           long max = Math.max(Math.max(rw1, rw2), rw3);
           double maxLog = max >= MOD ? Math.log(max) : 0;
           if (maxLog > to.log
                   || to.log == 0 && max > to.mod) {
               to.log = maxLog;
               to.mod = max % MOD;
           }
       }
   }
}

class Node {
   long h, a, b, c;
   long mod = -1;
   double log;
   int inDeg;
   List<Node> conn = new LinkedList<>();

   public Node(int h, int a, int b, int c) {
       this.h = h;
       this.a = a;
       this.b = b;
       this.c = c;
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
       } catch (IOException ignored) {
       }
   }

   public void println(Object object) {
       try {
           writer.write(object.toString());
           writer.write("\n");
       } catch (IOException ignored) {
       }
   }

   @Override
   public void close() {
       try {
           writer.close();
       } catch (IOException ignored) {
       }
   }
}
