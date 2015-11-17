package com.maddogten.mtrack.util;

import com.maddogten.mtrack.lang.Language;
import com.maddogten.mtrack.lang.en_US;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LanguageHandler {
    // This file is a temporary solution, and will be changing.
    private final Logger log = Logger.getLogger(LanguageHandler.class.getName());

    private final Map<String, Language> languages = new HashMap<>();

    public LanguageHandler() {
        languages.put("en_US", new en_US());
        //languages.put("lipsum", new lipsum()); // Only temporary, For demonstration purposes. TODO Remove fully
    }

    public Map<String, String> getLanguageNames() {
        Map<String, String> languageNames = new HashMap<>();
        languages.forEach((internalName, languages) -> languageNames.put(internalName, languages.getName()));
        return languageNames;
    }

    public boolean setLanguage(String name) {
        log.info("Attempting to set language to " + name);
        if (languages.containsKey(name)) {
            languages.get(name).setAllStrings();
            return true;
        }
        return false;
    }

    // This verifies that all the Strings have been set, and if any haven't been, sets it as TextMissing. If TextMissing happens to be missing, it uses a default english string.
    public void addMissingTextForAllMissingStrings() {
        log.info("Checking for missing Strings...");
        if (Strings.TextMissing == null) Strings.TextMissing = "Text is missing, Please report.";

        if (Strings.Submit == null) Strings.Submit = Strings.TextMissing;
        if (Strings.Clear == null) Strings.Clear = Strings.TextMissing;
        if (Strings.Refresh == null) Strings.Refresh = Strings.TextMissing;
        if (Strings.Close == null) Strings.Close = Strings.TextMissing;
        if (Strings.Yes == null) Strings.Yes = Strings.TextMissing;
        if (Strings.No == null) Strings.No = Strings.TextMissing;
        if (Strings.Beginning == null) Strings.Beginning = Strings.TextMissing;
        if (Strings.End == null) Strings.End = Strings.TextMissing;
        if (Strings.QuestionMark == null) Strings.QuestionMark = Strings.TextMissing;
        if (Strings.Shows == null) Strings.Shows = Strings.TextMissing;
        if (Strings.Left == null) Strings.Left = Strings.TextMissing;
        if (Strings.Settings == null) Strings.Settings = Strings.TextMissing;
        if (Strings.Options == null) Strings.Options = Strings.TextMissing;
        if (Strings.Main == null) Strings.Main = Strings.TextMissing;
        if (Strings.Users == null) Strings.Users = Strings.TextMissing;
        if (Strings.Other == null) Strings.Other = Strings.TextMissing;
        if (Strings.Developers == null) Strings.Developers = Strings.TextMissing;
        if (Strings.Reset == null) Strings.Reset = Strings.TextMissing;
        if (Strings.About == null) Strings.About = Strings.TextMissing;
        if (Strings.Season == null) Strings.Season = Strings.TextMissing;
        if (Strings.Episode == null) Strings.Episode = Strings.TextMissing;
        if (Strings.SetSeasonEpisode == null) Strings.SetSeasonEpisode = Strings.TextMissing;
        if (Strings.PlaySeasonEpisode == null) Strings.PlaySeasonEpisode = Strings.TextMissing;
        if (Strings.HideShow == null) Strings.HideShow = Strings.TextMissing;
        if (Strings.ResetTo == null) Strings.ResetTo = Strings.TextMissing;
        if (Strings.OpenFileLocation == null) Strings.OpenFileLocation = Strings.TextMissing;
        if (Strings.GetRemaining == null) Strings.GetRemaining = Strings.TextMissing;
        if (Strings.PlayPreviousEpisode == null) Strings.PlayPreviousEpisode = Strings.TextMissing;
        if (Strings.SetInactive == null) Strings.SetInactive = Strings.TextMissing;
        if (Strings.SetActive == null) Strings.SetActive = Strings.TextMissing;
        if (Strings.SwitchBetweenActiveInactiveList == null)
            Strings.SwitchBetweenActiveInactiveList = Strings.TextMissing;
        if (Strings.AddUser == null) Strings.AddUser = Strings.TextMissing;
        if (Strings.DeleteUser == null) Strings.DeleteUser = Strings.TextMissing;
        if (Strings.ChangeUpdateTime == null) Strings.ChangeUpdateTime = Strings.TextMissing;
        if (Strings.ForceRecheckShows == null) Strings.ForceRecheckShows = Strings.TextMissing;
        if (Strings.AddDirectory == null) Strings.AddDirectory = Strings.TextMissing;
        if (Strings.RemoveDirectory == null) Strings.RemoveDirectory = Strings.TextMissing;
        if (Strings.OpenSettingsFolder == null) Strings.OpenSettingsFolder = Strings.TextMissing;
        if (Strings.PrintAllShowInfo == null) Strings.PrintAllShowInfo = Strings.TextMissing;
        if (Strings.PrintAllDirectories == null) Strings.PrintAllDirectories = Strings.TextMissing;
        if (Strings.PrintEmptyShows == null) Strings.PrintEmptyShows = Strings.TextMissing;
        if (Strings.PrintIgnoredShows == null) Strings.PrintIgnoredShows = Strings.TextMissing;
        if (Strings.PrintHiddenShows == null) Strings.PrintHiddenShows = Strings.TextMissing;
        if (Strings.UnHideAll == null) Strings.UnHideAll = Strings.TextMissing;
        if (Strings.SetAllActive == null) Strings.SetAllActive = Strings.TextMissing;
        if (Strings.SetAllInactive == null) Strings.SetAllInactive = Strings.TextMissing;
        if (Strings.PrintPSFV == null) Strings.PrintPSFV = Strings.TextMissing;
        if (Strings.PrintUSFV == null) Strings.PrintUSFV = Strings.TextMissing;
        if (Strings.PrintAllUserInfo == null) Strings.PrintAllUserInfo = Strings.TextMissing;
        if (Strings.DirectoryVersionPlus1 == null) Strings.DirectoryVersionPlus1 = Strings.TextMissing;
        if (Strings.ClearFile == null) Strings.ClearFile = Strings.TextMissing;
        if (Strings.ResetProgram == null) Strings.ResetProgram = Strings.TextMissing;
        if (Strings.ToggleDevMode == null) Strings.ToggleDevMode = Strings.TextMissing;
        if (Strings.InAppData == null) Strings.InAppData = Strings.TextMissing;
        if (Strings.WithTheJar == null) Strings.WithTheJar = Strings.TextMissing;
        if (Strings.ShowHideShowsWith0EpisodeLeft == null) Strings.ShowHideShowsWith0EpisodeLeft = Strings.TextMissing;
        if (Strings.CurrentlyRechecking == null) Strings.CurrentlyRechecking = Strings.TextMissing;
        if (Strings.DeleteUsersNoteCantDeleteCurrentUser == null)
            Strings.DeleteUsersNoteCantDeleteCurrentUser = Strings.TextMissing;
        if (Strings.WarningUnrecoverable == null) Strings.WarningUnrecoverable = Strings.TextMissing;
        if (Strings.AddNewUsername == null) Strings.AddNewUsername = Strings.TextMissing;
        if (Strings.PleaseEnterUsername == null) Strings.PleaseEnterUsername = Strings.TextMissing;
        if (Strings.UseDefaultUsername == null) Strings.UseDefaultUsername = Strings.TextMissing;
        if (Strings.DefaultUserNotSet == null) Strings.DefaultUserNotSet = Strings.TextMissing;
        if (Strings.PleaseChooseAFolder == null) Strings.PleaseChooseAFolder = Strings.TextMissing;
        if (Strings.PickTheEpisode == null) Strings.PickTheEpisode = Strings.TextMissing;
        if (Strings.YouHaveToPickASeason == null) Strings.YouHaveToPickASeason = Strings.TextMissing;
        if (Strings.YouHaveToPickAEpisode == null) Strings.YouHaveToPickAEpisode = Strings.TextMissing;
        if (Strings.NextEpisode == null) Strings.NextEpisode = Strings.TextMissing;
        if (Strings.UsernameIsntValid == null) Strings.UsernameIsntValid = Strings.TextMissing;
        if (Strings.UsernameAlreadyTaken == null) Strings.UsernameAlreadyTaken = Strings.TextMissing;
        if (Strings.UsernameIsTooLong == null) Strings.UsernameIsTooLong = Strings.TextMissing;
        if (Strings.DirectoryIsAlreadyAdded == null) Strings.DirectoryIsAlreadyAdded = Strings.TextMissing;
        if (Strings.MustBeANumberGreaterThanOrEqualTo10 == null)
            Strings.MustBeANumberGreaterThanOrEqualTo10 = Strings.TextMissing;
        if (Strings.ChooseYourUsername == null) Strings.ChooseYourUsername = Strings.TextMissing;
        if (Strings.DirectoryWasADuplicate == null) Strings.DirectoryWasADuplicate = Strings.TextMissing;
        if (Strings.AddAnotherDirectory == null) Strings.AddAnotherDirectory = Strings.TextMissing;
        if (Strings.WhatShould == null) Strings.WhatShould = Strings.TextMissing;
        if (Strings.BeResetTo == null) Strings.BeResetTo = Strings.TextMissing;
        if (Strings.ShowIsResetToThe == null) Strings.ShowIsResetToThe = Strings.TextMissing;
        if (Strings.DoYouWantToOpenAllAssociatedFolders == null)
            Strings.DoYouWantToOpenAllAssociatedFolders = Strings.TextMissing;
        if (Strings.PickTheFolderYouWantToOpen == null) Strings.PickTheFolderYouWantToOpen = Strings.TextMissing;
        if (Strings.NoDirectlyPrecedingEpisodesFound == null)
            Strings.NoDirectlyPrecedingEpisodesFound = Strings.TextMissing;
        if (Strings.HaveYouWatchedTheShow == null) Strings.HaveYouWatchedTheShow = Strings.TextMissing;
        if (Strings.AreYouSure == null) Strings.AreYouSure = Strings.TextMissing;
        if (Strings.PleaseChooseADefaultUser == null) Strings.PleaseChooseADefaultUser = Strings.TextMissing;
        if (Strings.ThereAreNoOtherUsersToDelete == null) Strings.ThereAreNoOtherUsersToDelete = Strings.TextMissing;
        if (Strings.UserToDelete == null) Strings.UserToDelete = Strings.TextMissing;
        if (Strings.AreYouSureToWantToDelete == null) Strings.AreYouSureToWantToDelete = Strings.TextMissing;
        if (Strings.EnterHowFastYouWantItToScanTheShowsFolders == null)
            Strings.EnterHowFastYouWantItToScanTheShowsFolders = Strings.TextMissing;
        if (Strings.LeaveItAsIs == null) Strings.LeaveItAsIs = Strings.TextMissing;
        if (Strings.PleaseEnterShowsDirectory == null) Strings.PleaseEnterShowsDirectory = Strings.TextMissing;
        if (Strings.YouNeedToEnterADirectory == null) Strings.YouNeedToEnterADirectory = Strings.TextMissing;
        if (Strings.DirectoryIsInvalid == null) Strings.DirectoryIsInvalid = Strings.TextMissing;
        if (Strings.ThereAreNoDirectoriesToDelete == null) Strings.ThereAreNoDirectoriesToDelete = Strings.TextMissing;
        if (Strings.ThereAreNoDirectoriesToClear == null) Strings.ThereAreNoDirectoriesToClear = Strings.TextMissing;
        if (Strings.DirectoryToClear == null) Strings.DirectoryToClear = Strings.TextMissing;
        if (Strings.AreYouSureToWantToClear == null) Strings.AreYouSureToWantToClear = Strings.TextMissing;
        if (Strings.AreYouSureThisWillDeleteEverything == null)
            Strings.AreYouSureThisWillDeleteEverything = Strings.TextMissing;
        if (Strings.OpenChangesWindow == null) Strings.OpenChangesWindow = Strings.TextMissing;
        if (Strings.Dev1 == null) Strings.Dev1 = Strings.TextMissing;
        if (Strings.Dev2 == null) Strings.Dev2 = Strings.TextMissing;
        if (Strings.SetDefaultUser == null) Strings.SetDefaultUser = Strings.TextMissing;
        if (Strings.DirectoryToDelete == null) Strings.DirectoryToDelete = Strings.TextMissing;
        if (Strings.PleaseChooseYourLanguage == null) Strings.PleaseChooseYourLanguage = Strings.TextMissing;
        if (Strings.ChangeLanguage == null) Strings.ChangeLanguage = Strings.TextMissing;
        if (Strings.RestartTheProgramForTheNewLanguageToTakeEffect == null)
            Strings.RestartTheProgramForTheNewLanguageToTakeEffect = Strings.TextMissing;
        if (Strings.WhereWouldYouLikeTheProgramFilesToBeStored == null)
            Strings.WhereWouldYouLikeTheProgramFilesToBeStored = Strings.TextMissing;
        if (Strings.HoverOverAButtonForThePath == null) Strings.HoverOverAButtonForThePath = Strings.TextMissing;
        if (Strings.DashSeason == null) Strings.DashSeason = Strings.TextMissing;
        if (Strings.DashEpisode == null) Strings.DashEpisode = Strings.TextMissing;
        if (Strings.PickTheSeasonAndEpisode == null) Strings.PickTheSeasonAndEpisode = Strings.TextMissing;
        if (Strings.YouHaveReachedTheEnd == null) Strings.YouHaveReachedTheEnd = Strings.TextMissing;
        if (Strings.PingingDirectories == null) Strings.PingingDirectories = Strings.TextMissing;
        if (Strings.PathToDirectory == null) Strings.PathToDirectory = Strings.TextMissing;
        log.info("Finished checking for missing Strings.");
    }
}
