package program;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import program.gui.*;
import program.information.DisplayShows;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.input.MoveWindow;
import program.io.FileManager;
import program.util.Variables;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Controller implements Initializable {
    private static final Logger log = Logger.getLogger(Controller.class.getName());

    private static String currentList = "active";
    private static ObservableList<DisplayShows> tableViewFields;
    private static boolean show0Remaining = false;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab showsTab;
    @FXML
    private Tab settingsTab;
    @FXML
    private Button exit;
    @FXML
    private TableView<DisplayShows> tableView = new TableView<>();
    @FXML
    private TableColumn<DisplayShows, String> shows;
    @FXML
    private TableColumn<DisplayShows, Integer> remaining;
    @FXML
    private TextField textField;
    @FXML
    private Button refreshTableView;
    @FXML
    private Button viewChanges;
    @FXML
    private CheckBox show0RemainingCheckBox;

    private static ObservableList<DisplayShows> MakeTableViewFields(ArrayList<String> showList) {
        ObservableList<DisplayShows> list = FXCollections.observableArrayList();
        if (showList != null) {
            if (currentList.matches("active") && !show0Remaining) {
                for (String aShow : showList) {
                    int remaining = UserInfoController.getRemainingNumberOfEpisodes(aShow);
                    if (remaining != 0) {
                        list.add(new DisplayShows(aShow, remaining));
                    }
                }
            } else for (String aShow : showList) {
                list.add(new DisplayShows(aShow, UserInfoController.getRemainingNumberOfEpisodes(aShow)));
            }
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

    public static void updateShowField(String aShow, int index) { // TODO Make this usable elsewhere -- Having difficulty doing this.
        removeShowField(index);
        int remaining = UserInfoController.getRemainingNumberOfEpisodes(aShow);
        if (show0Remaining && remaining != 0 || currentList.matches("inactive")) {
            tableViewFields.add(index, new DisplayShows(aShow, UserInfoController.getRemainingNumberOfEpisodes(aShow)));
        }
    }

    private static void removeShowField(int index) {
        tableViewFields.remove(index);
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
        tabPane.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT);
        tableView.setPrefSize(Variables.SIZE_WIDTH, Variables.SIZE_HEIGHT - 69);
        MainRun.startBackend();
        shows.setCellValueFactory(new PropertyValueFactory<>("show"));
        remaining.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        setTableView();
        tableView.getItems();

        tableView.setRowFactory(
                param -> {
                    final TableRow<DisplayShows> row = new TableRow<>();
                    final ContextMenu rowMenuActive = new ContextMenu();
                    final ContextMenu rowMenuInactive = new ContextMenu();

                    MenuItem setSeasonEpisode = new MenuItem("Set Season + Episode");
                    setSeasonEpisode.setOnAction(e -> {
                        log.info("\"Set Season + Episode\" is now running...");
                        String show = row.getItem().getShow();
                        String[] seasonEpisode = new ListSelectBox().pickSeasonEpisode("Pick the Season", show, ShowInfoController.getSeasonsList(show), tabPane.getScene().getWindow());
                        if (!seasonEpisode[0].contains("-1") && !seasonEpisode[1].contains("-1")) {
                            log.info("Season & Episode were valid.");
                            UserInfoController.setSeasonEpisode(show, Integer.parseInt(seasonEpisode[0]), seasonEpisode[1]);
                        } else {
                            log.info("Season & Episode weren't valid.");
                        }
                        log.info("\"Set Season + Episode\" is finished running.");
                    });
                    MenuItem playSeasonEpisode = new MenuItem("Play Season + Episode");
                    playSeasonEpisode.setOnAction(e -> {
                        log.info("\"Play Season + Episode\" is now running...");
                        String show = row.getItem().getShow();
                        String[] seasonEpisode = new ListSelectBox().pickSeasonEpisode("Pick the Season", show, ShowInfoController.getSeasonsList(show), tabPane.getScene().getWindow());
                        if (!seasonEpisode[0].contains("-1") && !seasonEpisode[1].contains("-1")) {
                            log.info("Season & Episode were valid.");
                            UserInfoController.playAnyEpisode(show, Integer.parseInt(seasonEpisode[0]), seasonEpisode[1]);
                        } else {
                            log.info("Season & Episode weren't valid.");
                        }
                        log.info("\"Play Season + Episode\" is finished running.");
                    });
                    MenuItem toggleActive = new MenuItem("Toggle Updating");
                    toggleActive.setOnAction(e -> {
                        if (currentList.matches("inactive")) {
                            UserInfoController.setActiveStatus(row.getItem().getShow(), true);
                            removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            tableView.getSelectionModel().clearSelection();
                        } else if (currentList.matches("active")) {
                            UserInfoController.setActiveStatus(row.getItem().getShow(), false);
                            removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                            tableView.getSelectionModel().clearSelection();
                        }
                    });
                    MenuItem setHidden = new MenuItem("Hide Show");
                    setHidden.setOnAction(e -> {
                        UserInfoController.setHiddenStatus(row.getItem().getShow(), true);
                        removeShowField(tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                        tableView.getSelectionModel().clearSelection();
                    });
                    MenuItem resetShow = new MenuItem("Reset to...");
                    resetShow.setOnAction(e -> {
                        log.info("Reset to running...");
                        String[] choices = {"Beginning", "End"};
                        String answer = new SelectBox().display("What should " + row.getItem().getShow() + " be reset to?", choices, tabPane.getScene().getWindow());
                        log.info(answer);
                        if (answer.matches("Beginning")) {
                            UserInfoController.setToBeginning(row.getItem().getShow());
                            log.info("Show is reset to the beginning.");
                        } else if (answer.matches("End")) {
                            UserInfoController.setToEnd(row.getItem().getShow());
                            log.info("Show is reset to the end.");
                        }
                        log.info("Reset to finished running.");
                    });
                    MenuItem openDirectory = new MenuItem("Open File Location");
                    openDirectory.setOnAction(e -> {
                        log.info("Started to open show directory...");
                        ArrayList<String> directories = ProgramSettingsController.getDirectories();
                        ArrayList<File> folders = new ArrayList<>();
                        FileManager fileManager = new FileManager();
                        for (String aDirectory : directories) {
                            String fileString = (aDirectory + '\\' + row.getItem().getShow());
                            if (fileManager.checkFolderExists(fileString)) {
                                folders.add(new File(fileString));
                            }
                        }
                        if (folders.size() == 1) {
                            fileManager.open(folders.get(0));
                        } else {
                            ConfirmBox confirmBox = new ConfirmBox();
                            Boolean openAll = confirmBox.display("Do you want to open ALL associated folders?", tabPane.getScene().getWindow());
                            if (openAll) {
                                for (File aFolder : folders) {
                                    fileManager.open(aFolder);
                                }
                            } else {
                                ListSelectBox listSelectBox = new ListSelectBox();
                                File file = listSelectBox.directories("Pick the Folder you want to open", folders, tabPane.getScene().getWindow());
                                fileManager.open(file);
                            }
                        }
                        log.info("Finished opening show directory...");
                    });
                    MenuItem getRemaining = new MenuItem("Get Remaining");
                    getRemaining.setOnAction(e -> log.info("Controller - There are " + UserInfoController.getRemainingNumberOfEpisodes(row.getItem().getShow()) + " episode(s) remaining."));

                    row.setOnMouseClicked(e -> {
                        if (e.getButton().equals(MouseButton.SECONDARY) && (!row.isEmpty())) {
                            if (currentList.matches("active")) {
                                if (Variables.devMode) {
                                    rowMenuActive.getItems().addAll(setSeasonEpisode, playSeasonEpisode, toggleActive, resetShow, getRemaining, openDirectory);
                                } else
                                    rowMenuActive.getItems().addAll(setSeasonEpisode, playSeasonEpisode, toggleActive, resetShow, openDirectory);
                                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenuActive).otherwise((ContextMenu) null));
                            } else if (currentList.matches("inactive")) {
                                if (Variables.devMode) {
                                    rowMenuInactive.getItems().addAll(toggleActive, setHidden, getRemaining, openDirectory);
                                } else rowMenuInactive.getItems().addAll(toggleActive, setHidden, openDirectory);
                                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenuInactive).otherwise((ContextMenu) null));
                            }
                        }
                        if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2 && (!row.isEmpty()) && currentList.matches("active")) {
                            String aShow = row.getItem().getShow();
                            Boolean keepPlaying = true;
                            while (keepPlaying) {
                                int fileExists = UserInfoController.doesEpisodeExists(aShow);
                                if (fileExists == 1 || fileExists == 2) {
                                    tableView.getSelectionModel().clearAndSelect(row.getIndex());
                                    UserInfoController.playAnyEpisode(aShow, UserInfoController.getCurrentSeason(aShow), UserInfoController.getCurrentEpisode(aShow));
                                    ShowConfirmBox showConfirmBox = new ShowConfirmBox();
                                    int userChoice = showConfirmBox.display("Have the watched the show?", tabPane.getScene().getWindow());
                                    if (userChoice == 1) {
                                        UserInfoController.changeEpisode(aShow, -2, true, fileExists);
                                        updateShowField(aShow, tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                                        tableView.getSelectionModel().clearSelection();
                                        keepPlaying = false;
                                    } else if (userChoice == 2) {
                                        UserInfoController.changeEpisode(aShow, -2, true, fileExists);
                                        updateShowField(aShow, tableViewFields.indexOf(tableView.getSelectionModel().getSelectedItem()));
                                        tableView.getSelectionModel().clearSelection();
                                    } else if (userChoice == 0) {
                                        tableView.getSelectionModel().clearSelection();
                                        keepPlaying = false;
                                    }
                                } else break;
                            }
                            if (keepPlaying) {
                                UserInfoController.changeEpisode(aShow, -2, false, 0);
                                MessageBox messageBox = new MessageBox();
                                messageBox.display("You have reached the end!", tabPane.getScene().getWindow());
                            }
                        }
                    });
                    return row;
                }
        );

        // ~~~~ Buttons ~~~~ \\
        exit.setOnAction(e -> Main.stop(Main.window, false, true));
        refreshTableView.setOnAction(event -> {
            if (currentList.matches("active")) {
                setTableViewFields("inactive");
                log.info("TableViewFields set to inactive.");
            } else if (currentList.matches("inactive")) {
                setTableViewFields("active");
                log.info("TableViewFields set to active.");
            }
            setTableView();
        });
        viewChanges.setOnAction(e -> {
            Boolean keepOpen = false;
            Object[] answer = null;
            do {
                Stage neededWindow = (Stage) tabPane.getScene().getWindow();
                if (answer != null && answer[1] != null) {
                    neededWindow = (Stage) answer[1];
                }
                answer = new ChangesBox().display(neededWindow);
                keepOpen = (Boolean) answer[0];
            } while (keepOpen);
        });

        show0RemainingCheckBox.setOnAction(e -> {
            show0Remaining = show0RemainingCheckBox.isSelected();
            if (show0Remaining && currentList.matches("active")) {
                log.info("Now showing shows with 0 episodes remaining.");
            } else if (currentList.matches("active")) {
                log.info("No longer showing shows with 0 episodes remaining.");
            }
            setTableViewFields(currentList);
            setTableView();
        });

        // || ~~~~ Settings Tab ~~~~ || \\
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        settingsTab.setOnSelectionChanged(e -> {
            selectionModel.select(showsTab);
            SettingsWindow settingsWindow = new SettingsWindow();
            try {
                settingsWindow.display();
            } catch (Exception e1) {
                log.severe(e1.toString());
            }
        });

        // Allow the undecorated window to be moved.
        new MoveWindow().moveWindow(tabPane);
    }
}