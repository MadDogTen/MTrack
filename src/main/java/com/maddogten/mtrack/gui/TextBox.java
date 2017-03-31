package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    public String addUser(final StringProperty message, final StringProperty messageIfNameFieldIsBlank, final String defaultValue, final ArrayList<String> allUsers, final Stage oldStage) {
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

        Platform.runLater(() -> new MoveStage(layout, oldStage, false));

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

    private boolean isUserValid(final String user, final ArrayList<String> allUsers, final Stage oldStage) {
        log.fine("isUserValid has been called.");
        if (user.contentEquals(Strings.AddNewUsername.getValue()) || !user.matches("^[a-zA-Z0-9]+$"))
            new MessageBox(new StringProperty[]{Strings.UsernameIsntValid}, oldStage);
        else if (allUsers.contains(user))
            new MessageBox(new StringProperty[]{Strings.UsernameAlreadyTaken}, oldStage);
        else if (user.length() > 20)
            new MessageBox(new StringProperty[]{Strings.UsernameIsTooLong}, oldStage);
        else return true;
        return false;
    }

    public ArrayList<File> addDirectory(@SuppressWarnings("SameParameterValue") final StringProperty message, final ArrayList<Directory> currentDirectories, final Stage oldStage) {
        log.fine("addDirectory has been opened.");

        Stage addDirectoryStage = new Stage();
        if (oldStage != null) addDirectoryStage.initOwner(oldStage);
        addDirectoryStage.initStyle(StageStyle.UNDECORATED);
        addDirectoryStage.initModality(Modality.APPLICATION_MODAL);
        addDirectoryStage.setMinWidth(250);
        GenericMethods.setIcon(addDirectoryStage);

        Label label = new Label();
        label.textProperty().bind(message);

        TextArea textArea = new TextArea();
        textArea.setPromptText(Strings.FileSeparator + Strings.PathToDirectory.getValue() + Strings.FileSeparator + Strings.Shows.getValue());
        textArea.setPrefSize(240, 60);

        ArrayList<String> directoryPaths = new ArrayList<>(currentDirectories.size());
        currentDirectories.forEach(aDirectory -> directoryPaths.add(String.valueOf(aDirectory.getDirectory())));
        DirectoryChooser DirectoryChooser = new DirectoryChooser();

        Button filePicker = new Button(), submit = new Button(), exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        filePicker.setId("filePicker");
        filePicker.setOnAction(e -> {
            File file = DirectoryChooser.showDialog(addDirectoryStage);
            if (file != null && !file.toString().isEmpty()) {
                textArea.setText((textArea.getText().isEmpty() ? "" : textArea.getText() + "\n") + String.valueOf(file));
            }
        });
        ArrayList<File> directories = new ArrayList<>();

        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (!textArea.getText().isEmpty()) {
                if (textArea.getText().contains("\n") || textArea.getText().contains(",")) {
                    String string = textArea.getText();
                    string = string.replaceAll(",", "\n");
                    String[] files = string.split("\n");
                    for (String file : files) {
                        if (!file.isEmpty()) {
                            if (isDirectoryValid(directoryPaths, file, addDirectoryStage))
                                directories.add(new File(file));
                            else
                                log.info("File: \"" + file + "\" was invalid, and not added."); // TODO Add user popup notification that groups all issues
                        }
                    }
                } else {
                    if (isDirectoryValid(directoryPaths, textArea.getText(), addDirectoryStage))
                        directories.add(new File(textArea.getText()));
                    else log.info("File: \"" + textArea.getText() + "\" was invalid, and not added.");
                }
            }

            if (!directories.isEmpty()) addDirectoryStage.close();
            else new MessageBox(new StringProperty[]{Strings.YouNeedToEnterADirectory}, oldStage);
        });

        exit.setOnAction(e -> {
            directories.clear();
            addDirectoryStage.close();
        });

        HBox hBox = new HBox(), hBox1 = new HBox();
        hBox.getChildren().addAll(textArea, filePicker);
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
            new MoveStage(layout, oldStage, false);
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
        return directories;
    }

    private boolean isDirectoryValid(final ArrayList<String> currentDirectories, final String directory, final Stage oldStage) {
        log.fine("isDirectoryValid has been called.");
        if (currentDirectories.contains(directory))
            new MessageBox(new StringProperty[]{Strings.DirectoryIsAlreadyAdded, new SimpleStringProperty(directory)}, oldStage);
        else if (new FileManager().checkFolderExistsAndReadable(new File(directory))) return true;
        else if (directory.isEmpty())
            new MessageBox(new StringProperty[]{Strings.YouNeedToEnterADirectory}, oldStage);
        else
            new MessageBox(new StringProperty[]{Strings.DirectoryIsInvalid, new SimpleStringProperty(directory)}, oldStage);
        return false;
    }

    public File pickFile(final StringProperty message, final StringProperty defaultFilename, final StringProperty[] extensionText, final String[] extensions, final boolean saveFile, final Stage oldStage) {
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
            if (isFileNameValid(textField.getText(), extensions, saveFile, pickFile) || textField.getText().endsWith(".xml")) {
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
            new MoveStage(layout, oldStage, false);
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

    private boolean isFileNameValid(final String fileName, final String[] extensions, final boolean saveFile, final Stage oldStage) {
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
                new MessageBox(new StringProperty[]{Strings.FileDoesNotExists}, oldStage);
        else {
            if (!fileName.endsWith(".xml"))
                new MessageBox(new StringProperty[]{new SimpleStringProperty(Strings.FilenameMustEndIn.getValue() + Arrays.toString(extensions))}, oldStage);
        }
        return false;
    }

    @SuppressWarnings("SameParameterValue")
    public int[] enterNumber(final StringProperty message, final StringProperty[] fieldText, final int numberOfFields, final Stage oldStage) {
        log.fine("addDirectory has been opened.");

        Stage enterNumber = new Stage();
        if (oldStage != null) enterNumber.initOwner(oldStage);
        enterNumber.initStyle(StageStyle.UNDECORATED);
        enterNumber.initModality(Modality.APPLICATION_MODAL);
        enterNumber.setMinWidth(250);
        GenericMethods.setIcon(enterNumber);

        Label label = new Label();
        label.textProperty().bind(message);

        ArrayList<TextField> textFields = new ArrayList<>();
        for (int x = 0; x < numberOfFields; x++) {
            TextField textField = new TextField();
            textField.promptTextProperty().bind(fieldText[x]);
            textField.setPrefWidth(60);
            textFields.add(textField);
        }

        final int[] numberResult = new int[numberOfFields];
        Button submit = new Button(), exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            boolean allValid = true;
            for (TextField textField : textFields) {
                if (isNumberValid(textField.getText(), 0, oldStage)) {
                    numberResult[textFields.indexOf(textField)] = textField.getText().isEmpty() ? 0 : Integer.parseInt(textField.getText());
                } else allValid = false;
            }
            if (allValid) enterNumber.close();
        });
        exit.setOnAction(e -> {
            for (int x = 0; x < numberOfFields; x++) {
                numberResult[x] = 0;
            }
            enterNumber.close();
        });

        HBox hBox = new HBox(), hBox1 = new HBox();
        hBox.getChildren().addAll(textFields);
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
            new MoveStage(layout, oldStage, false);
            enterNumber.requestFocus();
        });

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/TextBox.css");

        enterNumber.setScene(scene);
        enterNumber.show();
        enterNumber.hide();
        if (enterNumber.getOwner() != null) {
            enterNumber.setX(enterNumber.getOwner().getX() + (enterNumber.getOwner().getWidth() / 2) - (enterNumber.getWidth() / 2));
            enterNumber.setY(enterNumber.getOwner().getY() + (enterNumber.getOwner().getHeight() / 2) - (enterNumber.getHeight() / 2));
        }
        enterNumber.showAndWait();

        log.fine("addDirectory has been closed.");
        return numberResult;
    }

    private boolean isNumberValid(final String textFieldValue, @SuppressWarnings("SameParameterValue") final int minValue, final Stage oldStage) {
        log.finest("isNumberValid has been called.");
        if ((!textFieldValue.matches("^[0-9]+$") || Integer.parseInt(textFieldValue) > Variables.maxWaitTimeSeconds || Integer.parseInt(textFieldValue) < minValue)) {
            if (textFieldValue.isEmpty()) return true;
            new MessageBox(new StringProperty[]{new SimpleStringProperty(Strings.MustBeANumberBetween.getValue() + minValue + " - " + Variables.maxWaitTimeSeconds)}, oldStage);
            return false;
        } else return true;
    }
}
