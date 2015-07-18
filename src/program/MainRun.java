package program;

import javafx.concurrent.Task;
import program.gui.ListSelectBox;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.io.CheckShowFiles;
import program.io.FileManager;
import program.util.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

public class MainRun {
    private final Logger log = Logger.getLogger(MainRun.class.getName());
    private final ProgramSettingsController programSettingsController;
    private final ShowInfoController showInfoController;
    private final UserInfoController userInfoController;
    private final CheckShowFiles checkShowFiles;
    public boolean firstRun = false, continueStarting = true; //TODO Finish continueStarting.
    private boolean hasRan = false, forceRun = true;
    private int timer = Main.clock.getTimeSeconds();

    public MainRun(ProgramSettingsController programSettingsController, ShowInfoController showInfoController, UserInfoController userInfoController, CheckShowFiles checkShowFiles) {
        this.programSettingsController = programSettingsController;
        this.showInfoController = showInfoController;
        this.userInfoController = userInfoController;
        this.checkShowFiles = checkShowFiles;
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
                    String[] splitFile = file.toString().split("\\\\");
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
        if (Variables.devMode && Variables.startFresh && Variables.dataFolder != null) {
            log.warning("Starting Fresh...");
            if (Variables.dataFolder == fileManager.getAppDataFolder()) {
                fileManager.deleteFolder(Variables.dataFolder);
            } else {
                File[] files = Variables.dataFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (!file.toString().contains(".jar")) {
                            if (file.isDirectory()) {
                                fileManager.deleteFolder(file);
                            } else fileManager.deleteFile("", String.valueOf(file), "");
                        }
                    }
                }
            }
        }
        boolean runFinished = false;
        // If both of those failed or it deleted the files,  Then it starts firstRun.
        if (!folderFound) {
            firstRun = true;
            new FirstRun(programSettingsController, showInfoController, userInfoController).programFirstRun();
            firstRun = false;
            runFinished = true;
        }

        UpdateManager updateManager = new UpdateManager(programSettingsController, showInfoController, userInfoController);

        // If the MTrack folder exists, then this checks if the Program settings file exists, and if for some reason it doesn't, creates it.
        if (!runFinished) {
            if (fileManager.checkFileExists("", Strings.SettingsFileName, Variables.SettingsExtension)) {
                updateManager.updateProgramSettingsFile();
                programSettingsController.loadProgramSettingsFile();
                updateManager.updateShowFile();
                getLanguage();
                Strings.UserName = getUser();
                showInfoController.loadShowsFile();
                updateManager.updateUserSettingsFile();
                userInfoController.loadUserInfo();
            } else {
                new FirstRun(programSettingsController, showInfoController, userInfoController).generateProgramSettingsFile();
                programSettingsController.loadProgramSettingsFile();
                updateManager.updateShowFile();
                getLanguage();
                Strings.UserName = getUser();
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
            timer = Main.clock.getTimeSeconds();
            hasRan = true;
        }
        boolean isShowCurrentlyPlaying = Controller.getIsShowCurrentlyPlaying();
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!Variables.devMode && (forceRun && Main.clock.timeTakenSeconds(timer) > 2 || !isShowCurrentlyPlaying && (Main.clock.timeTakenSeconds(timer) > Variables.updateSpeed) || isShowCurrentlyPlaying && Main.clock.timeTakenSeconds(timer) > (Variables.updateSpeed * 10))) {
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
            timer = Main.clock.getTimeSeconds();
            forceRun = false;
        }
    }

    // This first checks if a DefaultUser currently exists, and if not, prompts the user to choose / create one.
    private String getUser() {
        log.info("getUser Running...");
        ArrayList<String> Users = userInfoController.getAllUsers();
        if (programSettingsController.isDefaultUsername()) {
            log.info("MainRun- Using default user.");
            return programSettingsController.getDefaultUsername();
        } else {
            return new ListSelectBox().display(Strings.ChooseYourUsername, Users, null);
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
                    log.info("Language is set: " + language);
                } else {
                    log.severe("Language was not set for some reason, Please report.");
                }
            } else {
                languageHandler.setLanguage(Variables.DefaultLanguage);
                String languageReadable = new ListSelectBox().pickLanguage(Strings.PleaseChooseYourLanguage, languages.values(), null);
                if (languageReadable != null && !languageReadable.isEmpty()) {
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
                        log.info("Language is set: " + languageReadable);
                    } else {
                        log.severe("Language was not set for some reason, Please report.");
                    }
                } else getLanguage();
            }
        }
    }
}
