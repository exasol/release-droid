package com.exasol.platform;

import java.util.Set;

import com.exasol.*;

/**
 * This class instantiates a corresponding {@link ReleaseMaker}.
 */
public class ReleaseMakerFactory {
    private ReleaseMakerFactory() {
        // prevent instantiation
    }

    /**
     * Instantiate a {@link ReleaseMaker}. Currently this method always creates an instance of {@link ReleaseMakerJava}.
     * 
     * @param repositoryName name of a github repository
     * @param platformsList set of release platforms
     * @return a new instance of {@link ReleaseMaker}
     */
    public static ReleaseMaker getReleaseMaker(final String repositoryName, Set<ReleasePlatform> platformsList) {
        return new ReleaseMakerJava(repositoryName, platformsList);
    }
}