package com.maddogten.mtrack;

import com.maddogten.mtrack.gui.ListSelectBox;
import com.maddogten.mtrack.information.*;
import com.maddogten.mtrack.io.CheckShowFiles;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.util.*;
import javafx.concurrent.Task;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/*
      MainRun handles starting the main logic of the program.
 */

public class MainRun {
    private final Logger log = Logger.getLogger(MainRun.class.getName());
    private final ProgramSettingsController programSettingsController;
    private final ShowInfoController showInfoController;
    private final UserInfoController userInfoController;
    private final CheckShowFiles checkShowFiles;
    private final DirectoryController directoryController;
    public boolean firstRun = false, continueStarting = true;
    private boolean hasRan = false, forceRun = true;
    private int timer = GenericMethods.getTimeSeconds();

    @SuppressWarnings("SameParameterValue")
    public MainRun(ProgramSettingsController programSettingsController, ShowInfoController showInfoController, UserInfoController userInfoController, CheckShowFiles checkShowFiles, DirectoryController directoryController) {
        this.programSettingsController = programSettingsController;
        this.showInfoController = showInfoController;
        this.userInfoController = userInfoController;
        this.checkShowFiles = checkShowFiles;
        this.directoryController = directoryController;
    }

    public boolean startBackend() {
        FileManager fileManager = new FileManager();
        // First it checks if the folder that contains the jar has the settings file.
        try {
            File path = new File(URLDecoder.decode(MainRun.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
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
            new FirstRun(programSettingsController, showInfoController, userInfoController, directoryController, this).programFirstRun();
            firstRun = false;
            needsToRun = false;
        }
        if (!continueStarting) return false;
        UpdateManager updateManager = new UpdateManager(programSettingsController, showInfoController, userInfoController, directoryController);
        // If the MTrack folder exists, then this checks if the Program settings file exists, and if for some reason it doesn't, creates it.
        if (needsToRun) {
            if (fileManager.checkFileExists("", Strings.SettingsFileName, Variables.SettingFileExtension)) {
                updateManager.updateProgramSettingsFile();
                programSettingsController.loadProgramSettingsFile();
                updateManager.updateShowFile();
                getLanguage();
                if (Variables.makeLanguageDefault) programSettingsController.setDefaultLanguage(Variables.language);
                if (!continueStarting) return false;
                Strings.UserName = getUser();
                if (!continueStarting) return false;
                if (!userInfoController.getAllUsers().contains(Strings.UserName)) {
                    new FirstRun(programSettingsController, showInfoController, userInfoController, directoryController, this).generateUserSettingsFile(Strings.UserName);
                }
                showInfoController.loadShowsFile();
                updateManager.updateUserSettingsFile();
                userInfoController.loadUserInfo();
            } else {
                new FirstRun(programSettingsController, showInfoController, userInfoController, directoryController, this).generateProgramSettingsFile();
                programSettingsController.loadProgramSettingsFile();
                updateManager.updateShowFile();
                getLanguage();
                if (Variables.makeLanguageDefault) programSettingsController.setDefaultLanguage(Variables.language);
                if (!continueStarting) return false;
                Strings.UserName = getUser();
                if (!continueStarting) return false;
                if (!userInfoController.getAllUsers().contains(Strings.UserName))
                    new FirstRun(programSettingsController, showInfoController, userInfoController, directoryController, this).generateUserSettingsFile(Strings.UserName);
                showInfoController.loadShowsFile();
                updateManager.updateUserSettingsFile();
                userInfoController.loadUserInfo();
                // If those both exists, then it starts normally.
            }
        }
        log.info("Username is set: " + Strings.UserName);
        updateManager.updateMainDirectoryVersion();
        loadSettings();
        return continueStarting;
    }

    private void loadSettings() {
        Variables.updateSpeed = programSettingsController.getSettingsFile().getUpdateSpeed();
        Variables.timeToWaitForDirectory = programSettingsController.getSettingsFile().getTimeToWaitForDirectory();
        Variables.recordChangesForNonActiveShows = programSettingsController.getSettingsFile().isRecordChangesForNonActiveShows();
        Variables.recordChangedSeasonsLowerThanCurrent = programSettingsController.getSettingsFile().isRecordChangedSeasonsLowerThanCurrent();
        Variables.disableAutomaticRechecking = programSettingsController.getSettingsFile().isDisableAutomaticShowUpdating();
        Variables.setStageMoveWithParentAndBlockParent(programSettingsController.getSettingsFile().isStageMoveWithParentAndBlockParent());
        ChangeReporter.setChanges(userInfoController.getUserSettings().getChanges());
    }

    public void tick() {
        if (!hasRan) {
            log.info("MainRun Running...");
            timer = GenericMethods.getTimeSeconds();
            hasRan = true;
        }
        if (directoryController.isReloadShowsFile()) showInfoController.loadShowsFile();
        recheck();
    }

    private void recheck() {
        // noinspection PointlessBooleanExpression
        if (!(Variables.disableAutomaticRechecking || Variables.forceDisableAutomaticRechecking) && (forceRun && GenericMethods.timeTakenSeconds(timer) > 2 || !Controller.getIsShowCurrentlyPlaying() && (GenericMethods.timeTakenSeconds(timer) > Variables.updateSpeed) || Controller.getIsShowCurrentlyPlaying() && GenericMethods.timeTakenSeconds(timer) > (Variables.updateSpeed * 10))) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    checkShowFiles.recheckShowFile(forceRun);
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
            timer = GenericMethods.getTimeSeconds();
            forceRun = false;
        }
    }

    // This first checks if a DefaultUser currently exists, and if not, prompts the user to choose / create one.
    private String getUser() {
        log.info("getUser Running...");
        if (programSettingsController.getSettingsFile().isUseDefaultUser()) {
            log.info("Using default user.");
            return programSettingsController.getSettingsFile().getDefaultUser();
        } else {
            Object[] pickUserResult = new ListSelectBox().pickUser(Strings.ChooseYourUsername, userInfoController.getAllUsers());
            String user = (String) pickUserResult[0];
            if (user.matches(Strings.AddNewUsername.getValue())) continueStarting = false;
            if ((boolean) pickUserResult[1]) programSettingsController.setDefaultUsername(user, true);
            return user;
        }
    }

    // Prompts the user to choose which language to startup with. If there is only 1 language, then it will skip the prompt and start with it.
    public void getLanguage() {
        LanguageHandler languageHandler = new LanguageHandler();
        Map<String, String> languages = languageHandler.getLanguageNames();
        String language = Strings.EmptyString;
        if (!firstRun) language = programSettingsController.getSettingsFile().getLanguage();
        if (languages.size() == 1)
            languages.forEach((internalName, readableName) -> languageHandler.setLanguage(internalName));
        else {
            if (!language.isEmpty() && languages.containsKey(language) && !language.contains("lipsum")) { // !language.contains("lipsum") will be removed when lipsum is removed as a choice // Note- Remove
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
}
