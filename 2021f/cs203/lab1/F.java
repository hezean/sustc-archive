import java.util.Scanner;

public class F {
    public static void main(String[] args) {
        int n;
        String s;
        Scanner sc = new Scanner(System.in);
        n = Integer.parseInt(sc.nextLine());
        while (n-- > 0) {
            s = sc.nextLine();
            judge(s);
        }
    }

    static void judge(String s) {
        String[] pts = s.split("\\+");
        for (String p : pts) {
            if (p.contains("^")) {
                int c = Integer.parseInt(p.split("\\^")[0]);
                if (c != 0) {
                    System.out.println("no");
                    return;
                }
            } else if (p.contains("/sinx") || p.contains("/cosx") || p.contains("/x")) {
                int c = Integer.parseInt(p.split("/")[0]);
                if (c != 0) {
                    System.out.println("no");
                    return;
                }
            } else if (p.contains("sinx")) {
                int c = Integer.parseInt(p.split("s")[0]);
                if (c != 0) {
                    System.out.println("no");
                    return;
                }
            } else if (p.contains("cosx")) {
                int c = Integer.parseInt(p.split("c")[0]);
                if (c != 0) {
                    System.out.println("no");
                    return;
                }
            } else if (p.contains("x")) {
                if (!p.equals("0x")) {
                    System.out.println("no");
                    return;
                }
            } else if (!p.equals("0")) {
                System.out.println("no");
                return;
            }
        }
        System.out.println("yes");
    }
}