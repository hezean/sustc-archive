package darkchess.control.controllers;

import darkchess.control.GUIRefresher;
import darkchess.control.ResourceRefresher;
import darkchess.model.Chess;
import darkchess.model.Chessboard;
import darkchess.model.Config;
import darkchess.model.utils.ChessColor;
import darkchess.model.utils.ChessPiece;
import darkchess.view.components.DroppingBoardView;
import darkchess.view.utils.ChessIcon;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GamePageController {

    @FXML
    @Getter
    private DroppingBoardView droppingBoard;

    @FXML
    private GridPane content;

    @FXML
    private GridPane blackDownBoard;

    @FXML
    private GridPane redDownBoard;

    @FXML
    private Label currentPlayerText1;

    @FXML
    private Label p1Score;

    @FXML
    private Label p2Score;

    @FXML
    private MenuItem undoMenuItem;

    @FXML
    private MenuItem redoMenuItem;

    @FXML
    private MenuItem saveMenuItem;

    @FXML
    private MenuItem quitMenuItem;

    @FXML
    private MenuItem trickModeMenuItem;

    @FXML
    private MenuItem musicToggle;

    @FXML
    private MenuItem restartMenuItem;

    @FXML
    private MenuItem recoverMenuItem;

    @FXML
    VBox rootBox;

    @FXML
    MenuBar menuBar;

    @FXML
    Button undoBtn;

    @FXML
    Button redoBtn;

    @FXML
    Label stepFlag;


    public void initialize() {
        content.minHeightProperty().bind(rootBox.heightProperty().subtract(menuBar.heightProperty()));

        new ResourceRefresher() {

            @Override
            public void refresh() {
                trickModeMenuItem.setText("Trick Mode: " + (Config.INSTANCE.isTreating() ? "On" : "Off"));
                musicToggle.setText("Music: " + (Config.INSTANCE.isMuted() ? "Off" : "On"));
            }
        }.register();

        new GUIRefresher() {

            @Override
            public void refresh() {
                var canRedo = droppingBoard.getChessboard().canRedo();
                var canUndo = droppingBoard.getChessboard().canUndo();
                undoMenuItem.setDisable(!canUndo);
                undoBtn.setDisable(!canUndo);
                redoMenuItem.setDisable(!canRedo);
                redoBtn.setDisable(!canRedo);
            }
        }.register();

        new GUIRefresher() {

            @Override
            public void refresh() {
                stepFlag.setText("On going #" + (droppingBoard.getChessboard().getStep()) + " step");
                var isP1 = droppingBoard.getChessboard().getStep() % 2 == 0;
                currentPlayerText1.setText("Current Player: " + (isP1 ? droppingBoard.getChessboard().getP1Name() : droppingBoard.getChessboard().getP2Name()));
                p1Score.setText("[P1] " + droppingBoard.getChessboard().getP1Name() + " :  " + droppingBoard.getChessboard().getP1Score() + " pts");
                p2Score.setText("[P2] " + droppingBoard.getChessboard().getP2Name() + " :  " + droppingBoard.getChessboard().getP2Score() + " pts");
                if (droppingBoard.getChessboard().getFirstPlayerColor() == null) {
                    currentPlayerText1.setTextFill(Paint.valueOf("#003cff"));
                    p1Score.setTextFill(Paint.valueOf("#003cff"));
                    p2Score.setTextFill(Paint.valueOf("#003cff"));
                } else {
                    var p1Color = droppingBoard.getChessboard().getFirstPlayerColor() == ChessColor.RED ? "#b30000" : "#1a0000";
                    var p2Color = droppingBoard.getChessboard().getFirstPlayerColor() != ChessColor.RED ? "#b30000" : "#1a0000";
                    currentPlayerText1.setTextFill(Paint.valueOf(isP1 ? p1Color : p2Color));
                    p1Score.setTextFill(Paint.valueOf(p1Color));
                    p2Score.setTextFill(Paint.valueOf(p2Color));
                }
            }
        }.register();

        new GUIRefresher() {
            boolean first = true;

            @Override
            public void refresh() {
                redDownBoard.getChildren().removeIf(Group.class::isInstance);
                blackDownBoard.getChildren().removeIf(Group.class::isInstance);
                for (int i = 0; i < ChessPiece.values().length; i++) {
                    if (first) {
                        var rc = new RowConstraints();
                        rc.setMinHeight(10);
                        rc.setPercentHeight(13);
                        rc.setFillHeight(true);
                        rc.setVgrow(Priority.ALWAYS);
                        redDownBoard.getRowConstraints().add(rc);
                    }
                    var gp = downChess(ChessColor.RED, ChessPiece.values()[i], droppingBoard.getChessboard().getDownBoard());
                    redDownBoard.add(gp, 0, i);
                }
                for (int i = 0; i < ChessPiece.values().length; i++) {
                    if (first) {
                        var rc = new RowConstraints();
                        rc.setMinHeight(10);
                        rc.setPercentHeight(13);
                        rc.setFillHeight(true);
                        rc.setVgrow(Priority.ALWAYS);
                        blackDownBoard.getRowConstraints().add(rc);
                    }
                    var gp = downChess(ChessColor.BLACK, ChessPiece.values()[ChessPiece.values().length - 1 - i], droppingBoard.getChessboard().getDownBoard());
                    blackDownBoard.add(gp, 0, i);
                }
                first = false;
            }
        }.register();
    }

    private Group downChess(ChessColor color, ChessPiece piece, List<Chess> downs) {
        int cnt = (int) downs.stream()
                .filter(c -> c.getColor() == color)
                .filter(c -> c.getPiece() == piece)
                .count();
        Image img = ChessIcon.mixed(color, piece, ((double) cnt) / piece.getNumber());
        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        iv.setFitHeight(50);
        iv.setImage(img);
        if (color == ChessColor.RED) {
            iv.setRotate(180);
        }
        Label txt = new Label("x " + cnt);
        txt.setTextFill(Paint.valueOf("#e60000"));
        txt.setLayoutX((iv.getBoundsInLocal().getMaxX() - txt.getWidth()) * 0.95);
        txt.setLayoutY(2);
        Group gp = new Group(iv);
        gp.getChildren().add(txt);
        return gp;
    }

    public void doRestart(ActionEvent actionEvent) {
        Chessboard res = droppingBoard.getChessboard();
        droppingBoard.bind(Chessboard.newGame(res.getP1Name(), res.getP2Name()));
    }

    public void doRecover(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select a game record to load");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("DarkChess records", "*.darkchess"));

        File file = fc.showOpenDialog(droppingBoard.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            droppingBoard.bind(Chessboard.recoverFrom(file));
            var stCol = droppingBoard.getChessboard().getFirstPlayerColor();
            new Thread(() -> {
                for (var sp : droppingBoard.getChessboard().getHistory()) {
                    Platform.runLater(() -> {
                        droppingBoard.getChessboard().recoverFrom(sp);
                        droppingBoard.getChessboard().setFirstPlayerColor(stCol);
                        GUIRefresher.refreshAll();
                    });

                    try {
                        Thread.sleep(350);
                    } catch (InterruptedException e) {
                    }
                }
            }).start();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to open the record");
            alert.setContentText("An error occurred, please try again: " + e.getMessage());
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid game record");
            alert.setContentText("Could not load this record: " + e.getMessage());
            alert.show();
        }
    }

    public void doUndo(ActionEvent actionEvent) {
        droppingBoard.getChessboard().undo();
        GUIRefresher.refreshAll();
    }

    public void doRedo(ActionEvent actionEvent) {
        droppingBoard.getChessboard().redo();
        GUIRefresher.refreshAll();
    }

    public void doSave(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Create or override a file");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("DarkChess records", "*.darkchess"));

        File file = fc.showSaveDialog(getDroppingBoard().getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            droppingBoard.getChessboard().saveTo(file);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to save the record");
            alert.setContentText("An error occurred, please try again: " + e.getMessage());
            alert.show();
        }
    }

    public void doQuitGame(ActionEvent actionEvent) {
    }

    public void onToggleTrickMode(ActionEvent actionEvent) {
        Config.INSTANCE.setTreating(!Config.INSTANCE.isTreating());
    }

    public void doChangeMusicToggle(ActionEvent actionEvent) {
        Config.INSTANCE.setMuted(!Config.INSTANCE.isMuted());
    }
}
