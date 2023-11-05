package Java;


class MyRunnable implements Runnable {

    int thread;
    public MyRunnable(int i){
        this.thread = i;
    }
    public void run() {
        methodOne();
    }

    public void methodOne() {
        int localVariable1 = 45;

        MySharedObject localVariable2 =
                MySharedObject.sharedInstance;

        //... do more with local variables.
        if(thread == 1){
            localVariable2.member1 = 12;
        }


        System.out.println("Thread-> "+ thread +" value-> "+ localVariable2.member1);

        methodTwo();
    }

    public void methodTwo() {
        Integer localVariable1 = new Integer(99);

        //... do more with local variable.
    }
}


class MySharedObject {

    //static variable pointing to instance of MySharedObject

    public static final MySharedObject sharedInstance = new MySharedObject();// will only initilize one.


    //member variables pointing to two objects on the heap

    public Integer object2 = new Integer(22);
    public Integer object4 = new Integer(44);

    public long member1 = 12345;
    public long member2 = 67890;
}

public class JavaMemory {
    public static void main(String args[]) throws InterruptedException {

        Runnable runnable1 = new MyRunnable(1);
        Runnable runnable2 = new MyRunnable(2);
        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);

        thread1.start();
        thread2.start();



        thread1.join();
        thread2.join();
    }
}
