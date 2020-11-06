package com.exasol.releaserobot.github;

import java.util.*;
import java.util.logging.Logger;

import com.exasol.releaserobot.repository.*;
import com.exasol.releaserobot.usecases.*;
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
     * @param githubGateway instance of {@link GithubGateway}
     */
    public GitHubPlatformValidator(final GithubGateway githubGateway) {
        this.githubGateway = githubGateway;
    }

    @Override
    public Report validate(final RepositoryTOGOAWAY repository) {
        LOGGER.fine("Validating GitHub-specific requirements.");
        final Report report = ReportImpl.validationReport();
        final Repository branch = repository.getBranch();
        final String version = branch.getVersion();
        final ReleaseLetter releaseLetter = branch.getReleaseLetter(version);
        report.merge(validateChangesFile(branch.getRepositoryFullName(), branch.isDefaultBranch(), releaseLetter));
        report.merge(validateFileExists(branch, GITHUB_WORKFLOW_PATH, "Workflow for a GitHub release."));
        return report;
    }

    // [impl->dsn~validate-release-letter~1]
    private Report validateChangesFile(final String repositoryFullName, final boolean isDefaultBranch,
            final ReleaseLetter releaseLetter) {
        final Report report = ReportImpl.validationReport();
        report.merge(validateContainsHeader(releaseLetter));
        report.merge(validateGitHubTickets(repositoryFullName, isDefaultBranch, releaseLetter));
        return report;
    }

    protected Report validateContainsHeader(final ReleaseLetter changes) {
        final Report report = ReportImpl.validationReport();
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
    protected Report validateGitHubTickets(final String repositoryFullName, final boolean isDefaultBranch,
            final ReleaseLetter changesFile) {
        final Report report = ReportImpl.validationReport();
        try {
            final List<String> wrongTickets = collectWrongTickets(repositoryFullName, changesFile);
            if (!wrongTickets.isEmpty()) {
                report.merge(reportWrongTickets(isDefaultBranch, changesFile.getFileName(), wrongTickets));
            } else {
                report.addResult(ValidationResult.successfulValidation("Mentioned GitHub tickets."));
            }
        } catch (final GitHubException exception) {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-10",
                    "Unable to retrieve a a list of closed tickets on GitHub:" + exception.getMessage()));
        }
        return report;
    }

    private Report reportWrongTickets(final boolean isDefaultBranch, final String fileName,
            final List<String> wrongTickets) {
        final Report report = ReportImpl.validationReport();
        final String wrongTicketsString = String.join(", ", wrongTickets);
        if (isDefaultBranch) {
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

    private List<String> collectWrongTickets(final String repositoryFullName, final ReleaseLetter changesFile)
            throws GitHubException {
        final Set<Integer> closedTickets = this.githubGateway.getClosedTickets(repositoryFullName);
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