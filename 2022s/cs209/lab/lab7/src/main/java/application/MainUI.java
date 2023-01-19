package application;

import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MainUI implements Initializable {
    private final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    private static Random rnd;

    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rnd = new Random();
    }

    public void moveWindow(MouseEvent me) {
        Node node = (Node) me.getSource();  // reference to button
        Stage stage = (Stage) node.getScene().getWindow();

        double height = screenBounds.getHeight();
        double width = screenBounds.getWidth();

        // add a fixed value to make sure that the Window moves far enough
        // As x and y represent the upper left corner, we take care of not having part of most of the window outside the screen
        double xMove = width / 10 + rnd.nextDouble() * width / 2;
        double yMove = height / 10 + rnd.nextDouble() * height / 2;

        this.x = (double) ((long) (this.x + xMove) % (long) (width - stage.getWidth()));
        this.y = (double) ((long) (this.y + yMove) % (long) (height - stage.getHeight()));

        stage.setX(this.x);
        stage.setY(this.y);
    }
}