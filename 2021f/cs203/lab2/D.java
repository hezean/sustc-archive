import java.util.Scanner;

class D {
    static int cnt = 0, n;
    static int[] arr;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        n = sc.nextInt();
        arr = new int[n];
        for (int i = 0; i < n; i++)
            arr[i] = sc.nextInt();
        int max_p = (int) (Math.log(arr[arr.length - 1]) / Math.log(2)) + 1;
        for (int i = 0; i < n - 1; i++)
            for (int j = 1 + (int) (Math.log(arr[i]) / Math.log(2)); j <= max_p; j++)
                bs(i + 1, n - 1, (int) Math.pow(2, j) - arr[i], i);
        System.out.print(cnt);
    }

    static void bs(int lo, int hi, int tar, int st) {
        if (lo > hi)
            return;
        int mid = lo + (hi - lo) / 2;
        if (arr[mid] < tar)
            bs(mid + 1, hi, tar, st);
        else if (arr[mid] > tar)
            bs(lo, mid - 1, tar, st);
        else {
            cnt++;
            int bias = mid - 1;
            while (bias > st && arr[bias--] == tar)
                cnt++;
            bias = mid + 1;
            while (bias < n && arr[bias++] == tar)
                cnt++;
        }
    }
}