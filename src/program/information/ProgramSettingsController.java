package program.information;

import program.Controller;
import program.information.settings.ProgramSettings;
import program.information.show.Show;
import program.io.FileManager;
import program.util.Strings;
import program.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProgramSettingsController {
    private static final Logger log = Logger.getLogger(ProgramSettingsController.class.getName());

    private static ProgramSettings settingsFile;
    private static boolean mainDirectoryVersionAlreadyChanged = false;

    @SuppressWarnings("unchecked")
    public static void loadProgramSettingsFile() {
        settingsFile = (ProgramSettings) new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension);
    }

    public static int getUpdateSpeed() {
        return settingsFile.getUpdateSpeed();
    }

    public static void setUpdateSpeed(int updateSpeed) {
        settingsFile.setUpdateSpeed(updateSpeed);
        Variables.setUpdateSpeed();
    }

    public static boolean getShow0Remaining() {
        return settingsFile.isShow0Remaining();
    }

    public static void setShow0Remaining(boolean show0Remaining) {
        settingsFile.setShow0Remaining(show0Remaining);
    }

    public static String getLanguage() {
        return settingsFile.getLanguage();
    }

    public static void setLanguage(String language) {
        settingsFile.setLanguage(language);
    }

    public static boolean isDefaultUsername() {
        return settingsFile.isUseDefaultUser();
    }

    public static String getDefaultUsername() {
        return settingsFile.getDefaultUser();
    }

    public static void setDefaultUsername(String userName, boolean disableDefaultUser) {
        log.info("ProgramSettingsController- DefaultUsername is being set...");
        settingsFile.setUseDefaultUser(disableDefaultUser);
        settingsFile.setDefaultUser(userName);
    }

    public static ArrayList<String> getDirectories() {
        ArrayList<String> directories = new ArrayList<>();
        if (settingsFile.getDirectories() != null) {
            directories.addAll(settingsFile.getDirectories().stream().map(aDirectory -> aDirectory.split(">")[1]).collect(Collectors.toList()));
        }
        return directories;
    }

    public static ArrayList<Integer> getDirectoriesIndexes() {
        ArrayList<Integer> directoriesIndexes = new ArrayList<>();
        if (settingsFile.getDirectories() != null) {
            getDirectories().forEach(aDirectory -> directoriesIndexes.add(getDirectoryIndex(String.valueOf(aDirectory))));
        }
        return directoriesIndexes;
    }

    public static int getProgramSettingsFileVersion() {
        return settingsFile.getProgramSettingsFileVersion();
    }

    public static int getMainDirectoryVersion() {
        return settingsFile.getMainDirectoryVersion();
    }

    public static void setMainDirectoryVersion(int version) {
        if (mainDirectoryVersionAlreadyChanged) {
            log.info("Already changed main directory version this run, no further change needed.");
        } else {
            settingsFile.setMainDirectoryVersion(version);
            // Current User should always be up to date, so its version can be updated with the Main Directory Version.
            if (!Controller.mainRun.firstRun) {
                UserInfoController.setUserDirectoryVersion(version);
            }
            saveSettingsFile();
            log.info("Main + User directory version updated to: " + version);
            mainDirectoryVersionAlreadyChanged = true;
        }
    }

    public static int getShowFileVersion() {
        return settingsFile.getShowFileVersion();
    }

    public static void setShowFileVersion(int version) {
        settingsFile.setShowFileVersion(version);
    }

    public static boolean isDirectoryCurrentlyActive(File directory) {
        return directory.isDirectory();
    }

    public static void removeDirectory(String aDirectory) {
        log.info("Currently processing removal of: " + aDirectory);
        int index = getDirectoryIndex(aDirectory);
        ArrayList<Map<String, Show>> showsFileArray = ShowInfoController.getDirectoriesMaps(index);
        Set<String> hashMapShows = ShowInfoController.getDirectoryMap(index).keySet();
        hashMapShows.forEach(aShow -> {
            log.info("Currently checking: " + aShow);
            boolean showExistsElsewhere = ShowInfoController.doesShowExistElsewhere(aShow, showsFileArray);
            if (!showExistsElsewhere) {
                UserInfoController.setIgnoredStatus(aShow, true);
            }
            Controller.updateShowField(aShow, showExistsElsewhere);
        });
        new FileManager().deleteFile(Variables.DirectoriesFolder, "Directory-" + index, Variables.ShowsExtension);
        settingsFile.getDirectories().remove(index + ">" + aDirectory);
        log.info("Finished processing removal of the directory.");
    }

    public static void printAllDirectories() {
        log.info("Printing out all directories:");
        if (getDirectories().isEmpty()) {
            log.info("No directories.");
        } else getDirectories().forEach(log::info);
        log.info("Finished printing out all directories:");
    }

    public static int getDirectoryIndex(String aDirectory) {
        for (String directory : settingsFile.getDirectories()) {
            if (directory.contains(aDirectory)) {
                return Integer.parseInt(directory.split(">")[0]);
            }
        }
        log.info("Error if this is reached, Please report.");
        return -3;
    }

    public static ArrayList<String> getDirectoriesNames() {
        ArrayList<String> directories = settingsFile.getDirectories();
        ArrayList<String> directoriesNames = new ArrayList<>();
        directories.forEach(aDirectory -> {
            int index = Integer.parseInt(aDirectory.split(">")[0]);
            directoriesNames.add("Directory-" + index + Variables.ShowsExtension);
        });
        return directoriesNames;
    }

    public static File getDirectory(int index) {
        ArrayList<String> directories = settingsFile.getDirectories();
        for (String aDirectory : directories) {
            String[] split = aDirectory.split(">");
            if (split[0].matches(String.valueOf(index))) {
                return new File(split[1]);
            }
        }
        log.info("Error if this is reached, Please report.");
        return new File(Strings.EmptyString);
    }

    public static boolean[] addDirectory(int index, File directory) {
        ArrayList<String> directories = settingsFile.getDirectories();
        boolean[] answer = {false, false};
        if (directories == null) {
            directories = new ArrayList<>();
        }
        if (!directory.toString().isEmpty() && !directories.contains(String.valueOf(directory))) {
            log.info("Added Directory");
            directories.add(index + ">" + String.valueOf(directory));
            log.info(index + ">" + String.valueOf(directory));
            settingsFile.setDirectories(directories);
            answer[0] = true;
        } else if (directory.toString().isEmpty()) {
            answer[1] = true;
        }
        return answer;
    }

    public static int getLowestFreeDirectoryIndex() {
        final int[] lowestFreeIndex = {0};
        settingsFile.getDirectories().forEach(aDirectory -> {
            int currentInt = Integer.parseInt(aDirectory.split(">")[0]);
            if (lowestFreeIndex[0] == currentInt) {
                lowestFreeIndex[0]++;
            }
        });
        return lowestFreeIndex[0];
    }

    public static ProgramSettings getSettingsFile() {
        return settingsFile;
    }

    public static void setSettingsFile(ProgramSettings settingsFile) {
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
