package no.ntnu.stud.minvakt.util;

/**
 * Created by Audun on 11.01.2017.
 */
public class TravisUtil {
    private static Boolean travis;

    public static boolean isTravis() {
        if(travis == null)
            travis = System.getenv("TRAVIS") != null;

        return travis;
    }
}
