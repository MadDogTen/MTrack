package com.maddogten.mtrack.FXMLControllers;

import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.OperatingSystem;
import com.maddogten.mtrack.util.VideoPlayer;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

public class VideoPlayerSelector implements Initializable {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(VideoPlayerSelector.class.getName());
    private final int userID;

    @FXML
    private Pane mainPane;
    @FXML
    private ComboBox<VideoPlayer.VideoPlayerEnum> videoPlayerTypeComboBox;
    @FXML
    private Button submitButton;
    @FXML
    private TextField videoPlayerLocationTextField;
    @FXML
    private Button closeButton;
    @FXML
    private Text videoPlayerLocationText;
    @FXML
    private Button autoDetectButton;
    @FXML
    private ComboBox<File> videoPlayerChoicesComboBox;

    private VideoPlayer result;

    public VideoPlayerSelector(int userID) {
        this.userID = userID;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) { // TODO Add localization whole file
        videoPlayerTypeComboBox.getItems().addAll(VideoPlayer.VideoPlayerEnum.values());
        result = ClassHandler.userInfoController().getVideoPlayer(userID);

        videoPlayerTypeComboBox.setValue(result.getVideoPlayerEnum());
        if (result.getVideoPlayerEnum() != VideoPlayer.VideoPlayerEnum.OTHER)
            videoPlayerLocationTextField.setText(result.getVideoPlayerLocation().getPath());

        final boolean[] warningShown = {false};
        videoPlayerTypeComboBox.setOnShown(event -> {
            if (!warningShown[0]) {
                new MessageBox(new StringProperty[]{new SimpleStringProperty("Warning, Improperly setting this value may cause shows not to play correctly."), new SimpleStringProperty("If you are unsure, Select Other, or let it automatically pick.")}, (Stage) mainPane.getScene().getWindow());
                warningShown[0] = true;
                videoPlayerTypeComboBox.show();
            }
        });

        videoPlayerLocationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean notFound = true;
            for (VideoPlayer.VideoPlayerEnum videoPlayerEnum : VideoPlayer.VideoPlayerEnum.values()) {
                if (videoPlayerEnum != VideoPlayer.VideoPlayerEnum.OTHER && newValue.contains(videoPlayerEnum.getPartFileName())) {
                    videoPlayerTypeComboBox.setValue(videoPlayerEnum);
                    notFound = false;
                    break;
                }
            }
            if (notFound && videoPlayerTypeComboBox.getValue() != VideoPlayer.VideoPlayerEnum.OTHER)
                videoPlayerTypeComboBox.setValue(VideoPlayer.VideoPlayerEnum.OTHER);
        });

        autoDetectButton.setOnAction(event -> {
            if (!videoPlayerTypeComboBox.getItems().isEmpty()) videoPlayerChoicesComboBox.getItems().clear();

            Set<File> locationChoices = new LinkedHashSet<>();
            for (VideoPlayer.VideoPlayerEnum videoPlayerEnum : VideoPlayer.VideoPlayerEnum.values()) {
                if (videoPlayerEnum != VideoPlayer.VideoPlayerEnum.OTHER) {
                    locationChoices.addAll(OperatingSystem.findVideoPlayers(videoPlayerEnum));
                }
            }

            if (!locationChoices.isEmpty()) {
                videoPlayerChoicesComboBox.getItems().addAll(locationChoices);
                videoPlayerLocationTextField.setVisible(false);
                videoPlayerChoicesComboBox.setVisible(true);
                videoPlayerLocationTextField.clear();
            }
        });

        videoPlayerChoicesComboBox.setOnAction(event -> {
            if (!videoPlayerChoicesComboBox.getItems().isEmpty() && videoPlayerChoicesComboBox.getValue() != null && !videoPlayerChoicesComboBox.getValue().getPath().isEmpty()) {
                videoPlayerLocationTextField.setText(videoPlayerChoicesComboBox.getValue().getPath());
                videoPlayerChoicesComboBox.setVisible(false);
                videoPlayerLocationTextField.setVisible(true);
                Platform.runLater(() -> {
                    videoPlayerChoicesComboBox.getSelectionModel().clearSelection();
                    videoPlayerChoicesComboBox.getItems().clear();
                });
            }
        });

        submitButton.setOnAction(event -> {
            if (videoPlayerLocationTextField.getText().toLowerCase().endsWith(".exe") && new File(videoPlayerLocationTextField.getText()).exists()) {
                result.setVideoPlayerEnum(videoPlayerTypeComboBox.getValue());
                result.setVideoPlayerLocation(new File(videoPlayerLocationTextField.getText()));
            /*if (userSettings.getVideoPlayer().getVideoPlayerEnum() == VideoPlayer.VideoPlayerEnum.OTHER) { // TODO Finish
                // Open Window warning some features won't be supported.
            }*/
                checkAndWarn();
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.close();
            } else
                new MessageBox(new StringProperty[]{new SimpleStringProperty("Incorrect File: \"" + videoPlayerLocationTextField.getText() + "\", Must be a valid file ending with \".exe\".")}, (Stage) mainPane.getScene().getWindow());
        });

        closeButton.setOnAction(event -> {

            result = ClassHandler.userInfoController().getVideoPlayer(userID);
           /* if (userSettings.getVideoPlayer().getVideoPlayerEnum() == VideoPlayer.VideoPlayerEnum.OTHER) { // TODO Finish
                // Open Window warning some features won't be supported.
            }*/
            checkAndWarn();
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.close();
        });

        new MoveStage(mainPane, null, false);
    }

    public VideoPlayer getResult() {
        return result;
    }

    private void checkAndWarn() {
        if (result.getVideoPlayerEnum() == VideoPlayer.VideoPlayerEnum.OTHER)
            new MessageBox(new StringProperty[]{new SimpleStringProperty("Notice- Video Player Type is set to \"Other\","), new SimpleStringProperty("Some features, Such as resuming a show at a saved position,"), new SimpleStringProperty(" and starting in fullscreen won't function.")}, (Stage) mainPane.getScene().getWindow());
    }
}
