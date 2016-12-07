package com.university.sounimg.generator;

import com.university.sounimg.util.ApplicationConstants;

import java.util.Random;

class Generator {

    private int[] arrayRedXor;
    private int[] arrayGreenXor;
    private int[] arrayBlueXor;

    private int keyX;
    private int keyY;
    private int keyZ;
    private int keyCube;

    int getKeyX() {
        return keyX;
    }

    int getKeyY() {
        return keyY;
    }

    int getKeyZ() {
        return keyZ;
    }

    int getKeyCube() {
        return keyCube;
    }

    int[] getArrayRedXor() {
        return arrayRedXor;
    }

    int[] getArrayGreenXor() {
        return arrayGreenXor;
    }

    int[] getArrayBlueXor() {
        return arrayBlueXor;
    }


    void generateRossler(int quantityPixels, double x, double y, double z, double alpha, double beta) {
        double tempX = 0; double tempY = 0; double tempZ = 0; double var;
        double[] arrayRosslerX = new double[quantityPixels];
        double[] arrayRosslerY = new double[quantityPixels];
        double[] arrayRosslerZ = new double[quantityPixels];


        arrayRosslerX[0] = x;
        arrayRosslerY[0] = y;
        arrayRosslerZ[0] = z;

        for (int i = 1; i <quantityPixels; i++) {
            arrayRosslerX[i] = (arrayRosslerX[i-1] + (-arrayRosslerY[i-1] - arrayRosslerZ[i-1])) * ApplicationConstants.DT;
            arrayRosslerY[i] = (arrayRosslerY[i-1] + (arrayRosslerX[i-1] + ApplicationConstants.P * arrayRosslerY[i-1])) * ApplicationConstants.DT;
            arrayRosslerZ[i] = (arrayRosslerZ[i-1] + (ApplicationConstants.Q + arrayRosslerZ[i-1] * (arrayRosslerX[i-1] - ApplicationConstants.R))) * ApplicationConstants.DT;
        }

        var = arrayRosslerX[100];

        for (int i = 0; i <quantityPixels; i++) {
            tempX += arrayRosslerX[i];
            tempY += arrayRosslerY[i];
            tempZ += arrayRosslerZ[i];
        }

        tempX = 10000 * tempX - 10000 * Math.round(tempX);
        tempY = 10000 * tempY - 10000 * Math.round(tempY);
        tempZ = 10000 * tempZ - 10000 * Math.round(tempZ);

        keyX = (int) tempX * 100;
        keyY = (int) tempY * 100;
        keyZ = (int) tempZ * 100;

        generateXCube(quantityPixels, alpha, beta, var);

        keyX = keyX ^ keyCube;
        keyY = keyY ^ keyCube;
        keyZ = keyZ ^ keyCube;
    }

    void preProccesor(int quantityPixels, int x, int y, int z) {
        arrayRedXor = new int[quantityPixels];
        arrayGreenXor = new int[quantityPixels];
        arrayBlueXor = new int[quantityPixels];
        Random randomRed = new Random(x);
        Random randomGreen = new Random(y);
        Random randomBlue = new Random(z);
        for (int i = 0; i < arrayRedXor.length; i++) {
            arrayRedXor[i] = randomRed.nextInt(255);
            arrayGreenXor[i] = randomGreen.nextInt(255);
            arrayBlueXor[i] = randomBlue.nextInt(255);
        }
    }

    private void generateXCube(int quantityPixels, double alpha, double beta,  double var) {
        double temp = 0;
        double[] arrayXcube = new double[quantityPixels];
        arrayXcube[0] = var;
        for (int i = 1; i <quantityPixels; i++) {
            arrayXcube[i] = alpha - beta * arrayXcube[i-1] + Math.pow(arrayXcube[i-1], 3);
        }

        for (int i = 0; i <quantityPixels ; i++) {
            temp += arrayXcube[i];
        }
        keyCube = (int) temp;
    }

}
