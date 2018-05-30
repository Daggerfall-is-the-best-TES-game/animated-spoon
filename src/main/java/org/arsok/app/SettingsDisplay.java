package org.arsok.app;

import com.meti.lib.asset.AssetManager;
import com.meti.lib.fx.FXBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static com.meti.lib.Environment.getMainInstance;
import static javafx.scene.layout.GridPane.setHalignment;
import static javafx.scene.layout.GridPane.setValignment;

public class SettingsDisplay extends Controller implements Initializable {
    private final HashMap<String, TextField> fields = new HashMap<>();

    @FXML
    private GridPane propertiesPane;
    private Settings settings;
    private boolean changed = false;

    @FXML
    public void close() {
        /*
        If the user hasn't saved some settings, show an alert box before closing. Otherwise, close naturally.
         */

        if (changed) {
            //display alert
            FXBundle<Alert> parentObjectFXMLBundle = AssetManager.<FXBundle<Alert>>firstNameContains("Alert.fxml").getContent();
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
        } else {
            //close normally
            stage.close();
        }
    }

    @FXML
    public void updateProperties() {
        fields.forEach((s, textField) -> settings.setValue(s, textField.getText()));

        changed = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settings = ((Main) getMainInstance()).getSettings();
        if (settings.size() == 0) {
            Text t1 = new Text("No settings found");
            Text t2 = new Text("No settings found");
            propertiesPane.add(t1, 0, 0);
            propertiesPane.add(t2, 1, 0);

            setHalignment(t1, HPos.CENTER);
            setValignment(t1, VPos.CENTER);
            setHalignment(t2, HPos.CENTER);
            setValignment(t2, VPos.CENTER);

        } else {
            settings.getNames().forEach(new Consumer<Object>() {
                int counter = 0;

                @Override
                public void accept(Object o) {
                    String key = (String) o;
                    String value = settings.getValue(key);

                    Text keyText = new Text(key);
                    TextField field = new TextField(value);
                    field.textProperty()
                            .addListener((observable, oldValue, newValue) -> changed = true);

                    propertiesPane.add(keyText, 0, counter);
                    propertiesPane.add(field, 1, counter);

                    setHalignment(keyText, HPos.CENTER);
                    setValignment(keyText, VPos.CENTER);
                    setHalignment(field, HPos.CENTER);
                    setValignment(field, VPos.CENTER);

                    fields.put(key, field);
                }
            });
        }
    }
}
