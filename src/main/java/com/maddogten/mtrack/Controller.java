package com.maddogten.mtrack;

import com.maddogten.mtrack.gui.*;
import com.maddogten.mtrack.information.*;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.information.show.DisplayShows;
import com.maddogten.mtrack.io.CheckShowFiles;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
      Controller is the controller for the main stage started in Main. Handles quite a bit, Most likely more then it should.
 */

@SuppressWarnings("WeakerAccess")
public class Controller implements Initializable {
    private static final Logger log = Logger.getLogger(Controller.class.getName());
    private static final SettingsWindow settingsWindow = new SettingsWindow();
    private static ProgramSettingsController programSettingsController;
    private static ShowInfoController showInfoController;
    private static UserInfoController userInfoController;
    private static CheckShowFiles checkShowFiles;
    private static DirectoryController directoryController;
    private static Controller controller;
    // This is the list that is currently showing in the tableView. Currently can only be "active" or "inactive".
    private static String currentList = Strings.EmptyString;
    private static ObservableList<DisplayShows> tableViewFields;
    // show0Remaining - If this true, It will display shows that have 0 episodes remaining, and if false, hides them. Only works with the active list.
    // wereShowsChanged - This is set the true if you set a show active while in the inactive list. If this is true when you switch back to the active this, it will start a recheck. This is because the show may be highly outdated as inactive shows aren't updated.
    // isShowCurrentlyPlaying - While a show is currently playing, this is true, otherwise it is false. This is used in mainRun to make rechecking take 10x longer to happen when a show is playing.
    private static boolean show0Remaining, wereShowsChanged, isShowCurrentlyPlaying;
    private final ChangesBox changesBox = new ChangesBox();
    @SuppressWarnings("unused")
    @FXML
    private Pane pane;
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
    private Pane pingingDirectoryPane;
    @FXML
    private ImageView pingingDirectory;
    @FXML
    private Button clearTextField;
    @FXML
    private Tooltip show0RemainingCheckBoxTooltip;
    @FXML
    private Tooltip isCurrentlyRecheckingTooltip;

