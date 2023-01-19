import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Lab11_1 {
    public static long[][] opt;
    public static Node[] points;

    public static void main(String[] args) {
        QReader in = new QReader();
        QWriter out = new QWriter();

        int N = in.nextInt();
        points = new Node[N + 1];
        for (int i = 0; i < N + 1; i++) {
            points[i] = new Node(i);
        }
        int u, v, w = 0;
        opt = new long[N + 1][2];
        for (int i = 0; i < N - 1; i++) {
            u = in.nextInt();
            v = in.nextInt();
            w = in.nextInt();
            points[u].next.add(new Edge(points[v], w));
            points[v].next.add(new Edge(points[u], w));
        }

        points[1].isVisited = true;
        dfs(1);
        out.println(opt[1][1]);
        out.close();
    }

    public static void dfs(int i) {
        Edge temp;
        long max = 0;
        long comp = 0;
        for (int j = 0; j < points[i].next.size(); j++) {
            temp = points[i].next.get(j);
            if (!temp.node.isVisited) {
                temp.node.isVisited = true;
                dfs(temp.node.index);
                opt[i][0] += opt[temp.node.index][1];
                comp = temp.value - opt[temp.node.index][1] + opt[temp.node.index][0];
                max = (comp > max) ? comp : max;
            }
        }
        opt[i][1] = max + opt[i][0];
    }

    private static class Node {
        int index;
        boolean isVisited;
        ArrayList<Edge> next = new ArrayList<>();

        public Node(int index) {
            this.index = index;
        }
    }

    private static class Edge {
        Node node;
        int value;

        public Edge(Node node, int value) {
            this.node = node;
            this.value = value;
        }
    }
}
