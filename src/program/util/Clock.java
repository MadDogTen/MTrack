package program.util;

import java.util.logging.Logger;

public class Clock {
    private static final Logger log = Logger.getLogger(Clock.class.getName());

    public static long getTimeNano() {
        return System.nanoTime();
    }

    public static long getTimeMilliSeconds() {
        return (System.nanoTime() / 1000000);
    }

    public static int getTimeSeconds() {
        return (int) (System.nanoTime() / 1000000000);
    }

    public static long timeTakenNano(long timer) {
        return (getTimeNano() - timer);
    }

    public static long timeTakenMilli(long timer) {
        return (getTimeMilliSeconds() - timer);
    }

    public static int timeTakenSeconds(int timer) {
        return getTimeSeconds() - timer;
    }
}
