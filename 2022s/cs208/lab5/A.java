//import java.io.*;
//import java.util.*;
//
//public class A {
//    static QReader sc = new QReader();
//    static QWriter out = new QWriter();
//
//    public static void main(String[] args) {
//        LinkedHashMap lru = new LinkedHashMap(sc.nextInt());
//        int m = sc.nextInt();
//        for (int i = 0; i < m; i++) {
//            String sel = sc.next();
//            if (sel.charAt(0) == 'p') lru.put(sc.nextInt(), sc.nextInt());
//            else out.println(lru.access(sc.nextInt()));
////            out.println(lru);
//        }
//        out.close();
//    }
//}
//
//class Node {
//    int key, val;
//    Node next, prev;
//
//    public Node(int key, int val) {
//        this.key = key;
//        this.val = val;
//    }
//}
//
//class LinkedHashMap {
//    int cap, size;
//    HashMap<Integer, Node> ptr2node;
//    Node head, rear;
//
////    @Override
////    public String toString() {
////        StringBuilder ls = new StringBuilder();
////        Node ptr = head;
////        while (ptr != null) {
////            ls.append(ptr.key).append(", ");
////            ptr = ptr.next;
////        }
////        return "LinkedHashMap{" +
////                "size=" + size +
////                ", list=" + ls +
////                '}';
////    }
//
//    LinkedHashMap(int cap) {
//        this.cap = cap;
//        ptr2node = new HashMap<>(cap);
//        head = new Node(-1, 0);
//        rear = new Node(-2, 0);
//        head.next = rear;
//        rear.prev = head;
//    }
//
//    void remove(int k) {
//        Node n = ptr2node.get(k);
//        if (n == null) return;
//        ptr2node.remove(k);
//        n.prev.next = n.next;
//        n.next.prev = n.prev;
//        --size;
//    }
//
//    // least used
//    void remove() {
//        if (head.next == rear) return;
//        Node lu = head.next;
//        head.next = head.next.next;
//        head.next.prev = head;
//        ptr2node.remove(lu.key);
//        --size;
//    }
//
//    void add(int k, int v) {
//        Node n = new Node(k, v);
//        ptr2node.put(k, n);
//        n.prev = rear.prev;
//        n.prev.next = n;
//        n.next = rear;
//        rear.prev = n;
//        ++size;
//    }
//
//    void put(int k, int v) {
//        remove(k);
//        while (size >= cap) remove();
//        add(k, v);
//    }
//
//    int access(int k) {
//        Node n = ptr2node.get(k);
//        if (n == null) return -1;
//        remove(k);
//        ptr2node.put(k, n);
//        ++size;
//        n.prev = rear.prev;
//        n.prev.next = n;
//        n.next = rear;
//        rear.prev = n;
//        return n.val;
//    }
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
//        } catch (IOException ignored) {
//        }
//    }
//
//    public void println(Object object) {
//        try {
//            writer.write(object.toString());
//            writer.write("\n");
//        } catch (IOException ignored) {
//        }
//    }
//
//    @Override
//    public void close() {
//        try {
//            writer.close();
//        } catch (IOException ignored) {
//        }
//    }
//}
