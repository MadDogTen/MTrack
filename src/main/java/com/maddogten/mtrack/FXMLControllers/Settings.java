package com.maddogten.mtrack.FXMLControllers;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.gui.*;
import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.information.settings.UserSettings;
import com.maddogten.mtrack.information.settings.UserShowSettings;
import com.maddogten.mtrack.information.show.Directory;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/*
      Settings handles pretty much ALL the programs settings. Goto ProgramSetting for information about which each setting does.
 */

public class Settings implements Initializable {
    private static final Logger log = Logger.getLogger(Settings.class.getName());
    private static Settings settings;
    @SuppressWarnings("unused")
    @FXML
    private Pane pane;
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
    @FXML
    private Button exportSettings;
    @FXML
    private Button importSettings;
    @FXML
    private Button nonForceRecheckShows;
    @FXML
    private CheckBox showUsername;
    @FXML
    private Button toggleIsChanges;
    @FXML
    private CheckBox automaticSaving;
    @FXML
    private Text savingText;
    @FXML
    private TextField updateSavingTextField;
    @FXML
    private Button setSavingTime;
    @FXML
    private CheckBox specialEffects;
    @FXML
    private Text currentUserText;
    @FXML
    private ComboBox<String> currentUserComboBox;


    public static Settings getSettings() {
        return settings;
    }

