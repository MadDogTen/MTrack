package program;

import javafx.concurrent.Task;
import program.gui.ConfirmBox;
import program.gui.ListSelectBox;
import program.gui.MessageBox;
import program.gui.TextBox;
import program.information.ProgramSettingsController;
import program.information.UserInfoController;
import program.io.CheckShowFiles;
import program.io.FileManager;
import program.io.GenerateNewShowFiles;
import program.io.GenerateSettingsFiles;
import program.util.Clock;
import program.util.Strings;
import program.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public class MainRun {
    private static final Logger log = Logger.getLogger(MainRun.class.getName());
    public static boolean hasRan = false, forceRun = true;
    public static int timer = Clock.getTimeSeconds();

    public static void startBackend() {
        // If true, It will Delete ALL Files each time the program is ran.
        if (Variables.StartFresh && FileManager.checkFolderExists(FileManager.getDataFolder())) {
            log.warning("Starting Fresh...");
            FileManager.deleteFolder(new File(FileManager.getDataFolder()));
        }
        // Check
        if (FileManager.checkFolderExists(FileManager.getDataFolder())) {
            Strings.UserName = getUser();
        } else firstRun();
        if (!UserInfoController.getAllUsers().contains(Strings.UserName)) {
            generateUserSettingsFile(Strings.UserName, false);
        }
        log.info("Username is set: " + Strings.UserName);
        Variables.setUpdateSpeed();
        Controller.setTableViewFields("active");
    }

    public static void tick() {
        if (!hasRan) {
            log.info("MainRun Running...");
            hasRan = true;
        }
        if (forceRun && Clock.timeTakenSeconds(timer) > 2 || Clock.timeTakenSeconds(timer) > Variables.updateSpeed) {
            final Boolean[] taskRunning = {true};
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    CheckShowFiles.recheckShowFile(false);
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
        log.info("getUser Running...\n");
        ArrayList<String> Users = UserInfoController.getAllUsers();
        if (ProgramSettingsController.isDefaultUsername()) {
            log.info("MainRun- Using default user.");
            return ProgramSettingsController.getDefaultUsername();
        } else {
            ListSelectBox listSelectBox = new ListSelectBox();
            return listSelectBox.display("Select User", "Choose your Username:", Users, Main.window);
        }
    }

    private static void firstRun() {
        log.info("MainRun- First Run, Generating Files...");
        FileManager.createBaseFolder();
        generateProgramSettingsFile();
        chooseDirectories();

        final Boolean[] taskRunning = {true};
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                generateShowFiles();
                taskRunning[0] = false;
                return null;
            }
        };
        new Thread(task).start();
        TextBox textBox = new TextBox();
        Strings.UserName = textBox.display("Enter Username", "Please enter your username: ", "Use default username?", "PublicDefault", Main.window);
        while (taskRunning[0]) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.severe(e.toString());
            }
        }
        generateUserSettingsFile(Strings.UserName, false);
    }

    private static void chooseDirectories() {
        Boolean addAnother = true;
        TextBox textBox = new TextBox();
        ConfirmBox confirmBox = new ConfirmBox();
        int directoryNumber = 0;
        while (addAnother) {
            File directory = textBox.addDirectoriesDisplay("Directories", "Please enter show directory", ProgramSettingsController.getDirectories(), "You need to enter a directory.", "Directory is invalid.", Main.window);
            Boolean matched = ProgramSettingsController.addDirectory(directoryNumber, directory);
            directoryNumber++;
            if (matched) {
                MessageBox messageBox = new MessageBox();
                messageBox.display("Duplicate", "Directory was a duplicate!", Main.window);
            }
            if (!confirmBox.display("Continue", "Add another directory?", Main.window)) {
                addAnother = false;
            }
        }
    }

    // File Generators
    private static void generateProgramSettingsFile() {
        GenerateSettingsFiles.generateProgramSettingsFile(Strings.SettingsFileName, Variables.EmptyString, Variables.SettingsExtension, false);
    }

    private static void generateShowFiles() {
        ArrayList<String> directories = ProgramSettingsController.getDirectories();
        for (String aDirectory : directories) {
            int fileName = directories.indexOf(aDirectory);
            File file = new File(aDirectory);
            GenerateNewShowFiles.generateShowsFile(fileName, file, false);
        }
    }

    private static void generateUserSettingsFile(String userName, Boolean override) {
        log.info("Attempting to generate settings file for " + userName + ".");
        GenerateSettingsFiles.generateUserSettingsFile(Strings.UserName, Variables.SettingsExtension, override);
    }
}
