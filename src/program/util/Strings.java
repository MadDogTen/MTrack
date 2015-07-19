package program.util;

import java.io.File;
import java.util.logging.Logger;

public class Strings {
    // Program Info
    public static final String ProgramTitle = "MTrack";
    //---------- Main Strings ----------\\
    public static final String MTrackVersion = "Pre-Alpha 0.9.8";
    public static final String CodedBy = "MadDogTen";
    public static final String CodedUsing = "Using: Intellij IDEA 14.1.4";
    public static final String codedWith = "Java";
    public static final String javaVersion = "Version: 1.8.0_45";
    //Other
    public static final String SettingsFileName = "Program";
    // Other
    public static final String EmptyString = "";
    public static final String FileSeparator = File.separator;
    public static final String ExitButtonText = "X";
    private static final Logger log = Logger.getLogger(Strings.class.getName());
    // All Other Text
    public static String DefaultUsername;
    @SuppressWarnings("unused")
    public static String TextMissing;

    // Single Words
    public static String Submit;
    public static String Clear;
    public static String Refresh;
    public static String Close;
    public static String Yes;
    public static String No;
    public static String Beginning;
    public static String End;
    public static String QuestionMark;
    public static String Shows;
    public static String Left;
    public static String Settings;
    public static String Options;
    public static String Main;
    public static String Users;
    public static String Other;
    public static String Developers;
    public static String Reset;
    public static String About;
    public static String Season;
    public static String Episode;

    // Button Text
    public static String SetSeasonEpisode;
    public static String PlaySeasonEpisode;
    public static String HideShow;
    public static String ResetTo;
    public static String OpenFileLocation;
    public static String GetRemaining;
    public static String PlayPreviousEpisode;
    public static String SetInactive;
    public static String SetActive;
    public static String SwitchBetweenActiveInactiveList;
    public static String AddUser;
    public static String DeleteUser;
    public static String ChangeUpdateTime;
    public static String ForceRecheckShows;
    public static String AddDirectory;
    public static String RemoveDirectory;
    public static String OpenSettingsFolder;
    public static String PrintAllShowInfo;
    public static String PrintAllDirectories;
    public static String PrintEmptyShows;
    public static String PrintIgnoredShows;
    public static String PrintHiddenShows;
    public static String UnHideAll;
    public static String SetAllActive;
    public static String SetAllInactive;
    public static String PrintPSFV;
    public static String PrintUSFV;
    public static String PrintAllUserInfo;
    public static String DirectoryVersionPlus1;
    public static String ClearFile;
    public static String ResetProgram;
    public static String ToggleDevMode;
    public static String InAppData;
    public static String WithTheJar;

    // Tooltip Text
    public static String ShowHideShowsWith0EpisodeLeft;
    public static String CurrentlyRechecking;
    public static String DeleteUsersNoteCantDeleteCurrentUser;
    public static String WarningUnrecoverable;

    // Other Text
    public static String AddNewUsername;
    public static String PleaseEnterUsername;
    public static String UseDefaultUsername;
    public static String DefaultUserNotSet;
    public static String PleaseChooseAFolder;
    public static String PickTheEpisode;
    public static String YouHaveToPickASeason;
    public static String YouHaveToPickAEpisode;
    public static String NextEpisode;
    public static String UsernameIsntValid;
    public static String UsernameAlreadyTaken;
    public static String UsernameIsTooLong;
    public static String DirectoryIsAlreadyAdded;
    public static String MustBeANumberGreaterThanOrEqualTo10;
    public static String ChooseYourUsername;
    public static String DirectoryWasADuplicate;
    public static String AddAnotherDirectory;
    public static String WhatShould;
    public static String BeResetTo;
    public static String ShowIsResetToThe;
    public static String DoYouWantToOpenAllAssociatedFolders;
    public static String PickTheFolderYouWantToOpen;
    public static String NoDirectlyPrecedingEpisodesFound;
    public static String HaveYouWatchedTheShow;
    public static String AreYouSure;
    public static String PleaseChooseADefaultUser;
    public static String ThereAreNoOtherUsersToDelete;
    public static String UserToDelete;
    public static String AreYouSureToWantToDelete;
    public static String EnterHowFastYouWantItToScanTheShowsFolders;
    public static String LeaveItAsIs;
    public static String PleaseEnterShowsDirectory;
    public static String YouNeedToEnterADirectory;
    public static String DirectoryIsInvalid;
    public static String ThereAreNoDirectoriesToDelete;
    public static String ThereAreNoDirectoriesToClear;
    public static String DirectoryToClear;
    public static String AreYouSureToWantToClear;
    public static String AreYouSureThisWillDeleteEverything;
    public static String OpenChangesWindow;
    public static String Dev1;
    public static String Dev2;
    public static String SetDefaultUser;
    public static String DirectoryToDelete;
    public static String PleaseChooseYourLanguage;
    public static String ChangeLanguage;
    public static String RestartTheProgramForTheNewLanguageToTakeEffect;
    public static String WhereWouldYouLikeTheProgramFilesToBeStored;
    public static String HoverOverAButtonForThePath;
    public static String WasAdded;
    public static String WasRemoved;
    public static String DashSeason;
    public static String DashEpisode;
    public static String PickTheSeason;
    public static String YouHaveReachedTheEnd;
    public static String PingingDirectories;
    public static String PathToDirectory;

