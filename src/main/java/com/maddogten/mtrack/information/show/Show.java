package main.java.com.maddogten.mtrack.information.show;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class Show implements Serializable {

    private static final long serialVersionUID = 3665902196971811148L;
    private final String name;
    private Map<Integer, Season> seasons;

    public Show(String name, Map<Integer, Season> seasons) {
        this.name = name;
        this.seasons = seasons;
    }

    public String getName() {
        return name;
    }

    public Map<Integer, Season> getSeasons() {
        return seasons;
    }

    public Season getSeason(int season) {
        return seasons.get(season);
    }

    public boolean containsSeason(int season) {
        return seasons.containsKey(season);
    }

    public void addOrReplaceSeason(int seasonInt, Season season) {
        if (seasons.containsKey(seasonInt)) {
            seasons.replace(seasonInt, season);
        } else {
            seasons.put(seasonInt, season);
        }
    }

    public void removeSeason(int seasonInt) {
        if (seasons.containsKey(seasonInt)) {
            seasons.remove(seasonInt);
        }
    }
}
