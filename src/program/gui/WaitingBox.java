package program.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import program.input.MoveWindow;

public class WaitingBox {

    Stage window;
    private MoveWindow moveWindow = new MoveWindow();

    public void display(String title, String message) {
        window = new Stage();
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Label label = new Label();
        label.setText(message);

        VBox layout = new VBox();
        layout.getChildren().addAll(label);
        layout.setAlignment(Pos.CENTER);

        layout.setPadding(new Insets(6, 6, 6, 6));

        Scene scene = new Scene(layout);

        moveWindow.moveWindow(window, scene);

        window.setScene(scene);
        window.show();
    }

    public void closeStage() {
        window.close();
    }
}