    private void setButtonDisable(boolean isDisable, Region... regions) {
        for (Region aRegion : regions) {
            aRegion.setDisable(isDisable);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setSettings();
        mainTab.textProperty().bind(Strings.Main);
        usersTab.textProperty().bind(Strings.Users);
        showTab.textProperty().bind(Strings.Show);
        otherTab.textProperty().bind(Strings.Other);
        uiTab.textProperty().bind(Strings.UI);
        developerTab.textProperty().bind(Strings.Developers);
        dev1Tab.textProperty().bind(Strings.Dev1);
        dev2Tab.textProperty().bind(Strings.Dev2);

        // ~~~~ Buttons ~~~~ \\

        exit.setOnAction(e -> {
            Stage stage = (Stage) tabPane.getScene().getWindow();
            if (Variables.specialEffects) GenericMethods.fadeStageOut(stage, 2, log, this.getClass());
            stage.close();
        });

        // Main
        updateText.textProperty().bind(Strings.UpdateTime);
        updateText.setTextAlignment(TextAlignment.CENTER);
        updateTimeTextField.setText(String.valueOf(Variables.updateSpeed));
        updateTimeTextField.setDisable(Variables.disableAutomaticRechecking);
        setUpdateTime.textProperty().bind(Strings.Set);
        setUpdateTime.setDisable(Variables.disableAutomaticRechecking);
        setUpdateTime.setOnAction(e -> {
            if (isNumberValid(updateTimeTextField.getText(), 10))
                ClassHandler.programSettingsController().setUpdateSpeed(Integer.valueOf(updateTimeTextField.getText()));
            else updateTimeTextField.setText(String.valueOf(Variables.updateSpeed));
        });
        disableAutomaticShowUpdating.textProperty().bind(Strings.DisableAutomaticShowSearching);
        //noinspection ConstantConditions
        disableAutomaticShowUpdating.setSelected(Variables.forceDisableAutomaticRechecking || Variables.disableAutomaticRechecking);
        disableAutomaticShowUpdating.setDisable(Variables.forceDisableAutomaticRechecking);
        disableAutomaticShowUpdating.setOnAction(e -> {
            ClassHandler.programSettingsController().getSettingsFile().setDisableAutomaticShowUpdating(!ClassHandler.programSettingsController().getSettingsFile().isDisableAutomaticShowUpdating());
            updateTimeTextField.setDisable(Variables.disableAutomaticRechecking);
            setUpdateTime.setDisable(Variables.disableAutomaticRechecking);
            log.info("Disable automatic show checking has been set to: " + Variables.disableAutomaticRechecking);
        });
        notifyChangesText.textProperty().bind(Strings.NotifyChangesFor);
        notifyChangesText.setTextAlignment(TextAlignment.CENTER);
        onlyChecksEveryText.textProperty().bind(Strings.OnlyChecksEveryRuns);
        onlyChecksEveryText.setTextAlignment(TextAlignment.CENTER);
        inactiveShowsCheckBox.textProperty().bind(Strings.InactiveShows);
        inactiveShowsCheckBox.setSelected(Variables.recordChangesForNonActiveShows);
        inactiveShowsCheckBox.setOnAction(e -> {
            ClassHandler.programSettingsController().getSettingsFile().setRecordChangesForNonActiveShows(!ClassHandler.programSettingsController().getSettingsFile().isRecordChangesForNonActiveShows());
            log.info("Record inactive shows has been set to: " + Variables.recordChangesForNonActiveShows);
        });
        olderSeasonsCheckBox.textProperty().bind(Strings.OlderSeasons);
        olderSeasonsCheckBox.setSelected(Variables.recordChangedSeasonsLowerThanCurrent);
        olderSeasonsCheckBox.setOnAction(e -> {
            ClassHandler.programSettingsController().getSettingsFile().setRecordChangedSeasonsLowerThanCurrent(!ClassHandler.programSettingsController().getSettingsFile().isRecordChangedSeasonsLowerThanCurrent());
            log.info("Record older seasons has been set to: " + Variables.recordChangedSeasonsLowerThanCurrent);
        });
        about.textProperty().bind(Strings.About);
        about.setOnAction(e -> {
            setButtonDisable(true, about);
            try {
                new AboutBox().display((Stage) tabPane.getScene().getWindow());
            } catch (Exception e1) {
                GenericMethods.printStackTrace(log, e1, this.getClass());
            }
            setButtonDisable(false, about);
        });

        // User
        currentUserText.textProperty().setValue("Current User:"); // TODO Add localization
        currentUserComboBox.getItems().addAll(ClassHandler.userInfoController().getAllUsers());
        currentUserComboBox.getSelectionModel().select(Strings.UserName.getValue());
        currentUserComboBox.setOnAction(e -> {
            if (currentUserComboBox.getValue() != null && !currentUserComboBox.getValue().matches(Strings.UserName.getValue())) {
                GenericMethods.saveSettings();
                Strings.UserName.setValue(currentUserComboBox.getValue());
                ClassHandler.mainRun().loadUser(new UpdateManager());
                Controller.setTableViewFields();
            }
        });
        setDefaultUsername.textProperty().bind(Strings.SetDefaultUser);
        setDefaultUsername.setOnAction(e -> {
            setButtonDisable(true, setDefaultUsername, clearDefaultUsername, addUser, deleteUser);
            ListSelectBox listSelectBox = new ListSelectBox();
            ArrayList<String> Users = ClassHandler.userInfoController().getAllUsers();
            String defaultUsername = listSelectBox.pickDefaultUser(Strings.PleaseChooseADefaultUser, Users, ClassHandler.programSettingsController().getSettingsFile().getDefaultUser(), (Stage) tabPane.getScene().getWindow());
            if (defaultUsername != null && !defaultUsername.isEmpty())
                ClassHandler.programSettingsController().setDefaultUsername(defaultUsername, true);
            setButtonDisable(false, setDefaultUsername, clearDefaultUsername, addUser, deleteUser);
        });
        clearDefaultUsername.textProperty().bind(Strings.Reset);
        clearDefaultUsername.setOnAction(e -> ClassHandler.programSettingsController().setDefaultUsername(Strings.EmptyString, false));
        addUser.textProperty().bind(Strings.AddUser);
        addUser.setOnAction(e -> {
            setButtonDisable(true, addUser, deleteUser, currentUserComboBox, setDefaultUsername);
            String userName = new TextBox().addUser(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, ClassHandler.userInfoController().getAllUsers(), (Stage) tabPane.getScene().getWindow());
            if (userName.isEmpty()) log.info("New user wasn't added.");
            else {
                Map<String, UserShowSettings> showSettings = new HashMap<>();
                ArrayList<String> showsList = ClassHandler.showInfoController().getShowsList();
                for (String aShow : showsList) {
                    if (Variables.genUserShowInfoAtFirstFound)
                        showSettings.put(aShow, new UserShowSettings(aShow, ClassHandler.showInfoController().findLowestSeason(aShow), ClassHandler.showInfoController().findLowestEpisode(ClassHandler.showInfoController().getEpisodesList(aShow, ClassHandler.showInfoController().findLowestSeason(aShow)))));
                    else showSettings.put(aShow, new UserShowSettings(aShow, 1, 1));
                }
                new FileManager().save(new UserSettings(userName, showSettings, true, new String[0], ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsID()), Variables.UsersFolder, userName, Variables.UserFileExtension, false);
                log.info(userName + " was added.");
            }
            currentUserComboBox.getItems().clear();
            currentUserComboBox.getItems().addAll(ClassHandler.userInfoController().getAllUsers());
            currentUserComboBox.getSelectionModel().select(Strings.UserName.getValue());
            setButtonDisable(false, addUser, deleteUser, currentUserComboBox, setDefaultUsername);
        });
        deleteUser.textProperty().bind(Strings.DeleteUser);
        deleteUser.setOnAction(e -> {
            setButtonDisable(true, deleteUser, addUser, currentUserComboBox, setDefaultUsername);
            ArrayList<String> users = ClassHandler.userInfoController().getAllUsers();
            users.remove(Strings.UserName.getValue());
            if (users.isEmpty())
                new MessageBox().message(new StringProperty[]{Strings.ThereAreNoOtherUsersToDelete}, (Stage) tabPane.getScene().getWindow());
            else {
                String userToDelete = new ListSelectBox().pickDefaultUser(Strings.UserToDelete, users, Strings.EmptyString, (Stage) tabPane.getScene().getWindow());
                if (userToDelete != null && !userToDelete.isEmpty()) {
                    boolean confirm = new ConfirmBox().confirm(new SimpleStringProperty(Strings.AreYouSureToWantToDelete.getValue() + userToDelete + Strings.QuestionMark.getValue()), (Stage) tabPane.getScene().getWindow());
                    if (confirm && !new FileManager().deleteFile(Variables.UsersFolder, userToDelete, Variables.UserFileExtension))
                        log.info("Wasn't able to delete user file.");
                }
            }
            currentUserComboBox.getItems().clear();
            currentUserComboBox.getItems().addAll(ClassHandler.userInfoController().getAllUsers());
            currentUserComboBox.getSelectionModel().select(Strings.UserName.getValue());
            setButtonDisable(false, deleteUser, addUser, currentUserComboBox, setDefaultUsername);
        });
        deleteUserTooltip.textProperty().bind(Strings.DeleteUsersNoteCantDeleteCurrentUser);

        // Show
        forceRecheck.textProperty().bind(Strings.ForceRecheckShows);
        forceRecheck.setOnAction(e -> {
            setButtonDisable(true, forceRecheck);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ClassHandler.checkShowFiles().recheckShowFile(true);
                    setButtonDisable(false, forceRecheck);
                    return null;
                }
            };
            new Thread(task).start();
        });
        unHideShow.textProperty().bind(Strings.UnHideShow);
        unHideShow.setOnAction(e -> {
            setButtonDisable(true, unHideShow);
            ArrayList<String> hiddenShows = ClassHandler.userInfoController().getHiddenShows();
            if (hiddenShows.isEmpty())
                new MessageBox().message(new StringProperty[]{Strings.ThereAreNoHiddenShows}, (Stage) tabPane.getScene().getWindow());
            else {
                String showToUnHide = new ListSelectBox().pickShow(ClassHandler.userInfoController().getHiddenShows(), (Stage) tabPane.getScene().getWindow());
                if (showToUnHide != null && !showToUnHide.isEmpty()) {
                    ClassHandler.userInfoController().setHiddenStatus(showToUnHide, false);
                    Controller.updateShowField(showToUnHide, true);
                    log.info(showToUnHide + " was unhidden.");
                } else log.info("No show was unhidden.");
            }
            setButtonDisable(false, unHideShow);
        });

