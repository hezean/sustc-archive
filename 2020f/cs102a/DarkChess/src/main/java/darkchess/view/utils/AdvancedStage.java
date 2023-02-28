package darkchess.view.utils;

import darkchess.control.GUIRefresher;
import darkchess.control.Refreshable;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class AdvancedStage<T> extends Stage {

    @Getter
    private final T controller;

    @Getter
    private Refreshable refresher;

    private boolean justifyToCenter;

    @SneakyThrows
    public AdvancedStage(String filename) {
        URL fxml = getClass().getClassLoader().getResource(String.format("pages/%s.fxml", filename));
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(fxml);
        Scene scene = new Scene(fxmlLoader.load());
        super.setScene(scene);
        this.controller = fxmlLoader.getController();
    }

    public AdvancedStage(Scene scene) {
        super.setScene(scene);
        this.controller = null;
    }

    public AdvancedStage<T> withTitle(String title) {
        setTitle(title);
        return this;
    }

    public AdvancedStage<T> useRefresher(Refreshable refresher) {
        this.refresher = refresher;
        return this;
    }

    public AdvancedStage<T> fitScreen(double minWPct, double maxWPct) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        setMinWidth(screenBounds.getWidth() * minWPct);
        setMaxWidth(screenBounds.getWidth() * maxWPct);
        return this;
    }

    public AdvancedStage<T> fixedRatio(double whRatio) {
        minHeightProperty().bind(widthProperty().divide(whRatio));
        maxHeightProperty().bind(widthProperty().divide(whRatio));
        return this;
    }

    public AdvancedStage<T> centering() {
        justifyToCenter = true;
        return this;
    }

    public AdvancedStage<T> showed() {
        show();
        if (justifyToCenter) {
            centerOnScreen();
        }
        return this;
    }

    public AdvancedStage<T> onCloseDoExit() {
        setOnCloseRequest(t -> Platform.exit());
        return this;
    }

    public void refresh() {
        try {
            Objects.requireNonNull(refresher).refresh();
        } catch (NullPointerException e) {
            log.debug("stage is not refreshable");
        } catch (Exception e) {
            log.error("error refreshing stage", e);
        }
    }
}
