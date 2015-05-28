package program.information;

import program.Controller;
import program.io.FileManager;
import program.util.Strings;
import program.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProgramSettingsController {
    private static final Logger log = Logger.getLogger(ProgramSettingsController.class.getName());

    private static HashMap<String, ArrayList<String>> settingsFile;
    private static boolean mainDirectoryVersionAlreadyChanged = false;

    @SuppressWarnings("unchecked")
    private static void loadProgramSettingsFile() {
        if (settingsFile == null) {
            settingsFile = (HashMap<String, ArrayList<String>>) new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension);
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

    public static boolean getShow0Remaining() {
        loadProgramSettingsFile();
        return Boolean.valueOf(settingsFile.get("General").get(1));
    }

    public static void setShow0Remaining(boolean show0Remaining) {
        loadProgramSettingsFile();
        settingsFile.get("General").set(1, String.valueOf(show0Remaining));
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
            defaultUsername.set(1, Strings.EmptyString);
        } else if (option == 1) {
            defaultUsername.set(0, "true");
            defaultUsername.set(1, userName);
        }
        settingsFile.replace("DefaultUser", defaultUsername);
    }

    public static ArrayList<String> getDirectories() {
        loadProgramSettingsFile();
        ArrayList<String> directories = new ArrayList<>();
        if (settingsFile.containsKey("Directories")) {
            directories.addAll(settingsFile.get("Directories").stream().map(aDirectory -> aDirectory.split(">")[1]).collect(Collectors.toList()));
        }
        return directories;
    }

    public static ArrayList<Integer> getDirectoriesIndexes() {
        loadProgramSettingsFile();
        ArrayList<Integer> directoriesIndexes = new ArrayList<>();
        if (settingsFile.containsKey("Directories")) {
            getDirectories().forEach(aDirectory -> directoriesIndexes.add(getDirectoryIndex(aDirectory)));
        }
        return directoriesIndexes;
    }

    public static int getProgramSettingsVersion() { //TODO Remove -2 return when program is at Version 0.9
        loadProgramSettingsFile();
        if (settingsFile.containsKey("ProgramVersions")) {
            return Integer.parseInt(settingsFile.get("ProgramVersions").get(0));
        } else return -2;
    }

    public static int getMainDirectoryVersion() {
        loadProgramSettingsFile();
        return Integer.parseInt(settingsFile.get("ProgramVersions").get(1));
    }

    public static void setMainDirectoryVersion(int version) {
        if (mainDirectoryVersionAlreadyChanged) {
            log.info("Already changed main directory version this run, no further change needed.");
        } else {
            settingsFile.get("ProgramVersions").set(1, String.valueOf(version));
            // Current User should always be up to date, so its version can be updated with the Main Directory Version. Only time updateUser is false is on firstRun.
            UserInfoController.setUserDirectoryVersion(version);
            saveSettingsFile();
            log.info("Main + User directory version updated to: " + version);
            mainDirectoryVersionAlreadyChanged = true;
        }
    }

    public static boolean isDirectoryCurrentlyActive(File directory) {
        return directory.isDirectory();
    }

    public static void removeDirectory(String aDirectory) {
        loadProgramSettingsFile();
        log.info("Currently processing removal of: " + aDirectory);
        int index = getDirectoryIndex(aDirectory);
        ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = ShowInfoController.getDirectoriesHashMaps(index);
        Set<String> hashMapShows = ShowInfoController.getDirectoryHashMap(index).keySet();
        hashMapShows.forEach(aShow -> {
            log.info("Currently checking: " + aShow);
            Boolean showExistsElsewhere = ShowInfoController.doesShowExistElsewhere(aShow, showsFileArray);
            if (!showExistsElsewhere) {
                UserInfoController.setIgnoredStatus(aShow, true);
            }
            Controller.updateShowField(aShow, showExistsElsewhere);
        });
        new FileManager().deleteFile(Variables.DirectoriesFolder, "Directory-" + index, Variables.ShowsExtension);
        settingsFile.get("Directories").remove(index + ">" + aDirectory);
        log.info("Finished processing removal of the directory.");
    }

    public static void printAllDirectories() {
        loadProgramSettingsFile();
        log.info("Printing out all directories:");
        if (getDirectories().isEmpty()) {
            log.info("No directories.");
        } else getDirectories().forEach(log::info);
        log.info("Finished printing out all directories:");
    }

    public static int getDirectoryIndex(String aDirectory) {
        for (String directory : settingsFile.get("Directories")) {
            if (directory.contains(aDirectory)) {
                return Integer.parseInt(directory.split(">")[0]);
            }
        }
        log.info("Error if this is reached, Please report.");
        return -3;
    }

    public static ArrayList<String> getDirectoriesNames() {
        loadProgramSettingsFile();
        ArrayList<String> directories = settingsFile.get("Directories");
        ArrayList<String> directoriesNames = new ArrayList<>();
        directories.forEach(aDirectory -> {
            int index = Integer.parseInt(aDirectory.split(">")[0]);
            directoriesNames.add("Directory-" + index + Variables.ShowsExtension);
        });
        return directoriesNames;
    }

    public static File getDirectory(int index) {
        loadProgramSettingsFile();
        ArrayList<String> directories = settingsFile.get("Directories");
        for (String aDirectory : directories) {
            String[] split = aDirectory.split(">");
            if (split[0].matches(String.valueOf(index))) {
                return new File(split[1]);
            }
        }
        log.info("Error if this is reached, Please report.");
        return new File(Strings.EmptyString);
    }

    public static Boolean[] addDirectory(int index, File directory) {
        loadProgramSettingsFile();
        ArrayList<String> directories = settingsFile.get("Directories");
        Boolean[] answer = {false, false};
        if (!directory.toString().isEmpty() && !directories.contains(String.valueOf(directory))) {
            log.info("Added Directory");
            directories.add(index + ">" + String.valueOf(directory));
            log.info(index + ">" + String.valueOf(directory));
            settingsFile.replace("Directories", directories);
            answer[0] = true;
        } else if (directory.toString().isEmpty()) {
            answer[1] = true;
        }
        return answer;
    }

    public static int getLowestFreeDirectoryIndex() {
        final int[] lowestFreeIndex = {0};
        settingsFile.get("Directories").forEach(aDirectory -> {
            int currentInt = Integer.parseInt(aDirectory.split(">")[0]);
            if (lowestFreeIndex[0] == currentInt) {
                lowestFreeIndex[0]++;
            }
        });
        return lowestFreeIndex[0];
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
            new FileManager().save(settingsFile, Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension, true);
            log.info("ProgramSettingsController- settingsFile has been saved!");
        }
    }
}
