package com.maddogten.mtrack.information.settings;

import java.io.Serializable;

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class UserShowSettings implements Serializable {

    private static final long serialVersionUID = -1596102571442125890L;
    private final String showName;

    // Booleans
    private boolean active;
    private boolean ignored;
    private boolean hidden;

    // Position Information
    private int currentSeason;
    private int currentEpisode;

    public UserShowSettings(String showName, int currentSeason, int currentEpisode) {
        this.showName = showName;
        active = false;
        ignored = false;
        hidden = false;
        this.currentSeason = currentSeason;
        this.currentEpisode = currentEpisode;
    }

    public UserShowSettings(String showName, boolean active, boolean ignored, boolean hidden, int currentSeason, int currentEpisode) {
        this.showName = showName;
        this.active = active;
        this.ignored = ignored;
        this.hidden = hidden;
        this.currentSeason = currentSeason;
        this.currentEpisode = currentEpisode;
    }

    // Basic Getters and Setters

    public String getShowName() {
        return showName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean isActive) {
        this.active = isActive;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean isIgnored) {
        this.ignored = isIgnored;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean isHidden) {
        this.hidden = isHidden;
    }

    public int getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(int currentSeason) {
        this.currentSeason = currentSeason;
    }

    public int getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(int currentEpisode) {
        this.currentEpisode = currentEpisode;
    }
}
