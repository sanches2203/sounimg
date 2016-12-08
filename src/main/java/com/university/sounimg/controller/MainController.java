package com.university.sounimg.controller;

import com.university.sounimg.common.ImageScanner;
import com.university.sounimg.security.Keys;
import com.university.sounimg.util.ApplicationConstants;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController implements Initializable {

    @FXML
    private Label lblInfoEncrypt;
    @FXML
    private Label lblInfoDecrypt;
    @FXML
    private ProgressBar prgBarEncrypt;
    @FXML
    private ProgressBar prgBarDecrypt;
    @FXML
    private ProgressIndicator prgIndicatorEncrypt;
    @FXML
    private ProgressIndicator prgIndicatorDecrypt;
    @FXML
    private TextField tfdEncryptXo;
    @FXML
    private TextField tfdEncryptYo;
    @FXML
    private TextField tfdEncryptZo;
    @FXML
    private TextField tfdEncryptA;
    @FXML
    private TextField tfdEncryptB;
    @FXML
    private TextField tfdDecryptXo;
    @FXML
    private TextField tfdDecryptYo;
    @FXML
    private TextField tfdDecryptZo;
    @FXML
    private TextField tfdDecryptA;
    @FXML
    private TextField tfdDecryptB;
    @FXML
    private ImageView imgViewEncrypt;
    @FXML
    private ImageView imgViewDecrypt;
    @FXML
    private Button btnOpenEncrypt;
    @FXML
    private Button btnOpenDecrypt;
    @FXML
    private Button btnEncrypt;
    @FXML
    private Button btnDecrypt;
    @FXML
    private Button btnSaveEncrypt;
    @FXML
    private Button btnSaveDecrypt;

    private FileChooser openFileChooser, saveFileChooser;
    private File selectedFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openFileChooser = new FileChooser();
        openFileChooser.setTitle("Open Image");
        openFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Image Files", ApplicationConstants.IMAGES_EXTENSIONS));
        saveFileChooser = new FileChooser();
        saveFileChooser.setTitle("Save Image");
        saveFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Image File", ApplicationConstants.IMAGES_EXTENSIONS_SAVE_PNG));
    }

    public void clickOpenEncrypt() {
        selectedFile = openFileChooser.showOpenDialog(btnEncrypt.getParent().getScene().getWindow());
        if (selectedFile != null) {
            imgViewEncrypt.imageProperty().unbind();
            imgViewEncrypt.setImage(new Image(selectedFile.toURI().toString()));
            disableEncryptFields();
            btnEncrypt.setDisable(false);
        }
    }

    public void clickEncrypt() throws ExecutionException, InterruptedException {
        if (!tfdEncryptXo.getText().matches(ApplicationConstants.PATTERN_NUMB)
                || !tfdEncryptYo.getText().matches(ApplicationConstants.PATTERN_NUMB) || !tfdEncryptZo.getText().matches(ApplicationConstants.PATTERN_NUMB) ||
                !tfdEncryptA.getText().matches(ApplicationConstants.PATTERN_NUMB) || !tfdEncryptB.getText().matches(ApplicationConstants.PATTERN_NUMB) ||
                !isValueOfRange(tfdEncryptA.getText(), -0.6, 0.6) || !isValueOfRange(tfdEncryptB.getText(), 0.8, 2.5) ||
                !isValueOfRange(tfdEncryptXo.getText(), -20, 20) || !isValueOfRange(tfdEncryptYo.getText(), -20, 20) ||
                !isValueOfRange(tfdEncryptZo.getText(), -20, 20)) {
            JOptionPane.showMessageDialog(null, "Введено некоректні дані",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
        } else {
            double x = Double.parseDouble(tfdEncryptXo.getText());
            double y = Double.parseDouble(tfdEncryptYo.getText());
            double z = Double.parseDouble(tfdEncryptZo.getText());
            double a = Double.parseDouble(tfdEncryptA.getText());
            double b = Double.parseDouble(tfdEncryptB.getText());
            Task<Image> imageScanner = new ImageScanner(selectedFile, x, y, z, a, b, true);

            prgIndicatorEncrypt.progressProperty().bind(imageScanner.progressProperty());
            prgBarEncrypt.progressProperty().bind(imageScanner.progressProperty());
            lblInfoEncrypt.textProperty().bind(imageScanner.messageProperty());

            tfdEncryptXo.disableProperty().bind(imageScanner.runningProperty());
            tfdEncryptYo.disableProperty().bind(imageScanner.runningProperty());
            tfdEncryptZo.disableProperty().bind(imageScanner.runningProperty());
            tfdEncryptA.disableProperty().bind(imageScanner.runningProperty());
            tfdEncryptB.disableProperty().bind(imageScanner.runningProperty());

            btnOpenEncrypt.disableProperty().bind(imageScanner.runningProperty());
            btnEncrypt.disableProperty().bind(imageScanner.runningProperty());
            btnSaveEncrypt.disableProperty().bind(imageScanner.runningProperty());

            imgViewEncrypt.imageProperty().bind(imageScanner.valueProperty());

            new Thread(imageScanner).start();
        }
    }

    public void clickSaveEncrypt() {
        File file = saveFileChooser.showSaveDialog(btnDecrypt.getParent().getScene().getWindow());
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(imgViewEncrypt.getImage(),
                        null), "png", file);

                Keys keys = new Keys(tfdEncryptXo.getText(), tfdEncryptYo.getText(), tfdEncryptZo.getText(), tfdEncryptA.getText(), tfdEncryptB.getText());
                String filePath = FilenameUtils.getFullPath(file.getAbsolutePath());
                FileOutputStream fileOutputStream = new FileOutputStream(filePath + FilenameUtils.getBaseName(file.getName()) + ".tmp");
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(keys);
                objectOutputStream.flush();
                objectOutputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(FileChooser.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    public void clickOpenDecrypt() {

        selectedFile = openFileChooser.showOpenDialog(btnDecrypt.getParent().getScene().getWindow());
        FileInputStream fileInputStream;
        if (selectedFile != null) {
            imgViewDecrypt.imageProperty().unbind();
            imgViewDecrypt.setImage(new Image(selectedFile.toURI().toString()));
            tfdDecryptXo.setText("");
            tfdDecryptYo.setText("");
            tfdDecryptZo.setText("");
            tfdDecryptA.setText("");
            tfdDecryptB.setText("");
            try {
                String filePath = FilenameUtils.getFullPath(selectedFile.getAbsolutePath());
                fileInputStream = new FileInputStream(filePath + FilenameUtils.getBaseName(selectedFile.getName()) + ".tmp");
                ObjectInputStream objectOutputStream = new ObjectInputStream(fileInputStream);
                Keys keys = (Keys) objectOutputStream.readObject();
                tfdDecryptXo.setText(String.valueOf(keys.getKeyX()));
                tfdDecryptYo.setText(String.valueOf(keys.getKeyY()));
                tfdDecryptZo.setText(String.valueOf(keys.getKeyZ()));
                tfdDecryptA.setText(String.valueOf(keys.getKeyA()));
                tfdDecryptB.setText(String.valueOf(keys.getKeyB()));
                btnDecrypt.setDisable(false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Файл з ключами не знайдено, або його пошкоджено. Введіть ключі вручну",
                        "Помилка", JOptionPane.ERROR_MESSAGE);
                tfdDecryptXo.setDisable(false);
                tfdDecryptYo.setDisable(false);
                tfdDecryptZo.setDisable(false);
                tfdDecryptA.setDisable(false);
                tfdDecryptB.setDisable(false);
                btnDecrypt.setDisable(false);
            }
        }
    }

    public void clickDecrypt() throws Exception {
        if (!tfdDecryptXo.getText().matches(ApplicationConstants.PATTERN_NUMB)
                || !tfdDecryptYo.getText().matches(ApplicationConstants.PATTERN_NUMB) || !tfdDecryptZo.getText().matches(ApplicationConstants.PATTERN_NUMB) ||
                !tfdDecryptA.getText().matches(ApplicationConstants.PATTERN_NUMB) || !tfdDecryptB.getText().matches(ApplicationConstants.PATTERN_NUMB) ||
                !isValueOfRange(tfdDecryptA.getText(), -0.6, 0.6) || !isValueOfRange(tfdDecryptB.getText(), 0.8, 2.5) ||
                !isValueOfRange(tfdDecryptXo.getText(), -20, 20) || !isValueOfRange(tfdDecryptYo.getText(), -20, 20) ||
                !isValueOfRange(tfdDecryptZo.getText(), -20, 20)) {
            JOptionPane.showMessageDialog(null, "Введено некоректні дані",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
        } else {
            double x = Double.parseDouble(tfdDecryptXo.getText());
            double y = Double.parseDouble(tfdDecryptYo.getText());
            double z = Double.parseDouble(tfdDecryptZo.getText());
            double a = Double.parseDouble(tfdDecryptA.getText());
            double b = Double.parseDouble(tfdDecryptB.getText());
            Task<Image> imageScanner = new ImageScanner(selectedFile, x, y, z, a, b, false);
            prgIndicatorDecrypt.progressProperty().bind(imageScanner.progressProperty());
            prgBarDecrypt.progressProperty().bind(imageScanner.progressProperty());
            lblInfoDecrypt.textProperty().bind(imageScanner.messageProperty());

            tfdDecryptXo.disableProperty().bind(imageScanner.runningProperty());
            tfdDecryptYo.disableProperty().bind(imageScanner.runningProperty());
            tfdDecryptZo.disableProperty().bind(imageScanner.runningProperty());
            tfdDecryptA.disableProperty().bind(imageScanner.runningProperty());
            tfdDecryptB.disableProperty().bind(imageScanner.runningProperty());

            btnOpenDecrypt.disableProperty().bind(imageScanner.runningProperty());
            btnDecrypt.disableProperty().bind(imageScanner.runningProperty());
            btnSaveDecrypt.disableProperty().bind(imageScanner.runningProperty());
            imgViewDecrypt.imageProperty().bind(imageScanner.valueProperty());

            new Thread(imageScanner).start();

        }
    }

    public void clickSaveDecrypt() {
        saveFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Image File", ApplicationConstants.IMAGES_EXTENSIONS_SAVE_JPEG));
        saveFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Image File", ApplicationConstants.IMAGES_EXTENSIONS_SAVE_JPG));
        File file = saveFileChooser.showSaveDialog(btnDecrypt.getParent().getScene().getWindow());
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(imgViewDecrypt.getImage(),
                        null), "png", file);
            } catch (IOException ex) {
                Logger.getLogger(FileChooser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void disableEncryptFields() {
        tfdEncryptXo.setDisable(false);
        tfdEncryptYo.setDisable(false);
        tfdEncryptZo.setDisable(false);
        tfdEncryptA.setDisable(false);
        tfdEncryptB.setDisable(false);
    }

    private boolean isValid(String string) {
        Pattern pattern = Pattern.compile(ApplicationConstants.PATTERN_NUMB);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    private boolean isValueOfRange(String string, double min, double max) {
        double v = Double.parseDouble(string);
        if (v <= max && v >= min) {
            return true;
        } else {
            return false;
        }
    }

}