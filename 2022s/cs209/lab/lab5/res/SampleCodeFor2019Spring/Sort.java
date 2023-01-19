import java.util.Scanner;

public class Sort {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int total = in.nextInt();

		while (total-- > 0) {
			int n = in.nextInt();
			int array[] = new int[n];
			int index[] = new int[n];
			for (int i = 0; i < n; i++) {
				array[i] = in.nextInt();
				index[i] = i;
			}
			//Bubble sort the index according the value
			int temp;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n - i - 1; j++) {

					if (array[index[j]] > array[index[j+1]]){
						temp = index[j + 1];
						index[j + 1] = index[j];
						index[j] = temp;
						
					}
				}
			}

			for (int i = 0; i < n; i++) {
				System.out.printf("%d", index[i]);
				if (i != n - 1)
					System.out.print(" ");
			}
			System.out.println();
		}
		in.close();
	}
}
