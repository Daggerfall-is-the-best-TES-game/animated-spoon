package org.arsok.app;

public class BlackHole {
    //TODO: fields
    private double mass; //in kilograms

    public BlackHole(double mass) {
        this.mass = mass;

    }

    public BlackHole() {
        this.mass = 1e31; //default value of mass of black hole in Kilograms

    }

    public double getMass() {
        return mass;
    }
}
