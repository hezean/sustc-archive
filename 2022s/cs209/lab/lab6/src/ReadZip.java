import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ReadZip {
    public static void main(String[] args) {
        try {
            ZipContentFilter zip = new ZipContentFilter("res/src.zip");
            ZipContentFilter jar = new ZipContentFilter("res/rt.jar");

            var src = zip.collectFiltered(".java", "java.io", "java.nio");
            var cls = jar.collectFiltered(".class", "java.io", "java.nio");

            var srcNames = src.stream().map(f -> f.split("\\.java")[0]).collect(Collectors.toSet());
            var clsNames = cls.stream().map(f -> f.split("(\\$|\\.class)")[0]).collect(Collectors.toSet());

            diff(".java files in src.zip that don't have corresponding .class files:", src, srcNames, clsNames);
            diff(".class files in rt.jar that don't have corresponding .java files:", cls, clsNames, srcNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void diff(String prompt, Set<String> comp,
                     Set<String> compHelp, Set<String> withHelp) {
        Set<String> diff = new HashSet<>(compHelp);
        diff.removeAll(withHelp);

        Supplier<Stream<String>> s = () ->
                comp.stream().filter(f -> {
                    for (String name : diff) {
                        if (f.startsWith(name)) return true;
                    }
                    return false;
                });

        System.out.println(prompt + " (" + s.get().count() + " items)");
        s.get().forEach(System.out::println);
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

    public Set<String> collectFiltered(String suffix, String... prefix) {
        return this.stream()
                .filter((f) -> {
                    if (!f.getName().endsWith(suffix)) return false;
                    for (String p : prefix)
                        if (f.getName().startsWith(p.replaceAll("\\.", "/"))) return true;
                    return false;
                })
                .map(ZipEntry::getName)
                .collect(Collectors.toSet());
    }
}
