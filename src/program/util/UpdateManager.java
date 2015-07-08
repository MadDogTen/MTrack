package program.util;

import program.information.ChangeReporter;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;


// Ch
public class UpdateManager {
    private final Logger log = Logger.getLogger(UpdateManager.class.getName());

    // These both compare the version defined in Variables (Latest Version) with the version the files are currently at (Old Version), and if they don't match, converts the files to the latest version.
    public void updateProgramSettingsFile() {
        int newVersion = Variables.ProgramSettingsFileVersion, currentVersion = ProgramSettingsController.getProgramSettingsVersion();
        if (Variables.ProgramSettingsFileVersion == currentVersion) {
            log.info("Program settings file versions matched.");
        } else {
            log.info("Program settings file versions didn't match " + newVersion + " - " + currentVersion + ", Updating...");
            convertProgramSettingsFile(currentVersion, newVersion);
        }
    }

    public void updateUserSettingsFile() {
        int newVersion = Variables.UserSettingsFileVersion, oldVersion = UserInfoController.getUserSettingsVersion();
        if (newVersion == oldVersion) {
            log.info("User settings file versions matched.");
        } else {
            log.info("User settings file versions didn't match " + newVersion + " - " + oldVersion + ", Updating...");
            convertUserSettingsFile(oldVersion, newVersion);
        }
    }

    // This checks if the user has the latest show information, and if they don't, updates it to the latest.
    public void updateMainDirectoryVersion() {
        int mainDirectoryVersion = ProgramSettingsController.getMainDirectoryVersion();
        if (mainDirectoryVersion == UserInfoController.getUserDirectoryVersion())
            log.info("User directory version matched.");
        else {
            log.info("User directory version didn't match, Updating...");
            updateUserShows(mainDirectoryVersion);
        }
    }

    // These are ran if the versions didn't match, and updates the file with the latest information. It uses the oldVersion to find where it last updated, and runs through all the cases at and below it to catch it up, than updates the file to the latest version if it ran successfully.
    @SuppressWarnings("SameParameterValue")
    private void convertProgramSettingsFile(int oldVersion, int newVersion) {
        HashMap<String, ArrayList<String>> programSettingsFile = ProgramSettingsController.getSettingsFile();
        boolean updated = false;
        switch (oldVersion) {
            case -2:
                ArrayList<String> temp = new ArrayList<>();
                temp.add(0, String.valueOf(Variables.ProgramSettingsFileVersion));
                programSettingsFile.put("ProgramVersions", temp);
                log.info("Program settings file has been updated from version -2.");
            case 1:
                programSettingsFile.get("General").add(1, Variables.dataFolder);
                log.info("Program has been updated from version 1.");
            case 2:
                log.info("Program has been updated from version 2.");
            case 1001:
                programSettingsFile.get("General").remove(1);
                log.info("Program has been updated from version 1001.");
            case 1002:
                log.info("Converting directories...");
                if (programSettingsFile.containsKey("Directories")) {
                    ArrayList<String> directories = programSettingsFile.get("Directories");
                    ArrayList<String> directoriesFixed = new ArrayList<>();
                    directories.forEach(aDirectory -> {
                        int index = directories.indexOf(aDirectory);
                        directoriesFixed.add(index + ">" + aDirectory);
                        log.info("Converted " + aDirectory + " to " + index + '>' + aDirectory);
                    });
                    programSettingsFile.replace("Directories", directoriesFixed);
                } else log.info("Program settings file contains no directories, Nothing was converted.");
                log.info("Finished converting directories");
                log.info("Program has been updated from version 1002.");
            case 1003:
                programSettingsFile.get("ProgramVersions").add(1, "1");
                log.info("Program has been updated from version 1003.");
            case 1004:
                programSettingsFile.get("General").add(1, "false");
            case 1005:
                programSettingsFile.get("General").add(2, Strings.EmptyString);
                updated = true;
        }

        if (updated) {
            // Update Program Settings File Version
            programSettingsFile.get("ProgramVersions").set(0, String.valueOf(newVersion));

            ProgramSettingsController.setSettingsFile(programSettingsFile);
            ProgramSettingsController.saveSettingsFile();
            log.info("Program settings file was successfully updated to version " + newVersion + '.');
        } else log.info("Program settings file was not updated. This is an error, please report.");
    }

