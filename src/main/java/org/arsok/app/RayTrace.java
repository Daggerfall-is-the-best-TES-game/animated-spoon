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




        //bending angle of light ray. This is a big deal
        //The bending angle is the angle between the initial trajectory of the light ray and the radius vector(tail at black hole, head at photon)
        //Note that this means that vector trajectories
        private double phi(double rSubZero, double theta) {
            double wavelength = 0.65e-6;//wavelength of light. hardcoded for now
            double rSubg = Math.sqrt((blackHole.getMass() * wavelength) / (2 * Math.PI)); //gravitational radius in meters http://www.journaloftheoretics.com/articles/4-1/kanarev-gravradbh.pdf

            //double rSubg = Math.pow((16 * Math.PI * blackHole.getMass()) / (2 * ((2 * Math.pow(Math.PI, 3.0/2)) / (0.8862269254))), 1.0/4);  //testing alternate forumla. prob wrong

            double n = 1; //number of dimensions including time - 3
            double qSubStar = Math.pow(2 / (n + 2), 1 / n); //position that gives minimum of lSubmax
            double lambda = rSubZero * Math.sin(theta); //impact parameter
            double l = lambda / rSubg;

            double qSubZero = q(rSubZero, rSubg); //dimensionless quantity
            double pSubZero = qSubZero / qSubStar;


            double nu = l / (1.0 / ((rSubg / rSubZero) * Math.sqrt(1 - Math.pow((rSubg / rSubZero), n))));

            //calculation of deltaPhiHat
            double kSubPhi = 0.47; //constant parameter for 4 dimensionional spacetime
            double phiSubPhi = Math.PI / 45;

            if (qSubZero <= qSubStar) { // Type I rays bending angle. formula (50) in paper
                double P = Math.pow(p(rSubZero, qSubStar, rSubg), n); //fixed
                double B = Math.pow(nu, 2); //fixed
                double a = (1 - B) * (1 - (2 * P) / (n + 2));
                double b = B * (1 - P);
                double c = -B * (1 - (n + 1) * P);
                double A = 1 - (2 * P) / (n + 2) + (n * (n + 1) * B * P) / (n + 2);

                double D = a * c - Math.pow(b, 2);
                //calculation of phiHat
                double phiHat;
                if (c < 0) {
                    phiHat = (nu / Math.sqrt(-c)) * Math.asin(((Math.sqrt(-c) * B) / D) * (n * P * Math.sqrt(a) - (1 - P) * Math.sqrt(A)));
                } else if (c == 0) {
                    phiHat = (nu / b) * (Math.sqrt(a + 2 * b) - Math.sqrt(a));
                } else { //c > 0
                    phiHat = (nu / Math.sqrt(c)) * Math.log((n * B * P + Math.sqrt(c * A)) / (b + Math.sqrt(a * c)));
                }
                double xSubPhi = Math.sqrt(2) * (Math.cos((Math.PI / 4) + phiSubPhi) * pSubZero + Math.sin((Math.PI / 4) + phiSubPhi) * nu);
                double deltaPhiHat = -kSubPhi * (Math.sqrt(1 - Math.pow(xSubPhi - 1, 2)) - 1) * heavisideStepFunction(xSubPhi - 1);
                return phiHat + deltaPhiHat; //phi, the bending angle
            } else { //qSubZero > qSubStar. Type II rays bending angle. formula (61) in paper
                //calculation of phiHatSubStar. needed for when P = 1. approximation. formula (47). replace with formula 46 if not good enough
                double lSubStar = Math.sqrt((n + 2) / n) * Math.pow((n + 2) / 2, 1 / n);
                double mu = l / lSubStar;
                double phiHatSubStar = (1.0 / Math.sqrt(n)) * Math.log((mu * Math.sqrt(n + 2) + Math.sqrt((n + 2) * Math.pow(mu, 2) + 1 - Math.pow(mu, 2))) / (Math.sqrt(1 - Math.pow(mu, 2))));
                double xSubPhiStar = Math.sqrt(2) * (Math.cos((Math.PI / 4) + phiSubPhi) * 1 + Math.sin((Math.PI / 4) + phiSubPhi) * nu); //like xSubPhi, but pSubZero = 1
                double deltaPhiHatSubStar = -kSubPhi * (Math.sqrt(1 - Math.pow(xSubPhiStar - 1, 2)) - 1) * heavisideStepFunction(xSubPhiStar - 1);
                double z = mu * Math.sqrt(n + 2) * (pSubZero - 1);
                double phiHatSubII = (1 / Math.sqrt(n)) * Math.log((z + Math.sqrt(Math.pow(z, 2) + 1 - Math.pow(mu, 2))) / Math.sqrt(1 - Math.pow(mu, 2)));
                return phiHatSubStar + deltaPhiHatSubStar + phiHatSubII;
            }
        }

        private double q(double radius, double rSubg) { //dimensionless radius
            return rSubg / radius;
        }

        private double p(double radius, double qSubStar, double rSubg) {
            return q(radius, rSubg) / qSubStar;
        }


        private double heavisideStepFunction(double n) {
            if (n <= 0) {
                return 0;
            }
            return 1;
        }


        @Override
        public void run() {
            //Project Plan V2 with https://arxiv.org/pdf/0804.4112.pdf
            double rSubZero = 2e12; //radius
            double theta = 2; //radians- temp value. angle between path of photon and radius vector of black hole
            //testing the output of the phi function, which is the bending angle
            for (double i = 0; i < Math.PI * 2; i += Math.PI / 100) {
                System.out.format("emission angle (degrees): %.1f Phi (degrees): %.1f%n", Math.toDegrees(i), Math.toDegrees(phi(rSubZero, i)));
            }
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
