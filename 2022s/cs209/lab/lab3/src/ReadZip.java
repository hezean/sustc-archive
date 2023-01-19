import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ReadZip {
    public static void main(String[] args) {
//        try {
//            ZipContentFilter zcf = new ZipContentFilter("res/src.zip");
//            zcf.printFiltered(".java", "java.io", "java.nio");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            ZipContentFilter zcf = new ZipContentFilter("res/rt.jar");
            zcf.printFiltered(".class", "java.io", "java.nio");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ZipContentFilter extends ZipFile {
    PrintStream outStream;

    public ZipContentFilter(String name, PrintStream out) throws IOException {
        super(name);
        this.outStream = out;
    }

    public ZipContentFilter(String name) throws IOException {
        this(name, System.out);
    }

    public void printFiltered(String suffix, String... prefix) {
        Supplier<Stream<? extends ZipEntry>> ssp = () -> this.stream()
                .filter((f) -> {
                    if (!f.getName().endsWith(suffix)) return false;
                    for (String p : prefix)
                        if (f.getName().startsWith(p.replaceAll("\\.", "/"))) return true;
                    return false;
                });
        outStream.printf("%s files in %s (total %d):\n", suffix, this.getName(), ssp.get().count());
        ssp.get().forEach(outStream::println);
    }
}
