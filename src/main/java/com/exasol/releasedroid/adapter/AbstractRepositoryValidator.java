package com.exasol.releasedroid.adapter;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationResult;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Contains a common logic for classes implementing {@link RepositoryValidator}.
 */
public abstract class AbstractRepositoryValidator implements RepositoryValidator {
    /**
     * Check that the workflow file exists and is reachable.
     *
     * @param repository      repository to check
     * @param filePath        path to the file
     * @param fileDescription workflow description for a report
     * @return new instance of {@link Report}
     */
    public Report validateFileExists(final Repository repository, final String filePath, final String fileDescription) {
        final Report report = Report.validationReport();
        try {
            repository.getSingleFileContentAsString(filePath);
            report.addResult(ValidationResult.successfulValidation(fileDescription));
        } catch (final RepositoryException exception) {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-VAL-9")
                    .message("The file {{filePath}} does not exist in the project.") //
                    .parameter("filePath", filePath) //
                    .mitigation("Please, add this file.").toString()));
        }
        return report;
    }
}