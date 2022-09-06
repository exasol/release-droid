package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.adapter.RepositoryValidatorHelper.validateRepositories;
import static com.exasol.releasedroid.adapter.github.GitHubConstants.GITHUB_UPLOAD_ASSETS_WORKFLOW_PATH;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
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
    private final Clock clock;

    /**
     * Create a new instance of {@link GitHubPlatformValidator}.
     *
     * @param repository    repository to validate
     * @param githubGateway instance of {@link GitHubGateway}
     */
    public GitHubPlatformValidator(final Repository repository, final GitHubGateway githubGateway) {
        this(repository, githubGateway, Clock.systemDefaultZone());
    }

    GitHubPlatformValidator(final Repository repository, final GitHubGateway githubGateway, final Clock clock) {
        this.repository = repository;
        this.githubGateway = githubGateway;
        this.clock = clock;
    }

    @Override
    public Report validate() {
        final var report = ValidationReport.create();
        report.merge(validateRepositories(this.repository.getRepositoryValidators()));
        LOGGER.fine("Validating GitHub-specific requirements.");
        final String version = this.repository.getVersion();
        final var releaseLetter = this.repository.getReleaseLetter(version);
        report.merge(validateChangesFile(releaseLetter));
        validateIfUploadAssetsWorkflowExists();
        return report;
    }

    private void validateIfUploadAssetsWorkflowExists() {
        if (!this.repository.hasFile(GITHUB_UPLOAD_ASSETS_WORKFLOW_PATH)) {
            LOGGER.warning(
                    "Attention! This repository doesn't have a workflow for uploading assets to the GitHub release: "
                            + GITHUB_UPLOAD_ASSETS_WORKFLOW_PATH
                            + ". It means the release will not have any uploaded assets!");
        }
    }

    // [impl->dsn~validate-release-letter~1]
    // [impl->dsn~validating-release-date~1]
    private Report validateChangesFile(final ReleaseLetter releaseLetter) {
        final var report = ValidationReport.create();
        report.merge(validateContainsHeader(releaseLetter));
        report.merge(validateGitHubTickets(releaseLetter));
        report.merge(validateReleaseDate(releaseLetter));
        return report;
    }

    private Report validateReleaseDate(final ReleaseLetter releaseLetter) {
        final var report = ValidationReport.create();
        final Optional<LocalDate> releaseDate = releaseLetter.getReleaseDate();
        final LocalDate today = LocalDate.ofInstant(this.clock.instant(), this.clock.getZone());
        if (releaseDate.isEmpty()) {
            report.addFailedResult(ExaError.messageBuilder("E-RD-GH-26")
                    .message("Release date is not specified in file {{fileName}}.", releaseLetter.getFileName()) //
                    .mitigation("Please update the file header to match '# <Project> <Version>, released <Date>'") //
                    .toString());
        } else if (releaseDate.get().equals(today)) {
            report.addSuccessfulResult("Release date.");
        } else {
            final String delta = releaseDate.get().isAfter(today) ? "is in the future" : "has past already";
            report.addFailedResult(ExaError.messageBuilder("E-RD-GH-31")
                    .message("Release date {{date}} in file {{fileName}} {{delta|uq}}.", releaseDate.get(),
                            releaseLetter.getFileName(), delta)
                    .mitigation("Please make sure to release on the same date as specified.") //
                    .toString());
        }
        return report;
    }

    private Report validateContainsHeader(final ReleaseLetter changes) {
        final var report = ValidationReport.create();
        final Optional<String> header = changes.getHeader();
        if (header.isEmpty() || header.get().isEmpty()) {
            report.addFailedResult(ExaError.messageBuilder("E-RD-GH-21").message(
                    "The file {{fileName}} does not contain 'Code name' section which is used as a GitHub release header."
                            + " Please, add this section to the file.",
                    changes.getFileName()) //
                    .toString());
        } else {
            report.addSuccessfulResult("Release letter header.");
        }
        return report;
    }

    // [impl->dsn~validate-github-issues-exists~1]
    // [impl->dsn~validate-github-issues-are-closed~1]
    private Report validateGitHubTickets(final ReleaseLetter releaseLetter) {
        final var report = ValidationReport.create();
        try {
            final List<String> wrongTickets = collectWrongTickets(this.repository.getName(), releaseLetter);
            if (!wrongTickets.isEmpty()) {
                report.merge(reportWrongTickets(this.repository.isOnDefaultBranch(), releaseLetter.getFileName(),
                        wrongTickets));
            } else {
                report.addSuccessfulResult("Mentioned GitHub tickets.");
            }
        } catch (final GitHubException exception) {
            report.addFailedResult(ExaError.messageBuilder("E-RD-GH-22")
                    .message("Unable to retrieve a list of closed tickets on GitHub: {{cause|uq}}",
                            exception.getMessage())
                    .toString());
        }
        return report;
    }

    private Report reportWrongTickets(final boolean isDefaultBranch, final String fileName,
            final List<String> wrongTickets) {
        final var report = ValidationReport.create();
        final var wrongTicketsString = String.join(", ", wrongTickets);
        if (isDefaultBranch) {
            report.addFailedResult(ExaError.messageBuilder("E-RD-GH-23").message(
                    "Some of the mentioned GitHub issues are not closed or do not exists: {{wrongTicketsString|uq}}.",
                    wrongTicketsString) //
                    .mitigation("Please, check the issues numbers in your {{fileName}}.")
                    .parameter("fileName", fileName).toString());
        } else {
            final var warningMessage = ExaError.messageBuilder("W-RD-GH-24").message(
                    "Don't forget to close the tickets mentioned in the {{fileName}} file before you release: {{wrongTicketsString|uq}}.",
                    fileName, wrongTicketsString) //
                    .toString();
            report.addSuccessfulResult("Skipping mentioned GitHub tickets validation. " + warningMessage);
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