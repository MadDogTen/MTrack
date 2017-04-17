package com.maddogten.mtrack;

import com.maddogten.mtrack.gui.*;
import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.information.show.DisplayShow;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.*;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
      Controller is the controller for the main stage started in Main. Handles quite a bit, Most likely more then it should.
 */

@SuppressWarnings("WeakerAccess")
public class Controller implements Initializable {
    private static final Logger log = Logger.getLogger(Controller.class.getName());
    private final static ObservableList<DisplayShow> tableViewFields = FXCollections.observableArrayList();
    // show0Remaining - If this true, It will display shows that have 0 episodes remaining, and if false, hides them. Only works with the active list.
    // wereShowsChanged - This is set the true if you set a show active while in the inactive list. If this is true when you switch back to the active this, it will start a recheck. This is because the show may be highly outdated as inactive shows aren't updated.
    // isShowCurrentlyPlaying - While a show is currently playing, this is true, otherwise it is false. This is used in mainRun to make rechecking take 10x longer to happen when a show is playing.
    private static boolean wereShowsChanged, menuExpanded, menuChanging, stopMenuChanging;
    private static DisplayShow showCurrentlyPlaying = null;
    private final Map<String, Integer> changedShows = new HashMap<>();
    private final ChangesBox changesBox = new ChangesBox();
    private final ShowPlayingBox showPlayingBox = new ShowPlayingBox();
    @SuppressWarnings("unused")
    @FXML
    private Pane pane;
    @FXML
    private Button homeButton;
    @FXML
    private Button settingsButton;
    @FXML
    private AnchorPane tableViewAnchorPane;
    @FXML
    private AnchorPane settingsAnchorPane;
    @FXML
    private Pane topBarRectangle;
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
    private Button changeTableViewButton;
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
    private Tooltip isCurrentlyRecheckingTooltip;
    @FXML
    private Label userName;
    @FXML
    private ComboBox<String> userNameComboBox;
    @FXML
    private Tooltip comboBoxTooltip;
    @FXML
    private CheckBox showActiveShowsCheckbox;
    @FXML
    private Tooltip showActiveShowsCheckboxTooltip;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab mainTab;
    @FXML
    private Tab usersTab;
    @FXML
    private Tab showTab;
    @FXML
    private Tab uiTab;
    @FXML
    private Tab otherTab;
    @FXML
    private Tab developerTab;
    @FXML
    private Tab dev1Tab;
    @FXML
    private Tab dev2Tab;
    @SuppressWarnings("unused")
    @FXML
    private Button forceRecheck;
    @FXML
    private Button openProgramFolder;
    @FXML
    private Button printAllShows;
    @FXML
    private Button deleteEverythingAndClose;
    @FXML
    private Button setDefaultUsername;
    @FXML
    private Button clearDefaultUsername;
    @FXML
    private Button deleteUser;
    @FXML
    private Button addUser;
    @FXML
    private Button about;
    @FXML
    private Button addDirectory;
    @FXML
    private Button removeDirectory;
    @FXML
    private Button printAllDirectories;
    @FXML
    private Button printEmptyShowFolders;
    @FXML
    private Button setAllActive;
    @FXML
    private Button setAllInactive;
    @FXML
    private Button printIgnoredShows;
    @FXML
    private Button clearFile;
    @FXML
    private Button printProgramSettingsFileVersion;
    @FXML
    private Button printHiddenShows;
    @FXML
    private Button unHideAll;
    @FXML
    private Button printAllUserInfo;
    @FXML
    private Button add1ToDirectoryVersion;
    @FXML
    private Button changeLanguage;
    @FXML
    private ToggleButton toggleDevMode;
    @FXML
    private Text updateText;
    @FXML
    private TextField updateTimeTextField;
    @FXML
    private Button setUpdateTime;
    @FXML
    private Text directoryTimeoutText;
    @FXML
    private TextField directoryTimeoutTextField;
    @FXML
    private Button setDirectoryTimeout;
    @FXML
    private Text notifyChangesText;
    @FXML
    private Text onlyChecksEveryText;
    @FXML
    private CheckBox inactiveShowsCheckBox;
    @FXML
    private CheckBox olderSeasonsCheckBox;
    @FXML
    private CheckBox unlockParentScene;
    @FXML
    private CheckBox disableAutomaticShowUpdating;
    @FXML
    private Button unHideShow;
    @FXML
    private Tooltip deleteUserTooltip;
    @FXML
    private Tooltip deleteEverythingAndCloseTooltip;
    @FXML
    private Button exportSettings;
    @FXML
    private Button importSettings;
    @FXML
    private Button nonForceRecheckShows;
    @FXML
    private CheckBox showUsername;
    @FXML
    private Button toggleIsChanges;
    @FXML
    private CheckBox automaticSaving;
    @FXML
    private Text savingText;
    @FXML
    private TextField updateSavingTextField;
    @FXML
    private Button setSavingTime;
    @FXML
    private CheckBox specialEffects;
    @FXML
    private Text currentUserText;
    @FXML
    private ComboBox<String> currentUserComboBox;
    @FXML
    private CheckBox enableLoggingCheckbox;
    @FXML
    private CheckBox useOnlineDatabaseCheckbox;
    @FXML
    private Text onlineWarningText;
    @FXML
    private Text directoryText;
    @FXML
    private Button menuButton;
    @FXML
    private ImageView menuButtonImage;
    @FXML
    private Pane mainPane;
    @FXML
    private RadioMenuItem show0RemainingRadioMenuItem;
    @FXML
    private Button changeVideoPlayerButton;

    // This will set the ObservableList using the showList provided. For the active list, if show0Remaining is false, then it skips adding those, otherwise all shows are added. For the inactive list, all shows are added.
    private static ObservableList<DisplayShow> MakeTableViewFields(final Set<Integer> showList) {
        ObservableList<DisplayShow> list = FXCollections.observableArrayList();
        if (!showList.isEmpty()) {
            if (currentList.isActive() && !Variables.show0Remaining) {
                showList.forEach(showID -> {
                    int remaining = ClassHandler.userInfoController().getRemainingNumberOfEpisodes(Variables.currentUser, showID);
                    if (remaining != 0)
                        list.add(new DisplayShow(ClassHandler.showInfoController().getShowNameFromShowID(showID), remaining, ClassHandler.userInfoController().getCurrentUserSeason(Variables.currentUser, showID), ClassHandler.userInfoController().getCurrentUserEpisode(Variables.currentUser, showID), showID));
                });
            } else
                list.addAll(showList.stream().map(showID -> new DisplayShow(ClassHandler.showInfoController().getShowNameFromShowID(showID), ClassHandler.userInfoController().getRemainingNumberOfEpisodes(Variables.currentUser, showID), ClassHandler.userInfoController().getCurrentUserSeason(Variables.currentUser, showID), ClassHandler.userInfoController().getCurrentUserEpisode(Variables.currentUser, showID), showID)).collect(Collectors.toList()));
        }
        return list;
    }

    public static void setTableViewFields() {
        setTableViewFields(currentList.getStatus());
    }

    // This sets the current tableView to whichever you want.
    public static void setTableViewFields(final currentList type) {
        switch (type) {
            case INACTIVE:
                if (currentList.getStatus() != currentList.INACTIVE) currentList.setStatus(currentList.INACTIVE);
                tableViewFields.clear();
                tableViewFields.addAll(MakeTableViewFields(Variables.showActiveShows ? ClassHandler.userInfoController().getUsersShows(Variables.currentUser) : ClassHandler.userInfoController().getShowsWithActiveStatus(Variables.currentUser, false)));
                break;
            case ACTIVE:
                if (currentList.getStatus() != currentList.ACTIVE) currentList.setStatus(currentList.ACTIVE);
                tableViewFields.clear();
                tableViewFields.addAll(MakeTableViewFields(ClassHandler.userInfoController().getShowsWithActiveStatus(Variables.currentUser, true)));
                break;
        }
    }

    private static DisplayShow getDisplayShowFromShowID(final int showID) {
        for (DisplayShow show : tableViewFields) {
            if (show.getShowID() == showID) {
                return show;
            }
        }
        return null;
    }

