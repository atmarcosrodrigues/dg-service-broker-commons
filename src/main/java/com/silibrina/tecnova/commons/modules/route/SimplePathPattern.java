package com.silibrina.tecnova.commons.modules.route;

import com.silibrina.tecnova.commons.exceptions.InvalidConditionException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.silibrina.tecnova.commons.utils.Preconditions.checkCondition;
import static com.silibrina.tecnova.commons.utils.Preconditions.checkNotNullCondition;

/**
 * This class implements a route. It verifies if a given path matches against a previous
 * pattern, if it does, it makes possible to extract the given parameters.
 * We are following this guide: https://www.playframework.com/documentation/2.4.x/JavaRouting
 */
class SimplePathPattern implements PathPattern {
    private static final Pattern SINGLE_DYNAMIC_PATTERN = Pattern.compile("^:(?<key>[^:]+)");
    private static final Pattern MULTI_DYNAMIC_PATTERN = Pattern.compile("^\\*(?<key>[^\\*]+)");
    private static final Pattern CUSTOM_DYNAMIC_PATTERN = Pattern.compile("(?:\\$)(?<key>[^\\$]+)<(?<value>.+)>");

    private final String rawPattern;
    private final Pattern pattern;
    private final List<String> parameterKeys;

    private Matcher matcher;

    SimplePathPattern(final String rawPattern) {
        checkNotNullCondition("A path pattern must not be null", rawPattern);

        this.rawPattern = rawPattern;
        this.parameterKeys = new LinkedList<>();

        pattern = Pattern.compile(generatePattern(rawPattern, parameterKeys));
    }

    /**
     * This method will substitute simple route expressions by real regex.
     * It will also populate the list with the key to access the value during
     * a request.
     * E.g:
     *
     * /somethign/:id -> /something/(?<id>[^/]+)
     *
     * The regex pattern that can be interpreted pra {@link Pattern} and {@link Matcher}.
     * @param rawPatter The url with patterns to convert to java regex.
     * @param keys A list of parameters to be accessed from the url.
     *                   This parameter is the value that matches the regex.
     * @return the raw regex pattern converted to an understandable regex pattern.
     */
    private String generatePattern(@Nonnull final String rawPatter,
                                   @Nonnull final List<String> keys) {
        CharSequence[] splitRawPattern = rawPatter.trim().split("/");
        keys.clear();

        Matcher matcher = Pattern.compile("(.+)").matcher(rawPatter);
        for (int i = 0; i < splitRawPattern.length; i++ ) {
            CharSequence token = splitRawPattern[i];
            matcher.reset(token);

            if (isSingleDynamic(matcher)) {
                splitRawPattern[i] = generateSimpleIdRegex(keys, matcher);
            } else if (isMultiDynamic(matcher)) {
                splitRawPattern[i] = generateMultiDynamicRegex(keys, matcher);
            } else if (isCustomRegex(matcher)) {
                splitRawPattern[i] = generateCustomRegex(keys, matcher);
            }
        }

        return String.join("/", splitRawPattern);
    }

    /**
     * Checks if token starts with :, which characterizes a single dynamic part.
     * E.g.
     *    :key -> is an id for a value
     * @param matcher a matcher containing already a string to be checked.
     * @return true if it starts with : and has no other :, false otherwise.
     */
    private boolean isSingleDynamic(Matcher matcher) {
        matcher.usePattern(SINGLE_DYNAMIC_PATTERN);
        return matcher.matches();
    }

    private String generateSimpleIdRegex(List<String> keys, Matcher matcher) {
        String key = matcher.group("key");
        checkKeyName(key);
        keys.add(key);
        return "(?<" + key + ">[^/]+)";
    }

    /**
     * Checks if token starts with *, which characterizes a several dynamic part.
     * E.g.
     *    *key -> is an id for a value
     * @param matcher a matcher containing already a string to be checked.
     * @return true if it starts with * and has no other *, false otherwise.
     */
    private boolean isMultiDynamic(Matcher matcher) {
        matcher.usePattern(MULTI_DYNAMIC_PATTERN);
        return matcher.matches();
    }

    private String generateMultiDynamicRegex(List<String> keys, Matcher matcher) {
        String key = matcher.group("key");
        checkKeyName(key);
        keys.add(key);
        return "(?<" + key + ">.+)";
    }

    /**
     *
     * Checks if token starts with $, followed by a string (without $), followed
     * by any string.
     * E.g.
     *    $key<regex>
     *
     * @param matcher a matcher containing already a string to be checked.
     * @return true if matches a user custom regex, false otherwise.
     */
    private boolean isCustomRegex(Matcher matcher) {
        matcher.usePattern(CUSTOM_DYNAMIC_PATTERN);
        return matcher.matches();
    }

    private String generateCustomRegex(List<String> keys, Matcher matcher) {
        String key = matcher.group("key");
        checkKeyName(key);
        String regex = matcher.group("value");
        keys.add(key);

        return "(?<" + key + ">" + regex + ")";
    }

    /**
     * This method should validate parameter name against valid regex group names.
     * @param key the key name to validade
     */
    private void checkKeyName(String key) throws InvalidConditionException {
        checkCondition("id must contain only alphanumeric characters", StringUtils.isAlphanumeric(key));
    }

    /**
     * Removes duplicates+ slashes (meaning that // or ///... becomes /) and removes
     * ending slash.
     *
     * @param url url to fix (format).
     * @return a formatted url.
     */
    private String formatPath(@Nonnull final String url) {
        String newUrl = url.replaceAll("/[/]+", "/");
        if (newUrl.endsWith("/") && newUrl.length() > 1) {
            newUrl = newUrl.substring(0, newUrl.length() - 1);
        }
        return newUrl;
    }

    @Override
    public String rawPattern() {
        return rawPattern;
    }

    @Override
    public Pattern pattern() {
        return pattern;
    }

    @Override
    public boolean matches(@Nonnull String path) {
        matcher = pattern.matcher(formatPath(path));
        return matcher.matches();
    }

    @Override
    public Map<String, String> parameters() {
        checkNotNullCondition("Matcher is null. You must run matches before extracting parameters", matcher);
        checkCondition("Path should match the given route", matcher.matches());
        checkCondition("Parameters keys mismatch found patterns", matcher.groupCount() == parameterKeys.size());

        Map<String, String> parameters = new HashMap<>(parameterKeys.size());

        for (String key : parameterKeys) {
            parameters.put(key, matcher.group(key));
        }

        return parameters;
    }

    @Override
    public int hashCode() {
        return rawPattern().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof  SimplePathPattern)) return false;

        SimplePathPattern other = (SimplePathPattern) obj;
        return other.rawPattern().equals(rawPattern());
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%s [rawPattern: %s, pattern: %s]",
                this.getClass().getSimpleName(), rawPattern, pattern);
    }

    @Override
    public int compareTo(@Nonnull PathPattern o) {

        return rawPattern().compareTo(o.rawPattern());
    }
}