package com.exasol.releaserobot;

import java.util.ArrayList;
import java.util.List;

import com.exasol.releaserobot.report.ValidationResult;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.GitRepositoryException;

/**
 * Contains a common logic for classes implementing {@link PlatformValidator}.
 */
public abstract class AbstractPlatformValidator implements PlatformValidator {
    protected final GitBranchContent branchContent;
    public final List<ValidationResult> validationResults = new ArrayList<>();

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
    public void validateFileExists(final String filePath, final String fileDescription) {
        try {
            this.branchContent.getSingleFileContentAsString(filePath);
            this.validationResults.add(ValidationResult.successfulValidation(fileDescription));
        } catch (final GitRepositoryException exception) {
            this.validationResults.add(ValidationResult.failedValidation("E-RR-VAL-3",
                    "The file '" + filePath + "' does not exist in the project. Please, add this file."));
        }
    }
}