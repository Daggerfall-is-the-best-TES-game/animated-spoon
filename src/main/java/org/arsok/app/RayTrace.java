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
        //units of space are all in meters
        double xPosition = 0; //x is left(-) and right(+) position coordinate
        double zPosition = -50; //z is towards(+) black hole and away(-) position coordinate
        double xWaveVelocity = 10; //direction of the propagation of light in the x direction
        double zWaveVelocity = 50; //direction of the propagation of light in the z direction
        //double waveNumber = 100; //temporary value until I figure out which value it must have. maybe the magnitude of k?
        double[] Y = {xPosition, zPosition, xWaveVelocity, zWaveVelocity};  //packaged up for your convenience

        private RayTraceRunnable() {

        }
        double M = blackHole.getMass(); //mass in kilograms
        double G = 6.67408e-11;
        //c = 1
        double schwarzschildRadius = 2 * M * G; // in meters

        @Override
        public void run() {

            rungeKutta(0.1, Y);

            /*
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    updatePixel(i, j);
                }
            }
            */
        }

        /*raytracing equations:
        formula 19 in:
        https://arxiv.org/ftp/arxiv/papers/1001/1001.2177.pdf
        k is the wavevector
        r is the magnitude of the radius magnitude(<xPosition, zPosition>)
        Φ is a the negative of the ratio of the Schwarzschild radius to r
        ψ is the angle between the wavevector k and the radius vector r
        */
        private double[] pathFunction(double x, double[] Y) {
            double[] r = new double[]{Y[0], Y[1]};
            double[] k = new double[]{Y[2], Y[3]};
            double radius = Math.sqrt(xPosition * xPosition + zPosition * zPosition);
            double Phi = -schwarzschildRadius / radius;
            //https://www.mathworks.com/matlabcentral/answers/101590-how-can-i-determine-the-angle-between-two-vectors-in-matlab?requestedDomain=true
            double angle = angleBetweenVectors(r, k);
            double y11 = 2 * x * Phi * Math.cos(angle) / radius;
            double y12 = 2 * (1 + Phi);
            double y21 = x * x * Phi * (1 + 3 * Math.cos(angle) * Math.cos(angle)) / (radius * radius);
            double y22 = -2 * x * Phi * Math.cos(angle) / radius;
            double[] answer = new double[4]; //temporary
            //answer = {rx, rz, kx, kz}
            answer[0] = r[0] * y11 + k[0] * y12;
            answer[1] = r[1] * y11 + k[1] * y12;
            answer[2] = r[0] * y21 + k[0] * y22;
            answer[3] = r[1] * y21 + k[1] * y22;
            return answer;
        }

        /*step is the stepsize for the algorithm
        fourth order Runge-Kutta

         */
        private double[] rungeKutta(double step, double[] initConditions) {
            //increments
            double[] k1;
            double[] k2;
            double[] k3;
            double[] k4;
            for (double i = 0; i < 1; i += step) {
                k1 = arrayScalarMult(step, pathFunction(i, initConditions));
                k2 = arrayScalarMult(step, pathFunction(i + 0.5 * step, arrayAdd(arrayScalarMult(0.5, k1), initConditions)));
                k3 = arrayScalarMult(step, pathFunction(i + 0.5 * step, arrayAdd(arrayScalarMult(0.5, k2), initConditions)));
                k4 = arrayScalarMult(step, pathFunction(i + step, arrayAdd(k3, initConditions)));


                double[] part1 = arrayAdd(k1, arrayScalarMult(2, k2));
                double[] part2 = arrayAdd(part1, arrayScalarMult(2, k3));
                double[] part3 = arrayScalarMult(1 / 6, arrayAdd(part2, k4));
                initConditions = arrayAdd(initConditions, part3);
                System.out.println("xradius" + initConditions[0]);
                System.out.println("zradius" + initConditions[1]);
                System.out.println("xwave" + initConditions[2]);
                System.out.println("zwave" + initConditions[3]);

            }


            return initConditions;

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


        //Project Plan V2 with https://arxiv.org/pdf/0804.4112.pdf



    }
}
