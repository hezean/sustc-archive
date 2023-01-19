import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientDemo {

    public static void main(String[] args) {
        boolean loop = true;
        String hostName = "localhost";
        int portNumber = 8888;
        while (loop) {
            try (Socket sock = new Socket(hostName, portNumber);
                 //set autoFlush true
                 PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));) {
                System.out.println("Please enter a command:");
                Scanner sc = new Scanner(System.in);
                // read from input
                String info = sc.nextLine();
                // send to server
                out.println(info);
                // read from server
                String fromServer;
                while ((fromServer = in.readLine()) != null) {
                    if (fromServer.length() > 0) {
                        System.out.println(fromServer);
                    }
                    if (fromServer.equals("Goodbye")) {
                        loop = false;
                        break;
                    }
                }

            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + hostName);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + hostName);
                System.exit(1);
            }
        }
    }

}
