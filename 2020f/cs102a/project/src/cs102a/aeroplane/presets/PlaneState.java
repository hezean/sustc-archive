package cs102a.aeroplane.presets;

public class PlaneState {
    public static final int BLUE = 0;
    public static final int GREEN = 1;
    public static final int RED = 2;
    public static final int YELLOW = 3;

    public static final int IN_HANGAR = 0;          // 机场
    public static final int ON_BOARD = 1;           // 棋盘上
    public static final int FINISH = 2;             // 到达终点，回机场，改颜色，不可按
}
