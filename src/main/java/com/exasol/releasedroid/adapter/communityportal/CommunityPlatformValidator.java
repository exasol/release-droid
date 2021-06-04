package com.exasol.releasedroid.adapter.communityportal;

import static com.exasol.releasedroid.adapter.communityportal.CommunityPortalConstants.COMMUNITY_CONFIG_PATH;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_CONFIG_PATH;

import java.util.Optional;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.validate.ReleasePlatformValidator;

/**
 * Contains validations for releasing on the Community Portal.
 */
public class CommunityPlatformValidator implements ReleasePlatformValidator {
    private final Repository repository;

    /**
     * Create a new instance of {@link CommunityPlatformValidator}.
     *
     * @param repository repository
     */
    public CommunityPlatformValidator(final Repository repository) {
        this.repository = repository;
    }

    @Override
    public Report validate() {
        final var report = ValidationReport.create();
        report.merge(validateCommunityPortalTemplate());
        report.merge(validateChangesDescription());
        return report;
    }

    private Report validateCommunityPortalTemplate() {
        final var report = ValidationReport.create();
        try {
            final String communityConfig = this.repository.getSingleFileContentAsString(COMMUNITY_CONFIG_PATH);
            report.merge(validateConfigurations(CommunityConfigParser.parse(communityConfig)));
        } catch (final RepositoryException exception) {
            report.addFailedResult(ExaError.messageBuilder("E-RD-CP-3") //
                    .message("Cannot find a required config file {{fileName}}.", RELEASE_CONFIG_PATH) //
                    .mitigation(" Please, add this file according to the user guide.").toString());
        }
        return report;
    }

    private Report validateConfigurations(final CommunityConfig releaseConfig) {
        final var report = ValidationReport.create();
        report.merge(validateProjectName(releaseConfig));
        report.merge(validateProjectDescription(releaseConfig));
        report.merge(validateTags(releaseConfig));
        return report;
    }

    private Report validateProjectName(final CommunityConfig config) {
        final var report = ValidationReport.create();
        if (config.hasCommunityProjectName()) {
            report.addSuccessfulResult(getSuccessfulMessage("Project name"));
        } else {
            report.addFailedResult(getFailedMessage("Project name"));
        }
        return report;
    }

    private String getSuccessfulMessage(final String value) {
        return value + " for releasing on the community portal.";
    }

    private String getFailedMessage(final String value) {
        return ExaError.messageBuilder("E-RD-CP-5") //
                .message("{{value}} for releasing on the community portal is missing.", value)
                .mitigation("Please add it according to the user guide.").toString();
    }

    private Report validateProjectDescription(final CommunityConfig config) {
        final var report = ValidationReport.create();
        if (config.hasCommunityProjectDescription()) {
            report.addSuccessfulResult(getSuccessfulMessage("Project description"));
        } else {
            report.addFailedResult(getFailedMessage("Project description"));
        }
        return report;
    }

    private Report validateTags(final CommunityConfig config) {
        final var report = ValidationReport.create();
        if (config.hasCommunityTags()) {
            report.addSuccessfulResult(getSuccessfulMessage("Tags"));
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-CP-6") //
                    .message("Tags for releasing on the community portal are missing.")
                    .mitigation("Please add at least one tag.").toString());
        }
        return report;
    }

    private Report validateChangesDescription() {
        final var report = ValidationReport.create();
        final var releaseLetter = this.repository.getReleaseLetter(this.repository.getVersion());
        final Optional<String> summary = releaseLetter.getSummary();
        if (summary.isPresent()) {
            report.addSuccessfulResult("Changes description from the changes file.");
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-CP-7")
                    .message("Cannot find the ## Summary section in the release letter {{releaseLetter}}.",
                            releaseLetter.getFileName())
                    .mitigation("Please add this section in order to release on the Community Portal.").toString());
        }
        return report;
    }
}