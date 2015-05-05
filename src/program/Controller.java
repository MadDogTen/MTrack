package program;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import program.gui.*;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.input.MoveWindow;
import program.io.CheckShowFiles;
import program.io.FileManager;
import program.util.Strings;
import program.util.Variables;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    private static ObservableList<DisplayShows> tableViewFields;
    private static String currentList;

    @FXML
    private Button exit1;
    @FXML
    private Button exit2;
    @FXML
    private TabPane tabPane;
    @FXML
    private TableView<DisplayShows> tableView = new TableView<>();
    @FXML
    private TableColumn<DisplayShows, String> shows;
    @FXML
    private TableColumn<DisplayShows, Integer> remaining;
    @FXML
    private Button forceRecheck;
    @FXML
    private Button openProgramFolder;
    @FXML
    private Button printAllShows;
    @FXML
    private TextField textField;
    @FXML
    private Button deleteEverythingAndClose;
    @FXML
    private Button setDefaultUsername;
    @FXML
    private Button clearDefaultUsername;
    @FXML
    private Button deleteUser;
    @FXML
    private Button refreshObservableList;
    @FXML
    private Button changeObservableList;

    private static ObservableList<DisplayShows> MakeTableViewFields(ArrayList<String> showList) {
        ObservableList<DisplayShows> list = FXCollections.observableArrayList();
        for (String aShow : showList) {
            list.add(new DisplayShows(aShow, UserInfoController.getRemainingNumberOfEpisodes(aShow)));
        }
        return list;
    }

    public static void setTableViewFields(String type) {
        System.out.println("setTableViewFields Running...\n");
        if (type.matches("active")) {
            tableViewFields = MakeTableViewFields(UserInfoController.getActiveShows());
            currentList = "active";
        } else if (type.matches("inactive")) {
            tableViewFields = MakeTableViewFields(UserInfoController.getInactiveShows());
            currentList = "inactive";
        }
    }

    public static void updateShowField(String aShow, int index) { // TODO Make this usable elsewhere
        tableViewFields.remove(index);
        tableViewFields.add(index, new DisplayShows(aShow, UserInfoController.getRemainingNumberOfEpisodes(aShow)));
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        System.out.println("Controller Running...\n");
        MainRun.startBackend();
        shows.setCellValueFactory(new PropertyValueFactory<>("show"));
        remaining.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        FilteredList<DisplayShows> filteredData = new FilteredList<>(tableViewFields, p -> true);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(show -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        return show.getShow().toLowerCase().contains(lowerCaseFilter);
                    }
            );
        });
        SortedList<DisplayShows> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
        tableView.setRowFactory(
                param -> {
                    final TableRow<DisplayShows> row = new TableRow<>();
                    final ContextMenu rowMenu = new ContextMenu();
                    MenuItem playSeasonEpisode = new MenuItem("Pick Season/Episode");
                    playSeasonEpisode.setOnAction(e -> {
                        DoubleTextBox doubleTextBox = new DoubleTextBox();
                        int[] seasonEpisode = doubleTextBox.displaySeasonEpisode("Open Episode", "Season", "Episode");
                        int fileExists = UserInfoController.doesSeasonEpisodeExists(row.getItem().getShow(), seasonEpisode[0], String.valueOf(seasonEpisode[1]));
                        if (fileExists == 1 || fileExists == 2) {
                            UserInfoController.playAnyEpisode(row.getItem().getShow(), seasonEpisode[0], String.valueOf(seasonEpisode[1]), true, -1);
                        } else {
                            MessageBox messageBox = new MessageBox();
                            messageBox.display("Pick Season/Episode", "Your selection doesn't exist.");
                        }
                    });
                    MenuItem setNotActive = new MenuItem("Don't Update");
                    setNotActive.setOnAction(e -> { //TODO Have this remove the show from the current ObservableList
                        UserInfoController.setActiveStatus(row.getItem().getShow(), false);
                    });
                    MenuItem resetShow = new MenuItem("Reset");
                    resetShow.setOnAction(e -> UserInfoController.setToBeginning(row.getItem().getShow()));
                    MenuItem getRemaining = new MenuItem("Get Remaining");
                    getRemaining.setOnAction(e -> {
                        System.out.println("There are " + UserInfoController.getRemainingNumberOfEpisodes(row.getItem().getShow()) + " episode(s) remaining.");
                    });
                    MenuItem openDirectory = new MenuItem("Open File Location");
                    openDirectory.setOnAction(e -> {
                        ArrayList<String> directories = ProgramSettingsController.getDirectories();
                        ArrayList<File> folders = new ArrayList<>();
                        for (String aDirectory : directories) {
                            String fileString = (aDirectory + '\\' + row.getItem().getShow());
                            if (FileManager.checkFolderExists(fileString)) {
                                folders.add(new File(fileString));
                            }
                        }
                        if (folders.size() == 1) {
                            FileManager.open(folders.get(0));
                        } else {
                            ConfirmBox confirmBox = new ConfirmBox();
                            Boolean openAll = confirmBox.display("Open Folder", "Do you want to open ALL associated folders?");
                            if (openAll) {
                                for (File aFolder : folders) {
                                    FileManager.open(aFolder);
                                }
                            } else {
                                ListSelectBox listSelectBox = new ListSelectBox();
                                File file = listSelectBox.directories("Open Folder", "Pick the Folder you want to open", folders);
                                FileManager.open(file);
                            }
                        }
                    });
                    rowMenu.getItems().addAll(playSeasonEpisode, setNotActive, resetShow, getRemaining, openDirectory);
                    row.contextMenuProperty().bind(
                            Bindings.when(Bindings.isNotNull(row.itemProperty()))
                                    .then(rowMenu)
                                    .otherwise((ContextMenu) null));
                    row.setOnMouseClicked(e -> {
                        if (e.getClickCount() == 2 && (!row.isEmpty())) {
                            String aShow = row.getItem().getShow();
                            Boolean keepPlaying = true;
                            while (keepPlaying) {
                                int fileExists = UserInfoController.doesEpisodeExists(aShow);
                                if (fileExists == 1 || fileExists == 2) {
                                    tableView.getSelectionModel().clearAndSelect(row.getIndex());
                                    UserInfoController.playAnyEpisode(aShow, -1, "", true, fileExists);
                                    ShowConfirmBox showConfirmBox = new ShowConfirmBox();
                                    int userChoice = showConfirmBox.display("Playing Show", "Have the watched the show?", tabPane);
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
                                messageBox.display("Error", "You have reached the end!", tabPane);
                            }
                        }
                    });
                    return row;
                }
        );
        // ~~~~ Buttons ~~~~ \\
        // Exit Buttons
        exit1.setOnAction(e -> {
            Main.stop(Main.window, false, true);
        });
        exit2.setOnAction(e -> {
            Main.stop(Main.window, false, true);
        });
        forceRecheck.setOnAction(e -> {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    CheckShowFiles.recheckShowFile();
                    return null;
                }
            };
            new Thread(task).start();
        });
        openProgramFolder.setOnAction(e -> {
            File file = new File(FileManager.getDataFolder());
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        printAllShows.setOnAction(e -> {
            ShowInfoController.printOutAllShowsAndEpisodes();
        });
        setDefaultUsername.setOnAction(e -> {
            ListSelectBox listSelectBox = new ListSelectBox();
            ArrayList<String> Users = UserInfoController.getAllUsers();
            String defaultUsername = listSelectBox.defaultUser("Default User", "Please choose a default user:", Users);
            if (defaultUsername != null && !defaultUsername.isEmpty()) {
                ProgramSettingsController.setDefaultUsername(defaultUsername, 1);
            }
        });
        clearDefaultUsername.setOnAction(e -> {
            ProgramSettingsController.setDefaultUsername("", 0);
        });
        deleteUser.setOnAction(e -> {
            ArrayList<String> users = UserInfoController.getAllUsers();
            users.remove(Strings.UserName);
            if (!users.isEmpty()) {
                ListSelectBox listSelectBox = new ListSelectBox();
                String userToDelete = listSelectBox.defaultUser("Delete User", "User to delete:", users);
                if (userToDelete != null) {
                    ConfirmBox confirmBox = new ConfirmBox();
                    Boolean confirm = confirmBox.display("Delete User", ("Are you sure to want to delete " + userToDelete + "?"));
                    if (confirm && !userToDelete.isEmpty()) {
                        FileManager.deleteFile(Variables.settingsFolder, userToDelete, Variables.settingsExtension);
                    }
                }
            } else {
                MessageBox messageBox = new MessageBox();
                messageBox.display("Delete User", "There are no users to delete.");
            }
        });
        deleteEverythingAndClose.setOnAction(e -> {
            ConfirmBox confirmBox = new ConfirmBox();
            if (confirmBox.display("Reset Program", "Are you sure? This will delete EVERYTHING!", tabPane)) {
                File file = new File(FileManager.getDataFolder());
                FileManager.deleteFolder(file);
                Main.stop(Main.window, true, false);
            }
        });
        refreshObservableList.setOnAction(e -> {
            setTableViewFields(currentList);
        });
        changeObservableList.setOnAction(e -> {
            ListSelectBox listSelectBox = new ListSelectBox();
            ArrayList<String> observableListChoices = new ArrayList<>();
            observableListChoices.add("Active");
            observableListChoices.add("Inactive");

            String choice = listSelectBox.display("Change TableView", "Select the TableView you want to switch too.", observableListChoices);

            if (choice.matches("Active")) {
                setTableViewFields("active");
            } else if (choice.matches("Inactive")) {
                setTableViewFields("inactive");
            }
        });
        deleteUser.setTooltip(new Tooltip("Delete Users. Note: Can't delete current user!"));
        MoveWindow moveWindow = new MoveWindow();
        moveWindow.moveTabPane(tabPane);
    }
}