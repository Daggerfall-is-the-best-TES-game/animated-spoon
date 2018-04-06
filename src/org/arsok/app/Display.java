package org.arsok.app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.arsok.lib.Controller;
import org.arsok.lib.FXMLBundleFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import static org.arsok.app.Main.instance;

public class Display extends Controller implements Initializable {
    private final URL propertiesURL = getClass().getResource("/PropertiesDisplay.fxml");

    @FXML
    private AnchorPane pane3d;

    @FXML
    private ImageView writableImageView;

    @FXML
    public void openProperties() {
        try {
            FXMLBundleFactory.newFXMLBundle(propertiesURL, new Stage());
        } catch (IOException e) {
            instance.log(Level.SEVERE, "Failed to open properties", e);
        }
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
        writableImageView.fitWidthProperty().bind(pane3d.widthProperty());
        writableImageView.fitHeightProperty().bind(pane3d.heightProperty());
    }
}
