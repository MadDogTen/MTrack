package com.maddogten.mtrack;

import com.maddogten.mtrack.FXMLControllers.Settings;
import com.maddogten.mtrack.gui.*;
import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.information.show.DisplayShow;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.*;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
      Controller is the controller for the main stage started in Main. Handles quite a bit, Most likely more then it should.
 */

@SuppressWarnings("WeakerAccess")
public class Controller implements Initializable {
    private static final Logger log = Logger.getLogger(Controller.class.getName());
    private static final SettingsWindow settingsWindow = new SettingsWindow();
    private final static ObservableList<DisplayShow> tableViewFields = FXCollections.observableArrayList();
    // This is the list that is currently showing in the tableView. 0 = Inactive, 1 = Active.
    private static int currentList = -1;
    // show0Remaining - If this true, It will display shows that have 0 episodes remaining, and if false, hides them. Only works with the active list.
    // wereShowsChanged - This is set the true if you set a show active while in the inactive list. If this is true when you switch back to the active this, it will start a recheck. This is because the show may be highly outdated as inactive shows aren't updated.
    // isShowCurrentlyPlaying - While a show is currently playing, this is true, otherwise it is false. This is used in mainRun to make rechecking take 10x longer to happen when a show is playing.
    private static boolean show0Remaining, wereShowsChanged, isShowCurrentlyPlaying; // TODO Move show0Remaining into settings?
    private final Map<String, Integer> changedShows = new HashMap<>();
    private final ChangesBox changesBox = new ChangesBox();
    private final ShowPlayingBox showPlayingBox = new ShowPlayingBox();
    @SuppressWarnings("unused")
    @FXML
    private Pane pane;
    @SuppressWarnings("unused")
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab showsTab;
    @FXML
    private Tab settingsTab;
    @SuppressWarnings("unused")
    @FXML
    private Button exit;
    @FXML
    private Button minimize;
    @SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
    @FXML
    private TableView<DisplayShow> tableView = new TableView<>();
    @FXML
    private TableColumn<DisplayShow, String> shows;
    @FXML
    private TableColumn<DisplayShow, Integer> remaining;
    @FXML
    private TableColumn<DisplayShow, Integer> season;
    @FXML
    private TableColumn<DisplayShow, Integer> episode;
    @FXML
    private TextField textField;
    @FXML
    private MenuButton menuButton;
    @FXML
    private MenuItem changeTableView;
    @FXML
    private MenuItem viewChanges;
    @FXML
    private CheckBox show0RemainingCheckBox;
    @FXML
    private ProgressIndicator isCurrentlyRechecking;
    @FXML
    private Button changesAlert;
    @FXML
    private Pane pingingDirectoryPane;
    @FXML
    private ImageView pingingDirectory;
    @FXML
    private Button clearTextField;
    @FXML
    private Tooltip show0RemainingCheckBoxTooltip;
    @FXML
    private Tooltip isCurrentlyRecheckingTooltip;
    @FXML
    private Label userName;
    @FXML
    private ComboBox<String> userNameComboBox;
    @FXML
    private Tooltip comboBoxTooltip;

    // This will set the ObservableList using the showList provided. For the active list, if show0Remaining is false, then it skips adding those, otherwise all shows are added. For the inactive list, all shows are added.
    private static ObservableList<DisplayShow> MakeTableViewFields(ArrayList<String> showList) {
        ObservableList<DisplayShow> list = FXCollections.observableArrayList();
        if (!showList.isEmpty()) {
            if (currentList == 1 && !show0Remaining) {
                showList.forEach(aShow -> {
                    int remaining = ClassHandler.userInfoController().getRemainingNumberOfEpisodes(aShow);
                    if (remaining != 0)
                        list.add(new DisplayShow(aShow, remaining, ClassHandler.userInfoController().getCurrentSeason(aShow), ClassHandler.userInfoController().getCurrentEpisode(aShow)));
                });
            } else
                list.addAll(showList.stream().map(aShow -> new DisplayShow(aShow, ClassHandler.userInfoController().getRemainingNumberOfEpisodes(aShow), ClassHandler.userInfoController().getCurrentSeason(aShow), ClassHandler.userInfoController().getCurrentEpisode(aShow))).collect(Collectors.toList()));
        }
        return list;
    }

    public static void setTableViewFields() {
        setTableViewFields(currentList);
    }

