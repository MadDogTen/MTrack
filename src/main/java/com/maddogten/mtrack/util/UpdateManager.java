package com.maddogten.mtrack.util;

import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.information.settings.ProgramSettings;
import com.maddogten.mtrack.information.settings.UserSettings;
import com.maddogten.mtrack.information.settings.UserShowSettings;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.information.show.Episode;
import com.maddogten.mtrack.information.show.Season;
import com.maddogten.mtrack.information.show.Show;
import com.maddogten.mtrack.io.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class UpdateManager {
    private final Logger log = Logger.getLogger(UpdateManager.class.getName());

    private Object[] neededObjects = new Object[0];

    // These both compare the version defined in Variables (Latest Version) with the version the files are currently at (Old Version), and if they don't match, converts the files to the latest version.
    public void updateProgramSettingsFile() {
        Object programSettingsFile = new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingFileExtension);
        if (programSettingsFile instanceof ProgramSettings) {
            ProgramSettings programSettings = (ProgramSettings) programSettingsFile;
            if (Variables.ProgramSettingsFileVersion == programSettings.getProgramSettingsFileVersion())
                log.info("Program settings file versions matched.");
            else {
                log.info("Program settings file versions didn't match " + Variables.ProgramSettingsFileVersion + " - " + programSettings.getProgramSettingsFileVersion() + ", Updating...");
                convertProgramSettingsFile(programSettings.getProgramSettingsFileVersion(), Variables.ProgramSettingsFileVersion);
            }
        } else if (programSettingsFile instanceof HashMap) {
            //noinspection unchecked
            HashMap<String, ArrayList<String>> oldProgramSettingsFile = (HashMap<String, ArrayList<String>>) programSettingsFile;
            int currentVersion;
            if (oldProgramSettingsFile.containsKey("ProgramVersions"))
                currentVersion = Integer.parseInt(oldProgramSettingsFile.get("ProgramVersions").get(0));
            else currentVersion = -2;
            if (Variables.ProgramSettingsFileVersion == currentVersion)
                log.info("Program settings file versions matched.");
            else {
                log.info("Program settings file versions didn't match " + Variables.ProgramSettingsFileVersion + " - " + currentVersion + ", Updating...");
                convertProgramSettingsFile(currentVersion, Variables.ProgramSettingsFileVersion);
            }
        } else {
            log.severe("Unable to load program file, Forcing shut down.");
            System.exit(0);
        }
    }

    public void updateUserSettingsFile() {
        if (!ClassHandler.userInfoController().getAllUsers().contains(Strings.UserName.getValue())) {
            log.info("Attempting to generate settings file for " + Strings.UserName.getValue() + '.');
            Map<String, UserShowSettings> showSettings = new HashMap<>();
            for (String aShow : ClassHandler.showInfoController().getShowsList()) {
                if (Variables.genUserShowInfoAtFirstFound)
                    showSettings.put(aShow, new UserShowSettings(aShow, ClassHandler.showInfoController().findLowestSeason(aShow), ClassHandler.showInfoController().findLowestEpisode(ClassHandler.showInfoController().getEpisodesList(aShow, ClassHandler.showInfoController().findLowestSeason(aShow)))));
                else showSettings.put(aShow, new UserShowSettings(aShow, 1, 1));
            }
            new FileManager().save(new UserSettings(Strings.UserName.getValue(), showSettings, true, new String[0], new HashMap<>(), ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsID()), Variables.UsersFolder, Strings.UserName.getValue(), Variables.UserFileExtension, false);
            log.info("User settings file was generated, skipping version check.");
            return;
        }
        Object userSettingsFile = new FileManager().loadFile(Variables.UsersFolder, Strings.UserName.getValue(), Variables.UserFileExtension);
        if (userSettingsFile instanceof UserSettings) {
            UserSettings userSettings = (UserSettings) userSettingsFile;
            if (Variables.UserSettingsFileVersion == userSettings.getUserSettingsFileVersion())
                log.info("User settings file versions matched.");
            else {
                log.info("User settings file versions didn't match " + Variables.UserSettingsFileVersion + " - " + userSettings.getUserSettingsFileVersion() + ", Updating...");
                convertUserSettingsFile(userSettings.getUserSettingsFileVersion(), Variables.UserSettingsFileVersion);
            }
        } else if (userSettingsFile instanceof HashMap) {
            //noinspection unchecked
            HashMap<String, HashMap<String, HashMap<String, String>>> oldUserSettingsFile = (HashMap<String, HashMap<String, HashMap<String, String>>>) userSettingsFile;
            int oldVersion;
            if (oldUserSettingsFile.containsKey("UserSettings"))
                oldVersion = Integer.parseInt(oldUserSettingsFile.get("UserSettings").get("UserVersions").get("0"));
            else oldVersion = -2;
            if (Variables.UserSettingsFileVersion == oldVersion) log.info("User settings file versions matched.");
            else {
                log.info("User settings file versions didn't match " + Variables.UserSettingsFileVersion + " - " + oldVersion + ", Updating...");
                convertUserSettingsFile(oldVersion, Variables.UserSettingsFileVersion);
            }
        } else {
            log.severe("Was unable to load user file, Forcing shut down.");
            System.exit(0);
        }
    }

    public void updateShowFile() {
        if (-2 == ClassHandler.programSettingsController().getSettingsFile().getShowFileVersion()) {
            log.info("Older show file(s) found, updating..." + Variables.DirectoryFileVersion + " - " + ClassHandler.programSettingsController().getSettingsFile().getShowFileVersion() + ", Updating...");
            convertShowFile();
        }

        for (Directory directory : ClassHandler.directoryController().findDirectories(true, false, true)) {
            if (Variables.DirectoryFileVersion == directory.getDirectoryFileVersion())
                log.info(directory.getFileName() + " directory versions matched.");
            else {
                log.info(directory.getFileName() + " directory version didn't match " + Variables.DirectoryFileVersion + " - " + directory.getDirectoryFileVersion() + ", Updating...");
                convertDirectory(directory, directory.getDirectoryFileVersion(), Variables.DirectoryFileVersion);
            }
        }
    }

    // This checks if the user has the latest show information, and if they don't, updates it to the latest.
    public void updateMainDirectoryVersion() {
        int mainDirectoryVersion = ClassHandler.programSettingsController().getSettingsFile().getMainDirectoryVersion();
        if (mainDirectoryVersion == ClassHandler.userInfoController().getUserSettings().getUserDirectoryVersion()) {
            log.info("User directory version matched, Now checking if number of directories match...");
            if (ClassHandler.directoryController().findDirectories(false, true, false).size() == ClassHandler.programSettingsController().getSettingsFile().getNumberOfDirectories()) {
                log.info("Number of directories matched, Now checking if programID's match...");
                boolean allMatched = true;
                for (Directory directory : ClassHandler.directoryController().findDirectories(true, false, true)) {
                    if (directory.getLastProgramID() != ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsID()) {
                        log.info("programID's didn't match, updating...");
                        allMatched = false;
                        ClassHandler.programSettingsController().setMainDirectoryVersion(mainDirectoryVersion + 1);
                        updateUserShows(mainDirectoryVersion + 1);
                        break;
                    }
                }
                if (allMatched) log.info("programID's matched.");
                else {
                    ClassHandler.directoryController().findDirectories(true, false, true).forEach(aDirectory -> {
                        if (aDirectory.getLastProgramID() != ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsID()) {
                            aDirectory.setLastProgramID(ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsID());
                            ClassHandler.directoryController().saveDirectory(aDirectory, false);
                        }
                    });
                }
            } else {
                log.info("Number of directories didn't match, updating...");
                ClassHandler.programSettingsController().setMainDirectoryVersion(mainDirectoryVersion + 1);
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
        ProgramSettings programSettings;
        boolean updated = false;
        String fileType = "Program settings";
        if (oldVersion <= 1008) {
            @SuppressWarnings("unchecked") HashMap<String, ArrayList<String>> oldProgramSettingsFile = (HashMap<String, ArrayList<String>>) new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingFileExtension);
            programSettings = new ProgramSettings();
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
                        ArrayList<String> directories = new ArrayList<>(oldProgramSettingsFile.get("Directories").size());
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
                    ArrayList<String> temp3 = new ArrayList<>();
                    temp3.add(0, "true");
                    temp3.add(1, "true");
                    temp3.add(2, "false");
                    temp3.add(3, "false");
                    oldProgramSettingsFile.put("GuiBooleanSettings", temp3);
                    updatedText(fileType, 1007, 1008);
                case 1008:
                    programSettings.setProgramSettingsFileVersion(newVersion);
                    programSettings.setMainDirectoryVersion(Integer.parseInt(oldProgramSettingsFile.get("ProgramVersions").get(1)));
                    programSettings.setShowFileVersion(Integer.parseInt(oldProgramSettingsFile.get("ProgramVersions").get(2)));
                    programSettings.setUpdateSpeed(Integer.parseInt(oldProgramSettingsFile.get("General").get(0)));
                    programSettings.setShow0Remaining(Boolean.parseBoolean(oldProgramSettingsFile.get("General").get(1)));
                    programSettings.setUseDefaultUser(Boolean.parseBoolean(oldProgramSettingsFile.get("DefaultUser").get(0)));
                    programSettings.setDefaultUser(oldProgramSettingsFile.get("DefaultUser").get(1));
                    programSettings.setShowColumnWidth(Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(0)));
                    programSettings.setRemainingColumnWidth(Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(1)));
                    programSettings.setSeasonColumnWidth(Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(2)));
                    programSettings.setEpisodeColumnWidth(Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(3)));
                    programSettings.setShowColumnVisibility(Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(0)));
                    programSettings.setRemainingColumnVisibility(Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(1)));
                    programSettings.setSeasonColumnVisibility(Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(2)));
                    programSettings.setEpisodeColumnVisibility(Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(3)));
                    neededObjects = new Object[]{oldProgramSettingsFile.get("Directories")};
                    updatedText(fileType, 1008, 1009);
                    oldVersion = 1009; //This is so the next switch will run from where this left off.
            }
        } else {
            ClassHandler.programSettingsController().loadProgramSettingsFile();
            programSettings = ClassHandler.programSettingsController().getSettingsFile();
        }
        switch (oldVersion) {
            case 1009:
                programSettings.setTimeToWaitForDirectory(Variables.defaultTimeToWaitForDirectory);
                updatedText(fileType, 1009, 1010);
            case 1010:
                programSettings.setRecordChangesForNonActiveShows(false);
                programSettings.setRecordChangedSeasonsLowerThanCurrent(false);
                updatedText(fileType, 1010, 1011);
            case 1011:
                programSettings.setStageMoveWithParentAndBlockParent(true);
                programSettings.setDisableAutomaticShowUpdating(false);
                updatedText(fileType, 1011, 1012);
            case 1012:
                programSettings.setEnableSpecialEffects(true);
                programSettings.setEnableAutomaticSaving(true);
                programSettings.setSaveSpeed(Variables.defaultSavingSpeed);
                updatedText(fileType, 1012, 1013);
            case 1013:
                programSettings.setFileLogging(true);
                programSettings.setUseRemoteDatabase(false);
                updatedText(fileType, 1013, 1014);
            case 1014:
                programSettings.setShowActiveShows(false);
                updatedText(fileType, 1014, 1015);
                ClassHandler.programSettingsController().setSettingsFile(programSettings);
                updated = true;
        }
        if (updated) {
            programSettings.setProgramSettingsFileVersion(newVersion); // Update Program Settings File Version
            new FileManager().save(programSettings, Strings.EmptyString, Strings.SettingsFileName, Variables.SettingFileExtension, true);
            log.info("Program settings file was successfully updated to version " + newVersion + '.');
        } else log.info("Program settings file was not updated. This is an error, please report.");
    }

    @SuppressWarnings("SameParameterValue")
    private void convertUserSettingsFile(int oldVersion, int newVersion) {
        UserSettings userSettings = null;
        boolean updated = false;
        String fileType = "User settings";
        if (oldVersion <= 1001) {
            @SuppressWarnings("unchecked") HashMap<String, HashMap<String, HashMap<String, String>>> oldUserSettingsFile = (HashMap<String, HashMap<String, HashMap<String, String>>>) new FileManager().loadFile(Variables.UsersFolder, Strings.UserName.getValue(), Variables.UserFileExtension);
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
                    if (oldUserSettingsFile.get("ShowSettings") != null)
                        oldUserSettingsFile.get("ShowSettings").keySet().forEach(aShow -> oldUserSettingsFile.get("ShowSettings").get(aShow).put("isHidden", "false"));
                    updatedText(fileType, 1, 2);
                case 2:
                    ClassHandler.showInfoController().getShowsList().forEach(aShow -> {
                        if (!oldUserSettingsFile.containsKey("ShowSettings"))
                            oldUserSettingsFile.put("ShowSettings", new HashMap<>());
                        if (!oldUserSettingsFile.get("ShowSettings").keySet().contains(aShow)) {
                            log.info("Adding " + aShow + " to user settings file.");
                            HashMap<String, String> temp2 = new HashMap<>();
                            temp2.put("isActive", "false");
                            temp2.put("isIgnored", "false");
                            temp2.put("isHidden", "false");
                            temp2.put("CurrentSeason", String.valueOf(ClassHandler.showInfoController().findLowestSeason(aShow)));
                            Set<Integer> episodes = ClassHandler.showInfoController().getEpisodesList(aShow, Integer.parseInt(temp2.get("CurrentSeason")));
                            temp2.put("CurrentEpisode", String.valueOf(ClassHandler.showInfoController().findLowestEpisode(episodes)));
                            oldUserSettingsFile.get("ShowSettings").put(aShow, temp2);
                        }
                    });
                    updatedText(fileType, 2, 1000);
                case 1000:
                    if (!oldUserSettingsFile.containsKey("ShowSettings"))
                        oldUserSettingsFile.put("ShowSettings", new HashMap<>());
                    oldUserSettingsFile.get("UserSettings").get("UserVersions").put("1", "0");
                    updatedText(fileType, 1000, 1001);
                case 1001:
                    Map<String, UserShowSettings> showsConverted = new HashMap<>();
                    oldUserSettingsFile.get("ShowSettings").forEach((showName, showSettings) -> showsConverted.put(showName, new UserShowSettings(showName, Boolean.parseBoolean(showSettings.get("isActive")), Boolean.parseBoolean(showSettings.get("isIgnored")), Boolean.parseBoolean(showSettings.get("isHidden")), Integer.parseInt(showSettings.get("CurrentSeason")), Integer.parseInt(showSettings.get("CurrentEpisode")))));
                    userSettings = new UserSettings(Strings.UserName.getValue(), showsConverted, true, new String[0], new HashMap<>(), ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsID());
                    updatedText(fileType, 1001, 1002);
                    oldVersion = 1002;
            }
        } else {
            ClassHandler.userInfoController().loadUserInfo();
            userSettings = ClassHandler.userInfoController().getUserSettings();
        }
        switch (oldVersion) {
            case 1002:
                //noinspection ConstantConditions
                userSettings.setChanges(new String[0]);
                updatedText(fileType, 1002, 1003);
            case 1003:
                //noinspection ConstantConditions
                userSettings.setShowUsername(true);
                updatedText(fileType, 1003, 1004);
            case 1004:
                //noinspection ConstantConditions
                userSettings.setChangedShowsStatus(new HashMap<>());
                updatedText(fileType, 1004, 1005);
            case 1005:
                if (userSettings != null)
                    userSettings.getShowSettings().forEach((showName, userShowSettings) -> userShowSettings.setRemaining(ClassHandler.userInfoController().getRemainingNumberOfEpisodes(showName)));
                updatedText(fileType, 1005, 1006);
                updated = true;
        }
        if (updated) {
            // Update User Settings File Version
            userSettings.setUserSettingsFileVersion(newVersion);
            new FileManager().save(userSettings, Variables.UsersFolder, Strings.UserName.getValue(), Variables.UserFileExtension, true);
            log.info("User settings file was successfully updated to version " + newVersion + '.');
        } else log.info("User settings file was not updated. This is an error, please report.");
    }

    private void convertShowFile() {
        //noinspection unchecked
        HashMap<String, HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileHashMap = new HashMap();
        //noinspection unchecked
        ArrayList<String> directories = (ArrayList<String>) neededObjects[0];
        directories.forEach(aString -> {
            String directoryFilename = "Directory-" + aString.split(">")[0];
            FileManager fileManager = new FileManager();
            //noinspection unchecked
            showsFileHashMap.put(aString, (HashMap<String, HashMap<Integer, HashMap<String, String>>>) fileManager.loadFile(Variables.DirectoriesFolder, directoryFilename, Variables.ShowFileExtension));
            if (!fileManager.deleteFile(Variables.DirectoriesFolder, directoryFilename, Variables.ShowFileExtension))
                log.info("Wasn't able to delete directory.");
        });
        showsFileHashMap.forEach((directory, aHashMap) -> {
            Map<String, Show> showsMap = new HashMap<>();
            aHashMap.forEach((showName, showHashMap) -> {
                Map<Integer, Season> seasonsMap = new HashMap<>();
                showHashMap.forEach((season, seasonHashMap) -> {
                    Map<Integer, Episode> episodesMap = new HashMap<>();
                    seasonHashMap.forEach((episode, episodeFileName) -> {
                        if (episode.contains("+")) {
                            for (String episodes : episode.split("[+]"))
                                episodesMap.put(Integer.parseInt(episodes), new Episode(Integer.parseInt(episodes), episodeFileName, true));
                        } else
                            episodesMap.put(Integer.parseInt(episode), new Episode(Integer.parseInt(episode), episodeFileName, false));
                    });
                    seasonsMap.put(season, new Season(season, episodesMap));
                });
                showsMap.put(showName, new Show(showName, seasonsMap));
            });
            String[] splitResult = directory.split(">")[1].split(Pattern.quote(Strings.FileSeparator));
            String fileName = "";
            for (String singleSplit : splitResult) {
                if (singleSplit.contains(":")) singleSplit = singleSplit.replace(":", "");
                if (!singleSplit.isEmpty()) {
                    if (fileName.isEmpty()) fileName = singleSplit;
                    else fileName += '_' + singleSplit;
                }
            }
            ClassHandler.directoryController().saveDirectory(new Directory(new File(directory.split(">")[1]), fileName, -1, showsMap), false);
        });
        updatedText("ShowsFile", -2, 1000);
        // Update Program Settings File Version
        ClassHandler.programSettingsController().getSettingsFile().setShowFileVersion(1000);
        ClassHandler.programSettingsController().saveSettingsFile();
        log.info("Show file was successfully updated to version " + Variables.DirectoryFileVersion + '.');
    }

    @SuppressWarnings("SameParameterValue")
    private void convertDirectory(Directory directory, int oldVersion, int newVersion) {
        boolean updated = false;
        String fileType = "ShowsFile";
        GetShowInfo getShowInfo = new GetShowInfo();
        switch (oldVersion) {
            case 0:
                updatedText(fileType, 0, 1000);
            case 1000:
                if (Variables.useOnlineDatabase) {
                    directory.getShows().forEach((showName, show) -> {
                        try {
                            show.setShowID(getShowInfo.getShowID(showName));
                            if (show.getShowID() != -1) {
                                HashMap<Integer, Integer> showInfo = getShowInfo.getShowInfo(show.getShowID());
                                if (!showInfo.isEmpty()) {
                                    int highestSeason = -1;
                                    for (Integer season : showInfo.keySet()) {
                                        if (season == -1 || season > highestSeason) highestSeason = season;
                                    }
                                    show.setNumberOfSeasons(highestSeason);
                                    showInfo.keySet().forEach(seasonInt -> {
                                        if (show.containsSeason(seasonInt))
                                            show.getSeason(seasonInt).setNumberOfEpisodes(showInfo.get(seasonInt));
                                    });
                                }
                            }
                        } catch (IOException e) {
                            log.info("Unable to get information for: \"" + showName + "\".");
                        }
                    });
                }
                updatedText(fileType, 1000, 1001);
            case 1001:
                log.info("Deleting then recreating directory: " + directory.getFileName());
                ClassHandler.directoryController().removeDirectory(directory);
                directory = new Directory(directory.getDirectory(), directory.getFileName(), directory.getPriority(), directory.getShows());
                log.info("Finished recreating directory: " + directory.getFileName());
                updatedText(fileType, 1001, 1002);
                updated = true;
        }
        if (updated) {
            directory.setDirectoryFileVersion(newVersion);
            ClassHandler.directoryController().saveDirectory(directory, false);
            log.info("Directory was successfully updated to version " + newVersion + '.');
        } else log.info("Directory was not updated. This is an error, please report.");
    }

    // This finds what shows have been added / remove if another user has ran the program (and found updated information) since you last ran your profile.
    private void updateUserShows(int newVersion) {
        ArrayList<String> shows = ClassHandler.showInfoController().getShowsList();
        ArrayList<String> userShows = ClassHandler.userInfoController().getAllNonIgnoredShows();
        ArrayList<String> ignoredShows = ClassHandler.userInfoController().getIgnoredShows();
        boolean[] changed = {false};
        shows.forEach(aShow -> {
            if (!userShows.contains(aShow) && !ignoredShows.contains(aShow)) {
                log.info(aShow + " was found during user shows update and added.");
                ClassHandler.userInfoController().addNewShow(aShow);
                /*ChangeReporter.addChange("+ " + aShow);
                ClassHandler.controller().addChangedShow(aShow, ClassHandler.userInfoController().getRemainingNumberOfEpisodes(aShow));*/
                changed[0] = true;
            } else if (ignoredShows.contains(aShow)) {
                log.info(aShow + " was found during user shows update and un-ignored.");
                ClassHandler.userInfoController().setIgnoredStatus(aShow, false);
                /*ChangeReporter.addChange("+ " + aShow);
                ClassHandler.controller().addChangedShow(aShow, ClassHandler.userInfoController().getRemainingNumberOfEpisodes(aShow));*/
                changed[0] = true;
            } /*else {
                int lastRemaining = ClassHandler.userInfoController().getUserSettings().getAShowSettings(aShow).getRemaining(), currentlyRemaining = ClassHandler.userInfoController().getRemainingNumberOfEpisodes(aShow);
                log.info(String.valueOf(lastRemaining));
                if (lastRemaining != -2 && lastRemaining != currentlyRemaining) {
                    ChangeReporter.addChange((lastRemaining < currentlyRemaining ? "+ " : "- ") + aShow + Strings.DashSeason.getValue() + " | " + Strings.Episode.getValue()); // Add\Fix localizations
                    Map<String, Integer> changedShows = ClassHandler.userInfoController().getUserSettings().getChangedShowsStatus();
                    if (!changedShows.containsKey(aShow)) {
                        changedShows.put(aShow, lastRemaining);
                        ClassHandler.userInfoController().getUserSettings().setChangedShowsStatus(changedShows);
                    }
                }
            }*/ //TODO Finish
        });
        userShows.forEach(aShow -> {
            if (!shows.contains(aShow)) {
                log.info(aShow + " wasn't found during user shows update.");
                ChangeReporter.addChange("- " + aShow);
                ClassHandler.userInfoController().setIgnoredStatus(aShow, true);
                changed[0] = true;
            }
        });
        if (!changed[0]) log.info("No changes found.");
        ClassHandler.userInfoController().getUserSettings().setUserDirectoryVersion(newVersion);
    }

    private void updatedText(String fileType, int oldVersion, int newVersion) {
        log.info(fileType + " file has been updated from version " + oldVersion + " -> " + newVersion);
    }
}
