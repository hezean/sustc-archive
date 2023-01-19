package cs102a.aeroplane.frontend;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.frontend.model.BackgroundPanel;
import cs102a.aeroplane.frontend.model.MatchDicePicture;
import cs102a.aeroplane.frontend.model.PlayerInfoPanel;
import cs102a.aeroplane.frontend.model.TimeDialog;
import cs102a.aeroplane.gamemall.GoodsList;
import cs102a.aeroplane.model.ChessBoard;
import cs102a.aeroplane.presets.Sound;
import cs102a.aeroplane.savegame.GameSaver;
import cs102a.aeroplane.util.SystemSelect;

import javax.swing.*;
import java.awt.*;

public class GameGUI extends JFrame {

    public static ImageIcon background;
    public static PlayerInfoPanel playerInfoPanel;

    // TODO: 2020/12/18 当棋子出现偏移时修改xy方向偏置
    public static JLabel selfDiceLabel;
    public static JLabel oppoDiceLabel;
    public ChessBoard chessBoard;
    String path = SystemSelect.getImagePath();
    JButton resetButton = new JButton("重置");
    JButton saveButton = new JButton("保存");
    JButton returnButton = new JButton("返回");
    ImageIcon bomb = new ImageIcon(path + "bomb.jpg");
    ImageIcon boeing = new ImageIcon(path + "boeing.jpg");
    ImageIcon VIP = new ImageIcon(path + "vip.jpg");

    JButton vipButton = new JButton(VIP);
    JButton bombButton = new JButton(bomb);
    JButton takeOffButton = new JButton(boeing);

    JPanel backgroundPanel;

    public GameGUI() {
        chessBoard = new ChessBoard(0, 0, this);
        this.setSize(900, 800);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(null);       // 以启用绝对布局

        this.setTitle("飞行棋");

        // 背景图片
        background = new ImageIcon(path + (GameInfo.getTheme() == 1 ? "海王主题.png" : "灵笼主题.jpg"));
        backgroundPanel = new BackgroundPanel(background.getImage());
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, 900, 800);
        backgroundPanel.setOpaque(false);
        backgroundPanel.setVisible(true);


        //窗口初始化
        ImageIcon boardImg = new ImageIcon(path + "blankBoard.png");
        JPanel boardImgPanel = new BackgroundPanel(boardImg.getImage());
        boardImgPanel.setLayout(null);
        boardImgPanel.setBounds(0, 0, 800, 800);

        chessBoard.setBounds(0, 0, 800, 800);
        chessBoard.setOpaque(false);
        boardImgPanel.add(chessBoard);

        boardImgPanel.setOpaque(false);
        backgroundPanel.add(boardImgPanel);

        //玩家面板
        playerInfoPanel = new PlayerInfoPanel(chessBoard);

        //util面板
        resetButton.setBounds(818, 590, 60, 60);
        saveButton.setBounds(818, 650, 60, 60);
        returnButton.setBounds(818, 710, 60, 60);
        resetButton.setOpaque(false);
        saveButton.setOpaque(false);
        returnButton.setOpaque(false);

        resetButton.addActionListener(e -> {
            Sound.GAMING_THEME1.end();
            Sound.GAMING_THEME2.end();
            GameGUI game1 = new GameGUI();
            game1.setVisible(true);
            game1.getChessBoard().startGame();
            this.dispose();
            System.gc();
        });
        saveButton.addActionListener(e -> {
            GameSaver.save(chessBoard);
            new TimeDialog().showDialog(Settings.window, "成功保存", 3);
        });
        returnButton.addActionListener(e -> {
            dispose();
            //打开startMenu
            Start.popStart();
            Sound.GAMING_THEME1.end();
            Sound.GAMING_THEME2.end();
        });

        JButton changeCheatMode = new JButton(!GameInfo.isIsCheatMode() ? "正常" : "作弊");
        changeCheatMode.setOpaque(false);
        changeCheatMode.setBorder(null);
        changeCheatMode.setForeground(Color.BLACK);
        changeCheatMode.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        changeCheatMode.setBounds(818, 130, 60, 30);
        changeCheatMode.addActionListener(e -> {
            if (!GameInfo.isIsCheatMode()) {
                changeCheatMode.setText("作弊");
                GameInfo.setIsCheatMode(true);
            } else {
                changeCheatMode.setText("正常");
                GameInfo.setIsCheatMode(false);
            }
            changeCheatMode.setOpaque(false);
            changeCheatMode.setBorder(null);
            changeCheatMode.setForeground(Color.BLACK);
            changeCheatMode.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        });

        int self = chessBoard.rollResult[0];
        int oppo = chessBoard.rollResult[1];
        selfDiceLabel = new JLabel();
        oppoDiceLabel = new JLabel();
        selfDiceLabel.setOpaque(false);
        oppoDiceLabel.setOpaque(false);
        selfDiceLabel.setIcon(MatchDicePicture.getImage(self));
        oppoDiceLabel.setIcon(MatchDicePicture.getImage(oppo));
        selfDiceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        oppoDiceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        selfDiceLabel.setBounds(800, 180, 100, 100);
        oppoDiceLabel.setBounds(800, 280, 100, 100);

        vipButton.setBounds(818, 385, 60, 60);
        bombButton.setBounds(818, 450, 60, 60);
        takeOffButton.setBounds(818, 515, 60, 60);
        vipButton.setOpaque(false);
        bombButton.setOpaque(false);
        takeOffButton.setOpaque(false);

        vipButton.addActionListener(e -> GoodsList.makeMeWin.use(chessBoard));
        bombButton.addActionListener(e -> GoodsList.bomb.use(chessBoard));
        takeOffButton.addActionListener(e -> GoodsList.takeOffAnyway.use(chessBoard));

        playerInfoPanel = new PlayerInfoPanel(chessBoard);
        playerInfoPanel.setBounds(820, 0, 60, 130);
        playerInfoPanel.setOpaque(false);

        //加入边缘的按钮
        backgroundPanel.add(resetButton);
        backgroundPanel.add(saveButton);
        backgroundPanel.add(returnButton);
        backgroundPanel.add(vipButton);
        backgroundPanel.add(bombButton);
        backgroundPanel.add(takeOffButton);
        backgroundPanel.add(playerInfoPanel);
        backgroundPanel.add(changeCheatMode);
        backgroundPanel.add(selfDiceLabel);
        backgroundPanel.add(oppoDiceLabel);
        backgroundPanel.setOpaque(false);


        this.add(backgroundPanel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public ChessBoard getChessBoard() {
        return chessBoard;
    }

    public PlayerInfoPanel getPlayerInfoPanel() {
        return playerInfoPanel;
    }
}