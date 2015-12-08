package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.io.MoveWindow;
import com.maddogten.mtrack.util.ImageLoader;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ListSelectBox {
    private static final Logger log = Logger.getLogger(ListSelectBox.class.getName());

    @SuppressWarnings("SameParameterValue")
    public String display(String message, ArrayList<String> users, Window oldWindow) {
        final String[] userName = new String[1];
        userName[0] = Strings.DefaultUsername;

        Stage window = new Stage();
        window.getIcons().add(ImageLoader.getImage(Variables.Logo));
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        log.info(String.valueOf(users));
        ObservableList<String> usersList = FXCollections.observableArrayList(users);
        usersList.sorted();
        usersList.add(Strings.AddNewUsername);
        ComboBox<String> comboBox = new ComboBox<>(usersList);
        comboBox.setValue(Strings.AddNewUsername);

        Button submit = new Button(Strings.Submit);
        submit.setOnAction(e -> {
            if (comboBox.getValue().contentEquals(Strings.AddNewUsername)) {
                TextBox textBox = new TextBox();
                userName[0] = textBox.display(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, users, window);
                window.close();
            } else if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                userName[0] = comboBox.getValue();
                window.close();
            }
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            userName[0] = Strings.AddNewUsername;
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

        window.setScene(scene);
        Platform.runLater(() -> {
            if (oldWindow != null) {
                window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
                window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            }
            new MoveWindow().moveWindow(window, oldWindow);
        });
        window.showAndWait();

        return userName[0];
    }

    public String defaultUser(String message, ArrayList<String> users, String currentDefaultUser, Window oldWindow) {
        final String[] userName = new String[1];
        userName[0] = Strings.EmptyString;

        Stage window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        ObservableList<String> usersList = FXCollections.observableArrayList(users);
        usersList.sorted();
        ComboBox<String> comboBox = new ComboBox<>(usersList);
        if (currentDefaultUser != null && !currentDefaultUser.isEmpty()) {
            comboBox.setValue(currentDefaultUser);
        }

        Button submit = new Button(Strings.Submit);
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().isEmpty()) {
                    new MessageBox().display(new String[]{Strings.DefaultUserNotSet}, window);
                    window.close();
                } else {
                    userName[0] = comboBox.getValue();
                    window.close();
                }
            }
        });

        Button exit = new Button(Strings.ExitButtonText);
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

        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window, oldWindow);
        });
        window.showAndWait();

        return userName[0];
    }

    public File directories(String message, ArrayList<File> files, Window oldWindow) {
        final File[] file = new File[1];
        file[0] = new File(Strings.EmptyString);

        Stage window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        ObservableList<File> fileList = FXCollections.observableArrayList(files);
        fileList.sorted();
        ComboBox<File> comboBox = new ComboBox<>(fileList);

        Button submit = new Button(Strings.Submit);
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().toString().isEmpty()) {
                    new MessageBox().display(new String[]{Strings.PleaseChooseAFolder}, window);
                } else {
                    file[0] = comboBox.getValue();
                    window.close();
                }
            }
        });

        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> window.close());

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window, oldWindow);
        });
        window.showAndWait();

        return file[0];
    }

    @SuppressWarnings("SameParameterValue")
    public int[] pickSeasonEpisode(String aShow, ShowInfoController showInfoController, Window oldWindow) {
        final int[] choice = new int[2];

        Stage window = new Stage();
        window.getIcons().add(ImageLoader.getImage(Variables.Logo));
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(Strings.PickTheSeasonAndEpisode);

        ArrayList<Integer> seasonsString = new ArrayList<>();
        seasonsString.addAll(showInfoController.getSeasonsList(aShow).stream().collect(Collectors.toList()));
        Collections.sort(seasonsString);
        ObservableList<Integer> seasonsList = FXCollections.observableArrayList(seasonsString);
        ComboBox<Integer> seasonsComboBox = new ComboBox<>(seasonsList);

        ArrayList<Integer> episodesArrayList = new ArrayList<>();
        ObservableList<Integer> episodesList = FXCollections.observableArrayList(episodesArrayList);
        ComboBox<Integer> episodesComboBox = new ComboBox<>(episodesList);
        episodesComboBox.setDisable(true);

        Button submit = new Button(Strings.Submit);
        submit.setOnAction(e -> {
            if (seasonsComboBox.getValue() != null && !seasonsComboBox.getValue().toString().isEmpty()) {
                if (episodesComboBox.getValue() != null && !episodesComboBox.getValue().toString().isEmpty()) {
                    choice[0] = seasonsComboBox.getValue();
                    choice[1] = episodesComboBox.getValue();
                    window.close();
                } else new MessageBox().display(new String[]{Strings.YouHaveToPickAEpisode}, window);
            } else new MessageBox().display(new String[]{Strings.YouHaveToPickASeason}, window);
        });

        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            choice[0] = -1;
            choice[1] = -1;
            window.close();
        });

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 5, 5, 5));

        HBox comboBoxLayout = new HBox();
        comboBoxLayout.getChildren().addAll(seasonsComboBox, episodesComboBox);
        comboBoxLayout.setAlignment(Pos.CENTER);
        comboBoxLayout.setPadding(new Insets(5, 5, 0, 5));

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBoxLayout, buttonLayout);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        window.setScene(scene);

        final int[] oldValue = {-1};
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (window.isShowing()) {
                    if (seasonsComboBox.getValue() != null && !seasonsComboBox.getValue().toString().isEmpty() && seasonsComboBox.getValue() != oldValue[0]) {
                        oldValue[0] = seasonsComboBox.getValue();
                        episodesArrayList.clear();
                        episodesArrayList.addAll(showInfoController.getEpisodesList(aShow, seasonsComboBox.getValue()).stream().collect(Collectors.toList()));
                        Collections.sort(episodesArrayList);
                        Platform.runLater(() -> {
                            episodesList.clear();
                            episodesList.addAll(episodesArrayList);
                            if (episodesComboBox.isDisabled()) {
                                episodesComboBox.setDisable(false);
                            }
                        });
                    }
                }
                return null;
            }
        };
        Platform.runLater(() -> {
            if (oldWindow != null) {
                window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
                window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            }
            new MoveWindow().moveWindow(window, oldWindow);
            new Thread(task).start();
        });
        window.showAndWait();

        return choice;
    }

    public String pickLanguage(String message, Collection<String> languages, Window oldWindow) {
        final String[] language = {"-2"};

        Stage window = new Stage();
        window.getIcons().add(ImageLoader.getImage(Variables.Logo));
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        log.info(String.valueOf(languages));
        ObservableList<String> languageList = FXCollections.observableArrayList(languages);
        languageList.sorted();
        ComboBox<String> comboBox = new ComboBox<>(languageList);

        Button submit = new Button(Strings.Submit);
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                language[0] = comboBox.getValue();
                window.close();
            }
        });

        HBox buttonLayout = new HBox();

        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> window.close());
        buttonLayout.getChildren().addAll(submit, exit);

        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        window.setScene(scene);
        Platform.runLater(() -> {
            if (oldWindow != null) {
                window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
                window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            }
            new MoveWindow().moveWindow(window, oldWindow);
        });
        window.showAndWait();

        return language[0];
    }
}