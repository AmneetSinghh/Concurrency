package TCP.ThreadPool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/*
Main thread will accept the connections, spoon new thread for every new connection.
*/
public class TcpUsingThreadPool {
    static final int PORT = 1730;

    static class ProcessRequest implements Runnable {
        Socket clientSocketCon;
        String TAG;

        public ProcessRequest(Socket clientSocketCon) {
            this.clientSocketCon = clientSocketCon;
        }

        @Override
        public void run() {
            try {
                TAG = Thread.currentThread().getName();
                System.out.println(TAG + " Processing request");
                System.out.println(TAG + " " + clientSocketCon);
                /*
                 * InputStream- Read binary data from various sources, network,sockets, files, system.in
                 * clientSocketCon is same instance that is used to connect in client and server side.
                 * Read data are blocking operations, until data is available from client side to read.
                 */
                InputStream inp = clientSocketCon.getInputStream();
                System.out.println(TAG + " input Sytream-> " + inp);
                /*
                 * InputStreamReader - Convert bytes to characters stream, so that we can read characters.
                 * reading will start when we call read method.
                 */
                InputStreamReader reader = new InputStreamReader(inp);
                System.out.println(TAG + " input Stream reader-> " + reader);


                /*
                 * InputStreamReader - read data in chunks from buffer, buffer is generated data data in chunks from memory using system call.
                 *
                 */
                BufferedReader input = new BufferedReader(reader);// This line is stucking....
                System.out.println(TAG + " BufferedReader input-> implementation paused" + input);
                Thread.sleep(2000);

                /*
                 * Blocking if no data available to read, or client is down.
                 *
                 */
                String greeting = input.readLine();
                System.out.println(TAG + " Request_Data: " + greeting);
                /*
                 * Write into outputStream, needs write system call, so basicaly printWriter internal buffer writes to outputstream, then writing to it, needs system call Write
                 *
                 */
                PrintWriter output = new PrintWriter(clientSocketCon.getOutputStream(), true);// write into output.
                if ("GET / HTTP/1.1".equals(greeting)) {
                    output.println("HTTP/1.1 200 OK\r\n\r\n Hello world!\r\n");
                } else {
                    output.println("HTTP/1.1 200 OK\r\n\r\n unrecognized greeting\r\n");
                }
                clientSocketCon.close();// significance of each line.
                System.out.println(TAG + "Request finished");
            } catch (Exception e) {
                System.out.println();
            }

        }
    }

    public static void main(String args[]) {
        try {
            System.out.println("Server at PORT: Using executer services " + PORT + " Started");
            ServerSocket serverSocket = new ServerSocket(PORT);// will find later.
//            ExecutorService executorService = Executors.newFixedThreadPool(8);
            ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);// it has more fine tuned parameters for thread pool.


            /*
            1. set the size of queue.
            2. if queue is full give callback, then rejectQueueHandler is called....
             */
            int i = 1;
            while (true) {
                Socket clientSocketCon = serverSocket.accept(); // Blocking System Call
                System.out.println("Accepting new Connection : " + i);
                executorService.submit(new ProcessRequest(clientSocketCon));

                System.out.println("After accepting connections -> "+ executorService.getPoolSize() + " CorePoolsise: "+ executorService.getCorePoolSize() + " maxPoolSize "+ executorService.getMaximumPoolSize()+" QueueSize: "+ executorService.getQueue().size());
                i++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

/*
ThreadPool is growing till 10224,,, but it stopped accepting connections because :
After accepting connections -> 8 CorePoolsise: 8 maxPoolSize 8 QueueSize: 10224
java.net.SocketException: Too many open files (Accept failed)

OS file descriptions overloaded......
 */