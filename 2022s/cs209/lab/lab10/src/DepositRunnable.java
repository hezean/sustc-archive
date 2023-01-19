public class DepositRunnable implements Runnable {

    private final int DELAY = 1;
    private final BankAccount account;
    private final double amount;
    private final int count;

    public DepositRunnable(BankAccount account, double amount, int count) {
        this.account = account;
        this.amount = amount;
        this.count = count;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < count; i++) {
                account.deposit(amount);
                Thread.sleep(DELAY);
            }
        } catch (InterruptedException ignored) {
        }
    }

}
