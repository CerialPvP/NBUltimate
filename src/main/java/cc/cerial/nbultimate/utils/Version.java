package cc.cerial.nbultimate.utils;

import javax.annotation.Nullable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Original code taken from <a href="https://github.com/SkriptLang/Skript/blob/master/src/main/java/ch/njol/skript/util/Version.java">Skript</a>.
 */
public class Version implements Serializable, Comparable<Version> {
    @Serial
    private final static long serialVersionUID = 8687040355286333293L;

    /**
     * Code taken from <a href="https://github.com/SkriptLang/Skript/blob/master/src/main/java/ch/njol/skript/util/Utils.java#L738-L753">Utils#parseInt of Skript</a>.<br><br>
     * Parses a number that was validated to be an integer but might still result in a {@link NumberFormatException} when parsed with {@link Integer#parseInt(String)} due to
     * overflow.
     * This method will return {@link Integer#MIN_VALUE} or {@link Integer#MAX_VALUE} respectively if that happens.
     *
     * @param s
     * @return The parsed integer, {@link Integer#MIN_VALUE} or {@link Integer#MAX_VALUE} respectively
     */
    private int parseInt(final String s) {
        assert s.matches("-?\\d+");
        try {
            return Integer.parseInt(s);
        } catch (final NumberFormatException e) {
            return s.startsWith("-") ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
    }

    private final Integer[] version = new Integer[3];
    /**
     * Everything after the version, e.g. "alpha", "b", "rc 1", "build 2314", "-SNAPSHOT" etc. or null if nothing.
     */
    @Nullable
    private final String postfix;

    public Version(int... version) {
        if (version.length < 1 || version.length > 3)
            throw new IllegalArgumentException("Versions must have a minimum of 2 and a maximum of 3 numbers (" + version.length + " numbers given)");
        for (int i = 0; i < version.length; i++)
            this.version[i] = version[i];
        postfix = null;
    }

    public Version(int major, int minor, @Nullable String postfix) {
        version[0] = major;
        version[1] = minor;
        this.postfix = postfix == null || postfix.isEmpty() ? null : postfix;
    }

    public final static Pattern versionPattern = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:-(.*))?");

    public Version(String version) {
        final Matcher m = versionPattern.matcher(version.trim());
        if (!m.matches())
            throw new IllegalArgumentException("'" + version + "' is not a valid version string");
        for (int i = 0; i < 3; i++) {
            if (m.group(i + 1) != null)
                //noinspection ConcatenationWithEmptyString
                this.version[i] = parseInt("" + m.group(i + 1));
        }
        postfix = m.group(4);
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Version))
            return false;
        return compareTo((Version) obj) == 0;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(version) * 31 + (postfix == null ? 0 : postfix.hashCode());
    }

    @Override
    public int compareTo(@Nullable Version other) {
        if (other == null)
            return 1;

        for (int i = 0; i < version.length; i++) {
            if (get(i) > other.get(i))
                return 1;
            if (get(i) < other.get(i))
                return -1;
        }

        if (postfix == null)
            return other.postfix == null ? 0 : 1;
        return other.postfix == null ? -1 : postfix.compareTo(other.postfix);
    }

    /**
     * @param other An array containing the major, minor, and revision (ex: 1,19,3)
     * @return a negative integer, zero, or a positive integer as this object is
     * less than, equal to, or greater than the specified object.
     */
    public int compareTo(int... other) {
        assert other.length >= 2 && other.length <= 3;
        for (int i = 0; i < version.length; i++) {
            if (get(i) > (i >= other.length ? 0 : other[i]))
                return 1;
            if (get(i) < (i >= other.length ? 0 : other[i]))
                return -1;
        }
        return 0;
    }

    private int get(int i) {
        return version[i] == null ? 0 : version[i];
    }

    public boolean isSmallerThan(final Version other) {
        return compareTo(other) < 0;
    }

    public boolean isLargerThan(final Version other) {
        return compareTo(other) > 0;
    }

    /**
     * @return Whether this is a stable version, i.e. a simple version number without any additional details (like alpha/beta/etc.)
     */
    public boolean isStable() {
        return postfix == null;
    }

    public int getMajor() {
        return version[0];
    }

    public int getMinor() {
        return version[1];
    }

    public int getRevision() {
        return version[2] == null ? 0 : version[2];
    }

    @Override
    public String toString() {
        return version[0] + "." + version[1] + (version[2] == null ? "" : "." + version[2]) + (postfix == null ? "" : "-" + postfix);
    }

    public static int compare(final String v1, final String v2) {
        return new Version(v1).compareTo(new Version(v2));
    }
}