package com.exasol.releasedroid.usecases.validate;

import com.exasol.releasedroid.repository.RepositoryException;
import com.exasol.releasedroid.usecases.*;

/**
 * Contains a common logic for classes implementing {@link RepositoryValidator}.
 */
public abstract class AbstractPlatformValidator implements RepositoryValidator {
    /**
     * Check that the workflow file exists and is reachable.
     * 
     * @param repository      repository to check
     * @param filePath        path to the file
     * @param fileDescription workflow description for a report
     * @return new instance of {@link Report}
     */
    public Report validateFileExists(final Repository repository, final String filePath, final String fileDescription) {
        final Report report = ReportImpl.validationReport();
        try {
            repository.getSingleFileContentAsString(filePath);
            report.addResult(ValidationResult.successfulValidation(fileDescription));
        } catch (final RepositoryException exception) {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-9",
                    "The file '" + filePath + "' does not exist in the project. Please, add this file."));
        }
        return report;
    }
}