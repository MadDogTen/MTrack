package com.maddogten.mtrack.lang;

import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

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
        Strings.TextMissing.setValue("Proper text is missing, Please report.");

        // Single Words
        Strings.Submit.setValue("Submit");
        Strings.Clear.setValue("Clear");
        Strings.Refresh.setValue("Refresh");
        Strings.Close.setValue("Close");
        Strings.Yes.setValue("Yes");
        Strings.No.setValue("No");
        Strings.Beginning.setValue("Beginning");
        Strings.End.setValue("End");
        Strings.QuestionMark.setValue("?");
        Strings.Shows.setValue("Shows");
        Strings.Left.setValue("Left");
        Strings.Settings.setValue("Settings");
        Strings.Options.setValue("Options");
        Strings.Main.setValue("Main");
        Strings.Users.setValue("Users");
        Strings.Other.setValue("Other");
        Strings.Developers.setValue("Developer");
        Strings.Reset.setValue("Reset");
        Strings.About.setValue("About");
        Strings.Season.setValue("Season");
        Strings.Episode.setValue("Episode");
        Strings.Set.setValue("Set");
        Strings.Warning.setValue("Warning- ");
        Strings.Show.setValue("Show");
        Strings.UI.setValue("UI");
        Strings.Cut.setValue("Cut");
        Strings.Copy.setValue("Copy");
        Strings.Paste.setValue("Paste");
        Strings.All.setValue("All");
        Strings.Program.setValue("Program");
        Strings.Directories.setValue("Directories");

        // Button Text
        Strings.SetSeasonEpisode.setValue("Set Season + Episode");
        Strings.PlaySeasonEpisode.setValue("Play Season + Episode");
        Strings.HideShow.setValue("Hide Show");
        Strings.ResetTo.setValue("Reset to...");
        Strings.OpenFileLocation.setValue("Open File Location");
        Strings.GetRemaining.setValue("Get Remaining");
        Strings.PlayPreviousEpisode.setValue("Play Previous Episode");
        Strings.SetInactive.setValue("Set Inactive");
        Strings.SetActive.setValue("Set Active");
        Strings.SwitchBetweenActiveInactiveList.setValue("Switch between Active/Inactive List");
        Strings.AddUser.setValue("Add User");
        Strings.DeleteUser.setValue("Delete User");
        Strings.ChangeUpdateTime.setValue("Change Update Time");
        Strings.ForceRecheckShows.setValue("Force Recheck Shows");
        Strings.AddDirectory.setValue("Add Directory");
        Strings.RemoveDirectory.setValue("Remove Directory");
        Strings.OpenSettingsFolder.setValue("Open Settings Folder");
        Strings.PrintAllShowInfo.setValue("Print All Show Info");
        Strings.PrintAllDirectories.setValue("Print All Directories");
        Strings.PrintEmptyShows.setValue("Print Empty Shows");
        Strings.PrintIgnoredShows.setValue("Print Ignored Shows");
        Strings.PrintHiddenShows.setValue("Print Hidden Shows");
        Strings.UnHideAll.setValue("UnHide All");
        Strings.SetAllActive.setValue("Set All Active");
        Strings.SetAllInactive.setValue("Set All Inactive");
        Strings.PrintPsfvAndUsfv.setValue("Print PSFV & USFV");
        Strings.PrintAllUserInfo.setValue("Print All User Info");
        Strings.DirectoryVersionPlus1.setValue("Directory Version +1");
        Strings.ClearFile.setValue("Clear File");
        Strings.ResetProgram.setValue("Reset Program");
        Strings.ToggleDevMode.setValue("Toggle Dev");
        Strings.InAppData.setValue("In Appdata");
        Strings.WithTheJar.setValue("With the Jar");
        Strings.UpdateTime.setValue("Searching Wait Time (Seconds):");
        Strings.DirectoryTimeout.setValue("Directory Timeout (Seconds):");
        Strings.UnHideShow.setValue("UnHide Show");
        Strings.OpenAll.setValue("Open All");
        Strings.OpenSelected.setValue("Open Selected");
        Strings.NonForceRecheckShows.setValue("Non-Force Recheck Shows");
        Strings.ToggleIsChanges.setValue("Toggle isChanges");

        // CheckBox Text
        Strings.InactiveShows.setValue("Inactive Shows");
        Strings.OlderSeasons.setValue("Older Seasons");
        Strings.AllowFullWindowMovementUse.setValue("Allow Full Window Movement/Use");
        Strings.DisableAutomaticShowSearching.setValue("Disable Automatic Show Searching");
        Strings.ShowUsername.setValue("Show Username");

        // Tooltip Text
        Strings.ShowHiddenShowsWith0EpisodeLeft.setValue("Show/Hide shows with 0 episode left.");
        Strings.CurrentlyRechecking.setValue("Currently Searching Shows...");
        Strings.DeleteUsersNoteCantDeleteCurrentUser.setValue("Note: Can't delete current user!");
        Strings.WarningUnrecoverable.setValue("Warning, Unrecoverable!");
        Strings.InternalVersion.setValue("Internal Version: " + Variables.InternalVersion);
        Strings.MakeUserDefault.setValue("Make User Default");
        Strings.MakeLanguageDefault.setValue("Make Language Default");

        // Other Text
        Strings.AddNewUsername.setValue("Add New Username");
        Strings.PleaseEnterUsername.setValue("Please enter your username: ");
        Strings.UseDefaultUsername.setValue("Use default username?");
        Strings.DefaultUserNotSet.setValue("Default user not set.");
        Strings.PleaseChooseAFolder.setValue("Please choose a folder.");
        Strings.PickTheEpisode.setValue("Pick the Episode");
        Strings.YouHaveToPickASeason.setValue("You have to pick a season!");
        Strings.YouHaveToPickAEpisode.setValue("You have to pick a episode!");
        Strings.NextEpisode.setValue("Next Episode");
        Strings.UsernameIsntValid.setValue("Username isn't valid.");
        Strings.UsernameAlreadyTaken.setValue("Username already taken.");
        Strings.UsernameIsTooLong.setValue("Username is too long.");
        Strings.DirectoryIsAlreadyAdded.setValue("Directory is already added.");
        Strings.MustBeANumberBetween.setValue("Must be a number between ");
        Strings.ChooseYourUsername.setValue("Choose your Username:");
        Strings.DirectoryWasADuplicate.setValue("Directory was a duplicate!");
        Strings.AddAnotherDirectory.setValue("Add another directory?");
        Strings.WhatShould.setValue("What should ");
        Strings.BeResetTo.setValue(" be reset to?");
        Strings.ShowIsResetToThe.setValue("Show is reset to the");
        Strings.DoYouWantToOpenAllAssociatedFolders.setValue("Do you want to open ALL associated folders?");
        Strings.PickTheFolderYouWantToOpen.setValue("Pick the Folder you want to open");
        Strings.NoDirectlyPrecedingEpisodesFound.setValue("No directly preceding episodes found.");
        Strings.HaveYouWatchedTheShow.setValue("Have you watched the show?");
        Strings.AreYouSure.setValue("Are you sure?");
        Strings.PleaseChooseADefaultUser.setValue("Please choose a default user:");
        Strings.ThereAreNoOtherUsersToDelete.setValue("There are no other users to delete.");
        Strings.UserToDelete.setValue("User to delete:");
        Strings.AreYouSureToWantToDelete.setValue("Are you sure to want to delete ");
        Strings.EnterHowFastYouWantItToScanTheShowsFolders.setValue("Enter how fast you want it to scan the show(s) folder(s)");
        Strings.LeaveItAsIs.setValue("Leave it as is?");
        Strings.PleaseEnterShowsDirectory.setValue("Please enter shows directory");
        Strings.YouNeedToEnterADirectory.setValue("You need to enter a directory.");
        Strings.DirectoryIsInvalid.setValue("Directory is invalid.");
        Strings.ThereAreNoDirectoriesToDelete.setValue("There are no directories to delete.");
        Strings.ThereAreNoDirectoriesToClear.setValue("There are no directories to clear.");
        Strings.DirectoryToClear.setValue("Directory to Clear:");
        Strings.AreYouSureToWantToClear.setValue("Are you sure to want to clear ");
        Strings.AreYouSureThisWillDeleteEverything.setValue("Are you sure? This will delete EVERYTHING!");
        Strings.OpenChangesWindow.setValue("Open Changes Window");
        Strings.Dev1.setValue("Dev 1");
        Strings.Dev2.setValue("Dev 2");
        Strings.SetDefaultUser.setValue("Set Default User");
        Strings.DirectoryToDelete.setValue("Directory to delete:");
        Strings.PleaseChooseYourLanguage.setValue("Please choose your language: ");
        Strings.ChangeLanguage.setValue("Change Language");
        Strings.RestartTheProgramForTheNewLanguageToTakeEffect.setValue("Restart the program for the new language to take effect.");
        Strings.WhereWouldYouLikeTheProgramFilesToBeStored.setValue("Where would you like the program files to be stored?");
        Strings.HoverOverAButtonForThePath.setValue("(Hover over a button for the path)");
        Strings.DashSeason.setValue(" - Season ");
        Strings.DashEpisode.setValue(" - Episode ");
        Strings.PickTheSeasonAndEpisode.setValue("Pick the Season & Episode");
        Strings.YouHaveReachedTheEnd.setValue("You have reached the end!");
        Strings.PingingDirectories.setValue("Pinging Directories...");
        Strings.PathToDirectory.setValue("PathToDirectory");
        Strings.PrintCurrentSeasonEpisode.setValue("Print Current Season & Episode");
        Strings.WasFoundToBeInactive.setValue(" was found to be inactive.");
        Strings.PleaseCorrectTheIssueThenForceRefresh.setValue("Please correct the issue then force refresh.");
        Strings.NotifyChangesFor.setValue("Notify Changes For:");
        Strings.OnlyChecksEveryRuns.setValue("Only checks every 5 runs");
        Strings.JavaVersionFull.setValue("Coded In " + Strings.CodedIn.getValue() + "\nVersion: " + Strings.JavaVersion.getValue());
        Strings.CodedUsingFull.setValue("Coded With: " + Strings.CodedUsing.getValue());
        Strings.ThereAreNoHiddenShows.setValue("There are no hidden shows.");
        Strings.PickShowToUnHide.setValue("Pick show to UnHide...");
        Strings.DirectoryToSaveExportIn.setValue("Directory to save export in: ");
        Strings.PrintShowInformation.setValue("Print Show Information");
        Strings.ChooseWhatToExport.setValue("Choose what to export:");
        Strings.EnterLocationToSaveExport.setValue("Enter Location to save export:");
        Strings.FileAlreadyExistsOverwriteIt.setValue("File already exists, Overwrite it?");
        Strings.FilenameMustEndIn.setValue("Filename must end in: ");
        Strings.YouMustSelectACheckbox.setValue("You must select a checkbox.");
        Strings.DoYouWantToImportFiles.setValue("Do you want to import files?");
        Strings.DoYouWantToRestartTheProgramForTheImportToTakeFullEffectWarningSettingsChangedOutsideOfTheImportWontBeSaved.setValue("Do you want to restart the program for the import to take full effect? (Warning: Settings changed outside of import won't be saved)");
        Strings.MTrackHasNowImportedTheFiles.setValue("MTrack has now imported the files.");
        Strings.AutomaticallyOverwriteFilesIfNoYouWillBeAskedForEachExistingFile.setValue("Automatically overwrite files? (If no, You will be asked for each existing file)");
        Strings.AlreadyExistsOverwriteIt.setValue("already exists, Overwrite it?");
        Strings.EnterFileLocation.setValue("Enter File Location:");
        Strings.ExportSettings.setValue("Export Settings");
        Strings.ImportSettings.setValue("Import Settings");
        Strings.FileDoesNotExists.setValue("File doesn't exists.");
        Strings.CurrentlyPlaying.setValue("Currently Playing:");
        Strings.WasUnableToPlayTheEpisode.setValue("Was unable to play the episode.");
    }
}
