package com.exasol.releaserobot;

import com.exasol.releaserobot.report.ValidationReport;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.GitRepositoryException;

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
    public void validateFileExists(final String filePath, final String fileDescription,
            final ValidationReport validationReport) {
        try {
            this.branchContent.getSingleFileContentAsString(filePath);
            validationReport.addSuccessfulValidation(fileDescription);
        } catch (final GitRepositoryException exception) {
            validationReport.addFailedValidations("E-RR-VAL-3",
                    "The file '" + filePath + "' does not exist in the project. Please, add this file.");
        }
    }
}