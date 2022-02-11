package com.silibrina.tecnova.commons.modules.route;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class represents a path pattern, checking if an income
 * request matches the given pattern and extracting the parameters given
 * in path according to a given pattern.
 */
public interface PathPattern extends Comparable<PathPattern> {

    /**
     * The pattern following play routes guide.
     *
     * @return the pattern
     */
    String rawPattern();

    /**
     * Formatted pattern compiled by the pattern mechanism. Usually, compiled by {@link Pattern}
     *
     * @return the pattern
     */
    Pattern pattern();

    /**
     * Checks if the given path matches the current path pattern.
     *
     * Ex: /user/:id checked against /user/23
     *
     * @param path a path to be checked against this pattern.
     * @return true if matches, false otherwise.
     */
    boolean matches(@Nonnull String path);

    /**
     * Extract the parameter of the given path based on the
     * path pattern.
     *
     * @return return a map with id provided in pattern with
     * the extracted value from the given path. With this example,
     * return should be:
     * &lt;'id', '23'&gt;
     */
    Map<String, String> parameters();
}