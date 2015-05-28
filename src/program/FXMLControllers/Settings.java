package program.FXMLControllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import program.Controller;
import program.Main;
import program.gui.*;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.io.CheckShowFiles;
import program.io.FileManager;
import program.io.GenerateNewShowFiles;
import program.io.MoveWindow;
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
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class Settings implements Initializable {
    private static final Logger log = Logger.getLogger(Settings.class.getName());

    @SuppressWarnings("unused")
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab developerTab;
    @FXML
    private Button exit;
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
    @FXML
    private Button printProgramSettingsFileVersion;
    @FXML
    private Button printUserSettingsFileVersion;
    @FXML
    private Button printHiddenShows;
    @FXML
    private Button unHideAll;
    @FXML
    private Button printAllUserInfo;
    @FXML
    private Button add1ToDirectoryVersion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // ~~~~ Buttons ~~~~ \\

        // Users
        exit.setOnAction(e -> {
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.close();
        });
        setDefaultUsername.setOnAction(e -> {
            ListSelectBox listSelectBox = new ListSelectBox();
            ArrayList<String> Users = UserInfoController.getAllUsers();
            String defaultUsername = listSelectBox.defaultUser("Please choose a default user:", Users, tabPane.getScene().getWindow());
            if (defaultUsername != null && !defaultUsername.isEmpty()) {
                ProgramSettingsController.setDefaultUsername(defaultUsername, 1);
            }
        });
        clearDefaultUsername.setOnAction(e -> ProgramSettingsController.setDefaultUsername(Strings.EmptyString, 0));
        deleteUser.setOnAction(e -> {
            ArrayList<String> users = UserInfoController.getAllUsers();
            users.remove(Strings.UserName);
            if (users.isEmpty()) {
                MessageBox messageBox = new MessageBox();
                messageBox.display("There are other users to delete.", tabPane.getScene().getWindow());
            } else {
                ListSelectBox listSelectBox = new ListSelectBox();
                String userToDelete = listSelectBox.defaultUser("User to delete:", users, tabPane.getScene().getWindow());
                if (userToDelete != null) {
                    ConfirmBox confirmBox = new ConfirmBox();
                    Boolean confirm = confirmBox.display(("Are you sure to want to delete " + userToDelete + '?'), tabPane.getScene().getWindow());
                    if (confirm && !userToDelete.isEmpty()) {
                        new FileManager().deleteFile(Variables.UsersFolder, userToDelete, Variables.UsersExtension);
                    }
                }
            }
        });
        deleteUser.setTooltip(new Tooltip("Delete Users. Note: Can't delete current user!"));

        // Other
        changeUpdateSpeed.setOnAction(e -> ProgramSettingsController.setUpdateSpeed(new TextBox().updateSpeed("Enter how fast you want it to scan the show(s) folder(s)", "Leave it as is?", 120, tabPane.getScene().getWindow())));
        addDirectory.setOnAction(e -> {
            int index = ProgramSettingsController.getLowestFreeDirectoryIndex();
            Boolean[] wasAdded = ProgramSettingsController.addDirectory(index, new TextBox().addDirectoriesDisplay("Please enter show directory", ProgramSettingsController.getDirectories(), "You need to enter a directory.", "Directory is invalid.", tabPane.getScene().getWindow()));
            if (wasAdded[0]) {
                log.info("Directory was added.");
                File directory = ProgramSettingsController.getDirectory(index);
                final Boolean[] taskRunning = {true};
                Task<Void> task = new Task<Void>() {
                    @SuppressWarnings("ReturnOfNull")
                    @Override
                    protected Void call() throws Exception {
                        GenerateNewShowFiles.generateShowsFile(index, directory, false, false);
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
                ShowInfoController.loadShowsFile(true);
                HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile = ShowInfoController.getDirectoryHashMap(index);
                showsFile.keySet().forEach(aShow -> {
                    UserInfoController.addNewShow(aShow);
                    Controller.updateShowField(aShow, true);
                });
                ProgramSettingsController.setMainDirectoryVersion(ProgramSettingsController.getMainDirectoryVersion() + 1, true);
            } else log.info("Directory wasn't added.");
        });
        removeDirectory.setOnAction(e -> {
            log.info("Remove Directory Started:");
            ArrayList<File> directories = new ArrayList<>();
            directories.addAll(ProgramSettingsController.getDirectories().stream().map(File::new).collect(Collectors.toList()));
            if (directories.isEmpty()) {
                log.info("No directories to delete.");
                MessageBox messageBox = new MessageBox();
                messageBox.display("There are no directories to delete.", tabPane.getScene().getWindow());
            } else {
                ListSelectBox listSelectBox = new ListSelectBox();
                String directoryToDelete = String.valueOf(listSelectBox.directories("Directory to delete:", directories, tabPane.getScene().getWindow()));
                if (directoryToDelete != null && !directoryToDelete.isEmpty()) {
                    log.info("Directory selected for deletion: " + directoryToDelete);
                    ConfirmBox confirmBox = new ConfirmBox();
                    Boolean confirm = confirmBox.display(("Are you sure to want to delete " + directoryToDelete + '?'), tabPane.getScene().getWindow());
                    if (confirm && !directoryToDelete.isEmpty()) {
                        ProgramSettingsController.removeDirectory(directoryToDelete);
                        ProgramSettingsController.setMainDirectoryVersion(ProgramSettingsController.getMainDirectoryVersion() + 1, true);
                        log.info("Directory has been deleted!");
                    } else log.info("No directory has been deleted.");
                }
            }
            log.info("Remove Directory Finished!");
        });
        forceRecheck.setOnAction(e -> {
            Task<Void> task = new Task<Void>() {
                @SuppressWarnings("ReturnOfNull")
                @Override
                protected Void call() throws Exception {
                    CheckShowFiles.recheckShowFile(true);
                    return null;
                }
            };
            new Thread(task).start();
        });
        openProgramFolder.setOnAction(e -> {
            File file = new File(Variables.dataFolder);
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

        if (!Variables.devMode) {
            developerTab.setDisable(true);
            tabPane.getTabs().remove(3);
        }

        // Developer
        printAllShows.setOnAction(e -> ShowInfoController.printOutAllShowsAndEpisodes());
        printAllDirectories.setOnAction(e -> ProgramSettingsController.printAllDirectories());
        printEmptyShowFolders.setOnAction(e -> {
            ArrayList<String> emptyShows = CheckShowFiles.getEmptyShows();
            log.info("Printing empty shows:");
            if (emptyShows.isEmpty()) log.info("No empty shows");
            else {
                ArrayList<String> directories = ProgramSettingsController.getDirectories();
                directories.forEach(aDirectory -> {
                    Set<String> shows = ShowInfoController.getDirectoryHashMap(ProgramSettingsController.getDirectoryIndex(aDirectory)).keySet();
                    ArrayList<String> emptyShowsDir = new ArrayList<>();
                    emptyShows.forEach(aShow -> {
                        String fileString = (aDirectory + Strings.FileSeparator + aShow);
                        if (new FileManager().checkFolderExists(fileString) && !shows.contains(aShow)) {
                            emptyShowsDir.add(aShow);
                        }
                    });
                    log.info("Empty shows in \"" + aDirectory + "\": " + emptyShowsDir);
                });
            }
            log.info("Finished printing empty shows.");
        });
        printIgnoredShows.setOnAction(e -> {
            log.info("Printing ignored shows:");
            ArrayList<String> ignoredShows = UserInfoController.getIgnoredShows();
            if (ignoredShows.isEmpty()) log.info("No ignored shows.");
            else ignoredShows.forEach(log::info);
            log.info("Finished printing ignored shows.");
        });
        printHiddenShows.setOnAction(e -> {
            log.info("Printing hidden shows:");
            ArrayList<String> hiddenShows = UserInfoController.getHiddenShows();
            if (hiddenShows.isEmpty()) log.info("No hidden shows.");
            else hiddenShows.forEach(log::info);

            log.info("Finished printing hidden shows.");
        });
        unHideAll.setOnAction(e -> {
            log.info("Un-hiding all shows...");
            ArrayList<String> ignoredShows = UserInfoController.getHiddenShows();
            if (ignoredShows.isEmpty()) log.info("No shows to un-hide.");
            else {
                ignoredShows.forEach(aShow -> {
                    log.info(aShow + " is no longer hidden.");
                    UserInfoController.setHiddenStatus(aShow, false);
                });
            }
            log.info("Finished un-hiding all shows.");
        });
        setAllActive.setOnAction(e -> {
            ArrayList<String> showsList = ShowInfoController.getShowsList();
            if (showsList.isEmpty()) log.info("No shows to change.");
            else {
                showsList.forEach(aShow -> {
                    UserInfoController.setActiveStatus(aShow, true);
                    Controller.updateShowField(aShow, true);
                });
                log.info("Set all shows active.");
            }
        });
        setAllInactive.setOnAction(e -> {
            ArrayList<String> showsList = ShowInfoController.getShowsList();
            if (showsList.isEmpty()) log.info("No shows to change.");
            else {
                showsList.forEach(aShow -> {
                    UserInfoController.setActiveStatus(aShow, false);
                    Controller.updateShowField(aShow, true);
                });
                log.info("Set all shows inactive.");
            }
        });

        // Dev 2
        printProgramSettingsFileVersion.setOnAction(e -> log.info(String.valueOf(ProgramSettingsController.getProgramSettingsVersion())));
        printUserSettingsFileVersion.setOnAction(e -> log.info(String.valueOf(UserInfoController.getUserSettingsVersion())));
        printAllUserInfo.setOnAction(e -> UserInfoController.printAllUserInfo());
        add1ToDirectoryVersion.setOnAction(e -> ProgramSettingsController.setMainDirectoryVersion(ProgramSettingsController.getMainDirectoryVersion() + 1, true));
        clearFile.setOnAction(e -> {
            ArrayList<File> directories = new ArrayList<>();
            directories.addAll(ProgramSettingsController.getDirectories().stream().map(File::new).collect(Collectors.toList()));
            if (directories.isEmpty()) {
                MessageBox messageBox = new MessageBox();
                messageBox.display("There are no directories to clear.", tabPane.getScene().getWindow());
            } else {
                ListSelectBox listSelectBox = new ListSelectBox();
                String directoryToClear = String.valueOf(listSelectBox.directories("Directory to Clear:", directories, tabPane.getScene().getWindow()));
                if (directoryToClear != null) {
                    ConfirmBox confirmBox = new ConfirmBox();
                    Boolean confirm = confirmBox.display(("Are you sure to want to clear " + directoryToClear + '?'), tabPane.getScene().getWindow());
                    if (confirm && !directoryToClear.isEmpty()) {
                        int index = ProgramSettingsController.getDirectories().indexOf(directoryToClear);
                        ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = ShowInfoController.getDirectoriesHashMaps(index);
                        ShowInfoController.getDirectoryHashMap(index).keySet().forEach(aShow -> {
                            Boolean showExistsElsewhere = ShowInfoController.doesShowExistElsewhere(aShow, showsFileArray);
                            if (!showExistsElsewhere) {
                                UserInfoController.setIgnoredStatus(aShow, true);
                            }
                        });
                        HashMap<String, HashMap<Integer, HashMap<String, String>>> blankHashMap = new HashMap<>();
                        ShowInfoController.saveShowsHashMapFile(blankHashMap, index);
                        ProgramSettingsController.setMainDirectoryVersion(ProgramSettingsController.getMainDirectoryVersion() + 1, true);
                    }
                }
            }
        });
        deleteEverythingAndClose.setOnAction(e -> {
            ConfirmBox confirmBox = new ConfirmBox();
            if (confirmBox.display("Are you sure? This will delete EVERYTHING!", tabPane.getScene().getWindow())) {
                Stage stage = (Stage) tabPane.getScene().getWindow();
                stage.close();
                new FileManager().deleteFolder(new File(Variables.dataFolder));
                program.Main.stop(Main.stage, true, false);
            }
        });
        deleteEverythingAndClose.setTooltip(new Tooltip("Warning, Unrecoverable!"));

        // Allow the undecorated stage to be moved.
        new MoveWindow().moveWindow(tabPane);
    }
}
