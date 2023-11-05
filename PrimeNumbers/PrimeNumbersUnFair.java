package PrimeNumbers;

        import java.util.concurrent.atomic.AtomicInteger;

public class PrimeNumbersUnFair {

    private static AtomicInteger totalPrimeNumbers = new AtomicInteger(1);// internally it wil use compare and swap in os...
    // it is the cpu atomic instruction, just java has a wrapper above it.
    static int CONCURRENCY = 10;// 10 threads.
    static Long MAX_LIMIT = 100000000L;

    static void checkPrime(int x){
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

    static class PrimeBatch implements Runnable {
        private String threadName;
        private int nstart;
        private int nend;

        public PrimeBatch(String threadName, int nstart, int nend) {
            this.threadName = threadName;
            this.nstart = nstart;
            this.nend = nend;
        }

        @Override
        public void run() {
            Long start = System.currentTimeMillis();
            for (int i = nstart; i < nend; i++) {
                checkPrime(i);
            }
            double endTime = (System.currentTimeMillis() - start)/1000.0;
            System.out.printf("Thread %s [%d %d] completed in %.2f\n", threadName, nstart, nend, endTime);
        }
    }


    public static void main(String args[]){
        // Create an array to hold the threads
        System.out.println("Execution for prime numbers started");
        int nstart = 3;
        Long startTime = System.currentTimeMillis();
        int batchSize = (int) (MAX_LIMIT/ CONCURRENCY);
        Thread[] threads = new Thread[CONCURRENCY];
        for (int i = 0; i < CONCURRENCY - 1; i++) {
            threads[i] = new Thread(new PrimeBatch("Thread: " + i, nstart, (int) (nstart + batchSize)));
            threads[i].start();
            nstart += batchSize;
        }

        threads[CONCURRENCY - 1] = new Thread(new PrimeBatch("Thread: " + (CONCURRENCY - 1), nstart, Math.toIntExact(MAX_LIMIT)));
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

Execution for prime numbers started
Thread Thread: 0 [3 10000003] completed in 4.23
Thread Thread: 1 [10000003 20000003] completed in 6.52
Thread Thread: 2 [20000003 30000003] completed in 7.91
Thread Thread: 3 [30000003 40000003] completed in 9.00
Thread Thread: 4 [40000003 50000003] completed in 9.76
Thread Thread: 5 [50000003 60000003] completed in 10.67
Thread Thread: 6 [60000003 70000003] completed in 11.27
Thread Thread: 7 [70000003 80000003] completed in 11.94
Thread Thread: 8 [80000003 90000003] completed in 12.49
Thread Thread: 9 [90000003 100000000] completed in 13.06
Checking till -> 100000000 Found -> 5761455 Prime numbers. took  -> 13.117




1 So basically the program is some threads getting start_batch and end_batch of large numbers in which finding prime numbes are very time taking,
so this approach is not fair.
2. Thread0,thread_1 ended, and we can't utilize that now..
 */


/* PC

 Model Name:	MacBook Pro
  Model Identifier:	MacBookPro18,1
  Model Number:	MK183HN/A
  Chip:	Apple M1 Pro
  Total Number of Cores:	10 (8 performance and 2 efficiency)
  Memory:	16 GB
 */