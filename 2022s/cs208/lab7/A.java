//import java.io.*;
//import java.util.*;
//
//public class A {
//    static QReader sc = new QReader();
//    static QWriter out = new QWriter();
//
//    public static void main(String[] args) {
//        long n = sc.nextLong();
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 59; i >= 0; i--) {
//            long tmp = n - (long) Math.pow(2, i);
//            if (tmp < 0) {
//                sb.append(0);
//                continue;
//            }
//            tmp >>= (i + 1);
//            sb.append(1 - (tmp % 2));
//        }
//        String gray = sb.toString();
//        int idx = 0;
//        while (idx < 60) {
//            if (gray.charAt(idx) != '0') break;
//            idx++;
//        }
//        out.print(gray.substring(idx));
//        out.close();
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
