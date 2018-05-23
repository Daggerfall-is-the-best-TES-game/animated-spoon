package org.arsok.app;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Setting {
    private final String name;
    private final StringProperty value = new SimpleStringProperty();

    public Setting(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public StringProperty valueProperty() {
        return value;
    }

    public enum SettingType {
        //TODO: settingsType
    }
}
