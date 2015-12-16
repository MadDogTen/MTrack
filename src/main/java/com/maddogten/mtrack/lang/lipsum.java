package com.maddogten.mtrack.lang;

import com.maddogten.mtrack.util.Strings;

import java.util.logging.Logger;

@SuppressWarnings("SpellCheckingInspection")
public class lipsum extends Language {
    private final Logger log = Logger.getLogger(lipsum.class.getName());

    @SuppressWarnings("WeakerAccess")
    public lipsum() {
        LanguageName = "Lipsum";
    }

    @Override
    public final void setAllStrings() {
        log.info("Setting language to " + LanguageName + "...");

        // All Other Text
        Strings.DefaultUsername = "curabitur";
        Strings.TextMissing = "manduris, deliour hiberious.";

        // Single Words
        Strings.Submit = "galatirm";
        Strings.Clear = "tortor";
        Strings.Refresh = "pulvinar";
        Strings.Close = "iaculis";
        Strings.Yes = "dapibus";
        Strings.No = "leo";
        Strings.Beginning = "libero";
        Strings.End = "volutpat";
        Strings.QuestionMark = "libero";
        Strings.Shows = "vitae";
        Strings.Left = "dapibus";
        Strings.Settings = "nulla";
        Strings.Options = "diam ";
        Strings.Main = "lacus";
        Strings.Users = "maecenas";
        Strings.Other = "accumsan";
        Strings.Developers = "fermentum";
        Strings.Reset = "massa";
        Strings.About = "eget";
        Strings.Season = "vertamis";
        Strings.Episode = "medutu";
        Strings.Set = "hegu";
        Strings.Warning = "murta";

        // Button Text
        Strings.SetSeasonEpisode = "malesuada";
        Strings.PlaySeasonEpisode = "vestibulum";
        Strings.HideShow = "nisl";
        Strings.ResetTo = "risus";
        Strings.OpenFileLocation = "tempus";
        Strings.GetRemaining = "lobortis";
        Strings.PlayPreviousEpisode = "eget";
        Strings.SetInactive = "ante";
        Strings.SetActive = "maecenas";
        Strings.SwitchBetweenActiveInactiveList = "porta";
        Strings.AddUser = "nibh";
        Strings.DeleteUser = "sed ";
        Strings.ChangeUpdateTime = "pretium";
        Strings.ForceRecheckShows = "ante";
        Strings.AddDirectory = "viverra";
        Strings.RemoveDirectory = "fringilla";
        Strings.OpenSettingsFolder = "Maecenas";
        Strings.PrintAllShowInfo = "rhoncus ";
        Strings.PrintAllDirectories = "rutrum";
        Strings.PrintEmptyShows = "donec";
        Strings.PrintIgnoredShows = "maximus";
        Strings.PrintHiddenShows = "eros";
        Strings.UnHideAll = "vitae";
        Strings.SetAllActive = "commodo";
        Strings.SetAllInactive = "ultrices";
        Strings.PrintPSFV = "morbi";
        Strings.PrintUSFV = "bibendum";
        Strings.PrintAllUserInfo = "magna";
        Strings.DirectoryVersionPlus1 = "sagittis";
        Strings.ClearFile = "vehicula";
        Strings.ResetProgram = "enim";
        Strings.ToggleDevMode = "gulamina";
        Strings.InAppData = "burgula";
        Strings.WithTheJar = "zemberna";
        Strings.UpdateTime = "nunula";
        Strings.DirectoryTimeout = "termula";

        // CheckBox Text
        Strings.InactiveShows = "miterra";
        Strings.OlderSeasons = "avartu";

        // Tooltip Text
        Strings.ShowHiddenShowsWith0EpisodeLeft = "sollicitudin";
        Strings.CurrentlyRechecking = "nisi";
        Strings.DeleteUsersNoteCantDeleteCurrentUser = "Nulla";
        Strings.WarningUnrecoverable = "vitae";
        Strings.InternalVersion = "gataluma";

        // Other Text
        Strings.AddNewUsername = "porta";
        Strings.PleaseEnterUsername = "aliquet";
        Strings.UseDefaultUsername = "volutpat";
        Strings.DefaultUserNotSet = "malesuada";
        Strings.PleaseChooseAFolder = "pellentesque";
        Strings.PickTheEpisode = "habitant";
        Strings.YouHaveToPickASeason = "morbi";
        Strings.YouHaveToPickAEpisode = "tristique";
        Strings.NextEpisode = "senectus";
        Strings.UsernameIsntValid = "netus";
        Strings.UsernameAlreadyTaken = "malesuada";
        Strings.UsernameIsTooLong = "fames";
        Strings.DirectoryIsAlreadyAdded = "turpis";
        Strings.MustBeANumberBetween = "egestas";
        Strings.ChooseYourUsername = "Vivamus";
        Strings.DirectoryWasADuplicate = "faucibus";
        Strings.AddAnotherDirectory = "felis";
        Strings.WhatShould = "lorem";
        Strings.BeResetTo = "ullamcorper";
        Strings.ShowIsResetToThe = "fringilla";
        Strings.DoYouWantToOpenAllAssociatedFolders = "donec";
        Strings.PickTheFolderYouWantToOpen = "varius";
        Strings.NoDirectlyPrecedingEpisodesFound = "rutrum";
        Strings.HaveYouWatchedTheShow = "donec";
        Strings.AreYouSure = "condimentum";
        Strings.PleaseChooseADefaultUser = "lacinia";
        Strings.ThereAreNoOtherUsersToDelete = "praesent";
        Strings.UserToDelete = "phasellus";
        Strings.AreYouSureToWantToDelete = "pharetra";
        Strings.EnterHowFastYouWantItToScanTheShowsFolders = "pulvinar";
        Strings.LeaveItAsIs = "hendrerit";
        Strings.PleaseEnterShowsDirectory = "fusce";
        Strings.YouNeedToEnterADirectory = "quisque";
        Strings.DirectoryIsInvalid = "tincidunt";
        Strings.ThereAreNoDirectoriesToDelete = "suspendisse";
        Strings.ThereAreNoDirectoriesToClear = "iaculis";
        Strings.DirectoryToClear = "curabitur";
        Strings.AreYouSureToWantToClear = "tincidunt";
        Strings.AreYouSureThisWillDeleteEverything = "velit";
        Strings.OpenChangesWindow = "lectus";
        Strings.Dev1 = "suscipit";
        Strings.Dev2 = "ipsum";
        Strings.SetDefaultUser = "magnis";
        Strings.DirectoryToDelete = "nascetur";
        Strings.PleaseChooseYourLanguage = "hibuatu";
        Strings.ChangeLanguage = "Gibula";
        Strings.RestartTheProgramForTheNewLanguageToTakeEffect = "veralib";
        Strings.WhereWouldYouLikeTheProgramFilesToBeStored = "abbera";
        Strings.HoverOverAButtonForThePath = "mezua";
        Strings.DashSeason = " - megatu";
        Strings.DashEpisode = " - jesuma";
        Strings.PickTheSeasonAndEpisode = "vertuta";
        Strings.YouHaveReachedTheEnd = "galaminka";
        Strings.PingingDirectories = "vermuna";
        Strings.PathToDirectory = "mezera";
        Strings.WasFoundToBeInactive = "galamru";
        Strings.PleaseCorrectTheIssueThenForceRefresh = "altez";
        Strings.NotifyChangesFor = "iternu";
        Strings.OnlyChecksEveryRuns = "viter";
    }
}
