package cs102a.aeroplane.frontend;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.frontend.model.BackgroundPanel;
import cs102a.aeroplane.gamemall.GoodsList;
import cs102a.aeroplane.gamemall.Wallet;
import cs102a.aeroplane.util.SystemSelect;

import javax.swing.*;
import java.awt.*;

public class GameMall extends JFrame {

    public static GameMall window = new GameMall("道具商店");


    private static int asPlayer = 0;
    public JPanel userInfoPanel;
    JLabel userBalanceLabel = new JLabel();
    JLabel userDiscountLabel = new JLabel();
    JButton editWallet = new JButton("修改账户信息");
    JRadioButton player1 = new JRadioButton("玩家1：" + GameInfo.getPlayerName()[0], true);
    JRadioButton player2 = new JRadioButton("玩家2：" + GameInfo.getPlayerName()[1]);
    JRadioButton player3 = new JRadioButton("玩家3：" + GameInfo.getPlayerName()[2]);
    JRadioButton player4 = new JRadioButton("玩家4：" + GameInfo.getPlayerName()[3]);


    public GameMall(String title) {

        this.setTitle(title);
        this.setSize(400, 600);
        this.setLocationRelativeTo(null);

        player1.setOpaque(false);
        player1.setBorder(null);
        player1.setForeground(Color.WHITE);
        player1.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        player2.setOpaque(false);
        player2.setBorder(null);
        player2.setForeground(Color.WHITE);
        player2.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        player3.setOpaque(false);
        player3.setBorder(null);
        player3.setForeground(Color.WHITE);
        player3.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        player4.setOpaque(false);
        player4.setBorder(null);
        player4.setForeground(Color.WHITE);
        player4.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 16));
        player1.setHorizontalAlignment(SwingConstants.CENTER);
        player2.setHorizontalAlignment(SwingConstants.CENTER);
        player3.setHorizontalAlignment(SwingConstants.CENTER);
        player4.setHorizontalAlignment(SwingConstants.CENTER);

        player1.addActionListener(e -> {
            if (player1.isSelected()) {
                asPlayer = 0;
                refreshInfo();
            }
        });
        player2.addActionListener(e -> {
            if (player2.isSelected()) {
                asPlayer = 1;
                refreshInfo();
            }
        });
        player3.addActionListener(e -> {
            if (player3.isSelected()) {
                asPlayer = 2;
                refreshInfo();
            }
        });
        player4.addActionListener(e -> {
            if (player4.isSelected()) {
                asPlayer = 3;
                refreshInfo();
            }
        });

        ButtonGroup playerSelect = new ButtonGroup();
        playerSelect.add(player1);
        playerSelect.add(player2);
        playerSelect.add(player3);
        playerSelect.add(player4);

        JPanel userSelectPanel = new JPanel(new GridLayout(2, 2, -5, -15));
        userSelectPanel.add(player1);
        userSelectPanel.add(player2);
        userSelectPanel.add(player3);
        userSelectPanel.add(player4);
        userSelectPanel.setOpaque(false);


        this.userBalanceLabel.setText(String.format("        账户余额：%.2f金币", Wallet.getBalance(asPlayer)));
        userBalanceLabel.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 18));
        userBalanceLabel.setForeground(Color.WHITE);
        userBalanceLabel.setOpaque(false);


        this.userDiscountLabel.setText(String.format("        优惠方案：%d折", (int) (Wallet.getDiscountAsPercent(asPlayer) * 100)));
        userDiscountLabel.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 18));
        userDiscountLabel.setForeground(Color.WHITE);
        userDiscountLabel.setOpaque(false);

        this.editWallet.addActionListener(e -> new EditUserInfo().setVisible(true));
        this.editWallet.setEnabled(GameInfo.isSuperUser());
        editWallet.setOpaque(false);
        editWallet.setForeground(Color.red);
        editWallet.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 14));

        JPanel editWalletPanel = new JPanel(new GridLayout(1, 3));
        editWalletPanel.add(new JLabel());
        editWalletPanel.add(editWallet);
        editWalletPanel.add(new JLabel());
        editWalletPanel.setOpaque(false);

        userInfoPanel = new JPanel(new GridLayout(3, 1));
        userInfoPanel.add(userBalanceLabel);
        userInfoPanel.add(userDiscountLabel);
        userInfoPanel.add(editWalletPanel);
        userInfoPanel.setOpaque(false);


        String path = SystemSelect.getImagePath();
        ImageIcon bomb = new ImageIcon(path + "bomb.jpg");
        ImageIcon boeing = new ImageIcon(path + "boeing.jpg");
        ImageIcon VIP = new ImageIcon(path + "vip.jpg");

        JButton bombButton = new JButton(bomb);
        JButton boeingButton = new JButton(boeing);
        JButton vipButton = new JButton(VIP);
        bombButton.addActionListener(e -> {
            GoodsList.bomb.setAsPlayer(asPlayer);
            GoodsList.bomb.itemDetail.setVisible(true);
        });
        boeingButton.addActionListener(e -> {
            GoodsList.takeOffAnyway.setAsPlayer(asPlayer);
            GoodsList.takeOffAnyway.itemDetail.setVisible(true);
        });
        vipButton.addActionListener(e -> {
            GoodsList.makeMeWin.setAsPlayer(asPlayer);
            GoodsList.makeMeWin.itemDetail.setVisible(true);
        });

        JPanel goodsPanel = new JPanel(new GridLayout(1, 3, 0, 20));
        goodsPanel.add(bombButton);
        goodsPanel.add(boeingButton);
        goodsPanel.add(vipButton);

        JButton back = new JButton("返回菜单");
        back.setOpaque(false);
        back.setBorder(null);
        back.setForeground(Color.WHITE);
        back.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 22));
        back.addActionListener(e -> {
            window.setVisible(false);
            Start.popStart();
        });


        JPanel backgroundPanel = new BackgroundPanel(new ImageIcon(SystemSelect.getImagePath() + "gameMall.jpg").getImage());
        backgroundPanel.setOpaque(false);
        backgroundPanel.setLayout(new GridLayout(4, 1, 0, 10));
        backgroundPanel.add(userSelectPanel);
        backgroundPanel.add(userInfoPanel);
        backgroundPanel.add(goodsPanel);
        backgroundPanel.add(back);


        JPanel setSizePanel = new JPanel(new GridLayout(1, 1));
        setSizePanel.setPreferredSize(new Dimension(260, 500));
        setSizePanel.add(backgroundPanel);


        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.add(setSizePanel);
    }

    public static int getAsPlayer() {
        return asPlayer;
    }

    public final void refreshInfo() {
        this.userBalanceLabel.setText(String.format("        账户余额：%.2f金币", Wallet.getBalance(asPlayer)));
        this.userDiscountLabel.setText(String.format("        优惠方案：%d折", (int) (Wallet.getDiscountAsPercent(asPlayer) * 100)));
        this.player1.setText("玩家1：" + GameInfo.getPlayerName()[0]);
        this.player2.setText("玩家2：" + GameInfo.getPlayerName()[1]);
        this.player3.setText("玩家3：" + GameInfo.getPlayerName()[2]);
        this.player4.setText("玩家4：" + GameInfo.getPlayerName()[3]);

        GoodsList.bomb.asPlayer = asPlayer;
        GoodsList.takeOffAnyway.asPlayer = asPlayer;
        GoodsList.makeMeWin.asPlayer = asPlayer;

        GoodsList.bomb.refresh();
        GoodsList.takeOffAnyway.refresh();
        GoodsList.makeMeWin.refresh();

        this.editWallet.setEnabled(GameInfo.isSuperUser());
    }
}
