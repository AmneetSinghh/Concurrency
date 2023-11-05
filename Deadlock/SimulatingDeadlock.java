package Deadlock;

/*
Simulating when 6 transactions/connections/threads want to acquire 2 random locks on 3 types of records..
// mimic database will work with row level locks.
*/

import ThreadPool.PoolThreadRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/*
Deadlock if occurs :
1.Kill the process if deadlock occurs... or blocked....

Deadlock prevention :
1. how databases does is: before aquiring lock check if circular dependency forms...   they maintain resource allocation graph,,,
2. Most real world system prefers deadlock prevention
3. Deadlock is prevented, by killing lock, but if we kill txn not complted, what will happen?


Deadlock Avoidance   -> writing code that avoids circular dependecy checking and resource allocation graph maintainance..:

1. You need to maintain total orders or sorted order, in which we need to lock,, if this is possible, this is the best possible way.
 */
class RecordData{
    public RecordData(int id, String name, String data){
        this.id = id; this.name = name; this.data = data;
    }
    int id;
    String name;
    String data;
}

// Row/Record Level Lock
class Record{
    public Record(RecordData recordData, Lock lock){
        this.recordData = recordData;
        this.lock = lock;
    }
    RecordData recordData;
    Lock lock;
}

// always have single db connection Thread-safe-db-connection
class Db {
    public List<Record> records;

    private Db() {
        // Initialization code
        records = new ArrayList<>();
        for (int i = 1; i <= Config.NUM_RECORDS; i++) {
            records.add(new Record(
                    new RecordData(i, i + "-> Name", null),
                    new ReentrantLock()
            ));
        }
        System.out.println(records.size());
    }

    // lazy initilization......... using static inner class.
    private static class DbHolder {
        private static final Db INSTANCE = new Db();// cannot be reassigned.
    }

    public static Db getInstance() {
        return DbHolder.INSTANCE;
    }
}


class DoDBRunnable implements  Runnable {

    private void acquireLock(int rec) {
        System.out.println("Txn: "+ Thread.currentThread().getName()+" wants to acquire lock on record: "+ rec);
        Db.getInstance().records.get(rec-1).lock.lock();
        System.out.println("Txn: "+ Thread.currentThread().getName()+" acquired lock on record:  "+ rec);
    }

    private void releaseLock(int rec) {
        Db.getInstance().records.get(rec-1).lock.unlock();
        System.out.println("Txn: "+ Thread.currentThread().getName()+"Released lock on record:  "+ rec);
    }

    @Override
    public void run() {
        while (true) {
            int rec1 = Utils.getRandom();
            int rec2 = Utils.getRandom();
            if (rec1 == rec2) {
                continue;
            }
            // just this below condition avoided deadlock....
            if(rec1>rec2){
                int temp = rec1;
                rec1 = rec2;
                rec2 = temp;
            }
            System.out.println(rec1 +" "+rec2);
            acquireLock(rec1);
            acquireLock(rec2);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            releaseLock(rec1);
            releaseLock(rec2);
        }
    }

}


public class SimulatingDeadlock {

    public static void main(String args[]){
        List<Thread> threads = new ArrayList<>();
        for(int i=1;i<=Config.NUM_CONN;i++){
            Thread thread = new Thread(new DoDBRunnable());
            threads.add(thread);
            thread.start();
        }

        for(Thread thread : threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}



class Utils {
    public static int getRandom() {
        Random random = new Random();
        int randomNumber = random.nextInt(Config.NUM_RECORDS-1);
        return randomNumber + 1;
    }
}
class Config{
    static int NUM_CONN = 6;// same as number of threads.
    static int NUM_RECORDS = 3;
}