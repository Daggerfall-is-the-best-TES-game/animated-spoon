package org.arsok.app;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
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
    private ScrollPane writableImageScrollPane;

    @FXML
    private AnchorPane pane3d;

    @FXML
    private ImageView writableImageView;
    private BlackHole blackHole;

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
        rayTrace.bindBlackHole(blackHole);
        rayTrace.start();

        writableImageView.setImage(rayTrace.getImage());
        writableImageView.fitWidthProperty().bind(pane3d.widthProperty());
        writableImageView.fitHeightProperty().bind(pane3d.heightProperty());

        writableImageScrollPane.setVvalue(0.5);
        writableImageScrollPane.setHvalue(0.5);
    }

    @Override
    public void inject() {
        stage.widthProperty().addListener(new CenterImageListener());
        stage.heightProperty().addListener(new CenterImageListener());
    }

    private class CenterImageListener implements ChangeListener<Number> {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            writableImageScrollPane.setVvalue(0.5);
            writableImageScrollPane.setHvalue(0.5);
        }
    }
}
