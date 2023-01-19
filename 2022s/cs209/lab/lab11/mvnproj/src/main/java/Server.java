import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Server {
    private ServerSocket serverSock;
    private List<Faculty> faculties;
    private Map<String, Function<String, Object>> route;
    private final ObjectMapper mapper = new ObjectMapper();

    public Server(int port) throws IOException {
        this.serverSock = new ServerSocket(port);
        MappingIterator<Faculty> facultyIter = new CsvMapper()
                .readerWithTypedSchemaFor(Faculty.class)
                .readValues(new InputStreamReader(new FileInputStream(
                        String.valueOf(new File("src/main/resources/FacultyList.csv"))),
                        Charset.forName("Windows-1252")));
        faculties = facultyIter.readAll();

        route = new HashMap<>(3);
        route.put("NAME", name -> faculties.stream()
                .filter(f -> name.strip().equalsIgnoreCase(f.getName()))
                .findFirst()
                .orElse(null));
        route.put("FIRSTLETTER", letter -> faculties.stream()
                .filter(f -> f.getName().startsWith(letter.strip()))
                .collect(Collectors.toList()));
        route.put("DEP", dep -> faculties.stream()
                .filter(f -> dep.strip().equalsIgnoreCase(f.getDepartment().strip()))
                .collect(Collectors.toList()));
    }

    public void dispatch() {
        while (true) {
            try {
                Socket client = serverSock.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                String req, resp = "";
                while ((req = in.readLine()) == null) {}
                    String[] command = req.split(" ");
                    if (route.containsKey(command[0])) {
                        Object respObj = route.get(command[0]).apply(req.substring(req.indexOf(' ') + 1));
                        resp = mapper.writeValueAsString(respObj);
//                    }
                    out.println(resp);
                }
                client.close();

                System.out.println(req);
                System.out.println(resp);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(8765);
        server.dispatch();
    }

}
