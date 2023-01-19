package cs102a.aeroplane.gamemall;

import cs102a.aeroplane.model.Aeroplane;
import cs102a.aeroplane.model.ChessBoard;
import cs102a.aeroplane.presets.PlaneState;

public class GoodsList {

    // 对应炸弹
    public static Goods bomb = new Goods(99, "炸弹", "单次使用，让场上其他方飞机回机场") {
        @Override
        public void use(ChessBoard chessBoard) {
            if (isAbleToUse(chessBoard)) {
                for (Aeroplane p : chessBoard.getPlanes()) {
                    if (p.getColor() != chessBoard.getNowPlayer()) {
                        if (p.getState() != PlaneState.FINISH && p.getState() == PlaneState.ON_BOARD) {
                            p.backToHangarDueToCrash();
                        }
                    }
                }
                setStoreCnt(getStoreCnt()[chessBoard.getNowPlayer()] - 1, chessBoard.getNowPlayer());
            } else unableToUse();
        }
    };

    // 对应波音
    public static Goods takeOffAnyway = new Goods(39, "芜湖起飞", "自己方任选一架当前在机场的飞机获得一次无条件起飞的资格") {
        @Override
        public void use(ChessBoard chessBoard) {
            if (isAbleToUse(chessBoard)) {
                for (Aeroplane p : chessBoard.getPlanes()) {
                    if (p.getColor() == chessBoard.getNowPlayer()) {
                        if (p.getState() == PlaneState.IN_HANGAR) {
                            p.setState(PlaneState.ON_BOARD);
                            p.getPlaneView().readyToBeSelected();
                        }
                    }
                }
                setStoreCnt(getStoreCnt()[chessBoard.getNowPlayer()] - 1, chessBoard.getNowPlayer());
            } else unableToUse();
        }
    };

    // 对应VIP
    public static Goods makeMeWin = new Goods(499, "VIP", "无论自己多菜，游戏结束后排行榜上一定是第一") {
        @Override
        public void use(ChessBoard chessBoard) {
            if (isAbleToUse(chessBoard)) {
                chessBoard.setWinner1Index(chessBoard.getNowPlayer());
                setStoreCnt(getStoreCnt()[chessBoard.getNowPlayer()] - 1, chessBoard.getNowPlayer());
            } else unableToUse();
        }
    };
}
