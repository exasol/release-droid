package com.exasol.releaserobot.release;

import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.releaserobot.MavenPlatform;
import com.exasol.releaserobot.report.ReleaseReport;

/**
 * This class is responsible for releases on Maven Central.
 */
public class MavenReleaseMaker implements ReleaseMaker {
    private static final Logger LOGGER = Logger.getLogger(MavenReleaseMaker.class.getName());
    private final MavenPlatform platform;
    private final ReleaseReport releaseReport;

    /**
     * Create a new instance of {@link MavenReleaseMaker}.
     * 
     * @param platform      instance of {@link MavenPlatform}
     * @param releaseReport instance of {@link ReleaseReport}
     */
    public MavenReleaseMaker(final MavenPlatform platform, final ReleaseReport releaseReport) {
        this.platform = platform;
        this.releaseReport = releaseReport;
    }

    @Override
    public boolean makeRelease() {
        LOGGER.fine("Releasing on Maven.");
        try {
            this.platform.makeNewMavenRelease();
            this.releaseReport.addSuccessfulRelease(this.platform.getPlatformName());
            return true;
        } catch (final RuntimeException runtimeException) {
            this.releaseReport.addFailedRelease(this.platform.getPlatformName(),
                    ExceptionUtils.getStackTrace(runtimeException));
            return false;
        }
    }
}