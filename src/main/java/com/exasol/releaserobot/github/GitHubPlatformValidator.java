package com.exasol.releaserobot.github;

import java.util.*;
import java.util.logging.Logger;

import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.ReleaseLetter;
import com.exasol.releaserobot.usecases.*;
import com.exasol.releaserobot.usecases.ReportImpl.ReportName;
import com.exasol.releaserobot.usecases.validate.AbstractPlatformValidator;

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
     * @param branchContent content of a branch to validate
     * @param githubGateway instance of {@link GithubGateway}
     */
    public GitHubPlatformValidator(final GitBranchContent branchContent, final GithubGateway githubGateway) {
        super(branchContent);
        this.githubGateway = githubGateway;
    }

    @Override
    public Report validate() {
        LOGGER.fine("Validating GitHub-specific requirements.");
        final Report report = new ReportImpl(ReportName.VALIDATION);
        final String version = this.branchContent.getVersion();
        final ReleaseLetter releaseLetter = this.branchContent.getReleaseLetter(version);
        report.merge(validateChangesFile(releaseLetter));
        report.merge(validateFileExists(GITHUB_WORKFLOW_PATH, "Workflow for a GitHub release."));
        return report;
    }

    // [impl->dsn~validate-release-letter~1]
    private Report validateChangesFile(final ReleaseLetter releaseLetter) {
        final Report report = new ReportImpl(ReportName.VALIDATION);
        report.merge(validateContainsHeader(releaseLetter));
        report.merge(validateGitHubTickets(releaseLetter));
        return report;
    }

    protected Report validateContainsHeader(final ReleaseLetter changes) {
        final Report report = new ReportImpl(ReportName.VALIDATION);
        final Optional<String> header = changes.getHeader();
        if (header.isEmpty()) {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-1",
                    "The file '" + changes.getFileName()
                            + "' does not contain 'Code name' section which is used as a GitHub release header."
                            + " Please, add this section to the file."));
        } else {
            report.addResult(ValidationResult.successfulValidation("Release letter header."));
        }
        return report;
    }

    // [impl->dsn~validate-github-issues-exists~1]
    // [impl->dsn~validate-github-issues-are-closed~1]
    protected Report validateGitHubTickets(final ReleaseLetter changesFile) {
        final Report report = new ReportImpl(ReportName.VALIDATION);
        try {
            final List<String> wrongTickets = collectWrongTickets(changesFile);
            if (!wrongTickets.isEmpty()) {
                report.merge(reportWrongTickets(changesFile.getFileName(), wrongTickets));
            } else {
                report.addResult(ValidationResult.successfulValidation("Mentioned GitHub tickets."));
            }
        } catch (final GitHubException exception) {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-10",
                    "Unable to retrieve a a list of closed tickets on GitHub:" + exception.getMessage()));
        }
        return report;
    }

    private Report reportWrongTickets(final String fileName, final List<String> wrongTickets) {
        final Report report = new ReportImpl(ReportName.VALIDATION);
        final String wrongTicketsString = String.join(", ", wrongTickets);
        if (this.branchContent.isDefaultBranch()) {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-2",
                    "Some of the mentioned GitHub issues are not closed or do not exists: " + wrongTicketsString
                            + ", Please, check the issues numbers in your '" + fileName + "' one more time."));
        } else {
            final String warningMessage = "W-RR-VAL-1. Don't forget to close the tickets mentioned in the '" + fileName
                    + "' file before you release: " + wrongTicketsString + ".";
            report.addResult(ValidationResult
                    .successfulValidation("Skipping mentioned GitHub tickets validation. " + warningMessage));
            LOGGER.warning(warningMessage);
        }
        return report;
    }

    private List<String> collectWrongTickets(final ReleaseLetter changesFile) throws GitHubException {
        final Set<Integer> closedTickets = this.githubGateway.getClosedTickets();
        final List<Integer> mentionedTickets = changesFile.getTicketNumbers();
        final List<String> wrongTickets = new ArrayList<>();
        for (final Integer ticket : mentionedTickets) {
            if (!closedTickets.contains(ticket)) {
                wrongTickets.add(String.valueOf(ticket));
            }
        }
        return wrongTickets;
    }
}