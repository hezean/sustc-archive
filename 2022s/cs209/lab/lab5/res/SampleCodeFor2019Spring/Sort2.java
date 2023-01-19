import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;



public class Sort2 {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int total = in.nextInt();

		while (total-- > 0) {
			int n = in.nextInt();
			ArrayList<Element> arrayList = new ArrayList<Element>(n);
			for (int i = 0; i < n; i++) {
				arrayList.add(new Element(i,in.nextInt()));
			}
			
			Collections.sort(arrayList, new Comparator<Element>() {

				@Override
				public int compare(Element o1, Element o2) {
					
					return o1.value - o2.value;
				}
				
			});
			

//			Collections.sort(arrayList, (e1, e2)->e1.value - e2.value);

			for (int i = 0; i < n; i++) {
				System.out.printf("%d", arrayList.get(i).index);
				if (i != n - 1)
					System.out.print(" ");
			}
			System.out.println();
		}
		in.close();
	}
}