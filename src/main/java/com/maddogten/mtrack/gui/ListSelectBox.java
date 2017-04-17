package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.ClassHandler;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

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
    public Object[] pickUser(final StringProperty message, final ArrayList<String> users) {
        log.fine("pickUser has been opened.");

        Stage pickUserStage = new Stage();
        pickUserStage.initStyle(StageStyle.UNDECORATED);
        pickUserStage.initModality(Modality.APPLICATION_MODAL);
        pickUserStage.setMinWidth(250);
        GenericMethods.setIcon(pickUserStage);

        Label label = new Label();
        label.textProperty().bind(message);
        log.info(String.valueOf(users));

        ObservableList<String> usersList = FXCollections.observableArrayList(users);
        usersList.sorted();
        usersList.add(Strings.AddNewUsername.getValue());
        ComboBox<String> comboBox = new ComboBox<>(usersList);
        comboBox.setValue(Strings.AddNewUsername.getValue());

        Button submit = new Button(), exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        submit.textProperty().bind(Strings.Submit);
        Object[] result = new Object[]{Strings.DefaultUsername, false};
        submit.setOnAction(e -> {
            if (comboBox.getValue().equalsIgnoreCase(Strings.AddNewUsername.getValue())) {
                result[0] = new TextBox().addUser(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, users, pickUserStage);
                if (!result[0].toString().isEmpty()) {
                    pickUserStage.close();
                }
            } else if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                result[0] = comboBox.getValue();
                pickUserStage.close();
            }
        });
        exit.setOnAction(e -> {
            result[0] = Strings.AddNewUsername.getValue();
            pickUserStage.close();
        });

        CheckBox makeLanguageDefault = new CheckBox();
        makeLanguageDefault.setPadding(new Insets(0, 6, 0, 0));
        Tooltip makeLanguageDefaultTooltip = new Tooltip();
        makeLanguageDefaultTooltip.setId("tooltip");
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
        layout.setPadding(new Insets(6, 6, 6, 6));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/ListSelectBox.css");

        pickUserStage.setScene(scene);

        Platform.runLater(() -> new MoveStage(layout, null, false));

        pickUserStage.showAndWait();

        log.fine("pickUser has been Closed.");
        return result;
    }

    public Integer pickDefaultUser(final StringProperty message, final ArrayList<Integer> users, int currentDefaultUser, final Stage oldStage) {
        log.fine("pickDefaultUser has been opened.");

        Stage pickDefaultUserStage = new Stage();
        pickDefaultUserStage.initOwner(oldStage);
        pickDefaultUserStage.initStyle(StageStyle.UNDECORATED);
        pickDefaultUserStage.initModality(Modality.APPLICATION_MODAL);
        pickDefaultUserStage.setMinWidth(250);
        GenericMethods.setIcon(pickDefaultUserStage);

        Label label = new Label();
        label.textProperty().bind(message);

        Map<String, Integer> usersString = new HashMap<>();
        users.forEach(userID -> usersString.put(ClassHandler.userInfoController().getUserNameFromID(userID), userID));
        ObservableList<String> usersList = FXCollections.observableArrayList(usersString.keySet());
        usersList.sorted();
        ComboBox<String> comboBox = new ComboBox<>(usersList);
        if (currentDefaultUser != -2)
            comboBox.setValue(ClassHandler.userInfoController().getUserNameFromID(currentDefaultUser));

        Button submit = new Button(), exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        submit.textProperty().bind(Strings.Submit);
        final int[] user = new int[]{-2};
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().isEmpty()) {
                    new MessageBox(new StringProperty[]{Strings.DefaultUserNotSet}, pickDefaultUserStage);
                    pickDefaultUserStage.close();
                } else {
                    user[0] = usersString.get(comboBox.getValue());
                    pickDefaultUserStage.close();
                }
            }
        });
        exit.setOnAction(e -> {
            user[0] = -2;
            pickDefaultUserStage.close();
        });

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));
        buttonLayout.setSpacing(3);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 6, 6, 6));

        Platform.runLater(() -> new MoveStage(layout, oldStage, false));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/ListSelectBox.css");

        pickDefaultUserStage.setScene(scene);
        pickDefaultUserStage.show();
        pickDefaultUserStage.hide();
        pickDefaultUserStage.setX(pickDefaultUserStage.getOwner().getX() + (pickDefaultUserStage.getOwner().getWidth() / 2) - (pickDefaultUserStage.getWidth() / 2));
        pickDefaultUserStage.setY(pickDefaultUserStage.getOwner().getY() + (pickDefaultUserStage.getOwner().getHeight() / 2) - (pickDefaultUserStage.getHeight() / 2));
        pickDefaultUserStage.showAndWait();

        log.fine("pickDefaultUser has been closed.");
        return user[0];
    }

    public void openDirectory(final ArrayList<File> directories, final Stage oldStage) {
        log.fine("openDirectory has been opened.");

        Stage openDirectoryStage = new Stage();
        openDirectoryStage.initOwner(oldStage);
        openDirectoryStage.initStyle(StageStyle.UNDECORATED);
        openDirectoryStage.initModality(Modality.APPLICATION_MODAL);
        GenericMethods.setIcon(openDirectoryStage);

        Label label = new Label();
        label.textProperty().bind(Strings.PickTheFolderYouWantToOpen);
        label.setPadding(new Insets(0, 0, 5, 0));

        ObservableList<File> directoriesObservable = FXCollections.observableArrayList(directories);
        directoriesObservable.sorted();
        ComboBox<File> directoryComboBox = new ComboBox<>(directoriesObservable);

        Button openAllButton = new Button(), openSelectedButton = new Button(), exitButton = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        openAllButton.textProperty().bind(Strings.OpenAll);
        openSelectedButton.textProperty().bind(Strings.OpenSelected);
        openAllButton.setOnAction(e -> {
            FileManager fileManager = new FileManager();
            directories.forEach(fileManager::openFolder);
            openDirectoryStage.close();
        });
        openSelectedButton.setOnAction(event -> {
            new FileManager().openFolder(directoryComboBox.getValue());
            openDirectoryStage.close();
        });
        exitButton.setOnAction(e -> openDirectoryStage.close());

        HBox buttonHBox = new HBox();
        buttonHBox.getChildren().addAll(openAllButton, openSelectedButton, exitButton);
        buttonHBox.setAlignment(Pos.BOTTOM_CENTER);
        buttonHBox.setPadding(new Insets(5, 5, 5, 5));
        buttonHBox.setSpacing(3);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, directoryComboBox, buttonHBox);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(6, 6, 6, 6));

        Platform.runLater(() -> new MoveStage(layout, oldStage, false));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/ListSelectBox.css");

        openDirectoryStage.setScene(scene);
        openDirectoryStage.show();
        openDirectoryStage.hide();
        openDirectoryStage.setX(openDirectoryStage.getOwner().getX() + (openDirectoryStage.getOwner().getWidth() / 2) - (openDirectoryStage.getWidth() / 2));
        openDirectoryStage.setY(openDirectoryStage.getOwner().getY() + (openDirectoryStage.getOwner().getHeight() / 2) - (openDirectoryStage.getHeight() / 2));
        openDirectoryStage.showAndWait();

        log.fine("openDirectory has been closed.");
    }

    public File pickDirectory(final StringProperty message, final ArrayList<File> files, final Stage oldStage) {
        log.fine("pickDirectory has been opened.");

        Stage pickDirectoryStage = new Stage();
        pickDirectoryStage.initOwner(oldStage);
        pickDirectoryStage.initStyle(StageStyle.UNDECORATED);
        pickDirectoryStage.initModality(Modality.APPLICATION_MODAL);
        pickDirectoryStage.setMinWidth(250);
        GenericMethods.setIcon(pickDirectoryStage);

        Label label = new Label();
        label.textProperty().bind(message);

        ObservableList<File> fileList = FXCollections.observableArrayList(files);
        fileList.sorted();
        ComboBox<File> comboBox = new ComboBox<>(fileList);

        Button submit = new Button(), exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        submit.textProperty().bind(Strings.Submit);
        final File[] directory = new File[1];
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().toString().isEmpty())
                    new MessageBox(new StringProperty[]{Strings.PleaseChooseAFolder}, pickDirectoryStage);
                else {
                    directory[0] = comboBox.getValue();
                    pickDirectoryStage.close();
                }
            }
        });
        exit.setOnAction(e -> pickDirectoryStage.close());

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));
        buttonLayout.setSpacing(3);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 6, 6, 6));

        Platform.runLater(() -> new MoveStage(layout, oldStage, false));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/ListSelectBox.css");

        pickDirectoryStage.setScene(scene);
        pickDirectoryStage.show();
        pickDirectoryStage.hide();
        pickDirectoryStage.setX(pickDirectoryStage.getOwner().getX() + (pickDirectoryStage.getOwner().getWidth() / 2) - (pickDirectoryStage.getWidth() / 2));
        pickDirectoryStage.setY(pickDirectoryStage.getOwner().getY() + (pickDirectoryStage.getOwner().getHeight() / 2) - (pickDirectoryStage.getHeight() / 2));
        pickDirectoryStage.showAndWait();

        log.fine("pickDirectory has been closed.");
        return directory[0];
    }

    public int[] pickSeasonEpisode(int userID, final int showID, final ShowInfoController showInfoController, final Stage oldStage) {
        log.fine("pickSeasonEpisode has been opened.");

        Stage pickSeasonEpisodeStage = new Stage();
        pickSeasonEpisodeStage.initOwner(oldStage);
        pickSeasonEpisodeStage.initStyle(StageStyle.UNDECORATED);
        pickSeasonEpisodeStage.initModality(Modality.APPLICATION_MODAL);
        pickSeasonEpisodeStage.setMinWidth(250);
        GenericMethods.setIcon(pickSeasonEpisodeStage);

        Label label = new Label();
        label.textProperty().bind(Strings.PickTheSeasonAndEpisode);

        // TODO Make this support option to display all known episodes or only currently found episodes.
        ArrayList<Integer> seasonsString = new ArrayList<>();
        seasonsString.addAll(new ArrayList<>(showInfoController.getSeasonsList(showID)));
        Collections.sort(seasonsString);
        ObservableList<Integer> seasonsList = FXCollections.observableArrayList(seasonsString);
        ComboBox<Integer> seasonsComboBox = new ComboBox<>(seasonsList);
        ArrayList<Integer> episodesArrayList = new ArrayList<>();
        ObservableList<Integer> episodesList = FXCollections.observableArrayList(episodesArrayList);
        ComboBox<Integer> episodesComboBox = new ComboBox<>(episodesList);

        int season = ClassHandler.userInfoController().getCurrentUserSeason(userID, showID);
        int episode = ClassHandler.userInfoController().getCurrentUserEpisode(userID, showID);
        if (ClassHandler.showInfoController().getEpisode(userID, showID, season, episode) == null || ClassHandler.showInfoController().getEpisode(userID, showID, season, episode).getEpisodeFilename().isEmpty()) {
            episodesComboBox.setDisable(true);
        } else {
            seasonsComboBox.getSelectionModel().select((Integer) season);
            episodesComboBox.getSelectionModel().select((Integer) episode);
        }

        Button submit = new Button(), exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        submit.textProperty().bind(Strings.Submit);
        final int[] choice = new int[2];
        submit.setOnAction(e -> {
            if (seasonsComboBox.getValue() != null && !seasonsComboBox.getValue().toString().isEmpty()) {
                if (episodesComboBox.getValue() != null && !episodesComboBox.getValue().toString().isEmpty()) {
                    choice[0] = seasonsComboBox.getValue();
                    choice[1] = episodesComboBox.getValue();
                    pickSeasonEpisodeStage.close();
                } else
                    new MessageBox(new StringProperty[]{Strings.YouHaveToPickAEpisode}, pickSeasonEpisodeStage);
            } else new MessageBox(new StringProperty[]{Strings.YouHaveToPickASeason}, pickSeasonEpisodeStage);
        });
        exit.setOnAction(e -> {
            choice[0] = -1;
            choice[1] = -1;
            pickSeasonEpisodeStage.close();
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
        comboBoxLayout.setSpacing(3);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, comboBoxLayout, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 6, 6, 6));

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int oldValue = -1;
                while (pickSeasonEpisodeStage.isShowing()) {
                    if (seasonsComboBox.getValue() != null && !seasonsComboBox.getValue().toString().isEmpty() && seasonsComboBox.getValue() != oldValue) {
                        oldValue = seasonsComboBox.getValue();
                        episodesArrayList.clear();
                        episodesArrayList.addAll(new ArrayList<>(showInfoController.getEpisodesList(aShow, seasonsComboBox.getValue())));
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
            new MoveStage(layout, oldStage, false);
            new Thread(task).start();
        });

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/ListSelectBox.css");
        pickSeasonEpisodeStage.setScene(scene);
        pickSeasonEpisodeStage.show();
        pickSeasonEpisodeStage.hide();
        pickSeasonEpisodeStage.setX(pickSeasonEpisodeStage.getOwner().getX() + (pickSeasonEpisodeStage.getOwner().getWidth() / 2) - (pickSeasonEpisodeStage.getWidth() / 2));
        pickSeasonEpisodeStage.setY(pickSeasonEpisodeStage.getOwner().getY() + (pickSeasonEpisodeStage.getOwner().getHeight() / 2) - (pickSeasonEpisodeStage.getHeight() / 2));
        pickSeasonEpisodeStage.showAndWait();

        log.fine("pickSeasonEpisode has been closed.");
        return choice;
    }

    public Object[] pickLanguage(final Collection<String> languages, final boolean preSelectDefault, final Stage oldStage) {
        log.fine("pickLanguage has been opened.");

        Stage pickLanguageStage = new Stage();
        if (oldStage != null) pickLanguageStage.initOwner(oldStage);
        pickLanguageStage.getIcons().add(GenericMethods.getImage(Variables.Logo));
        pickLanguageStage.initStyle(StageStyle.UNDECORATED);
        pickLanguageStage.initModality(Modality.APPLICATION_MODAL);
        pickLanguageStage.setMinWidth(250);

        Label label = new Label();
        label.textProperty().bind(Strings.PleaseChooseYourLanguage);

        log.info(String.valueOf(languages));
        ObservableList<String> languageList = FXCollections.observableArrayList(languages);
        languageList.sorted();
        ComboBox<String> comboBox = new ComboBox<>(languageList);
        if (preSelectDefault && comboBox.getItems().contains(Variables.DefaultLanguage[1]))
            comboBox.getSelectionModel().select(Variables.DefaultLanguage[1]);

        Button submit = new Button(), exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        submit.textProperty().bind(Strings.Submit);
        Object[] result = {"-2", false};
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                result[0] = comboBox.getValue();
                pickLanguageStage.close();
            }
        });
        exit.setOnAction(e -> pickLanguageStage.close());

        CheckBox makeLanguageDefault = new CheckBox();
        makeLanguageDefault.setPadding(new Insets(0, 6, 0, 0));
        Tooltip makeLanguageDefaultTooltip = new Tooltip();
        makeLanguageDefaultTooltip.setId("tooltip");
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
        layout.setPadding(new Insets(6, 6, 6, 6));

        Platform.runLater(() -> new MoveStage(layout, oldStage, false));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/ListSelectBox.css");

        pickLanguageStage.setScene(scene);
        pickLanguageStage.show();
        pickLanguageStage.hide();
        if (pickLanguageStage.getOwner() != null) {
            pickLanguageStage.setX(pickLanguageStage.getOwner().getX() + (pickLanguageStage.getOwner().getWidth() / 2) - (pickLanguageStage.getWidth() / 2));
            pickLanguageStage.setY(pickLanguageStage.getOwner().getY() + (pickLanguageStage.getOwner().getHeight() / 2) - (pickLanguageStage.getHeight() / 2));
        }
        pickLanguageStage.showAndWait();

        log.fine("pickLanguage has been closed.");
        return result;
    }

    public String pickShow(final ArrayList<String> shows, final Stage oldStage) {
        log.fine("pickShow has been opened.");

        Stage pickShowStage = new Stage();
        pickShowStage.initOwner(oldStage);
        pickShowStage.initStyle(StageStyle.UNDECORATED);
        pickShowStage.initModality(Modality.APPLICATION_MODAL);
        GenericMethods.setIcon(pickShowStage);

        Label label = new Label();
        label.textProperty().bind(Strings.PickShowToUnHide);

        ObservableList<String> showsList = FXCollections.observableList(shows);
        showsList.sorted();
        ComboBox<String> showComboBox = new ComboBox<>(showsList);

        Button submit = new Button(), exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        submit.textProperty().bind(Strings.Submit);
        String[] returnShow = {Strings.EmptyString};
        submit.setOnAction(e -> {
            if (showComboBox.getValue() != null && !showComboBox.getValue().isEmpty()) {
                returnShow[0] = showComboBox.getValue();
                pickShowStage.close();
            }
        });
        exit.setOnAction(e -> pickShowStage.close());

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 0, 0, 0));
        buttonLayout.setSpacing(3);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, showComboBox, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 6, 6, 6));

        Platform.runLater(() -> new MoveStage(layout, oldStage, false));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/ListSelectBox.css");

        pickShowStage.setScene(scene);
        pickShowStage.show();
        pickShowStage.hide();
        pickShowStage.setX(pickShowStage.getX() + (pickShowStage.getWidth() / 2) - (pickShowStage.getWidth() / 2));
        pickShowStage.setY(pickShowStage.getY() + (pickShowStage.getHeight() / 2) - (pickShowStage.getHeight() / 2));
        pickShowStage.showAndWait();

        log.fine("pickShow has been closed.");
        return returnShow[0];
    }
}
