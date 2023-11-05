package ThreadPool;

import java.util.concurrent.BlockingQueue;

public class PoolThreadRunnable implements  Runnable{
    private Thread thread = null;
    private BlockingQueue taskQueue = null;
    private boolean isStopped = false;

    public PoolThreadRunnable(BlockingQueue queue){
        this.taskQueue = queue;
    }

    @Override
    public void run() {
        this.thread = Thread.currentThread();
        while(!isStopped){
            try{
//                System.out.println("Entering "+ this.thread.getName());
//                System.out.println("I am here: "+ this.thread.getName());
                Runnable runnable = (Runnable) taskQueue.take();// this is blocking...... if queue is empty then it will wait or blocked... thats why we interrupted.......
//                System.out.println("Not going "+ this.thread.getName());
                runnable.run();
            } catch (Exception e){
                System.out.println("Thread is getting unblocked : "+ this.thread.getName());
            }
        }
    }

    public synchronized void doStop(){
        isStopped = true;
        // break pool thread out of dequeue() call.
        this.thread.interrupt();
    }

    public synchronized boolean isStopped(){
        return isStopped;
    }
}
