import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class E {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static ArrayList<Integer> ans = new ArrayList<>();

    public static void main(String[] args) {
        int n = sc.nextInt();
        Node[] tree = new Node[n];
        for (int i = 0; i < n; i++) {
            tree[i] = new Node(i + 1);
        }

        for (int i = 0; i < n - 1; i++) {
            int t1 = sc.nextInt() - 1;
            int t2 = sc.nextInt() - 1;
            tree[t1].conn.add(tree[t2]);
            tree[t2].conn.add(tree[t1]);
        }
        String lfSeq = sc.nextLine();
        String[] slfSeq = lfSeq.split(" ");
        int[] tarSeq = new int[slfSeq.length];
        for (int i = 0; i < tarSeq.length; i++) {
            tarSeq[i] = Integer.parseInt(slfSeq[i]);
        }
        dfs(tree[0], tarSeq[0]);
        for (int i = 1; i < tarSeq.length; i++) {
            rst(tree);
            dfs(tree[tarSeq[i - 1] - 1], tarSeq[i]);
        }
        rst(tree);
        dfs(tree[tarSeq[tarSeq.length - 1] - 1], 1);
        ans.add(1);
        if (ans.size() != 2 * n - 1) {
            out.print("-1");
        } else {
            for (int i : ans) {
                out.print(i);
                out.print(" ");
            }
        }
        out.close();
    }

static void dfs(Node root, int tar) {
    Queue<Node> q = new LinkedList<>();
    q.add(root);
    while (!q.isEmpty()) {
        for (Node i : q.peek().conn) {
            if (!i.vis) {
                q.add(i);
                i.path.addAll(0, q.peek().path);
                i.path.add(q.peek().id);
                if (i.id == tar) {
                    ans.addAll(i.path);
                    break;
                }
            }
        }
        q.peek().vis = true;
        q.remove();
    }
}

    static void rst(Node[] tree) {
        for (Node i : tree) {
            i.vis = false;
            i.path.clear();
        }
    }
}

class Node {
    int id;
    boolean vis;
    ArrayList<Integer> path;
    ArrayList<Node> conn;

    Node(int i) {
        id = i;
        path = new ArrayList<>();
        conn = new ArrayList<>();
    }
}


class QReader {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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
    private final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

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
