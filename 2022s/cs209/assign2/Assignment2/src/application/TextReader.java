package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class TextReader extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("TextReader.fxml"));
        Pane root = fxmlLoader.load();
        TextReaderController controller = fxmlLoader.getController();

        Scene scene = new Scene(root);
        stage.setTitle("Text Reader - 12011323");
        stage.setScene(scene);
        stage.setResizable(false);

        boolean windows = System.getProperty("os.name").toLowerCase().startsWith("win");

        KeyCombination quitProgram = windows ?
                new KeyCodeCombination(KeyCode.F4, KeyCodeCombination.ALT_ANY) :
                new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.META_ANY);
        KeyCombination openFile = windows ?
                new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_ANY) :
                new KeyCodeCombination(KeyCode.O, KeyCodeCombination.META_ANY);
        KeyCombination saveFile = windows ?
                new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_ANY) :
                new KeyCodeCombination(KeyCode.S, KeyCodeCombination.META_ANY);
        KeyCombination saveAsFile = windows ?
                KeyCombination.keyCombination("CTRL+SHIFT+S") :
                KeyCombination.keyCombination("META+SHIFT+S");
        KeyCombination closeFile = windows ?
                new KeyCodeCombination(KeyCode.W, KeyCodeCombination.CONTROL_ANY) :
                new KeyCodeCombination(KeyCode.W, KeyCodeCombination.META_ANY);

        KeyCombination find = windows ?
                new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_ANY) :
                new KeyCodeCombination(KeyCode.F, KeyCodeCombination.META_ANY);
        KeyCombination replaceAll = windows ?
                KeyCombination.keyCombination("CTRL+SHIFT+F") :
                KeyCombination.keyCombination("META+SHIFT+F");

        scene.getAccelerators().put(quitProgram, controller::quit);
        scene.getAccelerators().put(openFile, controller::openFile);
        scene.getAccelerators().put(saveFile, controller::saveFile);
        scene.getAccelerators().put(saveAsFile, controller::saveAsFile);
        scene.getAccelerators().put(closeFile, controller::closeFile);
        scene.getAccelerators().put(find, controller::find);
        scene.getAccelerators().put(replaceAll, controller::replaceAll);

        if (!windows) {
            KeyCodeCombination altCloseFile = new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCodeCombination.META_ANY);
            scene.getAccelerators().put(altCloseFile, controller::closeFile);
        }

        stage.show();
    }
}
