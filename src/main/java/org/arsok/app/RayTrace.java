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
/*        Image image = new Image("pixels-photo-110854.jpeg");
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


    //separate thread for running the program
    private class RayTraceRunnable implements Runnable {


        private RayTraceRunnable() {

        }

        //Project Plan V2 with https://arxiv.org/pdf/0804.4112.pdf
        double rSubZero = 1e5; //radius- 100 kilometers away from black hole
        double theta = 0.5; //radians- temp value. angle between path of photon and radius vector of black hole
        double rSubg = 2 * blackHole.getMass(); //Schwarzschild radius in meters
        double n = 1; //number of dimensions including time - 3
        double qSubStar = Math.pow(2 / (n + 2), 1 / n); //position that gives minimum of lSubmax
        double lambda = rSubZero * Math.sin(theta); //impact parameter
        double l = lambda / rSubg;

        //bending angle of light ray. This is a big deal
        private double phi(double rSubZero, double theta) {

            double qSubZero = q(rSubZero); //dimensionless quantity


            double P = Math.pow(p(rSubZero), n); //fixed
            double B = Math.pow(nu(rSubZero), 2); //fixed

            return 0; //temp
        }

        private double q(double radius) { //dimensionless radius
            return rSubg / radius;
        }

        private double y(double radius) {
            return 1 - rSubZero / radius;
        }

        private double p(double radius) {
            return q(radius) / qSubStar;
        }

        private double lSubmax(double radius) {
            return 1.0 / (q(radius) * Math.sqrt(1 - Math.pow(q(radius), n)));
        }

        private double nu(double radius) {
            return l / lSubmax(radius);
        }


        @Override
        public void run() {

            phi(rSubZero, theta);

            /*
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    updatePixel(i, j);
                }
            }
            */
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




        //https://www.mathworks.com/matlabcentral/answers/101590-how-can-i-determine-the-angle-between-two-vectors-in-matlab?requestedDomain=true
        private double angleBetweenVectors(double[] vec1, double[] vec2) {
            //norm of cross product
            double s1 = Math.abs(vec1[0] * vec2[1] - vec1[1] * vec2[0]);
            //dot product
            double s2 = vec1[0] * vec2[0] + vec1[1] * vec2[1];

            return Math.atan2(s1, s2);
        }

        //adds scalar to every element of array a
        private double[] arrayScalarAdd(double scalar, double[] a) {
            for (int i = 0; i < a.length; i++) {
                a[i] += scalar;
            }
            return a;
        }

        //array addition
        private double[] arrayAdd(double a[], double[] b) {
            for (int i = 0; i < a.length; i++) {
                a[i] += b[i];
            }
            return a;
        }

        //multiply every element of an array by a scalar
        private double[] arrayScalarMult(double scalar, double[] a) {
            for (int i = 0; i < a.length; i++) {
                a[i] += scalar;
            }
            return a;
        }



    }
}
