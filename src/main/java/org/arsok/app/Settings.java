package org.arsok.app;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class Settings {
    private final HashMap<String, Setting> settings = new HashMap<>();

    public void load(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] args = line.split(" ");
            Setting setting = new Setting(args[0]);
            setting.setValue(args[1]);
            settings.put(args[0], setting);
        }

        reader.close();
    }

    public void store(OutputStream outputStream) {
        PrintWriter writer = new PrintWriter(outputStream);
        settings.keySet().forEach(s -> writer.println(s + " " + settings.get(s).getValue()));

        writer.flush();
        writer.close();
    }

    public void setValue(String name, String value) {
        Setting setting = settings.get(name);
        if (setting == null) {
            setting = new Setting(name);
            settings.put(name, setting);
        }

        setting.setValue(value);
    }

    public String getValue(String name) {
        return settings.get(name).getValue();
    }

    public Set<String> getNames() {
        return settings.keySet();
    }

    public int size() {
        return settings.size();
    }

    public Setting getSetting(String name) {
        return settings.get(name);
    }
}
