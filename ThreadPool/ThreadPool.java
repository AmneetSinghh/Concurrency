package ThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ThreadPool {
    private BlockingQueue<Runnable> taskQueue = null;// Will learn this in 5th video,,,, of arpit concurency series.
    private List<PoolThreadRunnable> runnables = new ArrayList<>();
    private boolean isStopped = false;

    public ThreadPool(int noOfThreads, int maxTasks){
        taskQueue = new ArrayBlockingQueue<Runnable>(maxTasks);
        for(int i=1;i<=noOfThreads;i++){
            PoolThreadRunnable poolThreadRunnable = new PoolThreadRunnable(taskQueue);
            runnables.add(poolThreadRunnable);
        }
        for(PoolThreadRunnable runnable : runnables){
            new Thread(runnable).start();// all threads are started here.
        }
    }

    public void execute(Runnable task,int i) throws Exception{
        if(this.isStopped) throw new IllegalStateException("ThreadPool is stopped");
        boolean canOffer = this.taskQueue.offer(task);
        if(!canOffer){
            System.out.println("Queue is full : "+ i);
        }
//        System.out.println("Size of queue : " + taskQueue.size());
    }

    public synchronized void stop(){
        this.isStopped = true;
        for(PoolThreadRunnable runnable : runnables){
            runnable.doStop();
        }
    }

    public void waitUntilAllTaskFinished(){
        while(this.taskQueue.size()>0){
            try{
                Thread.sleep(1);// this is the main thread.
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}

/*
1. We not need synchronized keyword because above two only called by main thread, acco.. to my current understanding....

 */