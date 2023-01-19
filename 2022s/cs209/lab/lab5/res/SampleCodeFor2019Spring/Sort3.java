import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Sort3 {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws InvalidKeyException {
		ArrayList<Comparator<Element>> methodList = new ArrayList<>();
		methodList.add((e1, e2) -> e1.value - e2.value);
		methodList.add((e1, e2) -> e2.value - e1.value);

		Scanner in = new Scanner(System.in);
		int total = in.nextInt();

		while (total-- > 0) {

			int method = in.nextInt();
			if (method < 0 || method > 1) {

				throw new InvalidKeyException();
			}

			int n = in.nextInt();
			ArrayList<Element> arrayList = new ArrayList<Element>(n);
			for (int i = 0; i < n; i++) {
				arrayList.add(new Element(i, in.nextInt()));
			}

			Collections.sort(arrayList, methodList.get(method));

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