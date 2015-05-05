package program.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import program.information.UserInfoController;
import program.input.MoveWindow;
import program.io.FileManager;

import java.io.File;


public class TextBox {

    MoveWindow moveWindow = new MoveWindow();

    public String display(String title, String message, String messageIfNameFieldIsBlank, String defaultValue) {
        Stage window = new Stage();
        final String[] userName = new String[1];

        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        TextField textField = new TextField();

        Button submit = new Button("Submit");
        submit.setOnAction(event -> {
            if (isValid(title, messageIfNameFieldIsBlank, textField.getText(), window)) {
                userName[0] = textField.getText();
                window.close();
            } else {
                textField.clear();
            }
        });

        VBox layout = new VBox();
        layout.getChildren().addAll(label, textField, submit);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        moveWindow.moveWindow(window, scene);

        window.setScene(scene);
        window.showAndWait();

        if (userName[0].isEmpty()) {
            return defaultValue;
        } else return userName[0];
    }

    private boolean isValid(String title, String messageIfBlank, String message, Stage oldStage) {
        if (message.isEmpty()) {
            ConfirmBox confirmBox = new ConfirmBox();
            return confirmBox.display(title, messageIfBlank, oldStage);
        } else if (message.contentEquals("Add New Username") || message.contentEquals("Program") || !message.matches("^[a-zA-Z0-9]+$")) {
            MessageBox messageBox = new MessageBox();
            messageBox.display("Try Again", "Username isn't valid.");
            return false;
        } else if (UserInfoController.getAllUsers().contains(message)) {
            MessageBox messageBox = new MessageBox();
            messageBox.display("Try Again", "Username already taken.");
            return false;
        } else if (message.length() > 20) {
            MessageBox messageBox = new MessageBox();
            messageBox.display("Try Again", "Username is too long.");
            return false;
        } else return true;
    }

    public File addDirectoriesDisplay(String title, String message, String messageIfFieldIsBlank, String messageIfNotDirectory) {
        Stage window = new Stage();
        final File[] directories = new File[1];

        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        TextField textField = new TextField();
        textField.setPromptText("Test123");

        Button submit = new Button("Submit");
        submit.setOnAction(event -> {
            if (isDirectoryValid(title, messageIfFieldIsBlank, messageIfNotDirectory, textField.getText(), window)) {
                directories[0] = new File(textField.getText());
                window.close();
            } else textField.clear();
        });

        VBox layout = new VBox();
        layout.getChildren().addAll(label, textField, submit);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        moveWindow.moveWindow(window, scene);

        window.setScene(scene);
        window.showAndWait();

        return directories[0];
    }

    private boolean isDirectoryValid(String title, String messageIfBlank, String messageIfNotDirectory, String message, Stage oldStage) {
        if (FileManager.checkFolderExists(message)) {
            return true;
        } else if (!message.isEmpty()) {
            MessageBox messageBox = new MessageBox();
            messageBox.display(title, messageIfNotDirectory, oldStage);
            return false;
        } else {
            MessageBox messageBox = new MessageBox();
            messageBox.display(title, messageIfBlank, oldStage);
            return false;
        }

    }
}
