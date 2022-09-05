package com.exasol.releasedroid.usecases.repository.version;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Version implements Comparable<Version> {

    private static final String SUFFIX = "(v?)(\\d+(\\.\\d+)*+)";
    private static final Pattern PATTERN = Pattern.compile(SUFFIX);
    private static final Pattern GIT_TAG = Pattern.compile(org.eclipse.jgit.lib.Constants.R_TAGS + "(.*/)?" + SUFFIX);
    private static final int COMPONENTS = 3;
    private static final int LESS = -1;
    private static final int EQUAL = 0;
    private static final int GREATER = 1;

    public static Version fromGitTag(final String tag) {
        final Matcher matcher = GIT_TAG.matcher(tag);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Failed reading version from git tag '" + tag + "'.");
        }
        return parse(matcher.group(1), matcher.group(2) + matcher.group(3));
    }

    public static Version parse(final String string) {
        return parse("", string);
    }

    static Version parse(final String subfolder, final String string) {
        final Matcher matcher = PATTERN.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Illegal version format: '" + string + "'");
        }

        final int[] numbers = Arrays.stream(matcher.group(2).split("\\.")).mapToInt(Integer::parseInt).toArray();
        if (numbers.length != COMPONENTS) {
            throw new IllegalArgumentException("Illegal version format: '" + string //
                    + "'. Expected " + COMPONENTS + " components separated by dots.");
        }
        return new Version(subfolder == null ? "" : subfolder, matcher.group(1), numbers);
    }

    private final String subfolder;
    private final String prefix;
    private final int[] numbers;

    /**
     * @param subfolder optional subfolder, used for golang modules in subfolders
     * @param prefix    optional prefix "v"
     * @param numbers   major, minor, fix
     */
    public Version(final String subfolder, final String prefix, final int... numbers) {
        this.subfolder = subfolder;
        this.prefix = prefix;
        this.numbers = numbers;
    }

    @Override
    public String toString() {
        return this.subfolder + this.prefix + Arrays.stream(this.numbers) //
                .mapToObj(String::valueOf) //
                .collect(Collectors.joining("."));
    }

    /**
     * @return set of versions that are potential successors of the current version
     */
    public Set<Version> potentialSuccessors() {
        final Set<Version> result = new HashSet<>();
        for (int level = 0; level < this.numbers.length; level++) {
            final int[] successor = successorNumber(level);
            result.add(new Version("", "", successor));
        }
        return result;
    }

    /**
     * @param other other version to evaluate as potential successor
     * @return {@code true} if version {@code other} is a valid successor to the current version
     */
    public boolean acceptsSuccessor(final Version other) {
        for (int level = 0; level < this.numbers.length; level++) {
            final int[] successor = successorNumber(level);
            if (Arrays.equals(successor, other.numbers)) {
                return true;
            }
        }
        return false;
    }

    private int[] successorNumber(final int level) {
        final int[] successor = new int[this.numbers.length];
        for (int i = 0; i < this.numbers.length; i++) {
            successor[i] = digit(level, i, this.numbers[i]);
        }
        return successor;
    }

    private int digit(final int level, final int i, final int old) {
        if (i < level) {
            return old;
        }
        if (i == level) {
            return old + 1;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + Arrays.hashCode(this.numbers);
        result = (prime * result) + Objects.hash(this.prefix, this.subfolder);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Version other = (Version) obj;
        return Arrays.equals(this.numbers, other.numbers) && Objects.equals(this.prefix, other.prefix)
                && Objects.equals(this.subfolder, other.subfolder);
    }

    @Override
    public int compareTo(final Version other) {
        for (int i = 0; i < this.numbers.length; i++) {
            final int result = compare(i, other);
            if (differs(result)) {
                return result;
            }
        }
        if (this.numbers.length < other.numbers.length) {
            return LESS;
        }
        return this.prefix.compareTo(other.prefix);
    }

    /**
     * @param other other version to compare this version to
     * @return {@code true} if this version is greater or equal than the other one
     */
    public boolean isGreaterOrEqualThan(final Version other) {
        return compareTo(other) > LESS;
    }

    private int compare(final int i, final Version other) {
        if (i >= other.numbers.length) {
            return GREATER;
        }
        return Integer.compare(this.numbers[i], other.numbers[i]);
    }

    private boolean differs(final int result) {
        return result != EQUAL;
    }

}
