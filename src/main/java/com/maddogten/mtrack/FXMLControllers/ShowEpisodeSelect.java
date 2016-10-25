package com.maddogten.mtrack.FXMLControllers;

import com.maddogten.mtrack.gui.ShowEpisodeSelectBox;
import com.maddogten.mtrack.information.show.DisplayShow;
import com.maddogten.mtrack.util.ClassHandler;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class ShowEpisodeSelect implements Initializable {
    private static final Logger log = Logger.getLogger(ShowEpisodeSelect.class.getName());

    private final ShowEpisodeSelectBox showEpisodeSelectBox;
    private final DisplayShow show;
    private final IntegerProperty selectedSeason = new SimpleIntegerProperty(), selectedEpisode = new SimpleIntegerProperty();
    private boolean changingComboBoxes = false;

    @FXML
    private ComboBox<Integer> seasonComboBox;
    @FXML
    private ComboBox<Integer> episodeComboBox;
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
    private CheckBox editSeasonEpisodeCheckbox;

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
        ArrayList<Integer> episodesArrayList = new ArrayList<>(ClassHandler.showInfoController().getEpisodesList(show.getShow(), this.selectedSeason.getValue()));
        ObservableList<Integer> episodesList = FXCollections.observableArrayList(episodesArrayList);
        episodeComboBox.setItems(episodesList);

        seasonComboBox.setConverter(new IntegerStringConverter());
        episodeComboBox.setConverter(new IntegerStringConverter());

        if (seasonComboBox.getItems().contains(selectedSeason.getValue()))
            seasonComboBox.getSelectionModel().select(selectedSeason.getValue());
        else seasonComboBox.setValue(selectedSeason.getValue());
        if (episodeComboBox.getItems().contains(selectedEpisode.getValue()))
            episodeComboBox.getSelectionModel().select(selectedEpisode.getValue());
        else episodeComboBox.setValue(selectedEpisode.getValue());

        seasonComboBox.setOnAction(e -> {
            log.info("Ran1");
            if (!changingComboBoxes) {
                selectedSeason.setValue(seasonComboBox.getValue());

                episodesArrayList.clear();
                if (ClassHandler.showInfoController().getShowsFile().get(show.getShow()).containsSeason(seasonComboBox.getValue())) {
                    episodesArrayList.addAll(ClassHandler.showInfoController().getEpisodesList(show.getShow(), seasonComboBox.getValue()).stream().collect(Collectors.toList()));
                    Collections.sort(episodesArrayList);
                }
                episodesList.clear();
                episodesList.addAll(episodesArrayList);
                selectedEpisode.setValue(selectedSeason.getValue() == show.getSeason() ? show.getEpisode() : ClassHandler.showInfoController().findLowestInt(new HashSet<>(episodeComboBox.getItems())));
                int itemIndex = episodeComboBox.getItems().indexOf(selectedEpisode.getValue());
                if (itemIndex != -1) {
                    episodeComboBox.getSelectionModel().select(itemIndex);
                } else episodeComboBox.setValue(selectedEpisode.getValue());
            }
        });

        episodeComboBox.setOnAction(e -> {
            if (episodeComboBox.getValue() != null) selectedEpisode.setValue(episodeComboBox.getValue());
        });

        DecimalFormat format = new DecimalFormat("#.0");
        seasonComboBox.getEditor().setTextFormatter(new TextFormatter<Integer>(c -> {
            if (c.getControlNewText().isEmpty()) return c;
            ParsePosition parsePosition = new ParsePosition(0);
            if (format.parse(c.getControlNewText(), parsePosition) == null || parsePosition.getIndex() < c.getControlNewText().length())
                return null;
            else return c;
        }));
        episodeComboBox.getEditor().setTextFormatter(new TextFormatter<Integer>(c -> {
            if (c.getControlNewText().isEmpty()) return c;
            ParsePosition parsePosition = new ParsePosition(0);
            if (format.parse(c.getControlNewText(), parsePosition) == null || parsePosition.getIndex() < c.getControlNewText().length())
                return null;
            else return c;
        }));

        resetBeginningButton.setOnAction(e -> {
            seasonComboBox.getSelectionModel().selectFirst();
            episodeComboBox.getSelectionModel().selectFirst();
            updateFields();
        });
        resetEndButton.setOnAction(e -> {
            /*selectedSeason.setValue(ClassHandler.showInfoController().findHighestInt(ClassHandler.showInfoController().getSeasonsList(show.getShow())));
            selectedEpisode.setValue(ClassHandler.showInfoController().findHighestInt(ClassHandler.showInfoController().getEpisodesList(show.getShow(), selectedSeason.getValue())) + 1);*/
            seasonComboBox.getSelectionModel().selectLast();
            episodeComboBox.getSelectionModel().selectLast();
            episodeComboBox.getSelectionModel().select((Integer) (episodeComboBox.getValue() + 1));
            log.info(String.valueOf(selectedEpisode.getValue()));
            updateFields();
        });

        submitButton.setOnAction(e -> showEpisodeSelectBox.closeStage());
        exitButton.setOnAction(e -> {
            selectedSeason.setValue(-2);
            selectedEpisode.setValue(-2);
            showEpisodeSelectBox.closeStage();
        });

        editSeasonEpisodeCheckbox.setOnAction(e -> {
            changingComboBoxes = true;
            int season = seasonComboBox.getSelectionModel().getSelectedIndex(), episode = episodeComboBox.getSelectionModel().getSelectedIndex();
            seasonComboBox.setEditable(editSeasonEpisodeCheckbox.isSelected());
            episodeComboBox.setEditable(editSeasonEpisodeCheckbox.isSelected());
            seasonComboBox.getSelectionModel().select(season);
            episodeComboBox.getSelectionModel().select(episode);
            changingComboBoxes = false;
        });

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int oldValue = -2;
                while (showEpisodeSelectBox.isStageOpen()) {
                    if (seasonComboBox.getValue() != null && !seasonComboBox.getValue().toString().isEmpty() && seasonComboBox.getValue() != oldValue) {
                        oldValue = seasonComboBox.getValue();
                        /*episodesArrayList.clear();
                        if (ClassHandler.showInfoController().getShowsFile().get(show.getShow()).containsSeason(seasonComboBox.getValue())) {
                            episodesArrayList.addAll(ClassHandler.showInfoController().getEpisodesList(show.getShow(), seasonComboBox.getValue()).stream().collect(Collectors.toList()));
                            Collections.sort(episodesArrayList);
                        }
                        Platform.runLater(() -> {
                            episodesList.clear();
                            episodesList.addAll(episodesArrayList);
                        });*/
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
            if (episodeComboBox.getItems().contains(selectedEpisode.getValue()))
                episodeComboBox.getSelectionModel().select(selectedEpisode.getValue());
            else episodeComboBox.setValue(selectedEpisode.getValue());
        });
    }

    public Pane getMainPane() {
        return mainPane;
    }
}
