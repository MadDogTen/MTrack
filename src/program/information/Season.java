package program.information;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class Season implements Serializable {

    private final int season;
    private final Map<Integer, Episode> episodes;

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

}
