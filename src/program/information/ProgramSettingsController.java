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
            settingsFile = new FileManager().loadProgramSettings(Strings.SettingsFileName, Variables.SettingsExtension);
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

    public static String getDataFolder() {
        loadProgramSettingsFile();
        if (settingsFile.get("General").size() == 1) { //TODO remove return new FileManage().getDataFolder(); at Version 0.9
            return new FileManager().getDataFolder();
        }
        return settingsFile.get("General").get(1);
    }

    public static int getProgramSettingsVersion() { //TODO Remove -2 return when program is at Version 0.9
        loadProgramSettingsFile();
        if (settingsFile.containsKey("ProgramVersions")) {
            return Integer.parseInt(settingsFile.get("ProgramVersions").get(0));
        } else return -2;
    }

    public static boolean isDirectoryCurrentlyActive(File directory) {
        return directory.isDirectory();
    }

    public static int getNumberOfDirectories() {
        loadProgramSettingsFile();
        return settingsFile.get("Directories").size();
    }

    public static void removeDirectory(String directory) { // TODO Update other users when directory is deleted.
        loadProgramSettingsFile();
        log.info("Currently processing removal of: " + directory);
        int index = settingsFile.get("Directories").indexOf(directory);
        ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = ShowInfoController.getAllDirectoriesHashMaps();
        HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile = showsFileArray.get(index);
        showsFileArray.remove(index);
        for (String aShow : showsFile.keySet()) {
            log.info("Currently checking: " + aShow);
            Boolean showExistsElsewhere = ShowInfoController.doesShowExistElsewhere(aShow, showsFileArray);
            if (!showExistsElsewhere) {
                UserInfoController.setIgnoredStatus(aShow, true);
            }
        }
        settingsFile.get("Directories").remove(directory);
        new FileManager().deleteFile(Variables.DirectoriesFolder, "Directory-" + index, Variables.ShowsExtension);
        log.info("Finished processing removal. ");
    }

    public static void printAllDirectories() {
        loadProgramSettingsFile();
        log.info("Printing out all directories:");
        if (settingsFile.containsKey("Directories") && !settingsFile.get("Directories").isEmpty()) {
            for (String aDirectory : settingsFile.get("Directories")) {
                log.info(aDirectory);
            }
        } else {
            log.info("No directories.");
        }
        log.info("Finished printing out all directories:");
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

    public static Boolean[] addDirectory(int index, File directory) {
        loadProgramSettingsFile();
        ArrayList<String> directories = settingsFile.get("Directories");
        Boolean[] answer = {false, false};
        if (!directory.toString().isEmpty() && !directories.contains(String.valueOf(directory))) {
            log.info("Added Directory");
            directories.add(index, String.valueOf(directory));
            settingsFile.replace("Directories", directories);
            answer[0] = true;
        } else if (directory.toString().isEmpty()) {
            answer[1] = true;
        }
        return answer;
    }

    public static HashMap<String, ArrayList<String>> getSettingsFile() {
        return settingsFile;
    }

    public static void setSettingsFile(HashMap<String, ArrayList<String>> settingsFile) {
        ProgramSettingsController.settingsFile = settingsFile;
    }

    // Save the file
    public static void saveSettingsFile() {
        if (settingsFile != null) {
            new FileManager().save(settingsFile, Variables.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension, true);
            log.info("ProgramSettingsController- settingsFile has been saved!");
        }
    }
}
