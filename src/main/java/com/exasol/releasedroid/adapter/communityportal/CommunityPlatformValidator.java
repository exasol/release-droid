package com.exasol.releasedroid.adapter.communityportal;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_CONFIG_PATH;
import static com.exasol.releasedroid.usecases.report.ValidationResult.failedValidation;
import static com.exasol.releasedroid.usecases.report.ValidationResult.successfulValidation;

import java.util.Optional;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationResult;
import com.exasol.releasedroid.usecases.repository.ReleaseConfig;
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
        final var report = Report.validationReport();
        report.merge(validateCommunityPortalTemplate());
        report.merge(validateChangesDescription());
        return report;
    }

    private Report validateCommunityPortalTemplate() {
        final var report = Report.validationReport();
        final Optional<ReleaseConfig> releaseConfig = this.repository.getReleaseConfig();
        if (releaseConfig.isPresent()) {
            report.merge(validateConfigurations(releaseConfig.get()));
        } else {
            report.addResult(failedValidation(ExaError.messageBuilder("E-RD-CP-3") //
                    .message("Cannot find a required config file {{fileName}}.", RELEASE_CONFIG_PATH) //
                    .mitigation(" Please, add this file according to the user guide.").toString()));
        }
        return report;
    }

    private Report validateConfigurations(final ReleaseConfig releaseConfig) {
        final var report = Report.validationReport();
        report.merge(validateProjectName(releaseConfig));
        report.merge(validateProjectDescription(releaseConfig));
        report.merge(validateTags(releaseConfig));
        return report;
    }

    private Report validateProjectName(final ReleaseConfig config) {
        final var report = Report.validationReport();
        if (config.hasCommunityProjectName()) {
            report.addResult(getSuccessfulResult("Project name"));
        } else {
            report.addResult(getFailedResult("Project name"));
        }
        return report;
    }

    private ValidationResult getSuccessfulResult(final String value) {
        return successfulValidation(value + " for releasing on the community portal.");
    }

    private ValidationResult getFailedResult(final String value) {
        return failedValidation(ExaError.messageBuilder("E-RD-CP-5") //
                .message("{{value}} for releasing on the community portal is missing.", value)
                .mitigation("Please add it according to the user guide.").toString());
    }

    private Report validateProjectDescription(final ReleaseConfig config) {
        final var report = Report.validationReport();
        if (config.hasCommunityProjectDescription()) {
            report.addResult(getSuccessfulResult("Project description"));
        } else {
            report.addResult(getFailedResult("Project description"));
        }
        return report;
    }

    private Report validateTags(final ReleaseConfig config) {
        final var report = Report.validationReport();
        if (config.hasCommunityTags()) {
            report.addResult(getSuccessfulResult("Tags"));
        } else {
            report.addResult(failedValidation(ExaError.messageBuilder("E-RD-CP-6") //
                    .message("Tags for releasing on the community portal are missing.")
                    .mitigation("Please add at least one tag.").toString()));
        }
        return report;
    }

    private Report validateChangesDescription() {
        final var report = Report.validationReport();
        final var releaseLetter = this.repository.getReleaseLetter(this.repository.getVersion());
        final Optional<String> summary = releaseLetter.getSummary();
        if (summary.isPresent()) {
            report.addResult(ValidationResult.successfulValidation("Changes description from the changes file."));
        } else {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-CP-7")
                    .message("Cannot find the ## Summary section in the release letter {{releaseLetter}}.",
                            releaseLetter.getFileName())
                    .mitigation("Please add this section in order to release on the Community Portal.").toString()));
        }
        return report;
    }
}