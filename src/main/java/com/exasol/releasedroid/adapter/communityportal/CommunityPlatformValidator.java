package com.exasol.releasedroid.adapter.communityportal;

import static com.exasol.releasedroid.adapter.communityportal.CommunityPortalConstants.RELEASE_CONFIG;
import static com.exasol.releasedroid.usecases.report.ValidationResult.failedValidation;
import static com.exasol.releasedroid.usecases.report.ValidationResult.successfulValidation;

import java.util.Optional;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationResult;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Contains validations for releasing on the Community Portal.
 */
public class CommunityPlatformValidator implements RepositoryValidator {
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
        try {
            final var templateAsString = this.repository.getSingleFileContentAsString(RELEASE_CONFIG);
            report.merge(validateTemplate(templateAsString));
        } catch (final RepositoryException exception) {
            report.addResult(failedValidation(ExaError.messageBuilder("E-RD-CP-3") //
                    .message("Cannot find a file {{fileName}}.", RELEASE_CONFIG) //
                    .mitigation(" Please, add this file according to the user guide.").toString()));
        }
        return report;
    }

    private Report validateTemplate(final String templateAsString) {
        final var report = Report.validationReport();
        final var template = CommunityPortalTemplateParser.parse(templateAsString);
        report.merge(validateProjectName(template));
        report.merge(validateProjectDescription(template));
        report.merge(validateTags(template));
        return report;
    }

    private Report validateProjectName(final CommunityPortalTemplate template) {
        final var report = Report.validationReport();
        if (template.hasProjectName()) {
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

    private Report validateProjectDescription(final CommunityPortalTemplate template) {
        final var report = Report.validationReport();
        if (template.hasProjectDescription()) {
            report.addResult(getSuccessfulResult("Project description"));
        } else {
            report.addResult(getFailedResult("Project description"));
        }
        return report;
    }

    private Report validateTags(final CommunityPortalTemplate template) {
        final var report = Report.validationReport();
        if (template.hasTags()) {
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
                    .message("## Changes section of the {{releaseLetter}} is missing", releaseLetter.getFileName())
                    .mitigation("Please add this section in order to release on the Community Portal.").toString()));
        }
        return report;
    }
}