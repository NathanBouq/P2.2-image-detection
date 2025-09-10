package com.example.g14.GUI;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Information extends Pane {

    private HoverButton back;

    public Information() {

        Text infoText = new Text("Project 2.2 Group 14\n\nMembers: \n- Nathan Bouquet \n- Achilleas Leivadiotis \n- Alex Matlok \n- Esteban Salmeron Komolova \n- Lisa Theis \n- Nick Vroutsis");
        infoText.setStyle("-fx-font-size: 24px; -fx-padding: 24px;");
        infoText.setLayoutX(50);
        infoText.setLayoutY(50);

        back = new HoverButton("Back");
        back.setLayoutX(50);
        back.setLayoutY(800);
        back.setOnAction(e -> {

            ImageAnalysis.imageWindow.getScene().setRoot(new MainMenu());
        });

        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        getChildren().addAll(infoText, back);

    }
}