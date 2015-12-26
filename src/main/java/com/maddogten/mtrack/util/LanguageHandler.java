package com.maddogten.mtrack.util;

import com.maddogten.mtrack.lang.Language;
import com.maddogten.mtrack.lang.en_US;
import com.maddogten.mtrack.lang.lipsum;
import com.maddogten.mtrack.lang.template;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LanguageHandler {
    // This file is a temporary solution, and will be changing.
    private final Logger log = Logger.getLogger(LanguageHandler.class.getName());

    private final Map<String, Language> languages = new HashMap<>();

    public LanguageHandler() {
        languages.put("en_US", new en_US());
        languages.put("lipsum", new lipsum()); // Only temporary, For demonstration purposes. TODO <- Eventually Remove
        languages.put("template", new template());  // Only temporary, For demonstration purposes. TODO <- Eventually Remove
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
            if (!name.matches("template")) { // Only temporary, For demonstration purposes. TODO <- Eventually Remove
                addMissingTextForAllMissingStrings();
            }
            return true;
        }
        return false;
    }

    // This verifies that all the Strings have been set, and if any haven't been, sets it as TextMissing. If TextMissing happens to be missing, it uses a default english string.
    private void addMissingTextForAllMissingStrings() {
        log.info("Checking for missing strings...");
        if (isStringMissing(Strings.TextMissing))
            Strings.TextMissing.setValue("Proper text is missing, Please report.");

        if (isStringMissing(Strings.Submit)) Strings.Submit.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Clear)) Strings.Clear.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Refresh)) Strings.Refresh.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Close)) Strings.Close.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Yes)) Strings.Yes.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.No)) Strings.No.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Beginning)) Strings.Beginning.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.End)) Strings.End.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.QuestionMark)) Strings.QuestionMark.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Shows)) Strings.Shows.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Left)) Strings.Left.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Settings)) Strings.Settings.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Options)) Strings.Options.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Main)) Strings.Main.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Users)) Strings.Users.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Other)) Strings.Other.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Developers)) Strings.Developers.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Reset)) Strings.Reset.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.About)) Strings.About.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Season)) Strings.Season.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Episode)) Strings.Episode.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Set)) Strings.Set.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Warning)) Strings.Warning.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.SetSeasonEpisode))
            Strings.SetSeasonEpisode.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PlaySeasonEpisode))
            Strings.PlaySeasonEpisode.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.HideShow)) Strings.HideShow.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ResetTo)) Strings.ResetTo.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.OpenFileLocation))
            Strings.OpenFileLocation.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.GetRemaining)) Strings.GetRemaining.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PlayPreviousEpisode))
            Strings.PlayPreviousEpisode.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.SetInactive)) Strings.SetInactive.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.SetActive)) Strings.SetActive.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.SwitchBetweenActiveInactiveList))
            Strings.SwitchBetweenActiveInactiveList.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.AddUser)) Strings.AddUser.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DeleteUser)) Strings.DeleteUser.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ChangeUpdateTime))
            Strings.ChangeUpdateTime.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ForceRecheckShows))
            Strings.ForceRecheckShows.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.AddDirectory)) Strings.AddDirectory.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.RemoveDirectory)) Strings.RemoveDirectory.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.OpenSettingsFolder))
            Strings.OpenSettingsFolder.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PrintAllShowInfo))
            Strings.PrintAllShowInfo.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PrintAllDirectories))
            Strings.PrintAllDirectories.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PrintEmptyShows)) Strings.PrintEmptyShows.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PrintIgnoredShows))
            Strings.PrintIgnoredShows.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PrintHiddenShows))
            Strings.PrintHiddenShows.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.UnHideAll)) Strings.UnHideAll.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.SetAllActive)) Strings.SetAllActive.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.SetAllInactive)) Strings.SetAllInactive.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PrintPSFV)) Strings.PrintPSFV.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PrintUSFV)) Strings.PrintUSFV.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PrintAllUserInfo))
            Strings.PrintAllUserInfo.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DirectoryVersionPlus1))
            Strings.DirectoryVersionPlus1.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ClearFile)) Strings.ClearFile.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ResetProgram)) Strings.ResetProgram.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ToggleDevMode)) Strings.ToggleDevMode.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.InAppData)) Strings.InAppData.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.WithTheJar)) Strings.WithTheJar.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.UpdateTime)) Strings.UpdateTime.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DirectoryTimeout))
            Strings.DirectoryTimeout.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ShowHiddenShowsWith0EpisodeLeft))
            Strings.ShowHiddenShowsWith0EpisodeLeft.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.CurrentlyRechecking))
            Strings.CurrentlyRechecking.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DeleteUsersNoteCantDeleteCurrentUser))
            Strings.DeleteUsersNoteCantDeleteCurrentUser.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.WarningUnrecoverable))
            Strings.WarningUnrecoverable.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.AddNewUsername)) Strings.AddNewUsername.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PleaseEnterUsername))
            Strings.PleaseEnterUsername.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.UseDefaultUsername))
            Strings.UseDefaultUsername.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DefaultUserNotSet))
            Strings.DefaultUserNotSet.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PleaseChooseAFolder))
            Strings.PleaseChooseAFolder.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PickTheEpisode)) Strings.PickTheEpisode.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.YouHaveToPickASeason))
            Strings.YouHaveToPickASeason.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.YouHaveToPickAEpisode))
            Strings.YouHaveToPickAEpisode.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.NextEpisode)) Strings.NextEpisode.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.UsernameIsntValid))
            Strings.UsernameIsntValid.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.UsernameAlreadyTaken))
            Strings.UsernameAlreadyTaken.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.UsernameIsTooLong))
            Strings.UsernameIsTooLong.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DirectoryIsAlreadyAdded))
            Strings.DirectoryIsAlreadyAdded.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.MustBeANumberBetween))
            Strings.MustBeANumberBetween.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ChooseYourUsername))
            Strings.ChooseYourUsername.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DirectoryWasADuplicate))
            Strings.DirectoryWasADuplicate.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.AddAnotherDirectory))
            Strings.AddAnotherDirectory.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.WhatShould)) Strings.WhatShould.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.BeResetTo)) Strings.BeResetTo.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ShowIsResetToThe))
            Strings.ShowIsResetToThe.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DoYouWantToOpenAllAssociatedFolders))
            Strings.DoYouWantToOpenAllAssociatedFolders.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PickTheFolderYouWantToOpen))
            Strings.PickTheFolderYouWantToOpen.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.NoDirectlyPrecedingEpisodesFound))
            Strings.NoDirectlyPrecedingEpisodesFound.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.HaveYouWatchedTheShow))
            Strings.HaveYouWatchedTheShow.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.AreYouSure)) Strings.AreYouSure.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PleaseChooseADefaultUser))
            Strings.PleaseChooseADefaultUser.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ThereAreNoOtherUsersToDelete))
            Strings.ThereAreNoOtherUsersToDelete.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.UserToDelete)) Strings.UserToDelete.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.AreYouSureToWantToDelete))
            Strings.AreYouSureToWantToDelete.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.EnterHowFastYouWantItToScanTheShowsFolders))
            Strings.EnterHowFastYouWantItToScanTheShowsFolders.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.LeaveItAsIs)) Strings.LeaveItAsIs.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PleaseEnterShowsDirectory))
            Strings.PleaseEnterShowsDirectory.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.YouNeedToEnterADirectory))
            Strings.YouNeedToEnterADirectory.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DirectoryIsInvalid))
            Strings.DirectoryIsInvalid.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ThereAreNoDirectoriesToDelete))
            Strings.ThereAreNoDirectoriesToDelete.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ThereAreNoDirectoriesToClear))
            Strings.ThereAreNoDirectoriesToClear.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DirectoryToClear))
            Strings.DirectoryToClear.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.AreYouSureToWantToClear))
            Strings.AreYouSureToWantToClear.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.AreYouSureThisWillDeleteEverything))
            Strings.AreYouSureThisWillDeleteEverything.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.OpenChangesWindow))
            Strings.OpenChangesWindow.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Dev1)) Strings.Dev1.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Dev2)) Strings.Dev2.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.SetDefaultUser)) Strings.SetDefaultUser.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DirectoryToDelete))
            Strings.DirectoryToDelete.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PleaseChooseYourLanguage))
            Strings.PleaseChooseYourLanguage.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.ChangeLanguage)) Strings.ChangeLanguage.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.RestartTheProgramForTheNewLanguageToTakeEffect))
            Strings.RestartTheProgramForTheNewLanguageToTakeEffect.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.WhereWouldYouLikeTheProgramFilesToBeStored))
            Strings.WhereWouldYouLikeTheProgramFilesToBeStored.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.HoverOverAButtonForThePath))
            Strings.HoverOverAButtonForThePath.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DashSeason)) Strings.DashSeason.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DashEpisode)) Strings.DashEpisode.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PickTheSeasonAndEpisode))
            Strings.PickTheSeasonAndEpisode.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.YouHaveReachedTheEnd))
            Strings.YouHaveReachedTheEnd.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PingingDirectories))
            Strings.PingingDirectories.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PathToDirectory)) Strings.PathToDirectory.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PrintCurrentSeasonEpisode))
            Strings.PrintCurrentSeasonEpisode.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.WasFoundToBeInactive))
            Strings.WasFoundToBeInactive.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.PleaseCorrectTheIssueThenForceRefresh))
            Strings.PleaseCorrectTheIssueThenForceRefresh.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.InternalVersion)) Strings.InternalVersion.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.InactiveShows)) Strings.InactiveShows.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.OlderSeasons)) Strings.OlderSeasons.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.NotifyChangesFor))
            Strings.NotifyChangesFor.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.OnlyChecksEveryRuns))
            Strings.OnlyChecksEveryRuns.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.AllowFullWindowMovementUse))
            Strings.AllowFullWindowMovementUse.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.DisableAutomaticShowSearching))
            Strings.DisableAutomaticShowSearching.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.Show)) Strings.Show.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.UI)) Strings.UI.setValue(Strings.TextMissing.getValue());
        if (isStringMissing(Strings.UnHideShow)) Strings.UnHideShow.setValue(Strings.TextMissing.getValue());
        log.info("Finished checking for missing strings.");
    }

    private boolean isStringMissing(StringProperty stringToCheck) {
        return stringToCheck == null || stringToCheck.getValue() == null || stringToCheck.getValue().isEmpty();
    }
}
