package program.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import program.input.MoveWindow;

public class DoubleTextBox {
    MoveWindow moveWindow = new MoveWindow();

    public int[] displaySeasonEpisode(String title, String firstMessage, String secondMessage) {
        Stage window = new Stage();
        final int[] seasonEpisode = new int[2];

        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(firstMessage);
        label.setAlignment(Pos.CENTER);

        Label label1 = new Label();
        label1.setText(secondMessage);
        label1.setAlignment(Pos.CENTER);

        TextField textField = new TextField();
        textField.setMinSize(10.0, 5.0);
        textField.setMaxSize(50.0, 25.0);
        TextField textField1 = new TextField();
        textField1.setMinSize(10.0, 5.0);
        textField1.setMaxSize(50.0, 25.0);


        Button submit = new Button("Submit");
        submit.setOnAction(event -> {
            if (isValid(title, textField.getText(), textField1.getText())) {
                seasonEpisode[0] = Integer.parseInt(textField.getText());
                seasonEpisode[1] = Integer.parseInt(textField1.getText());
                window.close();
            } else {
                textField.clear();
                textField1.clear();
            }
        });

        VBox layout = new VBox();
        layout.getChildren().addAll(label, textField);
        layout.setPadding(new Insets(2.0, 2.0, 2.0, 0));
        layout.setAlignment(Pos.CENTER);

        VBox layout2 = new VBox();
        layout2.getChildren().addAll(label1, textField1);
        layout2.setPadding(new Insets(2.0, 0, 2.0, 2.0));
        layout2.setAlignment(Pos.CENTER);

        HBox layout3 = new HBox();
        layout3.getChildren().addAll(layout, layout2);
        layout3.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(layout3, submit);
        mainLayout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainLayout);

        moveWindow.moveWindow(window, scene);

        window.setScene(scene);
        window.showAndWait();

        return seasonEpisode;
    }

    private boolean isValid(String title, String messageOne, String messageTwo) {
        if (messageOne.isEmpty() || messageTwo.isEmpty()) {
            MessageBox messageBox = new MessageBox();
            messageBox.display(title, "Fields cannot be empty.");
            return false;
        } else if (!messageOne.matches("^[0-9]+$") || (!messageTwo.matches("^[0-9]+$"))) {
            MessageBox messageBox = new MessageBox();
            messageBox.display("Try Again", "Both fields need to be numbers");
            return false;
        } else return true;
    }
}