        // Other
        addDirectory.textProperty().bind(Strings.AddDirectory);
        addDirectory.setOnAction(e -> {
            setButtonDisable(true, addDirectory, removeDirectory);
            int index = ClassHandler.directoryController().getLowestFreeDirectoryIndex();
            boolean[] wasAdded = ClassHandler.directoryController().addDirectory(index, new TextBox().addDirectory(Strings.PleaseEnterShowsDirectory, ClassHandler.directoryController().findDirectories(true, false, true), (Stage) tabPane.getScene().getWindow()));
            if (wasAdded[0]) {
                log.info("Directory was added.");
                FindChangedShows findChangedShows = new FindChangedShows(ClassHandler.showInfoController().getShowsFile(), ClassHandler.userInfoController());
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        new FirstRun().generateShowsFile(ClassHandler.directoryController().getDirectory(index));
                        return null;
                    }
                };
                Thread generateShowsFileThread = new Thread(task);
                generateShowsFileThread.start();
                try {
                    generateShowsFileThread.join();
                } catch (InterruptedException e1) {
                    GenericMethods.printStackTrace(log, e1, this.getClass());
                }
                ClassHandler.showInfoController().loadShowsFile(ClassHandler.directoryController().findDirectories(false, true, false));
                findChangedShows.findShowFileDifferences(ClassHandler.showInfoController().getShowsFile());
                ClassHandler.directoryController().getDirectory(index).getShows().keySet().forEach(aShow -> {
                    ClassHandler.userInfoController().addNewShow(aShow);
                    Controller.updateShowField(aShow, true);
                });
                ClassHandler.programSettingsController().setMainDirectoryVersion(ClassHandler.programSettingsController().getSettingsFile().getMainDirectoryVersion() + 1);
            } else log.info("Directory wasn't added.");
            setButtonDisable(false, addDirectory, removeDirectory);
        });
        removeDirectory.textProperty().bind(Strings.RemoveDirectory);
        removeDirectory.setOnAction(e -> {
            log.info("Remove Directory Started:");
            setButtonDisable(true, removeDirectory, addDirectory);
            ArrayList<Directory> directories = ClassHandler.directoryController().findDirectories(true, false, true);
            if (directories.isEmpty()) {
                log.info("No directories to delete.");
                new MessageBox().message(new StringProperty[]{Strings.ThereAreNoDirectoriesToDelete}, (Stage) tabPane.getScene().getWindow());
            } else {
                Directory directoryToDelete = new ListSelectBox().pickDirectory(Strings.DirectoryToDelete, directories, (Stage) tabPane.getScene().getWindow());
                if (directoryToDelete != null && !directoryToDelete.toString().isEmpty()) {
                    log.info("Directory selected for deletion: " + directoryToDelete.getFileName());
                    boolean confirm = new ConfirmBox().confirm(new SimpleStringProperty(Strings.AreYouSureToWantToDelete.getValue() + directoryToDelete.getFileName() + Strings.QuestionMark.getValue()), (Stage) tabPane.getScene().getWindow());
                    if (confirm) {
                        ArrayList<Directory> otherDirectories = ClassHandler.directoryController().findDirectories(directoryToDelete.getIndex(), true, false, true);
                        Map<String, Boolean> showsToUpdate = new HashMap<>();
                        directoryToDelete.getShows().keySet().forEach(aShow -> {
                            log.info("Currently checking: " + aShow);
                            boolean showExistsElsewhere = ClassHandler.showInfoController().doesShowExistElsewhere(aShow, otherDirectories);
                            if (!showExistsElsewhere) {
                                ClassHandler.userInfoController().setIgnoredStatus(aShow, true);
                                ChangeReporter.addChange("- " + aShow);
                            }
                            showsToUpdate.put(aShow, showExistsElsewhere);
                        });
                        ClassHandler.directoryController().removeDirectory(directoryToDelete);
                        ClassHandler.showInfoController().loadShowsFile(ClassHandler.directoryController().findDirectories(false, true, false));
                        showsToUpdate.forEach(Controller::updateShowField);
                        ClassHandler.programSettingsController().setMainDirectoryVersion(ClassHandler.programSettingsController().getSettingsFile().getMainDirectoryVersion() + 1);
                        log.info('"' + directoryToDelete.getFileName() + "\" has been deleted!");
                    } else log.info("No directory has been deleted.");
                }
            }
            log.info("Remove Directory Finished!");
            setButtonDisable(false, removeDirectory, addDirectory);
        });
        directoryTimeoutText.textProperty().bind(Strings.DirectoryTimeout);
        directoryTimeoutText.setTextAlignment(TextAlignment.CENTER);
        directoryTimeoutTextField.setText(String.valueOf(Variables.timeToWaitForDirectory));
        setDirectoryTimeout.textProperty().bind(Strings.Set);
        setDirectoryTimeout.setOnAction(e -> {
            if (isNumberValid(directoryTimeoutTextField.getText(), 2)) {
                if (directoryTimeoutTextField.getText().isEmpty())
                    directoryTimeoutTextField.setText(String.valueOf(Variables.timeToWaitForDirectory));
                else
                    ClassHandler.programSettingsController().setTimeToWaitForDirectory(Integer.valueOf(directoryTimeoutTextField.getText()));
            }
        });
        exportSettings.textProperty().bind(Strings.ExportSettings);
        exportSettings.setOnAction(e -> new FileManager().exportSettings((Stage) tabPane.getScene().getWindow()));
        importSettings.textProperty().bind(Strings.ImportSettings);
        importSettings.setOnAction(e -> new FileManager().importSettings(false, (Stage) tabPane.getScene().getWindow()));
        if (Variables.showOptionToToggleDevMode) {
            if (Variables.devMode) toggleDevMode.setSelected(true);
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
                GenericMethods.printStackTrace(log, e1, this.getClass());
            }
        });
        if (!Variables.devMode) {
            developerTab.setDisable(true);
            tabPane.getTabs().remove(developerTab);
        }

        // UI
        unlockParentScene.textProperty().bind(Strings.AllowFullWindowMovementUse);
        unlockParentScene.setSelected(!ClassHandler.programSettingsController().getSettingsFile().isStageMoveWithParentAndBlockParent());
        unlockParentScene.setOnAction(e -> {
            ClassHandler.programSettingsController().getSettingsFile().setStageMoveWithParentAndBlockParent(!ClassHandler.programSettingsController().getSettingsFile().isStageMoveWithParentAndBlockParent());
            Variables.setStageMoveWithParentAndBlockParent(ClassHandler.programSettingsController().getSettingsFile().isStageMoveWithParentAndBlockParent());
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.close();
            Platform.runLater(() -> Controller.openSettingsWindow(3));
            log.info("MoveAndBlock has been set to: " + ClassHandler.programSettingsController().getSettingsFile().isStageMoveWithParentAndBlockParent());
        });
        showUsername.textProperty().bind(Strings.ShowUsername);
        showUsername.setSelected(ClassHandler.userInfoController().getUserSettings().isShowUsername());
        showUsername.setOnAction(e -> {
            ClassHandler.userInfoController().getUserSettings().setShowUsername(showUsername.isSelected());
            Controller.setShowUsernameVisibility(ClassHandler.userInfoController().getUserSettings().isShowUsername());
        });
        specialEffects.textProperty().bind(Strings.SpecialEffects);
        specialEffects.setSelected(Variables.specialEffects);
        specialEffects.setOnAction(e -> {
            ClassHandler.programSettingsController().getSettingsFile().setEnableSpecialEffects(!ClassHandler.programSettingsController().getSettingsFile().isEnableSpecialEffects());
            log.info("Special Effects has been set to: " + Variables.specialEffects);
        });
        automaticSaving.textProperty().bind(Strings.EnableAutomaticSaving);
        automaticSaving.setSelected(Variables.enableAutoSavingOnTimer);
        automaticSaving.setOnAction(e -> {
            ClassHandler.programSettingsController().getSettingsFile().setEnableAutomaticSaving(!ClassHandler.programSettingsController().getSettingsFile().isEnableAutomaticSaving());
            if (Variables.enableAutoSavingOnTimer && updateSavingTextField.getText().matches(String.valueOf(0))) {
                ClassHandler.programSettingsController().setSavingSpeed(Variables.defaultSavingSpeed);
                updateSavingTextField.setText(String.valueOf(Variables.defaultSavingSpeed));
            }
            setSavingTime.setDisable(!Variables.enableAutoSavingOnTimer);
            updateSavingTextField.setDisable(!Variables.enableAutoSavingOnTimer);
            log.info("Automatic saving has been set to: " + Variables.enableAutoSavingOnTimer);
        });
        savingText.textProperty().bind(Strings.SavingWaitTimeSeconds);
        savingText.setTextAlignment(TextAlignment.CENTER);
        updateSavingTextField.setText(String.valueOf(Variables.savingSpeed));
        updateSavingTextField.setDisable(!Variables.enableAutoSavingOnTimer);
        setSavingTime.textProperty().bind(Strings.Set);
        setSavingTime.setDisable(!Variables.enableAutoSavingOnTimer);
        setSavingTime.setOnAction(e -> {
            if (isNumberValid(updateSavingTextField.getText(), 5))
                ClassHandler.programSettingsController().setSavingSpeed(Integer.valueOf(updateSavingTextField.getText()));
            else updateSavingTextField.setText(String.valueOf(Variables.savingSpeed));
        });
        changeLanguage.textProperty().bind(Strings.ChangeLanguage);
        changeLanguage.setOnAction(e -> {
            setButtonDisable(true, changeLanguage);
            LanguageHandler languageHandler = new LanguageHandler();
            Map<String, String> languages = languageHandler.getLanguageNames();
            if (languages.containsKey(ClassHandler.programSettingsController().getSettingsFile().getLanguage()))
                languages.remove(ClassHandler.programSettingsController().getSettingsFile().getLanguage());
            Object[] pickLanguageResult = new ListSelectBox().pickLanguage(languages.values(), false, (Stage) tabPane.getScene().getWindow());
            String languageReadable = (String) pickLanguageResult[0];
            if (!languageReadable.contains("-2")) {
                String internalName = Strings.EmptyString;
                for (String langKey : languages.keySet()) {
                    if (languages.get(langKey).matches(languageReadable)) {
                        internalName = langKey;
                        break;
                    }
                }
                ClassHandler.programSettingsController().setDefaultLanguage(internalName);
                languageHandler.setLanguage(Variables.language);
            }
            setButtonDisable(false, changeLanguage);
        });

        // Developer
        printAllShows.textProperty().bind(Strings.PrintAllShowInfo);
        printAllShows.setOnAction(e -> ClassHandler.developerStuff().printOutAllShowsAndEpisodes());
        printAllDirectories.textProperty().bind(Strings.PrintAllDirectories);
        printAllDirectories.setOnAction(e -> ClassHandler.developerStuff().printAllDirectories());
        printEmptyShowFolders.textProperty().bind(Strings.PrintEmptyShows);
        printEmptyShowFolders.setOnAction(e -> ClassHandler.developerStuff().printEmptyShows());
        printIgnoredShows.textProperty().bind(Strings.PrintIgnoredShows);
        printIgnoredShows.setOnAction(e -> ClassHandler.developerStuff().printIgnoredShows());
        printHiddenShows.textProperty().bind(Strings.PrintHiddenShows);
        printHiddenShows.setOnAction(e -> ClassHandler.developerStuff().printHiddenShows());
        unHideAll.textProperty().bind(Strings.UnHideAll);
        unHideAll.setOnAction(e -> ClassHandler.developerStuff().unHideAllShows());
        setAllActive.textProperty().bind(Strings.SetAllActive);
        setAllActive.setOnAction(e -> ClassHandler.developerStuff().setAllShowsActive());
        setAllInactive.textProperty().bind(Strings.SetAllInactive);
        setAllInactive.setOnAction(e -> ClassHandler.developerStuff().setAllShowsInactive());
        // Dev 2
        printProgramSettingsFileVersion.textProperty().bind(Strings.PrintPsfvAndUsfv);
        printProgramSettingsFileVersion.setOnAction(e -> log.info("PSFV: " + String.valueOf(ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsFileVersion() + " || USFV: " + ClassHandler.userInfoController().getUserSettings().getUserSettingsFileVersion())));
        printAllUserInfo.textProperty().bind(Strings.PrintAllUserInfo);
        printAllUserInfo.setOnAction(e -> ClassHandler.developerStuff().printAllUserInfo());
        add1ToDirectoryVersion.textProperty().bind(Strings.DirectoryVersionPlus1);
        add1ToDirectoryVersion.setOnAction(e -> ClassHandler.programSettingsController().setMainDirectoryVersion(ClassHandler.programSettingsController().getSettingsFile().getMainDirectoryVersion() + 1));
        nonForceRecheckShows.textProperty().bind(Strings.NonForceRecheckShows);
        nonForceRecheckShows.setOnAction(e -> {
            setButtonDisable(true, nonForceRecheckShows);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ClassHandler.checkShowFiles().recheckShowFile(false);
                    setButtonDisable(false, nonForceRecheckShows);
                    return null;
                }
            };
            new Thread(task).start();
        });
        this.toggleIsChanges.textProperty().bind(Strings.ToggleIsChanges);
        this.toggleIsChanges.setOnAction(e -> {
            ClassHandler.developerStuff().toggleIsChanges();
            log.info("isChanges has been set to:" + ChangeReporter.getIsChanges());
        });
        clearFile.textProperty().bind(Strings.ClearFile);
        clearFile.setOnAction(e -> {
            setButtonDisable(true, clearFile);
            ClassHandler.developerStuff().clearDirectory((Stage) tabPane.getScene().getWindow());
            setButtonDisable(false, clearFile);
        });
        // Once I am sure this works properly, I will make it accessible.
        deleteEverythingAndClose.textProperty().bind(Strings.ResetProgram);
        deleteEverythingAndCloseTooltip.textProperty().bind(Strings.WarningUnrecoverable);
        deleteEverythingAndClose.setOnAction(e -> {
            setButtonDisable(true, deleteEverythingAndClose);
            ConfirmBox confirmBox = new ConfirmBox();
            if (confirmBox.confirm(Strings.AreYouSureThisWillDeleteEverything, (Stage) tabPane.getScene().getWindow())) {
                Stage stage = (Stage) tabPane.getScene().getWindow();
                stage.close();
                new FileManager().clearProgramFiles();
                Main.stop(Main.stage, true, false);
            } else setButtonDisable(false, deleteEverythingAndClose);
        });
        new MoveStage().moveStage(tabPane, Main.stage);
    }

    private boolean isNumberValid(String textFieldValue, int minValue) {
        log.finest("isNumberValid has been called.");
        if (textFieldValue.isEmpty() || !textFieldValue.matches("^[0-9]+$") || Integer.parseInt(textFieldValue) > Variables.maxWaitTimeSeconds || Integer.parseInt(textFieldValue) < minValue) {
            new MessageBox().message(new StringProperty[]{new SimpleStringProperty(Strings.MustBeANumberBetween.getValue() + minValue + " - " + Variables.maxWaitTimeSeconds)}, (Stage) this.tabPane.getScene().getWindow());
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
