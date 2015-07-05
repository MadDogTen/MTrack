package program;

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
import javafx.stage.Stage;
import program.gui.*;
import program.information.*;
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

    private static String currentList = "active";
    private static ObservableList<DisplayShows> tableViewFields;
    private static boolean show0Remaining, wereShowsChanged, isShowCurrentlyPlaying;
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

    private static ObservableList<DisplayShows> MakeTableViewFields(ArrayList<String> showList) {
        ObservableList<DisplayShows> list = FXCollections.observableArrayList();
        if (!showList.isEmpty()) {
            if (currentList.matches("active") && !show0Remaining) {
                showList.forEach(aShow -> {
                    int remaining = UserInfoController.getRemainingNumberOfEpisodes(aShow);
                    if (remaining != 0) {
                        list.add(new DisplayShows(aShow, remaining));
                    }
                });
            } else
                list.addAll(showList.stream().map(aShow -> new DisplayShows(aShow, UserInfoController.getRemainingNumberOfEpisodes(aShow))).collect(Collectors.toList()));
        }
        return list;
    }

    public static void setTableViewFields(String type) {
        if (type.matches("active")) {
            if (!currentList.matches("active")) {
                currentList = "active";
            }
            tableViewFields = MakeTableViewFields(UserInfoController.getActiveShows());
        } else if (type.matches("inactive")) {
            if (!currentList.matches("inactive")) {
                currentList = "inactive";
            }
            tableViewFields = MakeTableViewFields(UserInfoController.getInactiveShows());
        }
    }

    public static void updateShowField(String aShow, Boolean showExists) {
        int index = -2;
        for (DisplayShows test : tableViewFields) {
            if (test.getShow().replaceAll("[(]|[)]", "").matches(aShow.replaceAll("[(]|[)]", ""))) {
                index = tableViewFields.indexOf(test);
                break;
            }
        }
        Boolean isShowActive = (UserInfoController.isShowActive(aShow) && showExists), remove = false;
        if (currentList.contains("active") && !isShowActive || currentList.contains("inactive") && isShowActive) {
            remove = true;
        }
        if (index != -2) {
            removeShowField(index);
        }
        if (!remove) {
            int remaining = UserInfoController.getRemainingNumberOfEpisodes(aShow);
            if ((show0Remaining || remaining != 0) && index != -2) {
                tableViewFields.add(index, new DisplayShows(aShow, remaining));
            } else if (index == -2 && show0Remaining || index == -2 && remaining != 0) {
                tableViewFields.add(new DisplayShows(aShow, remaining));
            }
        }
    }

    private static void removeShowField(int index) {
        tableViewFields.remove(index);
    }

    public static boolean getIsShowCurrentlyPlaying() {
        return isShowCurrentlyPlaying;
    }

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
        pane.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT);
        tableView.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT - 69);
        MainRun.startBackend();
        showsTab.setText(Strings.Shows);
        settingsTab.setText(Strings.Settings);
        show0Remaining = ProgramSettingsController.getShow0Remaining();
        shows.setCellValueFactory(new PropertyValueFactory<>("show"));
        shows.setSortType(TableColumn.SortType.ASCENDING);
        shows.setText(Strings.Shows);
        remaining.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        remaining.setText(Strings.Left);
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
                        String[] seasonEpisode = new ListSelectBox().pickSeasonEpisode("Pick the Season", show, ShowInfoController.getSeasonsList(show), pane.getScene().getWindow());
                        if (!seasonEpisode[0].contains("-1") && !seasonEpisode[1].contains("-1")) {
                            log.info("Season & Episode were valid.");
                            UserInfoController.setSeasonEpisode(show, Integer.parseInt(seasonEpisode[0]), seasonEpisode[1]);
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
                        String[] seasonEpisode = new ListSelectBox().pickSeasonEpisode("Pick the Season", show, ShowInfoController.getSeasonsList(show), pane.getScene().getWindow());
                        if (!seasonEpisode[0].contains("-1") && !seasonEpisode[1].contains("-1")) {
                            log.info("Season & Episode were valid.");
                            UserInfoController.playAnyEpisode(show, Integer.parseInt(seasonEpisode[0]), seasonEpisode[1]);
                        } else {
                            log.info("Season & Episode weren't valid.");
                        }
                        log.info("\"Play Season + Episode\" is finished running.");
                    });
                    MenuItem toggleActive = new MenuItem();
                    toggleActive.setOnAction(e -> {
                        if (currentList.matches("inactive")) {
                            UserInfoController.setActiveStatus(row.getItem().getShow(), true);
                            if (!wereShowsChanged) {
                                wereShowsChanged = true;
                            }
                            removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            tableView.getSelectionModel().clearSelection();
                        } else if (currentList.matches("active")) {
                            UserInfoController.setActiveStatus(row.getItem().getShow(), false);
                            removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            tableView.getSelectionModel().clearSelection();
                        }
                    });
                    MenuItem setHidden = new MenuItem(Strings.HideShow);
                    setHidden.setOnAction(e -> {
                        UserInfoController.setHiddenStatus(row.getItem().getShow(), true);
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
                            UserInfoController.setToBeginning(row.getItem().getShow());
                            updateShowField(row.getItem().getShow(), true);
                            tableView.getSelectionModel().clearSelection();
                            log.info(Strings.ShowIsResetToThe + ' ' + Strings.Beginning.toLowerCase() + '.');
                        } else if (answer.matches(Strings.End)) {
                            UserInfoController.setToEnd(row.getItem().getShow());
                            updateShowField(row.getItem().getShow(), true);
                            tableView.getSelectionModel().clearSelection();
                            log.info(Strings.ShowIsResetToThe + ' ' + Strings.End.toLowerCase() + '.');
                        }
                        log.info("Reset to finished running.");
                    });
                    MenuItem openDirectory = new MenuItem(Strings.OpenFileLocation);
                    openDirectory.setOnAction(e -> {
                        log.info("Started to open show directory...");
                        ArrayList<String> directories = ProgramSettingsController.getDirectories();
                        ArrayList<File> folders = new ArrayList<>();
                        FileManager fileManager = new FileManager();
                        directories.forEach(aDirectory -> {
                            String fileString = (aDirectory + Strings.FileSeparator + row.getItem().getShow());
                            if (fileManager.checkFolderExists(fileString)) {
                                folders.add(new File(fileString));
                            }
                        });
                        if (folders.size() == 1) {
                            fileManager.open(folders.get(0));
                        } else {
                            ConfirmBox confirmBox = new ConfirmBox();
                            Boolean openAll = confirmBox.display(Strings.DoYouWantToOpenAllAssociatedFolders, pane.getScene().getWindow());
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
                    getRemaining.setOnAction(e -> log.info("Controller - There are " + UserInfoController.getRemainingNumberOfEpisodes(row.getItem().getShow()) + " episode(s) remaining."));
                    MenuItem playPreviousEpisode = new MenuItem(Strings.PlayPreviousEpisode);
                    playPreviousEpisode.setOnAction(e -> {
                        log.info("Attempting to play previous episode...");
                        String[] seasonEpisode = UserInfoController.getPreviousEpisodeIfExists(row.getItem().getShow());
                        if (seasonEpisode[0].contains("-2") || seasonEpisode[0].contains("-3")) {
                            new MessageBox().display(Strings.NoDirectlyPrecedingEpisodesFound, tabPane.getScene().getWindow());
                        } else {
                            UserInfoController.playAnyEpisode(row.getItem().getShow(), Integer.parseInt(seasonEpisode[0]), String.valueOf(seasonEpisode[1]));
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
                            Boolean keepPlaying = true;
                            isShowCurrentlyPlaying = true;
                            while (keepPlaying) {
                                int fileExists = UserInfoController.doesEpisodeExists(aShow);
                                if (fileExists == 1 || fileExists == 2) {
                                    UserInfoController.playAnyEpisode(aShow, UserInfoController.getCurrentSeason(aShow), UserInfoController.getCurrentEpisode(aShow));
                                    ShowConfirmBox showConfirmBox = new ShowConfirmBox();
                                    int userChoice = showConfirmBox.display(Strings.HaveYouWatchedTheShow, pane.getScene().getWindow());
                                    if (userChoice == 1) {
                                        UserInfoController.changeEpisode(aShow, -2, true);
                                        updateShowField(aShow, true);
                                        tableView.getSelectionModel().clearSelection();
                                        keepPlaying = false;
                                    } else if (userChoice == 2) {
                                        UserInfoController.changeEpisode(aShow, -2, true);
                                        updateShowField(aShow, true);
                                        tableView.getSelectionModel().clearSelection();
                                    } else if (userChoice == 0) {
                                        tableView.getSelectionModel().clearSelection();
                                        keepPlaying = false;
                                    }
                                } else break;
                            }
                            if (keepPlaying) {
                                UserInfoController.changeEpisode(aShow, -2, false);
                                MessageBox messageBox = new MessageBox();
                                messageBox.display("You have reached the end!", pane.getScene().getWindow());
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
                            new CheckShowFiles().recheckShowFile(true);
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
            ProgramSettingsController.setShow0Remaining(show0Remaining);
            if (show0Remaining && currentList.matches("active")) {
                log.info("Now showing shows with 0 episodes remaining.");
            } else if (currentList.matches("active")) {
                log.info("No longer showing shows with 0 episodes remaining.");
            }
            setTableViewFields(currentList);
            setTableView();
        });
        show0RemainingCheckBox.setTooltip(new Tooltip(Strings.ShowHideShowsWith0EpisodeLeft));

        // || ~~~~ Settings Tab ~~~~ || \\
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        settingsTab.setOnSelectionChanged(e -> {
            selectionModel.select(showsTab);
            try {
                new SettingsWindow().display();
            } catch (Exception e1) {
                log.severe(Arrays.toString(e1.getStackTrace()));
            }
        });

        // Allow the undecorated stage to be moved.
        new MoveWindow().moveWindow(tabPane, null);

        // Shows an indicator when its rechecking the shows.
        isCurrentlyRechecking.setTooltip(new Tooltip(Strings.CurrentlyRechecking));
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (Main.programRunning) {
                    isCurrentlyRechecking();
                    isChangesListPopulated();
                    Thread.sleep(800);
                }
                //noinspection ReturnOfNull
                return null;
            }
        };
        new Thread(task).start();
    }

    private void openChangeBox() {
        if (ChangesBox.getStage() == null) {
            Boolean keepOpen;
            Object[] answer = null;
            do {
                Stage neededWindow = (Stage) pane.getScene().getWindow();
                if (answer != null && answer[1] != null) {
                    neededWindow = (Stage) answer[1];
                }
                answer = new ChangesBox().display(neededWindow);
                keepOpen = (Boolean) answer[0];
            } while (keepOpen);
        } else {
            new ChangesBox().display(pane.getScene().getWindow());
        }
    }

    private void isCurrentlyRechecking() {
        if (CheckShowFiles.getRecheckShowFileRunning()) {
            if (!isCurrentlyRechecking.isVisible()) {
                isCurrentlyRechecking.setVisible(true);
            }
        } else if (isCurrentlyRechecking.isVisible()) {
            isCurrentlyRechecking.setVisible(false);
        }
    }

    private void isChangesListPopulated() {
        if (ChangeReporter.getIsChanges()) {
            changesAlert.setVisible(true);
        } else if (changesAlert.isVisible()) {
            changesAlert.setVisible(false);
        }
    }
}