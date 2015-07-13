package program.information;

import java.io.Serializable;

@SuppressWarnings({"ClassWithoutLogger", "SerializableClassInSecureContext", "DeserializableClassInSecureContext"})
public class Episode implements Serializable {

    private final int episode;
    private final String episodeFilename;
    private final Boolean partOfDoubleEpisode;

    public Episode(int episode, String episodeFilename, Boolean partOfDoubleEpisode) {
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

    public Boolean getPartOfDoubleEpisode() {
        return partOfDoubleEpisode;
    }
}
