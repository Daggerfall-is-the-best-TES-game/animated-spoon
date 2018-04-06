package org.arsok.lib;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FXMLBundleFactory {
    private FXMLBundleFactory() {
    }

    public static <P extends Parent, C> FXMLBundle<P, C> newFXMLBundle(URL location, Stage stage) throws IOException {
        FXMLBundle<P, C> bundle = newFXMLBundle(location);

        Scene scene = new Scene(bundle.getParent());
        stage.setScene(scene);
        stage.show();

        if (bundle.getController() instanceof Controller) {
            Controller controller = (Controller) bundle.getController();

            controller.setScene(scene);
            controller.setStage(stage);
            controller.inject();
        }

        return bundle;
    }

    public static <P extends Parent, C> FXMLBundle<P, C> newFXMLBundle(URL location) throws IOException {
        FXMLLoader loader = new FXMLLoader(location);
        P parent = loader.load();
        C controller = loader.getController();
        return new FXMLBundle<>(location, parent, controller);
    }
}
