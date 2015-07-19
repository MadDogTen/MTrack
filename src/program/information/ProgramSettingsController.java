package program.information;

import program.Controller;
import program.Main;
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
    private final Logger log = Logger.getLogger(ProgramSettingsController.class.getName());

    private ProgramSettings settingsFile;
    private boolean mainDirectoryVersionAlreadyChanged = false;
    private ShowInfoController showInfoController;
    private UserInfoController userInfoController;

    @SuppressWarnings("unchecked")
    public void loadProgramSettingsFile() {
        this.settingsFile = (ProgramSettings) new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension);
    }

    public int getUpdateSpeed() {
        return settingsFile.getUpdateSpeed();
    }

    public void setUpdateSpeed(int updateSpeed) {
        settingsFile.setUpdateSpeed(updateSpeed);
        Variables.setUpdateSpeed(updateSpeed);
        log.info("Update speed is now set too: ");
    }

    public boolean getShow0Remaining() {
        return settingsFile.isShow0Remaining();
    }

    public void setShow0Remaining(boolean show0Remaining) {
        settingsFile.setShow0Remaining(show0Remaining);
    }

    public String getLanguage() {
        return settingsFile.getLanguage();
    }

    public void setLanguage(String language) {
        settingsFile.setLanguage(language);
    }

    public boolean isDefaultUsername() {
        return settingsFile.isUseDefaultUser();
    }

    public String getDefaultUsername() {
        return settingsFile.getDefaultUser();
    }

    public void setDefaultUsername(String userName, boolean useDefaultUser) {
        log.info("DefaultUsername is being set...");
        settingsFile.setUseDefaultUser(useDefaultUser);
        settingsFile.setDefaultUser(userName);
        log.info("DefaultUsername is set.");
    }

    public ArrayList<String> getDirectories() {
        ArrayList<String> directories = new ArrayList<>();
        if (settingsFile.getDirectories() != null) {
            directories.addAll(settingsFile.getDirectories().stream().map(aDirectory -> aDirectory.split(">")[1]).collect(Collectors.toList()));
        }
        return directories;
    }

    public ArrayList<Integer> getDirectoriesIndexes() {
        ArrayList<Integer> directoriesIndexes = new ArrayList<>();
        if (settingsFile.getDirectories() != null) {
            getDirectories().forEach(aDirectory -> directoriesIndexes.add(getDirectoryIndex(String.valueOf(aDirectory))));
        }
        return directoriesIndexes;
    }

    public int getProgramSettingsFileVersion() {
        return settingsFile.getProgramSettingsFileVersion();
    }

    public int getMainDirectoryVersion() {
        return settingsFile.getMainDirectoryVersion();
    }

    public void setMainDirectoryVersion(int version) {
        if (mainDirectoryVersionAlreadyChanged) {
            log.info("Already changed main directory version this run, no further change needed.");
        } else {
            settingsFile.setMainDirectoryVersion(version);
            // Current User should always be up to date, so its version can be updated with the Main Directory Version.
            if (!Main.getMainRun().firstRun) {
                userInfoController.setUserDirectoryVersion(version);
            }

            saveSettingsFile();
            log.info("Main + User directory version updated to: " + version);
            mainDirectoryVersionAlreadyChanged = true;
        }
    }

    public int getShowFileVersion() {
        return settingsFile.getShowFileVersion();
    }

    public void setShowFileVersion(int version) {
        settingsFile.setShowFileVersion(version);
    }

    public boolean isDirectoryCurrentlyActive(File directory) {
        return directory.isDirectory();
    }

    public void removeDirectory(String aDirectory) {
        log.info("Currently processing removal of: " + aDirectory);
        int index = getDirectoryIndex(aDirectory);
        ArrayList<Map<String, Show>> showsFileArray = showInfoController.getDirectoriesMaps(index);
        Set<String> hashMapShows = showInfoController.getDirectoryMap(index).keySet();
        hashMapShows.forEach(aShow -> {
            log.info("Currently checking: " + aShow);
            boolean showExistsElsewhere = showInfoController.doesShowExistElsewhere(aShow, showsFileArray);
            if (!showExistsElsewhere) {
                userInfoController.setIgnoredStatus(aShow, true);
                ChangeReporter.addChange(aShow + Strings.WasRemoved);
            }
            Controller.updateShowField(aShow, showExistsElsewhere);
        });
        new FileManager().deleteFile(Variables.DirectoriesFolder, "Directory-" + index, Variables.ShowsExtension);
        settingsFile.getDirectories().remove(index + ">" + aDirectory);
        log.info("Finished processing removal of the directory.");
    }

    public void printAllDirectories() {
        log.info("Printing out all directories:");
        if (getDirectories().isEmpty()) {
            log.info("No directories.");
        } else getDirectories().forEach(log::info);
        log.info("Finished printing out all directories:");
    }

    public int getDirectoryIndex(String aDirectory) {
        for (String directory : settingsFile.getDirectories()) {
            if (directory.contains(aDirectory)) {
                return Integer.parseInt(directory.split(">")[0]);
            }
        }
        log.info("Error if this is reached, Please report.");
        return -3;
    }

    public ArrayList<String> getDirectoriesNames() {
        ArrayList<String> directories = settingsFile.getDirectories();
        ArrayList<String> directoriesNames = new ArrayList<>();
        directories.forEach(aDirectory -> {
            int index = Integer.parseInt(aDirectory.split(">")[0]);
            directoriesNames.add("Directory-" + index + Variables.ShowsExtension);
        });
        return directoriesNames;
    }

    public File getDirectory(int index) {
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

    public boolean[] addDirectory(int index, File directory) {
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

    public int getLowestFreeDirectoryIndex() {
        final int[] lowestFreeIndex = {0};
        settingsFile.getDirectories().forEach(aDirectory -> {
            int currentInt = Integer.parseInt(aDirectory.split(">")[0]);
            if (lowestFreeIndex[0] == currentInt) {
                lowestFreeIndex[0]++;
            }
        });
        return lowestFreeIndex[0];
    }

    public ProgramSettings getSettingsFile() {
        return settingsFile;
    }

    public void setSettingsFile(ProgramSettings settingsFile) {
        this.settingsFile = settingsFile;
    }

    public void setShowInfoController(ShowInfoController showInfoController) {
        this.showInfoController = showInfoController;
    }

    public void setUserInfoController(UserInfoController userInfoController) {
        this.userInfoController = userInfoController;
    }

    // Save the file
    public void saveSettingsFile() {
        if (settingsFile != null) {
            new FileManager().save(settingsFile, Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension, true);
            log.info("settingsFile has been saved!");
        }
    }
}
