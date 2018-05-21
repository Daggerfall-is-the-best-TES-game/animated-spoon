package org.arsok.engine;

public class Vector {
    private final float[] values;

    public Vector(float[] values) {
        this.values = values;
    }

    public Vector(int size) {
        this.values = new float[size];
    }

    public static Vector cross(Vector vector1, Vector vector2) throws EngineException {
        checkSize(vector1, vector2);

        if (vector1.size() == 3) {
            float[] newValues = new float[3];
            newValues[0] = vector1.getValue(1) * vector2.getValue(2) - vector1.getValue(2) * vector2.getValue(1);
            newValues[1] = vector1.getValue(0) * vector2.getValue(2) - vector1.getValue(2) * vector2.getValue(0);
            newValues[2] = vector1.getValue(0) * vector2.getValue(1) - vector1.getValue(1) * vector2.getValue(0);

            return new Vector(newValues);
        } else {
            throw new EngineException("Cannot take cross product of vectors without dimension of 3");
        }
    }

    public static void checkSize(Vector vector1, Vector vector2) throws EngineException {
        if (vector1.size() != vector2.size()) {
            throw new EngineException("Vector sizes are not equal");
        }
    }

    public int size() {
        return values.length;
    }

    public float getValue(int index) {
        return values[index];
    }

    public static float dot(Vector vector1, Vector vector2) throws EngineException {
        checkSize(vector1, vector2);

        float sum = 0;
        for (int i = 0; i < vector1.size(); i++) {
            sum += vector1.getValue(i) * vector2.getValue(i);
        }

        return sum;
    }

    public Vector normalize() {
        float length = length();
        float[] newValues = new float[values.length];

        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i] / length;
        }

        return new Vector(newValues);
    }

    public float length() {
        float sum = 0;

        for (int i = 0; i < values.length; i++) {
            sum += Math.pow(values[i], 2);
        }

        return (float) Math.sqrt(sum);
    }

    public float[] getValues() {
        return values;
    }
}
