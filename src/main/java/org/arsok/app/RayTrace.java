package org.arsok.app;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import static org.arsok.app.Main.instance;

public class RayTrace {
    private final WritableImage image;
    private final PixelWriter writer;
    private BlackHole blackHole;

    public RayTrace() {
        this(1000, 1000);
    }

    public RayTrace(int width, int height) {
        this.image = new WritableImage(width, height);
        this.writer = image.getPixelWriter();
        //temporary hardcoded assignment of blackhole for testing purposes
        blackHole = new BlackHole(1e31);
/*        Image image = new Image("pexels-photo-110854.jpeg");
        writer.setPixels(0, 0, width, height, image.getPixelReader(), 0, 0);*/
    }

    public void start() {
        instance.getService().submit(new RayTraceRunnable());
    }

    public Image getImage() {
        return image;
    }

    public void bindBlackHole(BlackHole blackHole) {
        this.blackHole = blackHole;
    }

    //seperate thread for running the program
    private class RayTraceRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    updatePixel(i, j);
                }
            }
        }

        //units of space are all in meters
        double xPosition; //x is left(-) and right(+) position coordinate
        double zPosition; //z is towards(+) black hole and away(-) position coordinate
        double xWaveVelocity; //direction of the propagation of light in the x direction
        double zWaveVelocity; //direction of the propagation of light in the z direction
        double M = blackHole.getMass(); //mass in kilograms
        double G = 6.67408e-11;
        //c = 1
        double schwarzschildRadius = 2 * M * G; // in meters
        double[] Y = {xPosition, zPosition, xWaveVelocity, zWaveVelocity};  //packaged up for your convenience


        private double[] rungeKutta(double[] initConditions) {
            return initConditions; //temporary return statement

        }


        private void stereographicProjection() {


        }

        //constructing the image
        private void updatePixel(int x, int y) {
            //TODO: update pixel

            /*
            example code
            double xDelta = (image.getWidth() / 2) - x;
            double yDelta = (image.getHeight() / 2) - y;

            double radius = Math.pow(xDelta, 2) + Math.pow(yDelta, 2);

            if (Math.abs(xDelta) <= 100 &&
                    Math.abs(yDelta) <= 100 && (radius <= Math.pow(101, 2) && radius >= Math.pow(99, 2))) {
                writer.setColor(x, y, Color.RED);
            }*/
        }

        /*raytracing equations:
        formula 19 in:
        https://arxiv.org/ftp/arxiv/papers/1001/1001.2177.pdf
        k is the wavenumber
        r is the magnitude of the radius magnitude(<xPosition, zPosition>)
        Φ is a the negative of the ratio of the Schwarzschild radius to r
        ψ is the angle between the wavevector k and the radius vector r
        */
        private double[] pathFunction(double x, double[] Y) {
            double[] r = new double[]{Y[0], Y[1]};
            double[] k = new double[]{Y[2], Y[3]};
            double radius = Math.sqrt(Math.pow(xPosition, 2) + Math.pow(zPosition, 2));
            double Phi = -schwarzschildRadius / radius;
            //https://www.mathworks.com/matlabcentral/answers/101590-how-can-i-determine-the-angle-between-two-vectors-in-matlab?requestedDomain=true
            double angle = angleBetweenVectors(r, k);
            double y11;
            double y12;
            double y21;
            double y22;

            double[] rPrime;
            double[] kPrime;
            double[] answer = new double[]{0}; //temporary
            return answer;
        }


        //https://www.mathworks.com/matlabcentral/answers/101590-how-can-i-determine-the-angle-between-two-vectors-in-matlab?requestedDomain=true
        private double angleBetweenVectors(double[] vec1, double[] vec2) {
            //norm of cross product
            double s1 = Math.abs(vec1[0] * vec2[1] - vec1[1] * vec2[0]);
            //dot product
            double s2 = Math.abs(vec1[0] * vec2[0] + vec1[1] * vec2[1]);

            return Math.atan2(s1, s2);
        }

        //adds scalar to every element of array a
        private double[] arrayScalarAdd(double scalar, double[] a) {
            for (int i = 0; i < a.length; i++) {
                a[i] += scalar;
            }
            return a;
        }

        //multiplies every element of array a by scalar
        private double[] arrayScalarMult(double scalar, double[] a) {
            for (int i = 0; i < a.length; i++) {
                a[i] *= scalar;
            }
            return a;
        }
    }
}
