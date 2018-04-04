package org.arsok.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    public static Main instance;
    private final URL displayURL = getClass().getResource("/Display.fxml");
    private final ExecutorService service = Executors.newCachedThreadPool();
    private final Logger logger = Logger.getLogger("Application");

    public static void main(String[] args) {
        launch(args);
    }

    public ExecutorService getService() {
        return service;
    }

    public void log(Level level, String message, Exception e) {
        StringBuilder builder = new StringBuilder();

        if (message != null) {
            builder.append(message);
        }

        if (message != null && e != null) {
            builder.append("\n");
        }

        if (e != null) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            builder.append(writer.toString());
        }

        logger.log(level, builder.toString());
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;

        log(Level.INFO, "Starting application", null);

        try {
            Parent parent = FXMLLoader.load(displayURL);
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            log(Level.SEVERE, "Failed to load display", e);
        }
    }

    @Override
    public void stop() {
        log(Level.INFO, "Stopping application", null);

        service.shutdown();

        if (!service.isShutdown()) {
            log(Level.INFO, "Executor service was not shutdown, firmly shutting down now!", null);

            try {
                Thread.sleep(1000);
                service.shutdownNow();

                log(Level.INFO, "Executor service was shutdown successfully", null);
            } catch (InterruptedException e) {
                System.exit(-1);
            }
        }
    }
}
