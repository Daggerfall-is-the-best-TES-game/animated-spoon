package org.arsok.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    public static Main instance;
    private final URL displayURL = getClass().getResource("/Display.fxml");
    private final ExecutorService service = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        launch(args);
    }

    public ExecutorService getService() {
        return service;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;

        Parent parent = FXMLLoader.load(displayURL);
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();

        //TODO: handle exceptions
    }

    @Override
    public void stop(){
        service.shutdown();

        if(!service.isShutdown()){
            try {
                Thread.sleep(1000);
                service.shutdownNow();
            } catch (InterruptedException e) {
                System.exit(-1);
            }
        }
    }
}