    // Strings set by program
    public static String UserName;

    public static void addMissingTextForAllMissingStrings() {
        log.info("Checking for missing Strings...");
        if (TextMissing == null) TextMissing = "This text is missing, Please report.";

        if (Submit == null) Submit = TextMissing;
        if (Clear == null) Clear = TextMissing;
        if (Refresh == null) Refresh = TextMissing;
        if (Close == null) Close = TextMissing;
        if (Yes == null) Yes = TextMissing;
        if (No == null) No = TextMissing;
        if (Beginning == null) Beginning = TextMissing;
        if (End == null) End = TextMissing;
        if (QuestionMark == null) QuestionMark = TextMissing;
        if (Shows == null) Shows = TextMissing;
        if (Left == null) Left = TextMissing;
        if (Settings == null) Settings = TextMissing;
        if (Options == null) Options = TextMissing;
        if (Main == null) Main = TextMissing;
        if (Users == null) Users = TextMissing;
        if (Other == null) Other = TextMissing;
        if (Developers == null) Developers = TextMissing;
        if (Reset == null) Reset = TextMissing;
        if (About == null) About = TextMissing;
        if (Season == null) Season = TextMissing;
        if (Episode == null) Episode = TextMissing;
        if (SetSeasonEpisode == null) SetSeasonEpisode = TextMissing;
        if (PlaySeasonEpisode == null) PlaySeasonEpisode = TextMissing;
        if (HideShow == null) HideShow = TextMissing;
        if (ResetTo == null) ResetTo = TextMissing;
        if (OpenFileLocation == null) OpenFileLocation = TextMissing;
        if (GetRemaining == null) GetRemaining = TextMissing;
        if (PlayPreviousEpisode == null) PlayPreviousEpisode = TextMissing;
        if (SetInactive == null) SetInactive = TextMissing;
        if (SetActive == null) SetActive = TextMissing;
        if (SwitchBetweenActiveInactiveList == null) SwitchBetweenActiveInactiveList = TextMissing;
        if (AddUser == null) AddUser = TextMissing;
        if (DeleteUser == null) DeleteUser = TextMissing;
        if (ChangeUpdateTime == null) ChangeUpdateTime = TextMissing;
        if (ForceRecheckShows == null) ForceRecheckShows = TextMissing;
        if (AddDirectory == null) AddDirectory = TextMissing;
        if (RemoveDirectory == null) RemoveDirectory = TextMissing;
        if (OpenSettingsFolder == null) OpenSettingsFolder = TextMissing;
        if (PrintAllShowInfo == null) PrintAllShowInfo = TextMissing;
        if (PrintAllDirectories == null) PrintAllDirectories = TextMissing;
        if (PrintEmptyShows == null) PrintEmptyShows = TextMissing;
        if (PrintIgnoredShows == null) PrintIgnoredShows = TextMissing;
        if (PrintHiddenShows == null) PrintHiddenShows = TextMissing;
        if (UnHideAll == null) UnHideAll = TextMissing;
        if (SetAllActive == null) SetAllActive = TextMissing;
        if (SetAllInactive == null) SetAllInactive = TextMissing;
        if (PrintPSFV == null) PrintPSFV = TextMissing;
        if (PrintUSFV == null) PrintUSFV = TextMissing;
        if (PrintAllUserInfo == null) PrintAllUserInfo = TextMissing;
        if (DirectoryVersionPlus1 == null) DirectoryVersionPlus1 = TextMissing;
        if (ClearFile == null) ClearFile = TextMissing;
        if (ResetProgram == null) ResetProgram = TextMissing;
        if (ToggleDevMode == null) ToggleDevMode = TextMissing;
        if (InAppData == null) InAppData = TextMissing;
        if (WithTheJar == null) WithTheJar = TextMissing;
        if (ShowHideShowsWith0EpisodeLeft == null) ShowHideShowsWith0EpisodeLeft = TextMissing;
        if (CurrentlyRechecking == null) CurrentlyRechecking = TextMissing;
        if (DeleteUsersNoteCantDeleteCurrentUser == null) DeleteUsersNoteCantDeleteCurrentUser = TextMissing;
        if (WarningUnrecoverable == null) WarningUnrecoverable = TextMissing;
        if (AddNewUsername == null) AddNewUsername = TextMissing;
        if (PleaseEnterUsername == null) PleaseEnterUsername = TextMissing;
        if (UseDefaultUsername == null) UseDefaultUsername = TextMissing;
        if (DefaultUserNotSet == null) DefaultUserNotSet = TextMissing;
        if (PleaseChooseAFolder == null) PleaseChooseAFolder = TextMissing;
        if (PickTheEpisode == null) PickTheEpisode = TextMissing;
        if (YouHaveToPickASeason == null) YouHaveToPickASeason = TextMissing;
        if (YouHaveToPickAEpisode == null) YouHaveToPickAEpisode = TextMissing;
        if (NextEpisode == null) NextEpisode = TextMissing;
        if (UsernameIsntValid == null) UsernameIsntValid = TextMissing;
        if (UsernameAlreadyTaken == null) UsernameAlreadyTaken = TextMissing;
        if (UsernameIsTooLong == null) UsernameIsTooLong = TextMissing;
        if (DirectoryIsAlreadyAdded == null) DirectoryIsAlreadyAdded = TextMissing;
        if (MustBeANumberGreaterThanOrEqualTo10 == null) MustBeANumberGreaterThanOrEqualTo10 = TextMissing;
        if (ChooseYourUsername == null) ChooseYourUsername = TextMissing;
        if (DirectoryWasADuplicate == null) DirectoryWasADuplicate = TextMissing;
        if (AddAnotherDirectory == null) AddAnotherDirectory = TextMissing;
        if (WhatShould == null) WhatShould = TextMissing;
        if (BeResetTo == null) BeResetTo = TextMissing;
        if (ShowIsResetToThe == null) ShowIsResetToThe = TextMissing;
        if (DoYouWantToOpenAllAssociatedFolders == null) DoYouWantToOpenAllAssociatedFolders = TextMissing;
        if (PickTheFolderYouWantToOpen == null) PickTheFolderYouWantToOpen = TextMissing;
        if (NoDirectlyPrecedingEpisodesFound == null) NoDirectlyPrecedingEpisodesFound = TextMissing;
        if (HaveYouWatchedTheShow == null) HaveYouWatchedTheShow = TextMissing;
        if (AreYouSure == null) AreYouSure = TextMissing;
        if (PleaseChooseADefaultUser == null) PleaseChooseADefaultUser = TextMissing;
        if (ThereAreNoOtherUsersToDelete == null) ThereAreNoOtherUsersToDelete = TextMissing;
        if (UserToDelete == null) UserToDelete = TextMissing;
        if (AreYouSureToWantToDelete == null) AreYouSureToWantToDelete = TextMissing;
        if (EnterHowFastYouWantItToScanTheShowsFolders == null)
            EnterHowFastYouWantItToScanTheShowsFolders = TextMissing;
        if (LeaveItAsIs == null) LeaveItAsIs = TextMissing;
        if (PleaseEnterShowsDirectory == null) PleaseEnterShowsDirectory = TextMissing;
        if (YouNeedToEnterADirectory == null) YouNeedToEnterADirectory = TextMissing;
        if (DirectoryIsInvalid == null) DirectoryIsInvalid = TextMissing;
        if (ThereAreNoDirectoriesToDelete == null) ThereAreNoDirectoriesToDelete = TextMissing;
        if (ThereAreNoDirectoriesToClear == null) ThereAreNoDirectoriesToClear = TextMissing;
        if (DirectoryToClear == null) DirectoryToClear = TextMissing;
        if (AreYouSureToWantToClear == null) AreYouSureToWantToClear = TextMissing;
        if (AreYouSureThisWillDeleteEverything == null) AreYouSureThisWillDeleteEverything = TextMissing;
        if (OpenChangesWindow == null) OpenChangesWindow = TextMissing;
        if (Dev1 == null) Dev1 = TextMissing;
        if (Dev2 == null) Dev2 = TextMissing;
        if (SetDefaultUser == null) SetDefaultUser = TextMissing;
        if (DirectoryToDelete == null) DirectoryToDelete = TextMissing;
        if (PleaseChooseYourLanguage == null) PleaseChooseYourLanguage = TextMissing;
        if (ChangeLanguage == null) ChangeLanguage = TextMissing;
        if (RestartTheProgramForTheNewLanguageToTakeEffect == null)
            RestartTheProgramForTheNewLanguageToTakeEffect = TextMissing;
        if (WhereWouldYouLikeTheProgramFilesToBeStored == null)
            WhereWouldYouLikeTheProgramFilesToBeStored = TextMissing;
        if (HoverOverAButtonForThePath == null) HoverOverAButtonForThePath = TextMissing;
        if (WasAdded == null) WasAdded = TextMissing;
        if (WasRemoved == null) WasRemoved = TextMissing;
        if (DashSeason == null) DashSeason = TextMissing;
        if (DashEpisode == null) DashEpisode = TextMissing;
        if (PickTheSeason == null) PickTheSeason = TextMissing;
        if (YouHaveReachedTheEnd == null) YouHaveReachedTheEnd = TextMissing;
        if (PingingDirectories == null) PingingDirectories = TextMissing;
        if (PathToDirectory == null) PathToDirectory = TextMissing;
        log.info("Finished checking for missing Strings.");
    }
}
