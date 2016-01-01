package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
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
    private final Stage[] textStages = new Stage[2];

    @SuppressWarnings("SameParameterValue")
    public String addUser(StringProperty message, StringProperty messageIfNameFieldIsBlank, String defaultValue, ArrayList<String> allUsers, Stage oldStage) {
        log.finest("TextBox display has been opened.");
        textStages[0] = new Stage();
        textStages[0].getIcons().add(GenericMethods.getImage(Variables.Logo));
        if (oldStage != null) textStages[0].initOwner(oldStage);
        textStages[0].initStyle(StageStyle.UNDECORATED);
        textStages[0].initModality(Modality.APPLICATION_MODAL);
        textStages[0].setMinWidth(250);
        Label label = new Label();
        label.textProperty().bind(message);
        TextField textField = new TextField();
        final String[] userName = new String[]{Strings.EmptyString};
        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(event -> {
            if (textField.getText().isEmpty() && new ConfirmBox().confirm(messageIfNameFieldIsBlank, oldStage)) {
                userName[0] = defaultValue;
                textStages[0].close();
            } else if (isUserValid(textField.getText(), allUsers, textStages[0])) {
                userName[0] = textField.getText();
                textStages[0].close();
            }
        });
        Button exitButton = new Button();
        exitButton.setText(Strings.ExitButtonText);
        exitButton.setOnAction(e -> {
            userName[0] = Strings.EmptyString;
            textStages[0].close();
        });
        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exitButton);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));
        buttonLayout.setSpacing(3);
        VBox layout = new VBox();
        layout.getChildren().addAll(label, textField, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        textStages[0].setScene(scene);
        Platform.runLater(() -> {
            if (textStages[0].getOwner() != null) {
                textStages[0].setX(textStages[0].getOwner().getX() + (textStages[0].getOwner().getWidth() / 2) - (textStages[0].getWidth() / 2));
                textStages[0].setY(textStages[0].getOwner().getY() + (textStages[0].getOwner().getHeight() / 2) - (textStages[0].getHeight() / 2));
            }
            new MoveStage().moveStage(layout, oldStage);
        });
        textStages[0].showAndWait();
        textStages[0] = null;
        return userName[0];
    }

    private boolean isUserValid(String user, ArrayList<String> allUsers, Stage oldStage) {
        log.finest("isUserValid has been called.");
        if (user.contentEquals(Strings.AddNewUsername.getValue()) || !user.matches("^[a-zA-Z0-9]+$")) {
            new MessageBox().message(new StringProperty[]{Strings.UsernameIsntValid}, oldStage);
            return false;
        } else if (allUsers.contains(user)) {
            new MessageBox().message(new StringProperty[]{Strings.UsernameAlreadyTaken}, oldStage);
            return false;
        } else if (user.length() > 20) {
            new MessageBox().message(new StringProperty[]{Strings.UsernameIsTooLong}, oldStage);
            return false;
        } else return true;
    }

    public File addDirectory(ArrayList<Directory> currentDirectories, Stage oldStage) {
        log.finest("TextBox addDirectory has been opened.");
        textStages[1] = new Stage();
        GenericMethods.setIcon(textStages[1]);
        if (oldStage != null) textStages[1].initOwner(oldStage);
        textStages[1].initStyle(StageStyle.UNDECORATED);
        textStages[1].initModality(Modality.APPLICATION_MODAL);
        textStages[1].setMinWidth(250);
        Label label = new Label();
        label.textProperty().bind(Strings.PleaseEnterShowsDirectory);
        TextField textField = new TextField();
        textField.setPromptText(Strings.FileSeparator + Strings.PathToDirectory.getValue() + Strings.FileSeparator + Strings.Shows.getValue());
        ArrayList<String> directoryPaths = new ArrayList<>();
        currentDirectories.forEach(aDirectory -> directoryPaths.add(String.valueOf(aDirectory.getDirectory())));
        final File[] directories = new File[1];
        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (isDirectoryValid(directoryPaths, textField.getText(), textStages[1])) {
                directories[0] = new File(textField.getText());
                textStages[1].close();
            }
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            directories[0] = new File(Strings.EmptyString);
            textStages[1].close();
        });
        HBox hBox = new HBox();
        hBox.getChildren().addAll(submit, exit);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(3, 0, 6, 0));
        hBox.setSpacing(3);
        VBox layout = new VBox();
        layout.getChildren().addAll(label, textField, hBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 6, 0, 6));
        Scene scene = new Scene(layout);
        textStages[1].setScene(scene);
        Platform.runLater(() -> {
            if (textStages[1].getOwner() != null) {
                textStages[1].setX(textStages[1].getOwner().getX() + (textStages[1].getOwner().getWidth() / 2) - (textStages[1].getWidth() / 2));
                textStages[1].setY(textStages[1].getOwner().getY() + (textStages[1].getOwner().getHeight() / 2) - (textStages[1].getHeight() / 2));
            }
            new MoveStage().moveStage(layout, oldStage);
            textStages[1].requestFocus();
        });
        textStages[1].showAndWait();
        textStages[1] = null;
        return directories[0];
    }

    private boolean isDirectoryValid(ArrayList<String> currentDirectories, String directory, Stage oldStage) {
        log.finest("isDirectoryValid has been called.");
        if (currentDirectories.contains(directory)) {
            new MessageBox().message(new StringProperty[]{Strings.DirectoryIsAlreadyAdded}, oldStage);
            return false;
        } else if (new FileManager().checkFolderExistsAndReadable(new File(directory))) return true;
        else if (directory.isEmpty()) {
            new MessageBox().message(new StringProperty[]{Strings.YouNeedToEnterADirectory}, oldStage);
            return false;
        } else {
            new MessageBox().message(new StringProperty[]{Strings.DirectoryIsInvalid}, oldStage);
            return false;
        }
    }
}