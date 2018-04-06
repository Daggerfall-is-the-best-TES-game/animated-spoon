package org.arsok.app;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static org.arsok.app.Main.instance;

public class RayTrace {
    private final WritableImage image;
    private final PixelWriter writer;

    public RayTrace() {
        this(1000, 1000);
    }

    public RayTrace(int width, int height) {
        this.image = new WritableImage(width, height);
        this.writer = image.getPixelWriter();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                writer.setColor(i, j, Color.BLACK);
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
            double xDelta = (image.getWidth() / 2) - x;
            double yDelta = (image.getHeight() / 2) - y;
            double radius = Math.pow(xDelta, 2) + Math.pow(yDelta, 2);
            if (Math.abs(xDelta) <= 100 &&
                    Math.abs(yDelta) <= 100 && (radius <= Math.pow(101, 2) && radius >= Math.pow(99, 2))) {
                writer.setColor(x, y, Color.RED);
            }
        }
    }
}
