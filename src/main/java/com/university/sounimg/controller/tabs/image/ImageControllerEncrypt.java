package com.university.sounimg.controller.tabs.image;

import com.university.sounimg.common.CommonImage;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageControllerEncrypt implements Initializable {

    @FXML private Label lblInfo;
    @FXML private ProgressBar prgBarIndicator;
    @FXML private ProgressIndicator prgIndicator;
    @FXML private TextField tfdXo;
    @FXML private TextField tfdYo;
    @FXML private TextField tfdZo;
    @FXML private TextField tfdA;
    @FXML private TextField tfdB;
    @FXML private ImageView imgView;
    @FXML private Button btnOpen;
    @FXML private Button btnEncrypt;
    @FXML private Button btnSave;

    private FileChooser openFileChooser, saveFileChooser;
    private File selectedFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openFileChooser = new FileChooser();
        openFileChooser.setTitle("Відкрити зображення");
        openFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Image Files", ApplicationConstants.IMAGES_EXTENSIONS));
        saveFileChooser = new FileChooser();
        saveFileChooser.setTitle("Зберегти зображення");
        saveFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Image File", ApplicationConstants.IMAGES_EXTENSIONS_SAVE_PNG));
    }

    public void clickOpen() {
        selectedFile = openFileChooser.showOpenDialog(btnEncrypt.getParent().getScene().getWindow());
        if (selectedFile != null) {
            imgView.imageProperty().unbind();
            imgView.setImage(new Image(selectedFile.toURI().toString()));
            disableTextFields();
            btnEncrypt.setDisable(false);
        }
    }

    public void clickEncrypt() {
        if (!tfdXo.getText().matches(ApplicationConstants.PATTERN_NUMB)
                || !tfdYo.getText().matches(ApplicationConstants.PATTERN_NUMB) || !tfdZo.getText().matches(ApplicationConstants.PATTERN_NUMB) ||
                !tfdA.getText().matches(ApplicationConstants.PATTERN_NUMB) || !tfdB.getText().matches(ApplicationConstants.PATTERN_NUMB) ||
                !isValueOfRange(tfdA.getText(), -0.6, 0.6) || !isValueOfRange(tfdB.getText(), 0.8, 2.5) ||
                !isValueOfRange(tfdXo.getText(), -20, 20) || !isValueOfRange(tfdYo.getText(), -20, 20) ||
                !isValueOfRange(tfdZo.getText(), -20, 20)) {
            ApplicationConstants.showAlertErrorDialog("Введено невірні дані.", "Будь ласка перевірте їх, там спробуйте ще раз." );
        } else {
            double x = Double.parseDouble(tfdXo.getText());
            double y = Double.parseDouble(tfdYo.getText());
            double z = Double.parseDouble(tfdZo.getText());
            double a = Double.parseDouble(tfdA.getText());
            double b = Double.parseDouble(tfdB.getText());
            Task<Image> imageScanner = new CommonImage(selectedFile, x, y, z, a, b, true);

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
                ImageIO.write(SwingFXUtils.fromFXImage(imgView.getImage(),
                        null), "png", file);

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

    private void disableTextFields() {
        tfdXo.setDisable(false);
        tfdYo.setDisable(false);
        tfdZo.setDisable(false);
        tfdA.setDisable(false);
        tfdB.setDisable(false);
    }

    private boolean isValueOfRange(String string, double min, double max) {
        double valueFromString = Double.parseDouble(string);
        return valueFromString <= max && valueFromString >= min;
    }
}
