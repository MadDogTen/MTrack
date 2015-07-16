package program.lang;

import java.util.logging.Logger;

@SuppressWarnings("unused")
public class template extends Language {
    private final Logger log = Logger.getLogger(template.class.getName());

    public template() {
        log.info("Loading template...");

        // All Other Text
        allStrings.put("LanguageName", "Blank Template");
        allStrings.put("DefaultUsername", "");
        allStrings.put("TextMissing", "");

        // Single Words
        allStrings.put("Submit", "");
        allStrings.put("Clear", "");
        allStrings.put("Refresh", "");
        allStrings.put("Close", "");
        allStrings.put("Yes", "");
        allStrings.put("No", "");
        allStrings.put("Beginning", "");
        allStrings.put("End", "");
        allStrings.put("QuestionMark", "");
        allStrings.put("Shows", "");
        allStrings.put("Left", "");
        allStrings.put("Settings", "");
        allStrings.put("Options", "");
        allStrings.put("Main", "");
        allStrings.put("Users", "");
        allStrings.put("Other", "");
        allStrings.put("Developers", "");
        allStrings.put("Reset", "");
        allStrings.put("About", "");
        allStrings.put("Season", "");
        allStrings.put("Episode", "");

        // Button Text
        allStrings.put("SetSeasonEpisode", "");
        allStrings.put("PlaySeasonEpisode", "");
        allStrings.put("HideShow", "");
        allStrings.put("ResetTo", "");
        allStrings.put("OpenFileLocation", "");
        allStrings.put("GetRemaining", "");
        allStrings.put("PlayPreviousEpisode", "");
        allStrings.put("SetInactive", "");
        allStrings.put("SetActive", "");
        allStrings.put("SwitchBetweenActiveInactiveList", "");
        allStrings.put("AddUser", "");
        allStrings.put("DeleteUser", "");
        allStrings.put("ChangeUpdateTime", "");
        allStrings.put("ForceRecheckShows", "");
        allStrings.put("AddDirectory", "");
        allStrings.put("RemoveDirectory", "");
        allStrings.put("OpenSettingsFolder", "");
        allStrings.put("PrintAllShowInfo", "");
        allStrings.put("PrintAllDirectories", "");
        allStrings.put("PrintEmptyShows", "");
        allStrings.put("PrintIgnoredShows", "");
        allStrings.put("PrintHiddenShows", "");
        allStrings.put("UnHideAll", "");
        allStrings.put("SetAllActive", "");
        allStrings.put("SetAllInactive", "");
        allStrings.put("PrintPSFV", "");
        allStrings.put("PrintUSFV", "");
        allStrings.put("PrintAllUserInfo", "");
        allStrings.put("DirectoryVersionPlus1", "");
        allStrings.put("ClearFile", "");
        allStrings.put("ResetProgram", "");

        // Tooltip Text
        allStrings.put("ShowHideShowsWith0EpisodeLeft", "");
        allStrings.put("CurrentlyRechecking", "");
        allStrings.put("DeleteUsersNoteCantDeleteCurrentUser", "");
        allStrings.put("WarningUnrecoverable", "");

        // Other Text
        allStrings.put("AddNewUsername", "");
        allStrings.put("PleaseEnterUsername", "");
        allStrings.put("UseDefaultUsername", "");
        allStrings.put("DefaultUserNotSet", "");
        allStrings.put("PleaseChooseAFolder", "");
        allStrings.put("PickTheEpisode", "");
        allStrings.put("YouHaveToPickASeason", "");
        allStrings.put("YouHaveToPickAEpisode", "");
        allStrings.put("NextEpisode", "");
        allStrings.put("UsernameIsntValid", "");
        allStrings.put("UsernameAlreadyTaken", "");
        allStrings.put("UsernameIsTooLong", "");
        allStrings.put("DirectoryIsAlreadyAdded", "");
        allStrings.put("MustBeANumberGreaterThanOrEqualTo10", "");
        allStrings.put("ChooseYourUsername", "");
        allStrings.put("DirectoryWasADuplicate", "");
        allStrings.put("AddAnotherDirectory", "");
        allStrings.put("WhatShould", "");
        allStrings.put("BeResetTo", "");
        allStrings.put("ShowIsResetToThe", "");
        allStrings.put("DoYouWantToOpenAllAssociatedFolders", "");
        allStrings.put("PickTheFolderYouWantToOpen", "");
        allStrings.put("NoDirectlyPrecedingEpisodesFound", "");
        allStrings.put("HaveYouWatchedTheShow", "");
        allStrings.put("AreYouSure", "");
        allStrings.put("PleaseChooseADefaultUser", "");
        allStrings.put("ThereAreNoOtherUsersToDelete", "");
        allStrings.put("UserToDelete", "");
        allStrings.put("AreYouSureToWantToDelete", "");
        allStrings.put("EnterHowFastYouWantItToScanTheShowsFolders", "");
        allStrings.put("LeaveItAsIs", "");
        allStrings.put("PleaseEnterShowsDirectory", "");
        allStrings.put("YouNeedToEnterADirectory", "");
        allStrings.put("DirectoryIsInvalid", "");
        allStrings.put("ThereAreNoDirectoriesToDelete", "");
        allStrings.put("ThereAreNoDirectoriesToClear", "");
        allStrings.put("DirectoryToClear", "");
        allStrings.put("AreYouSureToWantToClear", "");
        allStrings.put("AreYouSureThisWillDeleteEverything", "");
        allStrings.put("OpenChangesWindow", "");
        allStrings.put("Dev1", "");
        allStrings.put("Dev2", "");
        allStrings.put("SetDefaultUser", "");
        allStrings.put("DirectoryToDelete", "");
        allStrings.put("PleaseChooseYourLanguage", "");
        allStrings.put("ChangeLanguage", "");
        allStrings.put("RestartTheProgramForTheNewLanguageToTakeEffect", "");
    }
}