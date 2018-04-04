package org.arsok.app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.arsok.lib.Alert;
import org.arsok.lib.Controller;
import org.arsok.lib.FXMLBundle;
import org.arsok.lib.FXMLBundleFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;

public class PropertiesDisplay extends Controller implements Initializable {
    private final HashMap<String, TextField> fields = new HashMap<>();
    private final URL alertURL = getClass().getResource("/Alert.fxml");

    @FXML
    private GridPane propertiesPane;
    private Properties properties;
    private boolean changed = false;

    @FXML
    public void close() {
        //TODO: figure out closing

        /*
        If the user hasn't saved some properties, show an alert box before closing. Otherwise, close naturally.
         */

        if (changed) {
            //display alert
            try {
                FXMLBundle<Parent, Alert> parentObjectFXMLBundle = FXMLBundleFactory.newFXMLBundle(alertURL, new Stage());
                Alert alert = parentObjectFXMLBundle.getController();
                alert.setMessage("Unsaved settings!");
                alert.setSubMesage("Do you want to save your settings?");
                alert.setLocation1(null);
                alert.setLocation2(null);
                alert.setOption1("No", url -> {
                    alert.getStage().close();
                    stage.close();
                });
                alert.setOption2("Yes", url -> alert.getStage().close());
            } catch (IOException e) {
                Main.instance.log(Level.WARNING, "Failed to load alert", e);
            }
        } else {
            //close normally
            stage.close();
        }
    }

    @FXML
    public void updateProperties() {
        fields.forEach((s, textField) -> properties.setProperty(s, textField.getText()));

        changed = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        properties = Main.instance.getProperties();
        if (properties.size() == 0) {
            propertiesPane.add(new Text("No properties found"), 0, 0);
            propertiesPane.add(new Text("No properties found"), 1, 0);
        } else {
            properties.keySet().forEach(new Consumer<Object>() {
                int counter = 0;

                @Override
                public void accept(Object o) {
                    String key = (String) o;
                    String value = properties.getProperty(key);

                    TextField field = new TextField(value);
                    field.textProperty()
                            .addListener((observable, oldValue, newValue) -> changed = true);

                    //TODO: center nodes
                    propertiesPane.add(new Text(key), 0, counter);
                    propertiesPane.add(field, 1, counter);

                    fields.put(key, field);
                }
            });
        }
    }
}
