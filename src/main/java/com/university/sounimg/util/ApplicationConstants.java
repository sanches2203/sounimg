package com.university.sounimg.util;

import javafx.scene.control.Alert;

public final class ApplicationConstants {

    public static final String[] IMAGES_EXTENSIONS = {"*.png", "*.jpeg", "*.jpg"};
    public static final String[] AUDIO_EXTENSIONS = {"*.wav"};
    public static final String AUDIO_EXTENSIONS_SAVE_WAV = "*.wav";
    public static final String IMAGES_EXTENSIONS_SAVE_PNG = "*.png";
    public static final String IMAGES_EXTENSIONS_SAVE_JPEG = "*.jpeg";
    public static final String IMAGES_EXTENSIONS_SAVE_JPG = "*.jpg";

    public static final String PATTERN_NUMB = "[+-]?\\d*\\.?\\d+";

    public final static String MAIN_FORM_LOCATION = "/fxml/MainForm.fxml";
    public final static String APPLICATION_ICON_PATH  = "/img/gallery_icon1.png";

    public final static String APPLICATION_ARRS_PATH = "D:\\temp\\arss\\";

    public static final double P = 0.2;
    public static final double Q = 0.1;
    public static final double R = 4.5;
    public static final double DT = 0.001;

    public static void showAlertErrorDialog(String headerError, String contentError) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(headerError);
        alert.setContentText(contentError);
        alert.showAndWait();
    }

}
