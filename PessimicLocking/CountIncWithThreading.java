package PessimicLocking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountIncWithThreading {

    static int count = 0;
    private static Lock mutex = new ReentrantLock();

    static class CountRunnable implements Runnable {

        @Override
        public void run() {

            mutex.lock();
            try {
                // please not write heavy processing logic here.
                Thread.sleep(1000); // Time is multiplying...
                count++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            mutex.unlock();
        }
    }

    public static void main(String args[]) {
        Long start = System.currentTimeMillis();
        List<Thread> threads = new ArrayList<>();
        System.out.println("Threads started");
        for (int i = 1; i <= 10; i++) {// 5*4 = 20....
            Thread thread = new Thread(new CountRunnable());
            threads.add(thread);
            thread.start();
        }
        System.out.println("All threads  done started");
        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // Handle the exception
            }
        }
        double endTime = (System.currentTimeMillis() - start) / 1000.0;
        System.out.println("Final answer:-> " + count + " EndTime : " + endTime);

    }
}

/* Without locking
Threads started
All threads  done started
Final answer:-> 970 EndTime : 4.096
 */

/* With locking - Performance effect
Threads started
All threads  done started
Final answer:-> 970 EndTime :4.3
 */



/*

Taking > 30 seconds, we are sleeping a thread under section may be?

public void run() {
            mutex.lock();
            try {
                Thread.sleep(1000);
                count++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mutex.unlock();
        }


        NumberOfThreads * Processing_time_of_critical_block
 */