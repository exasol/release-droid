package com.exasol.releasedroid.adapter.jira;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_STATE_DIRECTORY;

import java.util.Map;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.release.ReleaseState;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.ReleasePlatformValidator;

/**
 * A validator for Jira platform.
 */
public class JiraPlatformValidator implements ReleasePlatformValidator {
    private final ReleaseState releaseState = new ReleaseState(RELEASE_DROID_STATE_DIRECTORY);
    private final Repository repository;

    /**
     * Create a new instance of {@link JiraPlatformValidator}.
     *
     * @param repository repository
     */
    public JiraPlatformValidator(final Repository repository) {
        this.repository = repository;
    }

    @Override
    public Report validate() {
        final var report = ValidationReport.create();
        final Map<PlatformName, String> progress = this.releaseState.getProgress(this.repository.getName(),
                this.repository.getVersion());
        report.merge(validateCommunityRelease(progress));
        return report;
    }

    protected Report validateCommunityRelease(final Map<PlatformName, String> progress) {
        final var report = ValidationReport.create();
        final Report communityReleaseReport = validateCommunityReleaseExists(progress);
        report.merge(communityReleaseReport);
        if (!communityReleaseReport.hasFailures()) {
            report.merge(validateCommunityReleaseHasOutput(progress));
        }
        return report;

    }

    private Report validateCommunityReleaseExists(final Map<PlatformName, String> progress) {
        final var report = ValidationReport.create();
        if (progress.containsKey(PlatformName.COMMUNITY)) {
            report.addSuccessfulResult("Community release was made.");
        } else {
            report.addFailedResult(
                    ExaError.messageBuilder("E-RD-JIRA-1").message("Community release is missing.").toString());
        }
        return report;
    }

    private Report validateCommunityReleaseHasOutput(final Map<PlatformName, String> progress) {
        final var report = ValidationReport.create();
        final String output = progress.get(PlatformName.COMMUNITY);
        if (output == null || output.isEmpty()) {
            report.addFailedResult(ExaError.messageBuilder("E-RD-JIRA-2")
                    .message("Community release output is not found.").toString());
        } else {
            report.addSuccessfulResult("Community release output is found.");
        }
        return report;
    }
}