    // Public method to update a particular show with new information. If the show happens to have been removed, then showExists will be false, which isShowActive will be false, which makes remove true, which meaning it won't add the show back to the list.
    public static void updateShowField(final int showID, final boolean showExists) {
        DisplayShow currentShow = getDisplayShowFromShowID(showID);
        final boolean isShowActive = showExists && ClassHandler.showInfoController().getShows().contains(showID) && ClassHandler.userInfoController().isShowActive(Variables.currentUser, showID);
        if ((!currentList.isActive() || isShowActive) && (!currentList.isInactive() || (Variables.showActiveShows || !isShowActive))) {
            int remaining = ClassHandler.userInfoController().getRemainingNumberOfEpisodes(Variables.currentUser, showID),
                    season = ClassHandler.userInfoController().getCurrentUserSeason(Variables.currentUser, showID),
                    episode = ClassHandler.userInfoController().getCurrentUserEpisode(Variables.currentUser, showID);
            if (currentShow != null) {
                if (remaining != 0 || Variables.show0Remaining || (currentList.isInactive())) {
                    if (currentShow.getSeason() != season) currentShow.setSeason(season);
                    if (currentShow.getEpisode() != episode) currentShow.setEpisode(episode);
                    if (currentShow.getRemaining() != remaining) currentShow.setRemaining(remaining);
                } else tableViewFields.remove(currentShow);
            } else if (remaining != 0 || Variables.show0Remaining || currentList.isInactive())
                tableViewFields.add(new DisplayShow(ClassHandler.showInfoController().getShowNameFromShowID(showID), remaining, season, episode, showID));
        } else if (currentShow != null) tableViewFields.remove(currentShow);
    }

    // Used to remove the show from the current list. If you are on the active list and set a show inactive, then it will remove it; and the same if you make a show active on the inactive list.
    private static void removeShowField(final int index) {
        tableViewFields.remove(index);
    }

