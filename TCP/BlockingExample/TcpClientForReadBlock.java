package TCP.BlockingExample;


import java.io.*;
import java.net.ServerSocket;
        import java.net.Socket;

/*
Main thread will accept the connections, spoon new thread for every new connection.
*/


public class TcpClientForReadBlock {
    static final int PORT = 1728;
    static final String IP = "127.0.0.1";
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void startConnection() throws IOException {
        clientSocket = new Socket(IP, PORT);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public static String sendMessage(String msg) throws IOException, InterruptedException, Exception {
        System.out.println("Client writing into server");
        out.println(msg);
        System.out.println("Client reading into server");
        Thread.sleep(9000);// client again down.
        String resp = in.readLine();// if server throws exceptoin client's read is blocked forever.
        System.out.println("getting response");
        return resp;
    }

    // why we need stop connection.
    public static void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    public static void main(String args[]) throws Exception {
        try{
            for(Long i = 1L; i<=1; i++){
                System.out.println("Start connection : "+ i);
                startConnection();
                Thread.sleep(10000);// client is gone........ in this spam of time....
                String response = sendMessage("hello server");
//                System.out.println("Response-> : "+ response);
                stopConnection();
            }
        } catch (Exception e){
            System.out.println("Error: "+ e);
            throw new Exception("sdf");
        }
//        Thread.sleep(10000);
    }
}