package com.maddogten.mtrack;

import com.maddogten.mtrack.Database.DBManager;
import com.maddogten.mtrack.gui.ListSelectBox;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.util.*;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/*
      MainRun handles starting the main logic of the program.
 */

public class MainRun {
    private final Logger log = Logger.getLogger(MainRun.class.getName());
    public boolean firstRun = false, continueStarting = true;
    private boolean starting = true, forceRun = true, disableChecking = false;
    private int recheckTimer, saveTimer, currentTime, waitTime;

    public boolean startBackend() throws IOException, SQLException { // TODO Remove public
        FileManager fileManager = new FileManager();
        // First it checks if the folder that contains the jar has the settings file.
        try {
            File path = fileManager.getJarLocationFolder();
            if (new File(path, Strings.SettingsFileName + Variables.SettingFileExtension).exists())
                Variables.setDataFolder(path);
            else {
                // If the above isn't the correct folder, it then checks if the default storage folder contains it.
                path = OperatingSystem.programFolder;
                if (fileManager.checkFolderExistsAndReadable(path)) Variables.setDataFolder(path);
            }
        } catch (UnsupportedEncodingException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        // If one above is found and both Variables devMode & StartFresh are true, It will Delete ALL Files each time the program is ran.
        // noinspection PointlessBooleanExpression
        if (DeveloperStuff.startFresh && DeveloperStuff.devMode && !Variables.dataFolder.toString().isEmpty()) {
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
        //UpdateManager updateManager = new UpdateManager();
        // If the MTrack folder exists, then this checks if the Program settings file exists, and if for some reason it doesn't, creates it.
        if (needsToRun) {
            try {
                ClassHandler.setDBManager(new DBManager(Variables.dataFolder.toString(), false));
                ClassHandler.programSettingsController().initDatabase(ClassHandler.getDBManager().getConnection());
                ClassHandler.directoryController().initDBHandler(ClassHandler.getDBManager().getConnection());
                ClassHandler.showInfoController().initDBManager(ClassHandler.getDBManager().getConnection());
                ClassHandler.userInfoController().initDatabase(ClassHandler.getDBManager().getConnection());
            } catch (SQLException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
            /*if (fileManager.checkFileExists("", Strings.SettingsFileName, Variables.SettingFileExtension))
                updateManager.updateProgramSettingsFile();
            else new FirstRun().generateProgramSettingsFile();*/
            //ClassHandler.programSettingsController().loadProgramSettingsFile();
            //loadProgramSettings();
            // updateManager.updateShowFile();
            Strings.UserName.setValue(ClassHandler.userInfoController().getUserNameFromID(getUser()));
            if (!continueStarting) return false;
            getLanguage();
            if (Variables.makeLanguageDefault)
                ClassHandler.userInfoController().setLanguage(Variables.currentUser, Variables.language);
            if (!continueStarting) return false;
            //loadUser(updateManager, true);
        }
        if (Variables.enableFileLogging && !GenericMethods.isFileLoggingStarted()) {
            try {
                GenericMethods.initFileLogging(log);
            } catch (IOException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }
        if (!needsToRun) {
            log.info("Username is set: " + ClassHandler.userInfoController().getUserNameFromID(Variables.currentUser));
            //updateManager.updateMainDirectoryVersion();
            //loadUserSettings(true);
            //loadProgramSettings();
        }
        return continueStarting;
    }

    void loadUser(final UpdateManager updateManager, final boolean mainLoad) throws IOException {
        /*if (!ClassHandler.userInfoController().getAllUsers().contains(Strings.UserName.getValue()))
            new FirstRun().generateUserSettingsFile(Strings.UserName.getValue());*/
        //updateManager.updateUserSettingsFile();
        log.info("Username is set: " + Strings.UserName.getValue());
        //updateManager.updateMainDirectoryVersion();
    }

    void tick() {
        if (starting) {
            log.finer("MainRun Running...");
            this.recheckTimer = GenericMethods.getTimeSeconds();
            this.saveTimer = GenericMethods.getTimeSeconds();
            this.currentTime = GenericMethods.getTimeSeconds();
            starting = false;
        }
        timeCheck();
        recheck();
        //saveSettings();
    }

    private void timeCheck() {
        if (GenericMethods.timeTakenSeconds(currentTime) > 30) {
            disableChecking = true;
            waitTime = GenericMethods.getTimeSeconds();
            log.info(GenericMethods.timeTakenSeconds(currentTime) + " seconds since last timeCheck, Setting a " + Variables.sleepTimeDelay + " rechecking delay.");
        }
        if (disableChecking && GenericMethods.timeTakenSeconds(waitTime) > Variables.sleepTimeDelay) {
            waitTime = 0;
            disableChecking = false;
            log.info("Rechecking delay finished, Re-enabling.");
        }
        currentTime = GenericMethods.getTimeSeconds();
    }

    private void recheck() {
        // noinspection PointlessBooleanExpression
        if (!(disableChecking || Variables.disableAutomaticRechecking || Variables.forceDisableAutomaticRechecking) && (forceRun && GenericMethods.timeTakenSeconds(recheckTimer) > 2 || !Controller.isShowCurrentlyPlaying() && (GenericMethods.timeTakenSeconds(recheckTimer) > Variables.updateSpeed) || Controller.isShowCurrentlyPlaying() && GenericMethods.timeTakenSeconds(recheckTimer) > (Variables.updateSpeed * 10))) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ClassHandler.checkShowFiles().checkShowFiles();
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
    public int getUser() {
        log.finer("getUser Running...");
        int defaultUser = ClassHandler.programSettingsController().getDefaultUser();
        if (defaultUser != -2) {
            log.finer("Using default user.");
            return defaultUser;
        } else {
            HashMap<String, Integer> users = new HashMap<>();
            ClassHandler.userInfoController().getAllUsers().forEach(userID -> {
                users.put(ClassHandler.userInfoController().getUserNameFromID(userID), userID);
            });
            Object[] pickUserResult = new ListSelectBox().pickUser(Strings.ChooseYourUsername, users.keySet());
            int user = -2;
            if (((String) pickUserResult[0]).matches(Strings.AddNewUsername.getValue())) continueStarting = false;
            else user = users.get(pickUserResult[0]);
            Variables.currentUser = user;
            if ((boolean) pickUserResult[1]) ClassHandler.programSettingsController().setDefaultUser(user);
            return user;
        }
    }

    // Prompts the user to choose which language to startup with. If there is only 1 language, then it will skip the prompt and start with it.
    public void getLanguage() {
        LanguageHandler languageHandler = new LanguageHandler();
        Map<String, String> languages = languageHandler.getLanguageNames();
        String language = Strings.EmptyString;
        if (!firstRun) language = ClassHandler.userInfoController().getLanguage(Variables.currentUser);
        if (languages.size() == 1)
            languages.forEach((internalName, readableName) -> languageHandler.setLanguage(internalName));
        else if (!language.isEmpty() && languages.containsKey(language) && !language.contains("lipsum")) { // !language.contains("lipsum") will be removed when lipsum is removed as a choice // Note- Remove
            Boolean wasSet = languageHandler.setLanguage(language);
            Variables.makeLanguageDefault = true;
            if (wasSet) {
                Variables.language = language;
                log.finer("Language is set: " + language);
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
                    log.finer("Language is set: " + languageReadable);
                } else log.severe("Language was not set for some reason, Please report.");
            }
        }
    }
}
