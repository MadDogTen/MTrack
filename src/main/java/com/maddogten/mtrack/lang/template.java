package com.maddogten.mtrack.lang;

import com.maddogten.mtrack.util.Strings;

import java.util.logging.Logger;

@SuppressWarnings("unused")
public class template extends Language {
    private final Logger log = Logger.getLogger(template.class.getName());

    public template() {
        LanguageName = "Blank Template";
    }

    @Override
    public final void setAllStrings() {
        log.info("Setting language to " + LanguageName + "...");

        // All Other Text
        Strings.DefaultUsername = "";
        Strings.TextMissing = "";

        // Single Words
        Strings.Submit = "";
        Strings.Clear = "";
        Strings.Refresh = "";
        Strings.Close = "";
        Strings.Yes = "";
        Strings.No = "";
        Strings.Beginning = "";
        Strings.End = "";
        Strings.QuestionMark = "";
        Strings.Shows = "";
        Strings.Left = "";
        Strings.Settings = "";
        Strings.Options = "";
        Strings.Main = "";
        Strings.Users = "";
        Strings.Other = "";
        Strings.Developers = "";
        Strings.Reset = "";
        Strings.About = "";
        Strings.Season = "";
        Strings.Episode = "";

        // Button Text
        Strings.SetSeasonEpisode = "";
        Strings.PlaySeasonEpisode = "";
        Strings.HideShow = "";
        Strings.ResetTo = "";
        Strings.OpenFileLocation = "";
        Strings.GetRemaining = "";
        Strings.PlayPreviousEpisode = "";
        Strings.SetInactive = "";
        Strings.SetActive = "";
        Strings.SwitchBetweenActiveInactiveList = "";
        Strings.AddUser = "";
        Strings.DeleteUser = "";
        Strings.ChangeUpdateTime = "";
        Strings.ForceRecheckShows = "";
        Strings.AddDirectory = "";
        Strings.RemoveDirectory = "";
        Strings.OpenSettingsFolder = "";
        Strings.PrintAllShowInfo = "";
        Strings.PrintAllDirectories = "";
        Strings.PrintEmptyShows = "";
        Strings.PrintIgnoredShows = "";
        Strings.PrintHiddenShows = "";
        Strings.UnHideAll = "";
        Strings.SetAllActive = "";
        Strings.SetAllInactive = "";
        Strings.PrintPSFV = "";
        Strings.PrintUSFV = "";
        Strings.PrintAllUserInfo = "";
        Strings.DirectoryVersionPlus1 = "";
        Strings.ClearFile = "";
        Strings.ResetProgram = "";
        Strings.ToggleDevMode = "";
        Strings.InAppData = "";
        Strings.WithTheJar = "";

        // Tooltip Text
        Strings.ShowHideShowsWith0EpisodeLeft = "";
        Strings.CurrentlyRechecking = "";
        Strings.DeleteUsersNoteCantDeleteCurrentUser = "";
        Strings.WarningUnrecoverable = "";

        // Other Text
        Strings.AddNewUsername = "";
        Strings.PleaseEnterUsername = "";
        Strings.UseDefaultUsername = "";
        Strings.DefaultUserNotSet = "";
        Strings.PleaseChooseAFolder = "";
        Strings.PickTheEpisode = "";
        Strings.YouHaveToPickASeason = "";
        Strings.YouHaveToPickAEpisode = "";
        Strings.NextEpisode = "";
        Strings.UsernameIsntValid = "";
        Strings.UsernameAlreadyTaken = "";
        Strings.UsernameIsTooLong = "";
        Strings.DirectoryIsAlreadyAdded = "";
        Strings.MustBeANumberGreaterThanOrEqualTo10 = "";
        Strings.ChooseYourUsername = "";
        Strings.DirectoryWasADuplicate = "";
        Strings.AddAnotherDirectory = "";
        Strings.WhatShould = "";
        Strings.BeResetTo = "";
        Strings.ShowIsResetToThe = "";
        Strings.DoYouWantToOpenAllAssociatedFolders = "";
        Strings.PickTheFolderYouWantToOpen = "";
        Strings.NoDirectlyPrecedingEpisodesFound = "";
        Strings.HaveYouWatchedTheShow = "";
        Strings.AreYouSure = "";
        Strings.PleaseChooseADefaultUser = "";
        Strings.ThereAreNoOtherUsersToDelete = "";
        Strings.UserToDelete = "";
        Strings.AreYouSureToWantToDelete = "";
        Strings.EnterHowFastYouWantItToScanTheShowsFolders = "";
        Strings.LeaveItAsIs = "";
        Strings.PleaseEnterShowsDirectory = "";
        Strings.YouNeedToEnterADirectory = "";
        Strings.DirectoryIsInvalid = "";
        Strings.ThereAreNoDirectoriesToDelete = "";
        Strings.ThereAreNoDirectoriesToClear = "";
        Strings.DirectoryToClear = "";
        Strings.AreYouSureToWantToClear = "";
        Strings.AreYouSureThisWillDeleteEverything = "";
        Strings.OpenChangesWindow = "";
        Strings.Dev1 = "";
        Strings.Dev2 = "";
        Strings.SetDefaultUser = "";
        Strings.DirectoryToDelete = "";
        Strings.PleaseChooseYourLanguage = "";
        Strings.ChangeLanguage = "";
        Strings.RestartTheProgramForTheNewLanguageToTakeEffect = "";
        Strings.WhereWouldYouLikeTheProgramFilesToBeStored = "";
        Strings.HoverOverAButtonForThePath = "";
        Strings.DashSeason = "";
        Strings.DashEpisode = "";
        Strings.PickTheSeasonAndEpisode = "";
        Strings.YouHaveReachedTheEnd = "";
        Strings.PingingDirectories = "";
        Strings.PathToDirectory = "";
    }
}