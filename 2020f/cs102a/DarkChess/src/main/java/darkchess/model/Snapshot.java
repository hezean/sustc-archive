package darkchess.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import darkchess.model.utils.ChessState;
import darkchess.model.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

@Data
@NoArgsConstructor
public class Snapshot {

    @ToString.Exclude
    public Chess[][] chessboard = new Chess[8][4];

    @ToString.Exclude
    public List<Chess> downBoard = new ArrayList<>();

    public int p1score;

    public int p2score;

    public int step;

    public Snapshot(Chessboard mod) {
        chessboard = new Chess[mod.getChessboard().length][];
        for (int r = 0; r < Constants.BOARD_ROWS; r++) {
            chessboard[r] = new Chess[Constants.BOARD_COLS];
            for (int c = 0; c < Constants.BOARD_COLS; c++) {
                if (mod.getChessboard()[r][c] != null) {
                    chessboard[r][c] = mod.getChessboard()[r][c].deepClone();
                }
            }
        }
        downBoard = List.copyOf(mod.getDownBoard());
        p1score = mod.getP1Score();
        p2score = mod.getP2Score();
        step = mod.getStep();
    }

//    @Override
//    public String toString() {
//        StringBuilder xx = new StringBuilder();
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 4; j++) {
//                if (chessboard[i][j] ==null){
//                    xx.append("(").append(i).append(",").append(j).append(")=null; ");
//                } else if (chessboard[i][j].getState() == ChessState.FLIPPED) {
//                    xx.append("(").append(i).append(",").append(j).append(")=").append(chessboard[i][j]).append("; ");
//                }
//            }
//        }
//        return "Snapshot{" +
//                "chessboard=" + xx +
//                ", downBoard=" + downBoard +
//                ", p1score=" + p1score +
//                ", p2score=" + p2score +
//                ", step=" + step +
//                '}';
//    }
}
