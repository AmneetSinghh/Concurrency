package SingleThreading.NodeJs;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

enum QueueType{
    TIMER,
    POLL,
    CLOSE,
    IMMEDIATE_TIMER,
    NEXT_TICK,
    MICRO_TASK
}
class CallBackData{
    public CallBackData(String queueName, Long timer, String callBackFunc){
        this.queueName = queueName;
        this.timer = timer;
        this.callBackFunc = callBackFunc;
    }
    String queueName;
    Long timer;
    String callBackFunc;
}
public class EventLoop {
    static int task=0;
    static Queue<String> timerCallBack;
    static Queue<String>  IOCallBack;
    static Queue<String>  immediateTimerCallBack;
    static Queue<String>  closeCallBack;
    static Queue<String>  microTask;
    static Queue<String>  nextTick;

    Stack<String> callStack;
    public EventLoop(Stack<String> callStack){
        timerCallBack = new LinkedList<>();
        IOCallBack =  new LinkedList<>();
        immediateTimerCallBack =  new LinkedList<>();
        closeCallBack = new LinkedList<>();
        microTask = new  LinkedList<>();
        nextTick =  new LinkedList<>();
        this.callStack = callStack;
        loop();
    }

    private Boolean ifTaskComplete(){
        return (task == 0);
    }

    private void checkBetween(){
        consumeQueueForDuration(microTask, QueueType.MICRO_TASK.toString());
        consumeQueueForDuration(nextTick,  QueueType.NEXT_TICK.toString());
    }
    private void loop(){
        while(true){
            consumeQueueForDuration(timerCallBack, QueueType.TIMER.toString());
            checkBetween();
            if(ifTaskComplete()){
                System.out.println("EventLoop exists");
                return;
            }
            consumeQueueForDuration(IOCallBack, QueueType.POLL.toString());
            checkBetween();
            if(ifTaskComplete()){
                System.out.println("EventLoop exists");
                return;
            }
            consumeQueueForDuration(immediateTimerCallBack, QueueType.IMMEDIATE_TIMER.toString());
            checkBetween();
            if(ifTaskComplete()){
                System.out.println("EventLoop exists");
                return;
            }
            consumeQueueForDuration(closeCallBack, QueueType.CLOSE.toString());
            checkBetween();
            if(ifTaskComplete()){
                System.out.println("EventLoop exists");
                return;
            }
        }
    }

    /*
     * Each Phrase has 200ms time to execute.
     */
    private void consumeQueueForDuration(Queue<String> queue, String QueueName) {
        long endTime = System.currentTimeMillis() + 200;// 200 ms each job runs...
        System.out.println("--------- Queue-> ---------- "+ QueueName);
        while (System.currentTimeMillis() < endTime) {
            if (!queue.isEmpty()) {
                String element = queue.poll();
                --task;
                System.out.println("Data-> "+ element + " pushed to callStack for execution");
                callStack.push(element);
            } else {
                // Queue is empty, wait for a short duration before checking again
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

class ThreadPoolRunnable implements Runnable{

    CallBackData callBackData;
    public ThreadPoolRunnable(CallBackData callBackData){
        this.callBackData = callBackData;
    }
    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " Executing-> QueueName: "+ callBackData.queueName+ " Timer: "+callBackData.timer);
            Thread.sleep(callBackData.timer);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendToQueue();
        System.out.println("-------- Data sent to QueueName: --------- "+ callBackData.queueName);

    }
    /*
     * Only these 2 are high processing tasks done by os using kqueue. kqueue send us notification whenever task done or not.
     * then c++ pushes to event-loop ...
     */
    private String sendToQueue(){
        switch (callBackData.queueName){
            case "TIMER": {
                EventLoop.timerCallBack.add(callBackData.callBackFunc);
                return "TIMER";
            }
            case "POLL": {
                EventLoop.IOCallBack.add(callBackData.callBackFunc);
                return "POLL";
            }
        }
        return null;
    }

}

class EventLoopRunnable implements  Runnable{
    Stack<String> callStack;
    public EventLoopRunnable(Stack<String> callStack){
        this.callStack = callStack;
    }

    @Override
    public void run() {
        EventLoop.task = 1;
        System.out.println(Thread.currentThread().getName() + " Event Loop started");
        EventLoop loop = new EventLoop(callStack);
        System.out.println(Thread.currentThread().getName() + " Event Loop Ended");
        // It is doing heavy processing.
    }
}

class CallStackRunnable implements  Runnable{
    Stack<String> callStack;
    public CallStackRunnable(Stack<String> callStack){
        this.callStack = callStack;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Started Getting from CallStack");
        while(true){
            if(!callStack.empty()){
                String value = callStack.pop();
                System.out.println(" Executed  ******************************************* "+ value);
            }
        }
    }
}

class Main{
    static ThreadPoolExecutor executorService;// Thread Pool of LibUV library.

    public static void main(String[] args){
        System.out.println(Thread.currentThread().getName() + " Node.js Application started");
        Stack<String> callStack = new Stack<>();
        /*
         * Represents callStack in Node.js, ( NODE_JS MAIN THREAD )
         */
        Thread callStackThread = new Thread(new CallStackRunnable(callStack));
        callStackThread.start();
        /*
         * Represents EventLoop in Node.js
         */
        Thread eventLoopThread = new Thread(new EventLoopRunnable(callStack));
        eventLoopThread.start();
        /*
         * Represents ThreadPool in Node.js libUV
         */
        executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        // start tasks..

        System.out.println("---------------- Start pushing data now  --- ----------");
        for(int i=0;i<=0;i++){
            executorService.submit(new ThreadPoolRunnable(new CallBackData("TIMER",2000L, "Data : 1" )));
            executorService.submit(new ThreadPoolRunnable(new CallBackData("POLL",10000L, "Data : 2" )));
            EventLoop.immediateTimerCallBack.add("Data : 3");
            EventLoop.closeCallBack.add("Data : 4");
            EventLoop.microTask.add("Data : 5");
            EventLoop.nextTick.add("Data : 6");
        }


        EventLoop.task--;// Task that we added .. faltu.....

    }
}



