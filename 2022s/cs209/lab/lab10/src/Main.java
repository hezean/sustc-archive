public class Main {

    public static void main(String[] args) {
        BankAccount account = new BankAccount();
        final double amount = 100;
        final int repetitions = 10;
        final int threads = 10;

        for (int i = 0; i < threads; i++) {
            DepositRunnable deposit = new DepositRunnable(account, amount, repetitions);
            WithdrawRunnable withdraw = new WithdrawRunnable(account, amount, repetitions);

            Thread dt = new Thread(deposit);
            Thread wt = new Thread(withdraw);

            dt.start();
            wt.start();
        }
    }

}
