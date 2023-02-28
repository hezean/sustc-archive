package darkchess.view.components;

import darkchess.control.GUIRefresher;
import darkchess.model.Chess;
import darkchess.model.Chessboard;
import darkchess.model.utils.ChessColor;
import darkchess.model.utils.ChessPiece;
import darkchess.model.utils.ChessState;
import darkchess.model.utils.Constants;
import darkchess.view.utils.ChessIcon;
import darkchess.view.utils.SoundEffects;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ChessView extends ImageView {

    @Getter
    private static final Map<Chess, ChessView> views = new ConcurrentHashMap<>(Constants.BOARD_ROWS * Constants.BOARD_COLS);

    private ChessColor color;

    private ChessPiece piece;

    private int identifier;

    private Chessboard boardCtx;

    private Animation clickPrompter;

    private Animation availablePrompter;

    @Nullable
    public Chess getModel() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                var x = boardCtx.getChessboard()[i][j];
                if (x != null && x.getPiece() == piece && x.getColor() == color && x.getIdentifier() == identifier) {
                    return x;
                }
            }
        }
        return null;
    }

    @Getter
    private boolean isSelecting;

    public void unselect() {
        isSelecting = false;
        setScaleX(1);
        setScaleY(1);
        clickPrompter.stop();
    }

    private ChessView() {
    }

    private ChessView(Chess model) {
        this.color = model.getColor();
        piece = model.getPiece();
        identifier = model.getIdentifier();

        ScaleTransition clickPrompter = new ScaleTransition(Duration.seconds(0.4), this);
        clickPrompter.setFromX(1);
        clickPrompter.setToX(0.9);
        clickPrompter.setFromY(1);
        clickPrompter.setToY(0.8);
        clickPrompter.setAutoReverse(true);
        this.clickPrompter = clickPrompter;

        FadeTransition availablePrompter = new FadeTransition(Duration.seconds(0.3), this);
        availablePrompter.setFromValue(0.6);
        availablePrompter.setToValue(1);
        availablePrompter.setAutoReverse(true);
        this.availablePrompter = availablePrompter;
    }

    public static ChessView of(@Nullable Chess model) {
        if (Objects.isNull(model)) {
            return empty();
        }
        if (!views.containsKey(model)) {
            synchronized (ChessView.class) {
                if (!views.containsKey(model)) {
                    ChessView view = new ChessView(model);
                    view.setVisible(true);
                    view.setFitHeight(200);
                    view.setFitWidth(200);
                    views.put(model, view);
                }
            }
        }
        return views.get(model).refreshed();
    }

    public static ChessView empty() {
        return new ChessView();
    }

    public ChessView fitting(Pane pane) {
        fitWidthProperty().bind(pane.widthProperty());
        fitHeightProperty().bind(pane.heightProperty());
        return this;
    }

    public ChessView refreshed() {
        try {
            setImage(ChessIcon.of(getModel()));
            setListeners();
        } catch (NullPointerException ignored) {
        }
        return this;
    }

    private void setListeners() {
        setOnMouseEntered(mouseEvent -> {
            var mod = getModel();
            if (mod == null || !mod.isSelectable(boardCtx)) {
                setOpacity(1);
                availablePrompter.stop();
            } else {
                availablePrompter.setCycleCount(1);
                availablePrompter.play();
            }
        });

        setOnMouseExited(mouseEvent -> {
            setOpacity(1);
            availablePrompter.stop();
        });

        setOnMouseClicked(mouseEvent -> {
            var mod = getModel();
            if (mod != null && ChessState.FACE_DOWN.equals(mod.getState())) {
                for (int r = 0; r < Constants.BOARD_ROWS; r++) {
                    for (int c = 0; c < Constants.BOARD_COLS; c++) {
                        if (ChessView.of(boardCtx.getChessboard()[r][c]).isSelecting()) {
                            return;
                        }
                    }
                }
                if (Objects.isNull(boardCtx.getFirstPlayerColor())) {
                    boardCtx.setFirstPlayerColor(mod.getColor());
                }
                mod.setState(ChessState.FLIPPED);
                SoundEffects.FLIP.playOnce();
                boardCtx.backupHistoryForOperation();
                GUIRefresher.refreshAll();
                return;
            }
            if (isSelecting) {
                unselect();
            } else if (mod != null && mod.isSelectable(boardCtx)) {
                boardCtx.tryTerminateGame();
                isSelecting = true;
                clickPrompter.setCycleCount(Animation.INDEFINITE);
                clickPrompter.play();
            }
            GUIRefresher.getCurrentDroppingBoard().promptMovables();
        });
    }


    public ChessView binding(Chessboard chessboard) {
        boardCtx = chessboard;
        return this.refreshed();
    }
}
