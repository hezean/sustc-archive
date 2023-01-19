package cs102a.aeroplane.frontend;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.frontend.model.BackgroundPanel;
import cs102a.aeroplane.util.SystemSelect;

import javax.swing.*;
import java.awt.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Start {
    private static final JFrame startFrame = new JFrame("飞行棋");


    public static void main(String[] args) {
        JLabel title = new JLabel("飞行棋", JLabel.CENTER);
        title.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 50));
        title.setForeground(Color.BLACK);
        title.setOpaque(false);

        JButton startButton = new JButton("开始游戏");
        startButton.setOpaque(false);
        startButton.setBorder(null);
        startButton.setForeground(Color.YELLOW);
        startButton.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 26));
        startButton.addActionListener(e -> {
            GameGUI game = new GameGUI();
            game.setVisible(true);
            game.getChessBoard().startGame();
        });

        JButton continueButton = new JButton("继续游戏");
        continueButton.setOpaque(false);
        continueButton.setBorder(null);
        continueButton.setForeground(Color.YELLOW);
        continueButton.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 26));
        continueButton.addActionListener(e -> {
            LoadHistory loadHistory = new LoadHistory("读档");
            loadHistory.setVisible(true);
        });

        JButton storeButton = new JButton("道具商店");
        storeButton.setOpaque(false);
        storeButton.setBorder(null);
        storeButton.setForeground(Color.white);
        storeButton.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 24));
        storeButton.addActionListener(e -> {
            startFrame.setVisible(false);
            GameMall.window.editWallet.setEnabled(GameInfo.isSuperUser());
            GameMall.window.setVisible(true);
        });

        JButton settingButton = new JButton("游戏设置");
        settingButton.setOpaque(false);
        settingButton.setBorder(null);
        settingButton.setForeground(Color.white);
        settingButton.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 24));
        settingButton.addActionListener(e -> {
            startFrame.setVisible(false);
            Settings.window.setVisible(true);
        });


        JPanel subStartPanel = new JPanel();
        subStartPanel.setLayout(new GridLayout(6, 1, 10, 40));
        subStartPanel.setPreferredSize(new Dimension(300, 500));
        subStartPanel.setBackground(null);
        subStartPanel.setOpaque(false);
        subStartPanel.add(new JLabel());
        subStartPanel.add(title);
        subStartPanel.add(startButton);
        subStartPanel.add(continueButton);
        subStartPanel.add(storeButton);
        subStartPanel.add(settingButton);

        String picPath = SystemSelect.getImagePath();
        JPanel startPanel = new BackgroundPanel((new ImageIcon(picPath + "start.jpg").getImage()));
        startPanel.add(subStartPanel);
        startFrame.add(startPanel);
        startFrame.setSize(400, 600);

        startFrame.setLocationRelativeTo(null);
        startFrame.setResizable(false);
        startFrame.setVisible(true);
        startFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public static void popStart() {
        startFrame.setVisible(true);
    }
}
