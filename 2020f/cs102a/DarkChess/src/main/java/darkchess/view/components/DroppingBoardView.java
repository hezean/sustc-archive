package darkchess.view.components;

import darkchess.control.GUIRefresher;
import darkchess.model.Chess;
import darkchess.model.Chessboard;
import darkchess.model.Config;
import darkchess.model.utils.ChessPiece;
import darkchess.model.utils.ChessState;
import darkchess.model.utils.Constants;
import darkchess.model.utils.Position;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class DroppingBoardView extends GridPane {

    @Getter
    private Chessboard chessboard;

    @Getter
    @ToString.Exclude
    private ChessWrapperView[][] wrappers;

    public DroppingBoardView() {
        GUIRefresher.setCurrentDroppingBoard(this);

        setPadding(new Insets(10, 7, 10, 7));
        setHgap(5);
        setVgap(5);
        setStyle("-fx-background-color: " + Config.INSTANCE.getTheme().getChessboardGapColor());

        for (int i = 0; i < Constants.BOARD_COLS; i++) {
            ColumnConstraints ccs = new ColumnConstraints();
            ccs.setPercentWidth(100. / Constants.BOARD_COLS);
            ccs.setHgrow(Priority.ALWAYS);
            ccs.setFillWidth(true);
            getColumnConstraints().add(ccs);
        }

        for (int i = 0; i < Constants.BOARD_ROWS; i++) {
            RowConstraints rcs = new RowConstraints();
            rcs.setPercentHeight(100. / Constants.BOARD_ROWS);
            rcs.setVgrow(Priority.ALWAYS);
            rcs.setFillHeight(true);
            getRowConstraints().add(rcs);
        }

        new GUIRefresher() {
            @Override
            public void refresh() {
                fillBoard();
            }
        }.register();
    }

    public void bind(Chessboard chessboard) {
        this.chessboard = chessboard;
        GUIRefresher.refreshAll();
    }

    public DroppingBoardView binding(Chessboard chessboard) {
        bind(chessboard);
        return this;
    }

    @Nullable
    public Position selectedChess() {
        for (int r = 0; r < Constants.BOARD_ROWS; r++) {
            for (int c = 0; c < Constants.BOARD_COLS; c++) {
                var chess = chessboard.getChessboard()[r][c];
                if (ChessView.of(chess).isSelecting()) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    private void fillBoard() {
        getChildren().removeAll();
        wrappers = new ChessWrapperView[Constants.BOARD_ROWS][Constants.BOARD_COLS];
        for (int r = 0; r < Constants.BOARD_ROWS; r++) {
            for (int c = 0; c < Constants.BOARD_COLS; c++) {
                ChessWrapperView wrapper = new ChessWrapperView(new Position(r, c), chessboard);
                wrapper.setChessView(
                        ChessView.of(chessboard.getChessboard()[r][c])
                                .fitting(wrapper)
                                .binding(chessboard)
                );
                wrappers[r][c] = wrapper;
                add(wrapper, c, r);
            }
        }
        promptMovables();
    }

    private Stream<ChessWrapperView> wrappers() {
        return Arrays.stream(wrappers)
                .flatMap(Arrays::stream);
    }

    public void promptMovables() {
        wrappers().forEach(w -> w.promptSelectable(false));
        Position selPos = selectedChess();
        if (Objects.isNull(selPos)) {
            return;
        }
        Chess sel = chessboard.getChessboard()[selPos.row()][selPos.col()];
        if (!ChessPiece.CANNON.equals(sel.getPiece())) {
            selPos.surroundings(1)
                    .filter(sp -> {
                        var sch = chessboard.getChessboard()[sp.row()][sp.col()];
                        if (Objects.isNull(sch)) {
                            return true;
                        }
                        return sch.getState() != ChessState.FACE_DOWN
                                && sch.getColor() != sel.getColor()
                                && sel.getPiece().canCapture(sch.getPiece());
                    })
                    .forEach(sp -> wrappers[sp.row()][sp.col()].promptSelectable(true));
        } else {
            selPos.surroundings(2)
                    .filter(sp -> {
                        var mid = new Position((sp.row() + selPos.row()) / 2, (sp.col() + selPos.col()) / 2);
                        if (chessboard.getChessboard()[mid.row()][mid.col()] == null) {
                            return false;
                        }
                        var sch = chessboard.getChessboard()[sp.row()][sp.col()];
                        return sch != null
                                && (sch.getState() == ChessState.FACE_DOWN || sch.getColor() != sel.getColor());
                    })
                    .forEach(sp -> wrappers[sp.row()][sp.col()].promptSelectable(true));
        }
    }
}
