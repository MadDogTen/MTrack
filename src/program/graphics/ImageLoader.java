package program.graphics;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ImageLoader {
    private static final Logger log = Logger.getLogger(ImageLoader.class.getName());

    private static ImageIcon imageIcon;
    private static BufferedImage bufferedImage;

    public static void bufferImageToIcon(String path) {
        File file = new File(path);
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            log.severe(e.toString());
        }
        imageIcon = new ImageIcon(bufferedImage);
    }

    public static ImageIcon getIcon(String path) {
        bufferImageToIcon(path);
        return imageIcon;
    }

    public static Image getImage(String directory) {
        return new Image(directory);
    }

    public static void setIcon(Stage stage) {
        stage.getIcons().add(ImageLoader.getImage("/image/LogoChoice.png"));
    }
}