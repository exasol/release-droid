package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.report.*;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.GitRepositoryException;
import com.exasol.releaserobot.usecases.*;
import com.exasol.releaserobot.usecases.ReportImpl.ReportName;

/**
 * Contains a common logic for classes implementing {@link PlatformValidator}.
 */
public abstract class AbstractPlatformValidator implements PlatformValidator {
    protected final GitBranchContent branchContent;

    /**
     * Create a new instance of {@link AbstractPlatformValidator}.
     *
     * @param branchContent content of a branch to validate
     */
    protected AbstractPlatformValidator(final GitBranchContent branchContent) {
        this.branchContent = branchContent;
    }

    /**
     * Check that the workflow file exists and is reachable.
     */
    public Report validateFileExists(final String filePath, final String fileDescription) {
        final Report report = new ReportImpl(ReportName.VALIDATION);
        try {
            this.branchContent.getSingleFileContentAsString(filePath);
            report.addResult(ValidationResult.successfulValidation(fileDescription));
        } catch (final GitRepositoryException exception) {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-9",
                    "The file '" + filePath + "' does not exist in the project. Please, add this file."));
        }
        return report;
    }
}