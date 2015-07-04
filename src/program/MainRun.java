package program;

import javafx.concurrent.Task;
import program.gui.ConfirmBox;
import program.gui.ListSelectBox;
import program.gui.MessageBox;
import program.gui.TextBox;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.io.CheckShowFiles;
import program.io.FileManager;
import program.io.GenerateNewShowFiles;
import program.io.GenerateSettingsFiles;
import program.lang.en_US;
import program.util.Clock;
import program.util.Strings;
import program.util.UpdateManager;
import program.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class MainRun {
    private static final Logger log = Logger.getLogger(MainRun.class.getName());
    public static boolean firstRun = false;
    private static boolean hasRan = false, forceRun = true;
    private static int timer = Clock.getTimeSeconds();

    public static void startBackend() {
        new en_US().setAllStrings(); //TODO Finish lang
        FileManager fileManager = new FileManager();
        Variables.setDataFolder(fileManager);
        // If true, It will Delete ALL Files each time the program is ran.
        if (Variables.devMode && Variables.StartFresh && fileManager.checkFolderExists(Variables.dataFolder)) {
            log.warning("Starting Fresh...");
            fileManager.deleteFolder(new File(Variables.dataFolder));
        }
        // Check
        if (!fileManager.checkFolderExists(Variables.dataFolder)) {
            firstRun();
        } else if (!fileManager.checkFileExists("", Strings.SettingsFileName, Variables.SettingsExtension)) {
            generateProgramSettingsFile();
            ProgramSettingsController.loadProgramSettingsFile();
            Strings.UserName = getUser();
            ShowInfoController.loadShowsFile();
            UserInfoController.loadUserInfo();
        } else {
            ProgramSettingsController.loadProgramSettingsFile();
            Strings.UserName = getUser();
            ShowInfoController.loadShowsFile();
            UserInfoController.loadUserInfo();
        }

        if (!UserInfoController.getAllUsers().contains(Strings.UserName)) {
            generateUserSettingsFile(Strings.UserName);
            UserInfoController.loadUserInfo();
        }
        log.info("Username is set: " + Strings.UserName);
        // Load all necessary files.
        log.info("Loading files...");
        log.info("Finished loading files.");
        new UpdateManager().updateFiles();
        Variables.setUpdateSpeed();
        Controller.setTableViewFields("active");
    }

    public static void tick() {
        if (!hasRan) {
            log.info("MainRun Running...");
            hasRan = true;
        }
        Boolean isShowCurrentlyPlaying = Controller.getIsShowCurrentlyPlaying();
        if (!Variables.devMode && (forceRun && Clock.timeTakenSeconds(timer) > 2 || (Clock.timeTakenSeconds(timer) > Variables.updateSpeed) && !isShowCurrentlyPlaying || isShowCurrentlyPlaying && Clock.timeTakenSeconds(timer) > (Variables.updateSpeed * 10))) {
            final Boolean[] taskRunning = {true};
            Task<Void> task = new Task<Void>() {
                @SuppressWarnings("ReturnOfNull")
                @Override
                protected Void call() throws Exception {
                    new CheckShowFiles().recheckShowFile(false);
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

    private static String getUser() {
        log.info("getUser Running...");
        ArrayList<String> Users = UserInfoController.getAllUsers();
        if (ProgramSettingsController.isDefaultUsername()) {
            log.info("MainRun- Using default user.");
            return ProgramSettingsController.getDefaultUsername();
        } else {
            return new ListSelectBox().display(Strings.ChooseYourUsername, Users, null);
        }
    }

    private static void firstRun() {
        firstRun = true;
        log.info("MainRun- First Run, Generating Files...");
        new FileManager().createFolder(Strings.EmptyString);
        generateProgramSettingsFile();
        ProgramSettingsController.loadProgramSettingsFile();
        addDirectories();

        final Boolean[] taskRunning = {true};
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
        Strings.UserName = textBox.display(Strings.PleaseEnterUsername, Strings.UseDefaultUsername, Strings.DefaultUsername, Main.stage);
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

    private static void addDirectories() {
        Boolean addAnother = true;
        TextBox textBox = new TextBox();
        ConfirmBox confirmBox = new ConfirmBox();
        int index = 0;
        while (addAnother) {
            Boolean[] matched = ProgramSettingsController.addDirectory(index, textBox.addDirectoriesDisplay("Please enter show directory", ProgramSettingsController.getDirectories(), "You need to enter a directory.", "Directory is invalid.", Main.stage));
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

    // File Generators
    private static void generateProgramSettingsFile() {
        log.info("Attempting to generate program settings file.");
        new GenerateSettingsFiles().generateProgramSettingsFile();
    }

    private static void generateShowFiles() {
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

    private static void generateUserSettingsFile(String userName) {
        log.info("Attempting to generate settings file for " + userName + '.' );
        new GenerateSettingsFiles().generateUserSettingsFile(Strings.UserName);
    }
}
