package cs102a.aeroplane.frontend.model;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.frontend.GameGUI;
import cs102a.aeroplane.model.ChessBoard;
import cs102a.aeroplane.util.SystemSelect;

import javax.swing.*;
import java.awt.*;


// 玩家面板,游戏窗体的右上角
// PlayerInfoPanel pip = new PlayerInfoPanel(this)
// pip.refresh();
public class PlayerInfoPanel extends JPanel {

    private static final String path = SystemSelect.getImagePath();

    private static final ImageIcon pic1 = new ImageIcon(path + "player1.jpg");
    private static final ImageIcon pic2 = new ImageIcon(path + "player2.jpg");
    private static final ImageIcon pic3 = new ImageIcon(path + "player3.jpg");
    private static final ImageIcon pic4 = new ImageIcon(path + "player4.jpg");

    private final JLabel playerLabel;
    private final JLabel nameLabel;
    private final JLabel colorLabel;
    private final ChessBoard chessBoard;

    public PlayerInfoPanel(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
        this.setLayout(null);
        this.setSize(60, 130);

        ImageIcon pic;
        switch (chessBoard.getNowPlayer() + 1) {
            case 1:
                pic = pic2;
                break;
            case 2:
                pic = pic3;
                break;
            case 3:
                pic = pic4;
                break;
            default:
                pic = pic1;
                break;
        }
        playerLabel = new JLabel(pic);
        playerLabel.setSize(50, 50);
        playerLabel.setBounds(5, 10, 50, 50);
        playerLabel.setOpaque(false);

        String color;
        switch (chessBoard.getNowPlayer() + 1) {
            case 2:
                color = "绿";
                break;
            case 3:
                color = "红";
                break;
            case 4:
                color = "黄";
                break;
            default:
                color = "蓝";
                break;
        }
        nameLabel = new JLabel(GameInfo.getPlayerName()[chessBoard.getNowPlayer()]);
        nameLabel.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 20));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(8, 80, 50, 20);
        nameLabel.setOpaque(false);

        colorLabel = new JLabel(color);
        colorLabel.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 15));
        colorLabel.setForeground(Color.WHITE);
        colorLabel.setBounds(8, 110, 50, 20);
        colorLabel.setOpaque(false);

        this.add(playerLabel);
        this.add(nameLabel);
        this.add(colorLabel);
    }

    public void refresh() {
        ImageIcon pic;
        switch (chessBoard.getNowPlayer() + 1) {
            case 1:
                pic = pic2;
                break;
            case 2:
                pic = pic3;
                break;
            case 3:
                pic = pic4;
                break;
            default:
                pic = pic1;
                break;
        }
        playerLabel.setIcon(pic);

        String color;
        switch (chessBoard.getNowPlayer() + 1) {
            case 2:
                color = "绿";
                break;
            case 3:
                color = "红";
                break;
            case 4:
                color = "黄";
                break;
            default:
                color = "蓝";
                break;
        }
        nameLabel.setText(GameInfo.getPlayerName()[chessBoard.getNowPlayer()]);
        colorLabel.setText(color);

        int self = chessBoard.rollResult[0];
        int oppo = chessBoard.rollResult[1];

        GameGUI.selfDiceLabel.setIcon(MatchDicePicture.getImage(self));
        GameGUI.oppoDiceLabel.setIcon(MatchDicePicture.getImage(oppo));

    }
}