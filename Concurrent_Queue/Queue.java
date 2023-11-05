package Concurrent_Queue;

public class Queue {
    private int maxSize;
    private int [] queue;
    private int front;
    private int rear;
    private int currentSize;

    public Queue(){
        maxSize = 100000;
        queue = new int[100000];
        this.front = 0;
        this.rear = -1;
        this.currentSize = 0;
    }

    public void enqueue(int item){
        if(isFull()){
            System.out.println("Queue is full, not able to [push] element");
            return;
        }
        rear = (rear+1)% maxSize;// it will rotate if reaches end.
        queue[rear] = item;
        currentSize ++;
    }

    public int dequeue(){
        if(isEmpty()){
            System.out.println("Queue is Empty, not able to [pop] element");
            return -1;
        }
        int removedItem = queue[front];
        front = (front+1)% maxSize;// it will rotate if reaches end.
        currentSize --;
        return removedItem;
    }


    public int peek() {
        if (isEmpty()) {
            System.out.println("Queue is empty. Cannot peek.");
            return -1;
        }
        return queue[front];
    }

    public Boolean isFull(){
        return currentSize == maxSize;
    }

    public Boolean isEmpty(){
        return currentSize == 0;
    }

    public int size(){
        return currentSize;
    }
}
