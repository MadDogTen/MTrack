package com.maddogten.mtrack.information.show;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class Season implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 8378503544871079547L;
    private final int season;
    private final Map<Integer, Episode> episodes;

    private int highestFoundEpisode;

    // TVMaze data
    private int numberOfEpisodes;

    public Season(int season, Map<Integer, Episode> episodes) {
        this.season = season;
        this.episodes = episodes;
        this.highestFoundEpisode = -1;
        this.numberOfEpisodes = -1;
    }

    public int getSeason() {
        return season;
    }

    public Episode getEpisode(int episode) {
        return episodes.get(episode);
    }

    public boolean containsEpisode(int episode) {
        return episodes.containsKey(episode);
    }

    public Map<Integer, Episode> getEpisodes() {
        return episodes;
    }

    public int getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(int numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public int getHighestFoundEpisode() {
        return highestFoundEpisode;
    }

    public void setHighestFoundEpisode(int highestFoundEpisode) {
        this.highestFoundEpisode = highestFoundEpisode;
    }
}
