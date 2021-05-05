package com.exasol.releasedroid.adapter;

import java.util.List;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationResult;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.validate.StructureValidator;

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
        final var report = Report.validationReport();
        try {
            repository.getSingleFileContentAsString(filePath);
            report.addResult(ValidationResult.successfulValidation(fileDescription));
        } catch (final RepositoryException exception) {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-REP-19")
                    .message("The file {{filePath}} does not exist in the project.") //
                    .parameter("filePath", filePath) //
                    .mitigation("Please, add this file.").toString()));
        }
        return report;
    }

    /**
     * Validate with structure validators and return a report.
     *
     * @param structureValidators structure validators
     * @return report
     */
    public static Report validateRepositories(final List<StructureValidator> structureValidators) {
        final var report = Report.validationReport();
        for (final StructureValidator repositoryValidator : structureValidators) {
            report.merge(repositoryValidator.validate());
        }
        return report;
    }
}