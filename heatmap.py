import matplotlib
matplotlib.use('Agg')  # Use Agg backend

from flask import Flask, request, send_file, Response
import tensorflow as tf
import numpy as np
from tensorflow.keras.preprocessing import image
from scipy.ndimage import zoom
import matplotlib.pyplot as plt
import io

app = Flask(__name__)

model_path = '/Users/nathanbouquet/Desktop/Project-2.2_Group-14/src/main/java/com/example/g14/NeuralNetworks/SavedNetworks/ART_ResNet_save_at_20.keras'

# Function for heatmap generation
def do_heatmap(img_path, model_path, layer_name):
    try:
        model_path = '/Users/nathanbouquet/Desktop/Project-2.2_Group-14/src/main/java/com/example/g14/NeuralNetworks/SavedNetworks/ART_ResNet_save_at_20.keras'
        model = tf.keras.models.load_model(model_path)
        model.build(input_shape=(None, 512, 512, 3))

        img = image.load_img(img_path, target_size=(512, 512))
        x = image.img_to_array(img)
        x = np.expand_dims(x, axis=0)
        x = x / 255.0

        last_conv_layer = model.get_layer(layer_name).output
        pred_layer = model.layers[-1].output
        intermediate_layer_model = tf.keras.models.Model(inputs=model.inputs, outputs=[last_conv_layer, pred_layer])
        conv, pred = intermediate_layer_model.predict(x)

        if not isinstance(pred, np.ndarray) or pred.shape[0] == 0:
            raise ValueError("Prediction output is empty or not an ndarray.")

        target = np.argmax(pred, axis=1).squeeze()
        w, b = model.layers[-1].get_weights()
        weight = w[:, target]

        heatmap = conv.squeeze(axis=0)
        heatmap_2d = np.mean(heatmap, axis=-1)

        scale_x = 512 / heatmap_2d.shape[1]
        scale_y = 512 / heatmap_2d.shape[0]
        zoom_tuple = (scale_y, scale_x)

        # Save heatmap to a file without displaying it
        heatmap_path = os.path.join(app.root_path, 'heatmap.png')
        plt.figure(figsize=(12, 12))
        plt.imshow(img)
        plt.imshow(zoom(heatmap_2d, zoom=zoom_tuple), cmap='jet', alpha=0.5)
        plt.axis('off')  # Remove axis
        plt.tight_layout(pad=0)
        plt.savefig(heatmap_path)
        plt.close()  # Close the figure to release resources

        return heatmap_path

    except Exception as e:
        print(f"Error in do_heatmap function: {e}")
        raise e  # Propagate the exception for further debugging


# Route for generating heatmap
@app.route('/generate_heatmap', methods=['POST'])
def generate_heatmap():
    try:
        if 'image' not in request.files:
            return "No image file", 400
        if 'model' not in request.form or 'layer' not in request.form:
            return "Model or layer name missing", 400
        
        image_file = request.files['image']
        model_path = request.form['model']
        layer_name = 'dense'
        
        img_path = 'temp_image.png'
        image_file.save(img_path)
        
        heatmap_bytes = do_heatmap(img_path, model_path, layer_name)
        
        return Response(heatmap_bytes, mimetype='image/png')
    
    except Exception as e:
        print(f"Error in generate_heatmap endpoint: {e}")
        return "Internal Server Error", 500

if __name__ == '__main__':
    app.run(debug=True, port=5001)
