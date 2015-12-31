package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
      ListSelectBox has multiple different stages it can display. All of them display a list to choose from, Then returns the choice.
      pickUser- Allows the option to pick a user, or to add a new user which then opens a new TextBox.
      pickDefaultUser- Allows picking a default user, Differs from above as it doesn't allow adding a new user.
      pickDirectory- Allow picking a directory.
      pickSeasonEpisode- Allows picking a season, Then loads the related episode list to choose from. Returns both the season and episode.
      pickLanguage- Allows picking of a language.
 */

public class ListSelectBox {
    private static final Logger log = Logger.getLogger(ListSelectBox.class.getName());

    @SuppressWarnings("SameParameterValue")
    public Object[] pickUser(StringProperty message, ArrayList<String> users, Stage oldStage) {
        Object[] result = new Object[]{Strings.DefaultUsername, false};
        Stage stage = new Stage();
        stage.getIcons().add(GenericMethods.getImage(Variables.Logo));
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
                result[0] = new TextBox().addUser(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, users, stage);
                if (!result[0].toString().isEmpty()) {
                    stage.close();
                }
            } else if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                result[0] = comboBox.getValue();
                stage.close();
            }
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            result[0] = Strings.AddNewUsername.getValue();
            stage.close();
        });
        CheckBox makeLanguageDefault = new CheckBox();
        makeLanguageDefault.setPadding(new Insets(0, 6, 0, 0));
        Tooltip makeLanguageDefaultTooltip = new Tooltip();
        makeLanguageDefaultTooltip.setStyle("tooltip");
        makeLanguageDefaultTooltip.textProperty().bind(Strings.MakeUserDefault);
        makeLanguageDefault.setTooltip(makeLanguageDefaultTooltip);
        makeLanguageDefault.setOnAction(e -> result[1] = !(boolean) result[1]);
        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(makeLanguageDefault, submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));
        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/ListSelectBox.css");
        stage.setScene(scene);
        Platform.runLater(() -> {
            if (oldStage != null) {
                stage.setX(oldStage.getX() + (oldStage.getWidth() / 2) - (stage.getWidth() / 2));
                stage.setY(oldStage.getY() + (oldStage.getHeight() / 2) - (stage.getHeight() / 2));
            }
            new MoveStage().moveStage(stage, oldStage);
        });
        stage.showAndWait();
        return result;
    }

    public String pickDefaultUser(StringProperty message, ArrayList<String> users, String currentDefaultUser, Stage oldStage) {
        final String[] userName = new String[1];
        userName[0] = Strings.EmptyString;
        Stage stage = new Stage();
        GenericMethods.setIcon(stage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);
        Label label = new Label();
        label.textProperty().bind(message);
        ObservableList<String> usersList = FXCollections.observableArrayList(users);
        usersList.sorted();
        ComboBox<String> comboBox = new ComboBox<>(usersList);
        if (currentDefaultUser != null && !currentDefaultUser.isEmpty()) comboBox.setValue(currentDefaultUser);
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
            stage.setX(oldStage.getX() + (oldStage.getWidth() / 2) - (stage.getWidth() / 2));
            stage.setY(oldStage.getY() + (oldStage.getHeight() / 2) - (stage.getHeight() / 2));
            new MoveStage().moveStage(stage, oldStage);
        });
        stage.showAndWait();
        return userName[0];
    }

    public Directory pickDirectory(StringProperty message, ArrayList<Directory> files, Stage oldStage) {
        final Directory[] directory = new Directory[1];
        Stage stage = new Stage();
        GenericMethods.setIcon(stage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);
        Label label = new Label();
        label.textProperty().bind(message);
        ObservableList<Directory> fileList = FXCollections.observableArrayList(files);
        fileList.sorted();
        ComboBox<Directory> comboBox = new ComboBox<>(fileList);
        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().toString().isEmpty())
                    new MessageBox().display(new StringProperty[]{Strings.PleaseChooseAFolder}, stage);
                else {
                    directory[0] = comboBox.getValue();
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
            stage.setX(oldStage.getX() + (oldStage.getWidth() / 2) - (stage.getWidth() / 2));
            stage.setY(oldStage.getY() + (oldStage.getHeight() / 2) - (stage.getHeight() / 2));
            new MoveStage().moveStage(stage, oldStage);
        });
        stage.showAndWait();
        return directory[0];
    }

    @SuppressWarnings("SameParameterValue")
    public int[] pickSeasonEpisode(String aShow, ShowInfoController showInfoController, Stage oldStage) {
        final int[] choice = new int[2];
        Stage stage = new Stage();
        stage.getIcons().add(GenericMethods.getImage(Variables.Logo));
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
                            if (episodesComboBox.isDisabled()) episodesComboBox.setDisable(false);
                        });
                    }
                }
                return null;
            }
        };
        Platform.runLater(() -> {
            if (oldStage != null) {
                stage.setX(oldStage.getX() + (oldStage.getWidth() / 2) - (stage.getWidth() / 2));
                stage.setY(oldStage.getY() + (oldStage.getHeight() / 2) - (stage.getHeight() / 2));
            }
            new MoveStage().moveStage(stage, oldStage);
            new Thread(task).start();
        });
        stage.showAndWait();
        return choice;
    }

    public Object[] pickLanguage(Collection<String> languages, Stage oldStage) {
        Object[] result = {"-2", false};
        Stage stage = new Stage();
        stage.getIcons().add(GenericMethods.getImage(Variables.Logo));
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
                result[0] = comboBox.getValue();
                stage.close();
            }
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> stage.close());
        CheckBox makeLanguageDefault = new CheckBox();
        makeLanguageDefault.setPadding(new Insets(0, 6, 0, 0));
        Tooltip makeLanguageDefaultTooltip = new Tooltip();
        makeLanguageDefaultTooltip.setStyle("tooltip");
        makeLanguageDefaultTooltip.textProperty().bind(Strings.MakeLanguageDefault);
        makeLanguageDefault.setTooltip(makeLanguageDefaultTooltip);
        makeLanguageDefault.setOnAction(e -> result[1] = !(boolean) result[1]);
        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(makeLanguageDefault, submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));
        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/ListSelectBox.css");
        stage.setScene(scene);
        Platform.runLater(() -> {
            if (oldStage != null) {
                stage.setX(oldStage.getX() + (oldStage.getWidth() / 2) - (stage.getWidth() / 2));
                stage.setY(oldStage.getY() + (oldStage.getHeight() / 2) - (stage.getHeight() / 2));
            }
            new MoveStage().moveStage(stage, oldStage);
        });
        stage.showAndWait();
        return result;
    }

    public String pickShow(ArrayList<String> shows, Stage oldStage) {
        String[] returnShow = {Strings.EmptyString};
        Stage stage = new Stage();
        stage.getIcons().add(GenericMethods.getImage(Variables.Logo));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        Label label = new Label();
        label.textProperty().bind(Strings.PickShowToUnHide);
        ObservableList<String> showsList = FXCollections.observableList(shows);
        showsList.sorted();
        ComboBox<String> showComboBox = new ComboBox<>(showsList);
        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            if (showComboBox.getValue() != null && !showComboBox.getValue().isEmpty()) {
                returnShow[0] = showComboBox.getValue();
                stage.close();
            }
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> stage.close());
        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));
        VBox layout = new VBox();
        layout.getChildren().addAll(label, showComboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(layout));
        Platform.runLater(() -> {
            if (oldStage != null) {
                stage.setX(oldStage.getX() + (oldStage.getWidth() / 2) - (stage.getWidth() / 2));
                stage.setY(oldStage.getY() + (oldStage.getHeight() / 2) - (stage.getHeight() / 2));
            }
            new MoveStage().moveStage(stage, oldStage);
        });
        stage.showAndWait();
        return returnShow[0];
    }
}