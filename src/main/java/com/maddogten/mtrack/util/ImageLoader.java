package com.maddogten.mtrack.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

@SuppressWarnings("ClassWithoutLogger")
public class ImageLoader {
    @SuppressWarnings("SameParameterValue")
    public static Image getImage(String directory) {
        return new Image(directory);
    }

    public static void setIcon(Stage stage) {
        stage.getIcons().add(getImage(Variables.Logo));
    }
}