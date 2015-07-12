package program.information;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class Season implements Serializable {

    private final int season;
    private Map<Integer, Episode> episodes;

    public Season(int season, Map<Integer, Episode> episodes) {
        this.season = season;
        this.episodes = episodes;
    }

    public int getSeason() {
        return season;
    }

    public Map<Integer, Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(Map<Integer, Episode> episodes) {
        this.episodes = episodes;
    }

    public String getEpisode(int episode) {
        return episodes.get(episode).getEpisodeFilename();
    }

    public void addOrReplaceEpisode(int episodeInt, Episode episode) {
        if (episodes.containsKey(episodeInt)) {
            episodes.replace(episodeInt, episode);
        } else {
            episodes.put(episodeInt, episode);
        }
    }

    public void removeEpisode(int episodeInt) {
        if (episodes.containsKey(episodeInt)) {
            episodes.remove(episodeInt);
        }
    }

}
