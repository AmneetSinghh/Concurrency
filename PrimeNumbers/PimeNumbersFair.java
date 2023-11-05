package PrimeNumbers;

import java.util.concurrent.atomic.AtomicInteger;

public class PimeNumbersFair {

    private static AtomicInteger totalPrimeNumbers = new AtomicInteger(1);
    private static AtomicInteger primeNumber = new AtomicInteger(2);
    static int CONCURRENCY = 2;// 10 threads.
    static Long MAX_LIMIT = 100000000L;

    static void checkPrime(int x, String threadName){
        if(x%2==0){
            return ;
        }
        for(int i=3;i<=Math.sqrt(x);i++){
            if(x%i==0) {
                return;
            }
        }
        totalPrimeNumbers.incrementAndGet();
    }

    static class PrimeCount implements Runnable {
        private String threadName;
        public PrimeCount(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public void run() {
            Long start = System.currentTimeMillis();
            while(true){
                // we are making sure, checkPrime only get specific value single time.
                int currentPrime = primeNumber.getAndIncrement();// shared atomic variable.
                if(currentPrime>MAX_LIMIT){
                    break;
                }
                checkPrime(currentPrime, threadName);// prime number is shared....
            }
            double endTime = (System.currentTimeMillis() - start)/1000.0;
            System.out.printf("%s completed in %.4f\n", threadName,endTime);
        }
    }


    public static void main(String args[]){
        // Create an array to hold the threads
        System.out.println("Execution for prime numbers started");
        Long startTime = System.currentTimeMillis();
        Thread[] threads = new Thread[CONCURRENCY];
        for (int i = 0; i < CONCURRENCY - 1; i++) {
            threads[i] = new Thread(new PrimeCount("Thread: " +i));
            threads[i].start();
        }

        threads[CONCURRENCY - 1] = new Thread(new PrimeCount("Thread: " + (CONCURRENCY - 1)));
        threads[CONCURRENCY - 1].start();

        // Wait for all threads to finish
        for (int i = 0; i < CONCURRENCY; i++) {
            try {
                threads[i].join();  // Wait for thread[i] to complete
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        double endTime = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.println("Checking till -> "+ MAX_LIMIT + " Found -> " + totalPrimeNumbers + " Prime numbers. took  -> " + endTime);

    }
}


/*
2 threads
// Performance Improved.

Execution for prime numbers started
Thread: 8 completed in 10.9590
Thread: 4 completed in 10.9610
Thread: 2 completed in 10.9620
Thread: 5 completed in 10.9610
Thread: 1 completed in 10.9620
Thread: 9 completed in 10.9590
Thread: 6 completed in 10.9610
Thread: 7 completed in 10.9610
Thread: 3 completed in 10.9610
Thread: 0 completed in 10.9620
Checking till -> 100000000 Found -> 5761455 Prime numbers. took  -> 11.021


1. every thread stops at same time... and starts at same time... a big improvement for making fair program..

*/


/* PC

 Model Name:	MacBook Pro
  Model Identifier:	MacBookPro18,1
  Model Number:	MK183HN/A
  Chip:	Apple M1 Pro
  Total Number of Cores:	10 (8 performance and 2 efficiency)
  Memory:	16 GB
 */