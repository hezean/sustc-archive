package darkchess.view.components;

import darkchess.model.Chessboard;
import darkchess.model.Config;
import darkchess.model.utils.Position;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class ChessWrapperView extends Pane {

    // refresher?

    private ChessView chessView;

    private final Position pos;

    private Animation selectablePrompter;

    private boolean isSelectableCache;

    private Chessboard ctx;

    public ChessWrapperView(final Position pos, Chessboard ctx) {
        this.pos = pos;
        this.ctx = ctx;
        setStyle("-fx-background-color: " + Config.INSTANCE.getTheme().getChessboardGridColor());

        Timeline fillBg = new Timeline(
                new KeyFrame(Duration.ZERO, e -> setStyle("-fx-background-color: " + Config.INSTANCE.getTheme().getChessboardGridColor())),
                new KeyFrame(Duration.seconds(0.5), e -> setStyle("-fx-background-color: #d76a49"))
        );
        fillBg.setAutoReverse(true);
        selectablePrompter = fillBg;
        setListeners();
    }

    public void setChessView(ChessView chessView) {
        this.chessView = chessView;
        getChildren().removeAll();
        getChildren().add(chessView);
    }

    public void promptSelectable(boolean isSelectable) {
        isSelectableCache = isSelectable;
        if (isSelectable) {
            selectablePrompter.setCycleCount(Animation.INDEFINITE);
            selectablePrompter.play();
        } else {
            selectablePrompter.stop();
            setStyle("-fx-background-color: " + Config.INSTANCE.getTheme().getChessboardGridColor());
        }
    }

    private void setListeners() {
        setOnMouseClicked(mouseEvent -> {
            if (!isSelectableCache) {
                return;
            }
            System.out.println(pos);
            ctx.doMove(pos);
        });
    }
}
