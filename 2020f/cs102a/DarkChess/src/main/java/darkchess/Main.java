package darkchess;

import darkchess.control.controllers.GamePageController;
import darkchess.model.Chessboard;
import darkchess.view.components.DroppingBoardView;
import darkchess.view.utils.AdvancedStage;
import darkchess.view.utils.SoundEffects;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        var x = new AdvancedStage<GamePageController>("GamePage")
                .withTitle("DarkChess")
                .fitScreen(0.2, 0.5)
                .fixedRatio(1.2)
                .centering()
                .showed();

        x.getController().getDroppingBoard().bind(Chessboard.newGame("A", "B"));
        SoundEffects.BGM.startInf();
    }
}
