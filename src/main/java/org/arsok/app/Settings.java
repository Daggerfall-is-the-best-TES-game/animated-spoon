package org.arsok.app;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;

public class Settings {
    private final HashMap<String, Setting> settings = new HashMap<>();

    //TODO: loading settings
    public void load(InputStream inputStream) {

    }

    public void store(OutputStream outputStream) {

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
