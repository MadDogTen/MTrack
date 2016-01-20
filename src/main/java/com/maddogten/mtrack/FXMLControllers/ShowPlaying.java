package com.maddogten.mtrack.FXMLControllers;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.information.UserInfoController;
import com.maddogten.mtrack.information.show.DisplayShows;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
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

    private final TableRow<DisplayShows> row;
    private final Controller controller;
    private final UserInfoController userInfoController;

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

    public ShowPlaying(TableRow<DisplayShows> row, Controller controller, UserInfoController userInfoController) {
        this.row = row;
        this.controller = controller;
        this.userInfoController = userInfoController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        haveYouWatchedText.textProperty().bind(Strings.HaveYouWatchedTheShow);
        haveYouWatchedText.setTextAlignment(TextAlignment.CENTER);
        yesButton.textProperty().bind(Strings.Yes);
        noButton.textProperty().bind(Strings.No);
        nextEpisodeButton.textProperty().bind(Strings.NextEpisode);

        currentlyPlayingText.textProperty().bind(Strings.CurrentlyPlaying);
        currentlyPlayingText.setTextAlignment(TextAlignment.CENTER);

        showNameText.setWrappingWidth(com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(row.getItem().getShow(), Variables.Font));
        showNameText.setTextAlignment(TextAlignment.LEFT);
        showNameText.setText(row.getItem().getShow());
        Tooltip showNameTextTooltip = new Tooltip(row.getItem().getShow());
        showNameTextTooltip.getStyleClass().add("tooltip");
        Tooltip.install(
                showNameText,
                showNameTextTooltip
        );

        setSeasonEpisodeText();

        yesButton.setOnAction(e -> {
            userInfoController.changeEpisode(row.getItem().getShow(), -2);
            Controller.updateShowField(row.getItem().getShow(), true);
            controller.setTableSelection(-2);
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.close();
        });
        noButton.setOnAction(e -> {
            controller.setTableSelection(-2);
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.close();
        });
        nextEpisodeButton.setDisable(!(row.getItem().getRemaining() > 1));
        nextEpisodeButton.setOnAction(e -> {
            userInfoController.changeEpisode(row.getItem().getShow(), -2);
            Controller.updateShowField(row.getItem().getShow(), true);
            controller.setTableSelection(row.getIndex());
            setSeasonEpisodeText();
            if (row.getItem().getRemaining() <= 1)
                nextEpisodeButton.setDisable(true);
            if (userInfoController.doesEpisodeExistInShowFile(row.getItem().getShow()) || userInfoController.isProperEpisodeInNextSeason(row.getItem().getShow())) {
                if (!userInfoController.playAnyEpisode(row.getItem().getShow(), row.getItem().getSeason(), row.getItem().getEpisode())) {
                    log.info("Unable to play: " + row.getItem().getShow() + " | Season: " + row.getItem().getSeason() + " | Episode: " + row.getItem().getEpisode());
                    new MessageBox().message(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
                    Stage stage = (Stage) mainPane.getScene().getWindow();
                    stage.close();
                }
            } else {
                log.info("Unable to play: " + row.getItem().getShow() + " | Season: " + row.getItem().getSeason() + " | Episode: " + row.getItem().getEpisode());
                new MessageBox().message(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.close();
            }
        });
        restartButton.setOnAction(e -> {
            if (!userInfoController.playAnyEpisode(row.getItem().getShow(), row.getItem().getSeason(), row.getItem().getEpisode())) {
                log.info("Unable to play: " + row.getItem().getShow() + " | Season: " + row.getItem().getSeason() + " | Episode: " + row.getItem().getEpisode());
                new MessageBox().message(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.close();
            }
        });
        new MoveStage().moveStage(mainPane, Main.stage);

        Platform.runLater(() -> {
            Stage stage = (Stage) mainPane.getScene().getWindow();
            if (com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(row.getItem().getShow(), Variables.Font) < stage.getWidth())
                showNameText.setX((stage.getWidth() / 2) - (showNameText.getLayoutBounds().getWidth() / 2));
        });
        // This makes the Show name scroll if text goes out of stage. I decided not to do it this way in favor of a tooltip, But am keeping it in case I change my mind.
        /*Task<Void> moveText = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(500);
                Stage stage = (Stage) mainPane.getScene().getWindow();
                double textLength = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(aShow, Variables.Font);
                if (textLength > stage.getWidth()) {
                    int time = GenericMethods.getTimeSeconds();
                    boolean moveLeft = true, pause = false;
                    while (stage.isShowing()) {
                        if (!pause) {
                            if ((showNameText.getX() + textLength) < stage.getWidth() - 8 || showNameText.getX() > 8) {
                                moveLeft = !moveLeft;
                                time = GenericMethods.getTimeSeconds();
                                pause = true;
                            }
                            if (moveLeft) showNameText.setX(showNameText.getX() - .5);
                            if (!moveLeft) showNameText.setX(showNameText.getX() + .5);
                        } else if (GenericMethods.timeTakenSeconds(time) > 4) pause = false;
                        Thread.sleep(80);
                    }
                } else showNameText.setX((stage.getWidth() / 2) - (showNameText.getLayoutBounds().getWidth() / 2));
                return null;
            }
        };
        new Thread(moveText).start();*/
    }

    private void setSeasonEpisodeText() {
        seasonText.setText(Strings.Season.getValue() + ":  " + userInfoController.getCurrentSeason(row.getItem().getShow()));
        seasonText.setTextAlignment(TextAlignment.CENTER);
        episodeText.setText(Strings.Episode.getValue() + ":  " + userInfoController.getCurrentEpisode(row.getItem().getShow()));
        episodeText.setTextAlignment(TextAlignment.CENTER);
    }
}