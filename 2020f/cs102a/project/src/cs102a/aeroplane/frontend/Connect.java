package cs102a.aeroplane.frontend;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.frontend.model.TimeDialog;
import cs102a.aeroplane.online.Client;
import cs102a.aeroplane.online.Server;

import javax.swing.*;
import java.awt.*;


public class Connect extends JFrame {
    public static Connect window = new Connect("联机模式");

    public Connect(String title) {

        this.setTitle(title);
        this.setSize(600, 220);
        this.setLocationRelativeTo(null);
        this.setVisible(false);
        this.setAlwaysOnTop(true);

        JPanel base = new JPanel();
        base.setLayout(new GridLayout(2, 1, 0, 0));
        base.setSize(new Dimension(600, 200));

        JLabel localIP = new JLabel("");
        try {
            localIP.setText("本地IP：" + Server.getLocalIP());
        } catch (Exception e) {
            new TimeDialog().showDialog(Settings.window, e.getMessage(), 3);
            this.dispose();
        }

        JTextField serverIp = new JTextField("如加入房间，请输入对方IP");

        JPanel upPanel = new JPanel();
        upPanel.setLayout(new GridLayout(2, 1, 0, 0));
        upPanel.add(localIP);
        upPanel.add(serverIp);

        JButton confirm = new JButton("确定");
        confirm.addActionListener(e -> {
            if (!serverIp.getText().equals("如加入房间，请输入对方IP"))
                GameInfo.localClient = new Client(serverIp.getText());
            else try {
                GameInfo.localServer.setSocket();
            } catch (Exception ex) {
                new TimeDialog().showDialog(Settings.window, ex.getMessage(), 3);
            }
            window.setVisible(false);
        });

        JPanel downPanel = new JPanel();
        downPanel.setLayout(new GridLayout(1, 3, 0, 0));
        downPanel.setOpaque(false);
        downPanel.add(new JLabel());
        downPanel.add(confirm);
        downPanel.add(new JLabel());

        base.add(upPanel);
        base.add(downPanel);

        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.add(base);
    }
}
