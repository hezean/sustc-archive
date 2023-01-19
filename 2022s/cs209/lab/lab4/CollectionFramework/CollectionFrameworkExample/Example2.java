import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Example2 {
    public static void doSomeOperations(HashMap<Encoding2,Integer> hm){
        Set<Map.Entry<Encoding2,Integer>> set = hm.entrySet();
        for(Map.Entry<Encoding2,Integer> entry:set){
            if (entry.getKey().getSign()=='1'){
                entry.getKey().setChar('2');
            }
        }
    }

    public static void main(String[] args) {
       // We want to count how many times we find an encoding
/**input:
1 a
2 b
1 a
0
*/
       HashMap<Encoding2,Integer> hm = new HashMap<>();
       Scanner input = new Scanner(System.in);
       String  first;
       String  second;
       boolean keepReading = true;
       Integer count;

       System.out.println("Enter sign and letter it represents:");
       do {
         first = input.next();
         second = input.nextLine().trim();
         if (second.length() > 0) {
           Encoding2 e = new Encoding2(first.charAt(0), second.charAt(0));
           
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

       doSomeOperations(hm);
       System.out.println("After otherTest:");
       System.out.println(hm);
       hm.put(new Encoding2('2','a'),1);
       System.out.println(hm);
       System.out.println(hm.get(new Encoding2('2','a')));
    }
}
