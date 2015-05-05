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

public class MainRun {
    public static boolean hasRan = false, forceRun = true;
    public static int timer = Clock.getTimeSeconds();


    public static void startBackend() {
        if (FileManager.checkFolderExists(FileManager.getDataFolder())) {
            Strings.setUserName(getUser());
        } else firstRun();

        if (!UserInfoController.getAllUsers().contains(Strings.UserName)) {
            generateUserSettingsFile(Strings.UserName, false);
        }

        System.out.println("Username is set: " + Strings.UserName);
        Controller.setTableViewFields("active");
    }

    public static void tick() {
        if (!hasRan) {
            System.out.println("MainRun Running ...\n");
            hasRan = true;
        }
        if (forceRun || Clock.timeTakenSeconds(timer) > Variables.updateSpeed) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    CheckShowFiles.recheckShowFile();
                    return null;
                }
            };
            new Thread(task).start();
            timer = Clock.getTimeSeconds();
            forceRun = false;
        }
    }

    private static String getUser() {
        System.out.println("getUser Running...\n");

        ArrayList<String> Users = UserInfoController.getAllUsers();
        if (ProgramSettingsController.isDefaultUsername()) {
            return ProgramSettingsController.getDefaultUsername();
        } else {
            ListSelectBox listSelectBox = new ListSelectBox();
            return listSelectBox.display("Select User", "Choose your Username:", Users);
        }
    }

    private static void firstRun() {
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
        Strings.setUserName(textBox.display("Enter Username", "Please enter your username: ", "Use default username?", "PublicDefault"));

        while (taskRunning[0]) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
            File directory = textBox.addDirectoriesDisplay("Directories", "Please enter show directory", "You need to enter a directory.", "Directory is invalid.");
            Boolean matched = ProgramSettingsController.setDirectory(directoryNumber, directory);
            directoryNumber++;
            if (matched) {
                MessageBox messageBox = new MessageBox();
                messageBox.display("Duplicate", "Directory was a duplicate!");
            }
            if (!confirmBox.display("Continue", "Add another directory?")) {
                addAnother = false;
            }
        }
    }

    // File Generators
    private static void generateProgramSettingsFile() {
        GenerateSettingsFiles.generateProgramSettingsFile(Strings.SettingsFileName, Variables.settingsFolder, Variables.settingsExtension, false);
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
        System.out.println("Attempting to generate settings file for " + userName + ".");
        GenerateSettingsFiles.generateUserSettingsFile(Variables.settingsFolder, Variables.settingsExtension, override);
    }
}
