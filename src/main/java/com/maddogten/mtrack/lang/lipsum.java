package com.maddogten.mtrack.lang;

import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.util.logging.Logger;

@SuppressWarnings({"SpellCheckingInspection", "unused", "WeakerAccess"})
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
        Strings.TextMissing.setValue("manduris, deliour hiberious.");

        // Single Words
        Strings.Submit.setValue("galatirm");
        Strings.Clear.setValue("tortor");
        Strings.Refresh.setValue("pulvinar");
        Strings.Close.setValue("iaculis");
        Strings.Yes.setValue("dapibus");
        Strings.No.setValue("leo");
        Strings.Beginning.setValue("libero");
        Strings.End.setValue("volutpat");
        Strings.QuestionMark.setValue("libero");
        Strings.Shows.setValue("vitaes");
        Strings.Left.setValue("dapibus");
        Strings.Settings.setValue("nulla");
        Strings.Options.setValue("diam ");
        Strings.Main.setValue("lacus");
        Strings.Users.setValue("maecenas");
        Strings.Other.setValue("accumsan");
        Strings.Developers.setValue("fermentum");
        Strings.Reset.setValue("massa");
        Strings.About.setValue("eget");
        Strings.Season.setValue("vertamis");
        Strings.Episode.setValue("medutu");
        Strings.Set.setValue("hegu");
        Strings.Warning.setValue("murta");
        Strings.Show.setValue("vitae");
        Strings.UI.setValue("gula");
        Strings.Cut.setValue("tu");
        Strings.Copy.setValue("ta");
        Strings.Paste.setValue("te");

        // Button Text
        Strings.SetSeasonEpisode.setValue("malesuada");
        Strings.PlaySeasonEpisode.setValue("vestibulum");
        Strings.HideShow.setValue("nisl");
        Strings.ResetTo.setValue("risus");
        Strings.OpenFileLocation.setValue("tempus");
        Strings.GetRemaining.setValue("lobortis");
        Strings.PlayPreviousEpisode.setValue("eget");
        Strings.SetInactive.setValue("ante");
        Strings.SetActive.setValue("maecenas");
        Strings.SwitchBetweenActiveInactiveList.setValue("porta");
        Strings.AddUser.setValue("nibh");
        Strings.DeleteUser.setValue("sed ");
        Strings.ChangeUpdateTime.setValue("pretium");
        Strings.ForceRecheckShows.setValue("ante");
        Strings.AddDirectory.setValue("viverra");
        Strings.RemoveDirectory.setValue("fringilla");
        Strings.OpenSettingsFolder.setValue("Maecenas");
        Strings.PrintAllShowInfo.setValue("rhoncus ");
        Strings.PrintAllDirectories.setValue("rutrum");
        Strings.PrintEmptyShows.setValue("donec");
        Strings.PrintIgnoredShows.setValue("maximus");
        Strings.PrintHiddenShows.setValue("eros");
        Strings.UnHideAll.setValue("vitae");
        Strings.SetAllActive.setValue("commodo");
        Strings.SetAllInactive.setValue("ultrices");
        Strings.PrintPSFV.setValue("morbi");
        Strings.PrintUSFV.setValue("bibendum");
        Strings.PrintAllUserInfo.setValue("magna");
        Strings.DirectoryVersionPlus1.setValue("sagittis");
        Strings.ClearFile.setValue("vehicula");
        Strings.ResetProgram.setValue("enim");
        Strings.ToggleDevMode.setValue("gulamina");
        Strings.InAppData.setValue("burgula");
        Strings.WithTheJar.setValue("zemberna");
        Strings.UpdateTime.setValue("nunula");
        Strings.DirectoryTimeout.setValue("termula");
        Strings.UnHideShow.setValue("mutalu");
        Strings.OpenAll.setValue("tarimi");
        Strings.OpenSelected.setValue("dorire");

        // CheckBox Text
        Strings.InactiveShows.setValue("miterra");
        Strings.OlderSeasons.setValue("avartu");
        Strings.AllowFullWindowMovementUse.setValue("hula");
        Strings.DisableAutomaticShowSearching.setValue("nomano");

        // Tooltip Text
        Strings.ShowHiddenShowsWith0EpisodeLeft.setValue("sollicitudin");
        Strings.CurrentlyRechecking.setValue("nisi");
        Strings.DeleteUsersNoteCantDeleteCurrentUser.setValue("Nulla");
        Strings.WarningUnrecoverable.setValue("vitae");
        Strings.InternalVersion.setValue("gataluma: " + Variables.InternalVersion);
        Strings.MakeUserDefault.setValue("tertavu");
        Strings.MakeLanguageDefault.setValue("mitaka");

        // Other Text
        Strings.AddNewUsername.setValue("porta");
        Strings.PleaseEnterUsername.setValue("aliquet");
        Strings.UseDefaultUsername.setValue("volutpat");
        Strings.DefaultUserNotSet.setValue("malesuada");
        Strings.PleaseChooseAFolder.setValue("pellentesque");
        Strings.PickTheEpisode.setValue("habitant");
        Strings.YouHaveToPickASeason.setValue("morbi");
        Strings.YouHaveToPickAEpisode.setValue("tristique");
        Strings.NextEpisode.setValue("senectus");
        Strings.UsernameIsntValid.setValue("netus");
        Strings.UsernameAlreadyTaken.setValue("malesuada");
        Strings.UsernameIsTooLong.setValue("fames");
        Strings.DirectoryIsAlreadyAdded.setValue("turpis");
        Strings.MustBeANumberBetween.setValue("egestas");
        Strings.ChooseYourUsername.setValue("Vivamus");
        Strings.DirectoryWasADuplicate.setValue("faucibus");
        Strings.AddAnotherDirectory.setValue("felis");
        Strings.WhatShould.setValue("lorem");
        Strings.BeResetTo.setValue("ullamcorper");
        Strings.ShowIsResetToThe.setValue("fringilla");
        Strings.DoYouWantToOpenAllAssociatedFolders.setValue("donec");
        Strings.PickTheFolderYouWantToOpen.setValue("varius");
        Strings.NoDirectlyPrecedingEpisodesFound.setValue("rutrum");
        Strings.HaveYouWatchedTheShow.setValue("donec");
        Strings.AreYouSure.setValue("condimentum");
        Strings.PleaseChooseADefaultUser.setValue("lacinia");
        Strings.ThereAreNoOtherUsersToDelete.setValue("praesent");
        Strings.UserToDelete.setValue("phasellus");
        Strings.AreYouSureToWantToDelete.setValue("pharetra");
        Strings.EnterHowFastYouWantItToScanTheShowsFolders.setValue("pulvinar");
        Strings.LeaveItAsIs.setValue("hendrerit");
        Strings.PleaseEnterShowsDirectory.setValue("fusce");
        Strings.YouNeedToEnterADirectory.setValue("quisque");
        Strings.DirectoryIsInvalid.setValue("tincidunt");
        Strings.ThereAreNoDirectoriesToDelete.setValue("suspendisse");
        Strings.ThereAreNoDirectoriesToClear.setValue("iaculis");
        Strings.DirectoryToClear.setValue("curabitur");
        Strings.AreYouSureToWantToClear.setValue("tincidunt");
        Strings.AreYouSureThisWillDeleteEverything.setValue("velit");
        Strings.OpenChangesWindow.setValue("lectus");
        Strings.Dev1.setValue("suscipit");
        Strings.Dev2.setValue("ipsum");
        Strings.SetDefaultUser.setValue("magnis");
        Strings.DirectoryToDelete.setValue("nascetur");
        Strings.PleaseChooseYourLanguage.setValue("hibuatu");
        Strings.ChangeLanguage.setValue("Gibula");
        Strings.RestartTheProgramForTheNewLanguageToTakeEffect.setValue("veralib");
        Strings.WhereWouldYouLikeTheProgramFilesToBeStored.setValue("abbera");
        Strings.HoverOverAButtonForThePath.setValue("mezua");
        Strings.DashSeason.setValue(" - megatu");
        Strings.DashEpisode.setValue(" - jesuma");
        Strings.PickTheSeasonAndEpisode.setValue("vertuta");
        Strings.YouHaveReachedTheEnd.setValue("galaminka");
        Strings.PingingDirectories.setValue("vermuna");
        Strings.PathToDirectory.setValue("mezera");
        Strings.WasFoundToBeInactive.setValue("galamru");
        Strings.PleaseCorrectTheIssueThenForceRefresh.setValue("altez");
        Strings.NotifyChangesFor.setValue("iternu");
        Strings.OnlyChecksEveryRuns.setValue("viter");
        Strings.JavaVersionFull.setValue("mutulu: " + Strings.JavaVersion);
        Strings.CodedUsingFull.setValue("mecala: " + Strings.CodedUsing);
        Strings.ThereAreNoHiddenShows.setValue("utala");
        Strings.PickShowToUnHide.setValue("hulva");
    }
}