package Concurrent_Queue;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeQueue {

    public static class Operation implements Runnable{
        Queue queue;
        private static Lock mutex = new ReentrantLock();
        int item ;
        public Operation(Queue queue, int item){
            this.queue = queue;
            this.item = item;
        }

        // This function answer is getting wrong, as we are using enqueue, this is just incremeing counter,, that is not thread safe..
        @Override
        public void run() {
            mutex.lock();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            queue.enqueue(item);
            mutex.unlock();
        }
    }

    public static void main(String args[]) throws InterruptedException {
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(3000);// it has more fine tuned parameters for thread pool.
        Queue queue = new Queue();
        // answer is getting wrong.
        for(int i=1;i<=100000;i++){
            executorService.submit(new Operation(queue,i));
        }
            // Shut down the executor to allow it to terminate after tasks are completed
        executorService.shutdown();
        boolean completed = executorService.awaitTermination(1, TimeUnit.MINUTES);

        if (completed) {
            System.out.println("All threads have finished their work. Size of Queue: " + queue.size());
        } else {
            System.out.println("Timeout exceeded while waiting for threads to complete.");
        }


        System.out.println("All threads have finished their work. SizeOFQueue: "+ queue.size());

    }
}


