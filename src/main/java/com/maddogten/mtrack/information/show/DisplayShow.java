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

    public DisplayShow(String show, int remaining, int season, int episode) {
        this.show = new SimpleStringProperty(show);
        this.remaining = new SimpleIntegerProperty(remaining);
        this.season = new SimpleIntegerProperty(season);
        this.episode = new SimpleIntegerProperty(episode);
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

    public void setRemaining(Integer remaining) {
        this.remaining.setValue(remaining);
    }

    public SimpleIntegerProperty seasonProperty() {
        return this.season;
    }

    public Integer getSeason() {
        return season.get();
    }

    public void setSeason(Integer season) {
        this.season.setValue(season);
    }

    public SimpleIntegerProperty episodeProperty() {
        return this.episode;
    }

    public Integer getEpisode() {
        return episode.get();
    }

    public void setEpisode(Integer episode) {
        this.episode.setValue(episode);
    }
}
