package com.university.sounimg.common;

import com.university.sounimg.generator.Generator;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CommonAudio extends Task<Image> {
    private Color imageColor;
    private Generator generator;
    private BufferedImage img;
    private File file;
    private double x;
    private double y;
    private double z;
    private double alpha;
    private double beta;
    private boolean isEncrypt;
    private String bmpFileName;

    public CommonAudio(File file, double x, double y, double z, double alpha, double beta, boolean isEncrypt) {
        this.file = file;
        this.x = x;
        this.y = y;
        this.z = z;
        this.alpha = alpha;
        this.beta = beta;
        this.isEncrypt = isEncrypt;
    }

    private void scanImage() {
        try {
            img = ImageIO.read(file);
            generator = new Generator();
            generator.generateRossler(img.getHeight() * img.getWidth(), x, y, z, alpha, beta);
            generator.preProccesor(img.getHeight() * img.getWidth(), generator.getKeyX(), generator.getKeyY(), generator.getKeyZ());

            if (isEncrypt) {
                encryptImage();
            } else {
                decryptImage();
            }

            String fileExtension = FilenameUtils.getExtension(file.getName());

            if (fileExtension.contains("jpeg") || fileExtension.contains("jpg")) {
                fileExtension = "png";
            }

            ImageIO.write(img, fileExtension, new File(FilenameUtils.getBaseName(file.getName()) + "." + fileExtension));
        } catch (Exception e) {
            System.out.println("Incorrect File " + e.getMessage());
        }

        updateProgress(img.getWidth() * img.getHeight(), img.getWidth() * img.getHeight());

        System.out.println("Finished");

    }

    private void encryptImage() {
        int[][] matrix = new int[img.getWidth()][img.getHeight()];
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                imageColor = new Color(img.getRGB(i, j));
                int r = imageColor.getRed() ^ generator.getArrayRedXor()[i + j];
                int g = imageColor.getGreen() ^ generator.getArrayGreenXor()[i + j];
                int b = imageColor.getBlue() ^ generator.getArrayBlueXor()[i + j];
                matrix[i][j] = getIntFromColor(r, g, b);
            }
        }

        encryptMatrix(matrix, img.getWidth() * img.getHeight(), generator.getKeyX() ^ generator.getKeyY() ^ generator.getKeyZ() ^ generator.getKeyCube());

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                img.setRGB(i, j, matrix[i][j]);
            }
        }

        convertAndSavePngToBmp();
        Task<File> imageConverter = new ImageToAudioConverter();
        new Thread(imageConverter).start();
    }

    private void decryptImage() {

        int[][] matrix = new int[img.getWidth()][img.getHeight()];
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                matrix[i][j] = img.getRGB(i, j);
            }
        }

        decryptMatrix(matrix, img.getWidth() * img.getHeight(), generator.getKeyX() ^ generator.getKeyY() ^ generator.getKeyZ() ^ generator.getKeyCube());

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                imageColor = new Color(matrix[i][j]);
                int r = imageColor.getRed() ^ generator.getArrayRedXor()[i + j];
                int g = imageColor.getGreen() ^ generator.getArrayGreenXor()[i + j];
                int b = imageColor.getBlue() ^ generator.getArrayBlueXor()[i + j];
                img.setRGB(i, j, getIntFromColor(r, g, b));
            }
        }
    }

    private void shuffle(int[] array, Random random) {
        int n = array.length;
        for (int i = 0; i < array.length; i++) {
            int rand = random.nextInt(n);
            int randomElement = array[rand];
            array[rand] = array[i];
            array[i] = randomElement;
        }
    }

    private void encryptMatrix(int[][] matrix, int size, int key) {
        int k = 0;
        int[] matrixMap = new int[size];

        Random random = new Random(key);
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < matrixMap.length; i++) {
            matrixMap[i] = i;
        }

        shuffle(matrixMap, random);

        for (int[] aMatrix : matrix) {
            for (int anAMatrix : aMatrix) {
                map.put(k, anAMatrix);
                k++;
            }
        }
        k = 0;
        updateMessage("Encrypting image...");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = map.get(indexOfArray(matrixMap, k));
                k++;
                updateProgress(k, size + 1);
            }
        }
    }

    private void decryptMatrix(int[][] matrix, int size, int key) {
        Map<Integer, Integer> map = new HashMap<>();
        Random random = new Random(key);
        int m = 0;
        int[] matrixMap = new int[size];

        for (int i = 0; i < matrixMap.length; i++) {
            matrixMap[i] = i;
        }

        int[] array2 = Arrays.copyOf(matrixMap, matrixMap.length);

        shuffle(array2, random);


        for (int[] aMatrix : matrix) {
            for (int anAMatrix : aMatrix) {
                map.put(m, anAMatrix);
                m++;
            }
        }
        m = 0;
        updateMessage("Decrypting image...");
        for (int i = 0; i <= matrix.length - 1; i++) {
            for (int j = 0; j <= matrix[i].length - 1; j++) {
                matrix[i][j] = map.get(searchIndex(array2, m));
                m++;
                updateProgress(m, size + 1);
            }
        }
    }

    private int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000;
        Green = (Green << 8) & 0x0000FF00;
        Blue = Blue & 0x000000FF;
        return 0xFF000000 | Red | Green | Blue;
    }

    private int indexOfArray(int[] array1, int index) {
        return array1[index];
    }

    private int searchIndex(int[] array1, int index) {
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] == index) {
                return i;
            }

        }
        return -1;
    }

    private void convertAndSavePngToBmp() {
        try {
            BufferedImage newImage = new BufferedImage(
                    img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

            Graphics2D g = newImage.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            //bmpFileName = FilenameUtils.getFullPath(file.getAbsolutePath()).concat(FilenameUtils.getBaseName(file.getName()).concat(".").concat(".bmp"));
            ImageIO.write(newImage, "BMP", new File("D:\\temp\\temp.bmp"));
            //FileUtils.forceDelete(new File(FilenameUtils.getFullPath(bmpFileName)
                    //.concat(FilenameUtils.getBaseName(bmpFileName)).concat(".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void convertImageToAudio() {

    }

    @Override
    protected Image call() throws Exception {
        updateMessage("Calculating data of image...");
        Thread.sleep(5000);
        updateMessage("Generation systems...");
        scanImage();
        updateMessage("Successful!");
        return SwingFXUtils.toFXImage(img, null);
    }
}
