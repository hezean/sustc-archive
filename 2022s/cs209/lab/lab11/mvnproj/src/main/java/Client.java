import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private static Scanner sc = new Scanner(System.in);
    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        try (Socket sock = new Socket("localhost", 8765);
             PrintWriter out = new PrintWriter(sock.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()))) {
//            out.println("GET / HTTP/1.1");
//            out.println("Host: localhost");
//            out.println("Host: localhost:8765");
//            out.println("");
            out.println(sc.nextLine());
            out.flush();
            String resp = in.readLine();
            System.out.println(resp);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            System.exit(1);
        }
    }

}
