package org.arsok.app;

import com.meti.lib.Environment;
import com.meti.lib.asset.AssetManager;
import com.meti.lib.asset.builder.FXBuilder;
import com.meti.lib.fx.FXBundle;
import javafx.scene.Scene;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static com.meti.lib.asset.AssetManager.addAssetBuilder;
import static com.meti.lib.asset.AssetManager.load;

public class Main extends Environment {
    private final Path propertiesPath = Paths.get(".\\.properties");
    private final Settings settings = new Settings();
    private final SaveHandler handler = new SaveHandler();

    public static void main(String[] args) {
        launch(args);
    }

    public Settings getSettings() {
        return settings;
    }

    @Override
    public void stop() {
        getConsole().log(Level.INFO, "Stopping application", null);

        saveProperties();
        saveLog();
    }

    @Override
    public void stopApp() {
        super.stopApp();
    }

    @Override
    public void startApp() {
        getConsole().log(Level.INFO, "Starting application", null);

        defaultBuilders();

        loadProperties();
        loadDisplay();
    }

    private void defaultBuilders() {
        addAssetBuilder(new FXBuilder());
    }

    private void loadDisplay() {
        load(getClass().getResource("/Display.fxml"));
        load(getClass().getResource("/Settings.fxml"));
        load(getClass().getResource("/Alert.fxml"));
        FXBundle<Display> bundle = AssetManager.<FXBundle<Display>>firstNameContains("Display.fxml").getContent();

        getStages().get(0).setScene(new Scene(bundle.getParent()));
        getStages().get(0).show();
    }

    private void loadProperties() {
        try {
            if (Files.exists(propertiesPath)) {
                settings.load(Files.newInputStream(propertiesPath));
            }

            settings.setValueIfNotContains("backgroundImage", "/mwpan2_watermarked.jpg");
        } catch (IOException e) {
            getConsole().log(Level.WARNING, "Failed to load properties", e);
        }
    }

    private void saveLog() {
        Path directory = getLoggingDirectory();
        String strDate = getDate();
        Path toSave = getPath(directory, strDate);

        try {
            writeLog(strDate, toSave);
        } catch (IOException e1) {
            getConsole().log(Level.WARNING, "Failed to write alert", e1);
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
                    getConsole().log(Level.WARNING, "Failed to save getConsole().log", e2);
                }
            } while (!valid);
        } catch (IOException e1) {
            getConsole().log(Level.WARNING, "Failed to save getConsole().log", e1);
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
            getConsole().log(Level.WARNING, "Failed to create directories", e1);
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

    private void saveProperties() {
        try {
            if (!Files.exists(propertiesPath)) {
                Files.createFile(propertiesPath);
            }

            settings.store(Files.newOutputStream(propertiesPath));
        } catch (IOException e) {
            getConsole().log(Level.WARNING, "Failed to store properties", e);
        }
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
