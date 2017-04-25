package com.maddogten.mtrack.FXMLControllers;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.gui.TextBox;
import com.maddogten.mtrack.information.show.DisplayShow;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class ShowPlaying implements Initializable {
    private static final Logger log = Logger.getLogger(ShowPlaying.class.getName());

    private final DisplayShow show;
    private final int userID;
    private boolean runTask = true;

    @FXML
    private Button restartButton;

    @FXML
    private ImageView restartImageView;

    @FXML
    private Text haveYouWatchedText;

    @FXML
    private Button yesButton;

    @FXML
    private Pane mainPane;

    @FXML
    private Button noButton;

    @FXML
    private Button nextEpisodeButton;

    @FXML
    private Text currentlyPlayingText;

    @FXML
    private Text showNameText;

    @FXML
    private Text seasonText;

    @FXML
    private Text episodeText;

    @FXML
    private MenuItem setShowPositionMenuItem;

    public ShowPlaying(int userID, final DisplayShow show) {
        this.userID = userID;
        this.show = show;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        haveYouWatchedText.textProperty().bind(Strings.HaveYouWatchedTheShow);
        haveYouWatchedText.setTextAlignment(TextAlignment.CENTER);
        yesButton.textProperty().bind(Strings.Yes);
        noButton.textProperty().bind(Strings.No);
        nextEpisodeButton.textProperty().bind(Strings.NextEpisode);

        currentlyPlayingText.textProperty().bind(Strings.CurrentlyPlaying);
        currentlyPlayingText.setTextAlignment(TextAlignment.CENTER);

        showNameText.setWrappingWidth(com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(show.getShow(), Variables.Font));
        showNameText.setTextAlignment(TextAlignment.LEFT);
        showNameText.setText(show.getShow());
        Tooltip showNameTextTooltip = new Tooltip(show.getShow());
        showNameTextTooltip.getStyleClass().add("tooltip");
        Tooltip.install(showNameText, showNameTextTooltip);

        setSeasonEpisodeText();

        yesButton.setOnAction(e -> {
            ClassHandler.userInfoController().clearEpisodeSettings(userID, ClassHandler.showInfoController().getEpisodeID(show.getShowID(), show.getSeason(), show.getEpisode()));
            ClassHandler.userInfoController().changeEpisode(userID, show.getShowID(), -2);
            Controller.updateShowField(show.getShowID(), true);
            ClassHandler.controller().setTableSelection(-2);
            Stage stage = (Stage) mainPane.getScene().getWindow();
            runTask = false;
            stage.close();
        });
        noButton.setOnAction(e -> {
            ClassHandler.controller().setTableSelection(-2);
            Stage stage = (Stage) mainPane.getScene().getWindow();
            runTask = false;
            stage.close();
        });
        nextEpisodeButton.setDisable(!(show.getRemaining() > 1));
        nextEpisodeButton.setOnAction(e -> {
            ClassHandler.userInfoController().changeEpisode(userID, show.getShowID(), -2);
            Controller.updateShowField(show.getShowID(), true);
            setSeasonEpisodeText();
            if (show.getRemaining() <= 1)
                nextEpisodeButton.setDisable(true);
            if (ClassHandler.showInfoController().doesEpisodeExist(show.getShowID(), show.getSeason(), show.getEpisode()) || ClassHandler.userInfoController().isProperEpisodeInNextSeason(userID, show.getShowID())) {
                if (!ClassHandler.userInfoController().playAnyEpisode(show.getShowID(), ClassHandler.showInfoController().getEpisodeID(show.getShowID(), show.getSeason(), show.getEpisode()))) {
                    log.info("Unable to play: " + show.getShow() + " | Season: " + show.getSeason() + " | Episode: " + show.getEpisode());
                    new MessageBox(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
                    Stage stage = (Stage) mainPane.getScene().getWindow();
                    runTask = false;
                    stage.close();
                }
            } else {
                log.info("Unable to play: " + show.getShow() + " | Season: " + show.getSeason() + " | Episode: " + show.getEpisode());
                new MessageBox(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
                Stage stage = (Stage) mainPane.getScene().getWindow();
                runTask = false;
                stage.close();
            }
        });
        restartButton.setOnAction(e -> {
            if (!ClassHandler.userInfoController().playAnyEpisode(show.getShowID(), ClassHandler.showInfoController().getEpisodeID(show.getShowID(), show.getSeason(), show.getEpisode()))) {
                log.info("Unable to play: " + show.getShow() + " | Season: " + show.getSeason() + " | Episode: " + show.getEpisode());
                new MessageBox(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
                Stage stage = (Stage) mainPane.getScene().getWindow();
                runTask = false;
                stage.close();
            }
        });

        setShowPositionMenuItem.setOnAction(e -> {
            int position[] = new TextBox().enterNumber(new SimpleStringProperty("Enter the shows current position in seconds:"), new StringProperty[]{new SimpleStringProperty("Hour"), new SimpleStringProperty("Minute"), new SimpleStringProperty("Second")}, 3, (Stage) mainPane.getScene().getWindow());
            int timeInSeconds = ((position[0] * 60) * 60) + (position[1] * 60) + position[2];
            if (timeInSeconds > 0) {
                ClassHandler.userInfoController().setEpisodePosition(userID, ClassHandler.showInfoController().getEpisodeID(show.getShowID(), show.getSeason(), show.getEpisode()), timeInSeconds);
                Stage stage = (Stage) mainPane.getScene().getWindow();
                runTask = false;
                stage.close();
            }
        });

        mainPane.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (yesButton.isFocused()) yesButton.fire();
                else if (noButton.isFocused()) noButton.fire();
                else if (nextEpisodeButton.isFocused()) nextEpisodeButton.fire();
                else if (restartButton.isFocused()) restartButton.fire();
            }
        });

        new MoveStage(mainPane, Main.stage, false);

        Platform.runLater(() -> {
            Stage stage = (Stage) mainPane.getScene().getWindow();
            if (com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(show.getShow(), Variables.Font) < stage.getWidth())
                showNameText.setX((stage.getWidth() / 2) - (showNameText.getLayoutBounds().getWidth() / 2));
            new Thread(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (runTask) {
                        wait(1000);
                        nextEpisodeButton.setDisable(!(show.getRemaining() > 1));
                    }
                    return null;
                }
            }).start();
        });
    }

    private void setSeasonEpisodeText() {
        seasonText.setText(Strings.Season.getValue() + ":  " + this.show.getSeason());
        seasonText.setTextAlignment(TextAlignment.CENTER);
        episodeText.setText(Strings.Episode.getValue() + ":  " + this.show.getEpisode());
        episodeText.setTextAlignment(TextAlignment.CENTER);
    }
}