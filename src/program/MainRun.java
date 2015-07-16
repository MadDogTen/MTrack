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
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class MainRun {
    private final Logger log = Logger.getLogger(MainRun.class.getName());
    public boolean firstRun = false;
    ProgramSettingsController programSettingsController;
    ShowInfoController showInfoController;
    UserInfoController userInfoController;
    CheckShowFiles checkShowFiles;
    private boolean hasRan = false, forceRun = true;
    private int timer = Main.clock.getTimeSeconds();

    public MainRun(ProgramSettingsController programSettingsController, ShowInfoController showInfoController, UserInfoController userInfoController, CheckShowFiles checkShowFiles) {
        this.programSettingsController = programSettingsController;
        this.showInfoController = showInfoController;
        this.userInfoController = userInfoController;
        this.checkShowFiles = checkShowFiles;
    }

    public void startBackend() {
        FileManager fileManager = new FileManager();
        Variables.setDataFolder(fileManager);
        // If both Variables devMode & StartFresh are true, It will Delete ALL Files each time the program is ran.
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (Variables.devMode && Variables.startFresh && fileManager.checkFolderExists(Variables.dataFolder)) {
            log.warning("Starting Fresh...");
            fileManager.deleteFolder(new File(Variables.dataFolder));
        }
        UpdateManager updateManager = new UpdateManager(programSettingsController, showInfoController, userInfoController);
        // First it checks if the MTrack folder currently exists, if not, creates it.
        if (!fileManager.checkFolderExists(Variables.dataFolder)) {
            firstRun = true;
            new FirstRun(programSettingsController, showInfoController, userInfoController).programFirstRun();
            firstRun = false;
            // If the MTrack folder exists, then this checks if the Program settings file exists, and if for some reason it doesn't, creates it.
        } else if (!fileManager.checkFileExists("", Strings.SettingsFileName, Variables.SettingsExtension)) {
            new FirstRun(programSettingsController, showInfoController, userInfoController).generateProgramSettingsFile();
            programSettingsController.loadProgramSettingsFile();
            updateManager.updateShowFile();
            getLanguage();
            Strings.UserName = getUser();
            showInfoController.loadShowsFile();
            updateManager.updateUserSettingsFile();
            userInfoController.loadUserInfo();
            // If those both exists, then it starts normally.
        } else {
            updateManager.updateProgramSettingsFile();
            programSettingsController.loadProgramSettingsFile();
            updateManager.updateShowFile();
            getLanguage();
            Strings.UserName = getUser();
            showInfoController.loadShowsFile();
            updateManager.updateUserSettingsFile();
            userInfoController.loadUserInfo();
        }

        log.info("Username is set: " + Strings.UserName);
        updateManager.updateMainDirectoryVersion();
        Variables.setUpdateSpeed(programSettingsController.getUpdateSpeed());
        Controller.setTableViewFields("active");
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
        String language = programSettingsController.getLanguage();
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
                        programSettingsController.setLanguage(internalName);
                        log.info("Language is set: " + languageReadable);
                    } else {
                        log.severe("Language was not set for some reason, Please report.");
                    }
                } else getLanguage();
            }
        }
    }
}
