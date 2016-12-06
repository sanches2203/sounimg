package com.university.sounimg.security;

import java.io.Serializable;

public class Keys implements Serializable {
    private double keyX = 0;
    private double keyY = 0;
    private double keyZ = 0;
    private double keyA = 0;
    private double keyB = 0;

    public Keys(String x, String y, String z, String a, String b) {
        keyX = Double.parseDouble(x);
        keyY = Double.parseDouble(y);
        keyZ = Double.parseDouble(z);
        keyA = Double.parseDouble(a);
        keyB = Double.parseDouble(b);
    }

    public double getKeyX() {
        return keyX;
    }

    public double getKeyY() {
        return keyY;
    }

    public double getKeyZ() {
        return keyZ;
    }

    public double getKeyA() {
        return keyA;
    }

    public double getKeyB() {
        return keyB;
    }

}
