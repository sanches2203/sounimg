package com.university.sounimg.controller.tabs.audio;

import com.university.sounimg.common.ConverterAudioToImage;
import com.university.sounimg.common.CommonAudio;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AudioControllerEncrypt implements Initializable {

    @FXML
    private Label lblInfo;
    @FXML
    private ProgressBar prgBarIndicator;
    @FXML
    private ProgressIndicator prgIndicator;
    @FXML
    private TextField tfdXo;
    @FXML
    private TextField tfdYo;
    @FXML
    private TextField tfdZo;
    @FXML
    private TextField tfdA;
    @FXML
    private TextField tfdB;
    @FXML
    private ImageView imgView;
    @FXML
    private Button btnOpen;
    @FXML
    private Button btnEncrypt;
    @FXML
    private Button btnSave;

    private FileChooser openFileChooser, saveFileChooser;

    double x, y, z, a, b;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cleanupTempFiles();
        openFileChooser = new FileChooser();
        openFileChooser.setTitle("Відкрити аудіо");
        openFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Audio files", ApplicationConstants.AUDIO_EXTENSIONS));
        saveFileChooser = new FileChooser();
        saveFileChooser.setTitle("Зберегти аудіо");
        saveFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Audio File", ApplicationConstants.AUDIO_EXTENSIONS_SAVE_WAV));
    }

    public void clickOpen() {
        cleanupTempFiles();
        File selectedFile = openFileChooser.showOpenDialog(btnEncrypt.getParent().getScene().getWindow());
        if (selectedFile != null) {
            Task<Image> audioConverter = new ConverterAudioToImage(selectedFile.getPath());
            imgView.imageProperty().bind(audioConverter.valueProperty());

            prgIndicator.progressProperty().bind(audioConverter.progressProperty());
            prgBarIndicator.progressProperty().bind(audioConverter.progressProperty());
            lblInfo.textProperty().bind(audioConverter.messageProperty());
            tfdXo.disableProperty().bind(audioConverter.runningProperty());
            tfdYo.disableProperty().bind(audioConverter.runningProperty());
            tfdZo.disableProperty().bind(audioConverter.runningProperty());
            tfdA.disableProperty().bind(audioConverter.runningProperty());
            tfdB.disableProperty().bind(audioConverter.runningProperty());

            btnOpen.disableProperty().bind(audioConverter.runningProperty());
            btnEncrypt.disableProperty().bind(audioConverter.runningProperty());
            new Thread(audioConverter).start();
        }
    }

    public void clickEncrypt() {
        if (!tfdXo.getText().matches(ApplicationConstants.PATTERN_NUMB)
                || !tfdYo.getText().matches(ApplicationConstants.PATTERN_NUMB) || !tfdZo.getText().matches(ApplicationConstants.PATTERN_NUMB) ||
                !tfdA.getText().matches(ApplicationConstants.PATTERN_NUMB) || !tfdB.getText().matches(ApplicationConstants.PATTERN_NUMB) ||
                !isValueOfRange(tfdA.getText(), -0.6, 0.6) || !isValueOfRange(tfdB.getText(), 0.8, 2.5) ||
                !isValueOfRange(tfdXo.getText(), -20, 20) || !isValueOfRange(tfdYo.getText(), -20, 20) ||
                !isValueOfRange(tfdZo.getText(), -20, 20)) {
            ApplicationConstants.showAlertErrorDialog("Введено невірні дані.", "Будь ласка перевірте їх, там спробуйте ще раз.");
        } else {
            x = Double.parseDouble(tfdXo.getText());
            y = Double.parseDouble(tfdYo.getText());
            z = Double.parseDouble(tfdZo.getText());
            a = Double.parseDouble(tfdA.getText());
            b = Double.parseDouble(tfdB.getText());
            Task<Image> imageScanner = new CommonAudio(new File("D:\\temp\\temp.png"), x, y, z, a, b, true);

            prgIndicator.progressProperty().bind(imageScanner.progressProperty());
            prgBarIndicator.progressProperty().bind(imageScanner.progressProperty());
            lblInfo.textProperty().bind(imageScanner.messageProperty());

            tfdXo.disableProperty().bind(imageScanner.runningProperty());
            tfdYo.disableProperty().bind(imageScanner.runningProperty());
            tfdZo.disableProperty().bind(imageScanner.runningProperty());
            tfdA.disableProperty().bind(imageScanner.runningProperty());
            tfdB.disableProperty().bind(imageScanner.runningProperty());

            btnOpen.disableProperty().bind(imageScanner.runningProperty());
            btnEncrypt.disableProperty().bind(imageScanner.runningProperty());
            btnSave.disableProperty().bind(imageScanner.runningProperty());

            imgView.imageProperty().bind(imageScanner.valueProperty());

            new Thread(imageScanner).start();
        }
    }

    public void clickSave() {
        File file = saveFileChooser.showSaveDialog(btnEncrypt.getParent().getScene().getWindow());
        if (file != null) {
            try {
                String filename = "D:\\temp\\" + FilenameUtils.getBaseName(file.getName()) + "_" +
                        imgView.getImage().getHeight() + "_" + imgView.getImage().getWidth() +
                        x + y + z + a + b;
                filename = filename.replaceAll("\\.", "_");
                copyFileUsingFileStreams(new File("D:\\temp\\temp.wav"), file);
                ImageIO.write(SwingFXUtils.fromFXImage(imgView.getImage(),
                        null), "png", new File(filename +  ".png"));
                System.out.printf(filename);
                Keys keys = new Keys(tfdXo.getText(), tfdYo.getText(), tfdZo.getText(), tfdA.getText(), tfdB.getText());
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

    private boolean isValueOfRange(String string, double min, double max) {
        double valueFromString = Double.parseDouble(string);
        return valueFromString <= max && valueFromString >= min;
    }

    private void copyFileUsingFileStreams(File source, File dest) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            if (output != null) {
                input.close();
                output.close();
            }
        }
    }

    private void cleanupTempFiles() {
        try {
            FileUtils.forceDelete(new File("D:\\temp\\temp.png"));
        } catch (IOException ignored) {}
        try {
            FileUtils.forceDelete(new File("D:\\temp\\temp.bmp"));
        } catch (IOException ignored) {}
        try {
            FileUtils.forceDelete(new File("D:\\temp\\temp.wav"));
        } catch (IOException ignored) {}
    }
}
