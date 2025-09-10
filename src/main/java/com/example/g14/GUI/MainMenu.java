package com.example.g14.GUI;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * This class represents the main menu screen of the game.<br>
 * Extends the {@link Pane} class, allowing for use as a root node.
 */
public class MainMenu extends Pane {

    /**
     * Creates a main menu instance, setting up all the buttons and environment
     */
    MainMenu() {

        HoverButton uploadImage = new HoverButton("Upload Image");
        uploadImage.setLayoutX(100);
        uploadImage.setLayoutY(100);
        uploadImage.setOnAction(e -> {

            ImageAnalysis.imageWindow.getScene().setRoot(new UploadImage(ImageAnalysis.imageWindow));

            // UploadImage uploadScreen = new UploadImage(primaryStage);
            // Scene upload = new Scene(uploadScreen);
            // primaryStage.setScene(upload);
        });

        HoverButton information = new HoverButton("Information");
        information.setLayoutX(100);
        information.setLayoutY(300);
        information.setOnAction(e -> {

            ImageAnalysis.imageWindow.getScene().setRoot(new Information());

        });

        HoverButton exit = new HoverButton("Exit");
        exit.setLayoutX(100);
        exit.setLayoutY(500);
        exit.setOnAction(e -> {
            System.exit(0);
        });

        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        getChildren().addAll(uploadImage, information, exit);

    }
    
}

