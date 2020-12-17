package com.exasol.releasedroid.github;

import java.util.*;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.Repository;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationResult;
import com.exasol.releasedroid.usecases.validate.AbstractPlatformValidator;

/**
 * This class checks if the project is ready for a release on GitHub.
 */
public class GitHubPlatformValidator extends AbstractPlatformValidator {
    protected static final String GITHUB_WORKFLOW_PATH = ".github/workflows/github_release.yml";
    private static final Logger LOGGER = Logger.getLogger(GitHubPlatformValidator.class.getName());
    private final GithubGateway githubGateway;

    /**
     * Create a new instance of {@link GitHubPlatformValidator}.
     *
     * @param githubGateway instance of {@link GithubGateway}
     */
    public GitHubPlatformValidator(final GithubGateway githubGateway) {
        this.githubGateway = githubGateway;
    }

    @Override
    // [impl->dsn~validate-github-workflow-exists~1]
    public Report validate(final Repository repository) {
        LOGGER.fine("Validating GitHub-specific requirements.");
        final Report report = Report.validationReport();
        final String version = repository.getVersion();
        final ReleaseLetter releaseLetter = repository.getReleaseLetter(version);
        report.merge(validateChangesFile(repository, releaseLetter));
        report.merge(validateFileExists(repository, GITHUB_WORKFLOW_PATH, "Workflow for a GitHub release."));
        return report;
    }

    // [impl->dsn~validate-release-letter~1]
    private Report validateChangesFile(final Repository repository, final ReleaseLetter releaseLetter) {
        final Report report = Report.validationReport();
        report.merge(validateContainsHeader(releaseLetter));
        report.merge(validateGitHubTickets(repository, releaseLetter));
        return report;
    }

    protected Report validateContainsHeader(final ReleaseLetter changes) {
        final Report report = Report.validationReport();
        final Optional<String> header = changes.getHeader();
        if (header.isEmpty()) {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RR-VAL-1").message(
                    "The file {{fileName}} does not contain 'Code name' section which is used as a GitHub release header."
                            + " Please, add this section to the file.")
                    .parameter("fileName", changes.getFileName()).toString()));
        } else {
            report.addResult(ValidationResult.successfulValidation("Release letter header."));
        }
        return report;
    }

    // [impl->dsn~validate-github-issues-exists~1]
    // [impl->dsn~validate-github-issues-are-closed~1]
    protected Report validateGitHubTickets(final Repository repository, final ReleaseLetter releaseLetter) {
        final Report report = Report.validationReport();
        try {
            final List<String> wrongTickets = collectWrongTickets(repository.getFullName(), releaseLetter);
            if (!wrongTickets.isEmpty()) {
                report.merge(
                        reportWrongTickets(repository.isOnDefaultBranch(), releaseLetter.getFileName(), wrongTickets));
            } else {
                report.addResult(ValidationResult.successfulValidation("Mentioned GitHub tickets."));
            }
        } catch (final GitHubException exception) {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RR-VAL-10")
                    .message("Unable to retrieve a list of closed tickets on GitHub: {{cause}}")
                    .unquotedParameter("cause", exception.getMessage()).toString()));
        }
        return report;
    }

    private Report reportWrongTickets(final boolean isDefaultBranch, final String fileName,
            final List<String> wrongTickets) {
        final Report report = Report.validationReport();
        final String wrongTicketsString = String.join(", ", wrongTickets);
        if (isDefaultBranch) {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RR-VAL-2").message(
                    "Some of the mentioned GitHub issues are not closed or do not exists: {{wrongTicketsString}}.")
                    .unquotedParameter("wrongTicketsString", wrongTicketsString)
                    .mitigation("Please, check the issues numbers in your {{fileName}}.")
                    .parameter("fileName", fileName).toString()));
        } else {
            final String warningMessage = ExaError.messageBuilder("W-RR-VAL-1").message(
                    "Don't forget to close the tickets mentioned in the {{fileName}} file before you release: {{wrongTicketsString}}.")
                    .parameter("fileName", fileName) //
                    .unquotedParameter("wrongTicketsString", wrongTicketsString).toString();
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