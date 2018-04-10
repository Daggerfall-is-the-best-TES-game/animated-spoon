package org.arsok.app;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.arsok.lib.Alert;
import org.arsok.lib.FXMLBundle;
import org.arsok.lib.FXMLBundleFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Main extends Application {
    public static Main instance;

    private final URL displayURL = getClass().getResource("/Display.fxml");
    private final URL alertURL = getClass().getResource("/Alert.fxml");

    private final ExecutorService service = Executors.newCachedThreadPool();
    private final Logger logger = Logger.getLogger("Application");
    private final Path propertiesPath = Paths.get(".\\.properties");
    private final Properties properties = new Properties();
    private final SaveHandler handler = new SaveHandler();

    private boolean exceptionLogged = false;

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
            exceptionLogged = true;

            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            builder.append(writer.toString());
        }

        logger.log(level, builder.toString());
        levelSevere(level, message, e, builder.toString());
    }

    private void levelSevere(Level level, String message, Exception e, String saveContent) {
        if (level.equals(Level.SEVERE)) {
            try {
                FXMLBundle<Parent, Alert> parentObjectFXMLBundle = FXMLBundleFactory.newFXMLBundle(alertURL, new Stage());
                Alert alert = parentObjectFXMLBundle.getController();
                if (message != null) {
                    alert.setMessage(message);
                }

                if (e != null) {
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));
                    alert.setSubMesage(writer.toString());
                }

                alert.setLocation1(null);
                alert.setLocation2(null);
                alert.setOption1("OK", url -> alert.getStage().close());
                alert.setOption2("Save", url -> {
                    Path directory = Paths.get(".\\logs\\alerts");
                    try {
                        Files.createDirectories(directory);
                    } catch (IOException e1) {
                        log(Level.WARNING, "Failed to create directories", e1);
                    }

                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
                    Date now = new Date();
                    String strDate = sdfDate.format(now);

                    Path toSave = null;
                    try {
                        Files.createFile(toSave = directory.resolve(strDate + ".alert"));
                    } catch (FileAlreadyExistsException e1) {
                        int counter = 0;
                        boolean valid;
                        try {
                            do {
                                try {
                                    Files.createFile(toSave = directory.resolve(strDate + counter + ".alert"));
                                    valid = true;
                                } catch (FileAlreadyExistsException e2) {
                                    valid = false;
                                }
                            } while (!valid);
                        } catch (IOException e2) {
                            log(Level.WARNING, "Failed to save alert", e2);
                        }
                    } catch (IOException e1) {
                        log(Level.WARNING, "Failed to save alert", e1);
                    }

                    try {
                        PrintWriter writer = new PrintWriter(Files.newBufferedWriter(toSave));
                        writer.println(strDate);
                        writer.println();

                        System.getProperties().forEach((o, o2) -> writer.println("[" + o.toString() + " - " + o2.toString() + "]"));

                        writer.println();
                        writer.println();
                        writer.println(saveContent);

                        writer.flush();
                        writer.close();
                    } catch (IOException e1) {
                        log(Level.WARNING, "Failed to write alert", e1);
                    }
                });
            } catch (IOException e1) {
                log(Level.WARNING, "Unable to load alert", e1);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;

        logger.setLevel(Level.ALL);
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);

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
            FXMLBundleFactory.newFXMLBundle(displayURL, primaryStage);
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

        if (exceptionLogged) {
            Path directory = Paths.get(".\\logs\\");
            try {
                Files.createDirectories(directory);
            } catch (IOException e1) {
                log(Level.WARNING, "Failed to create directories", e1);
            }

            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
            Date now = new Date();
            String strDate = sdfDate.format(now);

            Path toSave = null;
            try {
                Files.createFile(toSave = directory.resolve(strDate + ".alert"));
            } catch (FileAlreadyExistsException e1) {
                int counter = 0;
                boolean valid = false;
                do {
                    try {
                        Files.createFile(toSave = directory.resolve(strDate + counter + ".alert"));
                        valid = true;
                    } catch (FileAlreadyExistsException e2) {
                        valid = false;
                    } catch (IOException e2) {
                        log(Level.WARNING, "Failed to save log", e2);
                    }
                } while (!valid);
            } catch (IOException e1) {
                log(Level.WARNING, "Failed to save log", e1);
            }

            try {
                PrintWriter writer = new PrintWriter(Files.newBufferedWriter(toSave));
                writer.println(strDate);
                writer.println();

                System.getProperties().forEach((o, o2) -> writer.println("[" + o.toString() + " - " + o2.toString() + "]"));

                writer.println();
                writer.println();

                List<LogRecord> records = handler.getHandlers();
                for (LogRecord record : records) {
                    writer.println("-------------");

                    writer.println(strDate);
                    writer.println("[" + record.getLevel() + "]: " + record.getMessage());
                    writer.println(record.getLoggerName());
                    writer.println(record.getMillis());
                    writer.println();

                    writer.println(Arrays.toString(record.getParameters()));
                    writer.println(record.getThreadID());

                    writer.println();

                    writer.println(record.getSequenceNumber());
                    writer.println(record.getSourceClassName());
                    writer.println(record.getSourceMethodName());

                    writer.println();
                    writer.println("-------------");
                }

                writer.flush();
                writer.close();
            } catch (IOException e1) {
                log(Level.WARNING, "Failed to write alert", e1);
            }
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

    private class SaveHandler extends Handler {
        private List<LogRecord> handlers = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            handlers.add(record);
        }

        public List<LogRecord> getHandlers() {
            return handlers;
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    }
}
