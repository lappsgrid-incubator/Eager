package org.lappsgrid.eager.mining.core

/**
 *
 */
class Utils {
    /**
     * Simple duration formating.
     * <p>
     * Using the Duration and Period classes from java.time or Joda time
     * to format the duration is just as much work and no better than simply applying
     * div and mod to the input value (at least for our use case).
     *
     * @param input the duration to format
     * @return A string in the format DDd HH:MM:SS.sss
     */
    static String format(long input) {
        long msec = input % 1000
        long seconds = input / 1000
        long minutes = seconds / 60
        seconds = seconds % 60
        long hours = minutes / 60
        minutes = minutes % 60
        // Only display the days if we need to.
        if (hours >= 24) {
            long days = hours / 24
            hours = hours % 24
            return String.format("%dd %02d:%02d:%02d.%03d", days, hours, minutes, seconds, msec);
        }
        return String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, msec);
    }


}
