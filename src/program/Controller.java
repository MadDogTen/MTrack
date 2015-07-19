package program;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import program.gui.*;
import program.information.ChangeReporter;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.information.show.DisplayShows;
import program.io.CheckShowFiles;
import program.io.FileManager;
import program.io.MoveWindow;
import program.util.Strings;
import program.util.Variables;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class Controller implements Initializable {
    private static final Logger log = Logger.getLogger(Controller.class.getName());
    private static ProgramSettingsController programSettingsController;
    private static ShowInfoController showInfoController;
    private static UserInfoController userInfoController;
    private static CheckShowFiles checkShowFiles;
    private static Controller controller;
    // This is the list that is currently showing in the tableView. Currently can only be "active" or "inactive".
    private static String currentList = "active";
    private static ObservableList<DisplayShows> tableViewFields;
    // show0Remaining - If this true, It will display shows that have 0 episodes remaining, and if false, hides them. Only works with the active list.
    // wereShowsChanged - This is set the true if you set a show active while in the inactive list. If this is true when you switch back to the active this, it will start a recheck. This is because the show may be highly outdated as inactive shows aren't updated.
    // isShowCurrentlyPlaying - While a show is currently playing, this is true, otherwise it is false. This is used in mainRun to make rechecking take 10x longer to happen when a show is playing.
    private static boolean show0Remaining, wereShowsChanged, isShowCurrentlyPlaying;
    private ChangesBox changesBox;
    @FXML
    private Pane pane;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab showsTab;
    @FXML
    private Tab settingsTab;
    @FXML
    private Button exit;
    @FXML
    private Button minimize;
    @SuppressWarnings("CanBeFinal")
    @FXML
    private TableView<DisplayShows> tableView = new TableView<>();
    @FXML
    private TableColumn<DisplayShows, String> shows;
    @FXML
    private TableColumn<DisplayShows, Integer> remaining;
    @FXML
    private TableColumn<DisplayShows, Integer> season;
    @FXML
    private TableColumn<DisplayShows, Integer> episode;
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
    private Circle pingingDirectory;

    // This will set the ObservableList using the showList provided. For the active list, if show0Remaining is false, then it skips adding those, otherwise all shows are added. For the inactive list, all shows are added.
    private static ObservableList<DisplayShows> MakeTableViewFields(ArrayList<String> showList) {
        ObservableList<DisplayShows> list = FXCollections.observableArrayList();
        if (!showList.isEmpty()) {
            if (currentList.matches("active") && !show0Remaining) {
                showList.forEach(aShow -> {
                    int remaining = userInfoController.getRemainingNumberOfEpisodes(aShow, showInfoController);
                    if (remaining != 0) {
                        list.add(new DisplayShows(aShow, remaining, userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow)));
                    }
                });
            } else
                list.addAll(showList.stream().map(aShow -> new DisplayShows(aShow, userInfoController.getRemainingNumberOfEpisodes(aShow, showInfoController), userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow))).collect(Collectors.toList()));
        }
        return list;
    }

    // This sets the current tableView to whichever you want.
    public static void setTableViewFields(String type) {
        if (type.matches("active")) {
            if (!currentList.matches("active")) {
                currentList = "active";
            }
            tableViewFields = MakeTableViewFields(userInfoController.getActiveShows());
        } else if (type.matches("inactive")) {
            if (!currentList.matches("inactive")) {
                currentList = "inactive";
            }
            tableViewFields = MakeTableViewFields(userInfoController.getInactiveShows());
        }
    }

    // Public method to update a particular show with new information. If the show happens to have been remove, then showExists will be false, which isShowActive will be false, which makes remove true, which meaning it won't add the show back to the list.
    public static void updateShowField(String aShow, boolean showExists) {
        int index = -2;
        for (DisplayShows test : tableViewFields) {
            if (test.getShow().replaceAll("[(]|[)]", "").matches(aShow.replaceAll("[(]|[)]", ""))) {
                index = tableViewFields.indexOf(test);
                break;
            }
        }
        boolean isShowActive = (userInfoController.isShowActive(aShow) && showExists), remove = false;
        if (currentList.contains("active") && !isShowActive || currentList.contains("inactive") && isShowActive) {
            remove = true;
        }
        if (index != -2) {
            removeShowField(index);
        }
        if (!remove) {
            int remaining = userInfoController.getRemainingNumberOfEpisodes(aShow, showInfoController), season = userInfoController.getCurrentSeason(aShow), episode = userInfoController.getCurrentEpisode(aShow);
            if ((show0Remaining || remaining != 0) && index != -2) {
                tableViewFields.add(index, new DisplayShows(aShow, remaining, season, episode));
            } else if (index == -2 && show0Remaining || index == -2 && remaining != 0) {
                tableViewFields.add(new DisplayShows(aShow, remaining, season, episode));
            }
        }
    }

    // Used to remove the show from the current list. If you are on the active list and set a show inactive, then it will remove it; and the same if you make a show active on the inactive list.
    private static void removeShowField(int index) {
        tableViewFields.remove(index);
    }

    public static boolean getIsShowCurrentlyPlaying() {
        return isShowCurrentlyPlaying;
    }

    public static ProgramSettingsController getProgramSettingsController() {
        return programSettingsController;
    }

    public static ShowInfoController getShowInfoController() {
        return showInfoController;
    }

    public static UserInfoController getUserInfoController() {
        return userInfoController;
    }

    public static CheckShowFiles getCheckShowFiles() {
        return checkShowFiles;
    }

    public static double getShowColumnWidth() {
        return controller.shows.getWidth();
    }

    public static double getRemainingColumnWidth() {
        return controller.remaining.getWidth();
    }

    public static double getSeasonColumnWidth() {
        return controller.season.getWidth();
    }

    public static double getEpisodeColumnWidth() {
        return controller.episode.getWidth();
    }

    public static boolean getShowColumnVisibility() {
        return controller.shows.isVisible();
    }

    public static boolean getRemainingColumnVisibility() {
        return controller.remaining.isVisible();
    }

    public static boolean getSeasonColumnVisibility() {
        return controller.season.isVisible();
    }

    public static boolean getEpisodeColumnVisibility() {
        return controller.episode.isVisible();
    }

    public static boolean isChangeBoxStageOpen() {
        return controller.changesBox != null && controller.changesBox.getStage() != null;
    }

    public static void closeChangeBoxStage() {
        controller.changesBox.getStage().close();
    }

    // This first Filters the observableList if you have anything in the searchList, Then enables or disables the show0RemainingCheckbox depending on which list it is currently on.
    private void setTableView() {
        FilteredList<DisplayShows> newFilteredData = new FilteredList<>(tableViewFields, p -> true);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            newFilteredData.setPredicate(show -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        return show.getShow().toLowerCase().contains(lowerCaseFilter);
                    }
            );
        });
        SortedList<DisplayShows> newSortedData = new SortedList<>(newFilteredData);
        newSortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(newSortedData);

        if (currentList.matches("active")) {
            show0RemainingCheckBox.setDisable(false);
        } else if (currentList.matches("inactive")) {
            show0RemainingCheckBox.setDisable(true);
        }
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        log.info("MainController Running...");
        setController();
        programSettingsController = Main.getProgramSettingsController();
        showInfoController = Main.getShowInfoController();
        userInfoController = Main.getUserInfoController();
        checkShowFiles = Main.getCheckShowFiles();
        pane.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT);
        tabPane.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT);
        tableView.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT - 69);
        showsTab.setText(Strings.Shows);
        settingsTab.setText(Strings.Settings);
        show0Remaining = programSettingsController.getShow0Remaining();
        shows.setCellValueFactory(new PropertyValueFactory<>("show"));
        shows.setSortType(TableColumn.SortType.ASCENDING);
        shows.setText(Strings.Shows);
        shows.setPrefWidth(programSettingsController.getSettingsFile().getShowColumnWidth());
        shows.setVisible(programSettingsController.getSettingsFile().isShowColumnVisibility());
        remaining.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        remaining.setText(Strings.Left);
        remaining.setPrefWidth(programSettingsController.getSettingsFile().getRemainingColumnWidth());
        remaining.setVisible(programSettingsController.getSettingsFile().isRemainingColumnVisibility());
        season.setCellValueFactory(new PropertyValueFactory<>("season"));
        season.setText(Strings.Season);
        season.setPrefWidth(programSettingsController.getSettingsFile().getSeasonColumnWidth());
        season.setVisible(programSettingsController.getSettingsFile().isSeasonColumnVisibility());
        episode.setCellValueFactory(new PropertyValueFactory<>("episode"));
        episode.setText(Strings.Episode);
        episode.setPrefWidth(programSettingsController.getSettingsFile().getEpisodeColumnWidth());
        episode.setVisible(programSettingsController.getSettingsFile().isEpisodeColumnVisibility());
        Controller.setTableViewFields("active");
        setTableView();
        tableView.getItems();

        tableView.getSortOrder().add(shows);

        tableView.setRowFactory(
                param -> {
                    final TableRow<DisplayShows> row = new TableRow<>();
                    final ContextMenu rowMenuActive = new ContextMenu();
                    final ContextMenu rowMenuInactive = new ContextMenu();

                    MenuItem setSeasonEpisode = new MenuItem(Strings.SetSeasonEpisode);
                    setSeasonEpisode.setOnAction(e -> {
                        log.info("\"Set Season + Episode\" is now running...");
                        String show = row.getItem().getShow();
                        int[] seasonEpisode = new ListSelectBox().pickSeasonEpisode(Strings.PickTheSeason, show, showInfoController.getSeasonsList(show), showInfoController, pane.getScene().getWindow());
                        if (seasonEpisode[0] != -1 && seasonEpisode[1] != -1) {
                            log.info("Season & Episode were valid.");
                            userInfoController.setSeasonEpisode(show, seasonEpisode[0], seasonEpisode[1]);
                            updateShowField(row.getItem().getShow(), true);
                            tableView.getSelectionModel().clearSelection();
                        } else {
                            log.info("Season & Episode weren't valid.");
                        }
                        log.info("\"Set Season + Episode\" is finished running.");
                    });
                    MenuItem playSeasonEpisode = new MenuItem(Strings.PlaySeasonEpisode);
                    playSeasonEpisode.setOnAction(e -> {
                        log.info("\"Play Season + Episode\" is now running...");
                        String show = row.getItem().getShow();
                        int[] seasonEpisode = new ListSelectBox().pickSeasonEpisode(Strings.PickTheSeason, show, showInfoController.getSeasonsList(show), showInfoController, pane.getScene().getWindow());
                        if (seasonEpisode[0] != -1 && seasonEpisode[1] != -1) {
                            log.info("Season & Episode were valid.");
                            userInfoController.playAnyEpisode(show, seasonEpisode[0], seasonEpisode[1]);
                        } else {
                            log.info("Season & Episode weren't valid.");
                        }
                        log.info("\"Play Season + Episode\" is finished running.");
                    });
                    MenuItem toggleActive = new MenuItem();
                    toggleActive.setOnAction(e -> {
                        if (currentList.matches("inactive")) {
                            userInfoController.setActiveStatus(row.getItem().getShow(), true);
                            if (!wereShowsChanged) {
                                wereShowsChanged = true;
                            }
                            removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            tableView.getSelectionModel().clearSelection();
                        } else if (currentList.matches("active")) {
                            userInfoController.setActiveStatus(row.getItem().getShow(), false);
                            removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            tableView.getSelectionModel().clearSelection();
                        }
                    });
                    MenuItem setHidden = new MenuItem(Strings.HideShow);
                    setHidden.setOnAction(e -> {
                        userInfoController.setHiddenStatus(row.getItem().getShow(), true);
                        removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                        tableView.getSelectionModel().clearSelection();
                    });
                    MenuItem resetShow = new MenuItem(Strings.ResetTo);
                    resetShow.setOnAction(e -> {
                        log.info("Reset to running...");
                        String[] choices = {Strings.Beginning, Strings.End};
                        String answer = new SelectBox().display(Strings.WhatShould + row.getItem().getShow() + Strings.BeResetTo, choices, pane.getScene().getWindow());
                        log.info(answer);
                        if (answer.matches(Strings.Beginning)) {
                            userInfoController.setToBeginning(row.getItem().getShow());
                            updateShowField(row.getItem().getShow(), true);
                            tableView.getSelectionModel().clearSelection();
                            log.info(Strings.ShowIsResetToThe + ' ' + Strings.Beginning.toLowerCase() + '.');
                        } else if (answer.matches(Strings.End)) {
                            userInfoController.setToEnd(row.getItem().getShow());
                            updateShowField(row.getItem().getShow(), true);
                            tableView.getSelectionModel().clearSelection();
                            log.info(Strings.ShowIsResetToThe + ' ' + Strings.End.toLowerCase() + '.');
                        }
                        log.info("Reset to finished running.");
                    });
                    MenuItem openDirectory = new MenuItem(Strings.OpenFileLocation);
                    openDirectory.setOnAction(e -> {
                        log.info("Started to open show directory...");
                        ArrayList<String> directories = programSettingsController.getDirectories();
                        ArrayList<File> folders = new ArrayList<>();
                        FileManager fileManager = new FileManager();
                        directories.forEach(aDirectory -> {
                            String fileString = (aDirectory + Strings.FileSeparator + row.getItem().getShow());
                            if (fileManager.checkFolderExists(new File(fileString))) {
                                folders.add(new File(fileString));
                            }
                        });
                        if (folders.size() == 1) {
                            fileManager.open(folders.get(0));
                        } else {
                            ConfirmBox confirmBox = new ConfirmBox();
                            boolean openAll = confirmBox.display(Strings.DoYouWantToOpenAllAssociatedFolders, pane.getScene().getWindow());
                            if (openAll) {
                                folders.forEach(fileManager::open);
                            } else {
                                ListSelectBox listSelectBox = new ListSelectBox();
                                File file = listSelectBox.directories(Strings.PickTheFolderYouWantToOpen, folders, pane.getScene().getWindow());
                                if (!file.toString().isEmpty()) {
                                    fileManager.open(file);
                                }
                            }
                        }
                        log.info("Finished opening show directory...");
                    });
                    MenuItem getRemaining = new MenuItem(Strings.GetRemaining);
                    getRemaining.setOnAction(e -> log.info("There are " + userInfoController.getRemainingNumberOfEpisodes(row.getItem().getShow(), showInfoController) + " episode(s) remaining."));
                    MenuItem playPreviousEpisode = new MenuItem(Strings.PlayPreviousEpisode);
                    playPreviousEpisode.setOnAction(e -> {
                        log.info("Attempting to play previous episode...");
                        int[] seasonEpisode = userInfoController.getPreviousEpisodeIfExists(row.getItem().getShow());
                        if (seasonEpisode[0] == -2 || seasonEpisode[0] == -3) {
                            new MessageBox().display(Strings.NoDirectlyPrecedingEpisodesFound, tabPane.getScene().getWindow());
                        } else {
                            userInfoController.playAnyEpisode(row.getItem().getShow(), seasonEpisode[0], seasonEpisode[1]);
                        }
                        log.info("Finished attempting to play previous episode.");
                    });

                    row.setOnMouseClicked(e -> {
                        if (e.getButton().equals(MouseButton.SECONDARY) && (!row.isEmpty())) {
                            if (currentList.matches("active")) {
                                toggleActive.setText(Strings.SetInactive);
                                if (Variables.devMode) {
                                    rowMenuActive.getItems().addAll(setSeasonEpisode, playSeasonEpisode, playPreviousEpisode, resetShow, toggleActive, getRemaining, openDirectory);
                                } else
                                    rowMenuActive.getItems().addAll(setSeasonEpisode, playSeasonEpisode, playPreviousEpisode, resetShow, toggleActive, openDirectory);
                                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenuActive).otherwise((ContextMenu) null));
                            } else if (currentList.matches("inactive")) {
                                toggleActive.setText(Strings.SetActive);
                                if (Variables.devMode) {
                                    rowMenuInactive.getItems().addAll(toggleActive, setHidden, getRemaining, openDirectory);
                                } else rowMenuInactive.getItems().addAll(toggleActive, setHidden, openDirectory);
                                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenuInactive).otherwise((ContextMenu) null));
                            }
                        }
                        if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2 && (!row.isEmpty()) && currentList.matches("active")) {
                            String aShow = row.getItem().getShow();
                            boolean keepPlaying = true;
                            isShowCurrentlyPlaying = true;
                            while (keepPlaying) {
                                Boolean fileExists = userInfoController.doesEpisodeExists(aShow);
                                if (fileExists) {
                                    userInfoController.playAnyEpisode(aShow, userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow));
                                    ShowConfirmBox showConfirmBox = new ShowConfirmBox();
                                    int userChoice = showConfirmBox.display(Strings.HaveYouWatchedTheShow, pane.getScene().getWindow());
                                    if (userChoice == 1) {
                                        userInfoController.changeEpisode(aShow, -2, true);
                                        updateShowField(aShow, true);
                                        tableView.getSelectionModel().clearSelection();
                                        keepPlaying = false;
                                    } else if (userChoice == 2) {
                                        userInfoController.changeEpisode(aShow, -2, true);
                                        updateShowField(aShow, true);
                                        tableView.getSelectionModel().clearSelection();
                                    } else if (userChoice == 0) {
                                        tableView.getSelectionModel().clearSelection();
                                        keepPlaying = false;
                                    }
                                } else break;
                            }
                            if (keepPlaying) {
                                userInfoController.changeEpisode(aShow, -2, false);
                                MessageBox messageBox = new MessageBox();
                                messageBox.display(Strings.YouHaveReachedTheEnd, pane.getScene().getWindow());
                            }
                            isShowCurrentlyPlaying = false;
                        }
                    });
                    return row;
                }
        );

        // ~~~~ Buttons ~~~~ \\
        exit.setOnAction(e -> Main.stop(Main.stage, false, true));
        minimize.setOnAction(e -> Main.stage.setIconified(true));

        menuButton.setText(Strings.Options);
        changeTableView.setOnAction(event -> {
            if (currentList.matches("active")) {
                setTableViewFields("inactive");
                log.info("TableViewFields set to inactive.");
            } else if (currentList.matches("inactive")) {
                if (wereShowsChanged) {
                    Task<Void> task = new Task<Void>() {
                        @SuppressWarnings("ReturnOfNull")
                        @Override
                        protected Void call() throws Exception {
                            checkShowFiles.recheckShowFile(true);
                            return null;
                        }
                    };
                    new Thread(task).start();
                }
                setTableViewFields("active");
                log.info("TableViewFields set to active.");
            }
            setTableView();
        });
        changeTableView.setText(Strings.SwitchBetweenActiveInactiveList);

        viewChanges.setText(Strings.OpenChangesWindow);
        viewChanges.setOnAction(e -> openChangeBox());
        changesAlert.setOnAction(e -> openChangeBox());
        changesAlert.setStyle(
                "-fx-background-radius: 5em; " +
                        "-fx-min-width: 20px; " +
                        "-fx-min-height: 20px; " +
                        "-fx-max-width: 20px; " +
                        "-fx-max-height: 20px; " +
                        "-fx-background-color: -fx-body-color;" +
                        "-fx-background-insets: 0px; " +
                        "-fx-padding: 0px;"
        );

        show0RemainingCheckBox.setSelected(show0Remaining);
        if (show0Remaining) {
            setTableViewFields(currentList);
            setTableView();
        }
        show0RemainingCheckBox.setOnAction(e -> {
            show0Remaining = show0RemainingCheckBox.isSelected();
            programSettingsController.setShow0Remaining(show0Remaining);
            if (show0Remaining && currentList.matches("active")) {
                log.info("Now showing shows with 0 episodes remaining.");
            } else if (currentList.matches("active")) {
                log.info("No longer showing shows with 0 episodes remaining.");
            }
            setTableViewFields(currentList);
            setTableView();
        });
        show0RemainingCheckBox.setTooltip(new Tooltip(Strings.ShowHideShowsWith0EpisodeLeft));
        Tooltip.install(
                pingingDirectory,
                new Tooltip(Strings.PingingDirectories));

        // || ~~~~ Settings Tab ~~~~ || \\
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        settingsTab.setOnSelectionChanged(e -> {
            selectionModel.select(showsTab);
            try {
                new SettingsWindow().display();
            } catch (Exception e1) {
                log.info(Arrays.toString(e1.getStackTrace()));
            }
        });

        // Allow the undecorated stage to be moved.
        new MoveWindow().moveWindow(tabPane, null);

        // Shows an indicator when its rechecking the shows.
        isCurrentlyRechecking.setTooltip(new Tooltip(Strings.CurrentlyRechecking));

        final boolean[] lowerOpacity = {false};
        final double[] currentOpacity = {1};
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (Main.programRunning) {
                    boolean currentlyRechecking = isCurrentlyRechecking();
                    if (currentlyRechecking) {
                        if (checkShowFiles.isCurrentlyCheckingDirectories()) {
                            if (!pingingDirectory.isVisible()) {
                                pingingDirectory.setVisible(true);
                            }
                            pingingDirectory.setOpacity(currentOpacity[0]);
                            if (currentOpacity[0] >= 1.25) {
                                lowerOpacity[0] = true;
                            } else if (currentOpacity[0] <= 0.30) {
                                lowerOpacity[0] = false;
                            }
                            if (lowerOpacity[0]) {
                                currentOpacity[0] -= 0.03;
                            } else {
                                currentOpacity[0] += 0.03;
                            }
                        } else if (pingingDirectory.isVisible()) {
                            pingingDirectory.setVisible(false);
                        }

                        Platform.runLater(() -> {
                            Double temp = checkShowFiles.getRecheckShowFilePercentage();
                            isCurrentlyRechecking.setProgress(temp);
                        });
                    }
                    isChangesListPopulated();
                    if (currentlyRechecking) {
                        Thread.sleep(80);
                    } else {
                        Thread.sleep(800);
                    }
                }
                //noinspection ReturnOfNull
                return null;
            }
        };
        new Thread(task).start();
    }

    private void openChangeBox() {
        if (changesBox == null || changesBox.getStage() == null) {
            if (changesBox == null) {
                changesBox = new ChangesBox();
            }
            boolean keepOpen;
            Object[] answer = null;
            do {
                Stage neededWindow = (Stage) pane.getScene().getWindow();
                if (answer != null && answer[1] != null) {
                    neededWindow = (Stage) answer[1];
                }
                answer = changesBox.display(neededWindow, ChangeReporter.getChanges());
                keepOpen = (boolean) answer[0];
            } while (keepOpen);
            changesBox = null;
        } else {
            changesBox.display(pane.getScene().getWindow(), ChangeReporter.getChanges());
        }
    }

    private boolean isCurrentlyRechecking() {
        if (checkShowFiles.getRecheckShowFileRunning()) {
            if (!isCurrentlyRechecking.isVisible()) {
                isCurrentlyRechecking.setVisible(true);
            }
            return true;
        } else if (isCurrentlyRechecking.isVisible()) {
            isCurrentlyRechecking.setVisible(false);
        }
        return false;
    }

    private void isChangesListPopulated() {
        if (ChangeReporter.getIsChanges()) {
            changesAlert.setVisible(true);
        } else if (changesAlert.isVisible()) {
            changesAlert.setVisible(false);
        }
    }

    private void setController() {
        controller = this;
    }
}