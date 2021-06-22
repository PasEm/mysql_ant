package ru.itis.mysqlant.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class TimeUtil {

    @Getter
    @RequiredArgsConstructor
    private enum PicoTimeUnit {
        PICOSECONDS("ps", 10e-9),
        NANOSECONDS("ns", 10e-6),
        MICROSECONDS("us", 10e-3),
        MILLISECONDS("ms", 1),
        SECONDS("s", 10e3),
        MINUTES("min", 60 * 10e3),
        HOURS("h", 3600 * 10e3),
        DAYS("d", 24 * 3600 * 10e3);

        private final String name;
        private final double value;
    }

    public static long getMillisecondsFromPicoTime(String picoTime) {
        String timeUnit = picoTime.substring(picoTime.indexOf(" ") + 1);
        double timeValue = Double.parseDouble(picoTime.substring(0, picoTime.indexOf(" ")));

        PicoTimeUnit picoTimeUnit = PicoTimeUnit.valueOf(timeUnit);

        return Math.round(timeValue * picoTimeUnit.getValue());
    }

    public static long getMilliseconds(double picoseconds) {
        return Math.round(picoseconds / 1_000_000_000);
    }

    public static long getSecondsFromNanoseconds(double nanoseconds) {
        return Math.round(nanoseconds / 1_000_000_000);
    }

    public static long getNanosecondsFromSeconds(long seconds) {
        return seconds * 1_000_000_000;
    }

    public static long getNanosecondsFromMilliseconds(long milliseconds) {
        return milliseconds * 1_000_000;
    }
}
