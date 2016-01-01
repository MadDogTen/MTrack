package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.io.FileManager;
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
    private final Stage[] listSelectStages = new Stage[7];

    @SuppressWarnings("SameParameterValue")
    public Object[] pickUser(StringProperty message, ArrayList<String> users) {
        Object[] result = new Object[]{Strings.DefaultUsername, false};
        listSelectStages[0] = new Stage();
        listSelectStages[0].getIcons().add(GenericMethods.getImage(Variables.Logo));
        listSelectStages[0].initStyle(StageStyle.UNDECORATED);
        listSelectStages[0].initModality(Modality.APPLICATION_MODAL);
        listSelectStages[0].setMinWidth(250);
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
                result[0] = new TextBox().addUser(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, users, listSelectStages[0]);
                if (!result[0].toString().isEmpty()) {
                    listSelectStages[0].close();
                }
            } else if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                result[0] = comboBox.getValue();
                listSelectStages[0].close();
            }
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            result[0] = Strings.AddNewUsername.getValue();
            listSelectStages[0].close();
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
        buttonLayout.setSpacing(3);
        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/ListSelectBox.css");
        listSelectStages[0].setScene(scene);
        Platform.runLater(() -> new MoveStage().moveStage(layout, null));
        listSelectStages[0].showAndWait();
        listSelectStages[0] = null;
        return result;
    }

    public String pickDefaultUser(StringProperty message, ArrayList<String> users, String currentDefaultUser, Stage oldStage) {
        final String[] userName = new String[1];
        userName[0] = Strings.EmptyString;
        listSelectStages[1] = new Stage();
        GenericMethods.setIcon(listSelectStages[1]);
        listSelectStages[1].initOwner(oldStage);
        listSelectStages[1].initStyle(StageStyle.UNDECORATED);
        listSelectStages[1].initModality(Modality.APPLICATION_MODAL);
        listSelectStages[1].setMinWidth(250);
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
                    new MessageBox().message(new StringProperty[]{Strings.DefaultUserNotSet}, listSelectStages[1]);
                    listSelectStages[1].close();
                } else {
                    userName[0] = comboBox.getValue();
                    listSelectStages[1].close();
                }
            }
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            userName[0] = null;
            listSelectStages[1].close();
        });
        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));
        buttonLayout.setSpacing(3);
        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        listSelectStages[1].setScene(scene);
        Platform.runLater(() -> {
            listSelectStages[1].setX(listSelectStages[1].getOwner().getX() + (listSelectStages[1].getOwner().getWidth() / 2) - (listSelectStages[1].getWidth() / 2));
            listSelectStages[1].setY(listSelectStages[1].getOwner().getY() + (listSelectStages[1].getOwner().getHeight() / 2) - (listSelectStages[1].getHeight() / 2));
            new MoveStage().moveStage(layout, oldStage);
        });
        listSelectStages[1].showAndWait();
        listSelectStages[1] = null;
        return userName[0];
    }

    public void openDirectory(ArrayList<Directory> directories, Stage oldStage) {
        listSelectStages[6] = new Stage();
        listSelectStages[6].initOwner(oldStage);
        listSelectStages[6].initStyle(StageStyle.UNDECORATED);
        listSelectStages[6].initModality(Modality.APPLICATION_MODAL);
        GenericMethods.setIcon(listSelectStages[6]);
        Label label = new Label();
        label.textProperty().bind(Strings.PickTheFolderYouWantToOpen);
        label.setPadding(new Insets(0, 0, 5, 0));
        ObservableList<Directory> directoriesObservable = FXCollections.observableArrayList(directories);
        directoriesObservable.sorted();
        ComboBox<Directory> directoryComboBox = new ComboBox<>(directoriesObservable);
        Button openAllButton = new Button();
        openAllButton.textProperty().bind(Strings.OpenAll);
        Button openSelectedButton = new Button();
        openSelectedButton.textProperty().bind(Strings.OpenSelected);
        Button exitButton = new Button();
        exitButton.setText(Strings.ExitButtonText);
        openAllButton.setOnAction(e -> {
            FileManager fileManager = new FileManager();
            directories.forEach(directory -> fileManager.open(directory.getDirectory()));
            listSelectStages[6].close();
        });
        openSelectedButton.setOnAction(event -> {
            new FileManager().open(directoryComboBox.getValue().getDirectory());
            listSelectStages[6].close();
        });
        exitButton.setOnAction(e -> listSelectStages[6].close());
        HBox buttonHBox = new HBox();
        buttonHBox.getChildren().addAll(openAllButton, openSelectedButton, exitButton);
        buttonHBox.setAlignment(Pos.BOTTOM_CENTER);
        buttonHBox.setPadding(new Insets(5, 5, 5, 5));
        buttonHBox.setSpacing(3);
        VBox layout = new VBox();
        layout.getChildren().addAll(label, directoryComboBox, buttonHBox);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(5, 5, 0, 5));
        Scene scene = new Scene(layout);
        listSelectStages[6].setScene(scene);
        Platform.runLater(() -> {
            listSelectStages[6].setX(listSelectStages[6].getOwner().getX() + (listSelectStages[6].getOwner().getWidth() / 2) - (listSelectStages[6].getWidth() / 2));
            listSelectStages[6].setY(listSelectStages[6].getOwner().getY() + (listSelectStages[6].getOwner().getHeight() / 2) - (listSelectStages[6].getHeight() / 2));
            new MoveStage().moveStage(layout, oldStage);
        });
        listSelectStages[6].showAndWait();
        listSelectStages[6] = null;
    }

    public Directory pickDirectory(StringProperty message, ArrayList<Directory> files, Stage oldStage) {
        final Directory[] directory = new Directory[1];
        listSelectStages[2] = new Stage();
        GenericMethods.setIcon(listSelectStages[2]);
        listSelectStages[2].initOwner(oldStage);
        listSelectStages[2].initStyle(StageStyle.UNDECORATED);
        listSelectStages[2].initModality(Modality.APPLICATION_MODAL);
        listSelectStages[2].setMinWidth(250);
        Label label = new Label();
        label.textProperty().bind(message);
        ObservableList<Directory> fileList = FXCollections.observableArrayList(files);
        fileList.sorted();
        ComboBox<Directory> comboBox = new ComboBox<>(fileList);
        Button submit = new Button();
        submit.setText("Open Selected");
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().toString().isEmpty())
                    new MessageBox().message(new StringProperty[]{Strings.PleaseChooseAFolder}, listSelectStages[2]);
                else {
                    directory[0] = comboBox.getValue();
                    listSelectStages[2].close();
                }
            }
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> listSelectStages[2].close());
        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));
        buttonLayout.setSpacing(3);
        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        listSelectStages[2].setScene(scene);
        Platform.runLater(() -> {
            listSelectStages[2].setX(listSelectStages[2].getOwner().getX() + (listSelectStages[2].getOwner().getWidth() / 2) - (listSelectStages[2].getWidth() / 2));
            listSelectStages[2].setY(listSelectStages[2].getOwner().getY() + (listSelectStages[2].getOwner().getHeight() / 2) - (listSelectStages[2].getHeight() / 2));
            new MoveStage().moveStage(layout, oldStage);
        });
        listSelectStages[2].showAndWait();
        listSelectStages[2] = null;
        return directory[0];
    }

    public int[] pickSeasonEpisode(String aShow, ShowInfoController showInfoController, Stage oldStage) {
        final int[] choice = new int[2];
        listSelectStages[3] = new Stage();
        listSelectStages[3].initOwner(oldStage);
        listSelectStages[3].getIcons().add(GenericMethods.getImage(Variables.Logo));
        listSelectStages[3].initStyle(StageStyle.UNDECORATED);
        listSelectStages[3].initModality(Modality.APPLICATION_MODAL);
        listSelectStages[3].setMinWidth(250);
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
                    listSelectStages[3].close();
                } else
                    new MessageBox().message(new StringProperty[]{Strings.YouHaveToPickAEpisode}, listSelectStages[3]);
            } else new MessageBox().message(new StringProperty[]{Strings.YouHaveToPickASeason}, listSelectStages[3]);
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            choice[0] = -1;
            choice[1] = -1;
            listSelectStages[3].close();
        });
        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 5, 5, 5));
        buttonLayout.setSpacing(3);
        HBox comboBoxLayout = new HBox();
        comboBoxLayout.getChildren().addAll(seasonsComboBox, episodesComboBox);
        comboBoxLayout.setAlignment(Pos.CENTER);
        comboBoxLayout.setPadding(new Insets(5, 5, 0, 5));
        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBoxLayout, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        listSelectStages[3].setScene(scene);
        final int[] oldValue = {-1};
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (listSelectStages[3].isShowing()) {
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
            listSelectStages[3].setX(listSelectStages[3].getOwner().getX() + (listSelectStages[3].getOwner().getWidth() / 2) - (listSelectStages[3].getWidth() / 2));
            listSelectStages[3].setY(listSelectStages[3].getOwner().getY() + (listSelectStages[3].getOwner().getHeight() / 2) - (listSelectStages[3].getHeight() / 2));
            new MoveStage().moveStage(layout, oldStage);
            new Thread(task).start();
        });
        listSelectStages[3].showAndWait();
        listSelectStages[3] = null;
        return choice;
    }

    public Object[] pickLanguage(Collection<String> languages, Stage oldStage) {
        Object[] result = {"-2", false};
        listSelectStages[4] = new Stage();
        if (oldStage != null) listSelectStages[4].initOwner(oldStage);
        listSelectStages[4].getIcons().add(GenericMethods.getImage(Variables.Logo));
        listSelectStages[4].initStyle(StageStyle.UNDECORATED);
        listSelectStages[4].initModality(Modality.APPLICATION_MODAL);
        listSelectStages[4].setMinWidth(250);
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
                listSelectStages[4].close();
            }
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> listSelectStages[4].close());
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
        buttonLayout.setSpacing(3);
        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/ListSelectBox.css");
        listSelectStages[4].setScene(scene);
        Platform.runLater(() -> {
            if (listSelectStages[4].getOwner() != null) {
                listSelectStages[4].setX(listSelectStages[4].getOwner().getX() + (listSelectStages[4].getOwner().getWidth() / 2) - (listSelectStages[4].getWidth() / 2));
                listSelectStages[4].setY(listSelectStages[4].getOwner().getY() + (listSelectStages[4].getOwner().getHeight() / 2) - (listSelectStages[4].getHeight() / 2));
            }
            new MoveStage().moveStage(layout, oldStage);
        });
        listSelectStages[4].showAndWait();
        listSelectStages[4] = null;
        return result;
    }

    public String pickShow(ArrayList<String> shows, Stage oldStage) {
        String[] returnShow = {Strings.EmptyString};
        listSelectStages[5] = new Stage();
        listSelectStages[5].initOwner(oldStage);
        listSelectStages[5].getIcons().add(GenericMethods.getImage(Variables.Logo));
        listSelectStages[5].initStyle(StageStyle.UNDECORATED);
        listSelectStages[5].initModality(Modality.APPLICATION_MODAL);
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
                listSelectStages[5].close();
            }
        });
        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> listSelectStages[5].close());
        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));
        buttonLayout.setSpacing(3);
        VBox layout = new VBox();
        layout.getChildren().addAll(label, showComboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        listSelectStages[5].setScene(new Scene(layout));
        Platform.runLater(() -> {
            listSelectStages[5].setX(listSelectStages[5].getX() + (listSelectStages[5].getWidth() / 2) - (listSelectStages[5].getWidth() / 2));
            listSelectStages[5].setY(listSelectStages[5].getY() + (listSelectStages[5].getHeight() / 2) - (listSelectStages[5].getHeight() / 2));
            new MoveStage().moveStage(layout, oldStage);
        });
        listSelectStages[5].showAndWait();
        listSelectStages[5] = null;
        return returnShow[0];
    }
}