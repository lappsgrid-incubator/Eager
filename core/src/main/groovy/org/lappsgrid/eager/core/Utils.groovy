package org.lappsgrid.eager.core

/**
 *
 */
class Utils {
    static String format(long input) {
        long msec = input % 1000
        long seconds = input / 1000
        long minutes = seconds / 60
        seconds = seconds % 60
        long hours = minutes / 60
        minutes = minutes % 60
        return String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, msec);
    }


}
