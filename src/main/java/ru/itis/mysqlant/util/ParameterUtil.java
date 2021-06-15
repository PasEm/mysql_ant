package ru.itis.mysqlant.util;

public class ParameterUtil {

    public static int getNextStepValue(int now, int step, int max) {
        if (now == max) {
            return max + 1;
        }
        return Math.min(now + step, max);
    }

    public static long getNextStepValue(long now, long step, long max) {
        if (now == max) {
            return max + 1;
        }
        return Math.min(now + step, max);
    }
}
