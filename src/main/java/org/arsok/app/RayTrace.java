package org.arsok.app;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import static org.arsok.app.Main.instance;

public class RayTrace {
    private final WritableImage image;

    //TODO: handle writer
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
        private RayTraceRunnable() {

        }

        //bending angle of light ray. This is a big deal
        //The bending angle is the angle between the initial trajectory of the light ray and the radius vector(tail at black hole, head at photon)
        //Note that this means that vector trajectories
        //using formulas 27 and 23 from https://arxiv.org/pdf/gr-qc/0611086.pdf
        //valid for 0 < theta < 90 degrees
        private double phi(double rSubZero, double theta) {
            double G = 6.67408e-11; //https://en.wikipedia.org/wiki/Gravitational_constant
            double c = 299792458; //https://en.wikipedia.org/wiki/Speed_of_light
            double mDot = G * blackHole.getMass() / (c * c); //https://en.wikipedia.org/wiki/Schwarzschild_radius/impact parameter
            double btest = (rSubZero * Math.sin((Math.PI - theta))) / Math.sqrt(1 - mDot / rSubZero); //alternate, possibly more accurate formula for the impact parameter from BELOBORODOV
            double bCritical = 3 * Math.sqrt(3) * mDot; //critical impact parameter

            if (btest < bCritical) {
                return Double.NaN;
            }

            double bPrime = 1 - (bCritical / btest); //invariant paramter

            if (bPrime < 0.4705) { //light is close to black hole. strong lensing equation
                double bendingAngle = 0;
                double lambdaSubZero = 216 * (7 - 4 * Math.sqrt(3));
                double[] sigmaCoeff = {1, 5.0 / 18, 205.0 / 1296, 68145.0 / 629856};
                double[] rhoCoeff = {-Math.PI, (-17 + 4 * Math.sqrt(3)) / 18, (-879 + 236 * Math.sqrt(3)) / 1269, (-321590 + 90588 * Math.sqrt(3)) / 629857};
                for (int i = 0; i < sigmaCoeff.length; i++) {
                    bendingAngle += sigmaCoeff[i] * Math.pow(bPrime, i) * Math.log(lambdaSubZero / bPrime) + rhoCoeff[i] * Math.pow(bPrime, i);
                }
                return bendingAngle;
            } else { //light is far from black hole. weak lensing equation
                double bendingAngle = 0;
                double[] coefficients = {4 / (3 * Math.sqrt(3)), 5 * Math.PI / 36, 128 / (243 * Math.sqrt(3)), 385 * Math.PI / 5184, 3584 / (10935 * Math.sqrt(3))};
                for (int i = 0; i < coefficients.length; i++) {
                    bendingAngle += coefficients[i] * Math.pow((1 - bPrime), i + 1);
                }
                return bendingAngle;
            }
        }


        @Override
        public void run() {
        }
    }
}
