package com.maddogten.mtrack.util;

import com.maddogten.mtrack.lang.Language;
import com.maddogten.mtrack.lang.en_US;
import com.maddogten.mtrack.lang.lipsum;
import com.maddogten.mtrack.lang.template;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/*
      LanguageHandler handles loaded the Languages, Settings them, and checking for missing strings.

      To add another language, Simply put it under LanguageHandler(), and add the appropriate file into "lang" using the template as the base.
 */

public class LanguageHandler {
    private final Logger log = Logger.getLogger(LanguageHandler.class.getName());

    private final Map<String, Language> languages = new HashMap<>();

    public LanguageHandler() {
        languages.put("en_US", new en_US());
        languages.put("lipsum", new lipsum()); // Only temporary, For demonstration purposes. Note- Remove
        languages.put("template", new template());  // Only temporary, For demonstration purposes. Note- Remove
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
            if (!name.matches("template"))
                addMissingTextForAllMissingStrings(); // Only temporary, For demonstration purposes. Note- Remove if statement
            return true;
        }
        return false;
    }

    // This verifies that all the Strings have been set, and if any haven't been, sets it as TextMissing. If TextMissing happens to be missing, it uses a default english string.
    private void addMissingTextForAllMissingStrings() {
        log.info("Checking for missing strings...");
        if (Strings.TextMissing == null || Strings.TextMissing.getValue() == null || Strings.TextMissing.getValue().isEmpty())
            Strings.TextMissing.setValue("Proper text is missing, Please report.");
        // Single Words
        findAndSetMissingStrings(Strings.Submit);
        findAndSetMissingStrings(Strings.Clear);
        findAndSetMissingStrings(Strings.Refresh);
        findAndSetMissingStrings(Strings.Close);
        findAndSetMissingStrings(Strings.Yes);
        findAndSetMissingStrings(Strings.No);
        findAndSetMissingStrings(Strings.Beginning);
        findAndSetMissingStrings(Strings.End);
        findAndSetMissingStrings(Strings.QuestionMark);
        findAndSetMissingStrings(Strings.Shows);
        findAndSetMissingStrings(Strings.Left);
        findAndSetMissingStrings(Strings.Settings);
        findAndSetMissingStrings(Strings.Options);
        findAndSetMissingStrings(Strings.Main);
        findAndSetMissingStrings(Strings.Users);
        findAndSetMissingStrings(Strings.Other);
        findAndSetMissingStrings(Strings.Developers);
        findAndSetMissingStrings(Strings.Reset);
        findAndSetMissingStrings(Strings.About);
        findAndSetMissingStrings(Strings.Season);
        findAndSetMissingStrings(Strings.Episode);
        findAndSetMissingStrings(Strings.Set);
        findAndSetMissingStrings(Strings.Warning);
        findAndSetMissingStrings(Strings.Show);
        findAndSetMissingStrings(Strings.UI);
        findAndSetMissingStrings(Strings.Cut);
        findAndSetMissingStrings(Strings.Copy);
        findAndSetMissingStrings(Strings.Paste);
        // Button Text
        findAndSetMissingStrings(Strings.SetSeasonEpisode);
        findAndSetMissingStrings(Strings.PlaySeasonEpisode);
        findAndSetMissingStrings(Strings.HideShow);
        findAndSetMissingStrings(Strings.ResetTo);
        findAndSetMissingStrings(Strings.OpenFileLocation);
        findAndSetMissingStrings(Strings.GetRemaining);
        findAndSetMissingStrings(Strings.PlayPreviousEpisode);
        findAndSetMissingStrings(Strings.SetInactive);
        findAndSetMissingStrings(Strings.SetActive);
        findAndSetMissingStrings(Strings.SwitchBetweenActiveInactiveList);
        findAndSetMissingStrings(Strings.AddUser);
        findAndSetMissingStrings(Strings.DeleteUser);
        findAndSetMissingStrings(Strings.ChangeUpdateTime);
        findAndSetMissingStrings(Strings.ForceRecheckShows);
        findAndSetMissingStrings(Strings.AddDirectory);
        findAndSetMissingStrings(Strings.RemoveDirectory);
        findAndSetMissingStrings(Strings.OpenSettingsFolder);
        findAndSetMissingStrings(Strings.PrintAllShowInfo);
        findAndSetMissingStrings(Strings.PrintAllDirectories);
        findAndSetMissingStrings(Strings.PrintEmptyShows);
        findAndSetMissingStrings(Strings.PrintIgnoredShows);
        findAndSetMissingStrings(Strings.PrintHiddenShows);
        findAndSetMissingStrings(Strings.UnHideAll);
        findAndSetMissingStrings(Strings.SetAllActive);
        findAndSetMissingStrings(Strings.SetAllInactive);
        findAndSetMissingStrings(Strings.PrintPsfvAndUsfv);
        findAndSetMissingStrings(Strings.PrintAllUserInfo);
        findAndSetMissingStrings(Strings.DirectoryVersionPlus1);
        findAndSetMissingStrings(Strings.ClearFile);
        findAndSetMissingStrings(Strings.ResetProgram);
        findAndSetMissingStrings(Strings.ToggleDevMode);
        findAndSetMissingStrings(Strings.InAppData);
        findAndSetMissingStrings(Strings.WithTheJar);
        findAndSetMissingStrings(Strings.UpdateTime);
        findAndSetMissingStrings(Strings.DirectoryTimeout);
        findAndSetMissingStrings(Strings.UnHideShow);
        findAndSetMissingStrings(Strings.OpenAll);
        findAndSetMissingStrings(Strings.OpenSelected);
        // Checkbox Text
        findAndSetMissingStrings(Strings.InactiveShows);
        findAndSetMissingStrings(Strings.OlderSeasons);
        findAndSetMissingStrings(Strings.AllowFullWindowMovementUse);
        findAndSetMissingStrings(Strings.DisableAutomaticShowSearching);
        // Tooltip Text
        findAndSetMissingStrings(Strings.ShowHiddenShowsWith0EpisodeLeft);
        findAndSetMissingStrings(Strings.CurrentlyRechecking);
        findAndSetMissingStrings(Strings.DeleteUsersNoteCantDeleteCurrentUser);
        findAndSetMissingStrings(Strings.WarningUnrecoverable);
        findAndSetMissingStrings(Strings.InternalVersion);
        findAndSetMissingStrings(Strings.MakeUserDefault);
        findAndSetMissingStrings(Strings.MakeLanguageDefault);
        // Other Text
        findAndSetMissingStrings(Strings.AddNewUsername);
        findAndSetMissingStrings(Strings.PleaseEnterUsername);
        findAndSetMissingStrings(Strings.UseDefaultUsername);
        findAndSetMissingStrings(Strings.DefaultUserNotSet);
        findAndSetMissingStrings(Strings.PleaseChooseAFolder);
        findAndSetMissingStrings(Strings.PickTheEpisode);
        findAndSetMissingStrings(Strings.YouHaveToPickASeason);
        findAndSetMissingStrings(Strings.YouHaveToPickAEpisode);
        findAndSetMissingStrings(Strings.NextEpisode);
        findAndSetMissingStrings(Strings.UsernameIsntValid);
        findAndSetMissingStrings(Strings.UsernameAlreadyTaken);
        findAndSetMissingStrings(Strings.UsernameIsTooLong);
        findAndSetMissingStrings(Strings.DirectoryIsAlreadyAdded);
        findAndSetMissingStrings(Strings.MustBeANumberBetween);
        findAndSetMissingStrings(Strings.ChooseYourUsername);
        findAndSetMissingStrings(Strings.DirectoryWasADuplicate);
        findAndSetMissingStrings(Strings.AddAnotherDirectory);
        findAndSetMissingStrings(Strings.WhatShould);
        findAndSetMissingStrings(Strings.BeResetTo);
        findAndSetMissingStrings(Strings.ShowIsResetToThe);
        findAndSetMissingStrings(Strings.DoYouWantToOpenAllAssociatedFolders);
        findAndSetMissingStrings(Strings.PickTheFolderYouWantToOpen);
        findAndSetMissingStrings(Strings.NoDirectlyPrecedingEpisodesFound);
        findAndSetMissingStrings(Strings.HaveYouWatchedTheShow);
        findAndSetMissingStrings(Strings.AreYouSure);
        findAndSetMissingStrings(Strings.PleaseChooseADefaultUser);
        findAndSetMissingStrings(Strings.ThereAreNoOtherUsersToDelete);
        findAndSetMissingStrings(Strings.UserToDelete);
        findAndSetMissingStrings(Strings.AreYouSureToWantToDelete);
        findAndSetMissingStrings(Strings.EnterHowFastYouWantItToScanTheShowsFolders);
        findAndSetMissingStrings(Strings.LeaveItAsIs);
        findAndSetMissingStrings(Strings.PleaseEnterShowsDirectory);
        findAndSetMissingStrings(Strings.YouNeedToEnterADirectory);
        findAndSetMissingStrings(Strings.DirectoryIsInvalid);
        findAndSetMissingStrings(Strings.ThereAreNoDirectoriesToDelete);
        findAndSetMissingStrings(Strings.ThereAreNoDirectoriesToClear);
        findAndSetMissingStrings(Strings.DirectoryToClear);
        findAndSetMissingStrings(Strings.AreYouSureToWantToClear);
        findAndSetMissingStrings(Strings.AreYouSureThisWillDeleteEverything);
        findAndSetMissingStrings(Strings.OpenChangesWindow);
        findAndSetMissingStrings(Strings.Dev1);
        findAndSetMissingStrings(Strings.Dev2);
        findAndSetMissingStrings(Strings.SetDefaultUser);
        findAndSetMissingStrings(Strings.DirectoryToDelete);
        findAndSetMissingStrings(Strings.PleaseChooseYourLanguage);
        findAndSetMissingStrings(Strings.ChangeLanguage);
        findAndSetMissingStrings(Strings.RestartTheProgramForTheNewLanguageToTakeEffect);
        findAndSetMissingStrings(Strings.WhereWouldYouLikeTheProgramFilesToBeStored);
        findAndSetMissingStrings(Strings.HoverOverAButtonForThePath);
        findAndSetMissingStrings(Strings.DashSeason);
        findAndSetMissingStrings(Strings.DashEpisode);
        findAndSetMissingStrings(Strings.PickTheSeasonAndEpisode);
        findAndSetMissingStrings(Strings.YouHaveReachedTheEnd);
        findAndSetMissingStrings(Strings.PingingDirectories);
        findAndSetMissingStrings(Strings.PathToDirectory);
        findAndSetMissingStrings(Strings.PrintCurrentSeasonEpisode);
        findAndSetMissingStrings(Strings.WasFoundToBeInactive);
        findAndSetMissingStrings(Strings.PleaseCorrectTheIssueThenForceRefresh);
        findAndSetMissingStrings(Strings.NotifyChangesFor);
        findAndSetMissingStrings(Strings.OnlyChecksEveryRuns);
        findAndSetMissingStrings(Strings.JavaVersionFull);
        findAndSetMissingStrings(Strings.CodedUsingFull);
        findAndSetMissingStrings(Strings.ThereAreNoHiddenShows);
        findAndSetMissingStrings(Strings.PickShowToUnHide);

        log.info("Finished checking for missing strings.");
    }

    private void findAndSetMissingStrings(StringProperty stringToCheck) {
        if (stringToCheck.getValue() == null && stringToCheck.getValue().isEmpty()) {
            stringToCheck.setValue(Strings.TextMissing.getValue());
        }
    }
}
