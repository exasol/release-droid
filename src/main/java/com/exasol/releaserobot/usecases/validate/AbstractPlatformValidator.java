package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.repository.Branch;
import com.exasol.releaserobot.repository.GitRepositoryException;
import com.exasol.releaserobot.usecases.*;

/**
 * Contains a common logic for classes implementing {@link PlatformValidator}.
 */
public abstract class AbstractPlatformValidator implements PlatformValidator {
    /**
     * Check that the workflow file exists and is reachable.
     */
    public Report validateFileExists(final Branch branch, final String filePath, final String fileDescription) {
        final Report report = ReportImpl.validationReport();
        try {
            branch.getSingleFileContentAsString(filePath);
            report.addResult(ValidationResult.successfulValidation(fileDescription));
        } catch (final GitRepositoryException exception) {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-9",
                    "The file '" + filePath + "' does not exist in the project. Please, add this file."));
        }
        return report;
    }
}