import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Lab3_1 {
    public static void main(String[] args) {
        QReader in = new QReader();
        QWriter out = new QWriter();

        int N = in.nextInt();
        int M = in.nextInt();
        Node[] species = new Node[N + 1];
        for (int i = 0; i < N + 1; i++) {
            species[i] = new Node(i);
        }
        for (int i = 0; i < M; i++) {
            int pre = in.nextInt();
            int next = in.nextInt();
            species[pre].next.add(species[next]);
            species[pre].out_degree++;
            species[next].pre.add(species[pre]);
            species[next].in_degree++;
        }
        Queue<Node> queue = new LinkedList<>(); // offer poll
        for (int i = 1; i < N + 1; i++) {
            if (species[i].in_degree == 0) {
                species[i].res_in = 1;
                queue.offer(species[i]);
            }
        }
        int value = 0;
        while (!queue.isEmpty()) {
            value = queue.poll().value;
            for (int i = 0; i < species[value].next.size(); i++) {
                species[value].next.get(i).in_degree--;
                species[value].next.get(i).res_in += species[value].res_in;
                species[value].next.get(i).res_in %= 1000000007;
                if (species[value].next.get(i).in_degree == 0) {
                    queue.offer(species[value].next.get(i));
                }
            }
        }
        for (int i = 1; i < N + 1; i++) {
            if (species[i].out_degree == 0) {
                species[i].res_out = 1;
                queue.offer(species[i]);
            }
        }
        while (!queue.isEmpty()) {
            value = queue.poll().value;
            for (int i = 0; i < species[value].pre.size(); i++) {
                species[value].pre.get(i).out_degree--;
                species[value].pre.get(i).res_out += species[value].res_out;
                species[value].pre.get(i).res_out %= 1000000007;
                if (species[value].pre.get(i).out_degree == 0) {
                    queue.offer(species[value].pre.get(i));
                }
            }
        }
        for (int i = 1; i < N + 1; i++) {
            long res = (long) species[i].res_in * species[i].res_out;
            res %= 1000000007;
            out.print(res + " ");
        }

        out.close();
    }

    private static class Node {
        ArrayList<Node> pre = new ArrayList<>();
        ArrayList<Node> next = new ArrayList<>();
        int in_degree;
        int out_degree;
        int res_in;
        int res_out;
        int value;

        public Node(int value) {
            this.value = value;
        }
    }
}

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
//    public int nextInt() {
//        return Integer.parseInt(next());
//    }
//
//    public long nextLong() {
//        return Long.parseLong(next());
//    }
//}
//
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
