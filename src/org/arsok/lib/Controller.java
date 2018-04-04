package org.arsok.lib;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Controller {
    protected Stage stage;
    private Scene scene;

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
