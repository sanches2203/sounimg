package com.university.sounimg.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ImageConverter {

    public static void convertAndSavePngToBmp(String inputPNGFile, String outputBMPFile) {
        try {
            BufferedImage in = ImageIO.read(new File(inputPNGFile));
            BufferedImage newImage = new BufferedImage(
                    in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);

            Graphics2D g = newImage.createGraphics();
            g.drawImage(in, 0, 0, null);
            g.dispose();
            ImageIO.write(newImage, "BMP", new File(outputBMPFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
