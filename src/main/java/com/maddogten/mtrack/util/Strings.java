package com.maddogten.mtrack.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;

/*
      Strings stores all the strings the program uses. ALL Strings that aren't for logging should be in here.
 */

@SuppressWarnings("ClassWithoutLogger")
public class Strings {
    // Program Info
    public static final StringProperty ProgramTitle = new SimpleStringProperty("MTrack");
    //---------- Main Strings ----------\\
    public static final StringProperty MTrackVersion = new SimpleStringProperty("Alpha 1.0.0");
    public static final StringProperty CodedBy = new SimpleStringProperty("MadDogTen");
    public static final StringProperty CodedUsing = new SimpleStringProperty("Intellij IDEA 15.0.2");
    public static final StringProperty CodedIn = new SimpleStringProperty("Java");
    public static final StringProperty JavaVersion = new SimpleStringProperty("1.8.0_60");
    //Other
    public static final String SettingsFileName = "Program";
    // Other
    public static final String EmptyString = "";
    public static final String FileSeparator = File.separator;
    @SuppressWarnings("unused")
    public static final StringProperty TextMissing = new SimpleStringProperty();
    // Single Words
    public static final StringProperty Submit = new SimpleStringProperty();
    public static final StringProperty Clear = new SimpleStringProperty();
    public static final StringProperty Refresh = new SimpleStringProperty();
    public static final StringProperty Close = new SimpleStringProperty();
    public static final StringProperty Yes = new SimpleStringProperty();
    public static final StringProperty No = new SimpleStringProperty();
    public static final StringProperty Beginning = new SimpleStringProperty();
    public static final StringProperty End = new SimpleStringProperty();
    public static final StringProperty QuestionMark = new SimpleStringProperty();
    public static final StringProperty Shows = new SimpleStringProperty();
    public static final StringProperty Left = new SimpleStringProperty();
    public static final StringProperty Settings = new SimpleStringProperty();
    public static final StringProperty Options = new SimpleStringProperty();
    public static final StringProperty Main = new SimpleStringProperty();
    public static final StringProperty Users = new SimpleStringProperty();
    public static final StringProperty Other = new SimpleStringProperty();
    public static final StringProperty Developers = new SimpleStringProperty();
    public static final StringProperty Reset = new SimpleStringProperty();
    public static final StringProperty About = new SimpleStringProperty();
    public static final StringProperty Season = new SimpleStringProperty();
    public static final StringProperty Episode = new SimpleStringProperty();
    public static final StringProperty Set = new SimpleStringProperty();
    public static final StringProperty Warning = new SimpleStringProperty();
    public static final StringProperty Show = new SimpleStringProperty();
    public static final StringProperty UI = new SimpleStringProperty();
    public static final StringProperty Cut = new SimpleStringProperty();
    public static final StringProperty Copy = new SimpleStringProperty();
    public static final StringProperty Paste = new SimpleStringProperty();
    public static final StringProperty All = new SimpleStringProperty();
    public static final StringProperty Program = new SimpleStringProperty();
    public static final StringProperty Directories = new SimpleStringProperty();
    // Button Text
    public static final StringProperty SetSeasonEpisode = new SimpleStringProperty();
    public static final StringProperty PlaySeasonEpisode = new SimpleStringProperty();
    public static final StringProperty HideShow = new SimpleStringProperty();
    public static final StringProperty ResetTo = new SimpleStringProperty();
    public static final StringProperty OpenFileLocation = new SimpleStringProperty();
    public static final StringProperty GetRemaining = new SimpleStringProperty();
    public static final StringProperty PlayPreviousEpisode = new SimpleStringProperty();
    public static final StringProperty SetInactive = new SimpleStringProperty();
    public static final StringProperty SetActive = new SimpleStringProperty();
    public static final StringProperty SwitchBetweenActiveInactiveList = new SimpleStringProperty();
    public static final StringProperty AddUser = new SimpleStringProperty();
    public static final StringProperty DeleteUser = new SimpleStringProperty();
    public static final StringProperty ChangeUpdateTime = new SimpleStringProperty();
    public static final StringProperty ForceRecheckShows = new SimpleStringProperty();
    public static final StringProperty AddDirectory = new SimpleStringProperty();
    public static final StringProperty RemoveDirectory = new SimpleStringProperty();
    public static final StringProperty OpenSettingsFolder = new SimpleStringProperty();
    public static final StringProperty PrintAllShowInfo = new SimpleStringProperty();
    public static final StringProperty PrintAllDirectories = new SimpleStringProperty();
    public static final StringProperty PrintEmptyShows = new SimpleStringProperty();
    public static final StringProperty PrintIgnoredShows = new SimpleStringProperty();
    public static final StringProperty PrintHiddenShows = new SimpleStringProperty();
    public static final StringProperty UnHideAll = new SimpleStringProperty();
    public static final StringProperty SetAllActive = new SimpleStringProperty();
    public static final StringProperty SetAllInactive = new SimpleStringProperty();
    public static final StringProperty PrintPsfvAndUsfv = new SimpleStringProperty();
    public static final StringProperty PrintAllUserInfo = new SimpleStringProperty();
    public static final StringProperty DirectoryVersionPlus1 = new SimpleStringProperty();
    public static final StringProperty ClearFile = new SimpleStringProperty();
    public static final StringProperty ResetProgram = new SimpleStringProperty();
    public static final StringProperty ToggleDevMode = new SimpleStringProperty();
    public static final StringProperty InAppData = new SimpleStringProperty();
    public static final StringProperty WithTheJar = new SimpleStringProperty();
    public static final StringProperty UpdateTime = new SimpleStringProperty();
    public static final StringProperty DirectoryTimeout = new SimpleStringProperty();
    public static final StringProperty UnHideShow = new SimpleStringProperty();
    public static final StringProperty OpenAll = new SimpleStringProperty();
    public static final StringProperty OpenSelected = new SimpleStringProperty();
    public static final StringProperty NonForceRecheckShows = new SimpleStringProperty();
    // Checkbox Text
    public static final StringProperty InactiveShows = new SimpleStringProperty();
    public static final StringProperty OlderSeasons = new SimpleStringProperty();
    public static final StringProperty AllowFullWindowMovementUse = new SimpleStringProperty();
    public static final StringProperty DisableAutomaticShowSearching = new SimpleStringProperty();
    public static final StringProperty ShowUsername = new SimpleStringProperty();
    // Tooltip Text
    public static final StringProperty ShowHiddenShowsWith0EpisodeLeft = new SimpleStringProperty();
    public static final StringProperty CurrentlyRechecking = new SimpleStringProperty();
    public static final StringProperty DeleteUsersNoteCantDeleteCurrentUser = new SimpleStringProperty();
    public static final StringProperty WarningUnrecoverable = new SimpleStringProperty();
    public static final StringProperty InternalVersion = new SimpleStringProperty();
    public static final StringProperty MakeUserDefault = new SimpleStringProperty();
    public static final StringProperty MakeLanguageDefault = new SimpleStringProperty();
    // Other Text
    public static final StringProperty AddNewUsername = new SimpleStringProperty();
    public static final StringProperty PleaseEnterUsername = new SimpleStringProperty();
    public static final StringProperty UseDefaultUsername = new SimpleStringProperty();
    public static final StringProperty DefaultUserNotSet = new SimpleStringProperty();
    public static final StringProperty PleaseChooseAFolder = new SimpleStringProperty();
    public static final StringProperty PickTheEpisode = new SimpleStringProperty();
    public static final StringProperty YouHaveToPickASeason = new SimpleStringProperty();
    public static final StringProperty YouHaveToPickAEpisode = new SimpleStringProperty();
    public static final StringProperty NextEpisode = new SimpleStringProperty();
    public static final StringProperty UsernameIsntValid = new SimpleStringProperty();
    public static final StringProperty UsernameAlreadyTaken = new SimpleStringProperty();
    public static final StringProperty UsernameIsTooLong = new SimpleStringProperty();
    public static final StringProperty DirectoryIsAlreadyAdded = new SimpleStringProperty();
    public static final StringProperty MustBeANumberBetween = new SimpleStringProperty();
    public static final StringProperty ChooseYourUsername = new SimpleStringProperty();
    public static final StringProperty DirectoryWasADuplicate = new SimpleStringProperty();
    public static final StringProperty AddAnotherDirectory = new SimpleStringProperty();
    public static final StringProperty WhatShould = new SimpleStringProperty();
    public static final StringProperty BeResetTo = new SimpleStringProperty();
    public static final StringProperty ShowIsResetToThe = new SimpleStringProperty();
    public static final StringProperty DoYouWantToOpenAllAssociatedFolders = new SimpleStringProperty();
    public static final StringProperty PickTheFolderYouWantToOpen = new SimpleStringProperty();
    public static final StringProperty NoDirectlyPrecedingEpisodesFound = new SimpleStringProperty();
    public static final StringProperty HaveYouWatchedTheShow = new SimpleStringProperty();
    public static final StringProperty AreYouSure = new SimpleStringProperty();
    public static final StringProperty PleaseChooseADefaultUser = new SimpleStringProperty();
    public static final StringProperty ThereAreNoOtherUsersToDelete = new SimpleStringProperty();
    public static final StringProperty UserToDelete = new SimpleStringProperty();
    public static final StringProperty AreYouSureToWantToDelete = new SimpleStringProperty();
    public static final StringProperty EnterHowFastYouWantItToScanTheShowsFolders = new SimpleStringProperty();
    public static final StringProperty LeaveItAsIs = new SimpleStringProperty();
    public static final StringProperty PleaseEnterShowsDirectory = new SimpleStringProperty();
    public static final StringProperty YouNeedToEnterADirectory = new SimpleStringProperty();
    public static final StringProperty DirectoryIsInvalid = new SimpleStringProperty();
    public static final StringProperty ThereAreNoDirectoriesToDelete = new SimpleStringProperty();
    public static final StringProperty ThereAreNoDirectoriesToClear = new SimpleStringProperty();
    public static final StringProperty DirectoryToClear = new SimpleStringProperty();
    public static final StringProperty AreYouSureToWantToClear = new SimpleStringProperty();
    public static final StringProperty AreYouSureThisWillDeleteEverything = new SimpleStringProperty();
    public static final StringProperty OpenChangesWindow = new SimpleStringProperty();
    public static final StringProperty Dev1 = new SimpleStringProperty();
    public static final StringProperty Dev2 = new SimpleStringProperty();
    public static final StringProperty SetDefaultUser = new SimpleStringProperty();
    public static final StringProperty DirectoryToDelete = new SimpleStringProperty();
    public static final StringProperty PleaseChooseYourLanguage = new SimpleStringProperty();
    public static final StringProperty ChangeLanguage = new SimpleStringProperty();
    public static final StringProperty RestartTheProgramForTheNewLanguageToTakeEffect = new SimpleStringProperty();
    public static final StringProperty WhereWouldYouLikeTheProgramFilesToBeStored = new SimpleStringProperty();
    public static final StringProperty HoverOverAButtonForThePath = new SimpleStringProperty();
    public static final StringProperty DashSeason = new SimpleStringProperty();
    public static final StringProperty DashEpisode = new SimpleStringProperty();
    public static final StringProperty PickTheSeasonAndEpisode = new SimpleStringProperty();
    public static final StringProperty YouHaveReachedTheEnd = new SimpleStringProperty();
    public static final StringProperty PingingDirectories = new SimpleStringProperty();
    public static final StringProperty PathToDirectory = new SimpleStringProperty();
    public static final StringProperty PrintCurrentSeasonEpisode = new SimpleStringProperty();
    public static final StringProperty WasFoundToBeInactive = new SimpleStringProperty();
    public static final StringProperty PleaseCorrectTheIssueThenForceRefresh = new SimpleStringProperty();
    public static final StringProperty NotifyChangesFor = new SimpleStringProperty();
    public static final StringProperty OnlyChecksEveryRuns = new SimpleStringProperty();
    public static final StringProperty JavaVersionFull = new SimpleStringProperty();
    public static final StringProperty CodedUsingFull = new SimpleStringProperty();
    public static final StringProperty ThereAreNoHiddenShows = new SimpleStringProperty();
    public static final StringProperty PickShowToUnHide = new SimpleStringProperty();
    public static final StringProperty DirectoryToSaveExportIn = new SimpleStringProperty();
    public static final StringProperty PrintShowInformation = new SimpleStringProperty();
    public static final StringProperty ChooseWhatToExport = new SimpleStringProperty();
    public static final StringProperty EnterLocationToSaveExport = new SimpleStringProperty();
    public static final StringProperty FileAlreadyExistsOverwriteIt = new SimpleStringProperty();
    public static final StringProperty FilenameMustEndIn = new SimpleStringProperty();
    public static final StringProperty YouMustSelectACheckbox = new SimpleStringProperty();
    public static final StringProperty DoYouWantToImportFiles = new SimpleStringProperty();
    public static final StringProperty DoYouWantToRestartTheProgramForTheImportToTakeFullEffectWarningSettingsChangedOutsideOfTheImportWontBeSaved = new SimpleStringProperty();
    public static final StringProperty MTrackHasNowImportedTheFiles = new SimpleStringProperty();
    public static final StringProperty AutomaticallyOverwriteFilesIfNoYouWillBeAskedForEachExistingFile = new SimpleStringProperty();
    public static final StringProperty AlreadyExistsOverwriteIt = new SimpleStringProperty();
    public static final StringProperty EnterFileLocation = new SimpleStringProperty();
    public static final StringProperty ExportSettings = new SimpleStringProperty();
    public static final StringProperty ImportSettings = new SimpleStringProperty();
    public static final StringProperty FileDoesNotExists = new SimpleStringProperty();
    public static final StringProperty CurrentlyPlaying = new SimpleStringProperty();
    public static final StringProperty WasUnableToPlayTheEpisode = new SimpleStringProperty();
    // Other Language Specific Stuff // Todo - Add appropriate code - ie, To lang files, potentially allow multiple regex's / Season folder names
    public static final String seasonRegex = "[s][e][a][s][o][n]\\s\\d{1,4}";
    // Strings set by program
    public static final StringProperty UserName = new SimpleStringProperty();
    // All Other Text
    public static String DefaultUsername;
}
