package NestedLocking;

import java.util.ArrayList;
import java.util.List;


// WRONG ANSWER.
public class NestedLockingWrong {
    static int count=0;

    static public class CounterRunnable implements  Runnable{

        Boolean check;
        public CounterRunnable(Boolean check){
            this.check = check;
        }
        // 1 thread will enter incrementCounter 1 time

        // T1
        synchronized public void incrementCounter() throws InterruptedException {
            Thread.sleep(1);
            // 300 times.
            count++;// shared...
        }

        // 1 thread will enter decrementCounter Counter 1 time

        // T2
        synchronized public void decrementCounter() throws InterruptedException {
            // 300 times.
            Thread.sleep(2);
            count--;
        }

        // T1 ^ T2 ->  doing operations simumtaluesly on count,,->   that is not thread safe..
        @Override
        public void run() {
            if(check) {
                try {
                    incrementCounter();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    decrementCounter();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static void main(String args[]) throws InterruptedException {
        List<Thread> inThreads = new ArrayList<>();
        List<Thread> deThreads = new ArrayList<>();
        int inSize =4000, ouSize =4000;
        for(int i=1;i<=inSize;i++){
            Thread thread = (new Thread(new CounterRunnable(true)));
            inThreads.add(thread);
            thread.start();
        }

        for(int i=1;i<=ouSize;i++){
            Thread thread = (new Thread(new CounterRunnable(false)));
            deThreads.add(thread);
            thread.start();
        }


        for(int i=1;i<=inSize;i++){
            inThreads.get(i-1).join();
        }
        for(int i=1;i<=ouSize;i++){
            deThreads.get(i-1).join();
        }



        System.out.println("Value of count :->"+ count);
    }
}




