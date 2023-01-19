package cs102a.aeroplane.frontend;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.frontend.model.TimeDialog;
import cs102a.aeroplane.util.Dice;
import cs102a.aeroplane.util.Timer;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 后端只要调用 Battle.isWinner()
 */
public class Battle {

    public static boolean isWinner = true;

    public static boolean isWinner() {
        if (!GameInfo.isIsCheatMode()) {
            int[] result = Dice.rollDices();
            int self = result[0];
            int oppo = result[1];
            new TimeDialog().showDialog(Settings.window, String.format("你摇出 %d  %s  对方摇出 %d", self, (self < oppo ? " < " : " > "), oppo), 1);

            return self > oppo;

        } else {
            isWinner = true;
            new TimeDial().showDialog(Settings.window, "不选择，默认赢", 2);
            Timer.delay(0);
            return isWinner;
        }
    }
}

class TimeDial {

    private JDialog dialog = new JDialog();

    private int seconds;

    /**
     * @param jFrameOfButton 程序主窗口（按钮所在）
     * @param message        对话框主体消息
     * @param closeInSec     以秒记的自动关闭时间，可以提前按按钮关闭
     */
    public void showDialog(JFrame jFrameOfButton, String message, int closeInSec) {
        dialog.setLayout(null);

        seconds = closeInSec;
        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setBounds(80, 10, 200, 20);

        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();

        JButton confirm = new JButton("我要输");
        confirm.setBounds(140, 120, 50, 60);
        confirm.addActionListener(e -> {
            Battle.isWinner = false;
            TimeDial.this.dialog.dispose();
        });

        dialog = new JDialog(jFrameOfButton, true);
        dialog.setTitle("给你" + seconds + "秒，看好啦");
        dialog.setLayout(new GridLayout(2, 1));
        dialog.add(label);
        dialog.add(confirm);

        s.scheduleAtFixedRate(() -> {
            TimeDial.this.seconds--;
            if (TimeDial.this.seconds == 0) {
                TimeDial.this.dialog.dispose();
                System.gc();
            } else {
                dialog.setTitle("给你" + seconds + "秒，看好啦");
            }
        }, 1, 1, TimeUnit.SECONDS);

        dialog.setSize(new Dimension(250, 100));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
