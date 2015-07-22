package com.maddogten.mtrack.FXMLControllers;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.gui.*;
import com.maddogten.mtrack.information.ProgramSettingsController;
import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.information.UserInfoController;
import com.maddogten.mtrack.information.settings.UserSettings;
import com.maddogten.mtrack.information.settings.UserShowSettings;
import com.maddogten.mtrack.information.show.Show;
import com.maddogten.mtrack.io.CheckShowFiles;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.io.MoveWindow;
import com.maddogten.mtrack.util.FirstRun;
import com.maddogten.mtrack.util.LanguageHandler;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class Settings implements Initializable {
    private static final Logger log = Logger.getLogger(Settings.class.getName());

    private ProgramSettingsController programSettingsController;
    private ShowInfoController showInfoController;
    private UserInfoController userInfoController;
    private CheckShowFiles checkShowFiles;

    @SuppressWarnings("unused")
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab mainTab;
    @FXML
    private Tab usersTab;
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
    private Button addUser;
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
    @FXML
    private Button changeLanguage;
    @FXML
    private Button toggleDevMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.programSettingsController = Controller.getProgramSettingsController();
        this.showInfoController = Controller.getShowInfoController();
        this.userInfoController = Controller.getUserInfoController();
        this.checkShowFiles = Controller.getCheckShowFiles();
        mainTab.setText(Strings.Main);
        usersTab.setText(Strings.Users);
        otherTab.setText(Strings.Other);
        developerTab.setText(Strings.Developers);
        dev1Tab.setText(Strings.Dev1);
        dev2Tab.setText(Strings.Dev2);

        // ~~~~ Buttons ~~~~ \\

        // Users
        exit.setText(Strings.ExitButtonText);
        exit.setOnAction(e -> {
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.close();
        });
        setDefaultUsername.setText(Strings.SetDefaultUser);
        setDefaultUsername.setOnAction(e -> {
            ListSelectBox listSelectBox = new ListSelectBox();
            ArrayList<String> Users = userInfoController.getAllUsers();
            String defaultUsername = listSelectBox.defaultUser(Strings.PleaseChooseADefaultUser, Users, programSettingsController.getDefaultUsername(), tabPane.getScene().getWindow());
            if (defaultUsername != null && !defaultUsername.isEmpty()) {
                programSettingsController.setDefaultUsername(defaultUsername, true);
            }
        });
        clearDefaultUsername.setText(Strings.Reset);
        clearDefaultUsername.setOnAction(e -> programSettingsController.setDefaultUsername(Strings.EmptyString, false));
        addUser.setText(Strings.AddUser);
        addUser.setOnAction(e -> {
            String userName = new TextBox().display(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, userInfoController.getAllUsers(), tabPane.getScene().getWindow());
            Map<String, UserShowSettings> showSettings = new HashMap<>();
            ArrayList<String> showsList = showInfoController.getShowsList();
            for (String aShow : showsList) {
                int lowestSeason = showInfoController.findLowestSeason(aShow);
                showSettings.put(aShow, new UserShowSettings(aShow, showInfoController.findLowestSeason(aShow), showInfoController.findLowestEpisode(showInfoController.getEpisodesList(aShow, lowestSeason))));
            }
            new FileManager().save(new UserSettings(userName, showSettings), Variables.UsersFolder, userName, Variables.UsersExtension, false);
        });
        deleteUser.setText(Strings.DeleteUser);
        deleteUser.setOnAction(e -> {
            ArrayList<String> users = userInfoController.getAllUsers();
            users.remove(Strings.UserName);
            if (users.isEmpty()) {
                MessageBox messageBox = new MessageBox();
                messageBox.display(Strings.ThereAreNoOtherUsersToDelete, tabPane.getScene().getWindow());
            } else {
                ListSelectBox listSelectBox = new ListSelectBox();
                String userToDelete = listSelectBox.defaultUser(Strings.UserToDelete, users, programSettingsController.getDefaultUsername(), tabPane.getScene().getWindow());
                if (userToDelete != null) {
                    ConfirmBox confirmBox = new ConfirmBox();
                    boolean confirm = confirmBox.display((Strings.AreYouSureToWantToDelete + userToDelete + Strings.QuestionMark), tabPane.getScene().getWindow());
                    if (confirm && !userToDelete.isEmpty()) {
                        new FileManager().deleteFile(Variables.UsersFolder, userToDelete, Variables.UsersExtension);
                    }
                }
            }
        });
        deleteUser.setTooltip(new Tooltip(Strings.DeleteUsersNoteCantDeleteCurrentUser));

        // Other
        changeUpdateSpeed.setText(Strings.ChangeUpdateTime);
        changeUpdateSpeed.setOnAction(e -> {
            log.info("Change update speed started:");
            programSettingsController.setUpdateSpeed(new TextBox().updateSpeed(Strings.EnterHowFastYouWantItToScanTheShowsFolders, Strings.LeaveItAsIs, Variables.defaultUpdateSpeed, programSettingsController.getUpdateSpeed(), tabPane.getScene().getWindow()));
        });
        addDirectory.setText(Strings.AddDirectory);
        addDirectory.setOnAction(e -> {
            int index = programSettingsController.getLowestFreeDirectoryIndex();
            boolean[] wasAdded = programSettingsController.addDirectory(index, new TextBox().addDirectoriesDisplay(Strings.PleaseEnterShowsDirectory, programSettingsController.getDirectories(), Strings.YouNeedToEnterADirectory, Strings.DirectoryIsInvalid, tabPane.getScene().getWindow()));
            if (wasAdded[0]) {
                log.info("Directory was added.");
                File directory = programSettingsController.getDirectory(index);
                final boolean[] taskRunning = {true};
                Task<Void> task = new Task<Void>() {
                    @SuppressWarnings("ReturnOfNull")
                    @Override
                    protected Void call() throws Exception {
                        new FirstRun(programSettingsController, showInfoController, userInfoController).generateShowsFile(index, directory);
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
                showInfoController.loadShowsFile();
                Map<String, Show> showsFile = showInfoController.getDirectoryMap(index);
                showsFile.keySet().forEach(aShow -> {
                    userInfoController.addNewShow(aShow);
                    Controller.updateShowField(aShow, true);
                });
                programSettingsController.setMainDirectoryVersion(programSettingsController.getMainDirectoryVersion() + 1);
            } else log.info("Directory wasn't added.");
        });
        removeDirectory.setText(Strings.RemoveDirectory);
        removeDirectory.setOnAction(e -> {
            log.info("Remove Directory Started:");
            ArrayList<File> directories = new ArrayList<>();
            directories.addAll(programSettingsController.getDirectories().stream().map(File::new).collect(Collectors.toList()));
            if (directories.isEmpty()) {
                log.info("No directories to delete.");
                MessageBox messageBox = new MessageBox();
                messageBox.display(Strings.ThereAreNoDirectoriesToDelete, tabPane.getScene().getWindow());
            } else {
                ListSelectBox listSelectBox = new ListSelectBox();
                String directoryToDelete = String.valueOf(listSelectBox.directories(Strings.DirectoryToDelete, directories, tabPane.getScene().getWindow()));
                if (directoryToDelete != null && !directoryToDelete.isEmpty()) {
                    log.info("Directory selected for deletion: " + directoryToDelete);
                    ConfirmBox confirmBox = new ConfirmBox();
                    boolean confirm = confirmBox.display((Strings.AreYouSureToWantToDelete + directoryToDelete + Strings.QuestionMark), tabPane.getScene().getWindow());
                    if (confirm && !directoryToDelete.isEmpty()) {
                        programSettingsController.removeDirectory(directoryToDelete);
                        programSettingsController.setMainDirectoryVersion(programSettingsController.getMainDirectoryVersion() + 1);
                        log.info("Directory has been deleted!");
                    } else log.info("No directory has been deleted.");
                }
            }
            log.info("Remove Directory Finished!");
        });
        changeLanguage.setText(Strings.ChangeLanguage);
        changeLanguage.setOnAction(e -> {
            LanguageHandler languageHandler = new LanguageHandler();
            Map<String, String> languages = languageHandler.getLanguageNames();
            if (languages.containsKey(programSettingsController.getLanguage())) {
                languages.remove(programSettingsController.getLanguage());
            }
            String languageReadable = new ListSelectBox().pickLanguage(Strings.PleaseChooseYourLanguage, languages.values(), tabPane.getScene().getWindow());
            if (!languageReadable.contains("-1")) {
                String internalName = Strings.EmptyString;
                for (String langKey : languages.keySet()) {
                    if (languages.get(langKey).matches(languageReadable)) {
                        internalName = langKey;
                        break;
                    }
                }
                programSettingsController.setLanguage(internalName);
                new MessageBox().display(Strings.RestartTheProgramForTheNewLanguageToTakeEffect, tabPane.getScene().getWindow());
            }
        });
        forceRecheck.setText(Strings.ForceRecheckShows);
        forceRecheck.setOnAction(e -> {
            Task<Void> task = new Task<Void>() {
                @SuppressWarnings("ReturnOfNull")
                @Override
                protected Void call() throws Exception {
                    checkShowFiles.recheckShowFile(true);
                    return null;
                }
            };
            new Thread(task).start();
        });
        if (Variables.showOptionToToggleDevMode) {
            toggleDevMode.setText(Strings.ToggleDevMode);
            toggleDevMode.setOnAction(e -> Variables.devMode = !Variables.devMode);
        } else toggleDevMode.setDisable(true);
        openProgramFolder.setText(Strings.OpenSettingsFolder);
        openProgramFolder.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(Variables.dataFolder);
            } catch (IOException e1) {
                log.severe(e1.toString());
            }
        });
        about.setText(Strings.About);
        about.setOnAction(e -> {
            AboutBox aboutBox = new AboutBox();
            try {
                aboutBox.display(tabPane.getScene().getWindow());
            } catch (Exception e1) {
                log.info(Arrays.toString(e1.getStackTrace()));
            }
        });

        //noinspection PointlessBooleanExpression
        if (!Variables.devMode) {
            developerTab.setDisable(true);
            tabPane.getTabs().remove(3);
        }

        // Developer
        printAllShows.setText(Strings.PrintAllShowInfo);
        printAllShows.setOnAction(e -> showInfoController.printOutAllShowsAndEpisodes());
        printAllDirectories.setText(Strings.PrintAllDirectories);
        printAllDirectories.setOnAction(e -> programSettingsController.printAllDirectories());
        printEmptyShowFolders.setText(Strings.PrintEmptyShows);
        printEmptyShowFolders.setOnAction(e -> {
            ArrayList<String> emptyShows = checkShowFiles.getEmptyShows();
            log.info("Printing empty shows:");
            if (emptyShows.isEmpty()) log.info("No empty shows");
            else {
                ArrayList<String> directories = programSettingsController.getDirectories();
                directories.forEach(aDirectory -> {
                    Set<String> shows = showInfoController.getDirectoryMap(programSettingsController.getDirectoryIndex(aDirectory)).keySet();
                    ArrayList<String> emptyShowsDir = new ArrayList<>();
                    emptyShows.forEach(aShow -> {
                        if (new FileManager().checkFolderExists(new File(aDirectory + Strings.FileSeparator + aShow)) && !shows.contains(aShow)) {
                            emptyShowsDir.add(aShow);
                        }
                    });
                    log.info("Empty shows in \"" + aDirectory + "\": " + emptyShowsDir);
                });
            }
            log.info("Finished printing empty shows.");
        });
        printIgnoredShows.setText(Strings.PrintIgnoredShows);
        printIgnoredShows.setOnAction(e -> {
            log.info("Printing ignored shows:");
            ArrayList<String> ignoredShows = userInfoController.getIgnoredShows();
            if (ignoredShows.isEmpty()) log.info("No ignored shows.");
            else ignoredShows.forEach(log::info);
            log.info("Finished printing ignored shows.");
        });
        printHiddenShows.setText(Strings.PrintHiddenShows);
        printHiddenShows.setOnAction(e -> {
            log.info("Printing hidden shows:");
            ArrayList<String> hiddenShows = userInfoController.getHiddenShows();
            if (hiddenShows.isEmpty()) log.info("No hidden shows.");
            else hiddenShows.forEach(log::info);

            log.info("Finished printing hidden shows.");
        });
        unHideAll.setText(Strings.UnHideAll);
        unHideAll.setOnAction(e -> {
            log.info("Un-hiding all shows...");
            ArrayList<String> ignoredShows = userInfoController.getHiddenShows();
            if (ignoredShows.isEmpty()) log.info("No shows to un-hide.");
            else {
                ignoredShows.forEach(aShow -> {
                    log.info(aShow + " is no longer hidden.");
                    userInfoController.setHiddenStatus(aShow, false);
                });
            }
            log.info("Finished un-hiding all shows.");
        });
        setAllActive.setText(Strings.SetAllActive);
        setAllActive.setOnAction(e -> {
            ArrayList<String> showsList = showInfoController.getShowsList();
            if (showsList.isEmpty()) log.info("No shows to change.");
            else {
                showsList.forEach(aShow -> {
                    userInfoController.setActiveStatus(aShow, true);
                    Controller.updateShowField(aShow, true);
                });
                log.info("Set all shows active.");
            }
        });
        setAllInactive.setText(Strings.SetAllInactive);
        setAllInactive.setOnAction(e -> {
            ArrayList<String> showsList = showInfoController.getShowsList();
            if (showsList.isEmpty()) log.info("No shows to change.");
            else {
                showsList.forEach(aShow -> {
                    userInfoController.setActiveStatus(aShow, false);
                    Controller.updateShowField(aShow, true);
                });
                log.info("Set all shows inactive.");
            }
        });

        // Dev 2
        printProgramSettingsFileVersion.setText(Strings.PrintPSFV);
        printProgramSettingsFileVersion.setOnAction(e -> log.info(String.valueOf(programSettingsController.getProgramSettingsFileVersion())));
        printUserSettingsFileVersion.setText(Strings.PrintUSFV);
        printUserSettingsFileVersion.setOnAction(e -> log.info(String.valueOf(userInfoController.getUserSettingsVersion())));
        printAllUserInfo.setText(Strings.PrintAllUserInfo);
        printAllUserInfo.setOnAction(e -> userInfoController.printAllUserInfo());
        add1ToDirectoryVersion.setText(Strings.DirectoryVersionPlus1);
        add1ToDirectoryVersion.setOnAction(e -> programSettingsController.setMainDirectoryVersion(programSettingsController.getMainDirectoryVersion() + 1));
        clearFile.setText(Strings.ClearFile);
        clearFile.setOnAction(e -> {
            ArrayList<File> directories = new ArrayList<>();
            directories.addAll(programSettingsController.getDirectories().stream().map(File::new).collect(Collectors.toList()));
            if (directories.isEmpty()) {
                MessageBox messageBox = new MessageBox();
                messageBox.display(Strings.ThereAreNoDirectoriesToClear, tabPane.getScene().getWindow());
            } else {
                ListSelectBox listSelectBox = new ListSelectBox();
                String directoryToClear = String.valueOf(listSelectBox.directories(Strings.DirectoryToClear, directories, tabPane.getScene().getWindow()));
                if (directoryToClear != null) {
                    ConfirmBox confirmBox = new ConfirmBox();
                    boolean confirm = confirmBox.display((Strings.AreYouSureToWantToClear + directoryToClear + Strings.QuestionMark), tabPane.getScene().getWindow());
                    if (confirm && !directoryToClear.isEmpty()) {
                        int index = programSettingsController.getDirectories().indexOf(directoryToClear);
                        ArrayList<Map<String, Show>> showsFileArray = showInfoController.getDirectoriesMaps(index);
                        showInfoController.getDirectoryMap(index).keySet().forEach(aShow -> {
                            boolean showExistsElsewhere = showInfoController.doesShowExistElsewhere(aShow, showsFileArray);
                            if (!showExistsElsewhere) {
                                userInfoController.setIgnoredStatus(aShow, true);
                            }
                        });
                        Map<String, Show> blankMap = new HashMap<>();
                        showInfoController.saveShowsMapFile(blankMap, index, true);
                        programSettingsController.setMainDirectoryVersion(programSettingsController.getMainDirectoryVersion() + 1);
                    }
                }
            }
        });
        deleteEverythingAndClose.setText(Strings.ResetProgram);
        deleteEverythingAndClose.setOnAction(e -> {
            ConfirmBox confirmBox = new ConfirmBox();
            if (confirmBox.display(Strings.AreYouSureThisWillDeleteEverything, tabPane.getScene().getWindow())) {
                Stage stage = (Stage) tabPane.getScene().getWindow();
                stage.close();
                new FileManager().deleteFolder(Variables.dataFolder);
                Main.stop(Main.stage, true, false);
            }
        });
        deleteEverythingAndClose.setTooltip(new Tooltip(Strings.WarningUnrecoverable));

        // Allow the undecorated stage to be moved.
        new MoveWindow().moveWindow(tabPane, Main.stage);
    }
}
