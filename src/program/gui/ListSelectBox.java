package program.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import program.input.MoveWindow;
import program.util.Strings;
import program.util.Variables;

import java.io.File;
import java.util.ArrayList;

public class ListSelectBox {

    private MoveWindow moveWindow = new MoveWindow();

    public String display(String title, String message, ArrayList<String> users) {
        final String[] userName = new String[1];
        userName[0] = Strings.DefaultUsername;

        Stage window = new Stage();
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        ObservableList<String> usersList = FXCollections.observableArrayList(users);
        usersList.sorted();
        usersList.add("Add New Username");
        ComboBox<String> comboBox = new ComboBox<>(usersList);
        comboBox.setValue("Add New Username");

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            if (comboBox.getValue().contentEquals("Add New Username")) {
                TextBox textBox = new TextBox();
                userName[0] = textBox.display("Enter Username", "Please enter your username: ", "Use default username?", "PublicDefault");
                window.close();
            } else if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                userName[0] = comboBox.getValue();
                window.close();
            }
        });

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        moveWindow.moveWindow(window, scene);

        window.setScene(scene);
        window.showAndWait();

        return userName[0];
    }

    public String defaultUser(String title, String message, ArrayList<String> users) {
        final String[] userName = new String[1];
        userName[0] = Variables.EmptyString;

        Stage window = new Stage();
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        ObservableList<String> usersList = FXCollections.observableArrayList(users);
        usersList.sorted();
        ComboBox<String> comboBox = new ComboBox<>(usersList);

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().isEmpty()) {
                    MessageBox messageBox = new MessageBox();
                    messageBox.display("Default User", "Default user not set.");
                    window.close();
                } else {
                    userName[0] = comboBox.getValue();
                    window.close();
                }
            }
        });

        Button exit = new Button("X");
        exit.setOnAction(e -> {
            userName[0] = null;
            window.close();
        });

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        moveWindow.moveWindow(window, scene);

        window.setScene(scene);
        window.showAndWait();

        return userName[0];
    }

    public File directories(String title, String message, ArrayList<File> files) {
        final File[] file = new File[1];
        file[0] = new File(Variables.EmptyString);

        Stage window = new Stage();
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        ObservableList<File> fileList = FXCollections.observableArrayList(files);
        fileList.sorted();
        ComboBox<File> comboBox = new ComboBox<>(fileList);

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().toString().isEmpty()) {
                    MessageBox messageBox = new MessageBox();
                    messageBox.display("Open Folder", "Please choose a folder.");
                } else {
                    file[0] = comboBox.getValue();
                    window.close();
                }
            }
        });

        Button exit = new Button("X");
        exit.setOnAction(e -> {
            file[0] = null;
            window.close();
        });

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        moveWindow.moveWindow(window, scene);

        window.setScene(scene);
        window.showAndWait();

        return file[0];
    }
}