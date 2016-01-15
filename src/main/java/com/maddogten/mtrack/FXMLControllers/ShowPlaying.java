package com.maddogten.mtrack.FXMLControllers;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.information.UserInfoController;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.Strings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

    private final String aShow;
    private final Controller controller;
    private final ShowInfoController showInfoController;
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

    public ShowPlaying(String aShow, Controller controller, ShowInfoController showInfoController, UserInfoController userInfoController) {
        this.aShow = aShow;
        this.controller = controller;
        this.showInfoController = showInfoController;
        this.userInfoController = userInfoController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        haveYouWatchedText.textProperty().bind(Strings.HaveYouWatchedTheShow);
        yesButton.textProperty().bind(Strings.Yes);
        noButton.textProperty().bind(Strings.No);
        nextEpisodeButton.textProperty().bind(Strings.NextEpisode);

        currentlyPlayingText.textProperty().bind(Strings.CurrentlyPlaying);
        currentlyPlayingText.setTextAlignment(TextAlignment.CENTER);

        if (aShow.length() > 30) showNameText.setText(aShow.substring(0, 29));
        else showNameText.setText(aShow);
        showNameText.setTextAlignment(TextAlignment.CENTER);

        setSeasonEpisodeText();

        yesButton.setOnAction(e -> {
            userInfoController.changeEpisode(aShow, -2);
            Controller.updateShowField(aShow, true);
            controller.clearTableSelection();
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.close();
        });
        noButton.setOnAction(e -> {
            controller.clearTableSelection();
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.close();
        });
        nextEpisodeButton.setVisible(userInfoController.getRemainingNumberOfEpisodes(aShow, showInfoController) >= 1);
        nextEpisodeButton.setOnAction(e -> {
            userInfoController.changeEpisode(aShow, -2);
            Controller.updateShowField(aShow, true);
            setSeasonEpisodeText();
            if (userInfoController.getRemainingNumberOfEpisodes(aShow, showInfoController) == 1)
                nextEpisodeButton.setDisable(true);
            if (userInfoController.doesEpisodeExistInShowFile(aShow)) {
                if (!userInfoController.playAnyEpisode(aShow, userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow))) {
                    new MessageBox().message(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
                    Stage stage = (Stage) mainPane.getScene().getWindow();
                    stage.close();
                }
            } else {
                // This is being done because I set episodes 1 further when watched, which works when the season is ongoing. However, when moving onto a new season it breaks. This is a simple check to move
                // it into a new season if one is found, and no further episodes are found in the current season.
                if (userInfoController.getRemainingNumberOfEpisodes(aShow, showInfoController) > 0 && userInfoController.shouldSwitchSeasons(aShow, userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow))) {
                    log.info("No further episodes found for current season, further episodes found in next season, switching to new season.");
                    userInfoController.changeEpisode(aShow, -2);
                    Controller.updateShowField(aShow, true);
                    if (!userInfoController.playAnyEpisode(aShow, userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow))) {
                        new MessageBox().message(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
                        Stage stage = (Stage) mainPane.getScene().getWindow();
                        stage.close();
                    }
                }
            }
        });
        restartButton.setOnAction(e -> {
            if (!userInfoController.playAnyEpisode(aShow, userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow))) {
                new MessageBox().message(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.close();
            }
        });
        new MoveStage().moveStage(mainPane, Main.stage);
    }

    private void setSeasonEpisodeText() {
        seasonText.setText(Strings.Season.getValue() + ":  " + userInfoController.getCurrentSeason(aShow));
        seasonText.setTextAlignment(TextAlignment.CENTER);
        episodeText.setText(Strings.Episode.getValue() + ":  " + userInfoController.getCurrentEpisode(aShow));
        episodeText.setTextAlignment(TextAlignment.CENTER);
    }
}