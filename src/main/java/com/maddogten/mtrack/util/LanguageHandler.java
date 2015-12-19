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
        //languages.put("lipsum", new lipsum()); // Only temporary, For demonstration purposes. TODO <- Eventually Remove
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
            addMissingTextForAllMissingStrings();
            return true;
        }
        return false;
    }

    // This verifies that all the Strings have been set, and if any haven't been, sets it as TextMissing. If TextMissing happens to be missing, it uses a default english string.
    private void addMissingTextForAllMissingStrings() {
        log.info("Checking for missing strings...");
        if (isStringMissing(Strings.TextMissing)) Strings.TextMissing = "Proper text is missing, Please report.";

        if (isStringMissing(Strings.Submit)) Strings.Submit = Strings.TextMissing;
        if (isStringMissing(Strings.Clear)) Strings.Clear = Strings.TextMissing;
        if (isStringMissing(Strings.Refresh)) Strings.Refresh = Strings.TextMissing;
        if (isStringMissing(Strings.Close)) Strings.Close = Strings.TextMissing;
        if (isStringMissing(Strings.Yes)) Strings.Yes = Strings.TextMissing;
        if (isStringMissing(Strings.No)) Strings.No = Strings.TextMissing;
        if (isStringMissing(Strings.Beginning)) Strings.Beginning = Strings.TextMissing;
        if (isStringMissing(Strings.End)) Strings.End = Strings.TextMissing;
        if (isStringMissing(Strings.QuestionMark)) Strings.QuestionMark = Strings.TextMissing;
        if (isStringMissing(Strings.Shows)) Strings.Shows = Strings.TextMissing;
        if (isStringMissing(Strings.Left)) Strings.Left = Strings.TextMissing;
        if (isStringMissing(Strings.Settings)) Strings.Settings = Strings.TextMissing;
        if (isStringMissing(Strings.Options)) Strings.Options = Strings.TextMissing;
        if (isStringMissing(Strings.Main)) Strings.Main = Strings.TextMissing;
        if (isStringMissing(Strings.Users)) Strings.Users = Strings.TextMissing;
        if (isStringMissing(Strings.Other)) Strings.Other = Strings.TextMissing;
        if (isStringMissing(Strings.Developers)) Strings.Developers = Strings.TextMissing;
        if (isStringMissing(Strings.Reset)) Strings.Reset = Strings.TextMissing;
        if (isStringMissing(Strings.About)) Strings.About = Strings.TextMissing;
        if (isStringMissing(Strings.Season)) Strings.Season = Strings.TextMissing;
        if (isStringMissing(Strings.Episode)) Strings.Episode = Strings.TextMissing;
        if (isStringMissing(Strings.Set)) Strings.Set = Strings.TextMissing;
        if (isStringMissing(Strings.Warning)) Strings.Warning = Strings.TextMissing;
        if (isStringMissing(Strings.SetSeasonEpisode)) Strings.SetSeasonEpisode = Strings.TextMissing;
        if (isStringMissing(Strings.PlaySeasonEpisode)) Strings.PlaySeasonEpisode = Strings.TextMissing;
        if (isStringMissing(Strings.HideShow)) Strings.HideShow = Strings.TextMissing;
        if (isStringMissing(Strings.ResetTo)) Strings.ResetTo = Strings.TextMissing;
        if (isStringMissing(Strings.OpenFileLocation)) Strings.OpenFileLocation = Strings.TextMissing;
        if (isStringMissing(Strings.GetRemaining)) Strings.GetRemaining = Strings.TextMissing;
        if (isStringMissing(Strings.PlayPreviousEpisode)) Strings.PlayPreviousEpisode = Strings.TextMissing;
        if (isStringMissing(Strings.SetInactive)) Strings.SetInactive = Strings.TextMissing;
        if (isStringMissing(Strings.SetActive)) Strings.SetActive = Strings.TextMissing;
        if (isStringMissing(Strings.SwitchBetweenActiveInactiveList))
            Strings.SwitchBetweenActiveInactiveList = Strings.TextMissing;
        if (isStringMissing(Strings.AddUser)) Strings.AddUser = Strings.TextMissing;
        if (isStringMissing(Strings.DeleteUser)) Strings.DeleteUser = Strings.TextMissing;
        if (isStringMissing(Strings.ChangeUpdateTime)) Strings.ChangeUpdateTime = Strings.TextMissing;
        if (isStringMissing(Strings.ForceRecheckShows)) Strings.ForceRecheckShows = Strings.TextMissing;
        if (isStringMissing(Strings.AddDirectory)) Strings.AddDirectory = Strings.TextMissing;
        if (isStringMissing(Strings.RemoveDirectory)) Strings.RemoveDirectory = Strings.TextMissing;
        if (isStringMissing(Strings.OpenSettingsFolder)) Strings.OpenSettingsFolder = Strings.TextMissing;
        if (isStringMissing(Strings.PrintAllShowInfo)) Strings.PrintAllShowInfo = Strings.TextMissing;
        if (isStringMissing(Strings.PrintAllDirectories)) Strings.PrintAllDirectories = Strings.TextMissing;
        if (isStringMissing(Strings.PrintEmptyShows)) Strings.PrintEmptyShows = Strings.TextMissing;
        if (isStringMissing(Strings.PrintIgnoredShows)) Strings.PrintIgnoredShows = Strings.TextMissing;
        if (isStringMissing(Strings.PrintHiddenShows)) Strings.PrintHiddenShows = Strings.TextMissing;
        if (isStringMissing(Strings.UnHideAll)) Strings.UnHideAll = Strings.TextMissing;
        if (isStringMissing(Strings.SetAllActive)) Strings.SetAllActive = Strings.TextMissing;
        if (isStringMissing(Strings.SetAllInactive)) Strings.SetAllInactive = Strings.TextMissing;
        if (isStringMissing(Strings.PrintPSFV)) Strings.PrintPSFV = Strings.TextMissing;
        if (isStringMissing(Strings.PrintUSFV)) Strings.PrintUSFV = Strings.TextMissing;
        if (isStringMissing(Strings.PrintAllUserInfo)) Strings.PrintAllUserInfo = Strings.TextMissing;
        if (isStringMissing(Strings.DirectoryVersionPlus1)) Strings.DirectoryVersionPlus1 = Strings.TextMissing;
        if (isStringMissing(Strings.ClearFile)) Strings.ClearFile = Strings.TextMissing;
        if (isStringMissing(Strings.ResetProgram)) Strings.ResetProgram = Strings.TextMissing;
        if (isStringMissing(Strings.ToggleDevMode)) Strings.ToggleDevMode = Strings.TextMissing;
        if (isStringMissing(Strings.InAppData)) Strings.InAppData = Strings.TextMissing;
        if (isStringMissing(Strings.WithTheJar)) Strings.WithTheJar = Strings.TextMissing;
        if (isStringMissing(Strings.UpdateTime)) Strings.UpdateTime = Strings.TextMissing;
        if (isStringMissing(Strings.DirectoryTimeout)) Strings.DirectoryTimeout = Strings.TextMissing;
        if (isStringMissing(Strings.ShowHiddenShowsWith0EpisodeLeft))
            Strings.ShowHiddenShowsWith0EpisodeLeft = Strings.TextMissing;
        if (isStringMissing(Strings.CurrentlyRechecking)) Strings.CurrentlyRechecking = Strings.TextMissing;
        if (isStringMissing(Strings.DeleteUsersNoteCantDeleteCurrentUser))
            Strings.DeleteUsersNoteCantDeleteCurrentUser = Strings.TextMissing;
        if (isStringMissing(Strings.WarningUnrecoverable)) Strings.WarningUnrecoverable = Strings.TextMissing;
        if (isStringMissing(Strings.AddNewUsername)) Strings.AddNewUsername = Strings.TextMissing;
        if (isStringMissing(Strings.PleaseEnterUsername)) Strings.PleaseEnterUsername = Strings.TextMissing;
        if (isStringMissing(Strings.UseDefaultUsername)) Strings.UseDefaultUsername = Strings.TextMissing;
        if (isStringMissing(Strings.DefaultUserNotSet)) Strings.DefaultUserNotSet = Strings.TextMissing;
        if (isStringMissing(Strings.PleaseChooseAFolder)) Strings.PleaseChooseAFolder = Strings.TextMissing;
        if (isStringMissing(Strings.PickTheEpisode)) Strings.PickTheEpisode = Strings.TextMissing;
        if (isStringMissing(Strings.YouHaveToPickASeason)) Strings.YouHaveToPickASeason = Strings.TextMissing;
        if (isStringMissing(Strings.YouHaveToPickAEpisode)) Strings.YouHaveToPickAEpisode = Strings.TextMissing;
        if (isStringMissing(Strings.NextEpisode)) Strings.NextEpisode = Strings.TextMissing;
        if (isStringMissing(Strings.UsernameIsntValid)) Strings.UsernameIsntValid = Strings.TextMissing;
        if (isStringMissing(Strings.UsernameAlreadyTaken)) Strings.UsernameAlreadyTaken = Strings.TextMissing;
        if (isStringMissing(Strings.UsernameIsTooLong)) Strings.UsernameIsTooLong = Strings.TextMissing;
        if (isStringMissing(Strings.DirectoryIsAlreadyAdded)) Strings.DirectoryIsAlreadyAdded = Strings.TextMissing;
        if (isStringMissing(Strings.MustBeANumberBetween))
            Strings.MustBeANumberBetween = Strings.TextMissing;
        if (isStringMissing(Strings.ChooseYourUsername)) Strings.ChooseYourUsername = Strings.TextMissing;
        if (isStringMissing(Strings.DirectoryWasADuplicate)) Strings.DirectoryWasADuplicate = Strings.TextMissing;
        if (isStringMissing(Strings.AddAnotherDirectory)) Strings.AddAnotherDirectory = Strings.TextMissing;
        if (isStringMissing(Strings.WhatShould)) Strings.WhatShould = Strings.TextMissing;
        if (isStringMissing(Strings.BeResetTo)) Strings.BeResetTo = Strings.TextMissing;
        if (isStringMissing(Strings.ShowIsResetToThe)) Strings.ShowIsResetToThe = Strings.TextMissing;
        if (isStringMissing(Strings.DoYouWantToOpenAllAssociatedFolders))
            Strings.DoYouWantToOpenAllAssociatedFolders = Strings.TextMissing;
        if (isStringMissing(Strings.PickTheFolderYouWantToOpen))
            Strings.PickTheFolderYouWantToOpen = Strings.TextMissing;
        if (isStringMissing(Strings.NoDirectlyPrecedingEpisodesFound))
            Strings.NoDirectlyPrecedingEpisodesFound = Strings.TextMissing;
        if (isStringMissing(Strings.HaveYouWatchedTheShow)) Strings.HaveYouWatchedTheShow = Strings.TextMissing;
        if (isStringMissing(Strings.AreYouSure)) Strings.AreYouSure = Strings.TextMissing;
        if (isStringMissing(Strings.PleaseChooseADefaultUser)) Strings.PleaseChooseADefaultUser = Strings.TextMissing;
        if (isStringMissing(Strings.ThereAreNoOtherUsersToDelete))
            Strings.ThereAreNoOtherUsersToDelete = Strings.TextMissing;
        if (isStringMissing(Strings.UserToDelete)) Strings.UserToDelete = Strings.TextMissing;
        if (isStringMissing(Strings.AreYouSureToWantToDelete)) Strings.AreYouSureToWantToDelete = Strings.TextMissing;
        if (isStringMissing(Strings.EnterHowFastYouWantItToScanTheShowsFolders))
            Strings.EnterHowFastYouWantItToScanTheShowsFolders = Strings.TextMissing;
        if (isStringMissing(Strings.LeaveItAsIs)) Strings.LeaveItAsIs = Strings.TextMissing;
        if (isStringMissing(Strings.PleaseEnterShowsDirectory)) Strings.PleaseEnterShowsDirectory = Strings.TextMissing;
        if (isStringMissing(Strings.YouNeedToEnterADirectory)) Strings.YouNeedToEnterADirectory = Strings.TextMissing;
        if (isStringMissing(Strings.DirectoryIsInvalid)) Strings.DirectoryIsInvalid = Strings.TextMissing;
        if (isStringMissing(Strings.ThereAreNoDirectoriesToDelete))
            Strings.ThereAreNoDirectoriesToDelete = Strings.TextMissing;
        if (isStringMissing(Strings.ThereAreNoDirectoriesToClear))
            Strings.ThereAreNoDirectoriesToClear = Strings.TextMissing;
        if (isStringMissing(Strings.DirectoryToClear)) Strings.DirectoryToClear = Strings.TextMissing;
        if (isStringMissing(Strings.AreYouSureToWantToClear)) Strings.AreYouSureToWantToClear = Strings.TextMissing;
        if (isStringMissing(Strings.AreYouSureThisWillDeleteEverything))
            Strings.AreYouSureThisWillDeleteEverything = Strings.TextMissing;
        if (isStringMissing(Strings.OpenChangesWindow)) Strings.OpenChangesWindow = Strings.TextMissing;
        if (isStringMissing(Strings.Dev1)) Strings.Dev1 = Strings.TextMissing;
        if (isStringMissing(Strings.Dev2)) Strings.Dev2 = Strings.TextMissing;
        if (isStringMissing(Strings.SetDefaultUser)) Strings.SetDefaultUser = Strings.TextMissing;
        if (isStringMissing(Strings.DirectoryToDelete)) Strings.DirectoryToDelete = Strings.TextMissing;
        if (isStringMissing(Strings.PleaseChooseYourLanguage)) Strings.PleaseChooseYourLanguage = Strings.TextMissing;
        if (isStringMissing(Strings.ChangeLanguage)) Strings.ChangeLanguage = Strings.TextMissing;
        if (isStringMissing(Strings.RestartTheProgramForTheNewLanguageToTakeEffect))
            Strings.RestartTheProgramForTheNewLanguageToTakeEffect = Strings.TextMissing;
        if (isStringMissing(Strings.WhereWouldYouLikeTheProgramFilesToBeStored))
            Strings.WhereWouldYouLikeTheProgramFilesToBeStored = Strings.TextMissing;
        if (isStringMissing(Strings.HoverOverAButtonForThePath))
            Strings.HoverOverAButtonForThePath = Strings.TextMissing;
        if (isStringMissing(Strings.DashSeason)) Strings.DashSeason = Strings.TextMissing;
        if (isStringMissing(Strings.DashEpisode)) Strings.DashEpisode = Strings.TextMissing;
        if (isStringMissing(Strings.PickTheSeasonAndEpisode)) Strings.PickTheSeasonAndEpisode = Strings.TextMissing;
        if (isStringMissing(Strings.YouHaveReachedTheEnd)) Strings.YouHaveReachedTheEnd = Strings.TextMissing;
        if (isStringMissing(Strings.PingingDirectories)) Strings.PingingDirectories = Strings.TextMissing;
        if (isStringMissing(Strings.PathToDirectory)) Strings.PathToDirectory = Strings.TextMissing;
        if (isStringMissing(Strings.PrintCurrentSeasonEpisode)) Strings.PrintCurrentSeasonEpisode = Strings.TextMissing;
        if (isStringMissing(Strings.WasFoundToBeInactive)) Strings.WasFoundToBeInactive = Strings.TextMissing;
        if (isStringMissing(Strings.PleaseCorrectTheIssueThenForceRefresh))
            Strings.PleaseCorrectTheIssueThenForceRefresh = Strings.TextMissing;
        if (isStringMissing(Strings.InternalVersion)) Strings.InternalVersion = Strings.TextMissing;
        if (isStringMissing(Strings.InactiveShows)) Strings.InactiveShows = Strings.TextMissing;
        if (isStringMissing(Strings.OlderSeasons)) Strings.OlderSeasons = Strings.TextMissing;
        if (isStringMissing(Strings.NotifyChangesFor)) Strings.NotifyChangesFor = Strings.TextMissing;
        if (isStringMissing(Strings.OnlyChecksEveryRuns)) Strings.OnlyChecksEveryRuns = Strings.TextMissing;
        if (isStringMissing(Strings.AllowFullWindowMovementUse))
            Strings.AllowFullWindowMovementUse = Strings.TextMissing;
        log.info("Finished checking for missing strings.");
    }

    private boolean isStringMissing(String stringToCheck) {
        return stringToCheck == null || stringToCheck.isEmpty();
    }
}
