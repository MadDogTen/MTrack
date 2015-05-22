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
import program.util.UpdateManager;
import program.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public class MainRun {
    private static final Logger log = Logger.getLogger(MainRun.class.getName());
    private static boolean hasRan = false, forceRun = true;
    private static int timer = Clock.getTimeSeconds();

    public static void startBackend() {
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
            Strings.UserName = getUser();
        } else {
            Strings.UserName = getUser();
        }

        if (!UserInfoController.getAllUsers().contains(Strings.UserName)) {
            generateUserSettingsFile(Strings.UserName, false);
        }
        log.info("Username is set: " + Strings.UserName);
        new UpdateManager().updateFiles();
        Variables.setUpdateSpeed();
        Controller.setTableViewFields("active");
    }

    public static void tick() {
        if (!hasRan) {
            log.info("MainRun Running...");
            hasRan = true;
        }
        if (!Variables.devMode && (forceRun && Clock.timeTakenSeconds(timer) > 2 || Clock.timeTakenSeconds(timer) > Variables.updateSpeed)) {
            final Boolean[] taskRunning = {true};
            Task<Void> task = new Task<Void>() {
                @SuppressWarnings("ReturnOfNull")
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
            return listSelectBox.display("Choose your Username:", Users, null);
        }
    }

    private static void firstRun() {
        log.info("MainRun- First Run, Generating Files...");
        new FileManager().createFolder(Variables.EmptyString);
        generateProgramSettingsFile();
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
        Strings.UserName = textBox.display("Please enter your username: ", "Use default username?", "PublicDefault", Main.window);
        while (taskRunning[0]) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.severe(e.toString());
            }
        }
        generateUserSettingsFile(Strings.UserName, false);
    }

    private static void addDirectories() {
        Boolean addAnother = true;
        TextBox textBox = new TextBox();
        ConfirmBox confirmBox = new ConfirmBox();
        while (addAnother) {
            Boolean[] matched = ProgramSettingsController.addDirectory(ProgramSettingsController.getLowestFreeDirectoryIndex(), textBox.addDirectoriesDisplay("Please enter show directory", ProgramSettingsController.getDirectories(), "You need to enter a directory.", "Directory is invalid.", Main.window));
            if (!matched[0] && !matched[1]) {
                MessageBox messageBox = new MessageBox();
                messageBox.display("Directory was a duplicate!", Main.window);
            } else if (matched[1]) {
                break;
            }
            if (!confirmBox.display("Add another directory?", Main.window)) {
                addAnother = false;
            }
        }
    }

    // File Generators
    private static void generateProgramSettingsFile() {
        log.info("Attempting to generate program settings file.");
        new GenerateSettingsFiles().generateProgramSettingsFile(Strings.SettingsFileName, Variables.EmptyString, Variables.SettingsExtension, false);
    }

    private static void generateShowFiles() {
        log.info("Generating show files for first run...");
        ArrayList<String> directories = ProgramSettingsController.getDirectories();
        for (String aDirectory : directories) {
            log.info("Currently generating show files for: " + aDirectory);
            int fileName = directories.indexOf(aDirectory);
            File file = new File(aDirectory);
            GenerateNewShowFiles.generateShowsFile(fileName, file, false);
        }
        log.info("Finished generating show files.");
    }

    private static void generateUserSettingsFile(String userName, Boolean override) {
        log.info("Attempting to generate settings file for " + userName + '.' );
        new GenerateSettingsFiles().generateUserSettingsFile(Strings.UserName, Variables.SettingsExtension, override);
    }
}
