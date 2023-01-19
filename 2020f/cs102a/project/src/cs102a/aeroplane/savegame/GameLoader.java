package cs102a.aeroplane.savegame;

import cs102a.aeroplane.GameInfo;
import cs102a.aeroplane.frontend.GameGUI;
import cs102a.aeroplane.model.ChessBoard;
import cs102a.aeroplane.util.SystemSelect;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class GameLoader {

    private static final String fileDict = SystemSelect.getHistoryPath();
    private static String fileName = null;     // 传入不带文件后缀名


    /**
     * @throws SecurityException     演示时手动准备错误的文件头
     * @throws Exception             演示时手动准备错误的文件头
     * @throws NumberFormatException 读档异常，数据切分错误
     * @description: 在询问用户选择存档名后读取存档，并清除原来path
     */
    public static void tryToLoad() throws Exception {
        String filePath = String.format("%s%s", fileDict, fileName);
        try {
            Scanner sc = new Scanner(new File(filePath));


            // 检查文件是否存在异常
            while (!sc.nextLine().equals("@@")) {
                System.err.println("Cursor replacing...");//读一行，让光标移动到正确预备位置
            }
            if (!sc.nextLine().equals("@PLAYER_LENGTH=57,57,57,57"))
                throw new SecurityException("想让别人多走几步？坏家伙");
            if (!sc.nextLine().equals("@LANDING=96"))
                throw new SecurityException("着陆点上哪去啦？坏家伙");
            if (!sc.nextLine().equals("@PLAYER=4"))
                throw new SecurityException("说好的四个玩家呢？把人交出来！坏家伙");
            if (!GameInfo.isIsCheatMode()) {
                if (!sc.nextLine().equals("@DICE_NUM=2"))
                    throw new SecurityException("把我的骰子交出来！坏家伙");
            }

            // 读档
            while (!sc.nextLine().equals("@@")) {
                System.err.println("Cursor replacing...");//读一行，让光标移动到正确预备位置
            }

            GameGUI game = new GameGUI();
            try {
                game.setVisible(false);
                ChessBoard chessBoard = game.getChessBoard();

                String[] splitTemp;

                splitTemp = sc.nextLine().split("=");
                chessBoard.setNowPlayer(Integer.parseInt(splitTemp[splitTemp.length - 1]));

                splitTemp = sc.nextLine().split("=");
                chessBoard.getPlayerSteps()[0] = (Integer.parseInt(splitTemp[splitTemp.length - 1]));
                splitTemp = sc.nextLine().split("=");
                chessBoard.getPlayerSteps()[1] = (Integer.parseInt(splitTemp[splitTemp.length - 1]));
                splitTemp = sc.nextLine().split("=");
                chessBoard.getPlayerSteps()[2] = (Integer.parseInt(splitTemp[splitTemp.length - 1]));
                splitTemp = sc.nextLine().split("=");
                chessBoard.getPlayerSteps()[3] = (Integer.parseInt(splitTemp[splitTemp.length - 1]));

                splitTemp = sc.nextLine().split("=");
                chessBoard.setWinner1Index(Integer.parseInt(splitTemp[splitTemp.length - 1]));
                splitTemp = sc.nextLine().split("=");
                chessBoard.setWinner2Index(Integer.parseInt(splitTemp[splitTemp.length - 1]));

                while (!sc.nextLine().equals("@@")) {
                    System.err.println("Cursor replacing...");  //读一行，让光标移动到正确预备位置
                }

                int planeCnt = 0;
                for (int i = 0; i < 16; i++) {
                    splitTemp = sc.nextLine().split("P");
                    try {
                        chessBoard.getPlanes()[i].setGeneralGridIndexAndMove(Integer.parseInt(splitTemp[splitTemp.length - 1]));
                    } catch (AssertionError e) {
                        System.err.print(e.getMessage());
                        break;
                    }
                    planeCnt++;
                }
                if (planeCnt < 16) throw new SecurityException("呀勒？有效飞机个数怎么不对");
                chessBoard.checkStackForInit();
                game.setVisible(true);
            } catch (NumberFormatException e) {
                throw new Exception("先辈，读档失败了 QAQ");
            }

            game.getChessBoard().startGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setFileName(String fileName) {
        GameLoader.fileName = fileName;
    }
}
