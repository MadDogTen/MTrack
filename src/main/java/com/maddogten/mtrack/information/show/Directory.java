package com.maddogten.mtrack.information.show;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class Directory implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 8211794325592517516L;
    private final File directory;
    private final String fileName;
    private final int index;
    @SuppressWarnings({"FieldCanBeLocal", "CanBeFinal", "unused"})
    private int priority;
    private Map<String, Show> shows;
    private long lastProgramID;

    public Directory(File directory, String fileName, int index, int priority, Map<String, Show> shows, long lastProgramID) {
        this.directory = directory;
        this.fileName = fileName;
        this.index = index;
        this.priority = priority;
        this.shows = shows;
        this.lastProgramID = lastProgramID;
    }

    public File getDirectory() {
        return directory;
    }

    public String getFileName() {
        return fileName;
    }

    public int getIndex() {
        return index;
    }

    /*public int getPriority() { //TODO Implement Priority
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }*/

    public Map<String, Show> getShows() {
        return shows;
    }

    public void setShows(Map<String, Show> shows) {
        this.shows = shows;
    }

    public long getLastProgramID() {
        return lastProgramID;
    }

    public void setLastProgramID(long lastProgramID) {
        this.lastProgramID = lastProgramID;
    }
}
