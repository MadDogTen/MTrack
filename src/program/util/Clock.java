package program.util;

public class Clock {

    public static long getTimeNano() {
        return System.nanoTime();
    }

    public static int getTimeSeconds() {
        return (int) (System.nanoTime() / 1000000000);
    }

    public static long timeTakenNano(long timer) {
        return (getTimeNano() - timer);
    }

    public static int timeTakenSeconds(int timer) {
        return getTimeSeconds() - timer;
    }
}
