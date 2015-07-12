package program.information;

import java.io.Serializable;

@SuppressWarnings({"ClassWithoutLogger", "SerializableClassInSecureContext", "DeserializableClassInSecureContext"})
public class Episode implements Serializable {

    private final int episode;
    private final String episodeFilename;

    public Episode(int episode, String episodeFilename) {
        this.episode = episode;
        this.episodeFilename = episodeFilename;
    }

    public int getEpisode() {
        return episode;
    }

    public String getEpisodeFilename() {
        return episodeFilename;
    }
}
