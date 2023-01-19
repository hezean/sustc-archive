package cs102a.aeroplane.online;

import cs102a.aeroplane.frontend.Settings;
import cs102a.aeroplane.frontend.model.TimeDialog;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Server implements Closeable {

    protected final int serverPort = 20924;   // Because this port is safe to use and I ❤ this number
    protected InputStream inputStream;
    protected OutputStream outputStream;
    private ServerSocket serverSocket;
    private Socket socket;


    // 建立房间 -> 实例化Server -> 展示ip供其他人加入
    public Server() {
        try {
            serverSocket = new ServerSocket(serverPort, 4);     // 等待连接的队列最长4，多余的直接挤出
        } catch (IOException e) {
            new TimeDialog().showDialog(Settings.window, e.getMessage(), 10);
        }
    }

    /**
     * @return 本地IP的String格式 或 报错信息
     * TODO 在作为服务器的机器上将本地址显示出来，提示其他用户输入此ip进行连接
     * @throws UnknownHostException 本机网络异常
     */
    public static String getLocalIP() throws Exception {
        try {
            return InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            throw new UnknownHostException("嗯？获取不到自己的IP地址？？\n怎么会这样( •̥́ ˍ •̀ू )");
        }
    }

    /**
     * @throws IOException 客户机配置异常
     * @description: 暂停进程，重连接（配置客户机）
     */
    public void setSocket() throws Exception {
        try {
            this.socket = serverSocket.accept();
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new IOException("呐呐，先辈！与玩家的连接被破坏了唔...");
        }
    }

    /**
     * @throws IOException 发送信息失败
     * @description: 将信息写入输出流
     */
    public void sendMsg(String msg) throws Exception {
        try {
            byte[] bytesMsg = msg.getBytes();
            outputStream.write(bytesMsg.length);
            outputStream.write(bytesMsg);
            outputStream.flush();
        } catch (IOException e) {
            throw new IOException("呐呐，先辈！连接有些小问题唔...");
        }
    }
//写

    /**
     * @return 获取游戏相关状态变更/结束等广播
     * @description: 将信息写入输出流
     */
    public String sendMsg() {
        ArrayList<Byte> msg = new ArrayList<>();

        try {
            int msgLength = inputStream.read();
            for (int i = 0; i < msgLength; i++) {
                msg.add((byte) inputStream.read());
            }
            return msg.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return msg.toString();
        }
    }

    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
