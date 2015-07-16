package program.lang;

import program.util.Strings;

import java.util.HashMap;
import java.util.logging.Logger;

public abstract class Language {
    final HashMap<String, String> allStrings = new HashMap<>();
    private final Logger log = Logger.getLogger(Language.class.getName());

    public void setAllStrings() {
        log.info("Setting language to " + allStrings.get("LanguageName") + "...");

        // All Other Text
        Strings.DefaultUsername = ("DefaultUsername");

        // Single Words
        Strings.Submit = allStrings.get("Submit");
        Strings.Clear = allStrings.get("Clear");
        Strings.Refresh = allStrings.get("Refresh");
        Strings.Close = allStrings.get("Close");
        Strings.Yes = allStrings.get("Yes");
        Strings.No = allStrings.get("No");
        Strings.Beginning = allStrings.get("Beginning");
        Strings.End = allStrings.get("End");
        Strings.QuestionMark = allStrings.get("QuestionMark");
        Strings.Shows = allStrings.get("Shows");
        Strings.Left = allStrings.get("Left");
        Strings.Settings = allStrings.get("Settings");
        Strings.Options = allStrings.get("Options");
        Strings.Main = allStrings.get("Main");
        Strings.Users = allStrings.get("Users");
        Strings.Other = allStrings.get("Other");
        Strings.Developers = allStrings.get("Developers");
        Strings.Reset = allStrings.get("Reset");
        Strings.About = allStrings.get("About");
        Strings.Season = allStrings.get("Season");
        Strings.Episode = allStrings.get("Episode");

        // Button Text
        Strings.SetSeasonEpisode = allStrings.get("SetSeasonEpisode");
        Strings.PlaySeasonEpisode = allStrings.get("PlaySeasonEpisode");
        Strings.HideShow = allStrings.get("HideShow");
        Strings.ResetTo = allStrings.get("ResetTo");
        Strings.OpenFileLocation = allStrings.get("OpenFileLocation");
        Strings.GetRemaining = allStrings.get("GetRemaining");
        Strings.PlayPreviousEpisode = allStrings.get("PlayPreviousEpisode");
        Strings.SetInactive = allStrings.get("SetInactive");
        Strings.SetActive = allStrings.get("SetActive");
        Strings.SwitchBetweenActiveInactiveList = allStrings.get("SwitchBetweenActiveInactiveList");
        Strings.AddUser = allStrings.get("AddUser");
        Strings.DeleteUser = allStrings.get("DeleteUser");
        Strings.ChangeUpdateTime = allStrings.get("ChangeUpdateTime");
        Strings.ForceRecheckShows = allStrings.get("ForceRecheckShows");
        Strings.AddDirectory = allStrings.get("AddDirectory");
        Strings.RemoveDirectory = allStrings.get("RemoveDirectory");
        Strings.OpenSettingsFolder = allStrings.get("OpenSettingsFolder");
        Strings.PrintAllShowInfo = allStrings.get("PrintAllShowInfo");
        Strings.PrintAllDirectories = allStrings.get("PrintAllDirectories");
        Strings.PrintEmptyShows = allStrings.get("PrintEmptyShows");
        Strings.PrintIgnoredShows = allStrings.get("PrintIgnoredShows");
        Strings.PrintHiddenShows = allStrings.get("PrintHiddenShows");
        Strings.UnHideAll = allStrings.get("UnHideAll");
        Strings.SetAllActive = allStrings.get("SetAllActive");
        Strings.SetAllInactive = allStrings.get("SetAllInactive");
        Strings.PrintPSFV = allStrings.get("PrintPSFV");
        Strings.PrintUSFV = allStrings.get("PrintUSFV");
        Strings.PrintAllUserInfo = allStrings.get("PrintAllUserInfo");
        Strings.DirectoryVersionPlus1 = allStrings.get("DirectoryVersionPlus1+1");
        Strings.ClearFile = allStrings.get("ClearFile");
        Strings.ResetProgram = allStrings.get("ResetProgram");

        // Tooltip Text
        Strings.ShowHideShowsWith0EpisodeLeft = allStrings.get("ShowHideShowsWith0EpisodeLeft");
        Strings.CurrentlyRechecking = allStrings.get("CurrentlyRechecking");
        Strings.DeleteUsersNoteCantDeleteCurrentUser = allStrings.get("DeleteUsersNoteCantDeleteCurrentUser");
        Strings.WarningUnrecoverable = allStrings.get("WarningUnrecoverable");

        // Other Text
        Strings.AddNewUsername = allStrings.get("AddNewUsername");
        Strings.PleaseEnterUsername = allStrings.get("PleaseEnterUsername");
        Strings.UseDefaultUsername = allStrings.get("UseDefaultUsername");
        Strings.DefaultUserNotSet = allStrings.get("DefaultUserNotSet");
        Strings.PleaseChooseAFolder = allStrings.get("PleaseChooseAFolder");
        Strings.PickTheEpisode = allStrings.get("PickTheEpisode");
        Strings.YouHaveToPickASeason = allStrings.get("YouHaveToPickASeason");
        Strings.YouHaveToPickAEpisode = allStrings.get("YouHaveToPickAEpisode");
        Strings.NextEpisode = allStrings.get("NextEpisode");
        Strings.UsernameIsntValid = allStrings.get("UsernameIsntValid");
        Strings.UsernameAlreadyTaken = allStrings.get("UsernameAlreadyTaken");
        Strings.UsernameIsTooLong = allStrings.get("UsernameIsTooLong");
        Strings.DirectoryIsAlreadyAdded = allStrings.get("DirectoryIsAlreadyAdded");
        Strings.MustBeANumberGreaterThanOrEqualTo10 = allStrings.get("MustBeANumberGreaterThanOrEqualTo10");
        Strings.ChooseYourUsername = allStrings.get("ChooseYourUsername");
        Strings.DirectoryWasADuplicate = allStrings.get("DirectoryWasADuplicate");
        Strings.AddAnotherDirectory = allStrings.get("AddAnotherDirectory");
        Strings.WhatShould = allStrings.get("WhatShould");
        Strings.BeResetTo = allStrings.get(" BeResetTo");
        Strings.ShowIsResetToThe = allStrings.get("ShowIsResetToThe");
        Strings.DoYouWantToOpenAllAssociatedFolders = allStrings.get("DoYouWantToOpenAllAssociatedFolders");
        Strings.PickTheFolderYouWantToOpen = allStrings.get("PickTheFolderYouWantToOpen");
        Strings.NoDirectlyPrecedingEpisodesFound = allStrings.get("NoDirectlyPrecedingEpisodesFound");
        Strings.HaveYouWatchedTheShow = allStrings.get("HaveYouWatchedTheShow");
        Strings.AreYouSure = allStrings.get("AreYouSure");
        Strings.PleaseChooseADefaultUser = allStrings.get("PleaseChooseADefaultUser");
        Strings.ThereAreNoOtherUsersToDelete = allStrings.get("ThereAreNoOtherUsersToDelete");
        Strings.UserToDelete = allStrings.get("UserToDelete");
        Strings.AreYouSureToWantToDelete = allStrings.get("AreYouSureToWantToDelete");
        Strings.EnterHowFastYouWantItToScanTheShowsFolders = allStrings.get("EnterHowFastYouWantItToScanTheShowsFolders");
        Strings.LeaveItAsIs = allStrings.get("LeaveItAsIs");
        Strings.PleaseEnterShowsDirectory = allStrings.get("PleaseEnterShowsDirectory");
        Strings.YouNeedToEnterADirectory = allStrings.get("YouNeedToEnterADirectory");
        Strings.DirectoryIsInvalid = allStrings.get("DirectoryIsInvalid");
        Strings.ThereAreNoDirectoriesToDelete = allStrings.get("ThereAreNoDirectoriesToDelete");
        Strings.ThereAreNoDirectoriesToClear = allStrings.get("ThereAreNoDirectoriesToClear");
        Strings.DirectoryToClear = allStrings.get("DirectoryToClear");
        Strings.AreYouSureToWantToClear = allStrings.get("AreYouSureToWantToClear");
        Strings.AreYouSureThisWillDeleteEverything = ("AreYouSureThisWillDeleteEverything");
        Strings.OpenChangesWindow = allStrings.get("OpenChangesWindow");
        Strings.Dev1 = allStrings.get("Dev1");
        Strings.Dev2 = allStrings.get("Dev2");
        Strings.SetDefaultUser = allStrings.get("SetDefaultUser");
        Strings.DirectoryToDelete = allStrings.get("DirectoryToDelete");
        Strings.PleaseChooseYourLanguage = allStrings.get("PleaseChooseYourLanguage");
        Strings.ChangeLanguage = allStrings.get("ChangeLanguage");
        Strings.RestartTheProgramForTheNewLanguageToTakeEffect = allStrings.get("RestartTheProgramForTheNewLanguageToTakeEffect");
    }

    public String getName() {
        return allStrings.get("LanguageName");
    }
}
