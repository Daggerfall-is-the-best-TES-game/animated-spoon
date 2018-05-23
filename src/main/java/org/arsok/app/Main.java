package org.arsok.app;

import javafx.application.Application;
import javafx.stage.Stage;
import org.arsok.lib.FXMLBundleFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Main extends Application {
    static Main instance;

    private final URL displayURL = getClass().getResource("/Display.fxml");

    private final ExecutorService service = Executors.newCachedThreadPool();
    private final Path propertiesPath = Paths.get(".\\.properties");
    private final Settings settings = new Settings();
    private final SaveHandler handler = new SaveHandler();
    private final Console console = new Console();


    public static void main(String[] args) {
        launch(args);
    }

    ExecutorService getService() {
        return service;
    }

    Settings getSettings() {
        return settings;
    }


    @Override
    public void start(Stage primaryStage) {
        instance = this;

        console.getLogger().setLevel(Level.ALL);
        handler.setLevel(Level.ALL);
        console.getLogger().addHandler(handler);

        console.log(Level.INFO, "Starting application", null);

        loadProperties();
        loadDisplay(primaryStage);
    }

    private void loadProperties() {
        try {
            if (Files.exists(propertiesPath)) {
                settings.load(Files.newInputStream(propertiesPath));
            } else {
                settings.setValue("backgroundImage", "mwpan2_watermarked.jpg");
            }
        } catch (IOException e) {
            console.log(Level.WARNING, "Failed to load properties", e);
        }
    }

    private void loadDisplay(Stage primaryStage) {
        try {
            FXMLBundleFactory.newFXMLBundle(displayURL, primaryStage);
        } catch (IOException e) {
            console.log(Level.SEVERE, "Failed to load display", e);
        }
    }

    @Override
    public void stop() {
        console.log(Level.INFO, "Stopping application", null);

        saveProperties();
        saveLog();

        shutdownService();
    }

    private void saveLog() {
        if (console.isExceptionLogged()) {
            Path directory = getLoggingDirectory();
            String strDate = getDate();
            Path toSave = getPath(directory, strDate);

            try {
                writeLog(strDate, toSave);
            } catch (IOException e1) {
                console.log(Level.WARNING, "Failed to write alert", e1);
            }
        }
    }

    private Path getPath(Path directory, String strDate) {
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
                } catch (IOException e2) {
                    console.log(Level.WARNING, "Failed to save console.log", e2);
                }
            } while (!valid);
        } catch (IOException e1) {
            console.log(Level.WARNING, "Failed to save console.log", e1);
        }
        return toSave;
    }

    private String getDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
        Date now = new Date();
        return sdfDate.format(now);
    }

    private Path getLoggingDirectory() {
        Path directory = Paths.get(".\\logs\\");
        try {
            Files.createDirectories(directory);
        } catch (IOException e1) {
            console.log(Level.WARNING, "Failed to create directories", e1);
        }
        return directory;
    }

    private void writeLog(String strDate, Path toSave) throws IOException {
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
    }

    private void shutdownService() {
        service.shutdown();

        if (!service.isShutdown()) {
            console.log(Level.INFO, "Executor service was not shutdown, firmly shutting down now!", null);

            try {
                Thread.sleep(1000);
                service.shutdownNow();

                console.log(Level.INFO, "Executor service was shutdown successfully", null);
            } catch (InterruptedException e) {
                System.exit(-1);
            }
        }
    }

    private void saveProperties() {
        try {
            if (!Files.exists(propertiesPath)) {
                Files.createFile(propertiesPath);
            }

            settings.store(Files.newOutputStream(propertiesPath));
        } catch (IOException e) {
            console.log(Level.WARNING, "Failed to store properties", e);
        }
    }

    public Console getConsole() {
        return console;
    }

    private class SaveHandler extends Handler {
        private List<LogRecord> handlers = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            handlers.add(record);
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }

        public List<LogRecord> getHandlers() {
            return handlers;
        }
    }
}
