package com.maddogten.mtrack;

import com.maddogten.mtrack.gui.ListSelectBox;
import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.util.*;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/*
      MainRun handles starting the main logic of the program.
 */

public class MainRun {
    private final Logger log = Logger.getLogger(MainRun.class.getName());
    public boolean firstRun = false, continueStarting = true;
    private boolean hasRan = false, forceRun = true;
    private int recheckTimer, saveTimer;

    boolean startBackend() {
        FileManager fileManager = new FileManager();
        // First it checks if the folder that contains the jar has the settings file.
        try {
            File path = fileManager.getJarLocationFolder();
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    String[] splitFile = String.valueOf(file).split(Pattern.quote(Strings.FileSeparator));
                    if (splitFile[splitFile.length - 1].matches(Strings.SettingsFileName + Variables.SettingFileExtension))
                        Variables.setDataFolder(path);
                }
            }
        } catch (UnsupportedEncodingException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        // If the above isn't the correct folder, it then checks if the Roaming Appdata folder is the correct one.
        if (Variables.dataFolder.toString().isEmpty()) {
            File path = fileManager.findProgramFolder();
            if (fileManager.checkFolderExistsAndReadable(path)) Variables.setDataFolder(path);
        }
        // If one above is found and both Variables devMode & StartFresh are true, It will Delete ALL Files each time the program is ran.
        // noinspection PointlessBooleanExpression
        if (Variables.devMode && Variables.startFresh && !Variables.dataFolder.toString().isEmpty()) {
            log.warning("Starting Fresh...");
            fileManager.clearProgramFiles();
            Variables.setDataFolder(new File(Strings.EmptyString));
        }
        boolean needsToRun = true;
        // If both of those failed or it deleted the files,  Then it starts firstRun.
        if (Variables.dataFolder.toString().isEmpty()) {
            firstRun = true;
            needsToRun = new FirstRun().programFirstRun();
            if (Variables.dataFolder.toString().isEmpty()) continueStarting = false;
            firstRun = false;
        }
        if (!continueStarting) return false;
        UpdateManager updateManager = new UpdateManager();
        // If the MTrack folder exists, then this checks if the Program settings file exists, and if for some reason it doesn't, creates it.
        if (needsToRun) {
            if (fileManager.checkFileExists("", Strings.SettingsFileName, Variables.SettingFileExtension))
                updateManager.updateProgramSettingsFile();
            else new FirstRun().generateProgramSettingsFile();
            ClassHandler.programSettingsController().loadProgramSettingsFile();
            updateManager.updateShowFile();
            getLanguage();
            if (Variables.makeLanguageDefault)
                ClassHandler.programSettingsController().setDefaultLanguage(Variables.language);
            if (!continueStarting) return false;
            Strings.UserName.setValue(getUser());
            if (!continueStarting) return false;
            loadUser(updateManager, true);
        }

        if (Variables.enableFileLogging && !GenericMethods.isFileLoggingStarted()) {
            try {
                GenericMethods.initFileLogging(log);
            } catch (IOException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }

        if (!needsToRun) {
            log.info("Username is set: " + Strings.UserName.getValue());
            updateManager.updateMainDirectoryVersion();
            loadSettings(true);
        }
        return continueStarting;
    }

    public void loadUser(UpdateManager updateManager, boolean mainLoad) {
        if (!ClassHandler.userInfoController().getAllUsers().contains(Strings.UserName.getValue()))
            new FirstRun().generateUserSettingsFile(Strings.UserName.getValue());
        if (ClassHandler.showInfoController().getShowsFile() == null)
            ClassHandler.showInfoController().loadShowsFile(ClassHandler.directoryController().findDirectories(false, true, false));
        else
            ClassHandler.showInfoController().loadShowsFile(ClassHandler.directoryController().findDirectories(false, true, true));
        updateManager.updateUserSettingsFile();
        ClassHandler.userInfoController().loadUserInfo();
        log.info("Username is set: " + Strings.UserName.getValue());
        updateManager.updateMainDirectoryVersion();
        loadSettings(mainLoad);
    }

    private void loadSettings(boolean mainLoad) {
        Variables.updateSpeed = ClassHandler.programSettingsController().getSettingsFile().getUpdateSpeed();
        Variables.timeToWaitForDirectory = ClassHandler.programSettingsController().getSettingsFile().getTimeToWaitForDirectory();
        Variables.recordChangesForNonActiveShows = ClassHandler.programSettingsController().getSettingsFile().isRecordChangesForNonActiveShows();
        Variables.recordChangedSeasonsLowerThanCurrent = ClassHandler.programSettingsController().getSettingsFile().isRecordChangedSeasonsLowerThanCurrent();
        Variables.disableAutomaticRechecking = ClassHandler.programSettingsController().getSettingsFile().isDisableAutomaticShowUpdating();
        Variables.setStageMoveWithParentAndBlockParent(ClassHandler.programSettingsController().getSettingsFile().isStageMoveWithParentAndBlockParent());
        Variables.specialEffects = ClassHandler.programSettingsController().getSettingsFile().isEnableSpecialEffects();
        Variables.enableAutoSavingOnTimer = ClassHandler.programSettingsController().getSettingsFile().isEnableAutomaticSaving();
        Variables.savingSpeed = ClassHandler.programSettingsController().getSettingsFile().getSaveSpeed();
        Variables.enableFileLogging = ClassHandler.programSettingsController().getSettingsFile().isFileLogging();
        Variables.useOnlineDatabase = ClassHandler.programSettingsController().getSettingsFile().isUseRemoteDatabase();
        Variables.show0Remaining = ClassHandler.programSettingsController().getSettingsFile().isShow0Remaining();
        Variables.showActiveShows = ClassHandler.programSettingsController().getSettingsFile().isShowActiveShows();
        ChangeReporter.setChanges(ClassHandler.userInfoController().getUserSettings().getChanges());
        if (!mainLoad)
            ClassHandler.controller().setChangedShows(ClassHandler.userInfoController().getUserSettings().getChangedShowsStatus());
    }

    void tick() {
        if (!hasRan) {
            log.info("MainRun Running...");
            this.recheckTimer = GenericMethods.getTimeSeconds();
            this.saveTimer = GenericMethods.getTimeSeconds();
            hasRan = true;
        }
        recheck();
        saveSettings();
    }

    private void recheck() {
        // noinspection PointlessBooleanExpression
        if (!(Variables.disableAutomaticRechecking || Variables.forceDisableAutomaticRechecking) && (forceRun && GenericMethods.timeTakenSeconds(recheckTimer) > 2 || !Controller.getIsShowCurrentlyPlaying() && (GenericMethods.timeTakenSeconds(recheckTimer) > Variables.updateSpeed) || Controller.getIsShowCurrentlyPlaying() && GenericMethods.timeTakenSeconds(recheckTimer) > (Variables.updateSpeed * 10))) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ClassHandler.checkShowFiles().recheckShowFile(forceRun);
                    return null;
                }
            };
            Thread recheckThread = new Thread(task);
            recheckThread.start();
            try {
                recheckThread.join();
            } catch (InterruptedException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
            recheckTimer = GenericMethods.getTimeSeconds();
            forceRun = false;
        }
    }

    // This first checks if a DefaultUser currently exists, and if not, prompts the user to choose / create one.
    public String getUser() {
        log.info("getUser Running...");
        if (ClassHandler.programSettingsController().getSettingsFile().isUseDefaultUser()) {
            log.info("Using default user.");
            return ClassHandler.programSettingsController().getSettingsFile().getDefaultUser();
        } else {
            Object[] pickUserResult = new ListSelectBox().pickUser(Strings.ChooseYourUsername, ClassHandler.userInfoController().getAllUsers());
            String user = (String) pickUserResult[0];
            if (user.matches(Strings.AddNewUsername.getValue())) continueStarting = false;
            if ((boolean) pickUserResult[1]) ClassHandler.programSettingsController().setDefaultUsername(user, true);
            return user;
        }
    }

    private void saveSettings() {
        if (Variables.enableAutoSavingOnTimer && GenericMethods.timeTakenSeconds(saveTimer) > Variables.savingSpeed) {
            GenericMethods.saveSettings();
            saveTimer = GenericMethods.getTimeSeconds();
            log.info("Settings have automatically been saved.");
        }
    }


    // Prompts the user to choose which language to startup with. If there is only 1 language, then it will skip the prompt and start with it.
    public void getLanguage() {
        LanguageHandler languageHandler = new LanguageHandler();
        Map<String, String> languages = languageHandler.getLanguageNames();
        String language = Strings.EmptyString;
        if (!firstRun) language = ClassHandler.programSettingsController().getSettingsFile().getLanguage();
        if (languages.size() == 1)
            languages.forEach((internalName, readableName) -> languageHandler.setLanguage(internalName));
        else if (!language.isEmpty() && languages.containsKey(language) && !language.contains("lipsum")) { // !language.contains("lipsum") will be removed when lipsum is removed as a choice // Note- Remove
            Boolean wasSet = languageHandler.setLanguage(language);
            Variables.makeLanguageDefault = true;
            if (wasSet) {
                Variables.language = language;
                log.info("Language is set: " + language);
            } else log.severe("Language was not set for some reason, Please report.");
        } else {
            languageHandler.setLanguage(Variables.DefaultLanguage[0]);
            Object[] pickLanguageResult = new ListSelectBox().pickLanguage(languages.values(), true, null);
            String languageReadable = (String) pickLanguageResult[0];
            if (languageReadable.matches("-2")) continueStarting = false;
            else {
                String internalName = Strings.EmptyString;
                for (String langKey : languages.keySet()) {
                    if (languages.get(langKey).matches(languageReadable)) {
                        internalName = langKey;
                        break;
                    }
                }
                Variables.makeLanguageDefault = (boolean) pickLanguageResult[1];
                if (languageHandler.setLanguage(internalName)) {
                    Variables.language = internalName;
                    log.info("Language is set: " + languageReadable);
                } else log.severe("Language was not set for some reason, Please report.");
            }
        }
    }
}
