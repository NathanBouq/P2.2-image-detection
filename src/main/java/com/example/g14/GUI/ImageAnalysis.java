package com.example.g14.GUI;

import javafx.application.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the game. The main stage is created here. Contains some global variables<br>
 * used throughout the application. Extends the {@link Application} class.
 */
public class ImageAnalysis extends Application{
    /**
     * image window instance used
     */
    static Stage imageWindow;

    static final int WIDTH = 1440; // we can change that

    static final int HEIGHT = 900;

    /**
     * Sets up the Main Menu screen.
     * @param primaryStage stage used
     */
    @Override
    public void start(Stage primaryStage) {

        imageWindow = primaryStage;
        imageWindow.setTitle("Detect AI");
        imageWindow.setWidth(WIDTH);
        imageWindow.setHeight(HEIGHT);
        imageWindow.setFullScreen(true);

        MainMenu root = new MainMenu();

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        imageWindow.setScene(scene);
        
        imageWindow.show();
    }

    public static void main(String[] args) {

        launch(args);

    }

}
