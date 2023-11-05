package RaceCondition;

public class FirstExample {
    private static int sharedCounter = 0;// result is unpredictable. both threads can read same value at same time.

    public static void main(String[] args) {
        Runnable incrementTask = () -> {
            for (int i = 0; i < 10000; i++) {
                sharedCounter++;
            }
        };

        // Create two threads to increment the shared counter
        Thread thread1 = new Thread(incrementTask);
        Thread thread2 = new Thread(incrementTask);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final shared counter value: " + sharedCounter);
    }
}