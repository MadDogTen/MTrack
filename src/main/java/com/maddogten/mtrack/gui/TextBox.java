package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    public String addUser(StringProperty message, StringProperty messageIfNameFieldIsBlank, String defaultValue, ArrayList<String> allUsers, Stage oldStage) {
        log.fine("addUser display has been opened.");

        Stage addUserStage = new Stage();
        if (oldStage != null) addUserStage.initOwner(oldStage);
        addUserStage.initStyle(StageStyle.UNDECORATED);
        addUserStage.initModality(Modality.APPLICATION_MODAL);
        addUserStage.setMinWidth(250);
        GenericMethods.setIcon(addUserStage);

        Label label = new Label();
        label.textProperty().bind(message);

        TextField textField = new TextField();
        final String[] userName = new String[]{Strings.EmptyString};
        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(event -> {
            if (textField.getText().isEmpty() && new ConfirmBox().confirm(messageIfNameFieldIsBlank, oldStage)) {
                userName[0] = defaultValue;
                addUserStage.close();
            } else if (isUserValid(textField.getText(), allUsers, addUserStage)) {
                userName[0] = textField.getText();
                addUserStage.close();
            }
        });
        Button exitButton = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        exitButton.setOnAction(e -> {
            userName[0] = Strings.EmptyString;
            addUserStage.close();
        });

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exitButton);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));
        buttonLayout.setSpacing(3);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, textField, buttonLayout);
        layout.setAlignment(Pos.CENTER);

        Platform.runLater(() -> new MoveStage().moveStage(layout, oldStage));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/GenericStyle.css");

        addUserStage.setScene(scene);
        addUserStage.show();
        addUserStage.hide();
        if (addUserStage.getOwner() != null) {
            addUserStage.setX(addUserStage.getOwner().getX() + (addUserStage.getOwner().getWidth() / 2) - (addUserStage.getWidth() / 2));
            addUserStage.setY(addUserStage.getOwner().getY() + (addUserStage.getOwner().getHeight() / 2) - (addUserStage.getHeight() / 2));
        }
        addUserStage.showAndWait();

        log.fine("addUser display has been closed.");
        return userName[0];
    }

    private boolean isUserValid(String user, ArrayList<String> allUsers, Stage oldStage) {
        log.fine("isUserValid has been called.");
        if (user.contentEquals(Strings.AddNewUsername.getValue()) || !user.matches("^[a-zA-Z0-9]+$"))
            new MessageBox().message(new StringProperty[]{Strings.UsernameIsntValid}, oldStage);
        else if (allUsers.contains(user))
            new MessageBox().message(new StringProperty[]{Strings.UsernameAlreadyTaken}, oldStage);
        else if (user.length() > 20)
            new MessageBox().message(new StringProperty[]{Strings.UsernameIsTooLong}, oldStage);
        else return true;
        return false;
    }

    public File addDirectory(StringProperty message, ArrayList<Directory> currentDirectories, Stage oldStage) {
        log.fine("addDirectory has been opened.");

        Stage addDirectoryStage = new Stage();
        if (oldStage != null) addDirectoryStage.initOwner(oldStage);
        addDirectoryStage.initStyle(StageStyle.UNDECORATED);
        addDirectoryStage.initModality(Modality.APPLICATION_MODAL);
        addDirectoryStage.setMinWidth(250);
        GenericMethods.setIcon(addDirectoryStage);

        Label label = new Label();
        label.textProperty().bind(message);

        TextField textField = new TextField();
        textField.setPromptText(Strings.FileSeparator + Strings.PathToDirectory.getValue() + Strings.FileSeparator + Strings.Shows.getValue());

        ArrayList<String> directoryPaths = new ArrayList<>(currentDirectories.size());
        currentDirectories.forEach(aDirectory -> directoryPaths.add(String.valueOf(aDirectory.getDirectory())));
        final File[] directories = new File[1];
        DirectoryChooser DirectoryChooser = new DirectoryChooser();

        Button filePicker = new Button(), submit = new Button(), exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        filePicker.setId("filePicker");
        filePicker.setOnAction(e -> {
            File file = DirectoryChooser.showDialog(addDirectoryStage);
            if (file != null && !file.toString().isEmpty()) {
                textField.setText(String.valueOf(file));
            }
        });
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (isDirectoryValid(directoryPaths, textField.getText(), addDirectoryStage)) {
                directories[0] = new File(textField.getText());
                addDirectoryStage.close();
            }
        });
        exit.setOnAction(e -> {
            directories[0] = new File(Strings.EmptyString);
            addDirectoryStage.close();
        });

        HBox hBox = new HBox(), hBox1 = new HBox();
        hBox.getChildren().addAll(textField, filePicker);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(3);
        hBox1.getChildren().addAll(submit, exit);
        hBox1.setAlignment(Pos.CENTER);
        hBox1.setSpacing(3);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, hBox, hBox1);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 6, 6, 6));
        layout.setSpacing(3);

        Platform.runLater(() -> {
            new MoveStage().moveStage(layout, oldStage);
            addDirectoryStage.requestFocus();
        });

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/TextBox.css");

        addDirectoryStage.setScene(scene);
        addDirectoryStage.show();
        addDirectoryStage.hide();
        if (addDirectoryStage.getOwner() != null) {
            addDirectoryStage.setX(addDirectoryStage.getOwner().getX() + (addDirectoryStage.getOwner().getWidth() / 2) - (addDirectoryStage.getWidth() / 2));
            addDirectoryStage.setY(addDirectoryStage.getOwner().getY() + (addDirectoryStage.getOwner().getHeight() / 2) - (addDirectoryStage.getHeight() / 2));
        }
        addDirectoryStage.showAndWait();

        log.fine("addDirectory has been closed.");
        return directories[0];
    }

    private boolean isDirectoryValid(ArrayList<String> currentDirectories, String directory, Stage oldStage) {
        log.fine("isDirectoryValid has been called.");
        if (currentDirectories.contains(directory))
            new MessageBox().message(new StringProperty[]{Strings.DirectoryIsAlreadyAdded}, oldStage);
        else if (new FileManager().checkFolderExistsAndReadable(new File(directory))) return true;
        else if (directory.isEmpty())
            new MessageBox().message(new StringProperty[]{Strings.YouNeedToEnterADirectory}, oldStage);
        else new MessageBox().message(new StringProperty[]{Strings.DirectoryIsInvalid}, oldStage);
        return false;
    }

    public File pickFile(StringProperty message, StringProperty defaultFilename, StringProperty[] extensionText, String[] extensions, boolean saveFile, Stage oldStage) {
        log.fine("addDirectory has been opened.");

        Stage pickFile = new Stage();
        if (oldStage != null) pickFile.initOwner(oldStage);
        pickFile.initStyle(StageStyle.UNDECORATED);
        pickFile.initModality(Modality.APPLICATION_MODAL);
        pickFile.setMinWidth(250);
        GenericMethods.setIcon(pickFile);

        Label label = new Label();
        label.textProperty().bind(message);

        TextField textField = new TextField();
        textField.setPromptText(Strings.FileSeparator + Strings.PathToDirectory.getValue() + Strings.FileSeparator + Strings.Shows.getValue());

        final File[] directories = new File[1];
        FileChooser fileChooser = new FileChooser();
        if (!defaultFilename.getValue().isEmpty()) fileChooser.setInitialFileName(defaultFilename.getValue());
        int i = 0;
        for (String aExtension : extensions) {
            fileChooser.getExtensionFilters().add(i, new FileChooser.ExtensionFilter(extensionText[i].getValue(), '*' + aExtension));
            i++;
        }

        Button filePicker = new Button(), submit = new Button(), exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        filePicker.setId("filePicker");
        filePicker.setOnAction(e -> {
            File file;
            if (saveFile) file = fileChooser.showSaveDialog(pickFile);
            else file = fileChooser.showOpenDialog(pickFile);
            if (file != null && !file.toString().isEmpty()) {
                textField.setText(String.valueOf(file));
            }
        });
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (isFileNameValid(textField.getText(), extensions, saveFile, pickFile)) {
                directories[0] = new File(textField.getText());
                pickFile.close();
            }
        });
        exit.setOnAction(e -> {
            directories[0] = new File(Strings.EmptyString);
            pickFile.close();
        });

        HBox hBox = new HBox(), hBox1 = new HBox();
        hBox.getChildren().addAll(textField, filePicker);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(3);
        hBox1.getChildren().addAll(submit, exit);
        hBox1.setAlignment(Pos.CENTER);
        hBox1.setSpacing(3);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, hBox, hBox1);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 6, 6, 6));
        layout.setSpacing(3);

        Platform.runLater(() -> {
            new MoveStage().moveStage(layout, oldStage);
            pickFile.requestFocus();
        });

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/TextBox.css");

        pickFile.setScene(scene);
        pickFile.show();
        pickFile.hide();
        if (pickFile.getOwner() != null) {
            pickFile.setX(pickFile.getOwner().getX() + (pickFile.getOwner().getWidth() / 2) - (pickFile.getWidth() / 2));
            pickFile.setY(pickFile.getOwner().getY() + (pickFile.getOwner().getHeight() / 2) - (pickFile.getHeight() / 2));
        }
        pickFile.showAndWait();

        log.fine("addDirectory has been closed.");
        return directories[0];
    }

    private boolean isFileNameValid(String fileName, String[] extensions, boolean saveFile, Stage oldStage) {
        log.fine("isDirectoryValid has been called.");
        boolean properExtension = false;
        for (String extension : extensions) {
            if (fileName.endsWith(extension)) {
                properExtension = true;
                break;
            }
        }
        if (properExtension)
            if (saveFile)
                return !new File(fileName).exists() || new ConfirmBox().confirm(Strings.FileAlreadyExistsOverwriteIt, oldStage);
            else if (new File(fileName).exists()) return true;
            else
                new MessageBox().message(new StringProperty[]{Strings.FileDoesNotExists}, oldStage);
        else
            new MessageBox().message(new StringProperty[]{new SimpleStringProperty(Strings.FilenameMustEndIn.getValue() + Arrays.toString(extensions))}, oldStage);
        return false;
    }
}
