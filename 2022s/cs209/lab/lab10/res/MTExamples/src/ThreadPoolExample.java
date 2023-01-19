import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

//Reference: https://www.baeldung.com/thread-pool-java-and-guava

public class ThreadPoolExample {
    public static void main(String[] args) throws InterruptedException {
        //The Executor interface has a single execute method to submit Runnable instances for execution.
        //The Executors class provides factory methods for the executor services provided in this package.
        //run a single task that simply prints “Hello World“ on the console
        Executor executor = Executors.newSingleThreadExecutor();
        //submit the task as a lambda, which is inferred to be Runnable
        executor.execute(() -> System.out.println("Hello World"));

        //The ExecutorService interface contains a large number of methods to control the progress of
        // the tasks and manage the termination of the service. Using this interface, we can submit
        // the tasks for execution and also control their execution using the returned Future instance
        //Example:submit a task and then use the returned Future‘s get method to wait until the submitted task
        // finishes and the value is returned
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<String> future = executorService.submit(() -> "Hello World");
        // some operations
        String result;
        try {
            //in a real-life scenario, we usually don't want to call future.get() right away
            // but defer calling it until we actually need the value of the computation.
            result = future.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdown();

        //The ThreadPoolExecutor is an extensible thread pool implementation with lots of parameters
        // and hooks for fine-tuning.
        //The main configuration parameters are corePoolSize, maximumPoolSize and keepAliveTime
        //The corePoolSize parameter is the number of core threads that will be instantiated and
        // kept in the pool. When a new task comes in, if all core threads are busy and the internal
        // queue is full, the pool is allowed to grow up to maximumPoolSize.
        //The keepAliveTime parameter is the interval of time for which the excessive threads
        // (instantiated in excess of the corePoolSize) are allowed to exist in the idle state.
        // By default, the ThreadPoolExecutor only considers non-core threads for removal.
        // In order to apply the same removal policy to core threads, we can use the
        // allowCoreThreadTimeOut(true) method.

        //newFixedThreadPool Example
        ThreadPoolExecutor threadPoolExecutor1 =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(2);//corePoolSize=2 and maximumPoolSize=2 keepAliveTime = 0
        threadPoolExecutor1.submit(() -> {
            Thread.sleep(1000);
            return null;
        });
        threadPoolExecutor1.submit(() -> {
            Thread.sleep(1000);
            return null;
        });
        threadPoolExecutor1.submit(() -> {
            Thread.sleep(1000);
            return null;
        });
        System.out.println(threadPoolExecutor1.getPoolSize());
        //output:2
        System.out.println(threadPoolExecutor1.getQueue().size());
        //output:1

        //newFixedThreadPool Example
        ThreadPoolExecutor threadPoolExecutor2 =
                (ThreadPoolExecutor) Executors.newCachedThreadPool();//corePoolSize=0 and maximumPoolSize=Integer.MAX_VALUE keepAliveTime = 60s
        threadPoolExecutor2.submit(() -> {
            Thread.sleep(1000);
            return null;
        });
        threadPoolExecutor2.submit(() -> {
            Thread.sleep(1000);
            return null;
        });
        threadPoolExecutor2.submit(() -> {
            Thread.sleep(1000);
            return null;
        });
        System.out.println(threadPoolExecutor2.getPoolSize());
        //output:3
        System.out.println(threadPoolExecutor2.getQueue().size());
        //output:0

        //newSingleThreadExecutor Example
        AtomicInteger counter = new AtomicInteger();
        //newSingleThreadExecutor is decorated with an immutable wrapper, can't cast it to a ThreadPoolExecutor
        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        executorService1.submit(() -> {
            counter.set(1);
        });
        executorService1.submit(() -> {
            counter.compareAndSet(1, 2);
        });
        //Thread.sleep(100);
        System.out.println(counter.get());
        System.out.println(counter.get());


        //The ScheduledThreadPoolExecutor extends the ThreadPoolExecutor class and also implements
        // the ScheduledExecutorService interface with several additional methods:
        //schedule method: allows us to run a task once after a specified delay.
        //scheduleAtFixedRate method: allows us to run a task after a specified initial delay and
        // then run it repeatedly with a certain period. The period argument is the time measured
        // between the starting times of the tasks, so the execution rate is fixed.
        //scheduleWithFixedDelay method: is similar to scheduleAtFixedRate in that it repeatedly
        // runs the given task, but the specified delay is measured between the end of the previous
        // task and the start of the next. The execution rate may vary depending on the time it
        // takes to run any given task.
        ScheduledExecutorService scheduledExecutorService1 = Executors.newScheduledThreadPool(5);//corePoolSize=5 and maximumPoolSize=Integer.MAX_VALUE keepAliveTime = 0
        scheduledExecutorService1.schedule(() -> {
            System.out.println("schedule:Hello World");
        }, 1000, TimeUnit.MILLISECONDS);
        //delay 1s, output:
        // schedule:Hello World

        //The following code shows how to run a task after 2000 milliseconds delay and then repeat it
        // every 100 milliseconds. After scheduling the task, we wait until it fires five times using
        // the CountDownLatch lock. Then we cancel it using the Future.cancel() method:
        CountDownLatch lock = new CountDownLatch(5);
        ScheduledExecutorService scheduledExecutorService2 = Executors.newScheduledThreadPool(5);
        ScheduledFuture<?> future2 = scheduledExecutorService2.scheduleAtFixedRate(() -> {
            System.out.println("scheduleAtFixedRate:Hello World");
            lock.countDown();
        }, 2000, 100, TimeUnit.MILLISECONDS);
        //delay 2s, output:
        // scheduleAtFixedRate:Hello World

        lock.await(2250, TimeUnit.MILLISECONDS);
        future2.cancel(true);
        System.out.println("scheduleAtFixedRate:End");

        //ForkJoinPool is the central part of the fork/join framework introduced in Java 7.
        // It solves a common problem of spawning multiple tasks in recursive algorithms.
        // We'll run out of threads quickly by using a simple ThreadPoolExecutor, as every task
        // or subtask requires its own thread to run.
        //In a fork/join framework, any task can spawn (fork) a number of subtasks and wait for
        // their completion using the join method. The benefit of the fork/join framework is that
        // it does not create a new thread for each task or subtask

        //ForkJoinPool Example:
        //using ForkJoinPool to traverse a tree of nodes and calculate the sum of all leaf values.
        class TreeNode {

            int value;

            Set<TreeNode> children = new HashSet<>();

            TreeNode(int value, TreeNode... children) {
                this.value = value;
                for (int i = 0; i < children.length; i++) {
                    this.children.add(children[i]);
                }
            }
        }

        class CountingTask extends RecursiveTask<Integer> {

            private final TreeNode node;

            public CountingTask(TreeNode node) {
                this.node = node;
            }

            @Override
            protected Integer compute() {
//                int result = node.value + node.children.stream()
//                        .map(childNode -> new CountingTask(childNode).fork())
//                        .collect(Collectors.summingInt(ForkJoinTask::join));
//                System.out.println(result);
//                return result;
                return node.value + node.children.stream()
                        .map(childNode -> new CountingTask(childNode).fork())
                        .collect(Collectors.summingInt(ForkJoinTask::join));
            }
        }

        TreeNode tree = new TreeNode(5,
                new TreeNode(3), new TreeNode(2,
                new TreeNode(2), new TreeNode(8)));
        /** 5
        *  /\
        * 3  2
        *   /\
        *  2  8
         */
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        int sum = forkJoinPool.invoke(new CountingTask(tree));
        System.out.println("ForkJoinPool:the sum of all leaf values "+sum);
        //output:
        // ForkJoinPool:the sum of all leaf values 20

    }
}
