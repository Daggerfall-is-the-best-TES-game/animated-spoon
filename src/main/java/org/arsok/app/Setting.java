package org.arsok.app;

public class Setting {
    private final String name;
    private String value;

    public Setting(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public enum SettingType {

    }
}
