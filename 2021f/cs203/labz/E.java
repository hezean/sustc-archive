import java.io.*;
 
class Node {
    int val;
    Node l, r;
 
    Node(int v) {
        val = v;
    }
}
 
public class Main {
    static QReader sc = new QReader();
    static QWriter out = new QWriter();
 
    static int[] inorder = new int[1024];
    static int[] postorder = new int[1024];
 
    static String NAN = "";
 
    public static void main(String[] args) throws IOException {
        int q = sc.nextInt();
        for (int i = 0; i < q; i++) {
            int n = sc.nextInt();
            for (int j = 0; j < n; j++) {
                inorder[j] = sc.nextInt();
            }
            for (int j = 0; j < n; j++) {
                postorder[j] = sc.nextInt();
            }
 
            Node root = build(inorder, 0, n - 1, postorder, 0, n - 1);  // vis, rev
            preorder(root);
            out.println(NAN);
        }
        out.close();
    }
 
    static Node build(int[] in, int ins, int ine, int[] post, int pos, int poe) {
        if (ins > ine || pos > poe) return null;
        Node h = new Node(post[poe]);
        int subBias;
        for (subBias = 0; subBias < ine - ins; subBias++) {
            if (in[ins + subBias] == post[poe]) break;
        }
        h.l = build(in, ins, ins + subBias - 1, post, pos, pos + subBias - 1);
        h.r = build(in, ins + subBias + 1, ine, post, pos + subBias, poe - 1);
        return h;
    }
 
    static String SPACE = " ";
 
    static void preorder(Node root) {
        if (root == null) return;
        out.print(root.val);
        out.print(SPACE);
        preorder(root.l);
        preorder(root.r);
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