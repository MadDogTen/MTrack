package program.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

@SuppressWarnings("ClassWithoutLogger")
public class ImageLoader {

    /*private static ImageIcon imageIcon;
    private static BufferedImage bufferedImage;

    private static void bufferImageToIcon(String path) {
        File file = new File(path);
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            log.severe(e.toString());
        }
        imageIcon = new ImageIcon(bufferedImage);
    }

    @SuppressWarnings("unused")
    public static ImageIcon getIcon(String path) {
        bufferImageToIcon(path);
        return imageIcon;
    }*/

    @SuppressWarnings("SameParameterValue")
    public static Image getImage(String directory) {
        return new Image(directory);
    }

    public static void setIcon(Stage stage) {
        stage.getIcons().add(getImage(Variables.Logo));
    }
}