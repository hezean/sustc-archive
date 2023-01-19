import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextProcessor {
    private final Path path;
    private static final Pattern wordPatten = Pattern.compile("\\b[\\w-']+\\b");
    private static final String wrongSentencePattern = "^[^\\w]*[a-z].*";
    private static final Pattern sentenceStartFinder = Pattern.compile("[a-z]");
    private static final String lineEndingSentencePattern = ".*[.!?][^\\w]*$";

    public TextProcessor(String path) {
        this.path = Paths.get(path);
        if (!this.path.toFile().isFile() || !this.path.toFile().canRead()) {
            throw new IllegalArgumentException("not a path to readable file");
        }
    }

    public TreeMap<String, Integer> getCommonWords(int n, String[] stopwords) {
        try {
            Map<String, Integer> rawFreq = Files.lines(path)
                    .parallel()
                    .map(rawLine -> {
                        List<String> res = new ArrayList<>();
                        Matcher m = wordPatten.matcher(rawLine);
                        while (m.find()) {
                            res.add(m.group());
                        }
                        return res;
                    })
                    .flatMap(res -> res.stream().parallel())
                    .collect(Collectors.toConcurrentMap(String::toLowerCase, w -> 1, Integer::sum));
            List<String> sw = stopwords == null ? List.of() : List.of(stopwords);
            sw.forEach(rawFreq.keySet()::remove);

            Map<String, Integer> tops = rawFreq.entrySet().stream()
                    .sorted((e1, e2) -> {
                        int compVal = e1.getValue().compareTo(e2.getValue());
                        if (compVal == 0) {
                            return e1.getKey().compareTo(e2.getKey());
                        }
                        return -compVal;
                    })
                    .limit(n)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return new TreeMap<>(tops);
        } catch (IOException e) {
            return new TreeMap<>();
        }
    }

    public ArrayList<Position> highlightWord(Position pos) {
        try {
            List<String> lines = Files.readAllLines(path);
            String targetLine = lines.get(pos.getLine() - 1);

            List<Integer> wordsStart = new ArrayList<>();
            List<Integer> wordsEnd = new ArrayList<>();
            Matcher m = wordPatten.matcher(targetLine);
            m.results().forEach(r -> {
                wordsStart.add(r.start());
                wordsEnd.add(r.end() - 1);
            });

            int matchStartIdx = Collections.binarySearch(wordsStart, pos.getCol() - 1);
            int matchEndIdx = Collections.binarySearch(wordsEnd, pos.getCol() - 1);

            if (matchStartIdx < 0 && matchStartIdx == matchEndIdx) {
                return new ArrayList<>();
            }

            String targetWord;
            if (matchStartIdx >= 0 || matchEndIdx >= 0) {
                int intervalIdx = Math.max(matchStartIdx, matchEndIdx);
                targetWord = targetLine.substring(wordsStart.get(intervalIdx),
                                wordsEnd.get(intervalIdx) + 1)
                        .toLowerCase();
            } else {
                targetWord = targetLine.substring(wordsStart.get(-matchStartIdx - 2),
                                wordsEnd.get(-matchEndIdx - 1) + 1)
                        .toLowerCase();
            }

            Pattern targetWp = Pattern.compile(String.format("\\b(?<![-'])%s(?![-'])\\b", targetWord));

            ArrayList<Position> res = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                Matcher wm = targetWp.matcher(lines.get(i).toLowerCase());
                int ln = i + 1;
                wm.results().forEach(r -> res.add(new Position(ln, r.start() + 1)));
            }
            return res;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public List<Position> fixLowercaseFirstLetter() {
        try {
            List<Position> res = new LinkedList<>();
            List<String> lines = Files.readAllLines(path);
            boolean lastLineEnds = true;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);

                boolean biasAddOne = lastLineEnds;
                if (lastLineEnds) {
                    line = "." + line;
                }
                lastLineEnds = line.matches(lineEndingSentencePattern);

                String[] sentences = line.split("[.!?]");  // don't care sentences[0]
                for (int j = 1; j < sentences.length; j++) {
                    if (!sentences[j].matches(wrongSentencePattern)) {
                        continue;
                    }
                    int col = 1;
                    for (int k = 1; k < j; k++) {
                        col += sentences[k].length() + 1;
                    }
                    Matcher m = sentenceStartFinder.matcher(sentences[j]);
                    m.find();
                    col += m.start();
                    res.add(new Position(i + 1, col));
                }
            }
            return res;
        } catch (IOException e) {
            return List.of();
        }
    }

    public String encrypt() {
        try {
            StringBuilder res = new StringBuilder();

            Files.lines(path)
                    .map(rawLine -> {
                        StringBuilder encLine = new StringBuilder();
                        Matcher m = wordPatten.matcher(rawLine);
                        List<Integer> wordStart = new ArrayList<>();
                        List<Integer> wordEnd = new ArrayList<>();
                        while (m.find()) {
                            wordStart.add(m.start());
                            wordEnd.add(m.end());
                        }
                        if(wordStart.isEmpty()) {
                            return encLine.toString();
                        }
                        if (wordStart.get(0) > 0) {
                            encLine.append(rawLine, 0, wordStart.get(0));
                        }
                        for (int i = 0; i < wordStart.size(); i++) {
                            encLine.append(encryptWord(rawLine
                                    .substring(wordStart.get(i), wordEnd.get(i))));
                            if (i < wordStart.size() - 1) {
                                encLine.append(rawLine, wordEnd.get(i), wordStart.get(i + 1));
                            }
                        }
                        encLine.append(rawLine.substring(wordEnd.get(wordEnd.size() - 1)));
                        return encLine.toString();
                    })
                    .forEach(e -> {
                        res.append(e);
                        res.append('\n');
                    });
            return res.deleteCharAt(res.length() - 1).toString();
        } catch (IOException e) {
            return "";
        }
    }

    private static String encryptWord(String origin) {
        StringBuilder sb = new StringBuilder(origin);
        sb.reverse();
        char c = Character.toLowerCase(sb.charAt(0));
        if (c == 'a' || c == 'e' || c == 's') {
            sb.insert(1, (int) sb.charAt(0));
        }
        return sb.append(origin.length()).toString();
    }

    public HashSet<List<String>> ngramSim(int ngram, String pathForAnotherFile) {
        try {
            String self = Files.readString(path).toLowerCase();
            String oppo = Files.readString(Path.of(pathForAnotherFile)).toLowerCase();

            Matcher selfWordsMatch = wordPatten.matcher(self);
            List<String> selfWords = new ArrayList<>();
            selfWordsMatch.results().forEach(w -> selfWords.add(w.group()));

            HashSet<List<String>> selfGrams = new HashSet<>();
            for (int i = 0; i <= selfWords.size() - ngram; i++) {
                List<String> tmp = new ArrayList<>();
                for (int j = 0; j < ngram; j++) {
                    tmp.add(selfWords.get(i + j));
                }
                selfGrams.add(tmp);
            }

            Matcher oppoWordsMatch = wordPatten.matcher(oppo);
            List<String> oppoWords = new ArrayList<>();
            oppoWordsMatch.results().forEach(w -> oppoWords.add(w.group()));

            HashSet<List<String>> res = new HashSet<>();
            for (int i = 0; i <= oppoWords.size() - ngram; i++) {
                List<String> tmp = new ArrayList<>();
                for (int j = 0; j < ngram; j++) {
                    tmp.add(oppoWords.get(i + j));
                }
                if (selfGrams.contains(tmp)) {
                    res.add(tmp);
                }
            }
            return res;
        } catch (IOException e) {
            return new HashSet<>();
        }
    }
}
