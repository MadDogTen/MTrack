package program.lang;

import java.util.logging.Logger;

@SuppressWarnings("SpellCheckingInspection")
public class lipsum extends Language {
    private final Logger log = Logger.getLogger(lipsum.class.getName());

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
        allStrings.put("Season", "vertamis");
        allStrings.put("Episode", "medutu");

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
}