package ru.itis.mysqlant.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StringUtil {

    private static final Locale LOCALE = Locale.getDefault();
    private static final DecimalFormatSymbols FORMAT_SYMBOLS = new DecimalFormatSymbols(LOCALE);

    static {
        FORMAT_SYMBOLS.setDecimalSeparator('.');
    }

    public static DecimalFormat getDecimalFormatForTime() {
        DecimalFormat df = new DecimalFormat("#0.000", FORMAT_SYMBOLS);
        df.setGroupingUsed(false);

        return df;
    }

    public static DecimalFormat getDecimalFormatForTPS() {
        DecimalFormat df = new DecimalFormat("#0.0", FORMAT_SYMBOLS);
        df.setGroupingUsed(false);

        return df;
    }

    public static DecimalFormat getDecimalFormatForMemory() {
        DecimalFormat df = new DecimalFormat("#0.00", FORMAT_SYMBOLS);
        df.setGroupingUsed(false);

        return df;
    }

    public static String getFormattedTps(double tps) {
        return StringUtil.getDecimalFormatForTPS().format(tps);
    }

    public static String getFormattedTimeInSeconds(Long nanoseconds) {
        return StringUtil.getDecimalFormatForTime().format((double) nanoseconds / 1_000_000_000.0);
    }

    public static String getFormattedTimeInMillis(Long milliseconds) {
        return StringUtil.getDecimalFormatForTime().format(milliseconds);
    }

    public static String getFormattedNumber(double memory) {
        return StringUtil.getDecimalFormatForMemory().format(memory);
    }
}
