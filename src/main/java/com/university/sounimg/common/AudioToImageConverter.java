package com.university.sounimg.common;

import com.sun.istack.internal.NotNull;
import com.university.sounimg.application.Main;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class AudioToImageConverter extends Task<Image> {

    private BufferedImage image;

    @NotNull
    private String inputAudioPath;

    public AudioToImageConverter(String inputAudioPath) {
        this.inputAudioPath = inputAudioPath;
    }

    private void convertAudioToImage(String inputAudioFilePath) {
        try {
            Process p = Runtime.getRuntime().exec("cmd /c cd /d \"D:\\temp\\arss\"" +
                    " && arss -q " + inputAudioFilePath +
                    " " + "D:\\temp\\temp.png" + " -b 12 --min-freq 55 -max 16000 --pps 100 -f 16");
            new Thread(() -> {
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;

                try {
                    while ((line = input.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            try {
                p.waitFor();
                image = ImageIO.read(new File("D:\\temp\\temp.png"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Image call() throws Exception {
        updateMessage("Converting audio...");
        convertAudioToImage(inputAudioPath);
        updateProgress(1,1);
        updateMessage("Successful!");
        return SwingFXUtils.toFXImage(image, null);
    }
}
