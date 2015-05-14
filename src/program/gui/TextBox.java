package program.gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import program.information.ProgramSettingsController;
import program.information.UserInfoController;
import program.input.MoveWindow;
import program.io.FileManager;

import java.io.File;
import java.util.ArrayList;


public class TextBox {

    public String display(String title, String message, String messageIfNameFieldIsBlank, String defaultValue, Window oldWindow) {
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

        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window);
        });
        window.showAndWait();

        if (userName[0].isEmpty()) {
            return defaultValue;
        } else return userName[0];
    }

    private boolean isValid(String title, String messageIfBlank, String message, Window oldWindow) {
        if (message.isEmpty()) {
            ConfirmBox confirmBox = new ConfirmBox();
            return confirmBox.display(title, messageIfBlank, oldWindow);
        } else if (message.contentEquals("Add New Username") || !message.matches("^[a-zA-Z0-9]+$")) {
            MessageBox messageBox = new MessageBox();
            messageBox.display("Try Again", "Username isn't valid.", oldWindow);
            return false;
        } else if (UserInfoController.getAllUsers().contains(message)) {
            MessageBox messageBox = new MessageBox();
            messageBox.display("Try Again", "Username already taken.", oldWindow);
            return false;
        } else if (message.length() > 20) {
            MessageBox messageBox = new MessageBox();
            messageBox.display("Try Again", "Username is too long.", oldWindow);
            return false;
        } else return true;
    }

    public File addDirectoriesDisplay(String title, String message, ArrayList<String> currentDirectories, String messageIfFieldIsBlank, String messageIfNotDirectory, Window oldWindow) {
        Stage window = new Stage();
        final File[] directories = new File[1];

        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        TextField textField = new TextField();
        textField.setPromptText("\\PathToDirectory\\Shows");

        Button submit = new Button("Submit");
        submit.setOnAction(event -> {
            if (isDirectoryValid(title, currentDirectories, messageIfFieldIsBlank, messageIfNotDirectory, textField.getText(), window)) {
                directories[0] = new File(textField.getText());
                window.close();
            } else textField.clear();
        });

        VBox layout = new VBox();
        layout.getChildren().addAll(label, textField, submit);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window);
        });
        new MoveWindow().moveWindow(window);
        window.showAndWait();

        return directories[0];
    }

    private boolean isDirectoryValid(String title, ArrayList<String> currentDirectories, String messageIfBlank, String messageIfNotDirectory, String message, Window oldWindow) {
        if (currentDirectories.contains(message)) {
            new MessageBox().display(title, "Directory is already added.", oldWindow);
            return false;
        } else if (FileManager.checkFolderExists(message)) {
            return true;
        } else if (!message.isEmpty()) {
            MessageBox messageBox = new MessageBox();
            messageBox.display(title, messageIfNotDirectory, oldWindow);
            return false;
        } else {
            MessageBox messageBox = new MessageBox();
            messageBox.display(title, messageIfBlank, oldWindow);
            return false;
        }
    }

    public int updateSpeed(String title, String message, String messageFieldIsBlank, int defaultValue, Window oldWindow) {
        Stage window = new Stage();
        final int[] userName = new int[1];

        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        TextField textField = new TextField();
        textField.setText(String.valueOf(ProgramSettingsController.getUpdateSpeed()));

        Button submit = new Button("Submit");
        submit.setOnAction(event -> {
            if (isUpdateSpeedValid(title, messageFieldIsBlank, textField.getText(), window)) {
                if (textField.getText().isEmpty()) {
                    userName[0] = defaultValue;
                } else userName[0] = Integer.parseInt(textField.getText());
                window.close();
            } else {
                textField.clear();
            }
        });

        VBox layout = new VBox();
        layout.getChildren().addAll(label, textField, submit);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window);
        });
        window.showAndWait();

        return userName[0];
    }

    private boolean isUpdateSpeedValid(String title, String messageIfBlank, String textFieldValue, Window oldWindow) {
        if (textFieldValue.isEmpty()) {
            ConfirmBox confirmBox = new ConfirmBox();
            return confirmBox.display(title, messageIfBlank, oldWindow);
        } else if (!textFieldValue.matches("^[0-9]+$") || Integer.parseInt(textFieldValue) < 10) {
            MessageBox messageBox = new MessageBox();
            messageBox.display("Try Again", "Must be a number greater than or equal to 10", oldWindow);
            return false;
        } else return true;
    }
}
