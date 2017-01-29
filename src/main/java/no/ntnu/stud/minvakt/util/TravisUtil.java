package no.ntnu.stud.minvakt.util;

/**
 * Utility class for detecting if Travis CI is running
 */
public class TravisUtil {
    private static Boolean travis;

    /**
     * Detects if Travis CI is running
     * @return True if Travis CI is running
     */
    public static boolean isTravis() {
        if(travis == null)
            travis = System.getenv("TRAVIS") != null;

        return travis;
    }
}
