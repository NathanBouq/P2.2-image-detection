package com.example.g14.GUI;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.*;

public class ResultScreen extends Pane {
    
    private Stage primaryStage;

    public ResultScreen(Stage primaryStage, File imageFile, Image heatMap, String prediction) {
        this.primaryStage = primaryStage;
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        prediction = prediction.trim().substring(2, prediction.length()-2); // removes the brackets

        Label predictionLabel = new Label("The probability of this image being AI generated is: " + prediction + "%");
        predictionLabel.setLayoutX(200);
        predictionLabel.setLayoutY(50);
        predictionLabel.setFont(new Font("Arial", 30));

        Label imageLabel = new Label("Image predicted");
        imageLabel.setLayoutX(100);
        imageLabel.setLayoutY(150);
        imageLabel.setFont(new Font("Arial", 30));

        ImageView imageView = new ImageView();
        imageView.setLayoutX(100);
        imageView.setLayoutY(200);
        imageView.setFitWidth(500);
        imageView.setFitHeight(500);
        try {
            Image image = new Image(new FileInputStream(imageFile));
            imageView.setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Label heatMapLabel = new Label("Heat map of the image");
        heatMapLabel.setLayoutX(800);
        heatMapLabel.setLayoutY(150);
        heatMapLabel.setFont(new Font("Arial", 30));

        ImageView heatMapImage = new ImageView();
        heatMapImage.setLayoutX(800);
        heatMapImage.setLayoutY(200);
        heatMapImage.setFitWidth(500);
        heatMapImage.setFitHeight(500);
        Image heatmapImage = heatMap;
        heatMapImage.setImage(heatmapImage);

        HoverButton back = new HoverButton("Back");
        back.setLayoutX(50);
        back.setLayoutY(800);
        back.setOnAction(e -> ImageAnalysis.imageWindow.getScene().setRoot(new UploadImage(primaryStage)));

        getChildren().addAll(predictionLabel, imageLabel, imageView, heatMapLabel, heatMapImage, back);
    }
}
