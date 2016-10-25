package com.maddogten.mtrack.information.settings;

import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.io.Serializable;
import java.util.Random;

/*
      ProgramSettings stores all the program settings.
 */

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class ProgramSettings implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 3699693145859359106L;
    private final long programGeneratedID; /*programGeneratedID is generated per programs file. It is used to compare against the programGeneratedID's in UserSettings and Directory, and if they are different,
    do the appropriate tasks, and set it to this one.*/

    // Versions
    private int programSettingsFileVersion; /*Used to check if the saved ProgramSettings file is up to date with the program, and if not, update it.*/
    private int mainDirectoryVersion; /*If multiple users use the program and new shows are found, this number is used to check if the other users files are outdated on next run, and update them.
    This number only update if changes are found.*/
    private int showFileVersion; /*Needs to be moved the Directory*/

    // General
    private int updateSpeed; /*How long it waits in-between searching for new files.*/
    private boolean disableAutomaticShowUpdating; /*Stops the program from automatically searching for new files. You must do it manually then*/
    private int timeToWaitForDirectory; /*How long the program will wait before reporting the directory as failed to load*/
    private boolean show0Remaining; /*Saves the last state of show0Remaining, Which changes whether it shows active shows with no further episodes to currently watch*/
    private boolean showActiveShows; /*Save the last state of showActiveShows, Which changes whether it show active shows on the inactive list*/
    private String language; /*Saves the default language*/

    // ChangeList Settings
    private boolean recordChangesForNonActiveShows; /*Controls whether the program will show changes for non-active shows on the changelog or not. It will still search for them.*/
    private boolean recordChangedSeasonsLowerThanCurrent; /*Controls whether the program will show changes for seasons lower than the one you are currently on, on the changelog or not. It will still search for them.*/

    // UI Settings
    private boolean moveStageWithParent; /*Changes whether or not you can freely move stages or not. Otherwise the parent stays locked to them*/
    private boolean haveStageBlockParentStage; /*Changes whether the child stage will block the parent stage. Not fully supported currently*/
    private boolean enableSpecialEffects;

    // Default User
    private boolean useDefaultUser; /*True if the default user is used.*/
    private String defaultUser; /*The default user used for the above setting*/

    // Automatic Saving
    private boolean enableAutomaticSaving;
    private int saveSpeed;

    // Logging
    private boolean fileLogging;

    // TV Maze
    private boolean useRemoteDatabase;

    private int numberOfDirectories; /*Stores the number of directories it last ended with, and if different, handles adding them to the program. */

    // ---- Gui Values ---- \\

    // Default Widths
    private double showColumnWidth; /*These 4 simply save the width of the columns */
    private double remainingColumnWidth;
    private double seasonColumnWidth;
    private double episodeColumnWidth;

    // Visibility
    private boolean showColumnVisibility; /*These 4 simple save if the columns are showing or not*/
    private boolean remainingColumnVisibility;
    private boolean seasonColumnVisibility;
    private boolean episodeColumnVisibility;

    public ProgramSettings() {
        this.programGeneratedID = new Random().nextLong();
        this.programSettingsFileVersion = Variables.ProgramSettingsFileVersion;
        this.mainDirectoryVersion = 0;
        this.showFileVersion = Variables.DirectoryFileVersion;
        this.updateSpeed = Variables.defaultUpdateSpeed;
        this.disableAutomaticShowUpdating = false;
        this.timeToWaitForDirectory = Variables.defaultTimeToWaitForDirectory;
        this.show0Remaining = false;
        this.showActiveShows = false;
        this.language = "None";
        this.recordChangesForNonActiveShows = false;
        this.recordChangedSeasonsLowerThanCurrent = false;
        this.moveStageWithParent = true;
        this.haveStageBlockParentStage = true;
        this.enableSpecialEffects = true;
        this.useDefaultUser = false;
        this.defaultUser = Strings.EmptyString;
        this.enableAutomaticSaving = true;
        this.saveSpeed = 600;
        this.fileLogging = true;
        this.useRemoteDatabase = false;
        this.showColumnWidth = Variables.SHOWS_COLUMN_WIDTH;
        this.remainingColumnWidth = Variables.REMAINING_COLUMN_WIDTH;
        this.seasonColumnWidth = Variables.SEASONS_COLUMN_WIDTH;
        this.episodeColumnWidth = Variables.EPISODE_COLUMN_WIDTH;
        this.showColumnVisibility = true;
        this.remainingColumnVisibility = true;
        this.seasonColumnVisibility = false;
        this.episodeColumnVisibility = false;
        this.numberOfDirectories = 0;
    }

    public long getProgramSettingsID() {
        return programGeneratedID;
    }

    public int getProgramSettingsFileVersion() {
        return programSettingsFileVersion;
    }

    public void setProgramSettingsFileVersion(final int version) {
        programSettingsFileVersion = version;
    }

    public int getMainDirectoryVersion() {
        return mainDirectoryVersion;
    }

    public void setMainDirectoryVersion(final int version) {
        mainDirectoryVersion = version;
    }

    public int getShowFileVersion() {
        return showFileVersion;
    }

    public void setShowFileVersion(final int version) {
        showFileVersion = version;
    }

    public int getUpdateSpeed() {
        return updateSpeed;
    }

    public void setUpdateSpeed(final int updateSpeed) {
        this.updateSpeed = updateSpeed;
        Variables.updateSpeed = updateSpeed;
    }

    public boolean isDisableAutomaticShowUpdating() {
        return disableAutomaticShowUpdating;
    }

    public void setDisableAutomaticShowUpdating(final boolean disableAutomaticShowUpdating) {
        this.disableAutomaticShowUpdating = disableAutomaticShowUpdating;
        Variables.disableAutomaticRechecking = disableAutomaticShowUpdating;
    }

    public int getTimeToWaitForDirectory() {
        return timeToWaitForDirectory;
    }

    public void setTimeToWaitForDirectory(final int timeToWaitForDirectory) {
        this.timeToWaitForDirectory = timeToWaitForDirectory;
        Variables.timeToWaitForDirectory = timeToWaitForDirectory;
    }

    public boolean isShow0Remaining() {
        return show0Remaining;
    }

    public void setShow0Remaining(final boolean show0Remaining) {
        this.show0Remaining = show0Remaining;
        Variables.show0Remaining = show0Remaining;
    }

    public boolean isShowActiveShows() {
        return showActiveShows;
    }

    public void setShowActiveShows(final boolean showActiveShows) {
        this.showActiveShows = showActiveShows;
        Variables.showActiveShows = showActiveShows;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
        Variables.language = language;
    }

    public boolean isRecordChangesForNonActiveShows() {
        return recordChangesForNonActiveShows;
    }

    public void setRecordChangesForNonActiveShows(final boolean recordChangesForNonActiveShows) {
        this.recordChangesForNonActiveShows = recordChangesForNonActiveShows;
        Variables.recordChangesForNonActiveShows = recordChangesForNonActiveShows;
    }

    public boolean isRecordChangedSeasonsLowerThanCurrent() {
        return recordChangedSeasonsLowerThanCurrent;
    }

    public void setRecordChangedSeasonsLowerThanCurrent(final boolean recordChangedSeasonsLowerThanCurrent) {
        this.recordChangedSeasonsLowerThanCurrent = recordChangedSeasonsLowerThanCurrent;
        Variables.recordChangedSeasonsLowerThanCurrent = recordChangedSeasonsLowerThanCurrent;
    }

    private boolean isMoveStageWithParent() {
        return moveStageWithParent;
    }

    private void setMoveStageWithParent(final boolean moveStageWithParent) {
        this.moveStageWithParent = moveStageWithParent;
        Variables.moveStageWithParent = moveStageWithParent;
    }

    private boolean isHaveStageBlockParentStage() {
        return haveStageBlockParentStage;
    }

    private void setHaveStageBlockParentStage(final boolean haveStageBlockParentStage) {
        this.haveStageBlockParentStage = haveStageBlockParentStage;
        Variables.haveStageBlockParentStage = haveStageBlockParentStage;
    }

    public boolean isStageMoveWithParentAndBlockParent() {
        return isMoveStageWithParent() && isHaveStageBlockParentStage();
    }

    public void setStageMoveWithParentAndBlockParent(final boolean moveAndBlock) {
        setMoveStageWithParent(moveAndBlock);
        setHaveStageBlockParentStage(moveAndBlock);
    }

    public boolean isEnableSpecialEffects() {
        return enableSpecialEffects;
    }

    public void setEnableSpecialEffects(final boolean enableSpecialEffects) {
        this.enableSpecialEffects = enableSpecialEffects;
        Variables.specialEffects = enableSpecialEffects;
    }

    public boolean isUseDefaultUser() {
        return useDefaultUser;
    }

    public void setUseDefaultUser(final boolean useDefaultUser) {
        this.useDefaultUser = useDefaultUser;
    }

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(final String defaultUser) {
        this.defaultUser = defaultUser;
    }

    public boolean isEnableAutomaticSaving() {
        return enableAutomaticSaving;
    }

    public void setEnableAutomaticSaving(final boolean enableAutomaticSaving) {
        this.enableAutomaticSaving = enableAutomaticSaving;
        Variables.enableAutoSavingOnTimer = enableAutomaticSaving;
    }

    public int getSaveSpeed() {
        return saveSpeed;
    }

    public void setSaveSpeed(final int saveSpeed) {
        this.saveSpeed = saveSpeed;
        Variables.savingSpeed = saveSpeed;
    }

    public boolean isFileLogging() {
        return fileLogging;
    }

    public void setFileLogging(final boolean fileLogging) {
        this.fileLogging = fileLogging;
        Variables.enableFileLogging = fileLogging;
    }

    public boolean isUseRemoteDatabase() {
        return useRemoteDatabase;
    }

    public void setUseRemoteDatabase(final boolean useRemoteDatabase) {
        this.useRemoteDatabase = useRemoteDatabase;
        Variables.useOnlineDatabase = useRemoteDatabase;
    }

    public double getShowColumnWidth() {
        return showColumnWidth;
    }

    public void setShowColumnWidth(final double showColumnWidth) {
        this.showColumnWidth = showColumnWidth;
    }

    public double getRemainingColumnWidth() {
        return remainingColumnWidth;
    }

    public void setRemainingColumnWidth(final double remainingColumnWidth) {
        this.remainingColumnWidth = remainingColumnWidth;
    }

    public double getSeasonColumnWidth() {
        return seasonColumnWidth;
    }

    public void setSeasonColumnWidth(final double seasonColumnWidth) {
        this.seasonColumnWidth = seasonColumnWidth;
    }

    public double getEpisodeColumnWidth() {
        return episodeColumnWidth;
    }

    public void setEpisodeColumnWidth(final double episodeColumnWidth) {
        this.episodeColumnWidth = episodeColumnWidth;
    }

    public boolean isShowColumnVisibility() {
        return showColumnVisibility;
    }

    public void setShowColumnVisibility(final boolean showColumnVisibility) {
        this.showColumnVisibility = showColumnVisibility;
    }

    public boolean isRemainingColumnVisibility() {
        return remainingColumnVisibility;
    }

    public void setRemainingColumnVisibility(final boolean remainingColumnVisibility) {
        this.remainingColumnVisibility = remainingColumnVisibility;
    }

    public boolean isSeasonColumnVisibility() {
        return seasonColumnVisibility;
    }

    public void setSeasonColumnVisibility(final boolean seasonColumnVisibility) {
        this.seasonColumnVisibility = seasonColumnVisibility;
    }

    public boolean isEpisodeColumnVisibility() {
        return episodeColumnVisibility;
    }

    public void setEpisodeColumnVisibility(final boolean episodeColumnVisibility) {
        this.episodeColumnVisibility = episodeColumnVisibility;
    }

    public int getNumberOfDirectories() {
        return numberOfDirectories;
    }

    public void setNumberOfDirectories(final int numberOfDirectories) {
        this.numberOfDirectories = numberOfDirectories;
    }
}
