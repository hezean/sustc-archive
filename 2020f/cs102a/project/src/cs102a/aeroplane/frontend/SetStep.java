package cs102a.aeroplane.frontend;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.frontend.model.BackgroundPanel;
import cs102a.aeroplane.frontend.model.MatchDicePicture;
import cs102a.aeroplane.model.ChessBoard;
import cs102a.aeroplane.util.SystemSelect;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SetStep {

    public static int stepSelected;
    public static boolean needFly;

    // 弹窗，只有设置好步数才能关闭
    public static void askPlayerStep(GameGUI nowGamingGUI, ChessBoard chessBoard, int[] rollResult, boolean flag) {
        JFrame setStepFrame = new JFrame("选择棋子要走的步数");
        nowGamingGUI.getPlayerInfoPanel().refresh();
        setStepFrame.setSize(300, 250);
        setStepFrame.setAlwaysOnTop(true);
        setStepFrame.setResizable(false);
        setStepFrame.setLocationRelativeTo(null);
        setStepFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // 作弊模式直接选择步数
        if (GameInfo.isIsCheatMode()) {

            JComboBox<Integer> choiceComboBox = new JComboBox<>();
            for (int i = 1; i <= 12; i++) {
                choiceComboBox.addItem(i);
                choiceComboBox.addItemListener(e -> SetStep.stepSelected = Integer.parseInt(Objects.requireNonNull(choiceComboBox.getSelectedItem()).toString()));
            }
            choiceComboBox.setOpaque(false);

            JButton confirmButton = new JButton("确定");
            confirmButton.setOpaque(false);
            confirmButton.setBorder(null);
            confirmButton.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 24));
            confirmButton.setForeground(Color.blue);
            confirmButton.addActionListener(e -> {
                SetStep.stepSelected = Integer.parseInt(Objects.requireNonNull(choiceComboBox.getSelectedItem()).toString());
                setStepFrame.dispose();
                chessBoard.nowMove = stepSelected;
                if (flag) chessBoard.continueAfterAsk();
                else chessBoard.continueAfterAskFalse();
            });


            JPanel choicePanel = new JPanel(new GridLayout(1, 1));
            choicePanel.add(choiceComboBox);
            choicePanel.setOpaque(false);

            JPanel basePanel = new JPanel(new GridLayout(2, 1));
            basePanel.setPreferredSize(new Dimension(150, 150));
            basePanel.add(choicePanel);
            basePanel.add(confirmButton);
            basePanel.setOpaque(false);

            String picPath = SystemSelect.getImagePath();
            JPanel picPanel = new BackgroundPanel((new ImageIcon(picPath + "setStep.jpg").getImage()));
            picPanel.add(basePanel);

            setStepFrame.add(picPanel);
        }
        //显示骰子，显示下拉菜单让用户选择
        else {
            int self = rollResult[0];
            int oppo = rollResult[1];
            JLabel selfDiceLabel = new JLabel();
            JLabel oppoDiceLabel = new JLabel();
            selfDiceLabel.setOpaque(false);
            oppoDiceLabel.setOpaque(false);
            selfDiceLabel.setIcon(MatchDicePicture.getImage(self));
            oppoDiceLabel.setIcon(MatchDicePicture.getImage(oppo));
            selfDiceLabel.setHorizontalAlignment(SwingConstants.CENTER);
            oppoDiceLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JComboBox<Integer> choiceComboBox = getPossibleChoice(rollResult);
            choiceComboBox.setOpaque(false);

            JButton confirmButton = new JButton("确定");
            confirmButton.setOpaque(false);
            confirmButton.addActionListener(e -> {
                SetStep.stepSelected = Integer.parseInt(Objects.requireNonNull(choiceComboBox.getSelectedItem()).toString());
                setStepFrame.dispose();
                chessBoard.nowMove = stepSelected;
                if (flag) chessBoard.continueAfterAsk();
                else chessBoard.continueAfterAskFalse();

            });


            JPanel dicePanel = new JPanel(new GridLayout(1, 2));
            dicePanel.setOpaque(false);
            dicePanel.add(selfDiceLabel);
            dicePanel.add(oppoDiceLabel);

            JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
            buttonPanel.setOpaque(false);
            buttonPanel.add(new JLabel());
            buttonPanel.add(confirmButton);
            buttonPanel.add(new JLabel());

            JPanel choicePanel = new JPanel(new GridLayout(1, 2));
            choicePanel.setOpaque(false);
            choicePanel.add(choiceComboBox);
            choicePanel.add(buttonPanel);

            JPanel basePanel = new JPanel(new GridLayout(2, 1, 0, 20));
            basePanel.setOpaque(false);
            basePanel.setPreferredSize(new Dimension(150, 100));
            basePanel.add(dicePanel);
            basePanel.add(choicePanel);

            String picPath = SystemSelect.getImagePath();
            JPanel picPanel = new BackgroundPanel((new ImageIcon(picPath + "setStep.jpg").getImage()));
            picPanel.setLayout(new GridLayout(1, 1));
            picPanel.add(basePanel);

            setStepFrame.add(picPanel);

        }

        setStepFrame.setVisible(true);
    }

    public static boolean askIfFly() {
        needFly = true;
        new TimeDial1().showDialog(Settings.window, "默认允许起飞，在接下来的界面选择任意数即可", 2);
        return needFly;
    }


    public static void askPlayerStep(ChessBoard chessBoard, int nowMovingNumber) {
        JFrame setStepFrame = new JFrame("选择棋子要走的步数");
        setStepFrame.setSize(300, 250);
        setStepFrame.setAlwaysOnTop(true);
        setStepFrame.setResizable(false);
        setStepFrame.setLocationRelativeTo(null);
        setStepFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        GameGUI.playerInfoPanel.refresh();
        // 作弊模式直接选择步数
        if (GameInfo.isIsCheatMode()) {
            JComboBox<Integer> choiceComboBox = new JComboBox<>();
            for (int i = 1; i <= 12; i++) {
                choiceComboBox.addItem(i);
                choiceComboBox.addItemListener(e -> SetStep.stepSelected = Integer.parseInt(Objects.requireNonNull(choiceComboBox.getSelectedItem()).toString()));
            }
            choiceComboBox.setOpaque(false);

            JButton confirmButton = new JButton("确定");
            confirmButton.setOpaque(false);
            confirmButton.setBorder(null);
            confirmButton.setFont(new java.awt.Font("微软雅黑", Font.BOLD, 24));
            confirmButton.setForeground(Color.blue);
            confirmButton.addActionListener(e -> {
                SetStep.stepSelected = Integer.parseInt(Objects.requireNonNull(choiceComboBox.getSelectedItem()).toString());
                setStepFrame.dispose();
                chessBoard.nowMove = stepSelected;
                System.err.println("nowMovePlane: " + nowMovingNumber);
                chessBoard.rollResult[0] = stepSelected;
                chessBoard.rollResult[1] = 0;


                chessBoard.getPlanes()[nowMovingNumber].steps = stepSelected;
                chessBoard.getPlanes()[nowMovingNumber].tryMovingFront();
            });


            JPanel choicePanel = new JPanel(new GridLayout(1, 1));
            choicePanel.add(choiceComboBox);
            choicePanel.setOpaque(false);

            JPanel basePanel = new JPanel(new GridLayout(2, 1));
            basePanel.setPreferredSize(new Dimension(150, 150));
            basePanel.add(choicePanel);
            basePanel.add(confirmButton);
            basePanel.setOpaque(false);

            String picPath = SystemSelect.getImagePath();
            JPanel picPanel = new BackgroundPanel((new ImageIcon(picPath + "setStep.jpg").getImage()));
            picPanel.add(basePanel);

            setStepFrame.add(picPanel);
        }
        //显示骰子，显示下拉菜单让用户选择
        else {
            int self = chessBoard.rollResult[0];
            int oppo = chessBoard.rollResult[1];
            JLabel selfDiceLabel = new JLabel();
            JLabel oppoDiceLabel = new JLabel();
            selfDiceLabel.setOpaque(false);
            oppoDiceLabel.setOpaque(false);
            selfDiceLabel.setIcon(MatchDicePicture.getImage(self));
            oppoDiceLabel.setIcon(MatchDicePicture.getImage(oppo));
            selfDiceLabel.setHorizontalAlignment(SwingConstants.CENTER);
            oppoDiceLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JComboBox<Integer> choiceComboBox = getPossibleChoice(new int[]{self, oppo});
            choiceComboBox.setOpaque(false);

            JButton confirmButton = new JButton("确定");
            confirmButton.setOpaque(false);
            confirmButton.addActionListener(e -> {
                SetStep.stepSelected = Integer.parseInt(Objects.requireNonNull(choiceComboBox.getSelectedItem()).toString());
                setStepFrame.dispose();
                chessBoard.nowMove = stepSelected;
                System.err.println("nowMovePlane: " + nowMovingNumber);

                chessBoard.getPlanes()[nowMovingNumber].steps = stepSelected;
                chessBoard.getPlanes()[nowMovingNumber].tryMovingFront();

            });


            JPanel dicePanel = new JPanel(new GridLayout(1, 2));
            dicePanel.setOpaque(false);
            dicePanel.add(selfDiceLabel);
            dicePanel.add(oppoDiceLabel);

            JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
            buttonPanel.setOpaque(false);
            buttonPanel.add(new JLabel());
            buttonPanel.add(confirmButton);
            buttonPanel.add(new JLabel());

            JPanel choicePanel = new JPanel(new GridLayout(1, 2));
            choicePanel.setOpaque(false);
            choicePanel.add(choiceComboBox);
            choicePanel.add(buttonPanel);

            JPanel basePanel = new JPanel(new GridLayout(2, 1, 0, 20));
            basePanel.setOpaque(false);
            basePanel.setPreferredSize(new Dimension(150, 100));
            basePanel.add(dicePanel);
            basePanel.add(choicePanel);

            String picPath = SystemSelect.getImagePath();
            JPanel picPanel = new BackgroundPanel((new ImageIcon(picPath + "setStep.jpg").getImage()));
            picPanel.setLayout(new GridLayout(1, 1));
            picPanel.add(basePanel);

            setStepFrame.add(picPanel);

        }

        setStepFrame.setVisible(true);
    }

    // 获取一个装满所有可能步数和配置好listener的JComboBox
    public static JComboBox<Integer> getPossibleChoice(int[] rollResult) {
        ArrayList<Integer> possibleChoice = new ArrayList<>();
        if (!GameInfo.isIsCheatMode()) {
            int a = rollResult[0] + rollResult[1];
            int b = rollResult[0] - rollResult[1];
            int c = rollResult[1] - rollResult[0];
            int e = rollResult[0] * rollResult[1];
            float f = rollResult[0] / (float) rollResult[1];
            float g = rollResult[1] / (float) rollResult[0];
            if (a <= 12) possibleChoice.add(a);
            if (b > 0 && b != a) possibleChoice.add(b);
            if (c > 0 && c != a) possibleChoice.add(c);
            if (e <= 12 && e != a && e != b && e != c) possibleChoice.add(e);
            if (f % 1f == 0f && f != a && f != b && f != c && f != e) possibleChoice.add((int) f);
            if (g % 1f == 0f && g != a && g != b && g != c && g != e && g != f) possibleChoice.add((int) g);
        } else {
            for (int i = 1; i <= 12; i++) {
                possibleChoice.add(i);
            }
        }

        JComboBox<Integer> stepChoices = new JComboBox<>();
        for (Integer integer : possibleChoice) stepChoices.addItem(integer);
        stepChoices.addItemListener(e -> SetStep.stepSelected = Integer.parseInt(Objects.requireNonNull(stepChoices.getSelectedItem()).toString()));
        stepSelected = -1;
        return stepChoices;
    }
}

class TimeDial1 {

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

        JButton confirm = new JButton("不起飞");
        confirm.setBounds(140, 120, 50, 60);
        confirm.addActionListener(e -> {
            SetStep.needFly = false;
            TimeDial1.this.dialog.dispose();
        });

        dialog = new JDialog(jFrameOfButton, true);
        dialog.setTitle("给你" + seconds + "秒，看好啦");
        dialog.setLayout(new GridLayout(2, 1));
        dialog.add(label);
        dialog.add(confirm);

        s.scheduleAtFixedRate(() -> {
            TimeDial1.this.seconds--;
            if (TimeDial1.this.seconds == 0) {
                TimeDial1.this.dialog.dispose();
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