package de.loggi.util;

/**
 * @author CptSpaetzle
 */
public class StringUtils {

    public static String createProgressBar(long done, long total) {
        int scale = 20;
        StringBuilder builder = new StringBuilder("\r");
        int progress = Math.round(((float) done / (float) total) * scale);
        builder.append("[").append(repeatString("=", progress - 1));
        builder.append(">").append(repeatString(" ", scale - progress));
        builder.append("] ").append(done).append("/").append(total);
        return builder.toString();
    }

    public static String repeatString(String val, int count) {
        StringBuilder repeat = new StringBuilder();
        for (int i = 0; i < count; i++) {
            repeat.append(val);
        }
        return repeat.toString();
    }
}
