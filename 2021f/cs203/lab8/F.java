import java.io.*;
import java.util.StringTokenizer;

public class Main {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
    static AVL bunnies = new AVL();
    static AVL whores = new AVL();

    public static void main(String[] args) {
        long sum = 0;
        int m = sc.nextInt();
        while (m-- > 0) {
            if (sc.nextInt() == 0)
                sum += bunnies.buy(whores, sc.nextInt());
            else
                sum += whores.buy(bunnies, sc.nextInt());
        }
        out.println(sum);
        out.close();
    }
}

class AVL {
    static boolean debug = false;

    static class Node {
        int val;
        Node left, right;
        int height;

        Node(int val) {
            this.val = val;
            height = 1;
        }
    }

    Node root;

    static int height(Node node) {
        if (node == null)
            return 0;
        return node.height;
    }

    Node rotLeft(Node node) {
        Node temp = node.right;
        node.right = temp.left;
        temp.left = node;
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        temp.height = Math.max(height(temp.left), height(temp.right)) + 1;
        return temp;
    }

    Node rotRight(Node node) {
        Node temp = node.left;
        node.left = temp.right;
        temp.right = node;
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        temp.height = Math.max(height(temp.left), height(temp.right)) + 1;
        return temp;
    }

    Node fuck(Node node) {
        if (node == null)
            return null;
        if (height(node.left) > height(node.right) + 1) {
            if (height(node.left.left) < height(node.left.right)) // LL -> rotR, LR -> rotL+rotR
                node.left = rotLeft(node.left);
            node = rotRight(node);
        } else if (height(node.right) > height(node.left) + 1) {
            if (height(node.right.right) < height(node.right.left)) // RR -> rotL, RL -> rotR+rotL
                node.right = rotRight(node.right);
            node = rotLeft(node);
        }
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        return node;
    }

    int prevOrEql(int tar) {
        if (root == null)
            return -1;
        Node cur = root;
        int last = root.val > tar ? -1 : root.val;
        while (last != tar && cur != null) {
            if (tar < cur.val)
                cur = cur.left;
            else if (tar > cur.val) {
                last = cur.val;
                cur = cur.right;
            } else
                return cur.val;
        }
        return last;
    }

    int nextOrEql(int tar) {
        if (root == null)
            return -1;
        Node cur = root;
        int last = root.val < tar ? -1 : root.val;
        while (last != tar && cur != null) {
            if (tar < cur.val) {
                last = cur.val;
                cur = cur.left;
            } else if (tar > cur.val)
                cur = cur.right;
            else
                return cur.val;
        }
        return last;
    }

    Node next(Node root) {
        if (root == null || root.right == null)
            return null;
        Node cur = root.right;
        while (cur.left != null)
            cur = cur.left;
        return cur;
    }

    Node insert(Node root, int val) {
        if (root == null)
            return new Node(val);
        if (val < root.val)
            root.left = insert(root.left, val);
        else
            root.right = insert(root.right, val);
        return fuck(root);
    }

    void insert(int val) {
        root = insert(root, val);
    }

    Node delete(Node root, int val) {
        if (root == null
                || root.val == val && root.left == null && root.right == null)
            // || next(root, root.val) == null)
            return null;
        if (val < root.val)
            root.left = fuck(delete(root.left, val));
        else if (val > root.val)
            root.right = fuck(delete(root.right, val));
        else {
            if (root.left == null) {
                root = root.right;
            } else if (root.right == null) {
                root = root.left;
            } else {
                Node nxt = next(root);
                root.val = nxt.val; // * chk null
                root.right = delete(root.right, nxt.val); // ? debug
            }
        }
        return fuck(root);
    }

    void delete(int val) {
        root = delete(root, val);
    }

    boolean empty() {
        return root == null;
    }

    int buy(AVL op, int val) {
        if (op.empty()) {
            this.insert(val);
            return 0;
        } else {
            int prev = op.prevOrEql(val);
            int next = op.nextOrEql(val);
            if (debug)
                System.out.println(">>> prev: " + prev);
            if (debug)
                System.out.println(">>> next: " + next + "\n");
            if (prev == -1 || next == -1) {
                op.delete(prev == -1 ? next : prev);
                return Math.abs(val - (prev == -1 ? next : prev));
            } else if (Math.abs(val - prev) == Math.abs(val - next)) {
                op.delete(prev);
                return Math.abs(val - prev);
            } else {
                if (Math.abs(val - prev) < Math.abs(val - next)) {
                    op.delete(prev);
                    return Math.abs(val - prev);
                } else {
                    op.delete(next);
                    return Math.abs(val - next);
                }
            }
        }
    }
}

class QReader {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    StringTokenizer tokenizer = new StringTokenizer("");

    String innerNextLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    boolean hasNext() {
        while (!tokenizer.hasMoreTokens()) {
            String nextLine = innerNextLine();
            if (nextLine == null) {
                return false;
            }
            tokenizer = new StringTokenizer(nextLine);
        }
        return true;
    }

    String nextLine() {
        tokenizer = new StringTokenizer("");
        return innerNextLine();
    }

    String next() {
        hasNext();
        return tokenizer.nextToken();
    }

    int nextInt() {
        return Integer.parseInt(next());
    }

    long nextLong() {
        return Long.parseLong(next());
    }
}

class QWriter implements Closeable {
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

    void print(Object object) {
        try {
            writer.write(object.toString());
        } catch (IOException e) {
            return;
        }
    }

    void println(Object object) {
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
