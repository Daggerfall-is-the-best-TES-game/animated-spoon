package org.arsok.app;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static org.arsok.app.Main.*;

public class RayTrace {
    private final WritableImage image;

    public RayTrace(){
        this(500, 500);
    }

    public RayTrace(int width, int height){
        this.image = new WritableImage(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.getPixelWriter().setColor(i, j, Color.BLACK);
            }
        }
    }

    public void start() {
        instance.getService().submit(new RayTraceRunnable());
    }

    public Image getImage() {
        return image;
    }

    private class RayTraceRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    updatePixel(i, j);
                }
            }
        }

        private void updatePixel(int x, int y) {
            //TODO: update pixel
        }
    }
}
