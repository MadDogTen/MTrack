package com.maddogten.mtrack.information.show;

import java.io.Serializable;
import java.util.Map;

/*
      Show stores the information pertaining to each show, Including a Map
      that stores the Season Number and Season itself.
 */

@SuppressWarnings({"ClassWithoutLogger", "DeserializableClassInSecureContext", "SerializableClassInSecureContext"})
public class Show implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 3665902196971811148L;
    private final String name;
    @SuppressWarnings({"CanBeFinal", "FieldMayBeFinal"})
    private Map<Integer, Season> seasons;

    // TVMaze data
    private int showID;
    private int numberOfSeasons;

    public Show(String name, Map<Integer, Season> seasons) {
        this.name = name;
        this.seasons = seasons;
        this.showID = -1;
        this.numberOfSeasons = -1;
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

    public boolean isShowData() {
        return showID != -1 && numberOfSeasons != -1;
    }

    public int getShowID() {
        return showID;
    }

    public void setShowID(int showID) {
        this.showID = showID;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }
}
