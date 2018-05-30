package org.arsok.app;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static com.meti.lib.Environment.getMainInstance;

public class RayTrace {
    private final ObjectProperty<Image> backgroundImage = new SimpleObjectProperty<>();
    private final WritableImage image;
    private final SimpleDoubleProperty distance = new SimpleDoubleProperty();

    //TODO: handle writer
    private final PixelWriter writer;
    private BlackHole blackHole;

    public RayTrace() {
        this(1000, 1000);
    }

    public void init() {
        this.setDistance(3 * Math.sqrt(3) * 6.67408e-11 * blackHole.getMass() / (299792458.0 * 299792458) * 1.5); //1.5 times the critical distance

    }

    public double getDistance() {
        return distance.get();
    }

    public void setDistance(double distance) {
        this.distance.set(distance);
    }

    public SimpleDoubleProperty distanceProperty() {
        return distance;
    }

    public RayTrace(int width, int height) {
        this.image = new WritableImage(width, height);
        this.writer = image.getPixelWriter();
        //temporary hardcoded assignment of blackhole for testing purposes
    }

    public void start() {
        getMainInstance().getService().submit(new RayTraceRunnable());
    }

    public Image getImage() {
        return image;
    }

    public void bindBlackHole(BlackHole blackHole) {
        this.blackHole = blackHole;
    }

    public void setBackgroundImage(Image image) {
        backgroundImage.setValue(image);
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
            PixelReader backgroundReader = backgroundImage.get().getPixelReader();

            //double test = phi(getDistance(), -0.3);
            //TODO: compute background coordinates


            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {


                    //camera specifications
                    int centeredX = (int) (x - image.getWidth() / 2);
                    int centeredY = (int) (y - image.getHeight() / 2);
                    double angleOfView = Math.PI / 4; //vertical angle of view
                    double aspectRatio = 1;
                    double halfPlaneHeight = Math.tan(angleOfView / 2);
                    double halfPlaneWidth = aspectRatio * halfPlaneHeight;


                    //mapping from pixel to emission angle and lensing that angle
                    double emissionAzithmuthalAngle = Math.atan(centeredX * halfPlaneWidth / (image.getWidth() / 2.0)); //angle to the right or left in radians
                    double emissionEquatoralAngle = Math.atan(centeredY * halfPlaneHeight / (image.getHeight() / 2.0)); //angle up or down in radians
                    double blackHoleAngle = Math.hypot(emissionAzithmuthalAngle, emissionEquatoralAngle); //angle between black hole radius and vector pointing to pixel in camera plane
                    double lensedAngle = blackHoleAngle - phi(getDistance(), blackHoleAngle); //angle after lensing

                    if (lensedAngle != lensedAngle) {  //means lensedAngle is NaN
                        writer.setColor(x, (int) image.getHeight() - y, Color.BLACK);
                        continue;

                    }

                    //applying lensing to emission angle to get final angle of impact in the celestial sphere
                    double change = (lensedAngle - blackHoleAngle) / blackHoleAngle;
                    double sphereAzithmuthalAngle = emissionAzithmuthalAngle + emissionAzithmuthalAngle * change; //horizontal component of the lensed angle
                    double sphereEquatoralAngle = emissionEquatoralAngle + emissionEquatoralAngle * change; //vertical component of the lensed angle

                    double longitude = ((sphereAzithmuthalAngle - Math.PI / 2) % (Math.PI * 2) + sphereAzithmuthalAngle - Math.PI / 2) % (Math.PI * 2) - Math.PI; //mapping emission angle to longitude
                    double latitude = ((sphereEquatoralAngle - Math.PI / 2) % (Math.PI * 2) + sphereEquatoralAngle - Math.PI / 2) % (Math.PI * 2) - Math.PI; //mapping emission angle to latitude


                    int backgroundX = (int) (longitude / (Math.PI * 2) * backgroundImage.get().getWidth() + backgroundImage.get().getWidth() / 2); //what background pixel the light hits
                    int backgroundY = (int) (latitude / (Math.PI * 2) * backgroundImage.get().getHeight() + backgroundImage.get().getHeight() / 2);//what background pixel the light hits

                    //drawing the pixel
                    writer.setColor(x, (int) image.getHeight() - y, backgroundReader.getColor(backgroundX, (int) backgroundImage.get().getHeight() - backgroundY));
                }
            }
        }
    }
}
