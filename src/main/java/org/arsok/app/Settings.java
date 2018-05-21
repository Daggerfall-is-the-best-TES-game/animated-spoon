package org.arsok.app;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;

public class Settings {
    private final HashMap<String, Setting> map = new HashMap<>();

    public void load(InputStream inputStream) {

    }

    public void store(OutputStream outputStream) {

    }

    public void updateValue(String name, String text) {
        map.get(name).setValue(text);
    }

    public String getValue(String name) {
        return map.get(name).getValue();
    }

    public Set<String> getNames() {
        return map.keySet();
    }

    public int size() {
        return map.size();
    }
}
