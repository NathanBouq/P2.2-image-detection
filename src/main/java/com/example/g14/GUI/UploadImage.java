package com.example.g14.GUI;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import com.google.gson.Gson;

public class UploadImage extends Pane {

    private File selectedFile;
    private ImageView imageView;
    private ComboBox<String> whichModel;
    private Stage primaryStage;

    public UploadImage(Stage primaryStage) {

        this.primaryStage = primaryStage;
        imageView = new ImageView();
        imageView.setLayoutX(500);
        imageView.setLayoutY(150);

        Button selectImageButton = new Button("Upload Image");
        selectImageButton.setLayoutX(200);
        selectImageButton.setLayoutY(150);
        selectImageButton.setPrefWidth(250);
        selectImageButton.setPrefHeight(75);

        selectImageButton.setOnAction(e -> selectImage(primaryStage));

        Label modelLabel = new Label("Which model would you like to detect with?");
        modelLabel.setLayoutX(200);
        modelLabel.setLayoutY(250);
        modelLabel.setFont(new Font("Arial", 16)); 

        whichModel = new ComboBox<>();
        whichModel.getItems().addAll("CNN_art_dataset", "CNN_art_dataset_dropout", "CNN_art_dataset_FourLayers_dropout", "RNN_art_dataset", "RNN_shoe_dataset");
        whichModel.setLayoutX(200);
        whichModel.setLayoutY(280);

        Button detectAI = new Button("Detect AI");
        detectAI.setLayoutX(200);
        detectAI.setLayoutY(320);
        detectAI.setPrefWidth(250);
        detectAI.setPrefHeight(75);
        detectAI.setOnAction(e -> {
            String selectedModel = whichModel.getValue();
            if (selectedModel != null) {
                try {
                    detectAI(selectedModel);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                System.out.println("Please select a model first!");
            }
        });

        HoverButton back = new HoverButton("Back");
        back.setLayoutX(50);
        back.setLayoutY(800);
        back.setOnAction(e -> ImageAnalysis.imageWindow.getScene().setRoot(new MainMenu()));

        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        getChildren().addAll(selectImageButton, imageView, modelLabel, whichModel, detectAI, back);
    }

    private void selectImage(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        this.selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            displayImage(selectedFile);
        }
    }

    /**
     * displays an image using the image view from javaFX
     * @param file image file
     */
    private void displayImage(File file) {
        if (file != null) {
            try {
                Image image = new Image(new FileInputStream(file));

                double maxImageViewWidth = 700;
                double maxImageViewHeight = 700;
                double imageWidth = image.getWidth();
                double imageHeight = image.getHeight();

                double scale = Math.min(maxImageViewWidth / imageWidth, maxImageViewHeight / imageHeight);
                imageView.setImage(image);
                imageView.setFitWidth(imageWidth * scale);
                imageView.setFitHeight(imageHeight * scale);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * sends an image and model to a Flask server and receives a prediction
     * @param model the model used to predict
     * @throws IOException
     */
    private void detectAI(String model) throws IOException {
        if (selectedFile != null) {
            String boundary = "Boundary-" + System.currentTimeMillis();
            String LINE_FEED = "\r\n";
            String url = "http://127.0.0.1:5000/predict";

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Prepare request body
            try (OutputStream os = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true)) {

                // Add form field for model
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"model\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(model).append(LINE_FEED);
                writer.flush();

                // Add file part
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                        .append(selectedFile.getName()).append("\"").append(LINE_FEED);
                writer.append("Content-Type: ").append(Files.probeContentType(selectedFile.toPath())).append(LINE_FEED);
                writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();

                try (FileInputStream inputStream = new FileInputStream(selectedFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                }

                writer.append(LINE_FEED);
                writer.append("--").append(boundary).append("--").append(LINE_FEED);
                writer.flush();
            }

            // Get response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Response: " + response.toString());

                    // parse the JSON response directly
                    HashMap<String, Object> jsonResponse = new HashMap<>();
                    jsonResponse = new Gson().fromJson(response.toString(), jsonResponse.getClass());

                    // check if prediction key exists
                    if (jsonResponse.containsKey("prediction")) {
                        String prediction = jsonResponse.get("prediction").toString();
                        System.out.println("Prediction: " + prediction);

                        // Generate heatmap
                        File heatmapFile = generateHeatmap(selectedFile, model);

                        // Load heatmap image for display
                        Image heatmapImage = new Image(new FileInputStream(heatmapFile));

                        // Show result screen
                        ResultScreen resultScreen = new ResultScreen(primaryStage, selectedFile, heatmapImage, prediction);
                        primaryStage.getScene().setRoot(resultScreen);
                    } else {
                        System.out.println("Prediction key not found in JSON response");
                    }
                }
            } else {
                System.out.println("Request failed: " + connection.getResponseMessage());
            }

            // close connection
            connection.disconnect();
        } else {
            System.out.println("Select an image first!");
        }
    }

    private File generateHeatmap(File imageFile, String model) throws IOException {
        String boundary = "Boundary-" + System.currentTimeMillis();
        String LINE_FEED = "\r\n";
        String url = "http://127.0.0.1:5001/generate_heatmap";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        File heatmapFile = File.createTempFile("heatmap", ".png");

        try (OutputStream os = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true)) {

            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"model\"").append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
            writer.append(LINE_FEED).append(model).append(LINE_FEED);
            writer.flush();

            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"layer\"").append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
            writer.append(LINE_FEED).append("layer_name").append(LINE_FEED);  // Replace "layer_name" with the actual layer name you want to use
            writer.flush();

            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"")
                    .append(imageFile.getName()).append("\"").append(LINE_FEED);
            writer.append("Content-Type: ").append(Files.probeContentType(imageFile.toPath())).append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            try (FileInputStream inputStream = new FileInputStream(imageFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }

            writer.append(LINE_FEED);
            writer.append("--").append(boundary).append("--").append(LINE_FEED);
            writer.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(heatmapFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } else {
            System.out.println("Request failed: " + connection.getResponseMessage());
        }

        connection.disconnect();
        return heatmapFile;
    }
}
