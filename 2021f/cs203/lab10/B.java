import java.io.*;
import java.util.ArrayList;

class Node {
    boolean vis;
    ArrayList<Node> ins = new ArrayList<>();
    ArrayList<Node> out = new ArrayList<>();
}

public class B {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static Node[] msp;
    static int n, m;

    public static void main(String[] args) throws IOException {
        n = sc.nextInt();
        m = sc.nextInt();
        if (m < n) {
            out.print(-1);
            out.close();
            return;
        }
        msp = new Node[n];
        for (int i = 0; i < n; i++) {
            msp[i] = new Node();
        }
        for (int i = 0; i < n; i++) {
            int u = sc.nextInt() - 1, v = sc.nextInt() - 1;
            msp[u].out.add(msp[v]);
            msp[v].ins.add(msp[u]);
        }
        int inStillInvalid = n, outStillInvalid = n;
        for (int i = 0; i < n; i++) {
            if (!msp[i].ins.isEmpty()) {
                inStillInvalid--;
            }
            if (!msp[i].out.isEmpty()) {
                outStillInvalid--;
            }
        }

        int rds = n;
        while (inStillInvalid > 0 || outStillInvalid > 0) {
            if (rds == m) {
                break;
            }
            int u = sc.nextInt() - 1, v = sc.nextInt() - 1;
            msp[u].out.add(msp[v]);
            msp[v].ins.add(msp[u]);
            if (msp[v].ins.size() == 1) {
                inStillInvalid--;
            }
            if (msp[u].out.size() == 1) {
                outStillInvalid--;
            }
            rds++;
        }

        while (rds <= m) {
            if (fullyScc()) {
                out.print(rds);
                out.close();
                return;
            }
            int u = sc.nextInt() - 1, v = sc.nextInt() - 1;
            msp[u].out.add(msp[v]);
            msp[v].ins.add(msp[u]);
            rds++;
        }
        out.print(-1);
        out.close();
    }

    static boolean fullyScc() {
        dfsPosCnt = dfsRevCnt = 0;
        dfsRev(msp[0]);
        if (dfsRevCnt != n) {
            return false;
        }
        for (Node n : msp) {
            n.vis = false;
        }
        dfsPos(msp[0]);
        return dfsPosCnt == n;
    }

    static int dfsPosCnt = 0, dfsRevCnt = 0;

    static void dfsPos(Node src) {
        if (src.vis) {
            return;
        }
        src.vis = true;
        for (Node nxt : src.out) {
            if (!nxt.vis) {
                dfsPos(nxt);
            }
        }
        dfsPosCnt++;
    }

    static void dfsRev(Node src) {
        if (src.vis) {
            return;
        }
        src.vis = true;
        for (Node nxt : src.ins) {
            if (!nxt.vis) {
                dfsRev(nxt);
            }
        }
        dfsRevCnt++;
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