    // This will set the ObservableList using the showList provided. For the active list, if show0Remaining is false, then it skips adding those, otherwise all shows are added. For the inactive list, all shows are added.
    private static ObservableList<DisplayShows> MakeTableViewFields(ArrayList<String> showList) {
        ObservableList<DisplayShows> list = FXCollections.observableArrayList();
        if (!showList.isEmpty()) {
            if (currentList.matches("active") && !show0Remaining) {
                showList.forEach(aShow -> {
                    int remaining = userInfoController.getRemainingNumberOfEpisodes(aShow, showInfoController);
                    if (remaining != 0)
                        list.add(new DisplayShows(aShow, remaining, userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow)));
                });
            } else
                list.addAll(showList.stream().map(aShow -> new DisplayShows(aShow, userInfoController.getRemainingNumberOfEpisodes(aShow, showInfoController), userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow))).collect(Collectors.toList()));
        }
        return list;
    }

    // This sets the current tableView to whichever you want.
    public static void setTableViewFields(String type) {
        switch (type) {
            case "active":
                if (!currentList.matches("active")) currentList = "active";
                tableViewFields = MakeTableViewFields(userInfoController.getActiveShows());
                break;
            case "inactive":
                if (!currentList.matches("inactive")) currentList = "inactive";
                tableViewFields = MakeTableViewFields(userInfoController.getInactiveShows());
                break;
        }
    }

    // Public method to update a particular show with new information. If the show happens to have been remove, then showExists will be false, which isShowActive will be false, which makes remove true, which meaning it won't add the show back to the list.
    public static void updateShowField(String aShow, boolean showExists) {
        int index = -2;
        for (DisplayShows show : tableViewFields) {
            if (show.getShow().replaceAll("[(]|[)]", "").matches(aShow.replaceAll("[(]|[)]", ""))) {
                index = tableViewFields.indexOf(show);
                break;
            }
        }
        boolean isShowActive = (userInfoController.isShowActive(aShow) && showExists), keep = true;
        if (currentList.matches("active") && !isShowActive || currentList.matches("inactive") && isShowActive)
            keep = false;
        if (index != -2) removeShowField(index);
        if (keep) {
            int remaining = userInfoController.getRemainingNumberOfEpisodes(aShow, showInfoController), season = userInfoController.getCurrentSeason(aShow), episode = userInfoController.getCurrentEpisode(aShow);
            DisplayShows show = new DisplayShows(aShow, remaining, season, episode);
            if (((show0Remaining || currentList.contains("inactive")) || remaining != 0) && index != -2)
                tableViewFields.add(index, show);
            else if (index == -2 && ((show0Remaining) || currentList.contains("inactive") || remaining != 0))
                tableViewFields.add(show);
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

    public static DirectoryController getDirectoryController() {
        return directoryController;
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

    public static void closeChangeBoxStage() {
        log.fine("ChangeBox was open, closing...");
        if (controller != null) controller.changesBox.closeStage();
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

    // This first Filters the observableList if you have anything in the searchList, Then enables or disables the show0RemainingCheckbox depending on which list it is currently on.
    private void setTableView() {
        FilteredList<DisplayShows> newFilteredData = new FilteredList<>(tableViewFields, p -> true);
        newFilteredData.setPredicate(show -> textField.getText() == null || textField.getText().isEmpty() || show.getShow().toLowerCase().contains(textField.getText().toLowerCase()));
        textField.textProperty().addListener((observable, oldValue, newValue) -> newFilteredData.setPredicate(show -> newValue == null || newValue.isEmpty() || show.getShow().toLowerCase().contains(newValue.toLowerCase())));
        SortedList<DisplayShows> newSortedData = new SortedList<>(newFilteredData);
        newSortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(newSortedData);
        if (currentList.matches("active")) show0RemainingCheckBox.setVisible(true);
        else if (currentList.matches("inactive")) show0RemainingCheckBox.setVisible(false);
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        log.info("MainController Running...");
        setController();
        programSettingsController = Main.getProgramSettingsController();
        showInfoController = Main.getShowInfoController();
        userInfoController = Main.getUserInfoController();
        checkShowFiles = Main.getCheckShowFiles();
        directoryController = Main.getDirectoryController();
        pane.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT);
        tabPane.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT);
        tableView.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT - 69);
        showsTab.textProperty().bind(Strings.Shows);
        settingsTab.textProperty().bind(Strings.Settings);
        show0Remaining = programSettingsController.getSettingsFile().isShow0Remaining();
        shows.setCellValueFactory(new PropertyValueFactory<>("show"));
        shows.setSortType(TableColumn.SortType.ASCENDING);
        shows.textProperty().bind(Strings.Shows);
        shows.setPrefWidth(programSettingsController.getSettingsFile().getShowColumnWidth());
        shows.setVisible(programSettingsController.getSettingsFile().isShowColumnVisibility());
        remaining.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        remaining.textProperty().bind(Strings.Left);
        remaining.setPrefWidth(programSettingsController.getSettingsFile().getRemainingColumnWidth());
        remaining.setVisible(programSettingsController.getSettingsFile().isRemainingColumnVisibility());
        season.setCellValueFactory(new PropertyValueFactory<>("season"));
        season.textProperty().bind(Strings.Season);
        season.setPrefWidth(programSettingsController.getSettingsFile().getSeasonColumnWidth());
        season.setVisible(programSettingsController.getSettingsFile().isSeasonColumnVisibility());
        episode.setCellValueFactory(new PropertyValueFactory<>("episode"));
        episode.textProperty().bind(Strings.Episode);
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
                    MenuItem setSeasonEpisode = new MenuItem();
                    setSeasonEpisode.textProperty().bind(Strings.SetSeasonEpisode);
                    setSeasonEpisode.setOnAction(e -> {
                        log.info("\"Set Season + Episode\" is now running...");
                        String show = row.getItem().getShow();
                        int[] seasonEpisode = new ListSelectBox().pickSeasonEpisode(show, showInfoController, (Stage) pane.getScene().getWindow());
                        if (seasonEpisode[0] != -1 && seasonEpisode[1] != -1) {
                            log.info("Season & Episode were valid.");
                            userInfoController.setSeasonEpisode(show, seasonEpisode[0], seasonEpisode[1]);
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
                        int[] seasonEpisode = new ListSelectBox().pickSeasonEpisode(show, showInfoController, (Stage) pane.getScene().getWindow());
                        if (seasonEpisode[0] != -1 && seasonEpisode[1] != -1) {
                            log.info("Season & Episode were valid.");
                            userInfoController.playAnyEpisode(show, seasonEpisode[0], seasonEpisode[1]);
                        } else log.info("Season & Episode weren't valid.");
                        log.info("\"Play Season + Episode\" is finished running.");
                    });
                    MenuItem toggleActive = new MenuItem();
                    toggleActive.setOnAction(e -> {
                        if (currentList.matches("inactive")) {
                            userInfoController.setActiveStatus(row.getItem().getShow(), true);
                            if (!wereShowsChanged) wereShowsChanged = true;
                            removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            tableView.getSelectionModel().clearSelection();
                        } else if (currentList.matches("active")) {
                            userInfoController.setActiveStatus(row.getItem().getShow(), false);
                            removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            tableView.getSelectionModel().clearSelection();
                        }
                    });
                    MenuItem setHidden = new MenuItem();
                    setHidden.textProperty().bind(Strings.HideShow);
                    setHidden.setOnAction(e -> {
                        userInfoController.setHiddenStatus(row.getItem().getShow(), true);
                        removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                        tableView.getSelectionModel().clearSelection();
                    });
                    MenuItem resetShow = new MenuItem();
                    resetShow.textProperty().bind(Strings.ResetTo);
                    resetShow.setOnAction(e -> {
                        log.info("Reset to running...");
                        String[] choices = {Strings.Beginning.getValue(), Strings.End.getValue()};
                        String answer = new SelectBox().select(Strings.WhatShould.getValue() + row.getItem().getShow() + Strings.BeResetTo.getValue(), choices, (Stage) pane.getScene().getWindow());
                        log.info(answer);
                        if (answer.matches(Strings.Beginning.getValue())) {
                            userInfoController.setToBeginning(row.getItem().getShow());
                            updateShowField(row.getItem().getShow(), true);
                            tableView.getSelectionModel().clearSelection();
                            log.info(Strings.ShowIsResetToThe.getValue() + ' ' + Strings.Beginning.getValue().toLowerCase() + '.');
                        } else if (answer.matches(Strings.End.getValue())) {
                            userInfoController.setToEnd(row.getItem().getShow());
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
                        ArrayList<Directory> directories = directoryController.getDirectories();
                        ArrayList<Directory> folders = new ArrayList<>();
                        FileManager fileManager = new FileManager();
                        directories.forEach(aDirectory -> {
                            File fileName = new File(aDirectory.getDirectory() + Strings.FileSeparator + row.getItem().getShow());
                            if (fileName.exists())
                                folders.add(new Directory(fileName, fileName.toString(), -2, -2, null, -2));
                        });
                        if (folders.size() == 1) fileManager.open(folders.get(0).getDirectory());
                        else {
                            new ListSelectBox().openDirectory(folders, (Stage) tabPane.getScene().getWindow());
                        }
                        log.info("Finished opening show directory...");
                    });
                    MenuItem getRemaining = new MenuItem();
                    getRemaining.textProperty().bind(Strings.GetRemaining);
                    getRemaining.setOnAction(e -> log.info("There are " + userInfoController.getRemainingNumberOfEpisodes(row.getItem().getShow(), showInfoController) + " episode(s) remaining."));
                    MenuItem playPreviousEpisode = new MenuItem();
                    playPreviousEpisode.textProperty().bind(Strings.PlayPreviousEpisode);
                    playPreviousEpisode.setOnAction(e -> {
                        log.info("Attempting to play previous episode...");
                        int[] seasonEpisode = userInfoController.getPreviousEpisodeIfExists(row.getItem().getShow());
                        if (seasonEpisode[0] == -2 || seasonEpisode[0] == -3)
                            new MessageBox().message(new StringProperty[]{Strings.NoDirectlyPrecedingEpisodesFound}, (Stage) tabPane.getScene().getWindow());
                        else
                            userInfoController.playAnyEpisode(row.getItem().getShow(), seasonEpisode[0], seasonEpisode[1]);
                        log.info("Finished attempting to play previous episode.");
                    });
                    MenuItem printCurrentSeasonEpisode = new MenuItem();
                    printCurrentSeasonEpisode.textProperty().bind(Strings.PrintCurrentSeasonEpisode);
                    printCurrentSeasonEpisode.setOnAction(e -> log.info(row.getItem().getShow() + " - Season: " + userInfoController.getCurrentSeason(row.getItem().getShow()) + " - Episode: " + userInfoController.getCurrentEpisode(row.getItem().getShow())));
                    MenuItem printShowInformation = new MenuItem();
                    printShowInformation.setText("Print Show Information"); //TODO Add Localization
                    printShowInformation.setOnAction(e -> showInfoController.printShowInformation(row.getItem().getShow()));
                    row.setOnMouseClicked(e -> {
                        if (e.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
                            if (currentList.matches("active")) {
                                toggleActive.textProperty().bind(Strings.SetInactive);
                                if (Variables.devMode)
                                    rowMenuActive.getItems().addAll(setSeasonEpisode, playSeasonEpisode, playPreviousEpisode, resetShow, toggleActive, getRemaining, openDirectory, printCurrentSeasonEpisode, printShowInformation);
                                else
                                    rowMenuActive.getItems().addAll(setSeasonEpisode, playSeasonEpisode, playPreviousEpisode, resetShow, toggleActive, openDirectory);
                                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenuActive).otherwise((ContextMenu) null));
                            } else if (currentList.matches("inactive")) {
                                toggleActive.textProperty().bind(Strings.SetActive);
                                if (Variables.devMode)
                                    rowMenuInactive.getItems().addAll(toggleActive, setHidden, getRemaining, openDirectory, printShowInformation);
                                else rowMenuInactive.getItems().addAll(toggleActive, setHidden, openDirectory);
                                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenuInactive).otherwise((ContextMenu) null));
                            }
                        }
                        if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && (!row.isEmpty()) && currentList.matches("active")) {
                            String aShow = row.getItem().getShow();
                            boolean keepPlaying = true;
                            isShowCurrentlyPlaying = true;
                            while (keepPlaying) {
                                if (userInfoController.doesEpisodeExistInShowFile(aShow)) {
                                    userInfoController.playAnyEpisode(aShow, userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow));
                                    int userChoice = new ShowConfirmBox().showConfirm(Strings.HaveYouWatchedTheShow, userInfoController.getRemainingNumberOfEpisodes(aShow, showInfoController) == 1, (Stage) pane.getScene().getWindow());
                                    if (userChoice == 1) {
                                        userInfoController.changeEpisode(aShow, -2);
                                        updateShowField(aShow, true);
                                        tableView.getSelectionModel().clearSelection();
                                        keepPlaying = false;
                                    } else if (userChoice == 2) {
                                        userInfoController.changeEpisode(aShow, -2);
                                        updateShowField(aShow, true);
                                        tableView.getSelectionModel().clearSelection();
                                    } else if (userChoice == 0) {
                                        tableView.getSelectionModel().clearSelection();
                                        keepPlaying = false;
                                    }
                                } else {
                                    // This is being done because I set episodes 1 further when watched, which works when the season is ongoing. However, when moving onto a new season it breaks. This is a simple check to move
                                    // it into a new season if one is found, and no further episodes are found in the current season.
                                    if (row.getItem().getRemaining() > 0 && userInfoController.shouldSwitchSeasons(aShow, userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow))) {
                                        log.info("No further episodes found for current season, further episodes found in next season, switching to new season.");
                                        userInfoController.changeEpisode(aShow, -2);
                                        updateShowField(aShow, true);
                                        tableView.getSelectionModel().clearSelection();
                                    } else break;
                                }
                            }
                            isShowCurrentlyPlaying = false;
                        }
                    });
                    return row;
                }
        );

        // ~~~~ Search TextField ~~~~ \\
        // The ContextMenu *really* isn't needed, But I wanted to make the clearTextField button to properly disappear and appear when needed. Using a Task would have been much easier...
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
        // End of unnecessary stuff.

        clearTextField.setText(Strings.EmptyString);
        textField.setOnKeyTyped(e -> {
            if (!e.getCharacter().matches("") && !clearTextField.isVisible()) clearTextField.setVisible(true);
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
            if (currentList.matches("active")) {
                setTableViewFields("inactive");
                log.info("TableViewFields set to inactive.");
            } else if (currentList.matches("inactive")) {
                if (wereShowsChanged) {
                    Task<Void> task = new Task<Void>() {
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
            this.setTableView();
        });
        changeTableView.textProperty().bind(Strings.SwitchBetweenActiveInactiveList);
        viewChanges.textProperty().bind(Strings.OpenChangesWindow);
        viewChanges.setOnAction(e -> openChangeBox());
        changesAlert.setOnAction(e -> openChangeBox());
        show0RemainingCheckBox.setSelected(show0Remaining);
        show0RemainingCheckBox.setOnAction(e -> {
            show0Remaining = show0RemainingCheckBox.isSelected();
            programSettingsController.getSettingsFile().setShow0Remaining(show0Remaining);
            if (show0Remaining && currentList.matches("active"))
                log.info("Now showing shows with 0 episodes remaining.");
            else if (currentList.matches("active")) log.info("No longer showing shows with 0 episodes remaining.");
            setTableViewFields(currentList);
            setTableView();
        });
        show0RemainingCheckBoxTooltip.textProperty().bind(Strings.ShowHiddenShowsWith0EpisodeLeft);
        Tooltip pingingDirectoryTooltip = new Tooltip();
        pingingDirectoryTooltip.textProperty().bind(Strings.PingingDirectories);
        pingingDirectoryTooltip.getStyleClass().add("tooltip");
        Tooltip.install(
                pingingDirectoryPane,
                pingingDirectoryTooltip
        );

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
        // Shows an indicator when its rechecking the shows.
        isCurrentlyRecheckingTooltip.textProperty().bind(Strings.CurrentlyRechecking);
        Task<Void> mainTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (Main.programRunning) {
                    if (checkShowFiles.getRecheckShowFileRunning()) {
                        pingingDirectory.setVisible(true);
                        pingingDirectoryPane.setMouseTransparent(false);
                        while (checkShowFiles.isCurrentlyCheckingDirectories()) {
                            if (pingingDirectory.getRotate() == 180) pingingDirectory.setRotate(0);
                            pingingDirectory.setRotate(pingingDirectory.getRotate() + 4);
                            Thread.sleep(80);
                        }
                        pingingDirectory.setRotate(0);
                        pingingDirectory.setVisible(false);
                        pingingDirectoryPane.setMouseTransparent(true);
                        isCurrentlyRechecking.setVisible(true);
                        isCurrentlyRechecking.setMouseTransparent(false);
                        while (checkShowFiles.getRecheckShowFileRunning()) {
                            Platform.runLater(() -> isCurrentlyRechecking.setProgress(checkShowFiles.getRecheckShowFilePercentage()));
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

    private void openChangeBox() {
        changesBox.openChanges((Stage) pane.getScene().getWindow());
    }

    private void checkIfChangesListIsPopulated() {
        if (ChangeReporter.getIsChanges()) changesAlert.setVisible(true);
        else if (changesAlert.isVisible()) changesAlert.setVisible(false);
    }

    private void setController() {
        controller = this;
    }
}
