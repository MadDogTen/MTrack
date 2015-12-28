package com.maddogten.mtrack.FXMLControllers;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.gui.*;
import com.maddogten.mtrack.information.*;
import com.maddogten.mtrack.information.settings.UserSettings;
import com.maddogten.mtrack.information.settings.UserShowSettings;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.io.CheckShowFiles;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

/*
      Settings handles pretty much ALL the programs settings.
 */

@SuppressWarnings("WeakerAccess")
public class Settings implements Initializable {
    private static final Logger log = Logger.getLogger(Settings.class.getName());
    private static Settings settings;
    private ProgramSettingsController programSettingsController;
    private ShowInfoController showInfoController;
    private UserInfoController userInfoController;
    private DirectoryController directoryController;
    private CheckShowFiles checkShowFiles;
    @SuppressWarnings("unused")
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

    public static Settings getSettings() {
        return settings;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setSettings();
        this.programSettingsController = Controller.getProgramSettingsController();
        this.showInfoController = Controller.getShowInfoController();
        this.userInfoController = Controller.getUserInfoController();
        this.directoryController = Controller.getDirectoryController();
        this.checkShowFiles = Controller.getCheckShowFiles();
        mainTab.textProperty().bind(Strings.Main);
        usersTab.textProperty().bind(Strings.Users);
        showTab.textProperty().bind(Strings.Show);
        otherTab.textProperty().bind(Strings.Other);
        uiTab.textProperty().bind(Strings.UI);
        developerTab.textProperty().bind(Strings.Developers);
        dev1Tab.textProperty().bind(Strings.Dev1);
        dev2Tab.textProperty().bind(Strings.Dev2);

        // ~~~~ Buttons ~~~~ \\

        // Main
        updateText.textProperty().bind(Strings.UpdateTime);
        updateText.setTextAlignment(TextAlignment.CENTER);
        updateTimeTextField.setText(String.valueOf(Variables.updateSpeed));
        setUpdateTime.textProperty().bind(Strings.Set);
        setUpdateTime.setOnAction(e -> {
            if (isNumberValid(updateTimeTextField.getText(), 10)) {
                if (updateTimeTextField.getText().isEmpty()) {
                    updateTimeTextField.setText(String.valueOf(Variables.updateSpeed));
                } else programSettingsController.setUpdateSpeed(Integer.valueOf(updateTimeTextField.getText()));
            }
        });
        disableAutomaticShowUpdating.textProperty().bind(Strings.DisableAutomaticShowSearching);
        if (Variables.forceDisableAutomaticRechecking) {
            disableAutomaticShowUpdating.setSelected(true);
            disableAutomaticShowUpdating.setDisable(true);
        } else
            disableAutomaticShowUpdating.setSelected(programSettingsController.getSettingsFile().isDisableAutomaticShowUpdating());
        disableAutomaticShowUpdating.setOnAction(e -> {
            programSettingsController.getSettingsFile().setDisableAutomaticShowUpdating(!programSettingsController.getSettingsFile().isDisableAutomaticShowUpdating());
            log.info("Disable automatic show checking has been set to: " + programSettingsController.getSettingsFile().isDisableAutomaticShowUpdating());
        });
        notifyChangesText.textProperty().bind(Strings.NotifyChangesFor);
        notifyChangesText.setTextAlignment(TextAlignment.CENTER);
        onlyChecksEveryText.textProperty().bind(Strings.OnlyChecksEveryRuns);
        onlyChecksEveryText.setTextAlignment(TextAlignment.CENTER);
        inactiveShowsCheckBox.textProperty().bind(Strings.InactiveShows);
        inactiveShowsCheckBox.setSelected(programSettingsController.getSettingsFile().isRecordChangesForNonActiveShows());
        inactiveShowsCheckBox.setOnAction(e -> {
            programSettingsController.getSettingsFile().setRecordChangesForNonActiveShows(!programSettingsController.getSettingsFile().isRecordChangesForNonActiveShows());
            log.info("Record inactive shows has been set to: " + programSettingsController.getSettingsFile().isRecordChangesForNonActiveShows());
        });
        olderSeasonsCheckBox.textProperty().bind(Strings.OlderSeasons);
        olderSeasonsCheckBox.setSelected(programSettingsController.getSettingsFile().isRecordChangedSeasonsLowerThanCurrent());
        olderSeasonsCheckBox.setOnAction(e -> {
            programSettingsController.getSettingsFile().setRecordChangedSeasonsLowerThanCurrent(!programSettingsController.getSettingsFile().isRecordChangedSeasonsLowerThanCurrent());
            log.info("Record older seasons has been set to: " + programSettingsController.getSettingsFile().isRecordChangedSeasonsLowerThanCurrent());
        });
        about.textProperty().bind(Strings.About);
        about.setOnAction(e -> {
            try {
                new AboutBox().display(tabPane.getScene().getWindow());
            } catch (Exception e1) {
                GenericMethods.printStackTrace(log, e1);
            }
        });

        // User
        exit.setOnAction(e -> {
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.close();
        });
        setDefaultUsername.textProperty().bind(Strings.SetDefaultUser);
        setDefaultUsername.setOnAction(e -> {
            ListSelectBox listSelectBox = new ListSelectBox();
            ArrayList<String> Users = userInfoController.getAllUsers();
            String defaultUsername = listSelectBox.pickDefaultUser(Strings.PleaseChooseADefaultUser, Users, programSettingsController.getSettingsFile().getDefaultUser(), tabPane.getScene().getWindow());
            if (defaultUsername != null && !defaultUsername.isEmpty()) {
                programSettingsController.setDefaultUsername(defaultUsername, true);
            }
        });
        clearDefaultUsername.textProperty().bind(Strings.Reset);
        clearDefaultUsername.setOnAction(e -> programSettingsController.setDefaultUsername(Strings.EmptyString, false));
        addUser.textProperty().bind(Strings.AddUser);
        addUser.setOnAction(e -> {
            String userName = new TextBox().addUser(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, userInfoController.getAllUsers(), tabPane.getScene().getWindow());
            Map<String, UserShowSettings> showSettings = new HashMap<>();
            ArrayList<String> showsList = showInfoController.getShowsList();
            for (String aShow : showsList) {
                int lowestSeason = showInfoController.findLowestSeason(aShow);
                showSettings.put(aShow, new UserShowSettings(aShow, showInfoController.findLowestSeason(aShow), showInfoController.findLowestEpisode(showInfoController.getEpisodesList(aShow, lowestSeason))));
            }
            new FileManager().save(new UserSettings(userName, showSettings, new String[0], programSettingsController.getSettingsFile().getProgramSettingsID()), Variables.UsersFolder, userName, Variables.UsersExtension, false);
        });
        deleteUser.textProperty().bind(Strings.DeleteUser);
        deleteUser.setOnAction(e -> {
            ArrayList<String> users = userInfoController.getAllUsers();
            users.remove(Strings.UserName);
            if (users.isEmpty()) {
                new MessageBox().display(new StringProperty[]{Strings.ThereAreNoOtherUsersToDelete}, tabPane.getScene().getWindow());
            } else {
                ListSelectBox listSelectBox = new ListSelectBox();
                String userToDelete = listSelectBox.pickDefaultUser(Strings.UserToDelete, users, programSettingsController.getSettingsFile().getDefaultUser(), tabPane.getScene().getWindow());
                if (userToDelete != null) {
                    ConfirmBox confirmBox = new ConfirmBox();
                    boolean confirm = confirmBox.display(new SimpleStringProperty(Strings.AreYouSureToWantToDelete + userToDelete + Strings.QuestionMark), tabPane.getScene().getWindow());
                    if (confirm && !userToDelete.isEmpty()) {
                        new FileManager().deleteFile(Variables.UsersFolder, userToDelete, Variables.UsersExtension);
                    }
                }
            }
        });
        deleteUserTooltip.textProperty().bind(Strings.DeleteUsersNoteCantDeleteCurrentUser);

        // Show
        forceRecheck.textProperty().bind(Strings.ForceRecheckShows);
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
        unHideShow.textProperty().bind(Strings.UnHideShow);
        unHideShow.setOnAction(e -> {
            ArrayList<String> hiddenShows = userInfoController.getHiddenShows();
            if (hiddenShows.isEmpty())
                new MessageBox().display(new StringProperty[]{Strings.ThereAreNoHiddenShows}, tabPane.getScene().getWindow());
            else {
                String showToUnHide = new ListSelectBox().pickShow(userInfoController.getHiddenShows(), tabPane.getScene().getWindow());
                if (showToUnHide != null && !showToUnHide.isEmpty()) {
                    userInfoController.setHiddenStatus(showToUnHide, false);
                    Controller.updateShowField(showToUnHide, true);
                    log.info(showToUnHide + " was unhidden.");
                } else log.info("No show was unhidden.");
            }
        });

        // Other
        addDirectory.textProperty().bind(Strings.AddDirectory);
        addDirectory.setOnAction(e -> {
            int index = directoryController.getLowestFreeDirectoryIndex();
            boolean[] wasAdded = directoryController.addDirectory(index, new TextBox().addDirectory(directoryController.getDirectories(), tabPane.getScene().getWindow()));
            if (wasAdded[0]) {
                log.info("Directory was added.");
                FindChangedShows findChangedShows = new FindChangedShows(showInfoController.getShowsFile(), userInfoController);
                Task<Void> task = new Task<Void>() {
                    @SuppressWarnings("ReturnOfNull")
                    @Override
                    protected Void call() throws Exception {
                        new FirstRun(programSettingsController, showInfoController, userInfoController, directoryController, Main.getMainRun()).generateShowsFile(directoryController.getDirectory(index));
                        return null;
                    }
                };
                Thread generateShowsFileThread = new Thread(task);
                generateShowsFileThread.start();
                try {
                    generateShowsFileThread.join();
                } catch (InterruptedException e1) {
                    GenericMethods.printStackTrace(log, e1);
                }
                showInfoController.loadShowsFile();
                findChangedShows.findShowFileDifferences(showInfoController.getShowsFile());
                Directory aDirectory = directoryController.getDirectory(index);
                aDirectory.getShows().keySet().forEach(aShow -> {
                    userInfoController.addNewShow(aShow);
                    Controller.updateShowField(aShow, true);
                });
                programSettingsController.setMainDirectoryVersion(programSettingsController.getSettingsFile().getMainDirectoryVersion() + 1);
            } else log.info("Directory wasn't added.");
        });
        removeDirectory.textProperty().bind(Strings.RemoveDirectory);
        removeDirectory.setOnAction(e -> {
            log.info("Remove Directory Started:");
            ArrayList<File> directories = new ArrayList<>();
            directoryController.getDirectories().forEach(aDirectory -> directories.add(aDirectory.getDirectory()));
            if (directories.isEmpty()) {
                log.info("No directories to delete.");
                new MessageBox().display(new StringProperty[]{Strings.ThereAreNoDirectoriesToDelete}, tabPane.getScene().getWindow());
            } else {
                ListSelectBox listSelectBox = new ListSelectBox();
                File directoryToDelete = listSelectBox.pickDirectory(Strings.DirectoryToDelete, directories, tabPane.getScene().getWindow());
                if (directoryToDelete != null && !directoryToDelete.toString().isEmpty()) {
                    log.info("Directory selected for deletion: " + directoryToDelete);
                    ConfirmBox confirmBox = new ConfirmBox();
                    boolean confirm = confirmBox.display(new SimpleStringProperty(Strings.AreYouSureToWantToDelete.getValue() + directoryToDelete + Strings.QuestionMark.getValue()), tabPane.getScene().getWindow());
                    if (confirm) {
                        for (Directory directory : directoryController.getDirectories()) {
                            if (directory.getDirectory().equals(directoryToDelete)) {
                                int index = directory.getIndex();
                                ArrayList<Directory> showsFileArray = directoryController.getDirectories(index);
                                Set<String> showsSet = directoryController.getDirectory(index).getShows().keySet();
                                showsSet.forEach(aShow -> {
                                    log.info("Currently checking: " + aShow);
                                    boolean showExistsElsewhere = showInfoController.doesShowExistElsewhere(aShow, showsFileArray);
                                    if (!showExistsElsewhere) {
                                        userInfoController.setIgnoredStatus(aShow, true);
                                        ChangeReporter.addChange("- " + aShow);
                                    }
                                    Controller.updateShowField(aShow, showExistsElsewhere);
                                });
                                directoryController.removeDirectory(directory);
                                programSettingsController.setMainDirectoryVersion(programSettingsController.getSettingsFile().getMainDirectoryVersion() + 1);
                                break;
                            }
                        }
                        log.info("\"" + directoryToDelete + "\" has been deleted!");
                    } else log.info("No directory has been deleted.");
                }
            }
            log.info("Remove Directory Finished!");
        });
        directoryTimeoutText.textProperty().bind(Strings.DirectoryTimeout);
        directoryTimeoutText.setTextAlignment(TextAlignment.CENTER);
        directoryTimeoutTextField.setText(String.valueOf(Variables.timeToWaitForDirectory));
        setDirectoryTimeout.textProperty().bind(Strings.Set);
        setDirectoryTimeout.setOnAction(e -> {
            if (isNumberValid(directoryTimeoutTextField.getText(), 2)) {
                if (directoryTimeoutTextField.getText().isEmpty()) {
                    directoryTimeoutTextField.setText(String.valueOf(Variables.timeToWaitForDirectory));
                } else
                    programSettingsController.setTimeToWaitForDirectory(Integer.valueOf(directoryTimeoutTextField.getText()));
            }
        });
        if (Variables.showOptionToToggleDevMode) {
            if (Variables.devMode) {
                toggleDevMode.setSelected(true);
            }
            toggleDevMode.textProperty().bind(Strings.ToggleDevMode);
            toggleDevMode.setOnAction(e -> {
                Variables.devMode = !Variables.devMode;
                if (Variables.devMode) {
                    developerTab.setDisable(false);
                    tabPane.getTabs().add(developerTab);
                } else {
                    developerTab.setDisable(true);
                    tabPane.getTabs().remove(developerTab);
                }
            });
        } else toggleDevMode.setVisible(false);
        openProgramFolder.textProperty().bind(Strings.OpenSettingsFolder);
        openProgramFolder.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(Variables.dataFolder);
            } catch (IOException e1) {
                GenericMethods.printStackTrace(log, e1);
            }
        });
        //noinspection PointlessBooleanExpression
        if (!Variables.devMode) {
            developerTab.setDisable(true);
            tabPane.getTabs().remove(developerTab);
        }

        // UI
        unlockParentScene.textProperty().bind(Strings.AllowFullWindowMovementUse);
        unlockParentScene.setSelected(!programSettingsController.getSettingsFile().isStageMoveWithParentAndBlockParent());
        unlockParentScene.setOnAction(e -> {
            programSettingsController.getSettingsFile().setStageMoveWithParentAndBlockParent(!programSettingsController.getSettingsFile().isStageMoveWithParentAndBlockParent());
            Variables.setStageMoveWithParentAndBlockParent(programSettingsController.getSettingsFile().isStageMoveWithParentAndBlockParent());
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.close();
            Platform.runLater(() -> Controller.openSettingsWindow(4));
            log.info("MoveAndBlock has been set to: " + programSettingsController.getSettingsFile().isStageMoveWithParentAndBlockParent());
        });
        changeLanguage.textProperty().bind(Strings.ChangeLanguage);
        changeLanguage.setOnAction(e -> {
            LanguageHandler languageHandler = new LanguageHandler();
            Map<String, String> languages = languageHandler.getLanguageNames();
            if (languages.containsKey(programSettingsController.getSettingsFile().getLanguage())) {
                languages.remove(programSettingsController.getSettingsFile().getLanguage());
            }
            Object[] pickLanguageResult = new ListSelectBox().pickLanguage(languages.values(), tabPane.getScene().getWindow());
            String languageReadable = (String) pickLanguageResult[0];
            if (!languageReadable.contains("-2")) {
                String internalName = Strings.EmptyString;
                for (String langKey : languages.keySet()) {
                    if (languages.get(langKey).matches(languageReadable)) {
                        internalName = langKey;
                        break;
                    }
                }
                programSettingsController.setDefaultLanguage(internalName);
                Variables.language = programSettingsController.getSettingsFile().getLanguage();
                languageHandler.setLanguage(Variables.language);
            }
        });

        // Developer
        printAllShows.textProperty().bind(Strings.PrintAllShowInfo);
        printAllShows.setOnAction(e -> showInfoController.printOutAllShowsAndEpisodes());
        printAllDirectories.textProperty().bind(Strings.PrintAllDirectories);
        printAllDirectories.setOnAction(e -> directoryController.printAllDirectories());
        printEmptyShowFolders.textProperty().bind(Strings.PrintEmptyShows);
        printEmptyShowFolders.setOnAction(e -> {
            ArrayList<String> emptyShows = checkShowFiles.getEmptyShows();
            log.info("Printing empty shows:");
            if (emptyShows.isEmpty()) log.info("No empty shows");
            else {
                ArrayList<Directory> directories = directoryController.getDirectories();
                directories.forEach(aDirectory -> {
                    ArrayList<String> emptyShowsDir = new ArrayList<>();
                    emptyShows.forEach(aShow -> {
                        if (new FileManager().checkFolderExistsAndReadable(new File(aDirectory + Strings.FileSeparator + aShow)) && !aDirectory.getShows().containsKey(aShow)) {
                            emptyShowsDir.add(aShow);
                        }
                    });
                    log.info("Empty shows in \"" + aDirectory + "\": " + emptyShowsDir);
                });
            }
            log.info("Finished printing empty shows.");
        });
        printIgnoredShows.textProperty().bind(Strings.PrintIgnoredShows);
        printIgnoredShows.setOnAction(e -> {
            log.info("Printing ignored shows:");
            ArrayList<String> ignoredShows = userInfoController.getIgnoredShows();
            if (ignoredShows.isEmpty()) log.info("No ignored shows.");
            else ignoredShows.forEach(log::info);
            log.info("Finished printing ignored shows.");
        });
        printHiddenShows.textProperty().bind(Strings.PrintHiddenShows);
        printHiddenShows.setOnAction(e -> {
            log.info("Printing hidden shows:");
            ArrayList<String> hiddenShows = userInfoController.getHiddenShows();
            if (hiddenShows.isEmpty()) log.info("No hidden shows.");
            else hiddenShows.forEach(log::info);

            log.info("Finished printing hidden shows.");
        });
        unHideAll.textProperty().bind(Strings.UnHideAll);
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
        setAllActive.textProperty().bind(Strings.SetAllActive);
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
        setAllInactive.textProperty().bind(Strings.SetAllInactive);
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
        printProgramSettingsFileVersion.textProperty().bind(Strings.PrintPSFV);
        printProgramSettingsFileVersion.setOnAction(e -> log.info(String.valueOf(programSettingsController.getSettingsFile().getProgramSettingsFileVersion())));
        printUserSettingsFileVersion.textProperty().bind(Strings.PrintUSFV);
        printUserSettingsFileVersion.setOnAction(e -> log.info(String.valueOf(userInfoController.getUserSettings().getUserSettingsFileVersion())));
        printAllUserInfo.textProperty().bind(Strings.PrintAllUserInfo);
        printAllUserInfo.setOnAction(e -> userInfoController.printAllUserInfo());
        add1ToDirectoryVersion.textProperty().bind(Strings.DirectoryVersionPlus1);
        add1ToDirectoryVersion.setOnAction(e -> programSettingsController.setMainDirectoryVersion(programSettingsController.getSettingsFile().getMainDirectoryVersion() + 1));
        clearFile.textProperty().bind(Strings.ClearFile);
        clearFile.setOnAction(e -> {
            ArrayList<File> directories = new ArrayList<>();
            directoryController.getDirectories().forEach(aDirectory -> directories.add(aDirectory.getDirectory()));
            if (directories.isEmpty()) {
                new MessageBox().display(new StringProperty[]{Strings.ThereAreNoDirectoriesToClear}, tabPane.getScene().getWindow());
            } else {
                ListSelectBox listSelectBox = new ListSelectBox();
                String directoryToClear = String.valueOf(listSelectBox.pickDirectory(Strings.DirectoryToClear, directories, tabPane.getScene().getWindow()));
                if (directoryToClear != null) {
                    ConfirmBox confirmBox = new ConfirmBox();
                    boolean confirm = confirmBox.display(new SimpleStringProperty(Strings.AreYouSureToWantToClear.getValue() + directoryToClear + Strings.QuestionMark.getValue()), tabPane.getScene().getWindow());
                    if (confirm && !directoryToClear.isEmpty()) {
                        for (Directory aDirectory : directoryController.getDirectories(-2)) {
                            if (aDirectory.getDirectory() == new File(directoryToClear)) {
                                aDirectory.getShows().keySet().forEach(aShow -> {
                                    boolean showExistsElsewhere = showInfoController.doesShowExistElsewhere(aShow, directoryController.getDirectories(aDirectory.getIndex()));
                                    if (!showExistsElsewhere) {
                                        userInfoController.setIgnoredStatus(aShow, true);
                                    }
                                });
                                aDirectory.setShows(new HashMap<>());
                                directoryController.saveDirectory(aDirectory, true);
                                programSettingsController.setMainDirectoryVersion(programSettingsController.getSettingsFile().getMainDirectoryVersion() + 1);
                                break;
                            }
                        }
                    }
                }
            }
        });
        deleteEverythingAndClose.textProperty().bind(Strings.ResetProgram);
        deleteEverythingAndCloseTooltip.textProperty().bind(Strings.WarningUnrecoverable);
        deleteEverythingAndClose.setOnAction(e -> {
            ConfirmBox confirmBox = new ConfirmBox();
            if (confirmBox.display(Strings.AreYouSureThisWillDeleteEverything, tabPane.getScene().getWindow())) {
                Stage stage = (Stage) tabPane.getScene().getWindow();
                stage.close();
                new FileManager().deleteFolder(Variables.dataFolder);
                Main.stop(Main.stage, true, false);
            }
        });
        // Allow the undecorated stage to be moved.
        new MoveStage().moveWindow(tabPane, Main.stage);
    }

    private boolean isNumberValid(String textFieldValue, int minValue) {
        log.finest("isNumberValid has been called.");
        if (textFieldValue.isEmpty()) {
            return new ConfirmBox().display(Strings.LeaveItAsIs, this.tabPane.getScene().getWindow());
        } else if (!textFieldValue.matches("^[0-9]+$") || Integer.parseInt(textFieldValue) > Variables.maxWaitTimeSeconds || Integer.parseInt(textFieldValue) < minValue) {
            new MessageBox().display(new StringProperty[]{new SimpleStringProperty(Strings.MustBeANumberBetween.getValue() + minValue + " - " + Variables.maxWaitTimeSeconds)}, this.tabPane.getScene().getWindow());
            return false;
        } else return true;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    private void setSettings() {
        settings = this;
    }
}