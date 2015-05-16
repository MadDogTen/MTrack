package program.FXMLControllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
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
    private Button about;
    @FXML
    private Button changeUpdateSpeed;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // ~~~~ Buttons ~~~~ \\

        // Users
        exit1.setOnAction(e -> {
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.close();
        });
        setDefaultUsername.setOnAction(e -> {
            ListSelectBox listSelectBox = new ListSelectBox();
            ArrayList<String> Users = UserInfoController.getAllUsers();
            String defaultUsername = listSelectBox.defaultUser("Default User", "Please choose a default user:", Users, tabPane.getScene().getWindow());
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
                String userToDelete = listSelectBox.defaultUser("Delete User", "User to delete:", users, tabPane.getScene().getWindow());
                if (userToDelete != null) {
                    ConfirmBox confirmBox = new ConfirmBox();
                    Boolean confirm = confirmBox.display("Delete User", ("Are you sure to want to delete " + userToDelete + "?"), tabPane.getScene().getWindow());
                    if (confirm && !userToDelete.isEmpty()) {
                        FileManager.deleteFile(Variables.UsersFolder, userToDelete, Variables.UsersExtension);
                    }
                }
            } else {
                MessageBox messageBox = new MessageBox();
                messageBox.display("Delete User", "There are no users to delete.", tabPane.getScene().getWindow());
            }
        });
        deleteUser.setTooltip(new Tooltip("Delete Users. Note: Can't delete current user!"));

        // Other
        exit2.setOnAction(e -> {
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.close();
        });
        changeUpdateSpeed.setOnAction(e -> {
            ProgramSettingsController.setUpdateSpeed(new TextBox().updateSpeed("Update Speed", "Enter how fast you want it to scan the show(s) folder(s)", "Leave it as is?", 120, tabPane.getScene().getWindow()));
        });
        addDirectory.setOnAction(e -> {
            int index = ProgramSettingsController.getNumberOfDirectories();
            Boolean wasAdded = ProgramSettingsController.addDirectory(index, new TextBox().addDirectoriesDisplay("Directories", "Please enter show directory", ProgramSettingsController.getDirectories(), "You need to enter a directory.", "Directory is invalid.", tabPane.getScene().getWindow()));
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
        removeDirectory.setOnAction(e -> {
            ArrayList<File> directories = new ArrayList<>();
            for (String aDirectory : ProgramSettingsController.getDirectories()) {
                directories.add(new File(aDirectory));
            }
            if (!directories.isEmpty()) {
                ListSelectBox listSelectBox = new ListSelectBox();
                String directoryToDelete = String.valueOf(listSelectBox.directories("Delete Directory", "Directory to delete:", directories, tabPane.getScene().getWindow()));
                if (directoryToDelete != null) {
                    ConfirmBox confirmBox = new ConfirmBox();
                    Boolean confirm = confirmBox.display("Delete Directory", ("Are you sure to want to delete " + directoryToDelete + "?"), tabPane.getScene().getWindow());
                    if (confirm && !directoryToDelete.isEmpty()) {
                        ProgramSettingsController.removeDirectory(directoryToDelete);
                    }
                }
            } else {
                MessageBox messageBox = new MessageBox();
                messageBox.display("Delete Directory", "There are no directories to delete.", tabPane.getScene().getWindow());
            }
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
        about.setOnAction(e -> {
            AboutBox aboutBox = new AboutBox();
            try {
                aboutBox.display(tabPane.getScene().getWindow());
            } catch (Exception e1) {
                log.severe(e1.toString());
            }
        });

        // Developer
        exit3.setOnAction(e -> {
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.close();
        });
        printAllShows.setOnAction(e -> {
            ShowInfoController.printOutAllShowsAndEpisodes();
        });
        printAllDirectories.setOnAction(e -> {
            ProgramSettingsController.printAllDirectories();
        });
        printEmptyShowFolders.setOnAction(e -> {
            ArrayList<String> emptyShows = CheckShowFiles.getEmptyShows();
            if (!emptyShows.isEmpty()) {
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
        printIgnoredShows.setOnAction(e -> {
            ArrayList<String> ignoredShow = UserInfoController.getIgnoredShows();
            log.info("Printing Ignored Shows:");
            if (!ignoredShow.isEmpty()) {
                for (String aIgnoredShow : ignoredShow) {
                    log.info(aIgnoredShow);
                }
            } else log.info("No ignored shows.");
            log.info("Finished printing ignored shows.");
        });
        setAllActive.setOnAction(e -> {
            Object[] showsList = ShowInfoController.getShowsList();
            for (Object aShow : showsList) {
                UserInfoController.setActiveStatus(String.valueOf(aShow), true);
            }
        });
        setAllInactive.setOnAction(e -> {
            Object[] showsList = ShowInfoController.getShowsList();
            for (Object aShow : showsList) {
                UserInfoController.setActiveStatus(String.valueOf(aShow), false);
            }
        });
        clearFile.setOnAction(e -> {
            ArrayList<File> directories = new ArrayList<>();
            for (String aDirectory : ProgramSettingsController.getDirectories()) {
                directories.add(new File(aDirectory));
            }
            if (!directories.isEmpty()) {
                ListSelectBox listSelectBox = new ListSelectBox();
                String directoryToClear = String.valueOf(listSelectBox.directories("Clear Directory", "Directory to Clear:", directories, tabPane.getScene().getWindow()));
                if (directoryToClear != null) {
                    ConfirmBox confirmBox = new ConfirmBox();
                    Boolean confirm = confirmBox.display("Clear Directory", ("Are you sure to want to clear " + directoryToClear + "?"), tabPane.getScene().getWindow());
                    if (confirm && !directoryToClear.isEmpty()) {
                        int index = ProgramSettingsController.getDirectories().indexOf(directoryToClear);
                        Set<String> hashMapShows = ShowInfoController.getShowsFile(index).keySet();
                        for (String aShow : hashMapShows) {
                            Boolean showExistsElsewhere = ShowInfoController.doesShowExistElsewhere(aShow, index);
                            if (!showExistsElsewhere) {
                                UserInfoController.setIgnoredStatus(aShow, true);
                            }
                        }
                        HashMap<String, HashMap<Integer, HashMap<String, String>>> blankHashMap = new HashMap<>();
                        ShowInfoController.saveShowsHashMapFile(blankHashMap, index);
                    }
                }
            } else {
                MessageBox messageBox = new MessageBox();
                messageBox.display("Clear Directory", "There are no directories to clear.", tabPane.getScene().getWindow());
            }
        });
        deleteEverythingAndClose.setOnAction(e -> {
            ConfirmBox confirmBox = new ConfirmBox();
            if (confirmBox.display("Reset Program", "Are you sure? This will delete EVERYTHING!", tabPane.getScene().getWindow())) {
                Stage stage = (Stage) tabPane.getScene().getWindow();
                stage.close();
                FileManager.deleteFolder(new File(FileManager.getDataFolder()));
                program.Main.stop(program.Main.window, true, false);
            }
        });
        deleteEverythingAndClose.setTooltip(new Tooltip("Warning, Unrecoverable!"));

        // Allow the undecorated window to be moved.
        new MoveWindow().moveWindow(tabPane);
    }
}
