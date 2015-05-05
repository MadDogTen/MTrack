package program.graphics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {
    private static ImageIcon imageIcon;
    private static BufferedImage bufferedImage;

    public static void bufferImageToIcon(String path) {
        File file = new File(path);
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        imageIcon = new ImageIcon(bufferedImage);
    }

    public static ImageIcon getIcon(String path){
        bufferImageToIcon(path);
        return imageIcon;
    }
}