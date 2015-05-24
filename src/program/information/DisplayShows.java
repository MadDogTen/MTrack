package program.information;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings({"unused", "ClassWithoutLogger"})
public class DisplayShows {
    private SimpleStringProperty show;
    private SimpleIntegerProperty remaining;

    public DisplayShows(String show, int remaining) {
        this.show = new SimpleStringProperty(show);
        this.remaining = new SimpleIntegerProperty(remaining);
    }

    public SimpleStringProperty showProperty() {
        return this.show;
    }

    public String getShow() {
        return show.get();
    }

    public void setShow(String show) {
        this.show = new SimpleStringProperty(show);
    }

    public SimpleIntegerProperty remainingProperty() {
        return this.remaining;
    }

    public Integer getRemaining() {
        return remaining.get();
    }

    public void setRemaining(Integer remaining) {
        this.remaining = new SimpleIntegerProperty(remaining);
    }
}
