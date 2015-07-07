package program.lang;

import program.util.Strings;

import java.util.HashMap;
import java.util.logging.Logger;

@SuppressWarnings("SpellCheckingInspection")
public class lipsum {
    private final Logger log = Logger.getLogger(lipsum.class.getName());

    private final HashMap<String, String> allStrings = new HashMap<>();

    public lipsum() {
        log.info("Loading lipsum...");

        // All Other Text
        allStrings.put("LanguageName", "Lipsum");
        allStrings.put("DefaultUsername", "curabitur");
        allStrings.put("TextMissing", "manduris, deliour hiberious.");

        // Single Words
        allStrings.put("Submit", "galatirm");
        allStrings.put("Clear", "tortor");
        allStrings.put("Refresh", "pulvinar");
        allStrings.put("Close", "iaculis");
        allStrings.put("Yes", "dapibus");
        allStrings.put("No", "leo");
        allStrings.put("Beginning", "libero");
        allStrings.put("End", "volutpat");
        allStrings.put("QuestionMark", "libero");
        allStrings.put("Shows", "vitae");
        allStrings.put("Left", "dapibus");
        allStrings.put("Settings", "nulla");
        allStrings.put("Options", "diam ");
        allStrings.put("Main", "lacus");
        allStrings.put("Users", "maecenas");
        allStrings.put("Other", "accumsan");
        allStrings.put("Developers", "fermentum");
        allStrings.put("Reset", "massa");
        allStrings.put("About", "eget");
        // Button Text
        allStrings.put("SetSeasonEpisode", "malesuada");
        allStrings.put("PlaySeasonEpisode", "vestibulum");
        allStrings.put("HideShow", "nisl");
        allStrings.put("ResetTo", "risus");
        allStrings.put("OpenFileLocation", "tempus");
        allStrings.put("GetRemaining", "lobortis");
        allStrings.put("PlayPreviousEpisode", "eget");
        allStrings.put("SetInactive", "ante");
        allStrings.put("SetActive", "maecenas");
        allStrings.put("SwitchBetweenActiveInactiveList", "porta");
        allStrings.put("AddUser", "nibh");
        allStrings.put("DeleteUser", "sed ");
        allStrings.put("ChangeUpdateTime", "pretium");
        allStrings.put("ForceRecheckShows", "ante");
        allStrings.put("AddDirectory", "viverra");
        allStrings.put("RemoveDirectory", "fringilla");
        allStrings.put("OpenSettingsFolder", "Maecenas");
        allStrings.put("PrintAllShowInfo", "rhoncus ");
        allStrings.put("PrintAllDirectories", "rutrum");
        allStrings.put("PrintEmptyShows", "donec");
        allStrings.put("PrintIgnoredShows", "maximus");
        allStrings.put("PrintHiddenShows", "eros");
        allStrings.put("UnHideAll", "vitae");
        allStrings.put("SetAllActive", "commodo");
        allStrings.put("SetAllInactive", "ultrices");
        allStrings.put("PrintPSFV", "morbi");
        allStrings.put("PrintUSFV", "bibendum");
        allStrings.put("PrintAllUserInfo", "magna");
        allStrings.put("DirectoryVersionPlus1", "sagittis");
        allStrings.put("ClearFile", "vehicula");
        allStrings.put("ResetProgram", "enim");
        // Tooltip Text
        allStrings.put("ShowHideShowsWith0EpisodeLeft", "sollicitudin");
        allStrings.put("CurrentlyRechecking", "nisi");
        allStrings.put("DeleteUsersNoteCantDeleteCurrentUser", "Nulla");
        allStrings.put("WarningUnrecoverable", "vitae");
        // Other Text
        allStrings.put("AddNewUsername", "porta");
        allStrings.put("PleaseEnterUsername", "aliquet");
        allStrings.put("UseDefaultUsername", "volutpat");
        allStrings.put("DefaultUserNotSet", "malesuada");
        allStrings.put("PleaseChooseAFolder", "pellentesque");
        allStrings.put("PickTheEpisode", "habitant");
        allStrings.put("YouHaveToPickASeason", "morbi");
        allStrings.put("YouHaveToPickAEpisode", "tristique");
        allStrings.put("NextEpisode", "senectus");
        allStrings.put("UsernameIsntValid", "netus");
        allStrings.put("UsernameAlreadyTaken", "malesuada");
        allStrings.put("UsernameIsTooLong", "fames");
        allStrings.put("DirectoryIsAlreadyAdded", "turpis");
        allStrings.put("MustBeANumberGreaterThanOrEqualTo10", "egestas");
        allStrings.put("ChooseYourUsername", "Vivamus");
        allStrings.put("DirectoryWasADuplicate", "faucibus");
        allStrings.put("AddAnotherDirectory", "felis");
        allStrings.put("WhatShould", "lorem");
        allStrings.put("BeResetTo", "ullamcorper");
        allStrings.put("ShowIsResetToThe", "fringilla");
        allStrings.put("DoYouWantToOpenAllAssociatedFolders", "donec");
        allStrings.put("PickTheFolderYouWantToOpen", "varius");
        allStrings.put("NoDirectlyPrecedingEpisodesFound", "rutrum");
        allStrings.put("HaveYouWatchedTheShow", "donec");
        allStrings.put("AreYouSure", "condimentum");
        allStrings.put("PleaseChooseADefaultUser", "lacinia");
        allStrings.put("ThereAreNoOtherUsersToDelete", "praesent");
        allStrings.put("UserToDelete", "phasellus");
        allStrings.put("AreYouSureToWantToDelete", "pharetra");
        allStrings.put("EnterHowFastYouWantItToScanTheShowsFolders", "pulvinar");
        allStrings.put("LeaveItAsIs", "hendrerit");
        allStrings.put("PleaseEnterShowsDirectory", "fusce");
        allStrings.put("YouNeedToEnterADirectory", "quisque");
        allStrings.put("DirectoryIsInvalid", "tincidunt");
        allStrings.put("ThereAreNoDirectoriesToDelete", "suspendisse");
        allStrings.put("ThereAreNoDirectoriesToClear", "iaculis");
        allStrings.put("DirectoryToClear", "curabitur");
        allStrings.put("AreYouSureToWantToClear", "tincidunt");
        allStrings.put("AreYouSureThisWillDeleteEverything", "velit");
        allStrings.put("OpenChangesWindow", "lectus");
        allStrings.put("Dev1", "suscipit");
        allStrings.put("Dev2", "ipsum");
        allStrings.put("SetDefaultUser", "magnis");
        allStrings.put("DirectoryToDelete", "nascetur");
        allStrings.put("PleaseChooseYourLanguage", "hibuatu");
        allStrings.put("ChangeLanguage", "Gibula");
        allStrings.put("RestartTheProgramForTheNewLanguageToTakeEffect", "veralib");
    }

    public void setAllStrings() {
        log.info("Setting language to lipsum...");
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

    public String registerLanguage() {
        return allStrings.get("LanguageName");
    }
}

