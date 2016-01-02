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
        Strings.TextMissing.setValue("");

        // Single Words
        Strings.Submit.setValue("");
        Strings.Clear.setValue("");
        Strings.Refresh.setValue("");
        Strings.Close.setValue("");
        Strings.Yes.setValue("");
        Strings.No.setValue("");
        Strings.Beginning.setValue("");
        Strings.End.setValue("");
        Strings.QuestionMark.setValue("");
        Strings.Shows.setValue(Strings.EmptyString);
        Strings.Left.setValue("");
        Strings.Settings.setValue("");
        Strings.Options.setValue("");
        Strings.Main.setValue("");
        Strings.Users.setValue("");
        Strings.Other.setValue("");
        Strings.Developers.setValue("");
        Strings.Reset.setValue("");
        Strings.About.setValue("");
        Strings.Season.setValue("");
        Strings.Episode.setValue("");
        Strings.Set.setValue("");
        Strings.Warning.setValue("");
        Strings.Show.setValue("");
        Strings.UI.setValue("");
        Strings.Cut.setValue("");
        Strings.Copy.setValue("");
        Strings.Paste.setValue("");

        // Button Text
        Strings.SetSeasonEpisode.setValue("");
        Strings.PlaySeasonEpisode.setValue("");
        Strings.HideShow.setValue("");
        Strings.ResetTo.setValue("");
        Strings.OpenFileLocation.setValue("");
        Strings.GetRemaining.setValue("");
        Strings.PlayPreviousEpisode.setValue("");
        Strings.SetInactive.setValue("");
        Strings.SetActive.setValue("");
        Strings.SwitchBetweenActiveInactiveList.setValue("");
        Strings.AddUser.setValue("");
        Strings.DeleteUser.setValue("");
        Strings.ChangeUpdateTime.setValue("");
        Strings.ForceRecheckShows.setValue("");
        Strings.AddDirectory.setValue("");
        Strings.RemoveDirectory.setValue("");
        Strings.OpenSettingsFolder.setValue("");
        Strings.PrintAllShowInfo.setValue("");
        Strings.PrintAllDirectories.setValue("");
        Strings.PrintEmptyShows.setValue("");
        Strings.PrintIgnoredShows.setValue("");
        Strings.PrintHiddenShows.setValue("");
        Strings.UnHideAll.setValue("");
        Strings.SetAllActive.setValue("");
        Strings.SetAllInactive.setValue("");
        Strings.PrintPSFV.setValue("");
        Strings.PrintUSFV.setValue("");
        Strings.PrintAllUserInfo.setValue("");
        Strings.DirectoryVersionPlus1.setValue("");
        Strings.ClearFile.setValue("");
        Strings.ResetProgram.setValue("");
        Strings.ToggleDevMode.setValue("");
        Strings.InAppData.setValue("");
        Strings.WithTheJar.setValue("");
        Strings.UpdateTime.setValue("");
        Strings.DirectoryTimeout.setValue("");
        Strings.UnHideShow.setValue("");
        Strings.OpenAll.setValue("");
        Strings.OpenSelected.setValue("");

        // CheckBox Text
        Strings.InactiveShows.setValue("");
        Strings.OlderSeasons.setValue("");
        Strings.AllowFullWindowMovementUse.setValue("");
        Strings.DisableAutomaticShowSearching.setValue("");

        // Tooltip Text
        Strings.ShowHiddenShowsWith0EpisodeLeft.setValue("");
        Strings.CurrentlyRechecking.setValue("");
        Strings.DeleteUsersNoteCantDeleteCurrentUser.setValue("");
        Strings.WarningUnrecoverable.setValue("");
        Strings.InternalVersion.setValue("");
        Strings.MakeUserDefault.setValue("");
        Strings.MakeLanguageDefault.setValue("");

        // Other Text
        Strings.AddNewUsername.setValue("");
        Strings.PleaseEnterUsername.setValue("");
        Strings.UseDefaultUsername.setValue("");
        Strings.DefaultUserNotSet.setValue("");
        Strings.PleaseChooseAFolder.setValue("");
        Strings.PickTheEpisode.setValue("");
        Strings.YouHaveToPickASeason.setValue("");
        Strings.YouHaveToPickAEpisode.setValue("");
        Strings.NextEpisode.setValue("");
        Strings.UsernameIsntValid.setValue("");
        Strings.UsernameAlreadyTaken.setValue("");
        Strings.UsernameIsTooLong.setValue("");
        Strings.DirectoryIsAlreadyAdded.setValue("");
        Strings.MustBeANumberBetween.setValue("");
        Strings.ChooseYourUsername.setValue("");
        Strings.DirectoryWasADuplicate.setValue("");
        Strings.AddAnotherDirectory.setValue("");
        Strings.WhatShould.setValue("");
        Strings.BeResetTo.setValue("");
        Strings.ShowIsResetToThe.setValue("");
        Strings.DoYouWantToOpenAllAssociatedFolders.setValue("");
        Strings.PickTheFolderYouWantToOpen.setValue("");
        Strings.NoDirectlyPrecedingEpisodesFound.setValue("");
        Strings.HaveYouWatchedTheShow.setValue("");
        Strings.AreYouSure.setValue("");
        Strings.PleaseChooseADefaultUser.setValue("");
        Strings.ThereAreNoOtherUsersToDelete.setValue("");
        Strings.UserToDelete.setValue("");
        Strings.AreYouSureToWantToDelete.setValue("");
        Strings.EnterHowFastYouWantItToScanTheShowsFolders.setValue("");
        Strings.LeaveItAsIs.setValue("");
        Strings.PleaseEnterShowsDirectory.setValue("");
        Strings.YouNeedToEnterADirectory.setValue("");
        Strings.DirectoryIsInvalid.setValue("");
        Strings.ThereAreNoDirectoriesToDelete.setValue("");
        Strings.ThereAreNoDirectoriesToClear.setValue("");
        Strings.DirectoryToClear.setValue("");
        Strings.AreYouSureToWantToClear.setValue("");
        Strings.AreYouSureThisWillDeleteEverything.setValue("");
        Strings.OpenChangesWindow.setValue("");
        Strings.Dev1.setValue("");
        Strings.Dev2.setValue("");
        Strings.SetDefaultUser.setValue("");
        Strings.DirectoryToDelete.setValue("");
        Strings.PleaseChooseYourLanguage.setValue("");
        Strings.ChangeLanguage.setValue("");
        Strings.RestartTheProgramForTheNewLanguageToTakeEffect.setValue("");
        Strings.WhereWouldYouLikeTheProgramFilesToBeStored.setValue("");
        Strings.HoverOverAButtonForThePath.setValue("");
        Strings.DashSeason.setValue("");
        Strings.DashEpisode.setValue("");
        Strings.PickTheSeasonAndEpisode.setValue("");
        Strings.YouHaveReachedTheEnd.setValue("");
        Strings.PingingDirectories.setValue("");
        Strings.PathToDirectory.setValue("");
        Strings.WasFoundToBeInactive.setValue("");
        Strings.PleaseCorrectTheIssueThenForceRefresh.setValue("");
        Strings.NotifyChangesFor.setValue("");
        Strings.OnlyChecksEveryRuns.setValue("");
        Strings.JavaVersionFull.setValue("");
        Strings.CodedUsingFull.setValue("");
        Strings.ThereAreNoHiddenShows.setValue("");
        Strings.PickShowToUnHide.setValue("");
    }
}
