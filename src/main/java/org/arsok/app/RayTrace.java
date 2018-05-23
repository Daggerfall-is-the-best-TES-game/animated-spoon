package org.arsok.app;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import static java.lang.Math.*;
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

        //https://en.wikipedia.org/wiki/Gravitational_constant
        private double g = 6.67408e-11;
        //https://en.wikipedia.org/wiki/Speed_of_light
        private double c = 299792458;

        private RayTraceRunnable() {

        }
        //bending angle of light ray. This is a big deal
        //The bending angle is the angle between the initial trajectory of the light ray and the radius vector(tail at black hole, head at photon)
        //Note that this means that vector trajectories
        //bending angle of light ray. This is a big deal
        //The bending angle is the angle between the initial trajectory of the light ray and the radius vector(tail at black hole, head at photon)
        //Note that this means that vector trajectories
        //using formulas 27 and 23 from https://arxiv.org/pdf/gr-qc/0611086.pdf
        //valid for 0 < theta < 90 degrees
        private double phi(double rSubZero, double theta) {
            double mDot = g * blackHole.getMass() / (c * c); //https://en.wikipedia.org/wiki/Schwarzschild_radius
            double b = abs(rSubZero * sin(theta)); //impact parameter
            double bCritical = 3 * sqrt(3) * mDot; //critical impact parameter

            if (b < bCritical) {
                return Double.NaN;
            }

            double bPrime = 1 - (bCritical / b); //invariant paramter

            //System.out.format("b: %.2f", bPrime);
            if (bPrime < 0.4705) { //light is close to black hole. strong lensing equation
                double bendingAngle = 0;
                double lambdaSubZero = 216 * (7 - 4 * sqrt(3));
                double[] sigmaCoeff = {1, 5.0 / 18, 205.0 / 1296, 68145.0 / 629856};
                double[] rhoCoeff = {-PI, (-17 + 4 * sqrt(3)) / 18, (-879 + 236 * sqrt(3)) / 1269, (-321590 + 90588 * sqrt(3)) / 629857};
                for (int i = 0; i < sigmaCoeff.length; i++) {
                    bendingAngle += sigmaCoeff[i] * pow(bPrime, i) * log(lambdaSubZero / bPrime) + rhoCoeff[i] * pow(bPrime, i);
                }
                return bendingAngle;
            } else { //light is far from black hole. weak lensing equation
                double bendingAngle = 0;
                double[] coefficients = {4 / (3 * sqrt(3)), 5 * PI / 36, 128 / (243 * sqrt(3)), 385 * PI / 5184, 3584 / (10935 * sqrt(3))};
                for (int i = 0; i < coefficients.length; i++) {
                    bendingAngle += coefficients[i] * pow((1 - bPrime), i + 1);
                }
                return bendingAngle;
            }
        }


        @Override
        public void run() {
            //Project Plan V2 with https://arxiv.org/pdf/0804.4112.pdf
            double rSubZero = 3 * sqrt(3) * 6.67408e-11 * blackHole.getMass() / (299792458.0 * 299792458) * 1.5; //radius
            //testing the output of the phi function, which is the bending angle
            for (double i = 0; i < PI * 2; i += PI / 100) {
                //System.out.format("emission angle (degrees): %.1f Phi (degrees): %.1f%n", toDegrees(i), toDegrees(phi(rSubZero, i)));
            }

            /*
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    updatePixel(i, j);
                }
            }
            */
        }
    }
}
