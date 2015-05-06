package program.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AboutBox {
    public void display() throws Exception {
        Stage window = new Stage();
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        Pane root = FXMLLoader.load(getClass().getResource("/gui/About.fxml"));

        window.setTitle("MTrack");

        assert root != null;
        Scene scene = new Scene(root);

        scene.setFill(Color.WHITESMOKE);

        window.setResizable(false);
        window.setScene(scene);
        window.showAndWait();
    }
}
