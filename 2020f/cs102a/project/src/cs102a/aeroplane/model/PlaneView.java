package cs102a.aeroplane.model;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.presets.BoardCoordinate;
import cs102a.aeroplane.presets.PlaneState;
import cs102a.aeroplane.util.SystemSelect;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @apiNote 点击按键时会自动移动叠子，虽然他们是 invisible & disabled
 * 棋子移动到终点会自动解散叠子状态
 */
public class PlaneView extends JButton {
    private final int color;
    private final int number;
    private final int itsHangar;

    // TODO: 2020/12/14 放好棋盘GUI后把棋盘图片左上角重定位(0,0)
    private final int xOffSet;
    private final int yOffSet;
    private final ChessBoard chessboard;    // 引用
    private final Aeroplane aeroplane;
    private int state;
    private int numOfStackedPlanes;
    MouseListener ableToMoveTipListener = new MouseListener() {
        @Override
        // 选择此飞机，禁止其他点击，飞到相应位置
        public void mousePressed(MouseEvent e) {
            System.out.println("Click plane " + number);
            System.out.printf("now index %d%n", aeroplane.getGeneralGridIndex());
            for (Aeroplane p : chessboard.getPlanes()) {
                p.getPlaneView().setEnabled(false);
            }
            aeroplane.steps = chessboard.nowMove;
            aeroplane.tryMovingFront();
        }

        // 做了个移入移出的可选择的提示
        @Override
        public void mouseEntered(MouseEvent e) {
            StringBuilder themeSelectedIconPath = new StringBuilder();
            themeSelectedIconPath.append(SystemSelect.getImagePath());

            themeSelectedIconPath.append("pl_fin_");
            switch (color) {
                case PlaneState.BLUE:
                    themeSelectedIconPath.append("bl.png");
                    break;
                case PlaneState.GREEN:
                    themeSelectedIconPath.append("gr.png");
                    break;
                case PlaneState.RED:
                    themeSelectedIconPath.append("re.png");
                    break;
                case PlaneState.YELLOW:
                    themeSelectedIconPath.append("ye.png");
                    break;
            }
            setIcon(new ImageIcon(themeSelectedIconPath.toString()));
        }

        // 做了个移入移出的可选择的提示
        @Override
        public void mouseExited(MouseEvent e) {
            setIconAsPlaneNum(numOfStackedPlanes);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }
    };

    public PlaneView(ChessBoard chessboard, int number, int color, int itsHangar, int xOffSet, int yOffSet, Aeroplane aeroplane) {
        this.color = color;
        this.xOffSet = xOffSet;
        this.yOffSet = yOffSet;
        this.chessboard = chessboard;
        this.number = number;
        this.itsHangar = itsHangar;
        this.aeroplane = aeroplane;

        this.setOpaque(false);
        this.setBorder(null);

        this.setSize(BoardCoordinate.GRID_SIZE, BoardCoordinate.GRID_SIZE);

        this.numOfStackedPlanes = 1;
        this.state = PlaneState.IN_HANGAR;
        this.setIconAsPlaneNum(1);  // 设置单个飞机图片
        this.setVisible(true);

        chessboard.add(this);
    }

    // 输入飞机叠子数量，将此飞机设置为对应的图片
    public void setIconAsPlaneNum(int numOfStackedPlanes) {
        this.numOfStackedPlanes = numOfStackedPlanes;
        StringBuilder themeSelectedIconPath = new StringBuilder();

        themeSelectedIconPath.append(SystemSelect.getImagePath());
        themeSelectedIconPath.append(GameInfo.getTheme() == 1 ? "t1_p" : "t2_p");
        if (numOfStackedPlanes == 0) numOfStackedPlanes++;
        themeSelectedIconPath.append(numOfStackedPlanes).append("_");
        switch (color) {
            case PlaneState.BLUE:
                themeSelectedIconPath.append("bl.png");
                break;
            case PlaneState.GREEN:
                themeSelectedIconPath.append("gr.png");
                break;
            case PlaneState.RED:
                themeSelectedIconPath.append("re.png");
                break;
            case PlaneState.YELLOW:
                themeSelectedIconPath.append("ye.png");
                break;
        }
        setIcon(new ImageIcon(themeSelectedIconPath.toString()));
    }

    public void moveTo(int generalIndex) {
        if (generalIndex == -1) {
            aeroplane.backToHangarWhenFinish();
            return;
        }
        this.setBounds(xOffSet + BoardCoordinate.GRID_CENTER_OFFSET[generalIndex][0] - BoardCoordinate.GRID_SIZE / 2,
                yOffSet + BoardCoordinate.GRID_CENTER_OFFSET[generalIndex][1] - BoardCoordinate.GRID_SIZE / 2,
                BoardCoordinate.GRID_SIZE, BoardCoordinate.GRID_SIZE);
        if (aeroplane.indexOfTeam != -1) {
            for (Aeroplane a : chessboard.getPartners(aeroplane.indexOfTeam)) {
                a.getPlaneView().setBounds(xOffSet + BoardCoordinate.GRID_CENTER_OFFSET[generalIndex][0] - BoardCoordinate.GRID_SIZE / 2,
                        yOffSet + BoardCoordinate.GRID_CENTER_OFFSET[generalIndex][1] - BoardCoordinate.GRID_SIZE / 2,
                        BoardCoordinate.GRID_SIZE, BoardCoordinate.GRID_SIZE);
                a.getPlaneView().setIconAsPlaneNum(chessboard.selfPlaneNumOnIndex(generalIndex));
                a.setGeneralGridIndex(generalIndex);
                a.setSelfPathIndex(a.getSelfPathIndexFromGeneralIndex(generalIndex));
            }
        }
        System.out.println("moving " + this.number + " to general index " + generalIndex);
    }

    public void readyToBeSelected() {
        this.addMouseListener(ableToMoveTipListener);
        this.setEnabled(true);
    }

    public void finish() {
        if (aeroplane.indexOfTeam != -1) chessboard.teamIndexUsed[color][aeroplane.indexOfTeam] = false;
        aeroplane.indexOfTeam = -1;
        moveTo(itsHangar);
        StringBuilder themeSelectedIconPath = new StringBuilder();
        themeSelectedIconPath.append(SystemSelect.getImagePath());

        themeSelectedIconPath.append("pl_fin_");
        switch (color) {
            case PlaneState.BLUE:
                themeSelectedIconPath.append("bl.png");
                break;
            case PlaneState.GREEN:
                themeSelectedIconPath.append("gr.png");
                break;
            case PlaneState.RED:
                themeSelectedIconPath.append("re.png");
                break;
            case PlaneState.YELLOW:
                themeSelectedIconPath.append("ye.png");
                break;
        }
        setIcon(new ImageIcon(themeSelectedIconPath.toString()));
        this.state = PlaneState.FINISH;
        this.setEnabled(false);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void setEnabled(boolean flag) {
        if (!flag) {
            for (MouseListener m : this.getMouseListeners())
                this.removeMouseListener(m);
        }
    }
}
