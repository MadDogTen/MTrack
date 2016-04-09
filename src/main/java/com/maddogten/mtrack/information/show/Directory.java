package com.maddogten.mtrack.information.show;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.Variables;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.Random;

/*
      Directory stores each directory the user added, and their related information / settings.
 */

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class Directory implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 8211794325592517516L;
    private final File directory;
    private final String fileName;
    private final long directoryID;
    @SuppressWarnings({"FieldCanBeLocal", "CanBeFinal", "unused", "FieldMayBeFinal"})
    // The idea for priority is that once this is implemented you will be able to choose which directory the program will prefer to play an episode from, if found in multiple locations. To be implemented at a later time.
    private int priority;
    private Map<String, Show> shows;
    private int directoryFileVersion;
    private long lastProgramID;

    public Directory(File directory, String fileName, int priority, Map<String, Show> shows) {
        this.directory = directory;
        this.fileName = fileName;
        this.directoryID = new Random().nextLong() + 1;
        this.priority = priority;
        this.shows = shows;
        this.directoryFileVersion = Variables.DirectoryFileVersion;
        this.lastProgramID = ClassHandler.programSettingsController().getSettingsFile().getProgramSettingsID();
    }

    public File getDirectory() {
        return directory;
    }

    public String getFileName() {
        return fileName;
    }

    public long getDirectoryID() {
        return directoryID;
    }

    public int getPriority() {
        return priority;
    }

    /*public void setPriority(int priority) {
        this.priority = priority;
    }*/

    public Map<String, Show> getShows() {
        return shows;
    }

    public void setShows(Map<String, Show> shows) {
        this.shows = shows;
    }

    public int getDirectoryFileVersion() {
        return directoryFileVersion;
    }

    public void setDirectoryFileVersion(int directoryFileVersion) {
        this.directoryFileVersion = directoryFileVersion;
    }

    public long getLastProgramID() {
        return lastProgramID;
    }

    public void setLastProgramID(long lastProgramID) {
        this.lastProgramID = lastProgramID;
    }

    public String toString() {
        return directory.toString();
    }
}
