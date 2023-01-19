package cs102a.aeroplane.savegame;

import cs102a.aeroplane.model.ChessBoard;
import cs102a.aeroplane.presets.BoardCoordinate;
import cs102a.aeroplane.util.SystemSelect;

import java.io.*;
import java.util.Objects;

public class GameSaver {

    /**
     * @description: 记录按下保存时的棋局，每次点击保存会把当前棋局记录到新文件，编号连续
     * @future: 将不同玩家的存档分文件夹存放
     */
    public static void save(ChessBoard chessBoard) {
        BufferedWriter bufferedWriter = null;

        String fileDict = SystemSelect.getHistoryPath();
        String filePath = String.format("%s%d.aeroplane", fileDict,
                Objects.requireNonNull(new File(fileDict).listFiles()).length + 1);

        // @@ 开始读
        // 验证下面信息是否与预置一样，否则抛出错误（无效存档）
        String gameInfo = String.format(
                "@@\n" +
                        "@PLAYER_LENGTH=%d,%d,%d,%d\n" +
                        "@LANDING=96\n" +
                        "@PLAYER=4\n" +
                        "@DICE_NUM=2\n",
                BoardCoordinate.PATH_LENGTH,
                BoardCoordinate.PATH_LENGTH,
                BoardCoordinate.PATH_LENGTH,
                BoardCoordinate.PATH_LENGTH);

        // @@ 开始读
        // 飞机编号 P 位置0-97
        // 完成 -1
        String stepInfo = String.format(
                "@@\n" +
                        "@NOW_PLAYER=%d\n" +
                        "@B=%d\n" +
                        "@G=%d\n" +
                        "@R=%d\n" +
                        "@Y=%d\n" +
                        "@W1=%d\n" +
                        "@W2=%d\n" +
                        "@@\n" +
                        "1P%d\n" +
                        "2P%d\n" +
                        "3P%d\n" +
                        "4P%d\n" +
                        "5P%d\n" +
                        "6P%d\n" +
                        "7P%d\n" +
                        "8P%d\n" +
                        "9P%d\n" +
                        "10P%d\n" +
                        "11P%d\n" +
                        "12P%d\n" +
                        "13P%d\n" +
                        "14P%d\n" +
                        "15P%d\n" +
                        "16P%d",
                chessBoard.getNowPlayer(),
                chessBoard.getPlayerSteps()[0],
                chessBoard.getPlayerSteps()[1],
                chessBoard.getPlayerSteps()[2],
                chessBoard.getPlayerSteps()[3],
                chessBoard.getWinner1Index(),
                chessBoard.getWinner2Index(),
                chessBoard.getPlanes()[0].getGeneralGridIndex(),
                chessBoard.getPlanes()[1].getGeneralGridIndex(),
                chessBoard.getPlanes()[2].getGeneralGridIndex(),
                chessBoard.getPlanes()[3].getGeneralGridIndex(),
                chessBoard.getPlanes()[4].getGeneralGridIndex(),
                chessBoard.getPlanes()[5].getGeneralGridIndex(),
                chessBoard.getPlanes()[6].getGeneralGridIndex(),
                chessBoard.getPlanes()[7].getGeneralGridIndex(),
                chessBoard.getPlanes()[8].getGeneralGridIndex(),
                chessBoard.getPlanes()[9].getGeneralGridIndex(),
                chessBoard.getPlanes()[10].getGeneralGridIndex(),
                chessBoard.getPlanes()[11].getGeneralGridIndex(),
                chessBoard.getPlanes()[12].getGeneralGridIndex(),
                chessBoard.getPlanes()[13].getGeneralGridIndex(),
                chessBoard.getPlanes()[14].getGeneralGridIndex(),
                chessBoard.getPlanes()[15].getGeneralGridIndex()
        );

        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath, true)));      // FileOutputStream 第二个参数：追加写入/覆写
            bufferedWriter.write(gameInfo);
            bufferedWriter.write(stepInfo);
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert bufferedWriter != null;
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
