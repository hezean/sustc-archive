/**
 * Whenever we spawn a new thread, that thread attains the new state. When the method start() is invoked
 * on a thread, the thread scheduler moves that thread to the runnable state. Whenever the join() method
 * is invoked on any thread instance, the current thread executing that statement has to wait for this
 * thread to finish its execution, i.e., move that thread to the terminated state. Therefore, before the
 * final print statement is printed on the console, the program invokes the method join() on thread t2,
 * making the thread t1 wait while the thread t2 finishes its execution and thus, the thread t2 get to
 * the terminated or dead state. Thread t1 goes to the waiting state because it is waiting for thread t2
 * to finish it's execution as it has invoked the method join() on thread t2.
 */

//ABC class implements the interface Runnable
class ABC implements Runnable {
    public void run() {

        // try-catch block
        try {
            // moving thread t2 to the state timed waiting
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }


        System.out.println("The state of thread t1 while it invoked the method join() on thread t2 -" + ThreadState.t1.getState());

        // try-catch block
        try {
            Thread.sleep(200);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

// ThreadState class implements the interface Runnable  
public class ThreadState implements Runnable {
    public static Thread t1;
    public static ThreadState obj;

    // main method
    public static void main(String argvs[]) {
        // creating an object of the class ThreadState
        obj = new ThreadState();
        t1 = new Thread(obj);

        // thread t1 is spawned
        // The thread t1 is currently in the NEW state.
        System.out.println("The state of thread t1 after spawning it - " + t1.getState());

        // invoking the start() method on
        // the thread t1
        t1.start();

        // thread t1 is moved to the Runnable state
        System.out.println("The state of thread t1 after invoking the method start() on it - " + t1.getState());
    }

    public void run() {
        ABC myObj = new ABC();
        Thread t2 = new Thread(myObj);

        // thread t2 is created and is currently in the NEW state.
        System.out.println("The state of thread t2 after spawning it - " + t2.getState());
        t2.start();

        // thread t2 is moved to the runnable state
        System.out.println("the state of thread t2 after calling the method start() on it - " + t2.getState());

        // try-catch block for the smooth flow of the  program
        try {
            // moving the thread t1 to the state timed waiting
            Thread.sleep(200);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        System.out.println("The state of thread t2 after invoking the method sleep() on it - " + t2.getState());

        // try-catch block for the smooth flow of the  program
        try {
            // waiting for thread t2 to complete its execution
            t2.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println("The state of thread t2 when it has completed it's execution - " + t2.getState());
    }

}  