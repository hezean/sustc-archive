package counter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
 //   private Lock accountLock = new ReentrantLock();
    private double balance;

    /**
     *
     * @param money
     */
    public /*synchronized*/ void deposit(double money) {
  //      accountLock.lock();
        try {
			double newBalance = balance + money;
			try {
			    Thread.sleep(10);   // Simulating this service takes some processing time
			}
			catch(InterruptedException ex) {
			    ex.printStackTrace();
			}
			balance = newBalance;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
 //       	accountLock.unlock();
        }
    }


    public double getBalance() {
        return balance;
    }
}