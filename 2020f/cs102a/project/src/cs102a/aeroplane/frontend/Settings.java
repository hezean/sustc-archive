package cs102a.aeroplane.frontend;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.frontend.model.BackgroundPanel;
import cs102a.aeroplane.util.SystemSelect;

import javax.swing.*;
import java.awt.*;

public class Settings extends JFrame {

    public static Settings window = new Settings("游戏设置");

    public static JButton enterSuperMode;

    public Settings(String title) {

        this.setTitle(title);
        this.setSize(400, 600);
        this.setLocationRelativeTo(null);

        JLabel themeLabel = new JLabel("当前主题：海王", JLabel.CENTER);
        themeLabel.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 20));
        themeLabel.setForeground(Color.WHITE);
        themeLabel.setOpaque(false);

        String path = SystemSelect.getImagePath();

        JButton themeSettings = new JButton("-> 灵笼主题");
        themeSettings.setOpaque(false);
        themeSettings.setBorder(null);
        themeSettings.setForeground(Color.WHITE);
        themeSettings.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        themeSettings.addActionListener(e -> {
            if (GameInfo.getTheme() == 1) {
                themeSettings.setText("-> 海王主题");
                themeLabel.setText("当前主题：灵笼");
                GameInfo.setTheme(2);
            } else {
                themeSettings.setText("-> 灵笼主题");
                themeLabel.setText("当前主题：海王");
                GameInfo.setTheme(1);
            }
            themeSettings.setOpaque(false);
            themeSettings.setBorder(null);
            themeSettings.setForeground(Color.WHITE);
            themeSettings.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        });


        JLabel modeLabel = new JLabel("当前模式：正常");
        modeLabel.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 20));
        modeLabel.setForeground(Color.WHITE);
        modeLabel.setOpaque(false);
        modeLabel.setHorizontalAlignment(SwingConstants.CENTER);


        JButton changeCheatMode = new JButton("-> 作弊模式");
        changeCheatMode.setOpaque(false);
        changeCheatMode.setBorder(null);
        changeCheatMode.setForeground(Color.WHITE);
        changeCheatMode.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        changeCheatMode.addActionListener(e -> {
            if (!GameInfo.isIsCheatMode()) {
                changeCheatMode.setText("-> 正常模式");
                modeLabel.setText("当前模式：作弊");
                GameInfo.setIsCheatMode(true);
            } else {
                changeCheatMode.setText("-> 作弊模式");
                modeLabel.setText("当前模式：正常");
                GameInfo.setIsCheatMode(false);
            }
            changeCheatMode.setOpaque(false);
            changeCheatMode.setBorder(null);
            changeCheatMode.setForeground(Color.WHITE);
            changeCheatMode.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        });


        JLabel onlineLabel = new JLabel("联机模式：关", JLabel.CENTER);
        onlineLabel.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 20));
        onlineLabel.setForeground(Color.WHITE);
        onlineLabel.setOpaque(false);

        JButton changeOnlineMode = new JButton("-> 开");
        changeOnlineMode.setOpaque(false);
        changeOnlineMode.setBorder(null);
        changeOnlineMode.setForeground(Color.WHITE);
        changeOnlineMode.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        changeOnlineMode.addActionListener(e -> {
            if (!GameInfo.isIsOnlineGame()) {
                changeOnlineMode.setText("-> 开");
                onlineLabel.setText("联机模式：关");
                GameInfo.setIsOnlineGame(true);
            } else {
                changeOnlineMode.setText("-> 关");
                onlineLabel.setText("联机模式：开");
                Connect.window.setVisible(true);
                GameInfo.setIsOnlineGame(false);
            }
            changeOnlineMode.setOpaque(false);
            changeOnlineMode.setBorder(null);
            changeOnlineMode.setForeground(Color.WHITE);
            changeOnlineMode.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        });


        JLabel humanCntLabel = new JLabel("人类：", JLabel.CENTER);
        humanCntLabel.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 20));
        humanCntLabel.setForeground(Color.WHITE);
        humanCntLabel.setOpaque(false);

        JRadioButton rb1 = new JRadioButton("1");
        JRadioButton rb2 = new JRadioButton("2");
        JRadioButton rb3 = new JRadioButton("3");
        JRadioButton rb4 = new JRadioButton("4", true);
        rb1.setOpaque(false);
        rb1.setBorder(null);
        rb1.setForeground(Color.WHITE);
        rb1.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        rb2.setOpaque(false);
        rb2.setBorder(null);
        rb2.setForeground(Color.WHITE);
        rb2.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        rb3.setOpaque(false);
        rb3.setBorder(null);
        rb3.setForeground(Color.WHITE);
        rb3.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        rb4.setOpaque(false);
        rb4.setBorder(null);
        rb4.setForeground(Color.WHITE);
        rb4.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));

        rb1.addActionListener(e -> {
            if (rb1.isSelected()) GameInfo.setHumanPlayerCnt(1);
        });
        rb2.addActionListener(e -> {
            if (rb2.isSelected()) GameInfo.setHumanPlayerCnt(2);
        });
        rb3.addActionListener(e -> {
            if (rb3.isSelected()) GameInfo.setHumanPlayerCnt(3);
        });
        rb4.addActionListener(e -> {
            if (rb4.isSelected()) GameInfo.setHumanPlayerCnt(4);
        });
        rb1.setOpaque(false);
        rb2.setOpaque(false);
        rb3.setOpaque(false);
        rb4.setOpaque(false);

        ButtonGroup humanCntSelection = new ButtonGroup();
        humanCntSelection.add(rb1);
        humanCntSelection.add(rb2);
        humanCntSelection.add(rb3);
        humanCntSelection.add(rb4);


        enterSuperMode = new JButton("获取管理权限");
        enterSuperMode.setOpaque(false);
        enterSuperMode.setBorder(null);
        enterSuperMode.setForeground(Color.red);
        enterSuperMode.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 14));
        enterSuperMode.addActionListener(e -> {
            if (!GameInfo.isSuperUser()) {
                EnterSuperMode.window.setVisible(true);
                if (EnterSuperMode.isRightPwd()) {
                    enterSuperMode.setText("已获取管理权限");
                    enterSuperMode.setEnabled(false);
                    GameInfo.setSuperUser(true);
                    GameMall.window.userInfoPanel.setEnabled(true);
                    enterSuperMode.setOpaque(false);
                    enterSuperMode.setBorder(null);
                    enterSuperMode.setForeground(Color.gray);
                    enterSuperMode.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 14));
                    GameMall.window.editWallet.setEnabled(true);
                }
            }
        });
        JPanel enterSuperModePanel = new JPanel();
        enterSuperModePanel.setLayout(new GridLayout(3, 3, 10, 10));
        enterSuperModePanel.setOpaque(false);
        enterSuperModePanel.add(new JLabel());
        enterSuperModePanel.add(new JLabel());
        enterSuperModePanel.add(new JLabel());
        enterSuperModePanel.add(new JLabel());
        enterSuperModePanel.add(enterSuperMode);
        enterSuperModePanel.add(new JLabel());
        enterSuperModePanel.add(new JLabel());
        enterSuperModePanel.add(new JLabel());
        enterSuperModePanel.add(new JLabel());

        JButton back = new JButton("返回菜单");
        back.setOpaque(false);
        back.setBorder(null);
        back.setForeground(Color.WHITE);
        back.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 18));
        back.addActionListener(e -> {
            window.setVisible(false);
            Start.popStart();
        });

        JPanel rowPanel1 = new JPanel();
        rowPanel1.setLayout(new GridLayout(1, 2, 10, 10));
        rowPanel1.add(themeLabel);
        rowPanel1.add(themeSettings);
        rowPanel1.setOpaque(false);

        JPanel onlinePanel = new JPanel();
        onlinePanel.setLayout(new GridLayout(1, 2, 10, 10));
        onlinePanel.add(onlineLabel);
        onlinePanel.setOpaque(false);
        onlinePanel.add(changeOnlineMode);


        JPanel cnt = new JPanel();
        cnt.setLayout(new GridLayout(1, 4));
        cnt.add(rb1);
        cnt.add(rb2);
        cnt.add(rb3);
        cnt.add(rb4);
        cnt.setOpaque(false);

        JPanel rowPanel2 = new JPanel();
        rowPanel2.setLayout(new GridLayout(1, 2, 10, 0));
        rowPanel2.add(humanCntLabel);
        rowPanel2.add(cnt);
        rowPanel2.setOpaque(false);

        JPanel isCheatModePanel = new JPanel();
        isCheatModePanel.setLayout(new GridLayout(1, 2, 10, 0));
        isCheatModePanel.add(modeLabel);
        isCheatModePanel.add(changeCheatMode);
        isCheatModePanel.setOpaque(false);

        JPanel backgroundPanel = new BackgroundPanel(new ImageIcon(path + "setting.jpg").getImage());
        backgroundPanel.setOpaque(false);
        backgroundPanel.setLayout(new GridLayout(6, 1, 10, 10));
        backgroundPanel.add(rowPanel1);
        backgroundPanel.add(isCheatModePanel);
        backgroundPanel.add(onlinePanel);
        backgroundPanel.add(rowPanel2);
        backgroundPanel.add(enterSuperModePanel);
        backgroundPanel.add(back);


        JPanel setSizePanel = new JPanel(new GridLayout(1, 1));
        setSizePanel.setPreferredSize(new Dimension(300, 500));
        setSizePanel.add(backgroundPanel);

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.add(setSizePanel);
    }
}