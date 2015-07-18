package program.lang;

import program.util.Strings;

import java.util.logging.Logger;

public class en_US extends Language {
    private final Logger log = Logger.getLogger(en_US.class.getName());

    public en_US() {
        LanguageName = "English US";
    }

    @Override
    public void setAllStrings() {
        log.info("Setting language to " + LanguageName + "...");

        // All Other Text
        Strings.DefaultUsername = "Default User";
        Strings.TextMissing = "Error, Please report.";

        // Single Words
        Strings.Submit = "Submit";
        Strings.Clear = "Clear";
        Strings.Refresh = "Refresh";
        Strings.Close = "Close";
        Strings.Yes = "Yes";
        Strings.No = "No";
        Strings.Beginning = "Beginning";
        Strings.End = "End";
        Strings.QuestionMark = "?";
        Strings.Shows = "Shows";
        Strings.Left = "Left";
        Strings.Settings = "Settings";
        Strings.Options = "Options";
        Strings.Main = "Main";
        Strings.Users = "Users";
        Strings.Other = "Other";
        Strings.Developers = "Developer";
        Strings.Reset = "Reset";
        Strings.About = "About";
        Strings.Season = "Season";
        Strings.Episode = "Episode";

        // Button Text
        Strings.SetSeasonEpisode = "Set Season + Episode";
        Strings.PlaySeasonEpisode = "Play Season + Episode";
        Strings.HideShow = "Hide Show";
        Strings.ResetTo = "Reset to...";
        Strings.OpenFileLocation = "Open File Location";
        Strings.GetRemaining = "Get Remaining";
        Strings.PlayPreviousEpisode = "Play Previous Episode";
        Strings.SetInactive = "Set Inactive";
        Strings.SetActive = "Set Active";
        Strings.SwitchBetweenActiveInactiveList = "Switch between Active/Inactive List";
        Strings.AddUser = "Add User";
        Strings.DeleteUser = "Delete User";
        Strings.ChangeUpdateTime = "Change Update Time";
        Strings.ForceRecheckShows = "Force Recheck Shows";
        Strings.AddDirectory = "Add Directory";
        Strings.RemoveDirectory = "Remove Directory";
        Strings.OpenSettingsFolder = "Open Settings Folder";
        Strings.PrintAllShowInfo = "Print All Show Info";
        Strings.PrintAllDirectories = "Print All Directories";
        Strings.PrintEmptyShows = "Print Empty Shows";
        Strings.PrintIgnoredShows = "Print Ignored Shows";
        Strings.PrintHiddenShows = "Print Hidden Shows";
        Strings.UnHideAll = "UnHide All";
        Strings.SetAllActive = "Set All Active";
        Strings.SetAllInactive = "Set All Inactive";
        Strings.PrintPSFV = "Print PSFV";
        Strings.PrintUSFV = "Print USFV";
        Strings.PrintAllUserInfo = "Print All User Info";
        Strings.DirectoryVersionPlus1 = "Directory Version +1";
        Strings.ClearFile = "Clear File";
        Strings.ResetProgram = "Reset Program";
        Strings.ToggleDevMode = "Toggle Dev";
        Strings.InAppData = "In Appdata";
        Strings.WithTheJar = "With the Jar";

        // Tooltip Text
        Strings.ShowHideShowsWith0EpisodeLeft = "Show/Hide shows with 0 episode left.";
        Strings.CurrentlyRechecking = "Currently Rechecking...";
        Strings.DeleteUsersNoteCantDeleteCurrentUser = "Delete Users. Note: Can't delete current user!";
        Strings.WarningUnrecoverable = "Warning, Unrecoverable!";

        // Other Text
        Strings.AddNewUsername = "Add New Username";
        Strings.PleaseEnterUsername = "Please enter your username: ";
        Strings.UseDefaultUsername = "Use default username?";
        Strings.DefaultUserNotSet = "Default user not set.";
        Strings.PleaseChooseAFolder = "Please choose a folder.";
        Strings.PickTheEpisode = "Pick the Episode";
        Strings.YouHaveToPickASeason = "You have to pick a season!";
        Strings.YouHaveToPickAEpisode = "You have to pick a episode!";
        Strings.NextEpisode = "Next Episode";
        Strings.UsernameIsntValid = "Username isn't valid.";
        Strings.UsernameAlreadyTaken = "Username already taken.";
        Strings.UsernameIsTooLong = "Username is too long.";
        Strings.DirectoryIsAlreadyAdded = "Directory is already added.";
        Strings.MustBeANumberGreaterThanOrEqualTo10 = "Cannot be set to less than 10 seconds.";
        Strings.ChooseYourUsername = "Choose your Username:";
        Strings.DirectoryWasADuplicate = "Directory was a duplicate!";
        Strings.AddAnotherDirectory = "Add another directory?";
        Strings.WhatShould = "What should ";
        Strings.BeResetTo = " be reset to?";
        Strings.ShowIsResetToThe = "Show is reset to the";
        Strings.DoYouWantToOpenAllAssociatedFolders = "Do you want to open ALL associated folders?";
        Strings.PickTheFolderYouWantToOpen = "Pick the Folder you want to open";
        Strings.NoDirectlyPrecedingEpisodesFound = "No directly preceding episodes found.";
        Strings.HaveYouWatchedTheShow = "Have you watched the show?";
        Strings.AreYouSure = "Are you sure?";
        Strings.PleaseChooseADefaultUser = "Please choose a default user:";
        Strings.ThereAreNoOtherUsersToDelete = "There are no other users to delete.";
        Strings.UserToDelete = "User to delete:";
        Strings.AreYouSureToWantToDelete = "Are you sure to want to delete ";
        Strings.EnterHowFastYouWantItToScanTheShowsFolders = "Enter how fast you want it to scan the show(s) folder(s)";
        Strings.LeaveItAsIs = "Leave it as is?";
        Strings.PleaseEnterShowsDirectory = "Please enter shows directory";
        Strings.YouNeedToEnterADirectory = "You need to enter a directory.";
        Strings.DirectoryIsInvalid = "Directory is invalid.";
        Strings.ThereAreNoDirectoriesToDelete = "There are no directories to delete.";
        Strings.ThereAreNoDirectoriesToClear = "There are no directories to clear.";
        Strings.DirectoryToClear = "Directory to Clear:";
        Strings.AreYouSureToWantToClear = "Are you sure to want to clear ";
        Strings.AreYouSureThisWillDeleteEverything = "Are you sure? This will delete EVERYTHING!";
        Strings.OpenChangesWindow = "Open Changes Window";
        Strings.Dev1 = "Dev 1";
        Strings.Dev2 = "Dev 2";
        Strings.SetDefaultUser = "Set Default User";
        Strings.DirectoryToDelete = "Directory to delete:";
        Strings.PleaseChooseYourLanguage = "Please choose your language: ";
        Strings.ChangeLanguage = "Change Language";
        Strings.RestartTheProgramForTheNewLanguageToTakeEffect = "Restart the program for the new language to take effect.";
        Strings.WhereWouldYouLikeTheProgramFilesToBeStored = "Where would you like the program files to be stored?";
        Strings.HoverOverAButtonForThePath = "(Hover over a button for the path)";
    }
}