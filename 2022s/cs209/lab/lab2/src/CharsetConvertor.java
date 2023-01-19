import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

public class CharsetConvertor {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("args.length != 3");
            return;
        }

        Charset src, dst;
        try {
            src = Charset.forName(args[1]);
            dst = Charset.forName(args[2]);
        } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            System.err.println(e.getLocalizedMessage());
            return;
        }

        String dstName = args[0].replaceFirst("\\.[^.]+$", "")
                .concat("_")
                .concat(dst.name().toLowerCase())
                .concat(args[0].lastIndexOf('.') == -1 ?
                        "" : args[0].substring(args[0].lastIndexOf('.')));

        try (BufferedReader reader = new BufferedReader(new FileReader(args[0], src));
             BufferedWriter writer = new BufferedWriter(new FileWriter(dstName, dst))) {
            String line;
            while((line = reader.readLine()) != null) {
                writer.write(line);
            }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }
}
