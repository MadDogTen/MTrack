package main.java.com.maddogten.mtrack.information.show;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class Season implements Serializable {

    private static final long serialVersionUID = 8378503544871079547L;
    private final int season;
    private final Map<Integer, Episode> episodes;

    public Season(int season, Map<Integer, Episode> episodes) {
        this.season = season;
        this.episodes = episodes;
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

}
