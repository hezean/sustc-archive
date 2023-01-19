import java.util.Scanner;

public class E {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        long l, r;
        int llen, rlen;
        while (sc.hasNext()) {
            l = sc.nextLong();
            r = sc.nextLong();

            llen = Long.toString(l).length();
            rlen = Long.toString(r).length();

           genMagAsc(l, llen);
            System.out.print(" ");
            genMagDesc(r, rlen);
            System.out.println();
        }
        sc.close();
    }

    static void genMagAsc(long l, int llen) {
        if (llen == 1) {
            switch ((int) l) {
                case 0:
                    System.out.print("0");
                    return;
                case 1:
                    System.out.print("1");
                    return;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    System.out.print("8");
                    return;
                case 9:
                    System.out.print("11");
                    return;
            }
        }
        StringBuilder n = new StringBuilder();
        int st = (int) Math.pow(5, llen / 2 - 1) - 1;
        int f = 0;
        do {
            n.replace(0, n.length(), "");
            if (llen % 2 == 1) {
                switch (f % 3) {
                    case 0:
                        n.append('0');
                        break;
                    case 1:
                        n.append('1');
                        break;
                    case 2:
                        n.append('8');
                }
                if (f++ % 3 == 0)
                    st++;
            } else st++;
            n.insert(0, Long.toString(st, 5)
                    .replace('2', '6')
                    .replace('3', '8')
                    .replace('4', '9'));

            flip(n, llen % 2 == 1);
        } while (!inRange(l, n, true));
    }

    static void genMagDesc(long r, int rlen) {
        if (rlen == 1) {
            switch ((int) r) {
                case 0:
                    System.out.print("0");
                    return;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    System.out.print("1");
                    return;
                case 8:
                case 9:
                    System.out.print("8");
                    return;
            }
        }
        StringBuilder n = new StringBuilder();
        int st = (int) Math.pow(5, rlen / 2);
        int f = 0;
        do {
            n.replace(0, n.length(), "");
            if (rlen % 2 == 1) {
                switch (f % 3) {
                    case 0:
                        n.append('8');
                        break;
                    case 1:
                        n.append('1');
                        break;
                    case 2:
                        n.append('0');
                }
                if (Math.abs(f++ % 3) == 0)
                    st--;
            } else st--;
            n.insert(0, Long.toString(st, 5)
                    .replace('2', '6')
                    .replace('3', '8')
                    .replace('4', '9'));

            flip(n, rlen % 2 == 1);
        } while (!inRange(r, n, false));
    }

    static void flip(StringBuilder n, boolean lenOdd) {
        int len = lenOdd ? n.length() - 1 : n.length();
        for (int i = len - 1; i >= 0; i--) {
            switch (n.charAt(i)) {
                case '0':
                    n.append('0');
                    break;
                case '1':
                    n.append('1');
                    break;
                case '6':
                    n.append('9');
                    break;
                case '8':
                    n.append('8');
                    break;
                case '9':
                    n.append('6');
            }
        }
    }

    static boolean inRange(long edge, StringBuilder n, boolean fromLow) {
        if (fromLow) {
            if (edge > Long.parseLong(n.toString())) {
                for (int i = 0; i < (n.length() % 2 == 0 ? n.length() / 2 - 1 : n.length() / 2); i++)
                    if (n.charAt(i) != '9') return false;
                if (n.length() % 2 == 1) {
                    if (n.charAt(n.length() / 2) != '8') return false;
                } else {
                    if (n.charAt(n.length() / 2 - 1) != '9') return false;
                }
                System.out.print('1' + "0".repeat(n.length() - 1) + '1');
                return true;
            }
        } else {
            if (edge < Long.parseLong(n.toString())) {
                if (n.charAt(0) != '1') return false;
                for (int i = 1; i <= n.length() / 2 - (n.length() % 2 == 0 ? 1 : 0); i++)
                    if (n.charAt(i) != '0') return false;
                if (n.length() % 2 == 1)
                    System.out.print("9".repeat(n.length() / 2) + "6".repeat(n.length() / 2));
                else System.out.print("9".repeat(n.length() / 2 - 1) + '8' + "6".repeat(n.length() / 2 - 1));
                return true;
            }
        }
        System.out.print(Long.parseLong(n.toString()));
        return true;
    }
}
