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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    public static Main instance;
    private final URL displayURL = getClass().getResource("/Display.fxml");
    private final ExecutorService service = Executors.newCachedThreadPool();
    private final Logger logger = Logger.getLogger("Application");
    private final Path propertiesPath = Paths.get(".\\.properties");
    private final Properties properties = new Properties();

    public static void main(String[] args) {
        launch(args);
    }

    public ExecutorService getService() {
        return service;
    }

    public Properties getProperties() {
        return properties;
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

        //TODO: if level is severe, display alert
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;

        log(Level.INFO, "Starting application", null);

        loadProperties();
        loadDisplay(primaryStage);
    }

    private void loadProperties() {
        try {
            if (Files.exists(propertiesPath)) {
                properties.load(Files.newInputStream(propertiesPath));
            } else {
                //TODO: load default properties
            }
        } catch (IOException e) {
            log(Level.WARNING, "Failed to load properties", e);
        }
    }

    private void loadDisplay(Stage primaryStage) {
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

        try {
            if (!Files.exists(propertiesPath)) {
                Files.createFile(propertiesPath);
            }
            properties.store(Files.newOutputStream(propertiesPath), null);
        } catch (IOException e) {
            log(Level.WARNING, "Failed to store properties", e);
        }

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
