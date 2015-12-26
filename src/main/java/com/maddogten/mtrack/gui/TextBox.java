package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.ImageLoader;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
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
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

/*
      TextBox handles multiple stages which allow the user to type their input. They check that the input is valid for the given request, and if it is, returns it.
      addUser- Allows the user to give a username, then checks it against isUserValid(), and if it is, returns the username. If the username is empty, The user name pick
      to use the default username, or enter one. If the username is invalid, The reason why is displayed, and allows the user to correct the issue.
      addDirectory- Allows the user to add a directory, then checks that the directory isn't already added, exists, and is otherwise valid.
 */

public class TextBox {
    private static final Logger log = Logger.getLogger(TextBox.class.getName());

    @SuppressWarnings("SameParameterValue")
    public String addUser(StringProperty message, StringProperty messageIfNameFieldIsBlank, String defaultValue, ArrayList<String> allUsers, Window oldWindow) {
        log.finest("TextBox display has been opened.");
        Stage stage = new Stage();
        stage.getIcons().add(ImageLoader.getImage(Variables.Logo));
        final String[] userName = new String[1];

        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);

        Label label = new Label();
        label.textProperty().bind(message);

        TextField textField = new TextField();
        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(event -> {
            if (isUserValid(messageIfNameFieldIsBlank, textField.getText(), allUsers, stage)) {
                userName[0] = textField.getText();
                stage.close();
            }
        });

        VBox layout = new VBox();
        layout.getChildren().addAll(label, textField, submit);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        stage.setScene(scene);
        Platform.runLater(() -> {
            if (oldWindow != null) {
                stage.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (stage.getWidth() / 2));
                stage.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (stage.getHeight() / 2));
            }
            new MoveStage().moveWindow(stage, oldWindow);
        });
        stage.showAndWait();

        if (userName[0].isEmpty()) {
            return defaultValue;
        } else return userName[0];
    }

    private boolean isUserValid(StringProperty messageIfBlank, String user, ArrayList<String> allUsers, Window oldWindow) {
        log.finest("isUserValid has been called.");
        if (user.isEmpty()) {
            return new ConfirmBox().display(messageIfBlank, oldWindow);
        } else if (user.contentEquals(Strings.AddNewUsername.getValue()) || !user.matches("^[a-zA-Z0-9]+$")) {
            new MessageBox().display(new StringProperty[]{Strings.UsernameIsntValid}, oldWindow);
            return false;
        } else if (allUsers.contains(user)) {
            new MessageBox().display(new StringProperty[]{Strings.UsernameAlreadyTaken}, oldWindow);
            return false;
        } else if (user.length() > 20) {
            new MessageBox().display(new StringProperty[]{Strings.UsernameIsTooLong}, oldWindow);
            return false;
        } else return true;
    }

    @SuppressWarnings("SameParameterValue")
    public File addDirectory(ArrayList<Directory> currentDirectories, Window oldWindow) {
        log.finest("TextBox addDirectory has been opened.");
        Stage stage = new Stage();
        ImageLoader.setIcon(stage);
        final File[] directories = new File[1];

        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);

        Label label = new Label();
        label.textProperty().bind(Strings.PleaseEnterShowsDirectory);

        TextField textField = new TextField();
        textField.setPromptText(Strings.FileSeparator + Strings.PathToDirectory.getValue() + Strings.FileSeparator + Strings.Shows.getValue());

        ArrayList<String> directoryPaths = new ArrayList<>();
        currentDirectories.forEach(aDirectory -> directoryPaths.add(String.valueOf(aDirectory.getDirectory())));

        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (isDirectoryValid(directoryPaths, textField.getText(), stage)) {
                directories[0] = new File(textField.getText());
                stage.close();
            }
        });

        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            directories[0] = new File(Strings.EmptyString);
            stage.close();
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(submit, exit);
        hBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, textField, hBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        stage.setScene(scene);
        Platform.runLater(() -> {
            if (oldWindow != null) {
                stage.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (stage.getWidth() / 2));
                stage.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (stage.getHeight() / 2));
            }
            new MoveStage().moveWindow(stage, oldWindow);
            stage.requestFocus();
        });
        stage.showAndWait();

        return directories[0];
    }

    private boolean isDirectoryValid(ArrayList<String> currentDirectories, String directory, Window oldWindow) {
        log.finest("isDirectoryValid has been called.");
        if (currentDirectories.contains(directory)) {
            new MessageBox().display(new StringProperty[]{Strings.DirectoryIsAlreadyAdded}, oldWindow);
            return false;
        } else if (new FileManager().checkFolderExistsAndReadable(new File(directory))) {
            return true;
        } else if (directory.isEmpty()) {
            new MessageBox().display(new StringProperty[]{Strings.YouNeedToEnterADirectory}, oldWindow);
            return false;
        } else {
            new MessageBox().display(new StringProperty[]{Strings.DirectoryIsInvalid}, oldWindow);
            return false;
        }
    }
}
