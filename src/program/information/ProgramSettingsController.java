package program.information;

import program.io.FileManager;
import program.util.Strings;
import program.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ProgramSettingsController {

    private static HashMap<String, ArrayList<String>> settingsFile;

    private static void loadProgramSettingsFile() {
        if (settingsFile == null) {
            settingsFile = FileManager.loadProgramSettings(Variables.settingsFolder, Strings.SettingsFileName, Variables.settingsExtension);
        }
    }

    public static boolean isDefaultUsername() {
        if (settingsFile == null) {
            loadProgramSettingsFile();
        }
        ArrayList<String> defaultUser = settingsFile.get("DefaultUser");
        return Boolean.parseBoolean(defaultUser.get(0));
    }

    public static String getDefaultUsername() {
        if (settingsFile == null) {
            loadProgramSettingsFile();
        }
        ArrayList<String> defaultUser = settingsFile.get("DefaultUser");
        return defaultUser.get(1);
    }

    public static void setDefaultUsername(String userName, int option) {
        System.out.println("DefaultUsername is being set...");
        ArrayList<String> defaultUsername = settingsFile.get("DefaultUser");
        if (option == 0) {
            defaultUsername.set(0, "false");
            defaultUsername.set(1, "");
        } else if (option == 1) {
            defaultUsername.set(0, "true");
            defaultUsername.set(1, userName);
        }
        settingsFile.replace("DefaultUser", defaultUsername);
    }

    public static ArrayList<String> getDirectories() {
        if (settingsFile == null) {
            loadProgramSettingsFile();
        }
        return settingsFile.get("Directories");
    }

    public static ArrayList<String> getDirectoriesNames() {
        if (settingsFile == null) {
            loadProgramSettingsFile();
        }
        ArrayList<String> directories = settingsFile.get("Directories");
        ArrayList<String> directoriesNames = new ArrayList<>();
        for (String aDirectory : directories) {
            int index = directories.indexOf(aDirectory);
            directoriesNames.add(index, "Directory-" + index + Variables.ShowsExtension);
        }
        return directoriesNames;
    }

    public static File getDirectory(int index) {
         return new File(settingsFile.get("Directories").get(index));
    }

    public static boolean setDirectory(int index, File directory) {
        if (settingsFile == null) {
            loadProgramSettingsFile();
        }
        ArrayList<String> directories = settingsFile.get("Directories");

        Boolean matched = false;
        if (directories.contains(String.valueOf(directory))) {
            matched = true;
        }
        if (!matched) {
            directories.add(index, String.valueOf(directory));
            settingsFile.replace("Directories", directories);
            return false;
        } else return true;
    }


    // Save the file
    public static void saveSettingsFile() {
        if (settingsFile != null) {
            FileManager.save(settingsFile, Variables.settingsFolder, Strings.SettingsFileName, Variables.settingsExtension, true);
            System.out.println("settingsFile has been saved!");
        }
    }
}
