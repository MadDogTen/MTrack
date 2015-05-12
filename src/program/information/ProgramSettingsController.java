package program.information;

import program.io.FileManager;
import program.util.Strings;
import program.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class ProgramSettingsController {
    private static final Logger log = Logger.getLogger(ProgramSettingsController.class.getName());

    private static HashMap<String, ArrayList<String>> settingsFile;

    private static void loadProgramSettingsFile() {
        if (settingsFile == null) {
            settingsFile = FileManager.loadProgramSettings(Strings.SettingsFileName, Variables.SettingsExtension);
        }
    }

    public static int getUpdateSpeed() {
        loadProgramSettingsFile();
        return Integer.parseInt(settingsFile.get("General").get(0));
    }

    public static void setUpdateSpeed(int updateSpeed) {
        loadProgramSettingsFile();
        settingsFile.get("General").set(0, String.valueOf(updateSpeed));
        Variables.setUpdateSpeed();
    }

    public static boolean isDefaultUsername() {
        loadProgramSettingsFile();
        ArrayList<String> defaultUser = settingsFile.get("DefaultUser");
        return Boolean.parseBoolean(defaultUser.get(0));
    }

    public static String getDefaultUsername() {
        loadProgramSettingsFile();
        ArrayList<String> defaultUser = settingsFile.get("DefaultUser");
        return defaultUser.get(1);
    }

    public static void setDefaultUsername(String userName, int option) {
        log.info("ProgramSettingsController- DefaultUsername is being set...");
        ArrayList<String> defaultUsername = settingsFile.get("DefaultUser");
        if (option == 0) {
            defaultUsername.set(0, "false");
            defaultUsername.set(1, Variables.EmptyString);
        } else if (option == 1) {
            defaultUsername.set(0, "true");
            defaultUsername.set(1, userName);
        }
        settingsFile.replace("DefaultUser", defaultUsername);
    }

    public static ArrayList<String> getDirectories() {
        loadProgramSettingsFile();
        return settingsFile.get("Directories");
    }

    public static int getNumberOfDirectories() {
        loadProgramSettingsFile();
        return settingsFile.get("Directories").size();
    }

    public static void printAllDirectories() {
        loadProgramSettingsFile();
        for (String aDirectory : settingsFile.get("Directories")) {
            log.info(aDirectory);
        }
    }

    public static int getDirectoryIndex(String directory) {
        return settingsFile.get("Directories").indexOf(directory);
    }

    public static ArrayList<String> getDirectoriesNames() {
        loadProgramSettingsFile();
        ArrayList<String> directories = settingsFile.get("Directories");
        ArrayList<String> directoriesNames = new ArrayList<>();
        for (String aDirectory : directories) {
            int index = directories.indexOf(aDirectory);
            directoriesNames.add(index, "Directory-" + index + Variables.ShowsExtension);
        }
        return directoriesNames;
    }

    public static File getDirectory(int index) {
        loadProgramSettingsFile();
        return new File(settingsFile.get("Directories").get(index));
    }

    public static boolean addDirectory(int index, File directory) {
        loadProgramSettingsFile();
        ArrayList<String> directories = settingsFile.get("Directories");
        System.out.println("PSC: " + index);
        if (!directories.contains(String.valueOf(directory))) {
            log.info("Added Directory");
            directories.add(index, String.valueOf(directory));
            settingsFile.replace("Directories", directories);
            return false;
        }
        return true;
    }

    // Save the file
    public static void saveSettingsFile() {
        if (settingsFile != null) {
            FileManager.save(settingsFile, Variables.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension, true);
            log.info("ProgramSettingsController- settingsFile has been saved!");
        }
    }
}
