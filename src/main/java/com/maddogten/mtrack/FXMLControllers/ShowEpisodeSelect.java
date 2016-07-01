package com.maddogten.mtrack.FXMLControllers;

import com.maddogten.mtrack.gui.ShowEpisodeSelectBox;
import com.maddogten.mtrack.information.show.DisplayShow;
import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Variables;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Duration;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ShowEpisodeSelect implements Initializable {
    private static final Logger log = Logger.getLogger(ShowEpisodeSelect.class.getName());

    private final ShowEpisodeSelectBox showEpisodeSelectBox;
    private final DisplayShow show;
    private final IntegerProperty selectedSeason = new SimpleIntegerProperty(), selectedEpisode = new SimpleIntegerProperty();
    private boolean isExpanded, isChanging, stopChanging;

    @FXML
    private ComboBox<Integer> seasonComboBox;
    @FXML
    private ComboBox<Integer> episodeComboBox;
    @FXML
    private TextField seasonTextField;
    @FXML
    private TextField episodeTextField;
    @FXML
    private Text seasonText;
    @FXML
    private Pane mainPane;
    @FXML
    private Text episodeText;
    @FXML
    private SplitMenuButton resetTooMenuButton;
    @FXML
    private Text setText;
    @FXML
    private Button exitButton;
    @FXML
    private MenuItem resetBeginningButton;
    @FXML
    private MenuItem resetEndButton;
    @FXML
    private Button submitButton;
    @FXML
    private Button toggleTextFieldsButton;
    @FXML
    private ImageView toggleTextFieldsButtonImage;
    @FXML
    private Pane movePane;

    public ShowEpisodeSelect(final ShowEpisodeSelectBox showEpisodeSelectBox, final DisplayShow show) {
        this.show = show;
        this.showEpisodeSelectBox = showEpisodeSelectBox;
        this.selectedSeason.setValue(this.show.getSeason());
        this.selectedEpisode.setValue(this.show.getEpisode());
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        ArrayList<Integer> seasonsInt = new ArrayList<>();
        seasonsInt.addAll(ClassHandler.showInfoController().getSeasonsList(show.getShow()).stream().collect(Collectors.toList()));
        ObservableList<Integer> seasonsList = FXCollections.observableArrayList(seasonsInt);
        seasonComboBox.setItems(seasonsList);
        ArrayList<Integer> episodesArrayList = new ArrayList<>();
        ObservableList<Integer> episodesList = FXCollections.observableArrayList(episodesArrayList);
        episodeComboBox.setItems(episodesList);

        if (seasonComboBox.getItems().contains(selectedSeason.getValue()))
            seasonComboBox.getSelectionModel().select(selectedSeason.getValue());
        else seasonComboBox.setValue(selectedSeason.getValue());
        if (episodeComboBox.getItems().contains(selectedEpisode.getValue()))
            episodeComboBox.getSelectionModel().select(selectedEpisode.getValue());
        else episodeComboBox.setValue(selectedEpisode.getValue());

        seasonComboBox.setOnAction(e -> {
            selectedSeason.setValue(seasonComboBox.getValue());
            seasonTextField.setText(selectedSeason.getValue().toString());
            selectedEpisode.setValue(selectedSeason.getValue().equals(show.getSeason()) ? show.getEpisode() : 1);
            if (episodeComboBox.getItems().contains(selectedEpisode.getValue()))
                episodeComboBox.getSelectionModel().select(selectedEpisode.getValue());
            else episodeComboBox.setValue(selectedEpisode.getValue());
            episodeTextField.setText(selectedEpisode.getValue().toString());
        });

        episodeComboBox.setOnAction(e -> {
            selectedEpisode.setValue(episodeComboBox.getValue());
            episodeTextField.setText(selectedEpisode.getValue().toString());
        });

        seasonTextField.textProperty().setValue(String.valueOf(selectedSeason.getValue()));
        episodeTextField.textProperty().setValue(String.valueOf(selectedEpisode.getValue()));
        DecimalFormat format = new DecimalFormat("#.0");
        seasonTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                seasonComboBox.setValue(1);
                selectedSeason.setValue(1);
            } else {
                try {
                    if (seasonComboBox.getItems().contains(Integer.valueOf(seasonTextField.getText())))
                        seasonComboBox.getSelectionModel().select(Integer.valueOf(seasonTextField.getText()));
                    else seasonComboBox.setValue(Integer.valueOf(seasonTextField.getText()));
                    selectedSeason.setValue(Integer.valueOf(seasonTextField.getText()));
                } catch (NumberFormatException e) {
                    // Do nothing
                }
            }
        });
        seasonTextField.setTextFormatter(new TextFormatter<Integer>(c -> {
            if (c.getControlNewText().isEmpty()) return c;
            ParsePosition parsePosition = new ParsePosition(0);
            if (format.parse(c.getControlNewText(), parsePosition) == null || parsePosition.getIndex() < c.getControlNewText().length())
                return null;
            else return c;
        }));
        episodeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                episodeComboBox.setValue(1);
                selectedEpisode.setValue(1);
            } else {
                try {
                    if (episodeComboBox.getItems().contains(Integer.valueOf(episodeTextField.getText())))
                        episodeComboBox.getSelectionModel().select(Integer.valueOf(episodeTextField.getText()));
                    else episodeComboBox.setValue(Integer.valueOf(episodeTextField.getText()));
                    selectedEpisode.setValue(Integer.valueOf(episodeTextField.getText()));
                } catch (NumberFormatException e) {
                    // Do nothing
                }
            }
        });
        episodeTextField.setTextFormatter(new TextFormatter<Integer>(c -> {
            if (c.getControlNewText().isEmpty()) return c;
            ParsePosition parsePosition = new ParsePosition(0);
            if (format.parse(c.getControlNewText(), parsePosition) == null || parsePosition.getIndex() < c.getControlNewText().length())
                return null;
            else return c;
        }));
        resetBeginningButton.setOnAction(e -> {
            selectedSeason.setValue(ClassHandler.showInfoController().findLowestInt(ClassHandler.showInfoController().getSeasonsList(show.getShow())));
            selectedEpisode.setValue(ClassHandler.showInfoController().findLowestInt(ClassHandler.showInfoController().getEpisodesList(show.getShow(), selectedSeason.getValue())));
            updateFields();
        });
        resetEndButton.setOnAction(e -> {
            selectedSeason.setValue(ClassHandler.showInfoController().findHighestInt(ClassHandler.showInfoController().getSeasonsList(show.getShow())));
            selectedEpisode.setValue(ClassHandler.showInfoController().findHighestInt(ClassHandler.showInfoController().getEpisodesList(show.getShow(), selectedSeason.getValue())));
            updateFields();
        });

        submitButton.setOnAction(e -> showEpisodeSelectBox.closeStage());
        exitButton.setOnAction(e -> {
            selectedSeason.setValue(-2);
            selectedEpisode.setValue(-2);
            showEpisodeSelectBox.closeStage();
        });
        isExpanded = false;
        int minHeight = 102, maxHeight = 132, difference = maxHeight - minHeight;
        Platform.runLater(() -> {
            if (isExpanded) {
                mainPane.getScene().getWindow().setHeight(maxHeight);
                textFieldVisibility(true);
            } else {
                mainPane.getScene().getWindow().setHeight(minHeight);
                textFieldVisibility(false);
                movePane.setLayoutY(movePane.getLayoutY() - difference);
            }
        });
        movePane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, null)));
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(toggleTextFieldsButtonImage);
        rotateTransition.setDuration(new Duration(difference * 10));
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(180);
        toggleTextFieldsButton.setOnAction(e -> {
            Window window = mainPane.getScene().getWindow();
            if (Variables.specialEffects) {
                new Thread(new Task<Void>() {
                    @Override
                    protected Void call() throws InterruptedException {
                        if (isChanging) {
                            stopChanging = true;
                            while (stopChanging) Thread.sleep(10);
                        }
                        isChanging = true;
                        rotateTransition.setFromAngle(toggleTextFieldsButtonImage.getRotate());
                        rotateTransition.setToAngle(!isExpanded ? 180 : 0);
                        rotateTransition.playFromStart();
                        if (!isExpanded) textFieldVisibility(true);
                        while (!stopChanging && ((isExpanded && window.getHeight() != minHeight) || (!isExpanded && window.getHeight() != maxHeight))) {
                            window.setHeight(window.getHeight() + (isExpanded ? -1 : 1));
                            movePane.setLayoutY(movePane.getLayoutY() + (isExpanded ? -1 : 1));
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e1) {
                                GenericMethods.printStackTrace(log, e1, this.getClass());
                            }
                        }
                        isChanging = false;
                        if (stopChanging) stopChanging = false;
                        else if (isExpanded) textFieldVisibility(false);
                        isExpanded = !isExpanded;
                        return null;
                    }
                }).start();
            } else {
                window.setHeight(isExpanded ? minHeight : maxHeight);
                movePane.setLayoutY(movePane.getLayoutY() + (isExpanded ? -difference : difference));
                textFieldVisibility(!isExpanded);
                isExpanded = !isExpanded;
            }
        });

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int oldValue = -2;
                while (showEpisodeSelectBox.isStageOpen()) {
                    if (seasonComboBox.getValue() != null && !seasonComboBox.getValue().toString().isEmpty() && seasonComboBox.getValue() != oldValue) {
                        oldValue = seasonComboBox.getValue();
                        episodesArrayList.clear();
                        if (ClassHandler.showInfoController().getShowsFile().get(show.getShow()).containsSeason(seasonComboBox.getValue())) {
                            episodesArrayList.addAll(ClassHandler.showInfoController().getEpisodesList(show.getShow(), seasonComboBox.getValue()).stream().collect(Collectors.toList()));
                            Collections.sort(episodesArrayList);
                        }
                        Platform.runLater(() -> {
                            episodesList.clear();
                            episodesList.addAll(episodesArrayList);
                        });
                        updateFields();
                    }
                    Thread.sleep(100);
                }
                return null;
            }
        };

        Platform.runLater(() -> new Thread(task).start());
    }

    public int[] getSeasonEpisode() {
        return selectedSeason.getValue() != -2 && selectedEpisode.getValue() != -2 ? new int[]{selectedSeason.getValue(), selectedEpisode.getValue()} : new int[0];
    }

    private void updateFields() {
        Platform.runLater(() -> {
            if (!selectedSeason.getValue().equals(seasonComboBox.getValue())) {
                if (seasonComboBox.getItems().contains(selectedSeason.getValue()))
                    seasonComboBox.getSelectionModel().select(selectedSeason.getValue());
                else seasonComboBox.setValue(selectedSeason.getValue());
            }
            if (!selectedSeason.getValue().equals(Integer.valueOf(seasonTextField.getText())))
                seasonTextField.setText(selectedSeason.getValue().toString());
            if (episodeComboBox.getItems().contains(selectedEpisode.getValue()))
                episodeComboBox.getSelectionModel().select(selectedEpisode.getValue());
            else episodeComboBox.setValue(selectedEpisode.getValue());
            episodeTextField.setText(selectedEpisode.getValue().toString());
        });
    }

    private void textFieldVisibility(final boolean isVisible) {
        seasonTextField.setVisible(isVisible);
        episodeTextField.setVisible(isVisible);
    }

    public Pane getMainPane() {
        return mainPane;
    }
}
