package program;

import javafx.concurrent.Task;
import program.gui.ConfirmBox;
import program.gui.ListSelectBox;
import program.gui.MessageBox;
import program.gui.TextBox;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.information.settings.ProgramSettings;
import program.information.settings.UserSettings;
import program.information.settings.UserShowSettings;
import program.io.FileManager;
import program.io.GenerateNewShowFiles;
import program.util.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MainRun {
    private final Logger log = Logger.getLogger(MainRun.class.getName());
    public boolean firstRun = false;
    private boolean hasRan = false, forceRun = true;
    private int timer = Clock.getTimeSeconds();

    public void startBackend() {
        FileManager fileManager = new FileManager();
        Variables.setDataFolder(fileManager);
        // If both Variables devMode & StartFresh are true, It will Delete ALL Files each time the program is ran.
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (Variables.devMode && Variables.startFresh && fileManager.checkFolderExists(Variables.dataFolder)) {
            log.warning("Starting Fresh...");
            fileManager.deleteFolder(new File(Variables.dataFolder));
        }
        UpdateManager updateManager = new UpdateManager();
        // First it checks if the MTrack folder currently exists, if not, creates it.
        if (!fileManager.checkFolderExists(Variables.dataFolder)) {
            firstRun();
            // If the MTrack folder exists, then this checks if the Program settings file exists, and if for some reason it doesn't, creates it.
        } else if (!fileManager.checkFileExists("", Strings.SettingsFileName, Variables.SettingsExtension)) {
            generateProgramSettingsFile();
            ProgramSettingsController.loadProgramSettingsFile();
            updateManager.updateShowFile();
            getLanguage();
            Strings.UserName = getUser();
            ShowInfoController.loadShowsFile();
            updateManager.updateUserSettingsFile();
            UserInfoController.loadUserInfo();
            // If those both exists, then it starts normally.
        } else {
            ProgramSettingsController.loadProgramSettingsFile();
            updateManager.updateProgramSettingsFile();
            updateManager.updateShowFile();
            getLanguage();
            Strings.UserName = getUser();
            ShowInfoController.loadShowsFile();
            updateManager.updateUserSettingsFile();
            UserInfoController.loadUserInfo();
        }

        if (!UserInfoController.getAllUsers().contains(Strings.UserName)) {
            generateUserSettingsFile(Strings.UserName);
            UserInfoController.loadUserInfo();
        }

        log.info("Username is set: " + Strings.UserName);
        updateManager.updateMainDirectoryVersion();
        Variables.setUpdateSpeed();
        Controller.setTableViewFields("active");
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
                    Controller.checkShowFiles.recheckShowFile(false);
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
    }

    // This first checks if a DefaultUser currently exists, and if not, prompts the user to choose / create one.
    private String getUser() {
        log.info("getUser Running...");
        ArrayList<String> Users = UserInfoController.getAllUsers();
        if (ProgramSettingsController.isDefaultUsername()) {
            log.info("MainRun- Using default user.");
            return ProgramSettingsController.getDefaultUsername();
        } else {
            return new ListSelectBox().display(Strings.ChooseYourUsername, Users, null);
        }
    }

    private void firstRun() {
        firstRun = true;
        log.info("MainRun- First Run, Generating Files...");
        new FileManager().createFolder(Strings.EmptyString);
        generateProgramSettingsFile();
        ProgramSettingsController.loadProgramSettingsFile();
        getLanguage();
        addDirectories();

        final boolean[] taskRunning = {true};
        Task<Void> task = new Task<Void>() {
            @SuppressWarnings("ReturnOfNull")
            @Override
            protected Void call() throws Exception {
                generateShowFiles();
                taskRunning[0] = false;
                return null;
            }
        };
        new Thread(task).start();
        TextBox textBox = new TextBox();
        Strings.UserName = textBox.display(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, null);
        while (taskRunning[0]) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.severe(e.toString());
            }
        }
        log.info(Strings.UserName);
        ShowInfoController.loadShowsFile();
        generateUserSettingsFile(Strings.UserName);
        UserInfoController.loadUserInfo();
        firstRun = false;
    }

    // During the firstRun, This is ran which shows a popup to add directory to scan. You can exit this without entering anything. If you do enter one, it will then ask you if you want to add another, or move on.
    private void addDirectories() {
        boolean addAnother = true;
        TextBox textBox = new TextBox();
        ConfirmBox confirmBox = new ConfirmBox();
        int index = 0;
        while (addAnother) {
            boolean[] matched = ProgramSettingsController.addDirectory(index, textBox.addDirectoriesDisplay("Please enter show directory", ProgramSettingsController.getDirectories(), "You need to enter a directory.", "Directory is invalid.", null));
            index++;
            if (!matched[0] && !matched[1]) {
                MessageBox messageBox = new MessageBox();
                messageBox.display(Strings.DirectoryWasADuplicate, Main.stage);
            } else if (matched[1]) {
                break;
            }
            if (!confirmBox.display(Strings.AddAnotherDirectory, Main.stage)) {
                addAnother = false;
            }
        }
    }

    // Prompts the user to choose which language to startup with. If there is only 1 language, then it will skip the prompt and start with it.
    private void getLanguage() {
        LanguageHandler languageHandler = new LanguageHandler();
        Map<String, String> languages = languageHandler.getLanguageNames();
        String language = ProgramSettingsController.getLanguage();
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
                        ProgramSettingsController.setLanguage(internalName);
                        log.info("Language is set: " + languageReadable);
                    } else {
                        log.severe("Language was not set for some reason, Please report.");
                    }
                } else getLanguage();
            }
        }
    }

    // File Generators
    // Generates the program settings file.
    private void generateProgramSettingsFile() {
        log.info("Attempting to generate program settings file.");
        new FileManager().save(new ProgramSettings(), Strings.EmptyString, program.util.Strings.SettingsFileName, Variables.SettingsExtension, true);
    }

    // Generates the ShowFiles (If a directory is added, otherwise this is skipped).
    private void generateShowFiles() {
        log.info("Generating show files for first run...");
        ArrayList<String> directories = ProgramSettingsController.getDirectories();
        directories.forEach(aDirectory -> {
            log.info("Currently generating show files for: " + aDirectory);
            int fileName = directories.indexOf(aDirectory);
            File file = new File(aDirectory);
            new GenerateNewShowFiles().generateShowsFile(fileName, file);
        });
        log.info("Finished generating show files.");
    }

    // Generates a user settings file for the given username.
    private void generateUserSettingsFile(String userName) {
        log.info("Attempting to generate settings file for " + userName + '.');
        Map<String, UserShowSettings> showSettings = new HashMap<>();
        ArrayList<String> showsList = ShowInfoController.getShowsList();
        for (String aShow : showsList) {
            showSettings.put(aShow, new UserShowSettings(aShow));
        }
        new FileManager().save(new UserSettings(userName, showSettings), Variables.UsersFolder, userName, Variables.UsersExtension, false);
    }
}
