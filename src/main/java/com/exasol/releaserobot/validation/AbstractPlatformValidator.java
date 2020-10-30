package com.exasol.releaserobot.validation;

import com.exasol.releaserobot.report.ValidationReport;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.GitRepositoryException;

/**
 * Contains a common logic for classes implementing {@link PlatformValidator}.
 */
public abstract class AbstractPlatformValidator implements PlatformValidator {
    protected final GitBranchContent branchContent;
    protected final ValidationReport validationReport;

    /**
     * Create a new instance of {@link AbstractPlatformValidator}.
     *
     * @param branchContent    content of a branch to validate
     * @param validationReport instance of {@link ValidationReport}
     */
    protected AbstractPlatformValidator(final GitBranchContent branchContent, final ValidationReport validationReport) {
        this.branchContent = branchContent;
        this.validationReport = validationReport;
    }

    /**
     * Check that the workflow file exists and is reachable.
     */
    protected void validateFileExists(final String filePath, final String fileDescription) {
        try {
            this.branchContent.getSingleFileContentAsString(filePath);
            this.validationReport.addSuccessfulValidation(fileDescription);
        } catch (final GitRepositoryException exception) {
            this.validationReport.addFailedValidations("E-RR-VAL-3",
                    "The file '" + filePath + "' does not exist in the project. Please, add this file.");
        }
    }
}