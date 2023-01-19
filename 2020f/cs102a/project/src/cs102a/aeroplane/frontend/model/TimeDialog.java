package cs102a.aeroplane.frontend.model;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeDialog {

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

        JButton confirm = new JButton("好咯...");
        confirm.setBounds(140, 120, 50, 60);
        confirm.addActionListener(e -> TimeDialog.this.dialog.dispose());

        dialog = new JDialog(jFrameOfButton, true);
        dialog.setTitle("给你" + seconds + "秒，看好啦");
        dialog.setLayout(new GridLayout(2, 1));
        dialog.add(label);
        dialog.add(confirm);

        s.scheduleAtFixedRate(() -> {
            TimeDialog.this.seconds--;
            if (TimeDialog.this.seconds == 0) {
                TimeDialog.this.dialog.dispose();
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
