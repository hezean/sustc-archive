import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class ServerDemo {

    public static void main(String[] args) throws IOException {
        ArrayList<Integer> data = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            data.add(i);
        }

        HashMap<String, Function<ArrayList<Integer>, ArrayList<Integer>>> serviceProcess = new HashMap<>();
        serviceProcess.put("GetEven", (l) -> {
            l.removeIf((e) -> e % 2 != 0);
            return l;
        });
        serviceProcess.put("GetOdd", (l) -> {
            l.removeIf((e) -> e % 2 == 0);
            return l;
        });
        Predicate<Integer> isPrime = (e) -> {
            if (e == 1 || e == 0)
                return false;
            for (int i = 2; i <= Math.sqrt(e); i++) {
                if (e % i == 0)
                    return false;
            }
            return true;
        };
        serviceProcess.put("GetPrime", (l) -> {
            l.removeIf(isPrime.negate());
            return l;
        });
        serviceProcess.put("GetBiggerThanFive", (l) -> {
            l.removeIf((e) -> e <= 5);
            return l;
        });
        serviceProcess.put("Reset", (l) -> {
            for (int i = 0; i < 10000; i++) {
                l.add(i);
            }
            return l;
        });
        int portNumber = 8888;
        PrintWriter out = null;
        BufferedReader in = null;
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is OK, is waiting for connect...");
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Have a connect");

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine, outputLine;
                // Wait for input
                if ((inputLine = in.readLine()) != null) {
                    String[] command = inputLine.split(" ");
                    String response = "";
                    if (serviceProcess.containsKey(command[0])) {
                        System.out.println(command[0]);
                        ArrayList<Integer> newl = serviceProcess.get(command[0]).apply(data);
                        response = newl.toString();
                    } else {
                        response = "Goodbye";
                    }
                    out.println(response);
                }
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }

        }

    }
}