    // This sets the current tableView to whichever you want.
    public static void setTableViewFields(int type) {
        switch (type) {
            case 0:
                if (currentList != 0) currentList = 0;
                tableViewFields.clear();
                tableViewFields.addAll(MakeTableViewFields(Variables.showActiveShows ? ClassHandler.userInfoController().getUsersShows() : ClassHandler.userInfoController().getInactiveShows()));
                break;
            case 1:
                if (currentList != 1) currentList = 1;
                tableViewFields.clear();
                tableViewFields.addAll(MakeTableViewFields(ClassHandler.userInfoController().getActiveShows()));
                break;
        }
    }

    private static DisplayShow getDisplayShowFromShow(String aShow) {
        String showReplaced = aShow.replaceAll(Variables.fileNameReplace, "");
        for (DisplayShow show : tableViewFields) {
            if (show.getShow().replaceAll(Variables.fileNameReplace, "").matches(showReplaced)) {
                return show;
            }
        }
        return null;
    }

    // Public method to update a particular show with new information. If the show happens to have been remove, then showExists will be false, which isShowActive will be false, which makes remove true, which meaning it won't add the show back to the list.
    public static void updateShowField(String aShow, boolean showExists) {
        DisplayShow currentShow = getDisplayShowFromShow(aShow);
        final boolean isShowActive = showExists && ClassHandler.showInfoController().getShowsList().contains(aShow) && ClassHandler.userInfoController().isShowActive(aShow);
        if ((currentList != 1 || isShowActive) && (currentList != 0 || !isShowActive)) {
            int remaining = ClassHandler.userInfoController().getRemainingNumberOfEpisodes(aShow);
            int season = ClassHandler.userInfoController().getCurrentSeason(aShow);
            int episode = ClassHandler.userInfoController().getCurrentEpisode(aShow);
            if (currentShow != null) {
                if (remaining != 0 || show0Remaining || currentList == 0) {
                    if (currentShow.getSeason() != season) currentShow.setSeason(season);
                    if (currentShow.getEpisode() != episode) currentShow.setEpisode(episode);
                    if (currentShow.getRemaining() != remaining) currentShow.setRemaining(remaining);
                } else tableViewFields.remove(currentShow);
            } else if (show0Remaining || currentList == 0 || remaining != 0)
                tableViewFields.add(new DisplayShow(aShow, remaining, season, episode));
        } else if (currentShow != null) tableViewFields.remove(currentShow);
    }

    // Used to remove the show from the current list. If you are on the active list and set a show inactive, then it will remove it; and the same if you make a show active on the inactive list.
    private static void removeShowField(int index) {
        tableViewFields.remove(index);
    }

    public static boolean getIsShowCurrentlyPlaying() {
        return isShowCurrentlyPlaying;
    }

    public static double getShowColumnWidth() {
        return ClassHandler.controller().shows.getWidth();
    }

    public static double getRemainingColumnWidth() {
        return ClassHandler.controller().remaining.getWidth();
    }

    public static double getSeasonColumnWidth() {
        return ClassHandler.controller().season.getWidth();
    }

    public static double getEpisodeColumnWidth() {
        return ClassHandler.controller().episode.getWidth();
    }

    public static boolean getShowColumnVisibility() {
        return ClassHandler.controller().shows.isVisible();
    }

    public static boolean getRemainingColumnVisibility() {
        return ClassHandler.controller().remaining.isVisible();
    }

    public static boolean getSeasonColumnVisibility() {
        return ClassHandler.controller().season.isVisible();
    }

    public static boolean getEpisodeColumnVisibility() {
        return ClassHandler.controller().episode.isVisible();
    }

    public static void closeChangeBoxStage() {
        if (ClassHandler.controller() != null) ClassHandler.controller().changesBox.closeStage();
    }

    public static void closeShowPlayingBoxStage() {
        if (ClassHandler.controller() != null) ClassHandler.controller().showPlayingBox.closeStage();
    }

    @SuppressWarnings("SameParameterValue")
    public static void openSettingsWindow(int tab) {
        settingsWindow.closeSettings();
        try {
            settingsWindow.settings(tab);
        } catch (Exception e) {
            GenericMethods.printStackTrace(log, e, Controller.class);
        }
    }

    public static SettingsWindow getSettingsWindow() {
        return settingsWindow;
    }

    public static void setShowUsernameVisibility(boolean isVisible) {
        ClassHandler.controller().userName.setVisible(isVisible);
        ClassHandler.controller().userNameComboBox.setVisible(isVisible);
        ClassHandler.controller().userNameComboBox.setDisable(!isVisible);
    }

    public Map<String, Integer> getChangedShows() {
        return this.changedShows;
    }

