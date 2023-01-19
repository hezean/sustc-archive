import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {

    private double balance;
    private Lock balanceChangeLock;
    private Condition sufficientFundsCondition;

    public BankAccount(double balance) {
        this.balance = balance;
        balanceChangeLock = new ReentrantLock();
        sufficientFundsCondition = balanceChangeLock.newCondition();
    }

    public BankAccount() {
        this(0);
    }

    public void withdraw(double amount) throws InterruptedException {
        balanceChangeLock.lock();
        try {
            while (balance < amount) {
                sufficientFundsCondition.await();
            }
            balance -= amount;
            System.out.println("Withdrawing " + amount + ", new balance is " + balance);
        } finally {
            balanceChangeLock.unlock();
        }
    }

    public void deposit(double amount) {
        balanceChangeLock.lock();
        try {
            balance += amount;
            System.out.println("Depositing " + amount + ", new balance is " + balance);
            sufficientFundsCondition.signalAll();
        } finally {
            balanceChangeLock.unlock();
        }
    }
}
