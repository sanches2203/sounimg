package com.university.sounimg.test;

import com.university.sounimg.util.ImageConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class New {

    public static void main(String[] args) throws Exception {
//        ProcessBuilder builder = new ProcessBuilder(
//                "cmd.exe", "/c", "cd \"C:\\Users\\sanch\\Desktop\\arss-0.2.3-windows\"" +
//                " && arss -q D:\\Record.wav D:\\Record.png -b 12 --min-freq 55 -max 16000 --pps 100 -f 16");
//        builder.redirectErrorStream(true);
//        Process p = builder.start();
//        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        String line;
//        while (true) {
//            line = r.readLine();
//            if (line == null) { break; }
//            System.out.println(line);
//        }

        Process p = Runtime.getRuntime().exec("cmd /c cd \"C:\\Users\\sanch\\Desktop\\arss-0.2.3-windows\"" +
                " && arss -q D:\\Record.wav D:\\Record.png -b 12 --min-freq 55 -max 16000 --pps 100 -f 16");

        new Thread(() -> {
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            try {
                while ((line = input.readLine()) != null)
                    System.out.println(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        p.waitFor();

        //ImageConverter.convertAndSavePngToBmp("/home/cooper/idea/sounimg/src/main/resources/img/gallery_icon1.png", "bmp.bmp");


    }

}
