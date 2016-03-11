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
        this.showColumnWidth = Variables.SHOWS_COLUMN_WIDTH;
        this.remainingColumnWidth = Variables.REMAINING_COLUMN_WIDTH;
        this.seasonColumnWidth = Variables.SEASONS_COLUMN_WIDTH;
        this.episodeColumnWidth = Variables.EPISODE_COLUMN_WIDTH;
        showColumnVisibility = true;
        remainingColumnVisibility = true;
        seasonColumnVisibility = false;
        episodeColumnVisibility = false;
        this.numberOfDirectories = 0;
    }

    public long getProgramSettingsID() {
        return programGeneratedID;
    }

    public int getProgramSettingsFileVersion() {
        return programSettingsFileVersion;
    }

    public void setProgramSettingsFileVersion(int version) {
        programSettingsFileVersion = version;
    }

    public int getMainDirectoryVersion() {
        return mainDirectoryVersion;
    }

    public void setMainDirectoryVersion(int version) {
        mainDirectoryVersion = version;
    }

    public int getShowFileVersion() {
        return showFileVersion;
    }

    public void setShowFileVersion(int version) {
        showFileVersion = version;
    }

    public int getUpdateSpeed() {
        return updateSpeed;
    }

    public void setUpdateSpeed(int updateSpeed) {
        this.updateSpeed = updateSpeed;
        Variables.updateSpeed = updateSpeed;
    }

    public boolean isDisableAutomaticShowUpdating() {
        return disableAutomaticShowUpdating;
    }

    public void setDisableAutomaticShowUpdating(boolean disableAutomaticShowUpdating) {
        this.disableAutomaticShowUpdating = disableAutomaticShowUpdating;
        Variables.disableAutomaticRechecking = disableAutomaticShowUpdating;
    }

    public int getTimeToWaitForDirectory() {
        return timeToWaitForDirectory;
    }

    public void setTimeToWaitForDirectory(int timeToWaitForDirectory) {
        this.timeToWaitForDirectory = timeToWaitForDirectory;
        Variables.timeToWaitForDirectory = timeToWaitForDirectory;
    }

    public boolean isShow0Remaining() {
        return show0Remaining;
    }

    public void setShow0Remaining(boolean show0Remaining) {
        this.show0Remaining = show0Remaining;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        Variables.language = language;
    }

    public boolean isRecordChangesForNonActiveShows() {
        return recordChangesForNonActiveShows;
    }

    public void setRecordChangesForNonActiveShows(boolean recordChangesForNonActiveShows) {
        this.recordChangesForNonActiveShows = recordChangesForNonActiveShows;
        Variables.recordChangesForNonActiveShows = recordChangesForNonActiveShows;
    }

    public boolean isRecordChangedSeasonsLowerThanCurrent() {
        return recordChangedSeasonsLowerThanCurrent;
    }

    public void setRecordChangedSeasonsLowerThanCurrent(boolean recordChangedSeasonsLowerThanCurrent) {
        this.recordChangedSeasonsLowerThanCurrent = recordChangedSeasonsLowerThanCurrent;
        Variables.recordChangedSeasonsLowerThanCurrent = recordChangedSeasonsLowerThanCurrent;
    }

    private boolean isMoveStageWithParent() {
        return moveStageWithParent;
    }

    private void setMoveStageWithParent(boolean moveStageWithParent) {
        this.moveStageWithParent = moveStageWithParent;
        Variables.moveStageWithParent = moveStageWithParent;
    }

    private boolean isHaveStageBlockParentStage() {
        return haveStageBlockParentStage;
    }

    private void setHaveStageBlockParentStage(boolean haveStageBlockParentStage) {
        this.haveStageBlockParentStage = haveStageBlockParentStage;
        Variables.haveStageBlockParentStage = haveStageBlockParentStage;
    }

    public boolean isStageMoveWithParentAndBlockParent() {
        return isMoveStageWithParent() && isHaveStageBlockParentStage();
    }

    public void setStageMoveWithParentAndBlockParent(boolean moveAndBlock) {
        setMoveStageWithParent(moveAndBlock);
        setHaveStageBlockParentStage(moveAndBlock);
    }

    public boolean isEnableSpecialEffects() {
        return enableSpecialEffects;
    }

    public void setEnableSpecialEffects(boolean enableSpecialEffects) {
        this.enableSpecialEffects = enableSpecialEffects;
        Variables.specialEffects = enableSpecialEffects;
    }

    public boolean isUseDefaultUser() {
        return useDefaultUser;
    }

    public void setUseDefaultUser(boolean useDefaultUser) {
        this.useDefaultUser = useDefaultUser;
    }

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser;
    }

    public boolean isEnableAutomaticSaving() {
        return enableAutomaticSaving;
    }

    public void setEnableAutomaticSaving(boolean enableAutomaticSaving) {
        this.enableAutomaticSaving = enableAutomaticSaving;
        Variables.enableAutoSavingOnTimer = enableAutomaticSaving;
    }

    public int getSaveSpeed() {
        return saveSpeed;
    }

    public void setSaveSpeed(int saveSpeed) {
        this.saveSpeed = saveSpeed;
        Variables.savingSpeed = saveSpeed;
    }

    public double getShowColumnWidth() {
        return showColumnWidth;
    }

    public void setShowColumnWidth(double showColumnWidth) {
        this.showColumnWidth = showColumnWidth;
    }

    public double getRemainingColumnWidth() {
        return remainingColumnWidth;
    }

    public void setRemainingColumnWidth(double remainingColumnWidth) {
        this.remainingColumnWidth = remainingColumnWidth;
    }

    public double getSeasonColumnWidth() {
        return seasonColumnWidth;
    }

    public void setSeasonColumnWidth(double seasonColumnWidth) {
        this.seasonColumnWidth = seasonColumnWidth;
    }

    public double getEpisodeColumnWidth() {
        return episodeColumnWidth;
    }

    public void setEpisodeColumnWidth(double episodeColumnWidth) {
        this.episodeColumnWidth = episodeColumnWidth;
    }

    public boolean isShowColumnVisibility() {
        return showColumnVisibility;
    }

    public void setShowColumnVisibility(boolean showColumnVisibility) {
        this.showColumnVisibility = showColumnVisibility;
    }

    public boolean isRemainingColumnVisibility() {
        return remainingColumnVisibility;
    }

    public void setRemainingColumnVisibility(boolean remainingColumnVisibility) {
        this.remainingColumnVisibility = remainingColumnVisibility;
    }

    public boolean isSeasonColumnVisibility() {
        return seasonColumnVisibility;
    }

    public void setSeasonColumnVisibility(boolean seasonColumnVisibility) {
        this.seasonColumnVisibility = seasonColumnVisibility;
    }

    public boolean isEpisodeColumnVisibility() {
        return episodeColumnVisibility;
    }

    public void setEpisodeColumnVisibility(boolean episodeColumnVisibility) {
        this.episodeColumnVisibility = episodeColumnVisibility;
    }

    public int getNumberOfDirectories() {
        return numberOfDirectories;
    }

    public void setNumberOfDirectories(int numberOfDirectories) {
        this.numberOfDirectories = numberOfDirectories;
    }
}
