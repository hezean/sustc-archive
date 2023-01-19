package counter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

    public static void main(String[] args) {
        Account account = new Account();
        ExecutorService service = Executors.newFixedThreadPool(100);

        for(int i = 1; i <= 100; i++) {
            service.execute(new DepositThread(account, 10));
        }

        service.shutdown();

        while(!service.isTerminated()) {}

        System.out.println("Balance: " + account.getBalance());
    }
}
