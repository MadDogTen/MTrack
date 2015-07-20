package main.java.com.maddogten.mtrack.gui;

import javafx.application.Platform;
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
import javafx.stage.Window;
import main.java.com.maddogten.mtrack.Main;
import main.java.com.maddogten.mtrack.information.ShowInfoController;
import main.java.com.maddogten.mtrack.io.MoveWindow;
import main.java.com.maddogten.mtrack.util.ImageLoader;
import main.java.com.maddogten.mtrack.util.Strings;
import main.java.com.maddogten.mtrack.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
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

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit);
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
                    MessageBox messageBox = new MessageBox();
                    messageBox.display(Strings.DefaultUserNotSet, window);
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
                    MessageBox messageBox = new MessageBox();
                    messageBox.display(Strings.PleaseChooseAFolder, window);
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
    public int[] pickSeasonEpisode(String message, String aShow, Set<Integer> seasons, ShowInfoController showInfoController, Window oldWindow) {
        final int[] choice = new int[2];

        Stage window = new Stage();
        window.getIcons().add(ImageLoader.getImage(Variables.Logo));
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        ArrayList<String> seasonsString = new ArrayList<>();
        seasonsString.addAll(seasons.stream().map(String::valueOf).collect(Collectors.toList()));

        ObservableList<String> seasonsList = FXCollections.observableArrayList(seasonsString);
        seasonsList.sorted();
        ComboBox<String> comboBox = new ComboBox<>(seasonsList);


        Button submit = new Button(Strings.Submit);
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null && !comboBox.getValue().isEmpty() && comboBox.getValue().matches("[0-9]*")) {
                choice[0] = Integer.parseInt(comboBox.getValue());
                choice[1] = pickEpisode(Strings.PickTheEpisode, showInfoController.getEpisodesList(aShow, choice[0]), window.getWidth(), window.getHeight(), window);
                window.close();
            } else new MessageBox().display(Strings.YouHaveToPickASeason, window);
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

        return choice;
    }

    @SuppressWarnings("SameParameterValue")
    private int pickEpisode(String message, Set<Integer> episodes, Double width, Double height, Window oldWindow) { //TODO Clean this up from the switch
        final int[] choice = new int[1];

        Stage window = new Stage();
        window.getIcons().add(ImageLoader.getImage(Variables.Logo));
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setWidth(width);
        window.setHeight(height);

        Label label = new Label();
        label.setText(message);

        ArrayList<Integer> episodesArrayList = new ArrayList<>();
        episodesArrayList.addAll(episodes.stream().collect(Collectors.toList()));

        log.info("Sorting the episodes before displaying...");
        ArrayList<Integer> episodesSorted = new ArrayList<>();
        while (!episodesArrayList.isEmpty()) {
            Iterator<Integer> stringIterator = episodesArrayList.iterator();
            int lowestEpisodeInt = -1;
            int lowestEpisodeString = 0;
            while (stringIterator.hasNext()) {
                int episode = stringIterator.next();
                int episodeInt;
                episodeInt = episode;
                if (lowestEpisodeInt == -1 || episodeInt < lowestEpisodeInt) {
                    lowestEpisodeInt = episodeInt;
                    lowestEpisodeString = episode;
                }
            }
            episodesSorted.add(lowestEpisodeString);
            episodesArrayList.remove(lowestEpisodeString);
        }
        log.info("Finished sorting the episodes.");

        ObservableList<Integer> seasonsList = FXCollections.observableArrayList(episodesSorted);
        ComboBox<Integer> comboBox = new ComboBox<>(seasonsList);

        Button submit = new Button(Strings.Submit);
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null && !comboBox.getValue().toString().isEmpty()) {
                choice[0] = comboBox.getValue();
                window.close();
            } else new MessageBox().display(Strings.YouHaveToPickAEpisode, window);
        });

        Button exit = new Button(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            choice[0] = -1;
            window.close();
        });

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(submit, exit);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(5, 5, 5, 5));

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

        return choice[0];
    }

    public String pickLanguage(String message, Collection<String> languages, Window oldWindow) {
        final String[] language = new String[1];
        language[0] = Variables.DefaultLanguage;

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

        if (Main.getMainRun().firstRun) {
            buttonLayout.getChildren().addAll(submit);
        } else {
            Button exit = new Button(Strings.ExitButtonText);
            exit.setOnAction(e -> {
                language[0] = "-1";
                window.close();
            });
            buttonLayout.getChildren().addAll(submit, exit);
        }

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