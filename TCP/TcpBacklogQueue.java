package TCP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
/*
Main thread will accept the connections, spoon new thread for every new connection.
*/
public class TcpBacklogQueue {
    static final int PORT = 1731;

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
                System.out.println(TAG + " BufferedReader input-> " + input);
                Thread.sleep(5000);

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
            } catch (Exception e) {
                System.out.println();
            }
            System.out.println(TAG + "Requet finished");
        }
    }

    public static void main(String args[]) {
        try {
            System.out.println("Server at PORT: " + PORT + " Started");
            ServerSocket serverSocket = new ServerSocket(PORT, 2);// will find later.
            int i = 1;
            while (true) {
                Thread.sleep(4000);
                Socket clientSocketCon = serverSocket.accept(); // Blocking System Call
                System.out.println("Accepting new Connection : " + i);
                Thread thread = new Thread(new ProcessRequest(clientSocketCon, "Thread: " + i));// span new thread each time.
                thread.start();
                i++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

/*
netstat -an | grep "1731" | grep LISTEN | wc -l,       command for checking size of backlog queue,,
 */




/*
netstat -an: Displays all active network connections and listening ports.
grep "PORT_NUMBER": Filters the output to show lines containing the specified port number.
grep LISTEN: Further filters the output to show only the lines with the LISTEN state, which indicates the listening ports.
wc -l: Counts the number of lines, giving you the number of LISTEN connections.
Replace PORT_NUMBER with the actual port number you're interested in.

For example, if your ServerSocket is configured with a backlog of 1 and listening on port 8080, you would use:
 */