import java.io.*;
import java.util.*;

public class Lab5_2 {
    public static void main(String[] args) {
        QReader in = new QReader();
        QWriter out = new QWriter();

        int N = in.nextInt();
        int M = in.nextInt();
        Node[] points = new Node[N + 1];
        for (int i = 1; i < N + 1; i++) {
            points[i] = new Node(i);
        }
        for (int i = 0; i < M; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            int w = in.nextInt();
            points[x].next.add(new Edge(points[y], w,i));
            points[y].next.add(new Edge(points[x], w,i));
        }
        int P = in.nextInt();
        int[] res = new int[M];
        for (int i = 0; i < P; i++) {
// delete
            for (int j = 1; j < N + 1; j++) {
                points[j].isVisited = false;
                points[j].f_isVisited = false;
                points[j].value = -1;
            }
//begin
            int s = in.nextInt();
            int t = in.nextInt();
            points[s].isVisited = true;
            points[s].value = 0;
            PriorityQueue<Node> queue = new PriorityQueue<>(); // offer poll
            queue.offer(points[s]);
// dijkstra
            while (!queue.isEmpty()) {
                Node curr = queue.poll();
                curr.isVisited = true;
                for (int j = 0; j < curr.next.size(); j++) {
                    Edge temp = curr.next.get(j);
                    if (temp.node.isVisited) {
                        continue;
                    }
                    if (temp.node.value == -1) {
                        temp.node.value = curr.value + temp.value;
                        queue.offer(temp.node);
                    } else if (temp.node.value > curr.value + temp.value) {
                        queue.remove(temp.node);
                        temp.node.value = curr.value + temp.value;
                        queue.offer(temp.node);
                    }
                }
            }
// bfs
            points[t].f_isVisited = true;
            Queue<Node> queue1 = new LinkedList<>();// offer poll
            queue1.offer(points[t]);
            while (!queue1.isEmpty()){
                Node temp = queue1.poll();
                for (int j = 0; j < temp.next.size(); j++) {
                    Edge curr = temp.next.get(j);
                    if (temp.value == curr.node.value+ curr.value){
                        res[curr.index]++;
                        if (!curr.node.f_isVisited){
                            curr.node.f_isVisited = true;
                            queue1.offer(curr.node);
                        }
                    }
                }
            }
        }
// output
        for (int i = 0; i < M; i++) {
            out.println(res[i]);
        }
        out.close();
    }


    private static class Node implements Comparable<Node> {
        int x;
        long value = -1;// 到起点的距离
        boolean isVisited;
        boolean f_isVisited;
        ArrayList<Edge> next = new ArrayList<>();

        public Node(int x) {
            this.x = x;
        }

        @Override
        public int compareTo(Node o) {
            return Long.compare(this.value,o.value);
        }
    }

    private static class Edge {
        Node node;
        int value;
        int index;
        public Edge(Node node, int value,int index) {
            this.node = node;
            this.value = value;
            this.index = index;
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
