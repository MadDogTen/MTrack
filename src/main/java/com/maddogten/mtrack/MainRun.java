package com.maddogten.mtrack;

import com.maddogten.mtrack.gui.ListSelectBox;
import com.maddogten.mtrack.information.DirectoryController;
import com.maddogten.mtrack.information.ProgramSettingsController;
import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.information.UserInfoController;
import com.maddogten.mtrack.io.CheckShowFiles;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.util.*;
import javafx.concurrent.Task;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class MainRun {
    private final Logger log = Logger.getLogger(MainRun.class.getName());
    private final ProgramSettingsController programSettingsController;
    private final ShowInfoController showInfoController;
    private final UserInfoController userInfoController;
    private final CheckShowFiles checkShowFiles;
    private final DirectoryController directoryController;
    public boolean firstRun = false, continueStarting = true;
    private boolean hasRan = false, forceRun = true;
    private int timer = Clock.getTimeSeconds();

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
        boolean folderFound = false;
        try {
            File path = new File(URLDecoder.decode(MainRun.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    String[] splitFile = String.valueOf(file).split(Pattern.quote(Strings.FileSeparator));
                    if (splitFile[splitFile.length - 1].matches(Strings.SettingsFileName + Variables.SettingsExtension)) {
                        Variables.setDataFolder(path);
                        folderFound = true;
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.severe(Arrays.toString(e.getStackTrace()));
        }

        // If the above isn't the correct folder, it then checks if the Roaming Appdata folder is the correct one.
        if (!folderFound) {
            File path = fileManager.getAppDataFolder();
            if (fileManager.checkFolderExists(path)) {
                Variables.setDataFolder(path);
                folderFound = true;
            }
        }

        // If one above is found and both Variables devMode & StartFresh are true, It will Delete ALL Files each time the program is ran.
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (Variables.devMode && Variables.startFresh && folderFound) {
            log.warning("Starting Fresh...");
            if (Variables.dataFolder.toString().matches(Pattern.quote(String.valueOf(fileManager.getAppDataFolder())))) {
                log.info("Deleting " + Variables.dataFolder + " in AppData...");
                fileManager.deleteFolder(Variables.dataFolder);
            } else {
                log.info("Deleting appropriate files found in folder jar is contained in...");
                File[] files = Variables.dataFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (!file.toString().contains(".jar") && (file.isDirectory() || file.toString().contains(Variables.SettingsExtension))) {
                            if (file.isDirectory() && (file.toString().matches(Variables.DirectoriesFolder) || file.toString().matches(Variables.UsersFolder))) {
                                fileManager.deleteFolder(file);
                            } else fileManager.deleteFile("", String.valueOf(file), "");
                        }
                    }
                }
            }
            folderFound = false;
        }

        boolean runFinished = false;
        // If both of those failed or it deleted the files,  Then it starts firstRun.
        if (!folderFound) {
            firstRun = true;
            new FirstRun(programSettingsController, showInfoController, userInfoController, directoryController).programFirstRun();
            firstRun = false;
            runFinished = true;
        }

        if (!continueStarting) return false;

        UpdateManager updateManager = new UpdateManager(programSettingsController, showInfoController, userInfoController, directoryController);

        // If the MTrack folder exists, then this checks if the Program settings file exists, and if for some reason it doesn't, creates it.
        if (!runFinished) {
            if (fileManager.checkFileExists("", Strings.SettingsFileName, Variables.SettingsExtension)) {
                updateManager.updateProgramSettingsFile();
                programSettingsController.loadProgramSettingsFile();
                updateManager.updateShowFile();
                getLanguage();
                if (!continueStarting) return false;
                Strings.UserName = getUser();
                if (!continueStarting) return false;
                if (!userInfoController.getAllUsers().contains(Strings.UserName)) {
                    new FirstRun(programSettingsController, showInfoController, userInfoController, directoryController).generateUserSettingsFile(Strings.UserName);
                }
                showInfoController.loadShowsFile();
                updateManager.updateUserSettingsFile();
                userInfoController.loadUserInfo();
            } else {
                new FirstRun(programSettingsController, showInfoController, userInfoController, directoryController).generateProgramSettingsFile();
                programSettingsController.loadProgramSettingsFile();
                updateManager.updateShowFile();
                getLanguage();
                if (!continueStarting) return false;
                Strings.UserName = getUser();
                if (!continueStarting) return false;
                if (!userInfoController.getAllUsers().contains(Strings.UserName)) {
                    new FirstRun(programSettingsController, showInfoController, userInfoController, directoryController).generateUserSettingsFile(Strings.UserName);
                }
                showInfoController.loadShowsFile();
                updateManager.updateUserSettingsFile();
                userInfoController.loadUserInfo();
                // If those both exists, then it starts normally.
            }
        }
        log.info("Username is set: " + Strings.UserName);
        updateManager.updateMainDirectoryVersion();
        Variables.setUpdateSpeed(programSettingsController.getUpdateSpeed());

        return continueStarting;
    }

    public void tick() {
        if (!hasRan) {
            log.info("MainRun Running...");
            timer = Clock.getTimeSeconds();
            hasRan = true;
        }
        boolean isShowCurrentlyPlaying = Controller.getIsShowCurrentlyPlaying();
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!Variables.devMode && (forceRun && Clock.timeTakenSeconds(timer) > 2 || !isShowCurrentlyPlaying && (Clock.timeTakenSeconds(timer) > Variables.updateSpeed) || isShowCurrentlyPlaying && Clock.timeTakenSeconds(timer) > (Variables.updateSpeed * 10))) {
            final boolean[] taskRunning = {true};
            Task<Void> task = new Task<Void>() {
                @SuppressWarnings("ReturnOfNull")
                @Override
                protected Void call() throws Exception {
                    checkShowFiles.recheckShowFile(false);
                    taskRunning[0] = false;
                    return null;
                }
            };
            new Thread(task).start();
            while (taskRunning[0]) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.severe(e.toString());
                }
            }
            timer = Clock.getTimeSeconds();
            forceRun = false;
        }
        if (directoryController.isReloadShowsFile()) {
            showInfoController.loadShowsFile();
        }
    }

    // This first checks if a DefaultUser currently exists, and if not, prompts the user to choose / create one.
    private String getUser() {
        log.info("getUser Running...");
        ArrayList<String> Users = userInfoController.getAllUsers();
        if (programSettingsController.isDefaultUsername()) {
            log.info("Using default user.");
            return programSettingsController.getDefaultUsername();
        } else {
            String result = new ListSelectBox().display(Strings.ChooseYourUsername, Users, null);
            if (result.matches(Strings.AddNewUsername)) {
                continueStarting = false;
            }
            return result;
        }
    }


    // Prompts the user to choose which language to startup with. If there is only 1 language, then it will skip the prompt and start with it.
    public void getLanguage() {
        LanguageHandler languageHandler = new LanguageHandler();
        Map<String, String> languages = languageHandler.getLanguageNames();
        String language = null;
        if (!firstRun) {
            language = programSettingsController.getLanguage();
        }
        if (languages.size() == 1) {
            languages.forEach((internalName, readableName) -> languageHandler.setLanguage(internalName));
        } else {
            if (language != null && !language.isEmpty() && languages.containsKey(language) && !language.contains("lipsum")) { // !language.contains("lipsum") will be removed when lipsum is removed as a choice TODO <- Remove
                Boolean wasSet = languageHandler.setLanguage(language);
                if (wasSet) {
                    Variables.language = language;
                    languageHandler.addMissingTextForAllMissingStrings();
                    log.info("Language is set: " + language);
                } else {
                    log.severe("Language was not set for some reason, Please report.");
                }
            } else {
                languageHandler.setLanguage(Variables.DefaultLanguage);
                String languageReadable = new ListSelectBox().pickLanguage(Strings.PleaseChooseYourLanguage, languages.values(), null);
                if (languageReadable.matches("-2")) continueStarting = false;
                else {
                    String internalName = Strings.EmptyString;
                    for (String langKey : languages.keySet()) {
                        if (languages.get(langKey).matches(languageReadable)) {
                            internalName = langKey;
                            break;
                        }
                    }
                    Boolean wasSet = languageHandler.setLanguage(internalName);
                    if (wasSet) {
                        Variables.language = internalName;
                        languageHandler.addMissingTextForAllMissingStrings();
                        log.info("Language is set: " + languageReadable);
                    } else {
                        log.severe("Language was not set for some reason, Please report.");
                    }
                }
            }
        }
    }
}
