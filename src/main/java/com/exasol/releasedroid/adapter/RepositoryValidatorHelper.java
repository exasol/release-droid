package com.exasol.releasedroid.adapter;

import java.util.List;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Contains a common logic for repository validators.
 */
public class RepositoryValidatorHelper {
    private RepositoryValidatorHelper() {
    }

    /**
     * Check that the workflow file exists and is reachable.
     *
     * @param repository      repository to check
     * @param filePath        path to the file
     * @param fileDescription workflow description for a report
     * @return new instance of {@link Report}
     */
    public static Report validateFileExists(final Repository repository, final String filePath,
            final String fileDescription) {
        final var report = ValidationReport.create();
        if (repository.hasFile(filePath)) {
            report.addSuccessfulResult(fileDescription);
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-19")
                    .message("The file {{filePath}} does not exist in the project.") //
                    .parameter("filePath", filePath) //
                    .mitigation("Please, add this file.").toString());
        }
        return report;
    }

    /**
     * Validate a repository and return a report.
     *
     * @param repositoryValidators repository validators
     * @return report
     */
    public static Report validateRepositories(final List<RepositoryValidator> repositoryValidators) {
        final var report = ValidationReport.create();
        for (final RepositoryValidator repositoryValidator : repositoryValidators) {
            report.merge(repositoryValidator.validate());
        }
        return report;
    }
}