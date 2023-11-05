package ThreadPool;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolMain {
    public static void main(String args[]){
        ThreadPool threadPool = new ThreadPool(3,10);// all 3 threads are waiting for message... as queue task method is blocked.....
        AtomicInteger count= new AtomicInteger();
        for(int i=0;i<20;i++){
            int taskNo = i;
//            System.out.println(taskNo);
            try{
                // if queue size is full it can't accept request.....
                threadPool.execute(() -> {
                    String message = Thread.currentThread().getName()+ " : Task " + taskNo;
                    for(int j=1;j<=1000000;j++){
                        count.getAndIncrement();
                    }
                    System.out.println(message);
                },taskNo);
//                System.out.println("Pushed");
            } catch (Exception e){
                System.out.println("Excepted catchtched in taskMain: "+ e.toString());
            }
        }

        threadPool.waitUntilAllTaskFinished();
        threadPool.stop();
    }
}
