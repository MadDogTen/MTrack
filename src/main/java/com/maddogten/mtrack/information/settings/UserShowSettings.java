package com.maddogten.mtrack.information.settings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private int remaining = -2;

    private Map<Integer, SavedInformation> savedInformationMap;

    public UserShowSettings(final String showName, final int currentSeason, final int currentEpisode) {
        this.showName = showName;
        active = false;
        ignored = false;
        hidden = false;
        this.currentSeason = currentSeason;
        this.currentEpisode = currentEpisode;
        savedInformationMap = new HashMap<>();
    }

    public UserShowSettings(final String showName, final boolean active, final boolean ignored, final boolean hidden, final int currentSeason, final int currentEpisode) {
        this.showName = showName;
        this.active = active;
        this.ignored = ignored;
        this.hidden = hidden;
        this.currentSeason = currentSeason;
        this.currentEpisode = currentEpisode;
        savedInformationMap = new HashMap<>();
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

    public void addEpisodeTimePosition(int season, int episode, int timePosition) {
        createSavedInformationMapIfNull();
        if (!savedInformationMap.containsKey(season)) savedInformationMap.put(season, new SavedInformation());
        savedInformationMap.get(season).addEpisodePosition(episode, timePosition);
    }

    public void removeEpisodeTimePosition(int season, int episode) {
        createSavedInformationMapIfNull();
        if (savedInformationMap.containsKey(season)) savedInformationMap.get(season).removeEpisode(episode);
    }

    public int getEpisodePosition(int season, int episode) {
        createSavedInformationMapIfNull();
        return savedInformationMap.containsKey(season) ? savedInformationMap.get(season).getEpisodePosition(episode) : 0;
    }

    private void createSavedInformationMapIfNull() {
        if (savedInformationMap == null) savedInformationMap = new HashMap<>();
    }


    private class SavedInformation implements Serializable {
        private static final long serialVersionUID = 8456648000944054454L;
        private final Map<Integer, EpisodeInfo> episodeInfo;

        SavedInformation() {
            this.episodeInfo = new HashMap<>();
        }

        void addEpisodePosition(int episode, int position) {
            if (!episodeInfo.containsKey(episode)) episodeInfo.put(episode, new EpisodeInfo(position));
            else episodeInfo.get(episode).setPosition(position);
        }

        int getEpisodePosition(int episode) {
            return episodeInfo.containsKey(episode) ? episodeInfo.get(episode).getPosition() : 0;
        }

        void removeEpisode(int episode) {
            if (episodeInfo.containsKey(episode)) episodeInfo.remove(episode);
        }

        private class EpisodeInfo implements Serializable {
            private static final long serialVersionUID = -2659853882202221187L;
            private int position;

            EpisodeInfo(int position) {
                this.position = position;
            }

            int getPosition() {
                return position;
            }

            void setPosition(int position) {
                this.position = position;
            }
        }
    }


}
