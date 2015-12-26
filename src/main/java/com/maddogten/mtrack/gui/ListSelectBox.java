package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.ImageLoader;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
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

/*
      ListSelectBox has multiple different stages it can display. All of them display a list to choose from, Then returns the choice.
      pickUser- Allows the option to pick a user, or to add a new user which then opens a new TextBox.
      pickDefaultUser- Allows picking a default user, Differs from above as it doesn't allow adding a new user. TODO Combine pickDefaultUser and pickUser
      pickDirectory- Allow picking a directory.
      pickSeasonEpisode- Allows picking a season, Then loads the related episode list to choose from. Returns both the season and episode.
      pickLanguage- Allows picking of a language.
 */

public class ListSelectBox {
    private static final Logger log = Logger.getLogger(ListSelectBox.class.getName());

    @SuppressWarnings("SameParameterValue")
    public String pickUser(StringProperty message, ArrayList<String> users, Window oldWindow) {
        final String[] userName = new String[1];
        userName[0] = Strings.DefaultUsername;

        Stage stage = new Stage();
        stage.getIcons().add(ImageLoader.getImage(Variables.Logo));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);

        Label label = new Label();
        label.textProperty().bind(message);

        log.info(String.valueOf(users));
        ObservableList<String> usersList = FXCollections.observableArrayList(users);
        usersList.sorted();
        usersList.add(Strings.AddNewUsername.getValue());
        ComboBox<String> comboBox = new ComboBox<>(usersList);
        comboBox.setValue(Strings.AddNewUsername.getValue());

        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (comboBox.getValue().contentEquals(Strings.AddNewUsername.getValue())) {
                userName[0] = new TextBox().addUser(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, users, stage);
                stage.close();
            } else if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                userName[0] = comboBox.getValue();
                stage.close();
            }
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            userName[0] = Strings.AddNewUsername.getValue();
            stage.close();
        });

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
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

        return userName[0];
    }

    public String pickDefaultUser(StringProperty message, ArrayList<String> users, String currentDefaultUser, Window oldWindow) {
        final String[] userName = new String[1];
        userName[0] = Strings.EmptyString;

        Stage stage = new Stage();
        ImageLoader.setIcon(stage);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);

        Label label = new Label();
        label.textProperty().bind(message);

        ObservableList<String> usersList = FXCollections.observableArrayList(users);
        usersList.sorted();
        ComboBox<String> comboBox = new ComboBox<>(usersList);
        if (currentDefaultUser != null && !currentDefaultUser.isEmpty()) {
            comboBox.setValue(currentDefaultUser);
        }

        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().isEmpty()) {
                    new MessageBox().display(new StringProperty[]{Strings.DefaultUserNotSet}, stage);
                    stage.close();
                } else {
                    userName[0] = comboBox.getValue();
                    stage.close();
                }
            }
        });

        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            userName[0] = null;
            stage.close();
        });

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        stage.setScene(scene);
        Platform.runLater(() -> {
            stage.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (stage.getWidth() / 2));
            stage.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (stage.getHeight() / 2));
            new MoveStage().moveWindow(stage, oldWindow);
        });
        stage.showAndWait();

        return userName[0];
    }

    public File pickDirectory(StringProperty message, ArrayList<File> files, Window oldWindow) {
        final File[] file = new File[1];
        file[0] = new File(Strings.EmptyString);

        Stage stage = new Stage();
        ImageLoader.setIcon(stage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);

        Label label = new Label();
        label.textProperty().bind(message);

        ObservableList<File> fileList = FXCollections.observableArrayList(files);
        fileList.sorted();
        ComboBox<File> comboBox = new ComboBox<>(fileList);

        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().toString().isEmpty()) {
                    new MessageBox().display(new StringProperty[]{Strings.PleaseChooseAFolder}, stage);
                } else {
                    file[0] = comboBox.getValue();
                    stage.close();
                }
            }
        });

        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> stage.close());

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        stage.setScene(scene);
        Platform.runLater(() -> {
            stage.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (stage.getWidth() / 2));
            stage.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (stage.getHeight() / 2));
            new MoveStage().moveWindow(stage, oldWindow);
        });
        stage.showAndWait();

        return file[0];
    }

    @SuppressWarnings("SameParameterValue")
    public int[] pickSeasonEpisode(String aShow, ShowInfoController showInfoController, Window oldWindow) {
        final int[] choice = new int[2];

        Stage stage = new Stage();
        stage.getIcons().add(ImageLoader.getImage(Variables.Logo));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);

        Label label = new Label();
        label.textProperty().bind(Strings.PickTheSeasonAndEpisode);

        ArrayList<Integer> seasonsString = new ArrayList<>();
        seasonsString.addAll(showInfoController.getSeasonsList(aShow).stream().collect(Collectors.toList()));
        Collections.sort(seasonsString);
        ObservableList<Integer> seasonsList = FXCollections.observableArrayList(seasonsString);
        ComboBox<Integer> seasonsComboBox = new ComboBox<>(seasonsList);

        ArrayList<Integer> episodesArrayList = new ArrayList<>();
        ObservableList<Integer> episodesList = FXCollections.observableArrayList(episodesArrayList);
        ComboBox<Integer> episodesComboBox = new ComboBox<>(episodesList);
        episodesComboBox.setDisable(true);

        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (seasonsComboBox.getValue() != null && !seasonsComboBox.getValue().toString().isEmpty()) {
                if (episodesComboBox.getValue() != null && !episodesComboBox.getValue().toString().isEmpty()) {
                    choice[0] = seasonsComboBox.getValue();
                    choice[1] = episodesComboBox.getValue();
                    stage.close();
                } else new MessageBox().display(new StringProperty[]{Strings.YouHaveToPickAEpisode}, stage);
            } else new MessageBox().display(new StringProperty[]{Strings.YouHaveToPickASeason}, stage);
        });

        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            choice[0] = -1;
            choice[1] = -1;
            stage.close();
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

        stage.setScene(scene);

        final int[] oldValue = {-1};
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (stage.isShowing()) {
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
                stage.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (stage.getWidth() / 2));
                stage.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (stage.getHeight() / 2));
            }
            new MoveStage().moveWindow(stage, oldWindow);
            new Thread(task).start();
        });
        stage.showAndWait();

        return choice;
    }

    public String pickLanguage(Collection<String> languages, Window oldWindow) {
        final String[] language = {"-2"};
        Stage stage = new Stage();
        stage.getIcons().add(ImageLoader.getImage(Variables.Logo));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);

        Label label = new Label();
        label.textProperty().bind(Strings.PleaseChooseYourLanguage);

        log.info(String.valueOf(languages));
        ObservableList<String> languageList = FXCollections.observableArrayList(languages);
        languageList.sorted();
        ComboBox<String> comboBox = new ComboBox<>(languageList);

        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                language[0] = comboBox.getValue();
                stage.close();
            }
        });

        HBox buttonLayout = new HBox();

        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> stage.close());
        buttonLayout.getChildren().addAll(submit, exit);

        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
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

        return language[0];
    }
}