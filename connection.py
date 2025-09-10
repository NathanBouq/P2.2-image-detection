"""
For this project, we used a simple Flask application serving as an API endpoint to connect our javaFX GUI to
our python models saved as .keras files. With this setup an user can make predictions on images.
This app supports CNN and RNN models.
The route for this application is /predict, which handles POST requests to predict whether an uploaded image is AI-generated.
"""

import numpy as np
from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename
import os
import io
import tensorflow as tf
from PIL import Image

app = Flask(__name__)

# loads models from .keras file
def load_model(path):
    try:
        model = tf.keras.models.load_model(path)
        return model
    except Exception as e:
        print(f"Error loading model from {path}: {e}")
        return None

# paths to the .keras models
# model_CNN_art_dataset_path = '/Users/nathanbouquet/Desktop/Project-2.2_Group-14/src/main/java/com/example/g14/NeuralNetworks/SavedNetworks/ART_CNN2_save_at_20.keras'
model_CNN_art_dataset_path = '/Users/nathanbouquet/Downloads/ART_CNN_dropout_0-2_save2_at_30.keras'
# model_CNN_art_dataset_dropout_path = '/Users/nathanbouquet/Desktop/Project-2.2_Group-14/src/main/java/com/example/g14/NeuralNetworks/SavedNetworks/ART_CNN_dropout2_save_at_8.keras'
model_CNN_art_dataset_dropout_path = '/Users/nathanbouquet/Downloads/ART_CNN_dropout_0-2_save2_at_30.keras'
# model_CNN_art_dataset_FourLayers_dropout_path = '/Users/nathanbouquet/Desktop/Project-2.2_Group-14/src/main/java/com/example/g14/NeuralNetworks/SavedNetworks/ART_CNN_4Layers_Dropout_0-2_save_at_28.keras'
model_CNN_art_dataset_FourLayers_dropout_path = '/Users/nathanbouquet/Downloads/ART_CNN_4Layers_Dropout_0-2_save_at_28.keras'

# model_RNN_art_dataset_path = '/Users/nathanbouquet/Desktop/Project-2.2_Group-14/src/main/java/com/example/g14/NeuralNetworks/SavedNetworks/ART_ResNet_save_at_30_2.keras'
model_RNN_art_dataset_path = '/Users/nathanbouquet/Downloads/ART_ResNet_save_at_24_2.keras'
# model_RNN_shoe_dataset_path = '/Users/nathanbouquet/Desktop/Project-2.2_Group-14/src/main/java/com/example/g14/NeuralNetworks/SavedNetworks/Shoe_ResNet_save_at_11.keras'
model_RNN_shoe_dataset_path = '/Users/nathanbouquet/Downloads/Shoe_ResNet_save_at_11.keras'

# Load models
model_CNN_art_dataset = load_model(model_CNN_art_dataset_path)
model_CNN_art_dataset_dropout = load_model(model_CNN_art_dataset_dropout_path)
model_CNN_art_dataset_FourLayers_dropout = load_model(model_CNN_art_dataset_FourLayers_dropout_path)
model_RNN_art_dataset = load_model(model_RNN_art_dataset_path)
model_RNN_shoe_dataset = load_model(model_RNN_shoe_dataset_path)

@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        print('No file part in the request')
        return jsonify({'error': 'No file part'}), 400
    
    file = request.files['file']
    model = request.form.get('model')
    print('Received model: ' + model)

    if file.filename == '':
        print('No selected file')
        return jsonify({'error': 'No selected file'}), 400

    if file:
        try:
            img = Image.open(io.BytesIO(file.read()))
            img = img.resize((512, 512))  # resize to match model input size
            img = np.array(img)
            
            if img.shape[-1] == 4:
                img = img[..., :3]  # Ensure RGB channels
                
            img = np.expand_dims(img, axis=0)
            img = img / 255.0  # Normalize image
        except Exception as e:
            print(f"Error processing image: {e}")
            return jsonify({'error': 'Error processing image'}), 500

        if model == 'CNN_art_dataset' and model_CNN_art_dataset:
            prediction = model_CNN_art_dataset.predict(img)
            return jsonify({'prediction': prediction.tolist()})
        elif model == 'CNN_art_dataset_dropout' and model_CNN_art_dataset_dropout:
            prediction = model_CNN_art_dataset_dropout.predict(img)
            return jsonify({'prediction': prediction.tolist()})
        elif model == 'CNN_art_dataset_FourLayers_dropout' and model_CNN_art_dataset_FourLayers_dropout:
            prediction = model_CNN_art_dataset_FourLayers_dropout.predict(img)
            return jsonify({'prediction': prediction.tolist()})
        elif model == 'RNN_art_dataset' and model_RNN_art_dataset:
            prediction = model_RNN_art_dataset.predict(img)
            return jsonify({'prediction': prediction.tolist()})
        elif model == 'RNN_shoe_dataset' and model_RNN_shoe_dataset:
            prediction = model_RNN_shoe_dataset.predict(img)
            return jsonify({'prediction': prediction.tolist()})
        else:
            print('Invalid model selection or model not loaded')
            return jsonify({'error': 'Invalid model selection or model not loaded'}), 400

if __name__ == '__main__':
    app.run(debug=True, port=5000)