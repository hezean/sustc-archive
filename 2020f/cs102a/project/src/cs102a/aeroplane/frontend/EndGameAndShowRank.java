package cs102a.aeroplane.frontend;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.presets.Sound;
import cs102a.aeroplane.util.SystemSelect;

import javax.swing.*;
import java.awt.*;

public class EndGameAndShowRank extends JFrame {

    public EndGameAndShowRank(GameGUI nowGamingGUI) {
        this.setTitle("排行榜");
        this.setLocationRelativeTo(null);
        this.setResizable(true);

        String path = SystemSelect.getImagePath();

        GridBagLayout gridBagLayout = new GridBagLayout();

        JPanel mainPanel = new JPanel(gridBagLayout);
        mainPanel.setSize(new Dimension(300, 500));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();


        //游戏结束label
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 1;
        JLabel overLabel = new JLabel("游戏结束");
        overLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        gridBagLayout.setConstraints(overLabel, gridBagConstraints);


        //确定button
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 1;
        JButton confirmButton = new JButton("确定");
        confirmButton.addActionListener(e -> {
            dispose();
            nowGamingGUI.dispose();
            System.gc();
            Start.popStart();
            Sound.GAMING_THEME1.end();
            Sound.GAMING_THEME2.end();
        });
        gridBagLayout.setConstraints(confirmButton, gridBagConstraints);


        //中间4个玩家的排名
        //第一名的三个label
        //第一名的图片
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel number1 = new JLabel(new ImageIcon(path + "第一名.png"));
        gridBagLayout.setConstraints(number1, gridBagConstraints);

        //第一名的第二个label
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel info1ofNumber1 = new JLabel(String.format(
                "第一名：玩家%d (" + GameInfo.getPlayerName()[nowGamingGUI.getChessBoard().getWinner1Index()] + ")",
                nowGamingGUI.getChessBoard().getWinner1Index() + 1));
        gridBagLayout.setConstraints(info1ofNumber1, gridBagConstraints);

        //第一名的第三个label
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel info2ofNumber1 = new JLabel("步数：" + nowGamingGUI.getChessBoard().getPlayerSteps()[nowGamingGUI.getChessBoard().getWinner1Index()]);
        gridBagLayout.setConstraints(info2ofNumber1, gridBagConstraints);


        //第二名的三个label
        //第二名的图片
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel number2 = new JLabel(new ImageIcon(path + "第二名.png"));
        gridBagLayout.setConstraints(number2, gridBagConstraints);

        //第二名的第二个label
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel info1ofNumber2 = new JLabel(String.format(
                "第二名：玩家%d (" + GameInfo.getPlayerName()[nowGamingGUI.getChessBoard().getWinner2Index()] + ")",
                nowGamingGUI.getChessBoard().getWinner2Index() + 1));
        gridBagLayout.setConstraints(info1ofNumber2, gridBagConstraints);

        //第二名的第三个label
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel info2ofNumber2 = new JLabel("步数：" + nowGamingGUI.getChessBoard().getPlayerSteps()[nowGamingGUI.getChessBoard().getWinner2Index()]);
        gridBagLayout.setConstraints(info2ofNumber2, gridBagConstraints);


        //第三名的三个label
        //第三名的图片
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel number3 = new JLabel(new ImageIcon(path + "第三名.png"));
        gridBagLayout.setConstraints(number3, gridBagConstraints);

        //第三名的第二个label
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel info1ofNumber3 = new JLabel(String.format(
                "第三名：玩家%d (" + GameInfo.getPlayerName()[nowGamingGUI.getChessBoard().getWinner3Index()] + ")",
                nowGamingGUI.getChessBoard().getWinner3Index() + 1));
        gridBagLayout.setConstraints(info1ofNumber3, gridBagConstraints);

        //第三名的第三个label
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel info2ofNumber3 = new JLabel("步数：" + nowGamingGUI.getChessBoard().getPlayerSteps()[nowGamingGUI.getChessBoard().getWinner3Index()]);
        gridBagLayout.setConstraints(info2ofNumber3, gridBagConstraints);


        //第四名的三个label
        //第四名的图片
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel number4 = new JLabel(new ImageIcon(path + "第四名.png"));
        gridBagLayout.setConstraints(number4, gridBagConstraints);

        //第四名的第二个label
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel info1ofNumber4 = new JLabel("第四名：玩家" + nowGamingGUI.getChessBoard().getWinner4Index() +
                "(" + GameInfo.getPlayerName()[nowGamingGUI.getChessBoard().getWinner4Index()] + ")");
        gridBagLayout.setConstraints(info1ofNumber4, gridBagConstraints);

        //第四名的第三个label
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        JLabel info2ofNumber4 = new JLabel(""); // 留空
        gridBagLayout.setConstraints(info2ofNumber4, gridBagConstraints);


        //初始化
        mainPanel.add(overLabel);
        mainPanel.add(number1);
        mainPanel.add(info1ofNumber1);
        mainPanel.add(info2ofNumber1);
        mainPanel.add(number2);
        mainPanel.add(info1ofNumber2);
        mainPanel.add(info2ofNumber2);
        mainPanel.add(number3);
        mainPanel.add(info1ofNumber3);
        mainPanel.add(info2ofNumber3);
        mainPanel.add(number4);
        mainPanel.add(info1ofNumber4);
        mainPanel.add(info2ofNumber4);
        mainPanel.add(confirmButton);
        mainPanel.setOpaque(false);

        this.add(mainPanel);

        this.setSize(400, 600);
        this.setAlwaysOnTop(true);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
