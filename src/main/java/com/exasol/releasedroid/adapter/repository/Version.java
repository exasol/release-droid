package com.exasol.releasedroid.adapter.repository;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Version implements Comparable<Version> {

    private static final Pattern PATTERN = Pattern.compile("(v?)(\\d+(\\.\\d+)*+)");
    private static final int COMPONENTS = 3;
    private static final int LESS = -1;
    private static final int EQUAL = 0;
    private static final int GREATER = 1;

    public static Version parse(final String string) {
        final Matcher matcher = PATTERN.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Illegal version format: '" + string + "'");
        }
        final int[] numbers = Arrays.stream(matcher.group(2).split("\\.")).mapToInt(Integer::parseInt).toArray();
        if (numbers.length != COMPONENTS) {
            throw new IllegalArgumentException("Illegal version format: '" + string //
                    + "'. Expected " + COMPONENTS + " components separated by dots.");
        }
        return new Version(matcher.group(1), numbers);
    }

    private final String prefix;
    private final int[] numbers;

    /**
     * @param prefix  optional prefix "v"
     * @param numbers major, minor, fix
     */
    Version(final String prefix, final int... numbers) {
        this.prefix = prefix;
        this.numbers = numbers;
    }

    @Override
    public String toString() {
        return this.prefix + Arrays.stream(this.numbers) //
                .mapToObj(String::valueOf) //
                .collect(Collectors.joining("."));
    }

    Set<Version> potentialSuccessors() {
        final Set<Version> result = new HashSet<>();
        for (int level = 0; level < this.numbers.length; level++) {
            final int[] successor = successorNumber(level);
            result.add(new Version("", successor));
            result.add(new Version("v", successor));
        }
        return result;
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
        result = (prime * result) + Objects.hash(this.prefix);
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
        return Arrays.equals(this.numbers, other.numbers) && Objects.equals(this.prefix, other.prefix);
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