    public void setChangedShows(Map<String, Integer> newShows) {
        newShows.forEach(this.changedShows::put);
    }

    public void addChangedShow(String aShow, int remaining) {
        if (!this.changedShows.containsKey(aShow)) {
            this.changedShows.put(aShow, remaining);
            tableView.refresh();
        }
    }

    public void resetChangedShows() {
        this.changedShows.clear();
    }

    // This first Filters the observableList if you have anything in the searchList, Then enables or disables the show0RemainingCheckbox depending on which list it is currently on.
    private void setTableView() {
        FilteredList<DisplayShow> newFilteredData = new FilteredList<>(tableViewFields, p -> true);
        newFilteredData.setPredicate(show -> textField.getText() == null || textField.getText().isEmpty() || show.getShow().toLowerCase().contains(textField.getText().toLowerCase()));
        textField.textProperty().addListener((observable, oldValue, newValue) -> newFilteredData.setPredicate(show -> newValue == null || newValue.isEmpty() || show.getShow().toLowerCase().contains(newValue.toLowerCase())));
        SortedList<DisplayShow> newSortedData = new SortedList<>(newFilteredData);
        newSortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(newSortedData);
        show0RemainingCheckBox.setVisible(currentList == 1);
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        log.info("MainController Running...");
        ClassHandler.setController(this);
        this.setChangedShows(ClassHandler.userInfoController().getUserSettings().getChangedShowsStatus());
        pane.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT);
        tabPane.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT);
        tableView.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT - 69);
        showsTab.textProperty().bind(Strings.Shows);
        settingsTab.textProperty().bind(Strings.Settings);
        shows.setCellValueFactory(new PropertyValueFactory<>("show"));
        shows.setSortType(TableColumn.SortType.ASCENDING);
        shows.textProperty().bind(Strings.Shows);
        shows.setPrefWidth(ClassHandler.programSettingsController().getSettingsFile().getShowColumnWidth());
        shows.setVisible(ClassHandler.programSettingsController().getSettingsFile().isShowColumnVisibility());
        remaining.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        remaining.textProperty().bind(Strings.Left);
        remaining.setPrefWidth(ClassHandler.programSettingsController().getSettingsFile().getRemainingColumnWidth());
        remaining.setVisible(ClassHandler.programSettingsController().getSettingsFile().isRemainingColumnVisibility());
        season.setCellValueFactory(new PropertyValueFactory<>("season"));
        season.textProperty().bind(Strings.Season);
        season.setPrefWidth(ClassHandler.programSettingsController().getSettingsFile().getSeasonColumnWidth());
        season.setVisible(ClassHandler.programSettingsController().getSettingsFile().isSeasonColumnVisibility());
        episode.setCellValueFactory(new PropertyValueFactory<>("episode"));
        episode.textProperty().bind(Strings.Episode);
        episode.setPrefWidth(ClassHandler.programSettingsController().getSettingsFile().getEpisodeColumnWidth());
        episode.setVisible(ClassHandler.programSettingsController().getSettingsFile().isEpisodeColumnVisibility());
        show0Remaining = ClassHandler.programSettingsController().getSettingsFile().isShow0Remaining();
        setTableViewFields(1);
        setTableView();
        tableView.getItems();
        tableView.getSortOrder().add(shows);
        tableView.setRowFactory(
                param -> {
                    final TableRow<DisplayShow> row = new TableRow<DisplayShow>() {
                        @Override
                        protected void updateItem(DisplayShow item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null) {
                                if (currentList == 0 && Variables.showActiveShows && ClassHandler.userInfoController().isShowActive(item.getShow())) {
                                    setStyle("-fx-background-color: " + Variables.ShowColorStatus.ACTIVE.getColor());
                                    return;
                                } else if (currentList == 1 && Variables.specialEffects && changedShows.containsKey(item.getShow()) && !isSelected()) {
                                    setStyle("-fx-background-color: " + Variables.ShowColorStatus.findColorFromRemaining(changedShows.get(item.getShow()), item.getRemaining()).getColor());
                                    return;
                                }
                            }
                            setStyle(Strings.EmptyString);
                        }
                    };
                    final ContextMenu rowMenuActive = new ContextMenu();
                    final ContextMenu rowMenuInactive = new ContextMenu();

                    row.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if (row.getItem() != null) {
                            if (!row.isSelected() && currentList == 0 && Variables.showActiveShows && ClassHandler.userInfoController().isShowActive(row.getItem().getShow())) {
                                row.setStyle("-fx-background-color: " + Variables.ShowColorStatus.ACTIVE.getColor());
                                return;
                            } else if (!row.isSelected() && currentList == 1 && Variables.specialEffects && this.changedShows.containsKey(row.getItem().getShow())) {
                                row.setStyle("-fx-background-color: " + Variables.ShowColorStatus.findColorFromRemaining(this.changedShows.get(row.getItem().getShow()), row.getItem().getRemaining()).getColor());
                                return;
                            }
                        }
                        row.setStyle(Strings.EmptyString);
                    });

                    MenuItem setSeasonEpisode = new MenuItem();
                    setSeasonEpisode.textProperty().bind(Strings.SetSeasonEpisode);
                    setSeasonEpisode.setOnAction(e -> {
                        log.info("\"Set Season + Episode\" is now running...");
                        String show = row.getItem().getShow();
                        int[] seasonEpisode = new ListSelectBox().pickSeasonEpisode(show, ClassHandler.showInfoController(), (Stage) pane.getScene().getWindow());
                        if (seasonEpisode[0] != -1 && seasonEpisode[1] != -1) {
                            log.info("Season & Episode were valid.");
                            ClassHandler.userInfoController().setSeasonEpisode(show, seasonEpisode[0], seasonEpisode[1]);
                            updateShowField(row.getItem().getShow(), true);
                            tableView.getSelectionModel().clearSelection();
                        } else log.info("Season & Episode weren't valid.");
                        log.info("\"Set Season + Episode\" is finished running.");
                    });
                    MenuItem playSeasonEpisode = new MenuItem();
                    playSeasonEpisode.textProperty().bind(Strings.PlaySeasonEpisode);
                    playSeasonEpisode.setOnAction(e -> {
                        log.info("\"Play Season + Episode\" is now running...");
                        String show = row.getItem().getShow();
                        int[] seasonEpisode = new ListSelectBox().pickSeasonEpisode(show, ClassHandler.showInfoController(), (Stage) pane.getScene().getWindow());
                        if (seasonEpisode[0] != -1 && seasonEpisode[1] != -1) {
                            log.info("Season & Episode were valid.");
                            ClassHandler.userInfoController().playAnyEpisode(show, seasonEpisode[0], seasonEpisode[1]);
                        } else log.info("Season & Episode weren't valid.");
                        log.info("\"Play Season + Episode\" is finished running.");
                    });
                    MenuItem toggleActive = new MenuItem();
                    toggleActive.setOnAction(e -> {
                        if (currentList == 0) {
                            if (Variables.showActiveShows && ClassHandler.userInfoController().isShowActive(row.getItem().getShow())) {
                                ClassHandler.userInfoController().setActiveStatus(row.getItem().getShow(), false);
                                row.setStyle("");
                            } else {
                                ClassHandler.userInfoController().setActiveStatus(row.getItem().getShow(), true);
                                if (!wereShowsChanged) wereShowsChanged = true;
                                if (Variables.showActiveShows)
                                    row.setStyle("-fx-background-color: " + Variables.ShowColorStatus.ACTIVE.getColor());
                                else
                                    removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            }
                            tableView.getSelectionModel().clearSelection();
                        } else if (currentList == 1) {
                            ClassHandler.userInfoController().setActiveStatus(row.getItem().getShow(), false);
                            removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            tableView.getSelectionModel().clearSelection();
                        }
                    });
                    MenuItem setHidden = new MenuItem();
                    setHidden.textProperty().bind(Strings.HideShow);
                    setHidden.setOnAction(e -> {
                        ClassHandler.userInfoController().setHiddenStatus(row.getItem().getShow(), true);
                        removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                        tableView.getSelectionModel().clearSelection();
                    });
                    MenuItem resetShow = new MenuItem();
                    resetShow.textProperty().bind(Strings.ResetTo);
                    resetShow.setOnAction(e -> {
                        log.info("Reset to running...");
                        String[] choices = {Strings.Beginning.getValue(), Strings.End.getValue()};
                        String answer = new SelectBox().select(Strings.WhatShould.getValue() + row.getItem().getShow() + Strings.BeResetTo.getValue(), choices, (Stage) pane.getScene().getWindow());
                        if (answer.matches(Strings.Beginning.getValue())) {
                            ClassHandler.userInfoController().setToBeginning(row.getItem().getShow());
                            updateShowField(row.getItem().getShow(), true);
                            tableView.getSelectionModel().clearSelection();
                            log.info(Strings.ShowIsResetToThe.getValue() + ' ' + Strings.Beginning.getValue().toLowerCase() + '.');
                        } else if (answer.matches(Strings.End.getValue())) {
                            ClassHandler.userInfoController().setToEnd(row.getItem().getShow());
                            updateShowField(row.getItem().getShow(), true);
                            tableView.getSelectionModel().clearSelection();
                            log.info(Strings.ShowIsResetToThe.getValue() + ' ' + Strings.End.getValue().toLowerCase() + '.');
                        }
                        log.info("Reset to finished running.");
                    });
                    MenuItem openDirectory = new MenuItem();
                    openDirectory.textProperty().bind(Strings.OpenFileLocation);
                    openDirectory.setOnAction(e -> {
                        log.info("Started to open show directory...");
                        ArrayList<Directory> directories = ClassHandler.directoryController().findDirectories(false, true, true);
                        ArrayList<Directory> folders = new ArrayList<>();
                        FileManager fileManager = new FileManager();
                        directories.forEach(aDirectory -> {
                            File fileName = new File(aDirectory.getDirectory() + Strings.FileSeparator + row.getItem().getShow());
                            if (fileName.exists())
                                folders.add(new Directory(fileName, fileName.toString(), -2, -2, null));
                        });
                        if (folders.size() == 1) fileManager.open(folders.get(0).getDirectory());
                        else new ListSelectBox().openDirectory(folders, (Stage) tabPane.getScene().getWindow());
                        log.info("Finished opening show directory...");
                    });
                    MenuItem getRemaining = new MenuItem();
                    getRemaining.textProperty().bind(Strings.GetRemaining);
                    getRemaining.setOnAction(e -> log.info("There are " + ClassHandler.userInfoController().getRemainingNumberOfEpisodes(row.getItem().getShow()) + " episode(s) remaining."));
                    MenuItem playPreviousEpisode = new MenuItem();
                    playPreviousEpisode.textProperty().bind(Strings.PlayPreviousEpisode);
                    playPreviousEpisode.setOnAction(e -> {
                        log.info("Attempting to play previous episode...");
                        int[] seasonEpisode = ClassHandler.userInfoController().getPreviousEpisodeIfExists(row.getItem().getShow());
                        if (seasonEpisode[0] == -2 || seasonEpisode[0] == -3)
                            new MessageBox().message(new StringProperty[]{Strings.NoDirectlyPrecedingEpisodesFound}, (Stage) tabPane.getScene().getWindow());
                        else
                            ClassHandler.userInfoController().playAnyEpisode(row.getItem().getShow(), seasonEpisode[0], seasonEpisode[1]);
                        log.info("Finished attempting to play previous episode.");
                    });
                    MenuItem printCurrentSeasonEpisode = new MenuItem();
                    printCurrentSeasonEpisode.textProperty().bind(Strings.PrintCurrentSeasonEpisode);
                    printCurrentSeasonEpisode.setOnAction(e -> log.info(row.getItem().getShow() + " - Season: " + ClassHandler.userInfoController().getCurrentSeason(row.getItem().getShow()) + " - Episode: " + ClassHandler.userInfoController().getCurrentEpisode(row.getItem().getShow())));
                    MenuItem printShowInformation = new MenuItem();
                    printShowInformation.textProperty().bind(Strings.PrintShowInformation);
                    printShowInformation.setOnAction(e -> ClassHandler.developerStuff().printShowInformation(row.getItem().getShow()));
                    MenuItem getMissingEpisodes = new MenuItem();
                    getMissingEpisodes.textProperty().bind(Strings.GetMissingEpisodes);
                    getMissingEpisodes.setOnAction(e -> {
                        Map<Integer, Set<Integer>> missingInfo = ClassHandler.showInfoController().getMissingEpisodes(row.getItem().getShow());
                        if (missingInfo.isEmpty())
                            new MessageBox().message(new StringProperty[]{new SimpleStringProperty("Show is missing database info / Has no missing episodes.")}, (Stage) tabPane.getScene().getWindow());
                        else {
                            StringProperty[] info = new StringProperty[missingInfo.keySet().size()];
                            int i = 0;
                            missingInfo.forEach((integer, integers) -> {
                                info[i] = new SimpleStringProperty("Season: " + integer + " - Episode: " + integers);
                                log.info("Missing info for " + row.getItem().getShow() + " - Season: " + integer + " - Episodes: " + integers);
                            });
                            new MessageBox().message(info, (Stage) tabPane.getScene().getWindow());
                        }
                    });
                    row.setOnMouseEntered(e -> {
                        if (row.getItem() != null && (row.getTooltip() == null || !row.getTooltip().getText().contains(row.getItem().getShow()))) {
                            Tooltip rowToolTip = new Tooltip(row.getItem().getShow() + " - " + Strings.Season.getValue() + " " + row.getItem().getSeason() + " - " + Strings.Episode.getValue() + " " + row.getItem().getEpisode());
                            rowToolTip.getStyleClass().add("tooltip");
                            row.setTooltip(rowToolTip);
                        }
                    });
                    row.setOnMouseClicked(e -> {
                        if (e.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
                            if (currentList == 1) {
                                rowMenuActive.getItems().clear();
                                toggleActive.textProperty().bind(Strings.SetInactive);
                                if (Variables.devMode)
                                    rowMenuActive.getItems().addAll(setSeasonEpisode, playSeasonEpisode, playPreviousEpisode, resetShow, toggleActive, getRemaining, openDirectory, printCurrentSeasonEpisode, printShowInformation, getMissingEpisodes);
                                else
                                    rowMenuActive.getItems().addAll(setSeasonEpisode, playSeasonEpisode, playPreviousEpisode, resetShow, toggleActive, openDirectory);
                                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenuActive).otherwise((ContextMenu) null));
                            } else if (currentList == 0) {
                                rowMenuInactive.getItems().clear();
                                if (Variables.showActiveShows && ClassHandler.userInfoController().isShowActive(row.getItem().getShow())) {
                                    toggleActive.textProperty().bind(Strings.SetInactive);
                                    rowMenuInactive.getItems().add(toggleActive);
                                } else {
                                    toggleActive.textProperty().bind(Strings.SetActive);
                                    if (Variables.devMode)
                                        rowMenuInactive.getItems().addAll(toggleActive, setHidden, getRemaining, openDirectory, printShowInformation);
                                    else rowMenuInactive.getItems().addAll(toggleActive, setHidden, openDirectory);
                                }
                                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenuInactive).otherwise((ContextMenu) null));
                            }
                        }
                        if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && !isShowCurrentlyPlaying && (!row.isEmpty()) && currentList == 1) {
                            isShowCurrentlyPlaying = true;
                            try {
                                showPlayingBox.showConfirm(row.getItem(), ClassHandler.controller(), ClassHandler.userInfoController(), (Stage) pane.getScene().getWindow());
                            } catch (IOException e1) {
                                GenericMethods.printStackTrace(log, e1, Controller.class);
                            }
                            isShowCurrentlyPlaying = false;
                        }
                    });
                    return row;
                }
        );

        userName.setVisible(ClassHandler.userInfoController().getUserSettings().isShowUsername());
        userName.textProperty().bind(Strings.UserName);
        userNameComboBox.setVisible(ClassHandler.userInfoController().getUserSettings().isShowUsername());
        userNameComboBox.setDisable(!ClassHandler.userInfoController().getUserSettings().isShowUsername());
        comboBoxTooltip.textProperty().bind(Strings.UserName);

        userNameComboBox.addEventFilter(MouseEvent.MOUSE_RELEASED, mouse -> {
            if (!mouse.isStillSincePress()) mouse.consume();
            userNameComboBox.setCursor(Cursor.DEFAULT);
        });
        userNameComboBox.setOnShown(e -> {
            if (getSettingsWindow().isSettingsOpen()) Settings.getSettings().toggleUsernameUsability(true);
            ArrayList<String> users = ClassHandler.userInfoController().getAllUsers();
            users.remove(Strings.UserName.getValue());
            userNameComboBox.getItems().addAll(users);
        });
        userNameComboBox.setOnHidden(event -> {
            if (getSettingsWindow().isSettingsOpen()) Settings.getSettings().toggleUsernameUsability(false);
            userNameComboBox.getItems().clear();
        });
        userNameComboBox.setOnAction(e -> {
            if (userNameComboBox.getValue() != null && !userNameComboBox.getValue().matches(Strings.UserName.getValue())) {
                GenericMethods.saveSettings();
                Strings.UserName.setValue(userNameComboBox.getValue());
                ChangeReporter.resetChanges();
                ClassHandler.mainRun().loadUser(new UpdateManager(), false);
                Controller.setTableViewFields();
            }
        });
        userName.setTextFill(Paint.valueOf(Color.DIMGRAY.toString()));

        // ~~~~ Search TextField ~~~~ \\
        // The ContextMenu *really* isn't needed, But I wanted to make the clearTextField button properly disappear and appear when needed. Using a Task would have been much easier...
        MenuItem textFieldCut = new MenuItem();
        textFieldCut.textProperty().bind(Strings.Cut);
        textFieldCut.setOnAction(e -> {
            //noinspection unchecked
            Toolkit.getToolkit().getSystemClipboard().putContent(new Pair<>(DataFormat.PLAIN_TEXT, textField.getSelectedText()));
            int caretPosition = textField.getSelection().getStart();
            String cutText = textField.getText().substring(0, textField.getSelection().getStart()) + textField.getText().substring(textField.getSelection().getEnd(), textField.getText().length());
            textField.setText(cutText);
            textField.positionCaret(caretPosition);
            if (clearTextField.isVisible() && textField.getText().isEmpty()) clearTextField.setVisible(false);
            else if (!clearTextField.isVisible() && !textField.getText().isEmpty()) clearTextField.setVisible(true);
        });
        MenuItem textFieldCopy = new MenuItem();
        textFieldCopy.textProperty().bind(Strings.Copy);
        textFieldCopy.setOnAction(e -> {
            //noinspection unchecked
            Toolkit.getToolkit().getSystemClipboard().putContent(new Pair<>(DataFormat.PLAIN_TEXT, textField.getSelectedText()));
        });
        MenuItem textFieldPaste = new MenuItem();
        textFieldPaste.textProperty().bind(Strings.Paste);
        textFieldPaste.setOnAction(e -> {
            String pasteTextAdded = textField.getText().substring(0, textField.getCaretPosition()) + Toolkit.getToolkit().getSystemClipboard().getContent(DataFormat.PLAIN_TEXT) + textField.getText().substring(textField.getCaretPosition(), textField.getText().length());
            textField.setText(pasteTextAdded);
            if (clearTextField.isVisible() && textField.getText().isEmpty()) clearTextField.setVisible(false);
            else if (!clearTextField.isVisible() && !textField.getText().isEmpty()) clearTextField.setVisible(true);
            textField.positionCaret(textField.getText().length());
        });
        ContextMenu textFieldContextMenu = new ContextMenu();
        textFieldContextMenu.getItems().addAll(textFieldCut, textFieldCopy, textFieldPaste);
        textField.setContextMenu(textFieldContextMenu);
        // End of ContextMenu

        clearTextField.setText(Strings.EmptyString);
        textField.setOnKeyTyped(e -> {
            if (!e.getCharacter().matches("\b") && !clearTextField.isVisible()) clearTextField.setVisible(true);
        });
        textField.setOnKeyReleased(e -> {
            if (textField.getText().isEmpty() && clearTextField.isVisible()) clearTextField.setVisible(false);
            else if (!textField.getText().isEmpty() && !clearTextField.isVisible()) clearTextField.setVisible(true);
        });
        clearTextField.setVisible(false);
        clearTextField.setOnAction(e -> {
            textField.clear();
            clearTextField.setVisible(false);
            textField.requestFocus();
        });

        // ~~~~ Buttons ~~~~ \\
        exit.setOnAction(e -> Main.stop(Main.stage, false, true));
        minimize.setOnAction(e -> Main.stage.setIconified(true));
        menuButton.textProperty().bind(Strings.Options);
        changeTableView.setOnAction(event -> {
            if (currentList == 1) {
                setTableViewFields(0);
                tableView.scrollTo(0);
                tableView.scrollToColumnIndex(0);
                log.info("TableViewFields set to inactive.");
            } else if (currentList == 0) {
                if (wereShowsChanged && !Variables.disableAutomaticRechecking) {
                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            ClassHandler.checkShowFiles().recheckShowFile(true);
                            return null;
                        }
                    };
                    new Thread(task).start();
                    wereShowsChanged = false;
                } else if (Variables.disableAutomaticRechecking) wereShowsChanged = false;
                setTableViewFields(1);
                tableView.scrollTo(0);
                tableView.scrollToColumnIndex(0);
                log.info("TableViewFields set to active.");
            }
            this.setTableView();
        });
        changeTableView.textProperty().bind(Strings.SwitchBetweenActiveInactiveList);
        viewChanges.textProperty().bind(Strings.OpenChangesWindow);
        viewChanges.setOnAction(e -> openChangeBox());
        changesAlert.setOnAction(e -> openChangeBox());
        if (Variables.specialEffects) changesAlert.setOpacity(0.0);
        else changesAlert.setOpacity(1.0);
        show0RemainingCheckBox.setSelected(show0Remaining);
        show0RemainingCheckBox.setOnAction(e -> {
            show0Remaining = show0RemainingCheckBox.isSelected();
            ClassHandler.programSettingsController().getSettingsFile().setShow0Remaining(show0Remaining);
            if (show0Remaining)
                log.info("Now showing shows with 0 episodes remaining.");
            else log.info("No longer showing shows with 0 episodes remaining.");
            setTableViewFields();
            setTableView();
        });
        show0RemainingCheckBoxTooltip.textProperty().bind(Strings.ShowHiddenShowsWith0EpisodeLeft);
        Tooltip pingingDirectoryTooltip = new Tooltip();
        pingingDirectoryTooltip.textProperty().bind(Strings.PingingDirectories);
        pingingDirectoryTooltip.getStyleClass().add("tooltip");
        Tooltip.install(pingingDirectoryPane, pingingDirectoryTooltip);

        // || ~~~~ Settings Tab ~~~~ || \\
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        settingsTab.setOnSelectionChanged(e -> {
            selectionModel.select(showsTab);
            if (!settingsWindow.isSettingsOpen()) {
                settingsTab.setDisable(true);
                try {
                    settingsWindow.settings(-2);
                } catch (Exception e1) {
                    GenericMethods.printStackTrace(log, e1, this.getClass());
                }
            }
        });
        new MoveStage().moveStage(tabPane, null);
        new MoveStage().moveStage(userNameComboBox, null);
        // Shows an indicator when its rechecking the shows.
        isCurrentlyRecheckingTooltip.textProperty().bind(Strings.CurrentlyRechecking);
        Task<Void> mainTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (Main.programRunning) {
                    if (ClassHandler.checkShowFiles().isRecheckingShowFile()) {
                        pingingDirectory.setVisible(true);
                        pingingDirectoryPane.setMouseTransparent(false);
                        while (ClassHandler.checkShowFiles().isCurrentlyCheckingDirectories()) {
                            if (pingingDirectory.getRotate() == 180) pingingDirectory.setRotate(0);
                            pingingDirectory.setRotate(pingingDirectory.getRotate() + 4);
                            Thread.sleep(80);
                        }
                        pingingDirectory.setRotate(0);
                        pingingDirectory.setVisible(false);
                        pingingDirectoryPane.setMouseTransparent(true);
                        isCurrentlyRechecking.setVisible(true);
                        isCurrentlyRechecking.setMouseTransparent(false);
                        while (ClassHandler.checkShowFiles().isRecheckingShowFile()) {
                            Platform.runLater(() -> isCurrentlyRechecking.setProgress(ClassHandler.checkShowFiles().getRecheckShowFilePercentage()));
                            Thread.sleep(80);
                        }
                        isCurrentlyRechecking.setProgress(0);
                        isCurrentlyRechecking.setMouseTransparent(true);
                        isCurrentlyRechecking.setVisible(false);
                    }
                    checkIfChangesListIsPopulated();
                    Thread.sleep(800);
                }
                return null;
            }
        };
        new Thread(mainTask).start();
        Task<Void> secondaryTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (Main.programRunning) {
                    if (settingsWindow.isSettingsOpen()) {
                        if (!settingsTab.isDisable()) settingsTab.setDisable(true);
                        while (settingsWindow.isSettingsOpen()) Thread.sleep(80);
                        settingsTab.setDisable(false);
                    } else if (settingsTab.isDisable()) settingsTab.setDisable(false);
                    Thread.sleep(800);
                }
                return null;
            }
        };
        new Thread(secondaryTask).start();
    }

    public void setTableSelection(int row) {
        if (row == -2) tableView.getSelectionModel().clearSelection();
        else tableView.getSelectionModel().select(row);
    }

    private void openChangeBox() {
        changesBox.openChanges((Stage) pane.getScene().getWindow());
    }

    private void checkIfChangesListIsPopulated() throws InterruptedException {
        if (ChangeReporter.getIsChanges() && !changesAlert.isVisible()) {
            changesAlert.setVisible(true);
            while (Variables.specialEffects && changesAlert.getOpacity() < 1.0) {
                final int opacity = (int) (changesAlert.getOpacity() * 100.0) + 10;
                changesAlert.setOpacity((double) (opacity / 100.0f));
                Thread.sleep(40);
            }
        } else if (!ChangeReporter.getIsChanges() && changesAlert.isVisible()) {
            while (Variables.specialEffects && changesAlert.getOpacity() > 0.0) {
                final int opacity = (int) (changesAlert.getOpacity() * 100.0) - 10;
                changesAlert.setOpacity((double) (opacity / 100.0f));
                Thread.sleep(40);
            }
            this.changesAlert.setVisible(false);
        }
    }
}
