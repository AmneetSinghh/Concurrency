package TCP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
/*
accept system call
read system call
1. do implement when read is blocking means input stream is empty
2. do implement when write is blocking , when server not read->
3. webservers are implemented also as an infinite for loop type.... always accepting connections.
4. servers always process request single at a time.


implementation :
1. blocking read
2. blocking write
3. multiple requests added into which queue, in tcp, implement that queue.....
4.
*/


/*                      Single Threaded Server                      */
/*                      Process single request at a time            */
public class TcpServer {
    static final int PORT = 1726;
    public static void main(String args[]) {
        try{
            System.out.println("Server at PORT: "+ PORT+ " Started");
            ServerSocket serverSocket = new ServerSocket(PORT);
            int i=1;
            while(true){
                Socket clientSocketCon =  serverSocket.accept(); // Blocking System Call


                System.out.println(clientSocketCon);
                // below line is blocking,,, if client.getInput is empty,,, then InputStream Reader till it not reaches endline ... so blocking....
                System.out.println(clientSocketCon.getInputStream().read());
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocketCon.getInputStream()));
                Thread.sleep(4000);
                String greeting = input.readLine();
                System.out.println(greeting+" time : "+ i);
                PrintWriter output = new PrintWriter(clientSocketCon.getOutputStream(), true);// write into output.
                if ("GET / HTTP/1.1".equals(greeting)) {
                    output.println("HTTP/1.1 200 OK\r\n\r\n Hello world!\r\n");

                    // what is output.write,,, is it blocking..
                }
                else {
                    output.println("HTTP/1.1 200 OK\r\n\r\n unrecognized greeting\r\n");
                }
                clientSocketCon.close();
                i++;
            }

        } catch (Exception e){
            System.out.println(e);
        }
    }
}