package com.maddogten.mtrack.util;

import com.maddogten.mtrack.information.*;
import com.maddogten.mtrack.information.settings.ProgramSettings;
import com.maddogten.mtrack.information.settings.UserSettings;
import com.maddogten.mtrack.information.settings.UserShowSettings;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.information.show.Episode;
import com.maddogten.mtrack.information.show.Season;
import com.maddogten.mtrack.information.show.Show;
import com.maddogten.mtrack.io.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class UpdateManager {
    private final Logger log = Logger.getLogger(UpdateManager.class.getName());

    private final ProgramSettingsController programSettingsController;
    private final ShowInfoController showInfoController;
    private final UserInfoController userInfoController;
    private final DirectoryController directoryController;
    private Object[] neededObjects = new Object[0];

    public UpdateManager(ProgramSettingsController programSettingsController, ShowInfoController showInfoController, UserInfoController userInfoController, DirectoryController directoryController) {
        this.programSettingsController = programSettingsController;
        this.showInfoController = showInfoController;
        this.userInfoController = userInfoController;
        this.directoryController = directoryController;
    }

    // These both compare the version defined in Variables (Latest Version) with the version the files are currently at (Old Version), and if they don't match, converts the files to the latest version.
    public void updateProgramSettingsFile() {
        Object programSettingsFile = new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension);
        if (programSettingsFile instanceof ProgramSettings) {
            ProgramSettings programSettings = (ProgramSettings) programSettingsFile;
            if (Variables.ProgramSettingsFileVersion == programSettings.getProgramSettingsFileVersion()) {
                log.info("Program settings file versions matched.");
            } else {
                log.info("Program settings file versions didn't match " + Variables.ProgramSettingsFileVersion + " - " + programSettings.getProgramSettingsFileVersion() + ", Updating...");
                convertProgramSettingsFile(programSettings.getProgramSettingsFileVersion(), Variables.ProgramSettingsFileVersion);
            }
        } else if (programSettingsFile instanceof HashMap) {
            //noinspection unchecked
            HashMap<String, ArrayList<String>> oldProgramSettingsFile = (HashMap<String, ArrayList<String>>) programSettingsFile;
            int currentVersion;
            if (oldProgramSettingsFile.containsKey("ProgramVersions")) {
                currentVersion = Integer.parseInt(oldProgramSettingsFile.get("ProgramVersions").get(0));
            } else currentVersion = -2;
            if (Variables.ProgramSettingsFileVersion == currentVersion) {
                log.info("Program settings file versions matched.");
            } else {
                log.info("Program settings file versions didn't match " + Variables.ProgramSettingsFileVersion + " - " + currentVersion + ", Updating...");
                convertProgramSettingsFile(currentVersion, Variables.ProgramSettingsFileVersion);
            }
        } else {
            log.severe("Was unable to load program file, Forcing shut down.");
            System.exit(0);
        }
    }


    public void updateUserSettingsFile() {
        if (!userInfoController.getAllUsers().contains(Strings.UserName)) {
            log.info("Attempting to generate settings file for " + Strings.UserName + '.');
            Map<String, UserShowSettings> showSettings = new HashMap<>();
            ArrayList<String> showsList = showInfoController.getShowsList();
            for (String aShow : showsList) {
                int lowestSeason = showInfoController.findLowestSeason(aShow);
                showSettings.put(aShow, new UserShowSettings(aShow, showInfoController.findLowestSeason(aShow), showInfoController.findLowestEpisode(showInfoController.getEpisodesList(aShow, lowestSeason))));
            }
            new FileManager().save(new UserSettings(Strings.UserName, showSettings, new String[0], programSettingsController.getSettingsFile().getProgramSettingsID()), Variables.UsersFolder, Strings.UserName, Variables.UsersExtension, false);
            log.info("User settings file was generated, skipping version check.");
            return;
        }
        Object userSettingsFile = new FileManager().loadFile(Variables.UsersFolder, Strings.UserName, Variables.UsersExtension);
        if (userSettingsFile instanceof UserSettings) {
            UserSettings userSettings = (UserSettings) userSettingsFile;
            if (Variables.UserSettingsFileVersion == userSettings.getUserSettingsFileVersion()) {
                log.info("User settings file versions matched.");
            } else {
                log.info("User settings file versions didn't match " + Variables.UserSettingsFileVersion + " - " + userSettings.getUserSettingsFileVersion() + ", Updating...");
                convertUserSettingsFile(userSettings.getUserSettingsFileVersion(), Variables.UserSettingsFileVersion);
            }
        } else if (userSettingsFile instanceof HashMap) {
            //noinspection unchecked
            HashMap<String, HashMap<String, HashMap<String, String>>> oldUserSettingsFile = (HashMap<String, HashMap<String, HashMap<String, String>>>) userSettingsFile;
            int oldVersion;
            if (oldUserSettingsFile.containsKey("UserSettings")) {
                oldVersion = Integer.parseInt(oldUserSettingsFile.get("UserSettings").get("UserVersions").get("0"));
            } else oldVersion = -2;
            if (Variables.UserSettingsFileVersion == oldVersion) {
                log.info("User settings file versions matched.");
            } else {
                log.info("User settings file versions didn't match " + Variables.UserSettingsFileVersion + " - " + oldVersion + ", Updating...");
                convertUserSettingsFile(oldVersion, Variables.UserSettingsFileVersion);
            }
        } else {
            log.severe("Was unable to load user file, Forcing shut down.");
            System.exit(0);
        }
    }

    public void updateShowFile() {
        if (Variables.ShowFileVersion == programSettingsController.getSettingsFile().getShowFileVersion()) {
            log.info("Show file versions matched.");
        } else {
            log.info("Show file versions didn't match " + Variables.ShowFileVersion + " - " + programSettingsController.getSettingsFile().getShowFileVersion() + ", Updating...");
            convertShowFile(programSettingsController.getSettingsFile().getShowFileVersion(), Variables.ShowFileVersion);
        }
    }

    // This checks if the user has the latest show information, and if they don't, updates it to the latest.
    public void updateMainDirectoryVersion() {
        int mainDirectoryVersion = programSettingsController.getSettingsFile().getMainDirectoryVersion();
        if (mainDirectoryVersion == userInfoController.getUserSettings().getUserDirectoryVersion()) {
            log.info("User directory version matched, Now checking if number of directories match...");
            if (directoryController.getDirectories().size() == programSettingsController.getSettingsFile().getNumberOfDirectories()) {
                log.info("Number of directories matched, Now checking if programID's match...");
                boolean allMatched = true;
                for (Directory directory : directoryController.getDirectories()) {
                    if (directory.getLastProgramID() != programSettingsController.getSettingsFile().getProgramSettingsID()) {
                        log.info("programID's didn't match, updating...");
                        allMatched = false;
                        programSettingsController.setMainDirectoryVersion(mainDirectoryVersion + 1);
                        updateUserShows(mainDirectoryVersion + 1);
                        break;
                    }
                }
                if (allMatched) log.info("programID's matched.");
                else {
                    directoryController.getDirectories().forEach(aDirectory -> {
                        if (aDirectory.getLastProgramID() != programSettingsController.getSettingsFile().getProgramSettingsID()) {
                            aDirectory.setLastProgramID(programSettingsController.getSettingsFile().getProgramSettingsID());
                            directoryController.saveDirectory(aDirectory, false);
                        }
                    });
                }
            } else {
                log.info("Number of directories didn't match, updating...");
                programSettingsController.setMainDirectoryVersion(mainDirectoryVersion + 1);
                updateUserShows(mainDirectoryVersion + 1);
            }
        } else {
            log.info("User directory version didn't match, Updating...");
            updateUserShows(mainDirectoryVersion);
        }
    }

    // These are ran if the versions didn't match, and updates the file with the latest information. It uses the oldVersion to find where it last updated, and runs through all the cases at and below it to catch it up, than updates the file to the latest version if it ran successfully.
    @SuppressWarnings("SameParameterValue")
    private void convertProgramSettingsFile(int oldVersion, int newVersion) {
        ProgramSettings programSettings = null;
        boolean updated = false;
        String fileType = "Program settings";
        if (oldVersion <= 1008) {
            @SuppressWarnings("unchecked") HashMap<String, ArrayList<String>> oldProgramSettingsFile = (HashMap<String, ArrayList<String>>) new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension);
            switch (oldVersion) {
                case -2:
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(0, String.valueOf(Variables.ProgramSettingsFileVersion));
                    oldProgramSettingsFile.put("ProgramVersions", temp);
                    updatedText(fileType, -2, 1);
                case 1:
                    oldProgramSettingsFile.get("General").add(1, String.valueOf(Variables.dataFolder));
                    updatedText(fileType, 1, 2);
                case 2:
                    // Nothing belongs here, Just for logging purposes.
                    updatedText(fileType, 2, 1001);
                case 1001:
                    oldProgramSettingsFile.get("General").remove(1);
                    updatedText(fileType, 1001, 1002);
                case 1002:
                    if (oldProgramSettingsFile.containsKey("Directories")) {
                        ArrayList<String> directories = new ArrayList<>();
                        oldProgramSettingsFile.get("Directories").forEach(aDirectory -> {
                            int index = oldProgramSettingsFile.get("Directories").indexOf(aDirectory);
                            directories.add(index + ">" + aDirectory);
                        });
                        oldProgramSettingsFile.replace("Directories", directories);
                    }
                    updatedText(fileType, 1002, 1003);
                case 1003:
                    oldProgramSettingsFile.get("ProgramVersions").add(1, "1");
                    updatedText(fileType, 1003, 1004);
                case 1004:
                    oldProgramSettingsFile.get("General").add(1, "false");
                    updatedText(fileType, 1004, 1005);
                case 1005:
                    oldProgramSettingsFile.get("General").add(2, Strings.EmptyString);
                    updatedText(fileType, 1005, 1006);
                case 1006:
                    oldProgramSettingsFile.get("ProgramVersions").add(2, "-2");
                    updatedText(fileType, 1006, 1007);
                case 1007:
                    ArrayList<String> temp2 = new ArrayList<>();
                    temp2.add(0, "239");
                    temp2.add(1, "29");
                    temp2.add(2, "48");
                    temp2.add(3, "50");
                    oldProgramSettingsFile.put("GuiNumberSettings", temp2);
                    //noinspection ReuseOfLocalVariable
                    temp2 = new ArrayList<>();
                    temp2.add(0, "true");
                    temp2.add(1, "true");
                    temp2.add(2, "false");
                    temp2.add(3, "false");
                    oldProgramSettingsFile.put("GuiBooleanSettings", temp2);
                    updatedText(fileType, 1007, 1008);
                case 1008:
                    programSettings = new ProgramSettings(newVersion,
                            Integer.parseInt(oldProgramSettingsFile.get("ProgramVersions").get(1)),
                            Integer.parseInt(oldProgramSettingsFile.get("ProgramVersions").get(2)),
                            Integer.parseInt(oldProgramSettingsFile.get("General").get(0)),
                            -1,
                            Boolean.parseBoolean(oldProgramSettingsFile.get("General").get(1)),
                            Strings.EmptyString,
                            false,
                            false,
                            Boolean.parseBoolean(oldProgramSettingsFile.get("DefaultUser").get(0)),
                            oldProgramSettingsFile.get("DefaultUser").get(1),
                            Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(0)),
                            Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(1)),
                            Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(2)),
                            Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(3)),
                            Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(0)),
                            Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(1)),
                            Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(2)),
                            Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(3))
                    );
                    neededObjects = new Object[]{oldProgramSettingsFile.get("Directories")};
                    updatedText(fileType, 1008, 1009);
                    oldVersion = 1009; //This is so the next switch will run from where this left off.
            }
        }
        if (programSettings == null) { // This loads in the programSettingsFile assuming the previous switch didn't run. If it did run, then the programSettings file was just generated, and doesn't need to be loaded.
            programSettingsController.loadProgramSettingsFile();
            programSettings = programSettingsController.getSettingsFile();
        }
        switch (oldVersion) {
            case 1009:
                programSettings.setTimeToWaitForDirectory(Variables.defaultTimeToWaitForDirectory);
                updatedText(fileType, 1009, 1010);
            case 1010:
                programSettings.setRecordChangesForNonActiveShows(false);
                programSettings.setRecordChangedSeasonsLowerThanCurrent(false);
                updatedText(fileType, 1010, 1011);
                programSettingsController.setSettingsFile(programSettings);
                updated = true;
        }
        if (updated) {
            // Update Program Settings File Version
            programSettings.setProgramSettingsFileVersion(newVersion);
            new FileManager().save(programSettings, Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension, true);
            log.info("Program settings file was successfully updated to version " + newVersion + '.');
        } else log.info("Program settings file was not updated. This is an error, please report.");
    }


    @SuppressWarnings("SameParameterValue")
    private void convertUserSettingsFile(int oldVersion, int newVersion) {
        UserSettings userSettings = null;
        boolean updated = false;
        String fileType = "User settings";
        if (oldVersion <= 1001) {
            @SuppressWarnings("unchecked") HashMap<String, HashMap<String, HashMap<String, String>>> oldUserSettingsFile = (HashMap<String, HashMap<String, HashMap<String, String>>>) new FileManager().loadFile(Variables.UsersFolder, Strings.UserName, Variables.UsersExtension);
            switch (oldVersion) {
                case -2:
                    HashMap<String, HashMap<String, String>> tempPut;
                    HashMap<String, String> temp;
                    tempPut = new HashMap<>();
                    temp = new HashMap<>();
                    temp.put("0", String.valueOf(Variables.UserSettingsFileVersion));
                    tempPut.put("UserVersions", temp);
                    oldUserSettingsFile.put("UserSettings", tempPut);
                    updatedText(fileType, -2, 1);
                case 1:
                    if (oldUserSettingsFile.get("ShowSettings") != null) {
                        oldUserSettingsFile.get("ShowSettings").keySet().forEach(aShow -> oldUserSettingsFile.get("ShowSettings").get(aShow).put("isHidden", "false"));
                    }
                    updatedText(fileType, 1, 2);
                case 2:
                    showInfoController.getShowsList().forEach(aShow -> {
                        if (!oldUserSettingsFile.containsKey("ShowSettings")) {
                            oldUserSettingsFile.put("ShowSettings", new HashMap<>());
                        }
                        if (!oldUserSettingsFile.get("ShowSettings").keySet().contains(aShow)) {
                            log.info("Adding " + aShow + " to user settings file.");
                            HashMap<String, String> temp2 = new HashMap<>();
                            temp2.put("isActive", "false");
                            temp2.put("isIgnored", "false");
                            temp2.put("isHidden", "false");
                            temp2.put("CurrentSeason", String.valueOf(showInfoController.findLowestSeason(aShow)));
                            Set<Integer> episodes = showInfoController.getEpisodesList(aShow, Integer.parseInt(temp2.get("CurrentSeason")));
                            temp2.put("CurrentEpisode", String.valueOf(showInfoController.findLowestEpisode(episodes)));
                            oldUserSettingsFile.get("ShowSettings").put(aShow, temp2);
                        }
                    });
                    updatedText(fileType, 2, 1000);
                case 1000:
                    if (!oldUserSettingsFile.containsKey("ShowSettings")) {
                        oldUserSettingsFile.put("ShowSettings", new HashMap<>());
                    }
                    oldUserSettingsFile.get("UserSettings").get("UserVersions").put("1", "0");
                    updatedText(fileType, 1000, 1001);
                case 1001:
                    Map<String, UserShowSettings> showsConverted = new HashMap<>();
                    oldUserSettingsFile.get("ShowSettings").forEach((showName, showSettings) -> showsConverted.put(showName, new UserShowSettings(showName, Boolean.parseBoolean(showSettings.get("isActive")), Boolean.parseBoolean(showSettings.get("isIgnored")), Boolean.parseBoolean(showSettings.get("isHidden")), Integer.parseInt(showSettings.get("CurrentSeason")), Integer.parseInt(showSettings.get("CurrentEpisode")))));
                    userSettings = new UserSettings(Strings.UserName, showsConverted, new String[0], programSettingsController.getSettingsFile().getProgramSettingsID());
                    updatedText(fileType, 1001, 1002);
                    oldVersion = 1002;
            }
        }
        if (userSettings == null) {
            userInfoController.loadUserInfo();
            userSettings = userInfoController.getUserSettings();
        }
        switch (oldVersion) {
            case 1002:
                if (userSettings.getChanges() == null) {
                    userSettings.setChanges(new String[0]);
                }
                updatedText(fileType, 1002, 1003);
                updated = true;
        }
        if (updated) {
            // Update User Settings File Version
            userSettings.setUserSettingsFileVersion(newVersion);
            new FileManager().save(userSettings, Variables.UsersFolder, Strings.UserName, Variables.UsersExtension, true);
            log.info("User settings file was successfully updated to version " + newVersion + '.');
        } else log.info("User settings file was not updated. This is an error, please report.");
    }

    private void convertShowFile(int oldVersion, @SuppressWarnings("SameParameterValue") int newVersion) {
        boolean updated = false;
        String fileType = "ShowsFile";
        switch (oldVersion) {
            case -2:
                //noinspection unchecked
                HashMap<String, HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileHashMap = new HashMap();
                //noinspection unchecked
                ArrayList<String> directories = (ArrayList<String>) neededObjects[0];
                directories.forEach(aString -> {
                    String directoryFilename = "Directory-" + aString.split(">")[0];
                    FileManager fileManager = new FileManager();
                    //noinspection unchecked
                    showsFileHashMap.put(aString, (HashMap<String, HashMap<Integer, HashMap<String, String>>>) fileManager.loadFile(Variables.DirectoriesFolder, directoryFilename, Variables.ShowsExtension));
                    fileManager.deleteFile(Variables.dataFolder + Variables.DirectoriesFolder, directoryFilename, Variables.ShowsExtension);
                });
                showsFileHashMap.forEach((directory, aHashMap) -> {
                    Map<String, Show> showsMap = new HashMap<>();
                    aHashMap.forEach((showName, showHashMap) -> {
                        Map<Integer, Season> seasonsMap = new HashMap<>();
                        showHashMap.forEach((season, seasonHashMap) -> {
                            Map<Integer, Episode> episodesMap = new HashMap<>();
                            seasonHashMap.forEach((episode, episodeFileName) -> {
                                if (episode.contains("+")) {
                                    for (String episodes : episode.split("[+]")) {
                                        episodesMap.put(Integer.parseInt(episodes), new Episode(Integer.parseInt(episodes), episodeFileName, true));
                                    }
                                } else {
                                    episodesMap.put(Integer.parseInt(episode), new Episode(Integer.parseInt(episode), episodeFileName, false));
                                }
                            });
                            seasonsMap.put(season, new Season(season, episodesMap));
                        });
                        showsMap.put(showName, new Show(showName, seasonsMap));
                    });
                    String[] splitResult = directory.split(">")[1].split(Pattern.quote(Strings.FileSeparator));
                    String fileName = "";
                    for (String singleSplit : splitResult) {
                        if (singleSplit.contains(":")) {
                            singleSplit = singleSplit.replace(":", "");
                        }
                        if (!singleSplit.isEmpty()) {
                            if (fileName.isEmpty()) {
                                fileName = singleSplit;
                            } else fileName += '_' + singleSplit;
                        }
                    }
                    directoryController.saveDirectory(new Directory(new File(directory.split(">")[1]), fileName, Integer.parseInt(directory.split(">")[0]), -1, showsMap, programSettingsController.getSettingsFile().getProgramSettingsID()), false);
                });
                updatedText(fileType, 1000, -2);
                log.info("Shows file has been updated from version -2 -> 1000.");
                updated = true;
        }
        if (updated) {
            // Update Program Settings File Version
            programSettingsController.getSettingsFile().setShowFileVersion(newVersion);
            programSettingsController.saveSettingsFile();
            log.info("Show file was successfully updated to version " + newVersion + '.');
        } else log.info("Show file was not updated. This is an error, please report.");
    }

    // This finds what shows have been added / remove if another user has ran the program (and found updated information) since you last ran your profile.
    private void updateUserShows(int newVersion) {
        ArrayList<String> shows = showInfoController.getShowsList();
        ArrayList<String> userShows = userInfoController.getAllNonIgnoredShows();
        ArrayList<String> ignoredShows = userInfoController.getIgnoredShows();
        boolean[] changed = {false};
        shows.forEach(aShow -> {
            if (!userShows.contains(aShow) && !ignoredShows.contains(aShow)) {
                log.info(aShow + " was found during user shows update and added.");
                ChangeReporter.addChange("+ " + aShow);
                userInfoController.addNewShow(aShow);
                changed[0] = true;
            } else if (ignoredShows.contains(aShow)) {
                log.info(aShow + " was found during user shows update and un-ignored.");
                ChangeReporter.addChange("+ " + aShow);
                userInfoController.setIgnoredStatus(aShow, false);
                changed[0] = true;
            }
        });
        userShows.forEach(aShow -> {
            if (!shows.contains(aShow)) {
                log.info(aShow + " wasn't found during user shows update.");
                ChangeReporter.addChange("- " + aShow);
                userInfoController.setIgnoredStatus(aShow, true);
                changed[0] = true;
            }
        });
        if (!changed[0]) {
            log.info("No changes found.");
        }
        userInfoController.getUserSettings().setUserDirectoryVersion(newVersion);
    }

    private void updatedText(String fileType, int oldVersion, int newVersion) {
        log.info(fileType + " file has been updated from version " + oldVersion + " -> " + newVersion);
    }
}