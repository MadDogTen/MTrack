package program.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import program.input.MoveWindow;

public class MessageBox {

    private final int Width = 130, Height = 60;
    private MoveWindow moveWindow = new MoveWindow();

    public void display(String title, String message, TabPane oldTabPane) {
        Stage window = new Stage();
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(Width);
        window.setMinHeight(Height);

        Label label = new Label();
        label.setText(message);

        Button close = new Button("Close");
        close.setMinHeight(20);
        close.setMinWidth(30);

        close.setOnAction(e -> {
            window.close();
        });

        VBox layout = new VBox();
        layout.getChildren().addAll(label, close);
        layout.setAlignment(Pos.CENTER);

        layout.setPadding(new Insets(6, 0, 0, 0));

        Scene scene = new Scene(layout);

        moveWindow.moveWindow(window, scene);

        window.setScene(scene);
        window.setX((oldTabPane.getScene().getWindow().getX() + oldTabPane.getWidth() / 2) - (Width / 2));
        window.setY((oldTabPane.getScene().getWindow().getY() + oldTabPane.getHeight() / 2) - (Height / 2));
        window.showAndWait();
    }

    public void display(String title, String message, Stage oldStage) {
        Stage window = new Stage();
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(Width);
        window.setMinHeight(Height);

        Label label = new Label();
        label.setText(message);

        Button close = new Button("Close");
        close.setMinHeight(20);
        close.setMinWidth(30);

        close.setOnAction(e -> {
            window.close();
        });

        VBox layout = new VBox();
        layout.getChildren().addAll(label, close);
        layout.setAlignment(Pos.CENTER);

        layout.setPadding(new Insets(6, 0, 0, 0));

        Scene scene = new Scene(layout);

        moveWindow.moveWindow(window, scene);

        window.setScene(scene);
        window.setX((oldStage.getScene().getWindow().getX() + oldStage.getWidth() / 2) - (Width / 2));
        window.setY((oldStage.getScene().getWindow().getY() + oldStage.getHeight() / 2) - (Height / 2));
        window.showAndWait();
    }

    public void display(String title, String message) {
        Stage window = new Stage();
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(Width);
        window.setMinHeight(Height);

        Label label = new Label();
        label.setText(message);

        Button close = new Button("Close");
        close.setMinHeight(20);
        close.setMinWidth(30);

        close.setOnAction(e -> {
            window.close();
        });

        VBox layout = new VBox();
        layout.getChildren().addAll(label, close);
        layout.setAlignment(Pos.CENTER);

        layout.setPadding(new Insets(6, 0, 0, 0));

        Scene scene = new Scene(layout);

        moveWindow.moveWindow(window, scene);

        window.setScene(scene);
        window.showAndWait();
    }
}