    public static boolean isShowCurrentlyPlaying() {
        return showCurrentlyPlaying != null;
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

    public Map<String, Integer> getChangedShows() {
        return this.changedShows;
    }

    public void setChangedShows(final Map<String, Integer> newShows) {
        newShows.forEach(this.changedShows::put);
    }

    public void addChangedShow(final String aShow, final int remaining) {
        if (!this.changedShows.containsKey(aShow)) {
            this.changedShows.put(aShow, remaining);
            tableView.refresh();
        }
    }

    public void resetChangedShows() {
        this.changedShows.clear();
        Controller.setTableViewFields();
    }

    // This first Filters the observableList if you have anything in the searchList, Then enables or disables the show0RemainingCheckbox depending on which list it is currently on.
    private void setTableView() {
        FilteredList<DisplayShow> newFilteredData = new FilteredList<>(tableViewFields, p -> true);
        newFilteredData.setPredicate(show -> textField.getText() == null || textField.getText().isEmpty() || show.getShow().toLowerCase().contains(textField.getText().toLowerCase()));
        textField.textProperty().addListener((observable, oldValue, newValue) -> newFilteredData.setPredicate(show -> newValue == null || newValue.isEmpty() || show.getShow().toLowerCase().contains(newValue.toLowerCase())));
        SortedList<DisplayShow> newSortedData = new SortedList<>(newFilteredData);
        newSortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(newSortedData);
        showActiveShowsCheckbox.setVisible(currentList.isInactive());
    }

    @Override
    public void initialize(final URL fxmlFileLocation, final ResourceBundle resources) {
        log.finer("Controller Running...");
        ClassHandler.setController(this);
        this.setChangedShows(ClassHandler.userInfoController().getUserSettings().getChangedShowsStatus());
        tableView.placeholderProperty().setValue(new Label(""));
        pane.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT);
        shows.setCellValueFactory(new PropertyValueFactory<>("show"));
        shows.setSortType(TableColumn.SortType.ASCENDING);
        shows.textProperty().bind(Strings.Shows);
        shows.setPrefWidth(ClassHandler.userInfoController().getColumnWidth(Variables.currentUser, StringDB.COLUMN_SHOWCOLUMNWIDTH)); // TODO Make visibility and width change on user change
        shows.setVisible(ClassHandler.userInfoController().getColumnVisibilityStatus(Variables.currentUser, StringDB.COLUMN_SHOWCOLUMNVISIBILITY));
        remaining.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        remaining.textProperty().bind(Strings.Left);
        remaining.setPrefWidth(ClassHandler.userInfoController().getColumnWidth(Variables.currentUser, StringDB.COLUMN_REMAININGCOLUMNWIDTH));
        remaining.setVisible(ClassHandler.userInfoController().getColumnVisibilityStatus(Variables.currentUser, StringDB.COLUMN_REMAININGCOLUMNVISIBILITY));
        season.setCellValueFactory(new PropertyValueFactory<>("season"));
        season.textProperty().bind(Strings.Season);
        season.setPrefWidth(ClassHandler.userInfoController().getColumnWidth(Variables.currentUser, StringDB.COLUMN_SEASONCOLUMNWIDTH));
        season.setVisible(ClassHandler.userInfoController().getColumnVisibilityStatus(Variables.currentUser, StringDB.COLUMN_SEASONCOLUMNVISIBILITY));
        episode.setCellValueFactory(new PropertyValueFactory<>("episode"));
        episode.textProperty().bind(Strings.Episode);
        episode.setPrefWidth(ClassHandler.userInfoController().getColumnWidth(Variables.currentUser, StringDB.COLUMN_EPISODECOLUMNWIDTH));
        episode.setVisible(ClassHandler.userInfoController().getColumnVisibilityStatus(Variables.currentUser, StringDB.COLUMN_EPISODECOLUMNVISIBILITY));
        setTableViewFields(currentList.ACTIVE);
        setTableView();
        tableView.getItems();
        tableView.getSortOrder().add(shows);
        tableView.setRowFactory(
                param -> {
                    final Tooltip rowToolTip = new Tooltip();
                    rowToolTip.getStyleClass().add("tooltip");
                    final TableRow<DisplayShow> row = new TableRow<DisplayShow>() {
                        @Override
                        protected void updateItem(DisplayShow item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null) {
                                if (getTooltip() == null) setTooltip(rowToolTip);
                                rowToolTip.textProperty().bind(Bindings.concat(getItem().showProperty(), " - ", Strings.Season, " ", getItem().seasonProperty(), " - ", Strings.Episode, " ", getItem().episodeProperty(), " - ", getItem().remainingProperty(), " ", Strings.Left));
                                if (currentList.isInactive() && ((Variables.showActiveShows && ClassHandler.userInfoController().isShowActive(Variables.currentUser, item.getShowID())) || (changedShows.containsKey(item.getShow()) && changedShows.get(item.getShow()) == -2)))
                                    setStyle("-fx-background-color: " + ((changedShows.containsKey(item.getShow()) && changedShows.get(item.getShow()) == -2) ? Variables.ShowColorStatus.ADDED.getColor() : Variables.ShowColorStatus.ACTIVE.getColor()));
                                else if (currentList.isActive() && Variables.specialEffects && changedShows.containsKey(item.getShow()) && !isSelected())
                                    setStyle("-fx-background-color: " + Variables.ShowColorStatus.findColorFromRemaining(changedShows.get(item.getShow()), item.getRemaining()).getColor());
                                else if (!getStyle().isEmpty()) setStyle(Strings.EmptyString);
                            } else {
                                if (getTooltip() != null) setTooltip(null);
                                if (!getStyle().isEmpty()) setStyle(Strings.EmptyString);
                            }
                        }
                    };
                    final ContextMenu rowMenu = new ContextMenu();
                    row.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if (row.getItem() != null) {
                            if (!row.isSelected() && currentList.isInactive() && Variables.showActiveShows && ClassHandler.userInfoController().isShowActive(Variables.currentUser, row.getItem().getShowID())) {
                                row.setStyle("-fx-background-color: " + Variables.ShowColorStatus.ACTIVE.getColor());
                                return;
                            } else if (!row.isSelected() && currentList.isActive() && Variables.specialEffects && this.changedShows.containsKey(row.getItem().getShow())) {
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
                        int[] seasonEpisode = new int[0];
                        try {
                            seasonEpisode = new ShowEpisodeSelectBox().seasonEpisodeSelect(row.getItem(), (Stage) pane.getScene().getWindow());
                        } catch (IOException e1) {
                            GenericMethods.printStackTrace(log, e1, this.getClass());
                        }
                        log.info(Arrays.toString(seasonEpisode));
                        if (seasonEpisode.length == 2) {
                            log.info("Season & Episode were valid.");
                            ClassHandler.userInfoController().setSeasonEpisode(Variables.currentUser, row.getItem().getShowID(), seasonEpisode[0], seasonEpisode[1]);
                            updateShowField(row.getItem().getShowID(), true);
                            tableView.getSelectionModel().clearSelection();
                        } else log.info("Season & Episode weren't valid.");
                        log.info("\"Set Season + Episode\" is finished running.");
                    });
                    MenuItem playSeasonEpisode = new MenuItem();
                    playSeasonEpisode.textProperty().bind(Strings.PlaySeasonEpisode);
                    playSeasonEpisode.setOnAction(e -> {
                        log.info("\"Play Season + Episode\" is now running...");
                        int showID = row.getItem().getShowID();
                        int[] seasonEpisode = new ListSelectBox().pickSeasonEpisode(Variables.currentUser, showID, ClassHandler.showInfoController(), (Stage) pane.getScene().getWindow());
                        if (seasonEpisode[0] != -1 && seasonEpisode[1] != -1) {
                            log.info("Season & Episode were valid.");
                            ClassHandler.userInfoController().playAnyEpisode(Variables.currentUser, ClassHandler.showInfoController().getEpisodeID(showID, seasonEpisode[0], seasonEpisode[1]));
                        } else log.info("Season & Episode weren't valid.");
                        log.info("\"Play Season + Episode\" is finished running.");
                    });
                    MenuItem toggleActive = new MenuItem();
                    toggleActive.textProperty().bind(Bindings.when(currentList.isInactiveProperty()).then(Strings.SetActive).otherwise(Strings.SetInactive));
                    toggleActive.setOnAction(e -> {
                        if (currentList.isInactive()) {
                            if (Variables.showActiveShows && ClassHandler.userInfoController().isShowActive(Variables.currentUser, row.getItem().getShowID())) {
                                ClassHandler.userInfoController().setActiveStatus(Variables.currentUser, row.getItem().getShowID(), false);
                                row.setStyle("");
                            } else {
                                ClassHandler.userInfoController().setActiveStatus(Variables.currentUser, row.getItem().getShowID(), true);
                                if (!wereShowsChanged) wereShowsChanged = true;
                                if (Variables.showActiveShows)
                                    row.setStyle("-fx-background-color: " + Variables.ShowColorStatus.ACTIVE.getColor());
                                else
                                    removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            }
                        } else if (currentList.isActive()) {
                            ClassHandler.userInfoController().setActiveStatus(Variables.currentUser, row.getItem().getShowID(), false);
                            removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                        }
                        tableView.getSelectionModel().clearSelection();
                    });
                    MenuItem setActiveAndSetEpisode = new MenuItem();
                    setActiveAndSetEpisode.textProperty().bind(Strings.SetActiveAndPickCurrentEpisode);
                    setActiveAndSetEpisode.setOnAction(e -> {
                        int[] seasonEpisode = new int[0];
                        try {
                            seasonEpisode = new ShowEpisodeSelectBox().seasonEpisodeSelect(row.getItem(), (Stage) pane.getScene().getWindow());
                        } catch (IOException e1) {
                            GenericMethods.printStackTrace(log, e1, this.getClass());
                        }
                        log.info(Arrays.toString(seasonEpisode));
                        if (seasonEpisode.length == 2) {
                            log.info("Season & Episode were valid.");
                            ClassHandler.userInfoController().setSeasonEpisode(Variables.currentUser, row.getItem().getShowID(), seasonEpisode[0], seasonEpisode[1]);
                            updateShowField(row.getItem().getShowID(), true);
                            ClassHandler.userInfoController().setActiveStatus(Variables.currentUser, row.getItem().getShowID(), true);
                            if (!wereShowsChanged) wereShowsChanged = true;
                            if (Variables.showActiveShows)
                                row.setStyle("-fx-background-color: " + Variables.ShowColorStatus.ACTIVE.getColor());
                            else
                                removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            tableView.getSelectionModel().clearSelection();
                        } else log.info("Season & Episode weren't valid.");
                    });
                    MenuItem setHidden = new MenuItem();
                    setHidden.textProperty().bind(Strings.HideShow);
                    setHidden.setOnAction(e -> {
                        ClassHandler.userInfoController().setHiddenStatus(Variables.currentUser, row.getItem().getShowID(), true);
                        removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                        tableView.getSelectionModel().clearSelection();
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
                                folders.add(new Directory(fileName, fileName.toString(), -2, null));
                        });
                        if (folders.size() == 1) fileManager.openFolder(folders.get(0).getDirectory());
                        else new ListSelectBox().openDirectory(folders, (Stage) pane.getScene().getWindow());
                        log.info("Finished opening show directory...");
                    });
                    MenuItem getRemaining = new MenuItem();
                    getRemaining.textProperty().bind(Strings.GetRemaining);
                    getRemaining.setOnAction(e -> log.info("There are " + ClassHandler.userInfoController().getRemainingNumberOfEpisodes(Variables.currentUser, row.getItem().getShowID()) + " episode(s) remaining."));
                    MenuItem playPreviousEpisode = new MenuItem();
                    playPreviousEpisode.textProperty().bind(Strings.PlayPreviousEpisode);
                    playPreviousEpisode.setOnAction(e -> {
                        log.info("Attempting to play previous episode...");
                        int[] seasonEpisode = ClassHandler.userInfoController().getPreviousEpisodeIfExists(Variables.currentUser, row.getItem().getShowID());
                        if (seasonEpisode[0] == -2 || seasonEpisode[0] == -3)
                            new MessageBox(new StringProperty[]{Strings.NoDirectlyPrecedingEpisodesFound}, (Stage) pane.getScene().getWindow());
                        else
                            ClassHandler.userInfoController().playAnyEpisode(Variables.currentUser, ClassHandler.showInfoController().getEpisodeID(row.getItem().getShowID(), seasonEpisode[0], seasonEpisode[1]));
                        log.info("Finished attempting to play previous episode.");
                    });
                    MenuItem printCurrentSeasonEpisode = new MenuItem();
                    printCurrentSeasonEpisode.textProperty().bind(Strings.PrintCurrentSeasonEpisode);
                    printCurrentSeasonEpisode.setOnAction(e -> log.info(row.getItem().getShow() + " - Season: " + ClassHandler.userInfoController().getCurrentUserSeason(Variables.currentUser, row.getItem().getShowID()) + " - Episode: " + ClassHandler.userInfoController().getCurrentUserEpisode(Variables.currentUser, row.getItem().getShowID())));
                    MenuItem printShowInformation = new MenuItem();
                    printShowInformation.textProperty().bind(Strings.PrintShowInformation);
                    printShowInformation.setOnAction(e -> ClassHandler.developerStuff().printShowInformation(row.getItem().getShow()));
                    MenuItem getMissingEpisodes = new MenuItem();
                    /*getMissingEpisodes.textProperty().bind(Strings.GetMissingEpisodes); // TODO Fix and Enable
                    getMissingEpisodes.setOnAction(e -> {
                        Map<Integer, Set<Integer>> missingInfo = ClassHandler.showInfoController().getMissingEpisodes(row.getItem().getShowID());
                        if (missingInfo.isEmpty())
                            new MessageBox(new StringProperty[]{new SimpleStringProperty("Show is missing database info / Has no missing episodes.")}, (Stage) pane.getScene().getWindow());
                        else {
                            StringProperty[] info = new StringProperty[missingInfo.keySet().size()];
                            int i = 0;
                            missingInfo.forEach((integer, integers) -> {
                                info[i] = new SimpleStringProperty("Season: " + integer + " - Episode: " + integers);
                                log.info("Missing info for " + row.getItem().getShow() + " - Season: " + integer + " - Episodes: " + integers);
                            });
                            new MessageBox(info, (Stage) pane.getScene().getWindow());
                        }
                    });*/
                    MenuItem showCurrentlyPlayingMenuItem = new MenuItem();
                    showCurrentlyPlayingMenuItem.textProperty().bind(Strings.ShowIsCurrentlyPlayingAndCannotBeEdited);
                    row.setOnMouseClicked(e -> {
                        if (e.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
                            if (currentList.isActive()) {
                                rowMenu.getItems().clear();
                                if (isShowCurrentlyPlaying()) {
                                    if (showCurrentlyPlaying.getShow().matches(row.getItem().getShow()))
                                        rowMenu.getItems().addAll(showCurrentlyPlayingMenuItem, openDirectory);
                                    else
                                        rowMenu.getItems().addAll(setSeasonEpisode, toggleActive, openDirectory);
                                } else
                                    rowMenu.getItems().addAll(setSeasonEpisode, playSeasonEpisode, playPreviousEpisode, toggleActive, openDirectory);
                                if (DeveloperStuff.devMode)
                                    rowMenu.getItems().addAll(getRemaining, printCurrentSeasonEpisode, printShowInformation, getMissingEpisodes);
                            } else if (currentList.isInactive()) {
                                rowMenu.getItems().clear();
                                if (Variables.showActiveShows && ClassHandler.userInfoController().isShowActive(Variables.currentUser, row.getItem().getShowID())) {
                                    if (!isShowCurrentlyPlaying() || !showCurrentlyPlaying.getShow().matches(row.getItem().getShow()))
                                        rowMenu.getItems().add(toggleActive);
                                    else rowMenu.getItems().add(showCurrentlyPlayingMenuItem);
                                } else {
                                    if (DeveloperStuff.devMode)
                                        rowMenu.getItems().addAll(toggleActive, setActiveAndSetEpisode, setHidden, getRemaining, openDirectory, printShowInformation);
                                    else
                                        rowMenu.getItems().addAll(toggleActive, setActiveAndSetEpisode, setHidden, openDirectory);
                                }
                            }
                            row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu).otherwise((ContextMenu) null));
                        } else if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && !isShowCurrentlyPlaying() && (!row.isEmpty()) && currentList.isActive())
                            this.playShow(row.getItem());
                    });
                    return row;
                }
        );

        tableView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER && !isShowCurrentlyPlaying() && (tableView.getFocusModel().getFocusedItem() != null) && currentList.isActive())
                this.playShow(tableView.getFocusModel().getFocusedItem());
        });

        userName.setVisible(ClassHandler.userInfoController().isShowUsername());
        userName.textProperty().bind(Strings.UserName);
        userNameComboBox.setVisible(ClassHandler.userInfoController().getUserSettings().isShowUsername());
        userNameComboBox.setDisable(!ClassHandler.userInfoController().getUserSettings().isShowUsername());
        comboBoxTooltip.textProperty().bind(Strings.UserName);

        userNameComboBox.addEventFilter(MouseEvent.MOUSE_RELEASED, mouse -> {
            if (!mouse.isStillSincePress()) mouse.consume();
            userNameComboBox.setCursor(Cursor.DEFAULT);
        });
        userNameComboBox.setOnShown(e -> {
            this.toggleUsernameUsability(true);
            ArrayList<String> users = ClassHandler.userInfoController().getAllUsers();
            users.remove(Strings.UserName.getValue());
            userNameComboBox.getItems().addAll(users);
        });
        userNameComboBox.setOnHidden(event -> {
            this.toggleUsernameUsability(false);
            userNameComboBox.getItems().clear();
        });
        userNameComboBox.setOnAction(e -> {
            if (userNameComboBox.getValue() != null && !userNameComboBox.getValue().matches(Strings.UserName.getValue())) {
                GenericMethods.saveSettings();
                Strings.UserName.setValue(userNameComboBox.getValue());
                ChangeReporter.resetChanges();
                try {
                    ClassHandler.mainRun().loadUser(new UpdateManager(), false);
                } catch (IOException e1) {
                    GenericMethods.printStackTrace(log, e1, getClass());
                }
                Controller.setTableViewFields();
            }
        });
        userName.setTextFill(Paint.valueOf(Color.DIMGRAY.toString()));

        clearTextField.setText(Strings.EmptyString);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() && clearTextField.isVisible()) clearTextField.setVisible(false);
            else if (!newValue.isEmpty() && !clearTextField.isVisible()) clearTextField.setVisible(true);
        });
        clearTextField.setVisible(false);
        clearTextField.setOnAction(e -> {
            textField.clear();
            textField.requestFocus();
        });

        // ~~~~ Buttons ~~~~ \\
        exit.setOnAction(e -> Main.stop(Main.stage, false, true));
        minimize.setOnAction(e -> Main.stage.setIconified(true));
        //changeTableViewButton.textProperty().bind(Strings.SwitchBetweenActiveInactiveList);
        changeTableViewButton.setOnAction(event -> {
            if (currentList.isActive()) {
                setTableViewFields(currentList.INACTIVE);
                tableView.scrollTo(0);
                tableView.scrollToColumnIndex(0);
                show0RemainingRadioMenuItem.setVisible(false);
                log.info("TableViewFields set to inactive.");
            } else if (currentList.isInactive()) {
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
                setTableViewFields(currentList.ACTIVE);
                tableView.scrollTo(0);
                tableView.scrollToColumnIndex(0);
                show0RemainingRadioMenuItem.setVisible(true);
                log.info("TableViewFields set to active.");
            }
            this.setTableView();
        });
        changesAlert.setOnAction(e -> openChangeBox());
        if (Variables.specialEffects) changesAlert.setOpacity(0.0);
        else changesAlert.setOpacity(1.0);
        show0RemainingRadioMenuItem.textProperty().bind(Strings.Show0Remaining);
        show0RemainingRadioMenuItem.setSelected(ClassHandler.userInfoController().getUserBooleanSetting(Variables.currentUser, StringDB.COLUMN_SHOW0REMAINING));
        show0RemainingRadioMenuItem.setOnAction(e -> {
            Variables.show0Remaining = !Variables.show0Remaining;
            ClassHandler.userInfoController().setUserBooleanSetting(Variables.currentUser, StringDB.COLUMN_SHOW0REMAINING, Variables.show0Remaining);
            if (Variables.show0Remaining)
                log.info("Now showing shows with 0 episodes remaining.");
            else log.info("No longer showing shows with 0 episodes remaining.");
            setTableViewFields();
            setTableView();
        });
        showActiveShowsCheckbox.setSelected(Variables.showActiveShows);
        showActiveShowsCheckbox.setOnAction(e -> {
            ClassHandler.userInfoController().setUserBooleanSetting(Variables.currentUser, StringDB.COLUMN_SHOWACTIVESHOWS, showActiveShowsCheckbox.isSelected());
            Variables.showActiveShows = showActiveShowsCheckbox.isSelected();
            if (Variables.showActiveShows)
                log.info("Now showing active shows.");
            else log.info("No longer showing active shows.");
            setTableViewFields();
            setTableView();
        });
        showActiveShowsCheckboxTooltip.textProperty().bind(Strings.ToggleActiveShowsVisibility);
        Tooltip pingingDirectoryTooltip = new Tooltip();
        pingingDirectoryTooltip.textProperty().bind(Strings.PingingDirectories);
        pingingDirectoryTooltip.getStyleClass().add("tooltip");
        Tooltip.install(pingingDirectoryPane, pingingDirectoryTooltip);

        //TODO Start
        mainTab.textProperty().bind(Strings.Main);
        usersTab.textProperty().bind(Strings.Users);
        showTab.textProperty().bind(Strings.Shows);
        uiTab.textProperty().bind(Strings.UI);
        otherTab.textProperty().bind(Strings.Other);
        developerTab.textProperty().bind(Strings.Dev);
        dev1Tab.textProperty().bind(Strings.Dev1);
        dev2Tab.textProperty().bind(Strings.Dev2);
        // Main
        updateText.textProperty().bind(Strings.UpdateTime);
        updateText.setTextAlignment(TextAlignment.CENTER);
        updateTimeTextField.setText(String.valueOf(Variables.updateSpeed));
        updateTimeTextField.setDisable(Variables.disableAutomaticRechecking);
        setUpdateTime.textProperty().bind(Strings.Set);
        setUpdateTime.setDisable(Variables.disableAutomaticRechecking);
        setUpdateTime.setOnAction(e -> {
            if (isNumberValid(updateTimeTextField.getText(), 10))
                ClassHandler.userInfoController().setUpdateSpeed(Variables.currentUser, Integer.valueOf(updateTimeTextField.getText()));
            else updateTimeTextField.setText(String.valueOf(Variables.updateSpeed));
        });
        disableAutomaticShowUpdating.textProperty().bind(Strings.DisableAutomaticShowSearching);
        //noinspection ConstantConditions
        disableAutomaticShowUpdating.setSelected(Variables.forceDisableAutomaticRechecking || Variables.disableAutomaticRechecking);
        disableAutomaticShowUpdating.setDisable(Variables.forceDisableAutomaticRechecking);
        disableAutomaticShowUpdating.setOnAction(e -> {
            ClassHandler.programSettingsController().getSettingsFile().setDisableAutomaticShowUpdating(!ClassHandler.programSettingsController().getSettingsFile().isDisableAutomaticShowUpdating());
            updateTimeTextField.setDisable(Variables.disableAutomaticRechecking);
            setUpdateTime.setDisable(Variables.disableAutomaticRechecking);
            log.info("Disable automatic show checking has been set to: " + Variables.disableAutomaticRechecking);
        });
        notifyChangesText.textProperty().bind(Strings.NotifyChangesFor);
        notifyChangesText.setTextAlignment(TextAlignment.CENTER);
        onlyChecksEveryText.textProperty().bind(Strings.OnlyChecksEveryRuns);
        onlyChecksEveryText.setTextAlignment(TextAlignment.CENTER);
        inactiveShowsCheckBox.textProperty().bind(Strings.InactiveShows);
        inactiveShowsCheckBox.setSelected(Variables.recordChangesForNonActiveShows);
        inactiveShowsCheckBox.setOnAction(e -> {
            Variables.recordChangesForNonActiveShows = !Variables.recordChangesForNonActiveShows;
            ClassHandler.userInfoController().setUserBooleanSetting(Variables.currentUser, StringDB.COLUMN_RECORDCHANGESFORNONACTIVESHOWS, Variables.recordChangesForNonActiveShows);
            log.info("Record inactive shows has been set to: " + Variables.recordChangesForNonActiveShows);
        });
        olderSeasonsCheckBox.textProperty().bind(Strings.OlderSeasons);
        olderSeasonsCheckBox.setSelected(Variables.recordChangedSeasonsLowerThanCurrent);
        olderSeasonsCheckBox.setOnAction(e -> {
            Variables.recordChangedSeasonsLowerThanCurrent = !Variables.recordChangedSeasonsLowerThanCurrent;
            ClassHandler.userInfoController().setUserBooleanSetting(Variables.currentUser, StringDB.COLUMN_RECORDCHANGEDSEASONSLOWERTHANCURRENT, Variables.recordChangedSeasonsLowerThanCurrent);
            log.info("Record older seasons has been set to: " + Variables.recordChangedSeasonsLowerThanCurrent);
        });
        about.textProperty().bind(Strings.About);
        about.setOnAction(e -> {
            setButtonDisable(true, about);
            try {
                new AboutBox((Stage) tabPane.getScene().getWindow());
            } catch (Exception e1) {
                GenericMethods.printStackTrace(log, e1, this.getClass());
            }
            setButtonDisable(false, about);
        });

        // User
        currentUserText.textProperty().bind(Strings.CurrentUser);
        currentUserComboBox.getItems().addAll(ClassHandler.userInfoController().getAllUsers());
        currentUserComboBox.getSelectionModel().select(Strings.UserName.getValue());
        currentUserComboBox.setOnAction(e -> {
            if (currentUserComboBox.getValue() != null && !currentUserComboBox.getValue().matches(Strings.UserName.getValue())) {
                GenericMethods.saveSettings();
                Strings.UserName.setValue(currentUserComboBox.getValue());
                ChangeReporter.resetChanges();
                try {
                    ClassHandler.mainRun().loadUser(new UpdateManager(), false);
                } catch (IOException e1) {
                    GenericMethods.printStackTrace(log, e1, getClass());
                }
                Controller.setTableViewFields();
            }
        });
        setDefaultUsername.textProperty().bind(Strings.SetDefaultUser);
        setDefaultUsername.setOnAction(e -> {
            setButtonDisable(true, setDefaultUsername, clearDefaultUsername, addUser, deleteUser);
            ListSelectBox listSelectBox = new ListSelectBox();
            ArrayList<Integer> Users = ClassHandler.userInfoController().getAllUsers();
            int defaultUsername = listSelectBox.pickDefaultUser(Strings.PleaseChooseADefaultUser, Users, ClassHandler.programSettingsController().getDefaultUser(), (Stage) tabPane.getScene().getWindow());
            if (defaultUsername != -2)
                ClassHandler.programSettingsController().setDefaultUser(defaultUsername);
            setButtonDisable(false, setDefaultUsername, clearDefaultUsername, addUser, deleteUser);
        });
        clearDefaultUsername.textProperty().bind(Strings.Reset);
        clearDefaultUsername.setOnAction(e -> ClassHandler.programSettingsController().removeDefaultUser());
        addUser.textProperty().bind(Strings.AddUser);
        addUser.setOnAction(e -> {
            setButtonDisable(true, addUser, deleteUser, currentUserComboBox, setDefaultUsername);
            String userName = new TextBox().addUser(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, (Stage) tabPane.getScene().getWindow());
            if (userName.isEmpty()) log.info("New user wasn't added.");
            else {
                ClassHandler.userInfoController().addUser(userName);
                log.info(userName + " was added.");
            }
            currentUserComboBox.getItems().clear();
            currentUserComboBox.getItems().addAll(ClassHandler.userInfoController().getAllUsers());
            currentUserComboBox.getSelectionModel().select(Strings.UserName.getValue());
            setButtonDisable(false, addUser, deleteUser, currentUserComboBox, setDefaultUsername);
        });
        deleteUser.textProperty().bind(Strings.DeleteUser);
        deleteUser.setOnAction(e -> {
            setButtonDisable(true, deleteUser, addUser, currentUserComboBox, setDefaultUsername);
            ArrayList<Integer> users = ClassHandler.userInfoController().getAllUsers();
            users.remove(Strings.UserName.getValue());
            if (users.isEmpty())
                new MessageBox(new StringProperty[]{Strings.ThereAreNoOtherUsersToDelete}, (Stage) tabPane.getScene().getWindow());
            else {
                int userToDelete = new ListSelectBox().pickDefaultUser(Strings.UserToDelete, users, -2, (Stage) tabPane.getScene().getWindow());
                if (userToDelete != -2) {
                    boolean confirm = new ConfirmBox().confirm(new SimpleStringProperty(Strings.AreYouSureToWantToDelete.getValue() + ClassHandler.userInfoController().getUserNameFromID(userToDelete) + Strings.QuestionMark.getValue()), (Stage) tabPane.getScene().getWindow());
                    // TODO Finish user deletion
                   /* if (confirm && !new FileManager().deleteFile(Variables.UsersFolder, userToDelete, Variables.UserFileExtension))
                        log.info("Wasn't able to delete user file.");*/
                }
            }
            currentUserComboBox.getItems().clear();
            currentUserComboBox.getItems().addAll(ClassHandler.userInfoController().getAllUsers());
            currentUserComboBox.getSelectionModel().select(Strings.UserName.getValue());
            setButtonDisable(false, deleteUser, addUser, currentUserComboBox, setDefaultUsername);
        });
        deleteUserTooltip.textProperty().bind(Strings.DeleteUsersNoteCantDeleteCurrentUser);

        // Show
        forceRecheck.textProperty().bind(Strings.ForceRecheckShows);
        forceRecheck.setOnAction(e -> {
            setButtonDisable(true, forceRecheck);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ClassHandler.checkShowFiles().recheckShowFile(true);
                    setButtonDisable(false, forceRecheck);
                    return null;
                }
            };
            new Thread(task).start();
        });
        unHideShow.textProperty().bind(Strings.UnHideShow);
        unHideShow.setOnAction(e -> {
            setButtonDisable(true, unHideShow);
            ArrayList<String> hiddenShows = ClassHandler.userInfoController().getHiddenShows();
            if (hiddenShows.isEmpty())
                new MessageBox(new StringProperty[]{Strings.ThereAreNoHiddenShows}, (Stage) tabPane.getScene().getWindow());
            else {
                String showToUnHide = new ListSelectBox().pickShow(ClassHandler.userInfoController().getHiddenShows(Variables.currentUser), (Stage) tabPane.getScene().getWindow());
                if (showToUnHide != null && !showToUnHide.isEmpty()) {
                    ClassHandler.userInfoController().setHiddenStatus(Variables.currentUser, showToUnHide, false);
                    Controller.updateShowField(showToUnHide, true);
                    log.info(showToUnHide + " was unhidden.");
                } else log.info("No show was unhidden.");
            }
            setButtonDisable(false, unHideShow);
        });
        useOnlineDatabaseCheckbox.textProperty().bind(Strings.UseOnlineDatabase); // TODO Enable once working
        useOnlineDatabaseCheckbox.setSelected(Variables.useOnlineDatabase);
        useOnlineDatabaseCheckbox.setOnAction(e -> {
            Variables.useOnlineDatabase = !Variables.useOnlineDatabase;
            ClassHandler.userInfoController().setUserBooleanSetting(Variables.currentUser, StringDB.COLUMN_USEREMOTEDATABASE, Variables.useOnlineDatabase);
            log.info("Use online database has been set too: " + Variables.useOnlineDatabase);
        });
        onlineWarningText.textProperty().bind(Strings.WarningConnectsToRemoteWebsite);
        useOnlineDatabaseCheckbox.setDisable(true);
        changeVideoPlayerButton.setOnAction(e -> { // TODO Add Localization
            try {
                ClassHandler.userInfoController().getUserSettings().setVideoPlayer(new VideoPlayerSelectorBox().videoPlayerSelector((Stage) tabPane.getScene().getWindow()));
            } catch (IOException e1) {
                GenericMethods.printStackTrace(log, e1, getClass());
            }
        });

        // UI
        unlockParentScene.textProperty().bind(Strings.AllowFullWindowMovementUse);
        unlockParentScene.setSelected(!ClassHandler.programSettingsController().getSettingsFile().isStageMoveWithParentAndBlockParent());
        unlockParentScene.setOnAction(e -> {
            ClassHandler.programSettingsController().getSettingsFile().setStageMoveWithParentAndBlockParent(!ClassHandler.programSettingsController().getSettingsFile().isStageMoveWithParentAndBlockParent());
            Variables.setStageMoveWithParentAndBlockParent(ClassHandler.programSettingsController().getSettingsFile().isStageMoveWithParentAndBlockParent());
            log.info("MoveAndBlock has been set to: " + ClassHandler.programSettingsController().getSettingsFile().isStageMoveWithParentAndBlockParent());
            if (!ClassHandler.programSettingsController().getSettingsFile().isStageMoveWithParentAndBlockParent())
                new MessageBox(new StringProperty[]{new SimpleStringProperty("Warning- Using this can cause things to break if used improperly.")}, (Stage) mainPane.getScene().getWindow()); // TODO Add localization
        });
        showUsername.textProperty().bind(Strings.ShowUsername);
        showUsername.setSelected(ClassHandler.userInfoController().getUserSettings().isShowUsername());
        showUsername.setOnAction(e -> {
            ClassHandler.userInfoController().getUserSettings().setShowUsername(showUsername.isSelected());
            userName.setVisible(ClassHandler.userInfoController().getUserSettings().isShowUsername());
        });
        specialEffects.textProperty().bind(Strings.SpecialEffects);
        specialEffects.setSelected(Variables.specialEffects);
        specialEffects.setOnAction(e -> {
            ClassHandler.programSettingsController().getSettingsFile().setEnableSpecialEffects(!ClassHandler.programSettingsController().getSettingsFile().isEnableSpecialEffects());
            log.info("Special Effects has been set to: " + Variables.specialEffects);
        });
        automaticSaving.textProperty().bind(Strings.EnableAutomaticSaving);
        automaticSaving.setSelected(Variables.enableAutoSavingOnTimer);
        automaticSaving.setOnAction(e -> {
            ClassHandler.programSettingsController().getSettingsFile().setEnableAutomaticSaving(!ClassHandler.programSettingsController().getSettingsFile().isEnableAutomaticSaving());
            if (Variables.enableAutoSavingOnTimer && updateSavingTextField.getText().matches(String.valueOf(0))) {
                ClassHandler.programSettingsController().setSavingSpeed(Variables.defaultSavingSpeed);
                updateSavingTextField.setText(String.valueOf(Variables.defaultSavingSpeed));
            }
            setSavingTime.setDisable(!Variables.enableAutoSavingOnTimer);
            updateSavingTextField.setDisable(!Variables.enableAutoSavingOnTimer);
            log.info("Automatic saving has been set to: " + Variables.enableAutoSavingOnTimer);
        });
        savingText.textProperty().bind(Strings.SavingWaitTimeSeconds);
        savingText.setTextAlignment(TextAlignment.CENTER);
        updateSavingTextField.setText(String.valueOf(Variables.savingSpeed));
        updateSavingTextField.setDisable(!Variables.enableAutoSavingOnTimer);
        setSavingTime.textProperty().bind(Strings.Set);
        setSavingTime.setDisable(!Variables.enableAutoSavingOnTimer);
        setSavingTime.setOnAction(e -> {
            if (isNumberValid(updateSavingTextField.getText(), 5))
                ClassHandler.programSettingsController().setSavingSpeed(Integer.valueOf(updateSavingTextField.getText()));
            else updateSavingTextField.setText(String.valueOf(Variables.savingSpeed));
        });
        changeLanguage.textProperty().bind(Strings.ChangeLanguage);
        changeLanguage.setOnAction(e -> {
            setButtonDisable(true, changeLanguage);
            LanguageHandler languageHandler = new LanguageHandler();
            Map<String, String> languages = languageHandler.getLanguageNames();
            if (languages.containsKey(ClassHandler.programSettingsController().getSettingsFile().getLanguage()))
                languages.remove(ClassHandler.programSettingsController().getSettingsFile().getLanguage());
            Object[] pickLanguageResult = new ListSelectBox().pickLanguage(languages.values(), false, (Stage) tabPane.getScene().getWindow());
            String languageReadable = (String) pickLanguageResult[0];
            if (!languageReadable.contains("-2")) {
                String internalName = Strings.EmptyString;
                for (String langKey : languages.keySet()) {
                    if (languages.get(langKey).matches(languageReadable)) {
                        internalName = langKey;
                        break;
                    }
                }
                ClassHandler.programSettingsController().setDefaultLanguage(internalName);
                languageHandler.setLanguage(Variables.language);
            }
            setButtonDisable(false, changeLanguage);
        });

        // Other
        directoryText.textProperty().bind(Strings.Directory);
        addDirectory.textProperty().bind(Strings.AddDirectory);
        addDirectory.setOnAction(e -> {
            setButtonDisable(true, addDirectory, removeDirectory);
            ArrayList<File> directories = new TextBox().addDirectory(Strings.PleaseEnterShowsDirectory, ClassHandler.directoryController().findDirectories(true, false, true), (Stage) tabPane.getScene().getWindow());
            directories.forEach(file -> {
                Long[] wasAdded = ClassHandler.directoryController().addDirectory(file);
                if (wasAdded[0] != null && wasAdded[1] == null) {
                    log.info("Directory was added.");
                    //FindChangedShows findChangedShows = new FindChangedShows(ClassHandler.showInfoController().getShowsFile(), ClassHandler.userInfoController());
                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            new FirstRun().generateShowsFile(ClassHandler.directoryController().getDirectory(wasAdded[0]));
                            return null;
                        }
                    };
                    Thread generateShowsFileThread = new Thread(task);
                    generateShowsFileThread.start();
                    try {
                        generateShowsFileThread.join();
                    } catch (InterruptedException e1) {
                        GenericMethods.printStackTrace(log, e1, this.getClass());
                    }
                    //ClassHandler.showInfoController().loadShowsFile(ClassHandler.directoryController().findDirectories(false, true, false));
                    //findChangedShows.findShowFileDifferences(ClassHandler.showInfoController().getShowsFile());
                    ClassHandler.directoryController().getDirectory(wasAdded[0]).getShows().keySet().forEach(aShow -> {
                        ClassHandler.userInfoController().addNewShow(aShow);
                        Controller.updateShowField(aShow, true);
                    });
                    ClassHandler.programSettingsController().setMainDirectoryVersion(ClassHandler.programSettingsController().getSettingsFile().getMainDirectoryVersion() + 1);
                } else log.info("Directory \"" + file + "\" wasn't added.");
            });
            setButtonDisable(false, addDirectory, removeDirectory);
        });
        removeDirectory.textProperty().bind(Strings.RemoveDirectory);
        removeDirectory.setOnAction(e -> {
            log.info("Remove Directory Started:");
            setButtonDisable(true, removeDirectory, addDirectory);
            ArrayList<Directory> directories = ClassHandler.directoryController().findDirectories(true, false, true);
            if (directories.isEmpty()) {
                log.info("No directories to delete.");
                new MessageBox(new StringProperty[]{Strings.ThereAreNoDirectoriesToDelete}, (Stage) tabPane.getScene().getWindow());
            } else {
                Directory directoryToDelete = new ListSelectBox().pickDirectory(Strings.DirectoryToDelete, directories, (Stage) tabPane.getScene().getWindow());
                if (directoryToDelete != null && !directoryToDelete.toString().isEmpty()) {
                    log.info("Directory selected for deletion: " + directoryToDelete.getFileName());
                    boolean confirm = new ConfirmBox().confirm(new SimpleStringProperty(Strings.AreYouSureToWantToDelete.getValue() + directoryToDelete.getFileName() + Strings.QuestionMark.getValue()), (Stage) tabPane.getScene().getWindow());
                    if (confirm) {
                        ArrayList<Directory> otherDirectories = ClassHandler.directoryController().findDirectories(directoryToDelete.getDirectoryID(), true, false, true);
                        Map<String, Boolean> showsToUpdate = new HashMap<>();
                        directoryToDelete.getShows().keySet().forEach(aShow -> {
                            log.info("Currently checking: " + aShow);
                            boolean showExistsElsewhere = ClassHandler.showInfoController().doesShowExistElsewhere(aShow, otherDirectories);
                            if (!showExistsElsewhere) {
                                ClassHandler.userInfoController().setIgnoredStatus(aShow, true);
                                ChangeReporter.addChange("- " + aShow);
                            }
                            showsToUpdate.put(aShow, showExistsElsewhere);
                        });
                        ClassHandler.directoryController().removeDirectory(directoryToDelete);
                        //ClassHandler.showInfoController().loadShowsFile(ClassHandler.directoryController().findDirectories(false, true, false));
                        showsToUpdate.forEach(Controller::updateShowField);
                        ClassHandler.programSettingsController().setMainDirectoryVersion(ClassHandler.programSettingsController().getSettingsFile().getMainDirectoryVersion() + 1);
                        log.info('"' + directoryToDelete.getFileName() + "\" has been deleted!");
                    } else log.info("No directory has been deleted.");
                }
            }
            log.info("Remove Directory Finished!");
            setButtonDisable(false, removeDirectory, addDirectory);
        });
        directoryTimeoutText.textProperty().bind(Strings.DirectoryTimeout);
        directoryTimeoutText.setTextAlignment(TextAlignment.CENTER);
        directoryTimeoutTextField.setText(String.valueOf(Variables.timeToWaitForDirectory));
        setDirectoryTimeout.textProperty().bind(Strings.Set);
        setDirectoryTimeout.setOnAction(e -> {
            if (isNumberValid(directoryTimeoutTextField.getText(), 2)) {
                if (directoryTimeoutTextField.getText().isEmpty())
                    directoryTimeoutTextField.setText(String.valueOf(Variables.timeToWaitForDirectory));
                else
                    ClassHandler.programSettingsController().setTimeToWaitForDirectory(Integer.valueOf(directoryTimeoutTextField.getText()));
            }
        });
        enableLoggingCheckbox.textProperty().bind(Strings.EnableFileLogging);
        enableLoggingCheckbox.setSelected(Variables.enableFileLogging);
        enableLoggingCheckbox.setOnAction(e -> {
            ClassHandler.programSettingsController().setFileLogging(!ClassHandler.programSettingsController().getSettingsFile().isFileLogging());
            log.info("Enable file logging is now: " + Variables.enableFileLogging);
        });
        exportSettings.textProperty().bind(Strings.ExportSettings);
        exportSettings.setOnAction(e -> new FileManager().exportSettings((Stage) tabPane.getScene().getWindow()));
        importSettings.textProperty().bind(Strings.ImportSettings);
        importSettings.setOnAction(e -> new FileManager().importSettings(false, (Stage) tabPane.getScene().getWindow()));
        if (DeveloperStuff.showOptionToToggleDevMode) {
            if (DeveloperStuff.devMode) toggleDevMode.setSelected(true);
            toggleDevMode.textProperty().bind(Strings.ToggleDevMode);
            toggleDevMode.setOnAction(e -> {
                DeveloperStuff.devMode = !DeveloperStuff.devMode;
                if (DeveloperStuff.devMode) {
                    developerTab.setDisable(false);
                    tabPane.getTabs().add(developerTab);
                    GenericMethods.setLoggerLevel(Level.ALL);
                } else {
                    developerTab.setDisable(true);
                    tabPane.getTabs().remove(developerTab);
                    GenericMethods.setLoggerLevel(Variables.loggerLevel);
                }
            });
        } else toggleDevMode.setVisible(false);
        openProgramFolder.textProperty().bind(Strings.OpenSettingsFolder);
        openProgramFolder.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(Variables.dataFolder);
            } catch (IOException e1) {
                GenericMethods.printStackTrace(log, e1, this.getClass());
            }
        });
        if (!DeveloperStuff.devMode) {
            developerTab.setDisable(true);
            tabPane.getTabs().remove(developerTab);
        }

        // Developer
        printAllShows.textProperty().bind(Strings.PrintAllShowInfo);
        printAllShows.setOnAction(e -> ClassHandler.developerStuff().printOutAllShowsAndEpisodes());
        printAllDirectories.textProperty().bind(Strings.PrintAllDirectories);
        printAllDirectories.setOnAction(e -> ClassHandler.developerStuff().printAllDirectories());
        printEmptyShowFolders.textProperty().bind(Strings.PrintEmptyShows);
        printEmptyShowFolders.setOnAction(e -> ClassHandler.developerStuff().printEmptyShows());
        printIgnoredShows.textProperty().bind(Strings.PrintIgnoredShows);
        printIgnoredShows.setOnAction(e -> ClassHandler.developerStuff().printIgnoredShows());
        printHiddenShows.textProperty().bind(Strings.PrintHiddenShows);
        printHiddenShows.setOnAction(e -> ClassHandler.developerStuff().printHiddenShows());
        unHideAll.textProperty().bind(Strings.UnHideAll);
        unHideAll.setOnAction(e -> ClassHandler.developerStuff().unHideAllShows());
        setAllActive.textProperty().bind(Strings.SetAllActive);
        setAllActive.setOnAction(e -> ClassHandler.developerStuff().setAllShowsActive());
        setAllInactive.textProperty().bind(Strings.SetAllInactive);
        setAllInactive.setOnAction(e -> ClassHandler.developerStuff().setAllShowsInactive());
        // Dev 2
        printProgramSettingsFileVersion.textProperty().bind(Strings.PrintPsfvAndUsfv); // TODO Reenable button
        //printProgramSettingsFileVersion.setOnAction(e -> log.info("PSFV: " + String.valueOf(ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsFileVersion() + " || USFV: " + ClassHandler.userInfoController().getUserSettings().getUserSettingsFileVersion())));
        printAllUserInfo.textProperty().bind(Strings.PrintAllUserInfo);
        printAllUserInfo.setOnAction(e -> ClassHandler.developerStuff().printAllUserInfo());
        nonForceRecheckShows.textProperty().bind(Strings.NonForceRecheckShows);
        nonForceRecheckShows.setOnAction(e -> {
            setButtonDisable(true, nonForceRecheckShows);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ClassHandler.checkShowFiles().recheckShowFile(false);
                    setButtonDisable(false, nonForceRecheckShows);
                    return null;
                }
            };
            new Thread(task).start();
        });
        this.toggleIsChanges.textProperty().bind(Strings.ToggleIsChanges);
        this.toggleIsChanges.setOnAction(e -> {
            ClassHandler.developerStuff().toggleIsChanges();
            log.info("isChanges has been set to:" + ChangeReporter.getIsChanges());
        });
        clearFile.textProperty().bind(Strings.ClearFile);
        clearFile.setOnAction(e -> {
            setButtonDisable(true, clearFile);
            ClassHandler.developerStuff().clearDirectory((Stage) tabPane.getScene().getWindow());
            setButtonDisable(false, clearFile);
        });
        // Once I am sure this works properly, I will make it accessible.
        deleteEverythingAndClose.textProperty().bind(Strings.ResetProgram);
        deleteEverythingAndCloseTooltip.textProperty().bind(Strings.WarningUnrecoverable);
        deleteEverythingAndClose.setOnAction(e -> {
            setButtonDisable(true, deleteEverythingAndClose);
            ConfirmBox confirmBox = new ConfirmBox();
            if (confirmBox.confirm(Strings.AreYouSureThisWillDeleteEverything, (Stage) tabPane.getScene().getWindow())) {
                Stage stage = (Stage) tabPane.getScene().getWindow();
                stage.close();
                new FileManager().clearProgramFiles();
                Main.stop(Main.stage, true, false);
            } else setButtonDisable(false, deleteEverythingAndClose);
        });
        // TODO End


        // || ~~~~ Settings Tab ~~~~ || \\
        settingsButton.setOnAction(e -> {
            mainPane.setVisible(false);
            settingsAnchorPane.setVisible(true);
            if (ClassHandler.userInfoController().getUserBooleanSetting(Variables.currentUser, StringDB.COLUMN_SHOWUSERNAME)) {
                userNameComboBox.setVisible(false);
            }
        });
        homeButton.setOnAction(e -> {
            settingsAnchorPane.setVisible(false);
            mainPane.setVisible(true);
            if (ClassHandler.userInfoController().getUserBooleanSetting(Variables.currentUser, StringDB.COLUMN_SHOWUSERNAME)) {
                userNameComboBox.setVisible(true);
            }
        });
        new MoveStage(topBarRectangle, null, true);
        new MoveStage(userNameComboBox, null, false);
        // Shows an indicator when its rechecking the shows.
        isCurrentlyRecheckingTooltip.textProperty().bind(Strings.CurrentlyRechecking);

        new Thread(new Task<Void>() {
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
        }).start();

        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                boolean setTransparent = false;
                while (Main.programRunning) {
                    if (!setTransparent && Variables.haveStageBlockParentStage && isShowCurrentlyPlaying() && !pane.isMouseTransparent()) {
                        pane.setMouseTransparent(true);
                        homeButton.fire();
                        setTransparent = true;
                    } else if (setTransparent && (!Variables.haveStageBlockParentStage || !isShowCurrentlyPlaying()) && pane.isMouseTransparent()) {
                        pane.setMouseTransparent(false);
                        setTransparent = false;
                    }
                    Thread.sleep(200);
                }
                return null;
            }
        }).start();

        menuButtonImage.setMouseTransparent(true);
        tableView.prefHeightProperty().bind(tableViewAnchorPane.heightProperty());
        menuButton.setFocusTraversable(false);
        menuExpanded = false;
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(menuButtonImage);
        rotateTransition.setDuration(new Duration(280));
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(180);
        menuButton.setOnAction(e -> {
            if (tableViewAnchorPane.isVisible()) {
                if (Variables.specialEffects) {
                    new Thread(new Task<Void>() {
                        @Override
                        protected Void call() throws InterruptedException {
                            if (menuChanging) {
                                stopMenuChanging = true;
                                while (stopMenuChanging) Thread.sleep(10);
                            }
                            menuChanging = true;
                            if (!menuExpanded) buttonMenuVisibility(true);
                            rotateTransition.setFromAngle(menuButtonImage.getRotate());
                            rotateTransition.setToAngle(!menuExpanded ? 180 : 0);
                            rotateTransition.playFromStart();
                            while (!stopMenuChanging && ((menuExpanded && tableViewAnchorPane.getLayoutY() != 0) || (!menuExpanded && tableViewAnchorPane.getLayoutY() != 28))) {
                                tableViewAnchorPane.setLayoutY(tableViewAnchorPane.getLayoutY() + (menuExpanded ? -1.0 : +1.0));
                                tableViewAnchorPane.setPrefHeight(pane.getHeight() - tableViewAnchorPane.getLayoutY() - 30.0);
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e1) {
                                    GenericMethods.printStackTrace(log, e1, this.getClass());
                                }
                            }
                            menuChanging = false;
                            if (stopMenuChanging) stopMenuChanging = false;
                            else if (menuExpanded) buttonMenuVisibility(false);
                            menuExpanded = !menuExpanded;
                            return null;
                        }
                    }).start();
                } else {
                    tableViewAnchorPane.setLayoutY(menuExpanded ? 0 : 28);
                    tableViewAnchorPane.setPrefHeight(pane.getHeight() - tableViewAnchorPane.getLayoutY() - 30);
                    buttonMenuVisibility(!menuChanging);
                    menuExpanded = !menuExpanded;
                    menuButtonImage.setRotate(menuExpanded ? 180 : 0);
                }
            }
        });
    }

    private void buttonMenuVisibility(final boolean buttonVisible) {
        textField.setVisible(buttonVisible);
        changeTableViewButton.setVisible(buttonVisible);
        settingsButton.setVisible(buttonVisible);
    }

    public void setTableSelection(final int row) {
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

    private void playShow(final DisplayShow displayShow) {
        showCurrentlyPlaying = displayShow;
        userNameComboBox.setDisable(true);
        currentUserComboBox.setDisable(true);
        try {
            showPlayingBox.showConfirm(displayShow, (Stage) pane.getScene().getWindow());
        } catch (IOException e1) {
            GenericMethods.printStackTrace(log, e1, Controller.class);
        }
        userNameComboBox.setDisable(false);
        currentUserComboBox.setDisable(false);
        showCurrentlyPlaying = null;
    }

    private boolean isNumberValid(final String textFieldValue, final int minValue) {
        log.finest("isNumberValid has been called.");
        if (textFieldValue.isEmpty() || !textFieldValue.matches("^[0-9]+$") || Integer.parseInt(textFieldValue) > Variables.maxWaitTimeSeconds || Integer.parseInt(textFieldValue) < minValue) {
            new MessageBox(new StringProperty[]{new SimpleStringProperty(Strings.MustBeANumberBetween.getValue() + minValue + " - " + Variables.maxWaitTimeSeconds)}, (Stage) this.tabPane.getScene().getWindow());
            return false;
        } else return true;
    }

    public void toggleUsernameUsability(final boolean disable) {
        setButtonDisable(disable, deleteUser, addUser, currentUserComboBox, setDefaultUsername);
    }

    private void setButtonDisable(final boolean isDisable, final Region... regions) {
        for (Region aRegion : regions) {
            if (isShowCurrentlyPlaying() && aRegion == currentUserComboBox) return;
            aRegion.setDisable(isDisable);
        }
    }

    // This is the list that is currently showing in the tableView.
    private enum currentList {
        ACTIVE, INACTIVE;

        private final static BooleanProperty isActive = new SimpleBooleanProperty(false);
        private final static BooleanProperty isInactive = new SimpleBooleanProperty(false);
        private static currentList currentStatus;

        public static currentList getStatus() {
            return currentStatus;
        }

        public static void setStatus(final currentList status) {
            currentStatus = status;
            isActive.setValue(currentStatus == ACTIVE);
            isInactive.setValue(currentStatus == INACTIVE);
        }

        public static boolean isActive() {
            return isActive.getValue();
        }

        public static boolean isInactive() {
            return isInactive.getValue();
        }

        public static BooleanProperty isInactiveProperty() {
            return isInactive;
        }
    }
}
