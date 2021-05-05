package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.adapter.RepositoryValidatorHelper.validateFileExists;
import static com.exasol.releasedroid.adapter.RepositoryValidatorHelper.validateRepositories;
import static com.exasol.releasedroid.adapter.github.GitHubConstants.GITHUB_RELEASE_WORKFLOW_PATH;

import java.util.*;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationResult;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.validate.ReleasePlatformValidator;

/**
 * This class checks if the project is ready for a release on GitHub.
 */
public class GitHubPlatformValidator implements ReleasePlatformValidator {
    private static final Logger LOGGER = Logger.getLogger(GitHubPlatformValidator.class.getName());
    private final GitHubGateway githubGateway;
    private final Repository repository;

    /**
     * Create a new instance of {@link GitHubPlatformValidator}.
     *
     * @param repository    repository to validate
     * @param githubGateway instance of {@link GitHubGateway}
     */
    public GitHubPlatformValidator(final Repository repository, final GitHubGateway githubGateway) {
        this.repository = repository;
        this.githubGateway = githubGateway;
    }

    @Override
    // [impl->dsn~validate-github-workflow-exists~1]
    public Report validate() {
        final var report = Report.validationReport();
        report.merge(validateRepositories(this.repository.getStructureValidators()));
        LOGGER.fine("Validating GitHub-specific requirements.");
        final String version = this.repository.getVersion();
        final var releaseLetter = this.repository.getReleaseLetter(version);
        report.merge(validateChangesFile(releaseLetter));
        report.merge(validateFileExists(this.repository, GITHUB_RELEASE_WORKFLOW_PATH, "Workflow for a GitHub release."));
        return report;
    }

    // [impl->dsn~validate-release-letter~1]
    private Report validateChangesFile(final ReleaseLetter releaseLetter) {
        final var report = Report.validationReport();
        report.merge(validateContainsHeader(releaseLetter));
        report.merge(validateGitHubTickets(releaseLetter));
        return report;
    }

    private Report validateContainsHeader(final ReleaseLetter changes) {
        final var report = Report.validationReport();
        final Optional<String> header = changes.getHeader();
        if (header.isEmpty() || header.get().isEmpty()) {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-GH-21").message(
                    "The file {{fileName}} does not contain 'Code name' section which is used as a GitHub release header."
                            + " Please, add this section to the file.",
                    changes.getFileName()) //
                    .toString()));
        } else {
            report.addResult(ValidationResult.successfulValidation("Release letter header."));
        }
        return report;
    }

    // [impl->dsn~validate-github-issues-exists~1]
    // [impl->dsn~validate-github-issues-are-closed~1]
    private Report validateGitHubTickets(final ReleaseLetter releaseLetter) {
        final var report = Report.validationReport();
        try {
            final List<String> wrongTickets = collectWrongTickets(this.repository.getName(), releaseLetter);
            if (!wrongTickets.isEmpty()) {
                report.merge(reportWrongTickets(this.repository.isOnDefaultBranch(), releaseLetter.getFileName(),
                        wrongTickets));
            } else {
                report.addResult(ValidationResult.successfulValidation("Mentioned GitHub tickets."));
            }
        } catch (final GitHubException exception) {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-GH-22")
                    .message("Unable to retrieve a list of closed tickets on GitHub: {{cause|uq}}",
                            exception.getMessage())
                    .toString()));
        }
        return report;
    }

    private Report reportWrongTickets(final boolean isDefaultBranch, final String fileName,
            final List<String> wrongTickets) {
        final var report = Report.validationReport();
        final var wrongTicketsString = String.join(", ", wrongTickets);
        if (isDefaultBranch) {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-GH-23").message(
                    "Some of the mentioned GitHub issues are not closed or do not exists: {{wrongTicketsString|uq}}.",
                    wrongTicketsString) //
                    .mitigation("Please, check the issues numbers in your {{fileName}}.")
                    .parameter("fileName", fileName).toString()));
        } else {
            final var warningMessage = ExaError.messageBuilder("W-RD-GH-24").message(
                    "Don't forget to close the tickets mentioned in the {{fileName}} file before you release: {{wrongTicketsString|uq}}.",
                    fileName, wrongTicketsString) //
                    .toString();
            report.addResult(ValidationResult
                    .successfulValidation("Skipping mentioned GitHub tickets validation. " + warningMessage));
            LOGGER.warning(warningMessage);
        }
        return report;
    }

    private List<String> collectWrongTickets(final String repositoryFullName, final ReleaseLetter releaseLetter)
            throws GitHubException {
        final Set<Integer> closedTickets = this.githubGateway.getClosedTickets(repositoryFullName);
        final List<Integer> mentionedTickets = releaseLetter.getTicketNumbers();
        final List<String> wrongTickets = new ArrayList<>();
        for (final Integer ticket : mentionedTickets) {
            if (!closedTickets.contains(ticket)) {
                wrongTickets.add(String.valueOf(ticket));
            }
        }
        return wrongTickets;
    }
}