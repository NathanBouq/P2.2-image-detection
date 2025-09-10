package com.example.g14.GUI;

import javafx.scene.control.Button;

/**
 * Represents a specific type of button we use within the game.
 * Has custom behaviour, extends the {@link Button} class.
 */
class HoverButton extends Button {

    /**
     * determines whether the cursor has entered a button area, button styling
     */
    private boolean mouseEntered = false;

    private String defaultButtonStyle = "-fx-font-size: 60; -fx-padding: 0;";
    private String hoveredStyle = "-fx-font-size: 70; -fx-padding: 0; -fx-text-fill: #D5FBC5;";

    /**
     * Creates a HoverButton instance.
     * @param text  will be displayed on the button
     */
    HoverButton(String text) {

        setText(text);
        setStyle(defaultButtonStyle);
        setBackground(null);


        setOnMouseEntered(ev -> {
            mouseEntered = true;
            buttonHoverEffect();

        });
        setOnMouseExited(ev -> {

            mouseEntered = false;
            buttonHoverEffect();

        });

    }


    /**
     * Changes the button appearance depending on the mouseEntered field
     */
    private void buttonHoverEffect() {

        if (mouseEntered) {

            setStyle(hoveredStyle);

        } else setStyle(defaultButtonStyle);

    }

    public String getHoveredStyle() {
        return hoveredStyle;
    }

    public void lock() {
        setOnMouseExited(e -> {});
        setOnMouseEntered(ev -> {});
    }

}