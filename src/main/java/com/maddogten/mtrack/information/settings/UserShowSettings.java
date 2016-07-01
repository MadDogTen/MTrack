package com.maddogten.mtrack.information.settings;

import java.io.Serializable;

/*
      UserShowSettings stores each shows settings.
 */

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class UserShowSettings implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -1596102571442125890L;
    private final String showName;

    // Booleans
    private boolean active;
    private boolean ignored;
    private boolean hidden;

    // Position Information
    private int currentSeason;
    private int currentEpisode;

    @SuppressWarnings("FieldCanBeLocal")
    private int remaining = -2;

    public UserShowSettings(final String showName, final int currentSeason, final int currentEpisode) {
        this.showName = showName;
        active = false;
        ignored = false;
        hidden = false;
        this.currentSeason = currentSeason;
        this.currentEpisode = currentEpisode;
    }

    public UserShowSettings(final String showName, final boolean active, final boolean ignored, final boolean hidden, final int currentSeason, final int currentEpisode) {
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

    public void setActive(final boolean isActive) {
        this.active = isActive;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(final boolean isIgnored) {
        this.ignored = isIgnored;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(final boolean isHidden) {
        this.hidden = isHidden;
    }

    public int getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(final int currentSeason) {
        this.currentSeason = currentSeason;
    }

    public int getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(final int currentEpisode) {
        this.currentEpisode = currentEpisode;
    }

    /*public int getRemaining() {
        return remaining;
    }*/

    public void setRemaining(final int remaining) {
        this.remaining = remaining;
    }
}
