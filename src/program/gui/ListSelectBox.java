package program.gui;

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
import program.graphics.ImageLoader;
import program.information.ShowInfoController;
import program.io.MoveWindow;
import program.util.Strings;
import program.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ListSelectBox {
    private static final Logger log = Logger.getLogger(ListSelectBox.class.getName());

    public String display(String message, ArrayList<String> users, Window oldWindow) {
        final String[] userName = new String[1];
        userName[0] = Strings.DefaultUsername;

        Stage window = new Stage();
        window.getIcons().add(ImageLoader.getImage("/image/MTrackLogo.png"));
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
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
                userName[0] = textBox.display("Please enter your username: ", "Use default username?", "PublicDefault", window);
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
            new MoveWindow().moveWindow(window);
        });
        window.showAndWait();

        return userName[0];
    }

    public String defaultUser(String message, ArrayList<String> users, Window oldWindow) {
        final String[] userName = new String[1];
        userName[0] = Variables.EmptyString;

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

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().isEmpty()) {
                    MessageBox messageBox = new MessageBox();
                    messageBox.display("Default user not set.", window);
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

        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window);
        });
        window.showAndWait();

        return userName[0];
    }

    public File directories(String message, ArrayList<File> files, Window oldWindow) {
        final File[] file = new File[1];
        file[0] = new File(Variables.EmptyString);

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

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null) {
                if (comboBox.getValue().toString().isEmpty()) {
                    MessageBox messageBox = new MessageBox();
                    messageBox.display("Please choose a folder.", window);
                } else {
                    file[0] = comboBox.getValue();
                    window.close();
                }
            }
        });

        Button exit = new Button("X");
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
            new MoveWindow().moveWindow(window);
        });
        window.showAndWait();

        return file[0];
    }

    public String[] pickSeasonEpisode(String message, String aShow, Set<Integer> seasons, Window oldWindow) {
        final String[] choice = new String[2];

        Stage window = new Stage();
        window.getIcons().add(ImageLoader.getImage("/image/MTrackLogo.png"));
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


        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                choice[0] = comboBox.getValue();
                choice[1] = pickEpisode("Pick the Episode", ShowInfoController.getEpisodesList(aShow, Integer.parseInt(choice[0])), window.getWidth(), window.getHeight(), window);
                window.close();
            } else new MessageBox().display("You have to pick a season!", window);
        });

        Button exit = new Button("X");
        exit.setOnAction(e -> {
            choice[0] = "-1";
            choice[1] = "-1";
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
            new MoveWindow().moveWindow(window);
        });
        window.showAndWait();

        return choice;
    }

    private String pickEpisode(String message, Set<String> episodes, Double width, Double height, Window oldWindow) {
        final String[] choice = new String[1];

        Stage window = new Stage();
        window.getIcons().add(ImageLoader.getImage("/image/MTrackLogo.png"));
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setWidth(width);
        window.setHeight(height);

        Label label = new Label();
        label.setText(message);

        // Needed, If you directly use the episodes Set, It completely removes them from the showsFile for some reason (Until you restart).
        ArrayList<String> episodesArrayList = new ArrayList<>();
        episodesArrayList.addAll(episodes.stream().collect(Collectors.toList()));

        log.info("Sorting the episodes before displaying...");
        ArrayList<String> episodesSorted = new ArrayList<>();
        while (!episodesArrayList.isEmpty()) {
            Iterator<String> stringIterator = episodesArrayList.iterator();
            int lowestEpisodeInt = -1;
            String lowestEpisodeString = null;
            while (stringIterator.hasNext()) {
                String episode = stringIterator.next();
                log.info(episode);
                int episodeInt;
                if (episode.contains("+")) {
                    log.info("Processing double episode...");
                    episodeInt = Integer.parseInt(episode.split("\\+")[0]);
                } else episodeInt = Integer.parseInt(episode);
                if (lowestEpisodeInt == -1) {
                    lowestEpisodeInt = episodeInt;
                    lowestEpisodeString = episode;
                } else if (episodeInt < lowestEpisodeInt) {
                    lowestEpisodeInt = Integer.parseInt(episode);
                    lowestEpisodeString = episode;
                }
            }
            episodesSorted.add(lowestEpisodeString);
            episodesArrayList.remove(lowestEpisodeString);
        }
        log.info("Finished sorting the episodes.");

        ObservableList<String> seasonsList = FXCollections.observableArrayList(episodesSorted);
        ComboBox<String> comboBox = new ComboBox<>(seasonsList);

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                choice[0] = comboBox.getValue();
                window.close();
            } else new MessageBox().display("You have to pick a episode!", window);
        });

        Button exit = new Button("X");
        exit.setOnAction(e -> {
            choice[0] = "-1";
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
            new MoveWindow().moveWindow(window);
        });
        window.showAndWait();

        return choice[0];
    }
}