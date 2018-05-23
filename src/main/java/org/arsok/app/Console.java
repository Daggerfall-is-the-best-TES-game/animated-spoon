package org.arsok.app;

import javafx.scene.Parent;
import javafx.stage.Stage;
import org.arsok.lib.Alert;
import org.arsok.lib.FXMLBundle;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.arsok.lib.FXMLBundleFactory.newFXMLBundle;

public class Console {
    private final URL alertURL = getClass().getResource("/Alert.fxml");
    private final Logger logger = Logger.getLogger("Application");
    private boolean exceptionLogged = false;


    public Console() {
    }

    public boolean isExceptionLogged() {
        return exceptionLogged;
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

            builder.append(exceptionToString(e));
        }

        logger.log(level, builder.toString());
        levelSevere(level, message, e, builder.toString());
    }

    public Logger getLogger() {
        return logger;
    }

    private void levelSevere(Level level, String message, Exception e, String saveContent) {
        if (level.equals(Level.SEVERE)) {
            try {
                FXMLBundle<Parent, Alert> parentObjectFXMLBundle = newFXMLBundle(alertURL, new Stage());
                Alert alert = parentObjectFXMLBundle.getController();
                if (message != null) {
                    alert.setMessage(message);
                }

                if (e != null) {
                    alert.setSubMesage(exceptionToString(e));
                }

                buildOptions(saveContent, alert);
            } catch (IOException e1) {
                log(Level.WARNING, "Unable to load alert", e1);
            }
        } else {
            throw new IllegalStateException("Invalid level: " + level.getName());
        }
    }

    private void buildOptions(String saveContent, Alert alert) {
        alert.setLocation1(null);
        alert.setLocation2(null);
        alert.setOption1("OK", url -> alert.getStage().close());
        alert.setOption2("Save", url -> saveLog(saveContent));
    }

    private void saveLog(String saveContent) {
        Path directory = getDirectory();
        String strDate = getDate();
        Path toSave = getPath(directory, strDate);
        writeLog(saveContent, strDate, toSave);
    }

    private Path getDirectory() {
        Path directory = Paths.get(".\\logs\\alerts");
        try {
            Files.createDirectories(directory);
        } catch (IOException e1) {
            log(Level.WARNING, "Failed to create directories", e1);
        }
        return directory;
    }

    private Path getPath(Path directory, String strDate) {
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
        return toSave;
    }

    private String getDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
        Date now = new Date();
        return sdfDate.format(now);
    }

    private void writeLog(String saveContent, String strDate, Path toSave) {
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
    }

    private String exceptionToString(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}