package com.maddogten.mtrack.information.show;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/*
      DisplayShow is used for the Controller Class. It stores the information that needs to be displayed
      on the tableView.
 */

@SuppressWarnings({"unused", "ClassWithoutLogger"})
public class DisplayShow {
    private final SimpleStringProperty show;
    private final SimpleIntegerProperty remaining;
    private final SimpleIntegerProperty season;
    private final SimpleIntegerProperty episode;
    private final SimpleIntegerProperty showID;

    public DisplayShow(final String show, final int remaining, final int season, final int episode, final int showID) {
        this.show = new SimpleStringProperty(show);
        this.remaining = new SimpleIntegerProperty(remaining);
        this.season = new SimpleIntegerProperty(season);
        this.episode = new SimpleIntegerProperty(episode);
        this.showID = new SimpleIntegerProperty(showID);
    }

    public SimpleStringProperty showProperty() {
        return this.show;
    }

    public String getShow() {
        return show.get();
    }

    public SimpleIntegerProperty remainingProperty() {
        return this.remaining;
    }

    public Integer getRemaining() {
        return remaining.get();
    }

    public void setRemaining(final Integer remaining) {
        if (!remaining.equals(this.getRemaining())) {
            this.remaining.setValue(remaining);
        }
    }

    public SimpleIntegerProperty seasonProperty() {
        return this.season;
    }

    public Integer getSeason() {
        return season.get();
    }

    public void setSeason(final Integer season) {
        this.season.setValue(season);
    }

    public SimpleIntegerProperty episodeProperty() {
        return this.episode;
    }

    public Integer getEpisode() {
        return episode.get();
    }

    public void setEpisode(final Integer episode) {
        this.episode.setValue(episode);
    }

    public SimpleIntegerProperty showIDProperty() {
        return this.showID;
    }

    public Integer getshowID() {
        return showID.get();
    }

    public void setshowID(final Integer showID) {
        this.showID.setValue(showID);
    }
}
