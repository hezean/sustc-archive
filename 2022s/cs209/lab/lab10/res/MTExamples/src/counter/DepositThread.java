package counter;


public class DepositThread implements Runnable {
	private Account account;
	private double money;

	public DepositThread(Account account, double money) {
		this.account = account;
		this.money = money;
	}

	@Override
	public void run() {
    //synchronized (account) {
			account.deposit(money);
		}
	//}

}
