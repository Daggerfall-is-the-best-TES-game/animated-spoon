package org.arsok.lib;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.function.Consumer;

public class Alert extends Controller {
    @FXML
    private Text messageText;

    @FXML
    private TextArea messageArea;

    @FXML
    private Button button1;

    @FXML
    private Button button2;

    private Consumer<URL> option1;
    private Consumer<URL> option2;

    private URL location1;
    private URL location2;

    public void setMessage(String message) {
        this.messageText.setText(message);
    }

    public void setSubMesage(String subMessage) {
        this.messageArea.setText(subMessage);
    }

    public URL getLocation1() {
        return location1;
    }

    public void setLocation1(URL location1) {
        this.location1 = location1;
    }

    public URL getLocation2() {
        return location2;
    }

    public void setLocation2(URL location2) {
        this.location2 = location2;
    }

    public void setOption1(String name, Consumer<URL> consumer) {
        button1.setText(name);
        option1 = consumer;
    }

    public void setOption2(String name, Consumer<URL> consumer) {
        button2.setText(name);
        option2 = consumer;
    }

    @FXML
    public void performOption1() {
        option1.accept(location1);
    }

    @FXML
    public void performOption2() {
        option2.accept(location2);
    }
}
