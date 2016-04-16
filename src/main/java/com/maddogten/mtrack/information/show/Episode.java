package com.maddogten.mtrack.information.show;

import java.io.*;

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

    public Episode(int episode, String episodeFilename, boolean partOfDoubleEpisode) {
        this.episode = episode;
        this.episodeFilename = episodeFilename;
        this.partOfDoubleEpisode = partOfDoubleEpisode;
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

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