    @SuppressWarnings("SameParameterValue")
    private void convertUserSettingsFile(int oldVersion, int newVersion) {
        HashMap<String, HashMap<String, HashMap<String, String>>> userSettingsFile = UserInfoController.getUserSettingsFile();
        boolean updated = false;
        switch (oldVersion) {
            case -2:
                HashMap<String, HashMap<String, String>> tempPut;
                HashMap<String, String> temp;
                tempPut = new HashMap<>();
                temp = new HashMap<>();
                temp.put("0", String.valueOf(Variables.UserSettingsFileVersion));
                tempPut.put("UserVersions", temp);
                userSettingsFile.put("UserSettings", tempPut);
                log.info("User settings file has been updated from version -2.");
            case 1:
                HashMap<String, HashMap<String, String>> shows = userSettingsFile.get("ShowSettings");
                if (shows != null) {
                    shows.keySet().forEach(aShow -> shows.get(aShow).put("isHidden", "false"));
                }
                log.info("User settings file has been updated from version 1.");
            case 2:
                ShowInfoController.getShowsList().forEach(aShow -> {
                    if (!userSettingsFile.containsKey("ShowSettings")) {
                        userSettingsFile.put("ShowSettings", new HashMap<>());
                    }
                    if (!userSettingsFile.get("ShowSettings").keySet().contains(aShow)) {
                        log.info("Adding " + aShow + " to user settings file.");
                        HashMap<String, String> temp2 = new HashMap<>();
                        temp2.put("isActive", "false");
                        temp2.put("isIgnored", "false");
                        temp2.put("isHidden", "false");
                        temp2.put("CurrentSeason", String.valueOf(ShowInfoController.findLowestSeason(aShow)));
                        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, Integer.parseInt(temp2.get("CurrentSeason")));
                        temp2.put("CurrentEpisode", String.valueOf(ShowInfoController.findLowestEpisode(episodes)));
                        userSettingsFile.get("ShowSettings").put(aShow, temp2);
                    }
                });
                log.info("User settings file has been updated from version 2.");
            case 1000:
                if (!userSettingsFile.containsKey("ShowSettings")) {
                    userSettingsFile.put("ShowSettings", new HashMap<>());
                }
                userSettingsFile.get("UserSettings").get("UserVersions").put("1", "0");
                log.info("User settings file has been updated from version 1000.");
                updated = true;
        }

        if (updated) {
            // Update User Settings File Version
            userSettingsFile.get("UserSettings").get("UserVersions").replace("0", String.valueOf(newVersion));

            UserInfoController.setUserSettingsFile(userSettingsFile);
            UserInfoController.saveUserSettingsFile();
            log.info("User settings file was successfully updated to version " + newVersion + '.');
        } else log.info("User settings file was not updated. This is an error, please report.");
    }

    // This finds what shows have been added / remove if another user has ran the program (and found updated information) since you last ran your profile.
    private void updateUserShows(int newVersion) {
        ArrayList<String> shows = ShowInfoController.getShowsList();
        ArrayList<String> userShows = UserInfoController.getAllNonIgnoredShows();
        ArrayList<String> ignoredShows = UserInfoController.getIgnoredShows();
        final boolean[] changed = {false};
        shows.forEach(aShow -> {
            if (!userShows.contains(aShow) && !ignoredShows.contains(aShow)) {
                log.info(aShow + " was found during user shows update and added.");
                ChangeReporter.addChange(aShow + " was added.");
                UserInfoController.addNewShow(aShow);
                changed[0] = true;
            } else if (ignoredShows.contains(aShow)) {
                log.info(aShow + " was found during user shows update and un-ignored.");
                ChangeReporter.addChange(aShow + " was added.");
                UserInfoController.setIgnoredStatus(aShow, false);
                changed[0] = true;
            }
        });
        userShows.forEach(aShow -> {
            if (!shows.contains(aShow)) {
                log.info(aShow + " wasn't found during user shows update.");
                ChangeReporter.addChange(aShow + " has been removed.");
                UserInfoController.setIgnoredStatus(aShow, true);
                changed[0] = true;
            }
        });
        if (!changed[0]) {
            log.info("No changes found.");
        }
        UserInfoController.setUserDirectoryVersion(newVersion);
    }
}