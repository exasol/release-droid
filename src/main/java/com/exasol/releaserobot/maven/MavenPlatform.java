package com.exasol.releaserobot.maven;

import com.exasol.releaserobot.*;
import com.exasol.releaserobot.github.GitHubException;

/**
 * This class controls Maven platform.
 */
public class MavenPlatform implements Platform {
    private final ReleaseMaker releaseMaker;

    /**
     * Create a new instance of {@link MavenPlatform}.
     *
     * @param releaseMaker instance of {@link ReleaseMaker}
     */
    public MavenPlatform(final ReleaseMaker releaseMaker) {
        this.releaseMaker = releaseMaker;
    }

    @Override
    public void release(final UserInput userInput) throws GitHubException {
        this.releaseMaker.makeRelease();
    }

    @Override
    public PlatformName getPlatformName() {
        return PlatformName.MAVEN;
    }
}