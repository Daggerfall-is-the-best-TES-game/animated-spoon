package org.arsok.app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class PropertiesDisplay implements Initializable {
    private final HashMap<String, TextField> fields = new HashMap<>();
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
        } else {
            //close normally
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

                    propertiesPane.add(new Text(key), 0, counter);
                    propertiesPane.add(field, 1, counter);

                    fields.put(key, field);
                }
            });
        }
    }
}
