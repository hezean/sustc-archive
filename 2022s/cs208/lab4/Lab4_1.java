import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Lab4_1 {
    public static final int modulo = 1000000007;

    public static void main(String[] args) {
        Node c = new Node(0, 0, 0, 0);
        c.rem = C;
        set_max(points[1], c);

        Queue<Node> queue = new LinkedList<>(); // offer poll
        while (!queue.isEmpty()) {
            Node temp = queue.poll();
            for (int i = 0; i < temp.next.size(); i++) {
                Node curr = temp.next.get(i);
                if (temp.logCE > 0 || temp.rem >= curr.h) {
                    set_max(curr, temp);
                }
                curr.in_degree--;
                if (curr.in_degree == 0) {
                    queue.offer(curr);
                }
            }
        }
        double max = 0;
        long res = 0;
        boolean isGood = false;
        double a = 0;
        long b = 0;
        for (int i = 1; i < N + 1; i++) {
            if (points[i].out_degree == 0) {
                a = points[i].logCE;
                b = points[i].rem;
                if (a > 0 || b > 0) {
                    isGood = true;
                } else {
                    continue;
                }
                if (a > max) {
                    max = a;
                    res = b;
                } else {
                    if (max == 0) {
                        if (b > res) {
                            res = b;
                        }
                    }
                }
            }
        }
        if (isGood) {
            out.println(res);
        } else {
            out.println(-1);
        }
        out.close();
    }

    public static void set_max(Node curr, Node temp) {  // temp为原结点
        double value = curr.logCE;
        long rem = curr.rem;
        if (temp.logCE > 0) {
            double d = temp.logCE + Math.log(curr.b);
            if (d > curr.logCE) {
                curr.logCE = d;
                curr.rem = (temp.rem * curr.b) % modulo;
            }
        } else {
            long a = temp.rem + curr.a;
            long b = temp.rem * curr.b;
            long c = curr.c;
            long max = a;
            double log_max = 0;
            long rem_max = 0;
            if (b > max) max = b;
            if (c > max) max = c;  //三种方法的最值
            if (max >= modulo) {
                log_max = Math.log(max);
                rem_max = max % modulo;
                if (value > 0) {
                    if (log_max > value) {
                        curr.logCE = log_max;
                        curr.rem = rem_max;
                    }
                } else {
                    curr.logCE = log_max;
                    curr.rem = rem_max;
                }
            } else {
                if (value == 0) {
                    if (max > rem) {
                        curr.rem = max;
                    }
                }
            }
        }
    }
}
