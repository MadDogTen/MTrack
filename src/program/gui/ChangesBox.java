package program.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import program.graphics.ImageLoader;
import program.information.ChangeReporter;
import program.input.MoveWindow;

public class ChangesBox {

    public void display(String title, String[] messages, Window oldWindow) {
        Stage window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        final VBox[] vBox = {new VBox()};
        for (String aMessage : messages) {
            Label label = new Label();
            label.setText(aMessage);
            vBox[0].getChildren().add(label);
        }

        Button clear = new Button("Clear");
        Button close = new Button("Close");

        clear.setOnAction(e -> {
            ChangeReporter.resetChanges();
            vBox[0] = new VBox();
        });

        close.setOnAction(e -> {
            window.close();
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(clear, close);
        hBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox();
        layout.getChildren().addAll(hBox, vBox[0]);
        layout.setAlignment(Pos.CENTER);

        layout.setPadding(new Insets(6, 0, 0, 0));

        Scene scene = new Scene(layout);

        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window);
        });
        window.showAndWait();
    }

}
