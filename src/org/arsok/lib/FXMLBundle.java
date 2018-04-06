package org.arsok.lib;

import javafx.scene.Parent;

import java.net.URL;

public class FXMLBundle<P extends Parent, C> {
    private final URL location;
    private final P parent;
    private final C controller;

    public FXMLBundle(URL location, P parent, C controller) {
        this.location = location;
        this.parent = parent;
        this.controller = controller;
    }

    public URL getLocation() {
        return location;
    }

    public P getParent() {
        return parent;
    }

    public C getController() {
        return controller;
    }
}
