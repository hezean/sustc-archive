//import java.io.*;
//import java.util.*;
//
//public class A {
//    static QReader sc = new QReader();
//    static QWriter out = new QWriter();
//
//    public static void main(String[] args) {
//        int n = sc.nextInt();
//        int ess = (int) Math.pow(2, n);
//        Complex[] arg = new Complex[ess];
//        for (int i = 0; i < ess; i++) {
//            arg[i] = new Complex(sc.nextDouble(), 0);
//        }
//        Complex[] res = fft(arg);
//
//        for (Complex re : res) {
//            out.println(re.magnitude());
//        }
//
//        out.close();
//    }
//
//    static Complex[] fft(Complex[] args) {
//        if (args.length == 1) return new Complex[]{args[0]};
//
//        Complex[] sub = new Complex[args.length >> 1];
//        for (int i = 0; i * 2 < args.length; i++) sub[i] = args[i * 2];
//        Complex[] evens = fft(sub);
//        for (int i = 0; i * 2 + 1 < args.length; i++) sub[i] = args[i * 2 + 1];
//        Complex[] odds = fft(sub);
//
//        Complex[] y = new Complex[args.length];
//        int nd2 = args.length >> 1;
//        for (int k = 0; k < nd2; k++) {
//            double tmp = 2 * Math.PI * k / args.length;
//            Complex omega = new Complex(Math.cos(tmp), Math.sin(tmp));
//            y[k] = evens[k].add(omega.mul(odds[k]));
//            y[k + nd2] = evens[k].minus(omega.mul(odds[k]));
//        }
//
//        return y;
//    }
//
//}
//
//class Complex {
//    double r, i;
//
//    public Complex(double r, double i) {
//        this.r = r;
//        this.i = i;
//    }
//
//    public Complex mul(Complex op) {
//        return new Complex(this.r * op.r - this.i * op.i,
//                this.r * op.i + this.i * op.r);
//    }
//
//    public Complex add(Complex op) {
//        return new Complex(this.r + op.r,
//                this.i + op.i);
//    }
//
//    public Complex minus(Complex op) {
//        return new Complex(this.r - op.r,
//                this.i - op.i);
//    }
//
//    public double magnitude() {
//        return Math.sqrt(i * i + r * r);
//    }
//
//    @Override
//    public String toString() {
//        return "Complex{" +
//                "r=" + r +
//                ", i=" + i +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Complex complex = (Complex) o;
//        return Double.compare(complex.r, r) == 0 && Double.compare(complex.i, i) == 0;
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
//    public double nextDouble() {
//        return Double.parseDouble(next());
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
