package OptimisticLocking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

// Repeatedly thread trying to access resources, if they not suceed.
class OptimisticLockCounterRepetedly{
    static private AtomicLong count = new AtomicLong(0);
    static public void inc(String thread) {
        System.out.println(thread + "-> entered inc");
        boolean incSuccessful = false;
        while(!incSuccessful) {
            long value = count.get();
            long newValue = value + 1;
            System.out.println(thread + " ---- old value :" + value + " New value : "+ newValue);
            incSuccessful = count.compareAndSet(value, newValue);
            if(!incSuccessful) System.out.println(thread + " Failed to update -> lets trigger again");
            else System.out.println(thread + " passed --- updated value is -> " + getCount());

        }
    }

    public static long getCount() {
        return count.get();
    }
}


public class Main {

    public static void main(String args[]) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for(int i=1;i<=3;i++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    OptimisticLockCounterRepetedly.inc(Thread.currentThread().getName());
                }
            });
            threads.add(thread);
            thread.start();
        }

        for(Thread thread : threads){
            thread.join();
        }

        System.out.println(OptimisticLockCounterRepetedly.getCount());

    }
}
