package no.ntnu.stud.minvakt.util;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Created by Audun on 27.01.2017.
 */
public class SanitizeUtil {
    private static final PolicyFactory policy = new HtmlPolicyBuilder().allowElements("").toFactory();

    public static String filterInput(String input) {
        return policy.sanitize(input).replace("&#64;", "@"); // Hack for allowing @
    }
}
