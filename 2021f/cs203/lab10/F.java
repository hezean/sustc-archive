import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

class Node {
    boolean vis;
    int sccId = -1;
    ArrayList<Node> ins = new ArrayList<>();
    ArrayList<Node> out = new ArrayList<>();
}

public class Main {
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
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt() - 1, v = sc.nextInt() - 1;
            msp[u].out.add(msp[v]);
            msp[v].ins.add(msp[u]);
        }
        int sccCnt = divScc();
        int[] sccDegIn = new int[sccCnt];
        int[] sccDegOut = new int[sccCnt];

        // System.err.println(sccCnt);

        for (int i = 0; i < n; i++) {
            for (Node n : msp[i].out) {
                if (n.sccId != msp[i].sccId) {
                    sccDegOut[msp[i].sccId]++;
                    sccDegIn[n.sccId]++;
                }
            }
        }

        int sccIns = 0, sccOuts = 0;
        for (int i = 0; i < sccCnt; i++) {
            if (sccDegIn[i] == 0) {
                sccIns++;
            }
            if (sccDegOut[i] == 0) {
                sccOuts++;
            }
        }

        // System.err.println(sccIns + " " + sccOuts);

        out.print(sccCnt == 1 ? 0 : Math.max(sccIns, sccOuts));
        out.close();
    }

    static int divScc() {
        for (Node n : msp) {
            if (!n.vis) {
                dfsRev(n);
            }
        }
        for (Node n : msp) {
            n.vis = false;
        }
        while (!s.isEmpty()) {
            Node n = s.pop();
            if (!n.vis) {
                dfsPos(n);
                sccCnt++;
            }
        }
        return sccCnt;
    }

    static Stack<Node> s = new Stack<>();

    static int sccCnt = 0;

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
        s.push(src);
    }

    static void dfsPos(Node src) {
        if (src.vis) {
            return;
        }
        src.vis = true;
        src.sccId = sccCnt;
        for (Node nxt : src.out) {
            if (!nxt.vis) {
                dfsPos(nxt);
            }
        }
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
