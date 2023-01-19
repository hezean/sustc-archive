
/**
 * DAG 两点间路径 条数固定
 * f(j) ~> 以 j 为终点的路径数
 * ~~> f(j) = sum([(f(x) + 1) for x in j.conn])
 * ~~> 要确定所有相邻边的 f 均有效，拓扑序求
 *
 * Topological Sort <BFS>:
 * - 不一定唯一
 * - queue.add: 所有入度为 0 的节点
 * - cur = q.poll() <DAG -> no need for var `vis`>
 * - for x in cur.conn:
 * - x.inDegree--
 * - if x.inDegree == 0:
 * - q.add(x)
 * - 队列内元素出现过的顺序即拓扑序
 *
 * 求不同的拓扑序?
 * - 同一时间内在队列的节点均可乱序 ==> *= q.size()
 *
 * ? f(i) = sum(f(i.prev) + a_i)
 */

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

class Node {
    int pos, inDeg;
    long cnt;
    ArrayList<Node> conn = new ArrayList<>();

    Node(int p) {
        pos = p;
    }
}

public class F {

    static boolean DEBUG = false;

    static QReader sc = new QReader();
    static QWriter out = new QWriter();

    public static void main(String[] args) {
        int t = sc.nextInt();
        while (t-- > 0) {
            int n = sc.nextInt(), m = sc.nextInt();
            int[] arr1 = new int[n], arr2 = new int[n];
            Node[] nds = new Node[n];
            for (int i = 0; i < n; ++i) {
                arr1[i] = sc.nextInt();
                arr2[i] = sc.nextInt();
                nds[i] = new Node(i);
            }
            for (int i = 0; i < m; ++i) {
                int u = sc.nextInt() - 1, v = sc.nextInt() - 1;
                nds[u].conn.add(nds[v]);
                ++nds[v].inDeg;
            }

            ArrayList<Node> starters = new ArrayList<>();
            for (Node nd : nds) {
                if (nd.inDeg == 0) {
                    starters.add(nd);
                }
            }

            for (Node nd : starters) {
                topSortHandleCnt(nd, arr1);
            }

            long ans = 0;
            for (int i = 0; i < n; ++i) {
                // ans += (long) nds[i].cnt * arr2[i] % 1000000007;
                ans = ((ans % 1000000007)
                        + (nds[i].cnt % 1000000007)
                                * (arr2[i] % 1000000007))
                        % 1000000007;
            }
            out.println(ans);
        }
        out.close();
    }

static void topSortHandleCnt(Node st, int[] arr1) {
    Queue<Node> q = new LinkedList<>();
    q.add(st);
    while (!q.isEmpty()) {
        Node h = q.poll();
        for (Node c : h.conn) {
            c.cnt += h.cnt % 1000000007 + arr1[h.pos] % 1000000007;
            c.cnt %= 1000000007;
            if (--c.inDeg == 0) {
                if (DEBUG) {
                    out.println(c.pos + " " + c.cnt);
                }
                q.add(c);
            }
        }
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
