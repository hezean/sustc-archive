import java.util.Arrays;
import java.util.Scanner;
import java.util.function.IntPredicate;

public class Lambda {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int sz = sc.nextInt();
        int[] arr = new int[sz];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = sc.nextInt();
        }

        printFilteredArr(arr, i -> i % 2 == 0);  // evens
        printFilteredArr(arr, i -> i % 2 != 0);  // odds
        printFilteredArr(arr, Lambda::isPrime);  // primes
        printFilteredArr(arr, i -> i > 5 && isPrime(i));  // biased primes

        int szQ2 = sc.nextInt();
        int[] arrQ2 = new int[szQ2];
        for (int i = 0; i < arrQ2.length; i++) {
            arrQ2[i] = sc.nextInt();
        }

        System.out.println(quadraticSum(arrQ2));
    }

    public static void printFilteredArr(int[] arr, IntPredicate f) {
        Arrays.stream(arr)
                .filter(f)
                .forEach(i -> System.out.printf("%d ", i));
        System.out.println();
    }

    private static boolean isPrime(int n) {
        if (n <= 1) return false;
        int tmp = (int) Math.ceil(Math.sqrt(n));
        for (int i = 2; i < tmp; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public static long quadraticSum(int[] arr) {
        return Arrays.stream(arr)
                .map(i -> (int) Math.pow(i, 2))
                .sum();
    }
}
