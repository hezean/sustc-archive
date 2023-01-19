//import javax.naming.NoInitialContextException;
//import java.io.*;
//import java.util.StringTokenizer;
//
//public class B1 {
//    static int[] arr;
//    static NodeInterval[] roots;
//    public static void main(String[] args) {
//
//
//
//
//
//
//
////        long sta = System.currentTimeMillis();
//
//
//
//
//
//
//
//        QReader in = new QReader();
//        QWriter out = new QWriter();
//        int n = in.nextInt();
//        int q = in.nextInt();
//        arr = new int[n+1];
//        roots = new NodeInterval[n+1];
//        int max = -1;
//        int min = Integer.MAX_VALUE;
//        for (int i = 1; i <= n; i++) {
//            arr[i] = in.nextInt();
//            if(arr[i] < min) min = arr[i];
//            if(arr[i] > max) max = arr[i];
//            //roots[i] = new NodeInterval(1, 1000000000, i);
//        }
//        int[][] query = new int[q][2];
//        for (int i = 0; i < q; i++) {
//            query[i][0] = in.nextInt();
//            query[i][1] = in.nextInt();
//        }
//
//        roots[1] = new NodeInterval(min, max, 1);
//        NodeInterval first_cur = roots[1];
//        while (first_cur.a != first_cur.b) {
//            if(arr[1] <= first_cur.midPoint()) {
//                first_cur.leSon = new NodeInterval(first_cur.a, first_cur.midPoint(), 1);
//                first_cur = first_cur.leSon;
//            } else {
//                first_cur.riSon = new NodeInterval(first_cur.midPoint() + 1, first_cur.b, 1);
//                first_cur = first_cur.riSon;
//            }
//        }
//        // build other
//        NodeInterval curr;
//        for (int i = 2; i <= n; i++) {
//            //prev = roots[i - 1];
//            roots[i] = roots[i - 1].copyPlusCnt();
//            curr = roots[i];
//            while (curr.a != curr.b) {
//                if(arr[i] <= curr.midPoint()) {
//                    if(curr.leSon != null)
//                        curr.leSon = curr.leSon.copyPlusCnt();
//                    else
//                        curr.leSon = new NodeInterval(curr.a, curr.midPoint(), 1);
//                    curr = curr.leSon;
//                } else {
//                    if(curr.riSon != null)
//                        curr.riSon = curr.riSon.copyPlusCnt();
//                    else
//                        curr.riSon = new NodeInterval(curr.midPoint() + 1, curr.b, 1);
//                    curr = curr.riSon;
//                }
//            }
//        }
//        roots[0] = null;
//        for (int i = 0; i < q; i++) {
//            int l = query[i][0];
//            int r = query[i][1];
//            out.println(Quey(roots[l-1], roots[r], min, max, (r - l)/2 + 1));
//        }
//
//
////        out.println(System.currentTimeMillis() - sta);
//
//
//
//        out.close();
//    }
//    public static int Quey(NodeInterval ln, NodeInterval rn, int qa, int qb, int rank) {
//        if(qa == qb) return qa;
//        int mid = (qb - qa) / 2 + qa;
//        int l_diff = leftCnt(rn) - leftCnt(ln);
//        if(rank <= l_diff) return Quey(ln == null ? null : ln.leSon, rn == null ? null : rn.leSon, qa, mid, rank);
//        else return               Quey(ln == null ? null : ln.riSon, rn == null ? null : rn.riSon, mid+1, qb, rank - l_diff);
//    }
//
//    public static int leftCnt(NodeInterval node) {
//        if(node == null)
//            return 0;
//        return node.leCnt();
//    }
//
//}
//
//class NodeInterval {
//    int a, b;// interval
//    int cnt;
//    NodeInterval leSon, riSon;
//
//    public NodeInterval(int a, int b, int cnt) {
//        this.a = a;
//        this.b = b;
//        this.cnt = cnt;
//    }
//
//    public int leCnt() {
//        if (leSon == null)
//            return 0;
//        else return leSon.cnt;
//    }
//
//    public int riCnt() {
//        if (riSon == null)
//            return 0;
//        else return riSon.cnt;
//    }
//
//    public NodeInterval copy() {
//        NodeInterval cp = new NodeInterval(this.a, this.b, this.cnt);
//        cp.leSon = this.leSon;
//        cp.riSon = this.riSon;
//        return cp;
//    }
//
//    public NodeInterval copyPlusCnt() {
//        NodeInterval cp = this.copy();
//        cp.cnt++;
//        return cp;
//    }
//
//    public NodeInterval next() {
//        if (this.leSon != null)
//            return leSon;
//        if (this.riSon != null)
//            return riSon;
//        return null;
//    }
//
//    public int midPoint() {
//        return (b - a) / 2 + a;
//    }
//
////    public void setLeSon(NodeInterval leSon) {
////        if (this.leSon != null) System.out.println("wrong leSon");
////        this.leSon = leSon;
////    }
////
////    public NodeInterval getLeSon() {
////        return leSon;
////    }
////
////    public void setRiSon(NodeInterval riSon) {
////        if (this.riSon != null) System.out.println("wrong riSon");
////        this.riSon = riSon;
////    }
////
////    public NodeInterval getRiSon() {
////        return riSon;
////    }
//}
//
//class QReader {
//    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//    private StringTokenizer tokenizer = new StringTokenizer("");
//
//    private String innerNextLine() {
//        try {
//            return reader.readLine();
//        } catch (IOException e) {
//            return null;
//        }
//    }
//
//    public boolean hasNext() {
//        while (!tokenizer.hasMoreTokens()) {
//            String nextLine = innerNextLine();
//            if (nextLine == null) {
//                return false;
//            }
//            tokenizer = new StringTokenizer(nextLine);
//        }
//        return true;
//    }
//
//    public String nextLine() {
//        tokenizer = new StringTokenizer("");
//        return innerNextLine();
//    }
//
//    public String next() {
//        hasNext();
//        return tokenizer.nextToken();
//    }
//
//    public double nextDouble() {
//        return Double.parseDouble(next());
//    }
//
//    public int nextInt() {
//        return Integer.parseInt(next());
//    }
//
//    public long nextLong() {
//        return Long.parseLong(next());
//    }
//}
//class QWriter implements Closeable {
//    private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
//
//    public void print(Object object) {
//        try {
//            writer.write(object.toString());
//        } catch (IOException e) {
//            return;
//        }
//    }
//
//    public void println(Object object) {
//        try {
//            writer.write(object.toString());
//            writer.write("\n");
//        } catch (IOException e) {
//            return;
//        }
//    }
//
//    @Override
//    public void close() {
//        try {
//            writer.close();
//        } catch (IOException e) {
//            return;
//        }
//    }
//}