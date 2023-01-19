import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Example3 {

    public static void main(String[] args) {
        // We want to count how many times we find an encoding
/**input:
 1 a
 2 b
 1 a
 0
 */
        HashMap<Encoding3, Integer> hm = new HashMap<>();
        Scanner input = new Scanner(System.in);
        String first;
        String second;
        boolean keepReading = true;
        Integer count;

        System.out.println("Enter sign and letter it represents:");
        do {
            first = input.next();
            second = input.nextLine().trim();
            if (second.length() > 0) {
                Encoding3 e = new Encoding3(first.charAt(0), second.charAt(0));
                if ((count = hm.get(e)) == null) {
                    hm.put(e, 1);
                } else {
                    hm.put(e, 1 + count);
                }
            } else {
                keepReading = false;
            }
        } while (keepReading);
        System.out.println(hm);

    }
}
