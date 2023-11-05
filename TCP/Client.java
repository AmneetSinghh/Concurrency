package TCP;


/* Client making Millions of requests */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    static final int PORT = 1731;
    static final String IP = "127.0.0.1";
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void startConnection() throws IOException {
        clientSocket = new Socket(IP, PORT);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public static String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    // why we need stop connection.
    public static void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    public static void main(String args[]) throws InterruptedException {
        try{
            for(Long i = 1L; i<=100000000; i++){
                System.out.println("Start connection : "+ i);
                startConnection();
//                String response = sendMessage("hello server");
//                System.out.println("Response-> : "+ response);
//                stopConnection();
            }
        } catch (Exception e){
            System.out.println("Error: "+ e);
        }
        Thread.sleep(10000);
    }
}



