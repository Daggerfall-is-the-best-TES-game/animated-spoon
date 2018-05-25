package org.arsok.app;

import com.meti.lib.asset.AssetManager;
import com.meti.lib.fx.FXBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static com.meti.lib.Environment.getMainInstance;

public class Display extends Controller implements Initializable {
    private final URL propertiesURL = getClass().getResource("/SettingsDisplay.fxml");

    @FXML
    private ScrollPane writableImageScrollPane;

    @FXML
    private AnchorPane pane3d;

    @FXML
    private ImageView writableImageView;
    private BlackHole blackHole = new BlackHole(1e31);

    @FXML
    public void openProperties() {
        FXBundle<SettingsDisplay> bundle = AssetManager.<FXBundle<SettingsDisplay>>firstNameContains("SettingsDisplay.fxml").getContent();

        getMainInstance().getStages().get(0).setScene(new Scene(bundle.getParent()));
        getMainInstance().getStages().get(0).show();
    }

    @FXML
    public void exit() {
        Platform.exit();
    }

    @FXML
    public void openWiki() {
        getMainInstance().getHostServices().showDocument("https://github.com/Arsok/animated-spoon/wiki");
    }

    @FXML
    public void openError() {
        getMainInstance().getHostServices().showDocument("https://github.com/Arsok/animated-spoon/issues/new");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RayTrace rayTrace = new RayTrace();
        rayTrace.bindBlackHole(blackHole);
        rayTrace.start();

        ((Main) getMainInstance()).getSettings().getSetting("backgroundImage").valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                rayTrace.setBackgroundImage(new Image(Files.newInputStream(Paths.get(newValue))));
            } catch (IOException e) {
                //TODO: handle e
                e.printStackTrace();
            }
        });

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
