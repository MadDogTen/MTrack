package program.util;

import program.information.ChangeReporter;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.information.settings.ProgramSettings;
import program.information.settings.UserSettings;
import program.information.settings.UserShowSettings;
import program.information.show.Episode;
import program.information.show.Season;
import program.information.show.Show;
import program.io.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class UpdateManager {
    private final Logger log = Logger.getLogger(UpdateManager.class.getName());

    private final ProgramSettingsController programSettingsController;
    private final ShowInfoController showInfoController;
    private final UserInfoController userInfoController;

    public UpdateManager(ProgramSettingsController programSettingsController, ShowInfoController showInfoController, UserInfoController userInfoController) {
        this.programSettingsController = programSettingsController;
        this.showInfoController = showInfoController;
        this.userInfoController = userInfoController;
    }

    // These both compare the version defined in Variables (Latest Version) with the version the files are currently at (Old Version), and if they don't match, converts the files to the latest version.
    public void updateProgramSettingsFile() {
        ProgramSettings programSettings = null;
        int newVersion = Variables.ProgramSettingsFileVersion;
        try {
            programSettings = (ProgramSettings) new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension);
        } catch (ClassCastException e) {
            log.info("Program file was unable to be loaded.");
        }
        if (programSettings == null) {
            HashMap<String, ArrayList<String>> oldProgramSettingsFile = null;
            try {
                //noinspection unchecked
                oldProgramSettingsFile = (HashMap<String, ArrayList<String>>) new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension);
            } catch (ClassCastException e) {
                log.info("Old Program file was unable to be loaded.");
            }
            if (oldProgramSettingsFile != null) {
                int currentVersion = Integer.parseInt(oldProgramSettingsFile.get("ProgramVersions").get(0));
                if (Variables.ProgramSettingsFileVersion == currentVersion) {
                    log.info("Program settings file versions matched.");
                } else {
                    log.info("Program settings file versions didn't match " + newVersion + " - " + currentVersion + ", Updating...");
                    convertProgramSettingsFile(currentVersion, newVersion);
                }
            } else {
                log.severe("Was unable to load program file, Forcing shut down.");
                System.exit(0);
            }
        } else {
            int currentVersion = programSettings.getProgramSettingsFileVersion();
            if (Variables.ProgramSettingsFileVersion == currentVersion) {
                log.info("Program settings file versions matched.");
            } else {
                log.info("Program settings file versions didn't match " + newVersion + " - " + currentVersion + ", Updating...");
                convertProgramSettingsFile(currentVersion, newVersion);
            }
        }
    }

    public void updateUserSettingsFile() {
        UserSettings userSettings = null;
        int newVersion = Variables.UserSettingsFileVersion;
        if (!userInfoController.getAllUsers().contains(Strings.UserName)) {
            log.info("Attempting to generate settings file for " + Strings.UserName + '.');
            Map<String, UserShowSettings> showSettings = new HashMap<>();
            ArrayList<String> showsList = showInfoController.getShowsList();
            for (String aShow : showsList) {
                int lowestSeason = showInfoController.findLowestSeason(aShow);
                showSettings.put(aShow, new UserShowSettings(aShow, showInfoController.findLowestSeason(aShow), showInfoController.findLowestEpisode(showInfoController.getEpisodesList(aShow, lowestSeason))));
            }
            new FileManager().save(new UserSettings(Strings.UserName, showSettings), Variables.UsersFolder, Strings.UserName, Variables.UsersExtension, false);
        }
        try {
            userSettings = (UserSettings) new FileManager().loadFile(Variables.UsersFolder, Strings.UserName, Variables.UsersExtension);
        } catch (ClassCastException e) {
            log.info("User file was unable to be loaded, Lets attempt to load it as an older one...");
        }
        if (userSettings == null) {
            HashMap<String, HashMap<String, HashMap<String, String>>> oldUserSettingsFile = null;
            try {
                //noinspection unchecked
                oldUserSettingsFile = (HashMap<String, HashMap<String, HashMap<String, String>>>) new FileManager().loadFile(Variables.UsersFolder, Strings.UserName, Variables.UsersExtension);
            } catch (ClassCastException e) {
                log.info("Old User file was unable to be loaded.");
            }
            if (oldUserSettingsFile != null) {
                int oldVersion = Integer.parseInt(oldUserSettingsFile.get("UserSettings").get("UserVersions").get("0"));
                if (newVersion == oldVersion) {
                    log.info("User settings file versions matched.");
                } else {
                    log.info("User settings file versions didn't match " + newVersion + " - " + oldVersion + ", Updating...");
                    convertUserSettingsFile(oldVersion, newVersion);
                }
            } else {
                log.severe("Was unable to load user file, Forcing shut down.");
                System.exit(0);
            }
        } else {
            int oldVersion = userSettings.getUserSettingsFileVersion();
            if (newVersion == oldVersion) {
                log.info("User settings file versions matched.");
            } else {
                log.info("User settings file versions didn't match " + newVersion + " - " + oldVersion + ", Updating...");
                convertUserSettingsFile(oldVersion, newVersion);
            }
        }
    }

    public void updateShowFile() {
        int newVersion = Variables.ShowFileVersion, oldVersion = programSettingsController.getShowFileVersion();
        if (newVersion == oldVersion) {
            log.info("Show file versions matched.");
        } else {
            log.info("Show file versions didn't match " + newVersion + " - " + oldVersion + ", Updating...");
            convertShowFile(oldVersion, newVersion);
        }
    }

    // This checks if the user has the latest show information, and if they don't, updates it to the latest.
    public void updateMainDirectoryVersion() {
        int mainDirectoryVersion = programSettingsController.getMainDirectoryVersion();
        if (mainDirectoryVersion == userInfoController.getUserDirectoryVersion())
            log.info("User directory version matched.");
        else {
            log.info("User directory version didn't match, Updating...");
            updateUserShows(mainDirectoryVersion);
        }
    }

    // These are ran if the versions didn't match, and updates the file with the latest information. It uses the oldVersion to find where it last updated, and runs through all the cases at and below it to catch it up, than updates the file to the latest version if it ran successfully.
    @SuppressWarnings("SameParameterValue")
    private void convertProgramSettingsFile(int oldVersion, int newVersion) {
        ProgramSettings programSettings = programSettingsController.getSettingsFile();
        boolean updated = false;
        if (oldVersion <= 1008) {
            @SuppressWarnings("unchecked") HashMap<String, ArrayList<String>> oldProgramSettingsFile = (HashMap<String, ArrayList<String>>) new FileManager().loadFile(Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension);
            switch (oldVersion) {
                case -2:
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(0, String.valueOf(Variables.ProgramSettingsFileVersion));
                    oldProgramSettingsFile.put("ProgramVersions", temp);
                    log.info("Program settings file has been updated from version -2.");
                case 1:
                    oldProgramSettingsFile.get("General").add(1, String.valueOf(Variables.dataFolder));
                    log.info("Program has been updated from version 1.");
                case 2:
                    log.info("Program has been updated from version 2.");
                case 1001:
                    oldProgramSettingsFile.get("General").remove(1);
                    log.info("Program has been updated from version 1001.");
                case 1002:
                    log.info("Converting directories...");
                    if (oldProgramSettingsFile.containsKey("Directories")) {
                        ArrayList<String> directories = oldProgramSettingsFile.get("Directories");
                        ArrayList<String> directoriesFixed = new ArrayList<>();
                        directories.forEach(aDirectory -> {
                            int index = directories.indexOf(aDirectory);
                            directoriesFixed.add(index + ">" + aDirectory);
                            log.info("Converted " + aDirectory + " to " + index + '>' + aDirectory);
                        });
                        oldProgramSettingsFile.replace("Directories", directoriesFixed);
                    } else log.info("Program settings file contains no directories, Nothing was converted.");
                    log.info("Finished converting directories");
                    log.info("Program has been updated from version 1002.");
                case 1003:
                    oldProgramSettingsFile.get("ProgramVersions").add(1, "1");
                    log.info("Program has been updated from version 1003.");
                case 1004:
                    oldProgramSettingsFile.get("General").add(1, "false");
                    log.info("Program has been updated from version 1004.");
                case 1005:
                    oldProgramSettingsFile.get("General").add(2, Strings.EmptyString);
                    log.info("Program has been updated from version 1005.");
                case 1006:
                    oldProgramSettingsFile.get("ProgramVersions").add(2, "-2");
                    log.info("Program has been updated from version 1006.");
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
                    log.info("Program has been updated from version 1007.");
                case 1008:
                    programSettings = new ProgramSettings(newVersion,
                            Integer.parseInt(oldProgramSettingsFile.get("ProgramVersions").get(1)),
                            Integer.parseInt(oldProgramSettingsFile.get("ProgramVersions").get(2)),
                            Integer.parseInt(oldProgramSettingsFile.get("General").get(0)),
                            Boolean.parseBoolean(oldProgramSettingsFile.get("General").get(1)),
                            oldProgramSettingsFile.get("General").get(2),
                            Boolean.parseBoolean(oldProgramSettingsFile.get("DefaultUser").get(0)),
                            oldProgramSettingsFile.get("DefaultUser").get(1),
                            oldProgramSettingsFile.get("Directories"),
                            Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(0)),
                            Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(1)),
                            Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(2)),
                            Double.parseDouble(oldProgramSettingsFile.get("GuiNumberSettings").get(3)),
                            Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(0)),
                            Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(1)),
                            Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(2)),
                            Boolean.parseBoolean(oldProgramSettingsFile.get("GuiBooleanSettings").get(3))
                    );
                    programSettingsController.setSettingsFile(programSettings);
                    log.info("Program has been updated from version 1008.");
                    updated = true;

            }
        } /*else if (oldVersion < 1008) {

        }*/
        if (updated) {
            // Update Program Settings File Version
            programSettings.setProgramSettingsFileVersion(newVersion);
            new FileManager().save(programSettings, Strings.EmptyString, Strings.SettingsFileName, Variables.SettingsExtension, true);
            log.info("Program settings file was successfully updated to version " + newVersion + '.');
        } else log.info("Program settings file was not updated. This is an error, please report.");
    }


    @SuppressWarnings("SameParameterValue")
    private void convertUserSettingsFile(int oldVersion, int newVersion) {
        UserSettings userSettings = userInfoController.getUserSettings();
        boolean updated = false;
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
                    log.info("User settings file has been updated from version -2.");
                case 1:
                    HashMap<String, HashMap<String, String>> shows = oldUserSettingsFile.get("ShowSettings");
                    if (shows != null) {
                        shows.keySet().forEach(aShow -> shows.get(aShow).put("isHidden", "false"));
                    }
                    log.info("User settings file has been updated from version 1.");
                case 2:
                    final HashMap<String, HashMap<String, HashMap<String, String>>> finalOldUserSettingsFile = oldUserSettingsFile;
                    showInfoController.getShowsList().forEach(aShow -> {
                        if (!finalOldUserSettingsFile.containsKey("ShowSettings")) {
                            finalOldUserSettingsFile.put("ShowSettings", new HashMap<>());
                        }
                        if (!finalOldUserSettingsFile.get("ShowSettings").keySet().contains(aShow)) {
                            log.info("Adding " + aShow + " to user settings file.");
                            HashMap<String, String> temp2 = new HashMap<>();
                            temp2.put("isActive", "false");
                            temp2.put("isIgnored", "false");
                            temp2.put("isHidden", "false");
                            temp2.put("CurrentSeason", String.valueOf(showInfoController.findLowestSeason(aShow)));
                            Set<Integer> episodes = showInfoController.getEpisodesList(aShow, Integer.parseInt(temp2.get("CurrentSeason")));
                            temp2.put("CurrentEpisode", String.valueOf(showInfoController.findLowestEpisode(episodes)));
                            finalOldUserSettingsFile.get("ShowSettings").put(aShow, temp2);
                        }
                    });
                    log.info("User settings file has been updated from version 2.");
                case 1000:
                    if (!oldUserSettingsFile.containsKey("ShowSettings")) {
                        oldUserSettingsFile.put("ShowSettings", new HashMap<>());
                    }
                    oldUserSettingsFile.get("UserSettings").get("UserVersions").put("1", "0");
                    log.info("User settings file has been updated from version 1000.");
                case 1001:
                    Map<String, UserShowSettings> showsConverted = new HashMap<>();
                    oldUserSettingsFile.get("ShowSettings").forEach((showName, showSettings) -> showsConverted.put(showName, new UserShowSettings(showName, Boolean.parseBoolean(showSettings.get("isActive")), Boolean.parseBoolean(showSettings.get("isIgnored")), Boolean.parseBoolean(showSettings.get("isHidden")), Integer.parseInt(showSettings.get("CurrentSeason")), Integer.parseInt(showSettings.get("CurrentEpisode")))));
                    userSettings = new UserSettings(Strings.UserName, showsConverted);
                    log.info("User settings file has been updated from version 1001.");
                    updated = true;
            }
        } /*else if (oldVersion > 1001) {
        }*/


        if (updated) {
            // Update User Settings File Version
            userSettings.setUserSettingsFileVersion(newVersion);
            new FileManager().save(userSettings, Variables.UsersFolder, Strings.UserName, Variables.UsersExtension, true);
            log.info("User settings file was successfully updated to version " + newVersion + '.');
        } else log.info("User settings file was not updated. This is an error, please report.");
    }

    private void convertShowFile(int oldVersion, int newVersion) {
        boolean updated = false;
        switch (oldVersion) {
            case -2:
                ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = new ArrayList<>();
                programSettingsController.getDirectoriesNames().forEach(aString -> {
                    //noinspection unchecked
                    showsFileArray.add((HashMap<String, HashMap<Integer, HashMap<String, String>>>) new FileManager().loadFile(Variables.DirectoriesFolder, aString, Strings.EmptyString));
                });
                showsFileArray.forEach(aHashMap -> {
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
                    showInfoController.saveShowsMapFile(showsMap, showsFileArray.indexOf(aHashMap), false);
                });
                log.info("Show file has been updated from version -2.");
                updated = true;
        }

        if (updated) {
            // Update Program Settings File Version
            programSettingsController.setShowFileVersion(newVersion);
            programSettingsController.saveSettingsFile();
            log.info("Show file was successfully updated to version " + newVersion + '.');
        } else log.info("Show file was not updated. This is an error, please report.");
    }

    // This finds what shows have been added / remove if another user has ran the program (and found updated information) since you last ran your profile.
    private void updateUserShows(int newVersion) {
        ArrayList<String> shows = showInfoController.getShowsList();
        ArrayList<String> userShows = userInfoController.getAllNonIgnoredShows();
        ArrayList<String> ignoredShows = userInfoController.getIgnoredShows();
        final boolean[] changed = {false};
        shows.forEach(aShow -> {
            if (!userShows.contains(aShow) && !ignoredShows.contains(aShow)) {
                log.info(aShow + " was found during user shows update and added.");
                ChangeReporter.addChange(aShow + " was added.");
                userInfoController.addNewShow(aShow);
                changed[0] = true;
            } else if (ignoredShows.contains(aShow)) {
                log.info(aShow + " was found during user shows update and un-ignored.");
                ChangeReporter.addChange(aShow + " was added.");
                userInfoController.setIgnoredStatus(aShow, false);
                changed[0] = true;
            }
        });
        userShows.forEach(aShow -> {
            if (!shows.contains(aShow)) {
                log.info(aShow + " wasn't found during user shows update.");
                ChangeReporter.addChange(aShow + " has been removed.");
                userInfoController.setIgnoredStatus(aShow, true);
                changed[0] = true;
            }
        });
        if (!changed[0]) {
            log.info("No changes found.");
        }
        userInfoController.setUserDirectoryVersion(newVersion);
    }
}