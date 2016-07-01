package com.maddogten.mtrack.information.show;

import java.io.File;
import java.io.Serializable;

/*
      Episode stores the information pertaining to each found episode.
 */

@SuppressWarnings({"ClassWithoutLogger", "SerializableClassInSecureContext", "DeserializableClassInSecureContext"})
public class Episode implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -6747075762839786092L;
    private final int episode;
    private final String episodeFilename;
    private final Boolean partOfDoubleEpisode;

    private int savedPlayTime;

    public Episode(final int episode, final String episodeFilename, final boolean partOfDoubleEpisode) {
        this.episode = episode;
        this.episodeFilename = episodeFilename;
        this.partOfDoubleEpisode = partOfDoubleEpisode;
        savedPlayTime = 0;
    }

    public int getEpisode() {
        return episode;
    }

    public String getEpisodeFilename() {
        return episodeFilename;
    }

    public String getEpisodeBareFilename() {
        return new File(episodeFilename).getName();
    }

    public boolean isPartOfDoubleEpisode() {
        return partOfDoubleEpisode;
    }

    public int getSavedPlayTime() {
        return savedPlayTime;
    }

    public void setSavedPlayTime(final int savedPlayTime) {
        this.savedPlayTime = savedPlayTime;
    }
}
