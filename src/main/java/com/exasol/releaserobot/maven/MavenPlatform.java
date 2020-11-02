package com.exasol.releaserobot.maven;

import com.exasol.releaserobot.*;
import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.report.ValidationReport;

/**
 * This class controls Maven platform.
 */
public class MavenPlatform implements Platform {
    private final ReleaseMaker releaseMaker;
    private final PlatformValidator platformValidator;

    /**
     * Create a new instance of {@link MavenPlatform}.
     *
     * @param releaseMaker      instance of {@link ReleaseMaker}
     * @param platformValidator instance of {@link PlatformValidator]}
     */
    public MavenPlatform(final ReleaseMaker releaseMaker, final PlatformValidator platformValidator) {
        this.releaseMaker = releaseMaker;
        this.platformValidator = platformValidator;
    }

    @Override
    public void release(final UserInput userInput) throws GitHubException {
        this.releaseMaker.makeRelease();
    }

    @Override
    public PlatformName getPlatformName() {
        return PlatformName.MAVEN;
    }

    @Override
    public void validate(final ValidationReport validationReport) {
        this.platformValidator.validate(validationReport);
    }
}