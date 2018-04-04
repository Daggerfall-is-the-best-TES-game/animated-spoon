package org.arsok.app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.net.URL;
import java.util.ResourceBundle;

import static org.arsok.app.Main.instance;

public class Display implements Initializable {
    @FXML
    private ImageView writableImageView;

    @FXML
    public void openProperties() {
        //TODO: open properties
    }

    @FXML
    public void exit() {
        Platform.exit();
    }

    @FXML
    public void openWiki() {
        instance.getHostServices().showDocument("https://github.com/Arsok/animated-spoon/wiki");
    }

    @FXML
    public void openError() {
        instance.getHostServices().showDocument("https://github.com/Arsok/animated-spoon/issues/new");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RayTrace rayTrace = new RayTrace();
        rayTrace.start();

        writableImageView.setImage(rayTrace.getImage());
    }
}
