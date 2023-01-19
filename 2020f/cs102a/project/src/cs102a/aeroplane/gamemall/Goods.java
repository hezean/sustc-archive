package cs102a.aeroplane.gamemall;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.frontend.GameMall;
import cs102a.aeroplane.frontend.Settings;
import cs102a.aeroplane.frontend.model.TimeDialog;
import cs102a.aeroplane.model.ChessBoard;

import javax.swing.*;
import java.awt.*;

public abstract class Goods {

    private final float price;
    private final int[] storeCnt = {2, 2, 2, 2};
    public int asPlayer;       // 记录本页的购买等信息/活动与此用户相关
    public JFrame itemDetail = new JFrame();
    JLabel possessionLabel;

    JLabel nameLabel;
    JLabel priceLabel;
    JButton buyButton;

    // 实例化一个介绍页面，默认invisible
    public Goods(float price, String itemName, String intro) {
        this.price = price;
        this.itemDetail.setTitle("道具：" + itemName);
        this.itemDetail.setSize(400, 300);
        this.itemDetail.setLocationRelativeTo(null);
        this.itemDetail.setResizable(false);
        this.itemDetail.setAlwaysOnTop(true);

        nameLabel = new JLabel(itemName);
        nameLabel.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 32));

        priceLabel = new JLabel(price + "金币");
        priceLabel.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 20));

        possessionLabel = new JLabel("拥有件数" + storeCnt[asPlayer]);
        possessionLabel.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 20));

        JLabel introLabel = new JLabel(intro);
        introLabel.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 14));

        buyButton = new JButton(Wallet.getDiscountAsPercent(asPlayer) == 1.00f ?
                "花费" + this.price + "金币购买一件" : String.format("优惠价 %.2f 金币", this.price * Wallet.getDiscountAsPercent(asPlayer)));
        buyButton.addActionListener(e -> {
            try {
                this.purchase(asPlayer);
            } catch (Exception ex) {
                TimeDialog td = new TimeDialog();
                td.showDialog(itemDetail, ex.getMessage(), 3);
            }
        });

        JPanel rowPanel1 = new JPanel();
        rowPanel1.setPreferredSize(new Dimension(300, 80));
        rowPanel1.setLayout(new GridLayout(1, 4));
        rowPanel1.add(nameLabel);

        JPanel rowPanel2 = new JPanel();
        rowPanel2.setPreferredSize(new Dimension(300, 60));
        rowPanel2.setLayout(new GridLayout(1, 3, 10, 0));
        rowPanel2.add(priceLabel);
        rowPanel2.add(possessionLabel);

        JPanel rowPanel3 = new JPanel();
        rowPanel3.setPreferredSize(new Dimension(300, 100));
        rowPanel3.setLayout(new GridLayout(1, 1));
        rowPanel3.add(introLabel);

        JPanel rowPanel4 = new JPanel();
        rowPanel4.setPreferredSize(new Dimension(300, 40));
        rowPanel4.setLayout(new GridLayout(1, 1));
        rowPanel4.add(buyButton);

        JPanel base = new JPanel(new GridLayout(4, 1));
        base.setPreferredSize(new Dimension(300, 250));
        base.add(rowPanel1);
        base.add(rowPanel2);
        base.add(rowPanel3);
        base.add(rowPanel4);

        itemDetail.add(base);
        itemDetail.setLocationRelativeTo(null);
        itemDetail.setDefaultCloseOperation(itemDetail.HIDE_ON_CLOSE);
    }

    public void refresh() {
        possessionLabel.setText("拥有件数" + storeCnt[asPlayer]);
        buyButton.setText(Wallet.getDiscountAsPercent(asPlayer) == 1.00f ?
                "花费" + this.price + "金币购买一件" : String.format("优惠价 %.2f 金币", this.price * Wallet.getDiscountAsPercent(asPlayer)));
    }

    public void purchase(int purchaser) throws Exception {
        if (price * Wallet.getDiscountAsPercent(purchaser) <= Wallet.getBalance(purchaser)) {
            Wallet.spendBalance(purchaser, price * Wallet.getDiscountAsPercent(purchaser));
            storeCnt[purchaser] += 1;
            possessionLabel.setText("拥有件数" + storeCnt[asPlayer]);       // 刷新内容
            GameMall.window.refreshInfo();
        } else throw new Exception("啊哦，余额不足啦");
    }


    public int[] getStoreCnt() {
        return storeCnt;
    }


    protected void setStoreCnt(int storeCnt, int nowPlayer) {
        if (GameInfo.isSuperUser()) this.storeCnt[nowPlayer] = storeCnt;
    }

    public void setAsPlayer(int asPlayer) {
        this.asPlayer = asPlayer;
    }

    public abstract void use(ChessBoard chessBoard);

    public boolean isAbleToUse(ChessBoard chessBoard) {
        return storeCnt[chessBoard.getNowPlayer()] > 0;
    }

    public void unableToUse() {
        new TimeDialog().showDialog(Settings.window, "剩余量不足，无法使用", 2);
    }
}
