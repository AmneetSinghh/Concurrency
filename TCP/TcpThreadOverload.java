package TCP;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
Main thread will accept the connections, spoon new thread for every new connection.
*/
public class TcpThreadOverload {
    static final int PORT = 1729;
    static List<Thread> threads;

    static class ProcessRequest implements Runnable {
        Socket clientSocketCon;
        String TAG;

        public ProcessRequest(Socket clientSocketCon, String threadName) {
            this.clientSocketCon = clientSocketCon;
            this.TAG = threadName;
        }

        @Override
        public void run() {
            try {
                System.out.println(TAG + " Processing request");// blocking....
                System.out.println(TAG + " " + clientSocketCon);
                InputStream inp = clientSocketCon.getInputStream();
                System.out.println(TAG + " input Sytream-> " + inp);
                InputStreamReader reader = new InputStreamReader(inp);
                System.out.println(TAG + " input Stream reader-> " + reader);
                BufferedReader input = new BufferedReader(reader);// This line is stucking....
                System.out.println(TAG + " BufferedReader input-> " + input);
//                for(Long i=0l;i<=10000000000000l;i++){
//                    // sleeping mode.
//                }
                Thread.sleep(1000);
                String greeting = input.readLine();
                System.out.println(TAG + " Request_Data: " + greeting);
                PrintWriter output = new PrintWriter(clientSocketCon.getOutputStream(), true);// write into output.
                if ("GET / HTTP/1.1".equals(greeting)) {
                    output.println("HTTP/1.1 200 OK\r\n\r\n Hello world!\r\n");
                } else {
                    output.println("HTTP/1.1 200 OK\r\n\r\n unrecognized greeting\r\n");
                }
//                clientSocketCon.close();// significance of each line.
//                input.close();// significance of each line.
//                output.close();
//                inp.close();
//                reader.close();
            } catch (Exception e) {
                System.out.println();
            }
            System.out.println(TAG + "Requet finished");
        }
    }

    public static int stateOfThread() {
        int idleCount = 0,completeCount = 0,runningCount =0;
        for (Thread thread : threads) {
            if (thread.getState() == Thread.State.WAITING || thread.getState() == Thread.State.TIMED_WAITING) {
                idleCount++;
            }
            else if(thread.getState() == Thread.State.TERMINATED){
                completeCount++;
            }
            else if(thread.getState() == Thread.State.RUNNABLE){
                runningCount++;
            }
        }
        System.out.println("Thread: State idle : "+ idleCount+" Running : "+ runningCount+" Complete "+completeCount);
        return idleCount;
    }

    public static void main(String args[]) {
        try {
            threads = new ArrayList<>();
            System.out.println("Server at PORT: " + PORT + " Started");
            ServerSocket serverSocket = new ServerSocket(PORT);
            int i = 1;
            while (true) {
                Socket clientSocketCon = serverSocket.accept(); // Blocking System Call
                System.out.println("Accepting new Connection : " + i);
                Thread thread = new Thread(new ProcessRequest(clientSocketCon, "Thread: " + i));// span new thread each time.
                thread.start();
                threads.add(thread);
                i++;
                if (i%10 == 0) stateOfThread();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

/*
1. If I do stop connection after every open connection on client and server :
    Error: java.net.ConnectException: Connection refused (Connection refused)

    same below error :
    [22.893s][warning][os,thread] Failed to start thread "Unknown thread" - pthread_create failed (EAGAIN) for attributes: stacksize: 1024k, guardsize: 4k, detached.
[22.893s][warning][os,thread] Failed to start the native thread for java.lang.Thread "Thread-4064"
Exception in thread "main" java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
	at java.base/java.lang.Thread.start0(Native Method)
	at java.base/java.lang.Thread.start(Thread.java:798)
	at TCP.TcpThreadOverload.main(TcpThreadOverload.java:67)


	1. almost same system resources.
    2. Idle Count of Threads: 3899    Thread.sleep(5000)
    3. if not add thread.sleep(5000) Thread: State idle : 0 Running : 0 Complete 113069
    4. Thread: State idle : 0 Running : 4059 Complete 0, If I convert thead.sleep to looop till 10^9

 */


/*
If I do not Close connection on client:   client requests->1 million loop
    Error: java.net.ConnectException: Operation timed out (Connection timed out)
    mac os Threads - 4032
    system utilize of mac - 30-40%
    Real memory size - 1.4 gb
    After accepting 4k connections it failed with exception:

    // Exception occurs at Thread.Star()
    Failed to start thread "Unknown thread" - pthread_create failed (EAGAIN) for attributes: stacksize: 1024k, guardsize: 4k, detached.
    Failed to start the native thread for java.lang.Thread "Thread-4065"
    Exception in thread "main" java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
        at java.base/java.lang.Thread.start0(Native Method)
        at java.base/java.lang.Thread.start(Thread.java:798)
        at TCP.TcpThreadOverload.main(TcpThreadOverload.java:63)

    It haven't accepted any more connections after this exception...





    if threads are doing well,,, after some point :
    java.net.SocketException: Too many open files (Accept failed)


    The error message "java.net.SocketException: Too many open files (Accept failed)" occurs when the operating system has reached the limit on the number of file descriptors that can be opened simultaneously. A file descriptor is a unique identifier used to access files, sockets, or other I/O resources in the operating system.
 */