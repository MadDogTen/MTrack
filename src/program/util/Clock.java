package program.util;

import java.util.logging.Logger;

public class Clock {
    private static final Logger log = Logger.getLogger(Clock.class.getName());

    @SuppressWarnings("WeakerAccess")
    public static long getTimeNano() {
        log.finest("getTimeNano has been ran.");
        return System.nanoTime();
    }

    @SuppressWarnings("WeakerAccess")
    public static long getTimeMilliSeconds() {
        log.finest("getTimeMilliSeconds has been ran.");
        return (System.nanoTime() / 1000000);
    }

    public static int getTimeSeconds() {
        log.finest("getTimeSeconds has been ran.");
        return (int) (System.nanoTime() / 1000000000);
    }

    public static long timeTakenNano(long timer) {
        log.finest("timeTakenNano has been ran.");
        return (getTimeNano() - timer);
    }

    public static long timeTakenMilli(long timer) {
        log.finest("timeTakenMilli has been ran.");
        return (getTimeMilliSeconds() - timer);
    }

    public static int timeTakenSeconds(int timer) {
        log.finest("timeTakenSeconds has been ran.");
        return getTimeSeconds() - timer;
    }
}
