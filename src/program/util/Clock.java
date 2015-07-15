package program.util;

import java.util.logging.Logger;

public class Clock {
    private final Logger log = Logger.getLogger(Clock.class.getName());

    @SuppressWarnings("WeakerAccess")
    public long getTimeNano() {
        log.finest("getTimeNano has been ran.");
        return System.nanoTime();
    }

    @SuppressWarnings("WeakerAccess")
    public long getTimeMilliSeconds() {
        log.finest("getTimeMilliSeconds has been ran.");
        return (System.nanoTime() / 1000000);
    }

    public int getTimeSeconds() {
        log.finest("getTimeSeconds has been ran.");
        return (int) (System.nanoTime() / 1000000000);
    }

    public long timeTakenNano(long timer) {
        log.finest("timeTakenNano has been ran.");
        return (getTimeNano() - timer);
    }

    public long timeTakenMilli(long timer) {
        log.finest("timeTakenMilli has been ran.");
        return (getTimeMilliSeconds() - timer);
    }

    public int timeTakenSeconds(int timer) {
        log.finest("timeTakenSeconds has been ran.");
        return getTimeSeconds() - timer;
    }
}
