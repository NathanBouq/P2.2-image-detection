# P2.2-image-detection
---
## Overview
This project explores the ability to distinguish between **AI-generated images** and **human-created images** using deep learning models.  
We trained and evaluated **Convolutional Neural Networks (CNNs)** and **ResNet architectures** to compare performance, and also conducted a human accuracy study.
## Results
- **CNN model**: 65% accuracy  
- **ResNet model**: 77% accuracy  
- **Human baseline**: 64% accuracy (20 images, 87 participants)
### Running the app
- The binaries were compiled using java 17.0.5 LTS, make sure to use this or newer version.
#### Using gradle
If you have a build system like gradle installed, you can execute the following commands depending on the operating system:
#### Windows
1. `cd <path/to/project>`
1. `./gradlew run`
#### MacOS/Linux
1. `cd <path/to/project>`
1. `gradle run`
#### Running the Flask APIs
To run the Flask APIs that provide endpoints for connecting the JavaFX GUI to Python models and generating heatmaps, follow these steps:
1. Open a terminal or command prompt.
2. Run the connection flask API : python connection.py 
3. Open another terminal or command prompt. 
4. Run the heatmap flask API : python heatmap.py 
# P2.2-image-detection