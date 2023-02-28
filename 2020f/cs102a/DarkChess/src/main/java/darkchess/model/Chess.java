package darkchess.model;

import darkchess.model.utils.*;
import darkchess.view.components.ChessView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class Chess {

    private ChessColor color;

    private ChessPiece piece;

    private int identifier;

    @EqualsAndHashCode.Exclude
    private ChessState state;

    public boolean isSelectable(Chessboard ctx) {
        for (int r = 0; r < Constants.BOARD_ROWS; r++) {
            for (int c = 0; c < Constants.BOARD_COLS; c++) {
                if (ChessView.of(ctx.getChessboard()[r][c]).isSelecting()) {
                    return false;
                }
            }
        }
        if (ChessState.FACE_DOWN.equals(state)) {
            return true;
        }
        boolean currentPlayerIsNotFirst = ctx.getStep() % 2 != 0;
        return color.equals(ctx.getFirstPlayerColor()) ^ currentPlayerIsNotFirst;
    }

    public Chess deepClone() {
        return Chess.builder()
                .color(color)
                .piece(piece)
                .identifier(identifier)
                .state(state)
                .build();
    }
}
