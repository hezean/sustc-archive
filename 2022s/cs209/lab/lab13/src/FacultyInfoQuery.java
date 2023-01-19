import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


public class FacultyInfoQuery {

    final static String INVALID = "Invalid command";
    final static String NO_RESULTS = "No results";
    final static String FILE_NAME = FacultyInfoQuery.class.getResource("FacultyList.csv").getPath();

    static List<Faculty> facultyList = new ArrayList<>();

    public static void readFile() {
        File file = new File(FILE_NAME);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            facultyList = reader.lines().map(e -> {
                String[] strs = e.replace("\n", "").split(",");
                return new Faculty(strs[0].trim(), strs[1].trim(), strs[2].trim());
            }).collect(Collectors.toList());

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("IO Exception occurred when reading the file:" + FILE_NAME);
        }

    }

    public static String handleCommand(String s) {
        s = s.replace("\n", "");
        s = s.trim();

        int idx = s.indexOf(' ');
        if (idx == -1) {
            return INVALID;
        }

        String command = s.substring(0, idx);
        String content = s.substring(idx + 1);

        String result = "";
        switch (command.toUpperCase()) {
            case "NAME":
                result = handleNameCommand(content);
                break;
            case "FIRSTLETTER":
                result = handleFirstLetterCommand(content);
                break;
            case "DEP":
                result = handleDepCommand(content);
                break;
            default:
                result = INVALID;
        }
        return result;
    }

    public static String handleNameCommand(String s) {

        String content = facultyList.stream()
                .filter(e -> e.getName().equals(s.trim()))
                .map(Faculty::toString)
                .collect(Collectors.joining("\n"));
        return content.equals("") ? NO_RESULTS : content;

    }

    public static String handleFirstLetterCommand(String s) {

        String content = facultyList.stream()
                .filter(e -> e.getName().toLowerCase().charAt(0) == s.trim().toLowerCase().charAt(0))
                .map((e) -> e.toString())
                .collect(Collectors.joining("\n"));
        return content.equals("") ? NO_RESULTS : content;

    }

    public static String handleDepCommand(String s) {

        String content = facultyList.stream()
                .filter(e -> e.getDep().toLowerCase().contains(s.trim().toLowerCase()))
                .map(Faculty::toString)
                .collect(Collectors.joining("\n"));
        return content.equals("") ? NO_RESULTS : content;

    }


    public static void main(String[] args) {
        readFile();
        while (true) {
            try {
                Scanner scaner = new Scanner(System.in);
                String s = scaner.nextLine();
                String result = handleCommand(s);
                System.out.println(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}