package program.FXMLControllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import program.Controller;
import program.gui.*;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.input.MoveWindow;
import program.io.CheckShowFiles;
import program.io.FileManager;
import program.io.GenerateNewShowFiles;
import program.util.Strings;
import program.util.Variables;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

public class Settings implements Initializable {
    private static final Logger log = Logger.getLogger(Settings.class.getName());

    @FXML
    private TabPane tabPane;
    @FXML
    private Button exit1;
    @FXML
    private Button exit2;
    @FXML
    private Button exit3;
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
    private Button refreshObservableList;
    @FXML
    private Button changeObservableList;
    @FXML
    private Button about;
    @FXML
    private Button changeUpdateSpeed;
    @FXML
    private Button addDirectory;
    @FXML
    private Button printAllDirectories;
    @FXML
    private Button printEmptyShowFolders;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // ~~~~ Buttons ~~~~ \\
        exit1.setOnAction(e -> {
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.close();
        });
        exit2.setOnAction(e -> {
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.close();
        });
        exit3.setOnAction(e -> {
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.close();
        });
        forceRecheck.setOnAction(e -> {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    CheckShowFiles.recheckShowFile(true);
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
                log.severe(e1.toString());
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
            ProgramSettingsController.setDefaultUsername(Variables.EmptyString, 0);
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
                        FileManager.deleteFile(Variables.UsersFolder, userToDelete, Variables.UsersExtension);
                    }
                }
            } else {
                MessageBox messageBox = new MessageBox();
                messageBox.display("Delete User", "There are no users to delete.");
            }
        });
        deleteUser.setTooltip(new Tooltip("Delete Users. Note: Can't delete current user!"));
        deleteEverythingAndClose.setOnAction(e -> {
            ConfirmBox confirmBox = new ConfirmBox();
            if (confirmBox.display("Reset Program", "Are you sure? This will delete EVERYTHING!", tabPane)) {
                Stage stage = (Stage) tabPane.getScene().getWindow();
                stage.close();
                FileManager.deleteFolder(new File(FileManager.getDataFolder()));
                program.Main.stop(program.Main.window, true, false);
            }
        });
        refreshObservableList.setOnAction(e -> {
            Controller.setTableViewFields(Controller.currentList);
        });
        changeObservableList.setOnAction(e -> {
            ListSelectBox listSelectBox = new ListSelectBox();
            ArrayList<String> observableListChoices = new ArrayList<>();
            observableListChoices.add("Active");
            observableListChoices.add("Inactive");

            String choice = listSelectBox.display("Change TableView", "Select the TableView you want to switch too.", observableListChoices);

            if (choice.matches("Active")) {
                Controller.setTableViewFields("active");
            } else if (choice.matches("Inactive")) {
                Controller.setTableViewFields("inactive");
            }
        });
        about.setOnAction(e -> {
            AboutBox aboutBox = new AboutBox();
            try {
                aboutBox.display();
            } catch (Exception e1) {
                log.severe(e1.toString());
            }

        });
        changeUpdateSpeed.setOnAction(e -> {
            ProgramSettingsController.setUpdateSpeed(new TextBox().updateSpeed("Update Speed", "Enter how fast you want it to scan the show(s) folder(s)", "Leave it as is?", 120));
        });
        printAllDirectories.setOnAction(e -> {
            ProgramSettingsController.printAllDirectories();
        });
        addDirectory.setOnAction(e -> {
            int index = ProgramSettingsController.getNumberOfDirectories();
            Boolean wasAdded = ProgramSettingsController.addDirectory(index, new TextBox().addDirectoriesDisplay("Directories", "Please enter show directory", ProgramSettingsController.getDirectories(), "You need to enter a directory.", "Directory is invalid."));
            if (!wasAdded) {
                log.info("Directory was added.");
                ArrayList<String> directories = ProgramSettingsController.getDirectories();
                directories = ProgramSettingsController.getDirectories();
                final ArrayList<String> finalDirectories = directories;
                final Boolean[] taskRunning = {true};
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        GenerateNewShowFiles.generateShowsFile(index, new File(finalDirectories.get(index)), false);
                        taskRunning[0] = false;
                        return null;
                    }
                };
                new Thread(task).start();
                while (taskRunning[0]) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        log.severe(e1.toString());
                    }
                }
                ShowInfoController.loadShowsFile();
                HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile = ShowInfoController.getShowsFile(index);
                for (String aShow : showsFile.keySet()) {
                    UserInfoController.addNewShow(aShow);
                }
            } else log.info("Directory wasn't added.");
        });
        printEmptyShowFolders.setOnAction(e -> {
            ArrayList<String> emptyShows = CheckShowFiles.getEmptyShows();
            if (!emptyShows.isEmpty()) {
                System.out.println(emptyShows);
                ArrayList<String> directories = ProgramSettingsController.getDirectories();
                for (String aDirectory : directories) {
                    Set<String> shows = ShowInfoController.getShowsFile(ProgramSettingsController.getDirectoryIndex(aDirectory)).keySet();
                    ArrayList<String> emptyShowsDir = new ArrayList<>();
                    for (String aShow : emptyShows) {
                        String fileString = (aDirectory + '\\' + aShow);
                        if (FileManager.checkFolderExists(fileString) && !shows.contains(aShow)) {
                            emptyShowsDir.add(aShow);
                        }
                    }
                    log.info("Empty shows in \"" + aDirectory + "\": " + emptyShowsDir);
                }
            }
        });

        new MoveWindow().moveTabPane(tabPane);
    }
}
