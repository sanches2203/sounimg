package com.university.sounimg.application;

import com.university.sounimg.util.ApplicationConstants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage stage) throws Exception {
        log.debug("Starting application...");
        Parent root = FXMLLoader.load(getClass().getResource(ApplicationConstants.MAIN_FORM_LOCATION));
        Scene scene = new Scene(root);
        stage.setTitle("Sound-Image-Encrypt");
        stage.getIcons().add(new Image(ApplicationConstants.APPLICATION_ICON_PATH));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
        log.debug("Application started successfully...");
    }

    public static void main(String[] args) {
        launch(args);
    }

}
