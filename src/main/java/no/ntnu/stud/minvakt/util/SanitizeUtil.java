package no.ntnu.stud.minvakt.util;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Utility class for sanitizing user input
 */
public class SanitizeUtil {
    private static final PolicyFactory policy = new HtmlPolicyBuilder().allowElements("").toFactory();

    /**
     * Removes all HTML from the input string
     * @param input The input
     * @return A new string which is filtered/sanitized
     */
    public static String filterInput(String input) {
        return policy.sanitize(input).replace("&#64;", "@"); // Hack for allowing @
    }
}
