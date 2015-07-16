package program.lang;

import java.util.logging.Logger;

public class en_US extends Language {
    private final Logger log = Logger.getLogger(en_US.class.getName());

    public en_US() {
        log.info("Loading en_US...");

        // All Other Text
        allStrings.put("LanguageName", "English US");
        allStrings.put("DefaultUsername", "Default User");
        allStrings.put("TextMissing", "Error, Please report.");

        // Single Words
        allStrings.put("Submit", "Submit");
        allStrings.put("Clear", "Clear");
        allStrings.put("Refresh", "Refresh");
        allStrings.put("Close", "Close");
        allStrings.put("Yes", "Yes");
        allStrings.put("No", "No");
        allStrings.put("Beginning", "Beginning");
        allStrings.put("End", "End");
        allStrings.put("QuestionMark", "?");
        allStrings.put("Shows", "Shows");
        allStrings.put("Left", "Left");
        allStrings.put("Settings", "Settings");
        allStrings.put("Options", "Options");
        allStrings.put("Main", "Main");
        allStrings.put("Users", "Users");
        allStrings.put("Other", "Other");
        allStrings.put("Developers", "Developer");
        allStrings.put("Reset", "Reset");
        allStrings.put("About", "About");
        allStrings.put("Season", "Season");
        allStrings.put("Episode", "Episode");

        // Button Text
        allStrings.put("SetSeasonEpisode", "Set Season + Episode");
        allStrings.put("PlaySeasonEpisode", "Play Season + Episode");
        allStrings.put("HideShow", "Hide Show");
        allStrings.put("ResetTo", "Reset to...");
        allStrings.put("OpenFileLocation", "Open File Location");
        allStrings.put("GetRemaining", "Get Remaining");
        allStrings.put("PlayPreviousEpisode", "Play Previous Episode");
        allStrings.put("SetInactive", "Set Inactive");
        allStrings.put("SetActive", "Set Active");
        allStrings.put("SwitchBetweenActiveInactiveList", "Switch between Active/Inactive List");
        allStrings.put("AddUser", "Add User");
        allStrings.put("DeleteUser", "Delete User");
        allStrings.put("ChangeUpdateTime", "Change Update Time");
        allStrings.put("ForceRecheckShows", "Force Recheck Shows");
        allStrings.put("AddDirectory", "Add Directory");
        allStrings.put("RemoveDirectory", "Remove Directory");
        allStrings.put("OpenSettingsFolder", "Open Settings Folder");
        allStrings.put("PrintAllShowInfo", "Print All Show Info");
        allStrings.put("PrintAllDirectories", "Print All Directories");
        allStrings.put("PrintEmptyShows", "Print Empty Shows");
        allStrings.put("PrintIgnoredShows", "Print Ignored Shows");
        allStrings.put("PrintHiddenShows", "Print Hidden Shows");
        allStrings.put("UnHideAll", "UnHide All");
        allStrings.put("SetAllActive", "Set All Active");
        allStrings.put("SetAllInactive", "Set All Inactive");
        allStrings.put("PrintPSFV", "Print PSFV");
        allStrings.put("PrintUSFV", "Print USFV");
        allStrings.put("PrintAllUserInfo", "Print All User Info");
        allStrings.put("DirectoryVersionPlus1", "Directory Version +1");
        allStrings.put("ClearFile", "Clear File");
        allStrings.put("ResetProgram", "Reset Program");

        // Tooltip Text
        allStrings.put("ShowHideShowsWith0EpisodeLeft", "Show/Hide shows with 0 episode left.");
        allStrings.put("CurrentlyRechecking", "Currently Rechecking...");
        allStrings.put("DeleteUsersNoteCantDeleteCurrentUser", "Delete Users. Note: Can't delete current user!");
        allStrings.put("WarningUnrecoverable", "Warning, Unrecoverable!");

        // Other Text
        allStrings.put("AddNewUsername", "Add New Username");
        allStrings.put("PleaseEnterUsername", "Please enter your username: ");
        allStrings.put("UseDefaultUsername", "Use default username?");
        allStrings.put("DefaultUserNotSet", "Default user not set.");
        allStrings.put("PleaseChooseAFolder", "Please choose a folder.");
        allStrings.put("PickTheEpisode", "Pick the Episode");
        allStrings.put("YouHaveToPickASeason", "You have to pick a season!");
        allStrings.put("YouHaveToPickAEpisode", "You have to pick a episode!");
        allStrings.put("NextEpisode", "Next Episode");
        allStrings.put("UsernameIsntValid", "Username isn't valid.");
        allStrings.put("UsernameAlreadyTaken", "Username already taken.");
        allStrings.put("UsernameIsTooLong", "Username is too long.");
        allStrings.put("DirectoryIsAlreadyAdded", "Directory is already added.");
        allStrings.put("MustBeANumberGreaterThanOrEqualTo10", "Must be a number greater than or equal to 10");
        allStrings.put("ChooseYourUsername", "Choose your Username:");
        allStrings.put("DirectoryWasADuplicate", "Directory was a duplicate!");
        allStrings.put("AddAnotherDirectory", "Add another directory?");
        allStrings.put("WhatShould", "What should ");
        allStrings.put("BeResetTo", " be reset to?");
        allStrings.put("ShowIsResetToThe", "Show is reset to the");
        allStrings.put("DoYouWantToOpenAllAssociatedFolders", "Do you want to open ALL associated folders?");
        allStrings.put("PickTheFolderYouWantToOpen", "Pick the Folder you want to open");
        allStrings.put("NoDirectlyPrecedingEpisodesFound", "No directly preceding episodes found.");
        allStrings.put("HaveYouWatchedTheShow", "Have you watched the show?");
        allStrings.put("AreYouSure", "Are you sure?");
        allStrings.put("PleaseChooseADefaultUser", "Please choose a default user:");
        allStrings.put("ThereAreNoOtherUsersToDelete", "There are no other users to delete.");
        allStrings.put("UserToDelete", "User to delete:");
        allStrings.put("AreYouSureToWantToDelete", "Are you sure to want to delete ");
        allStrings.put("EnterHowFastYouWantItToScanTheShowsFolders", "Enter how fast you want it to scan the show(s) folder(s)");
        allStrings.put("LeaveItAsIs", "Leave it as is?");
        allStrings.put("PleaseEnterShowsDirectory", "Please enter shows directory");
        allStrings.put("YouNeedToEnterADirectory", "You need to enter a directory.");
        allStrings.put("DirectoryIsInvalid", "Directory is invalid.");
        allStrings.put("ThereAreNoDirectoriesToDelete", "There are no directories to delete.");
        allStrings.put("ThereAreNoDirectoriesToClear", "There are no directories to clear.");
        allStrings.put("DirectoryToClear", "Directory to Clear:");
        allStrings.put("AreYouSureToWantToClear", "Are you sure to want to clear ");
        allStrings.put("AreYouSureThisWillDeleteEverything", "Are you sure? This will delete EVERYTHING!");
        allStrings.put("OpenChangesWindow", "Open Changes Window");
        allStrings.put("Dev1", "Dev 1");
        allStrings.put("Dev2", "Dev 2");
        allStrings.put("SetDefaultUser", "Set Default User");
        allStrings.put("DirectoryToDelete", "Directory to delete:");
        allStrings.put("PleaseChooseYourLanguage", "Please choose your language: ");
        allStrings.put("ChangeLanguage", "Change Language");
        allStrings.put("RestartTheProgramForTheNewLanguageToTakeEffect", "Restart the program for the new language to take effect.");
    }
}