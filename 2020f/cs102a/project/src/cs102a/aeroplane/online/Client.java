package cs102a.aeroplane.online;

import cs102a.aeroplane.frontend.Settings;
import cs102a.aeroplane.frontend.model.TimeDialog;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client extends Server {

    protected InputStream inputStream;
    protected OutputStream outputStream;
    private Socket socket;

    // TODO: 2020/12/12 此ip为用户输入，加入房间
    // 按键 加入房间 -> 输入ip传递过来 -> 实例化Client
    public Client(String ip) {
        try {
            socket = new Socket(ip, serverPort);
            socket.setSoTimeout(10000);                  // 尝试接收数据的超时 10s
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (Exception e) {
            new TimeDialog().showDialog(Settings.window, e.getMessage(), 5);
        }
    }

    public static void getAndApplyChange() {
//        加标识符

    }

    public static void uploadChanges() {

    }

    // 获取本机连接端口，以此大小给本地分配 myColor
    public int getLocalPort() {
        try {
            return socket.getLocalPort();
        } catch (Exception e) {
            return -1;
        }
    }

    public void notifyNewWinner() {

    }
}
