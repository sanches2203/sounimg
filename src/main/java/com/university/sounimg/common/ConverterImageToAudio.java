package com.university.sounimg.common;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConverterImageToAudio extends Task<File> {

    private File file;

    private void convertImageToAudio() {
        try {
            Process p = Runtime.getRuntime().exec("cmd /c cd /d \"D:\\temp\\arss\"" +
                    " && arss -q D:\\temp\\temp.bmp D:\\temp\\temp.wav -s -r 44100 -min 55.000 -b 12 -p 100");
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
                file = new File("D:\\temp\temp.wav");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected File call() throws Exception {
        updateMessage("Converting audio...");
        convertImageToAudio();
        return file;
    }
}
