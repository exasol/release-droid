package com.exasol.releasedroid.usecases.request;

import java.util.*;

public class PlatformPortfolio {

    public static PlatformPortfolio from(final Collection<PlatformName> all, final Collection<PlatformName> released) {
        final Set<PlatformName> unreleased = new HashSet<>(all);
        unreleased.removeAll(released);
        return new PlatformPortfolio(released, unreleased);
    }

    private final Collection<PlatformName> released;
    private final Collection<PlatformName> unreleased;

    /**
     * @param released   list of platforms that already have been released successfully in a former session
     * @param unreleased list of platforms, still waiting to be released
     */
    public PlatformPortfolio(final Collection<PlatformName> released, final Collection<PlatformName> unreleased) {
        this.released = released;
        this.unreleased = unreleased;
    }

    /**
     * @return {@code true} if there are more platforms, waiting to be released
     */
    public boolean hasUnreleased() {
        return !this.unreleased.isEmpty();
    }

    /**
     * @return list of platforms that already have been released successfully in a former session
     */
    public Collection<PlatformName> getReleased() {
        return this.released;
    }

    /**
     * @return list of platforms, still waiting to be released
     */
    public Collection<PlatformName> getUnreleased() {
        return this.unreleased;
    }
}
