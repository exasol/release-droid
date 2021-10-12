package com.exasol.releasedroid.adapter.repository;

import static com.exasol.releasedroid.adapter.github.GitHubConstants.PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH;
import static com.exasol.releasedroid.adapter.github.GitHubConstants.PRINT_QUICK_CHECKSUM_WORKFLOW_PATH;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.VERSION_REGEX;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Contains language-independent validations for a repository.
 */
// Removing string duplicates here will decrease readability.
public class CommonRepositoryValidator implements RepositoryValidator {
    private static final Logger LOGGER = Logger.getLogger(CommonRepositoryValidator.class.getName());
    private final Repository repository;

    /**
     * Create a new instance of {@link CommonRepositoryValidator}.
     *
     * @param repository repository
     */
    public CommonRepositoryValidator(final Repository repository) {
        this.repository = repository;
    }

    @Override
    public Report validate() {
        LOGGER.fine("Validating repository on branch '" + this.repository.getBranchName() + "'.");
        final var report = ValidationReport.create();
        final String version = this.repository.getVersion();
        report.merge(validateVersion(version));
        if (!report.hasFailures()) {
            final String changelog = this.repository.getChangelogFile();
            report.merge(validateChangelog(changelog, version));
            final ReleaseLetter releaseLetter = this.repository.getReleaseLetter(version);
            report.merge(validateChanges(releaseLetter, version, this.repository.isOnDefaultBranch()));
        }
        report.merge(validateChecksumWorkflows());
        return report;
    }

    private Report validateChecksumWorkflows() {
        final var report = ValidationReport.create();
        if (repositoryHasBothWorkflows()) {
            report.addSuccessfulResult("Workflows for checksum generation exist.");
        } else if (repositoryMissesBothWorkflows()) {
            LOGGER.warning("Attention! This repository misses workflows for checksum generation. "
                    + "Please make sure that it's intended.");
            report.addSuccessfulResult("Workflows for checksum generation are missing.");
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-28")
                    .message("Please whether add both `" + PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH + "` and `"
                            + PRINT_QUICK_CHECKSUM_WORKFLOW_PATH + "` or remove them both.")
                    .toString());
        }
        return report;
    }

    private boolean repositoryMissesBothWorkflows() {
        return !this.repository.hasFile(PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH)
                && !this.repository.hasFile(PRINT_QUICK_CHECKSUM_WORKFLOW_PATH);
    }

    private boolean repositoryHasBothWorkflows() {
        return this.repository.hasFile(PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH)
                && this.repository.hasFile(PRINT_QUICK_CHECKSUM_WORKFLOW_PATH);
    }

    private Report validateVersion(final String version) {
        LOGGER.fine("Validating a new version.");
        final var report = ValidationReport.create();
        report.merge(validateVersionFormat(version));
        if (!report.hasFailures()) {
            report.merge(validateIfNewReleaseTagValid(version));
        }
        return report;
    }

    private Report validateVersionFormat(final String version) {
        final var report = ValidationReport.create();
        if ((version != null) && version.matches(VERSION_REGEX)) {
            report.addSuccessfulResult("Version format is correct.");
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-22")
                    .message("A version or tag found in this repository has invalid format: {{version|uq}}. "
                            + "The valid format is: <major>.<minor>.<fix>.", version)
                    .toString());
        }
        return report;
    }

    private Report validateIfNewReleaseTagValid(final String newVersion) {
        final var report = ValidationReport.create();
        final Optional<String> latestReleaseTag = this.repository.getLatestTag();
        if (latestReleaseTag.isPresent()) {
            report.merge(validateNewVersionWithPreviousTag(newVersion, latestReleaseTag.get()));
        } else {
            report.addSuccessfulResult("A new tag. This is the first release.");
        }
        return report;
    }

    // [impl->dsn~validate-release-version-format~1]
    // [impl->dsn~validate-release-version-increased-correctly~1]
    private Report validateNewVersionWithPreviousTag(final String newTag, final String latestTag) {
        final var report = ValidationReport.create();
        final Set<String> possibleVersions = getPossibleVersions(latestTag);
        if (possibleVersions.contains(newTag)) {
            report.addSuccessfulResult("A new tag.");
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-23")
                    .message(
                            "The new version {{newTag}} does not fit the versioning rules. "
                                    + "Possible versions for the release are: {{possibleVersions|uq}}",
                            newTag, possibleVersions.toString())
                    .toString());
        }
        return report;
    }

    private Set<String> getPossibleVersions(final String previousVersion) {
        final Set<String> versions = new HashSet<>();
        final String[] versionParts = previousVersion.split("\\.");
        final int major = Integer.parseInt(versionParts[0]);
        final int minor = Integer.parseInt(versionParts[1]);
        final int fix = Integer.parseInt(versionParts[2]);
        versions.add((major + 1) + ".0.0");
        versions.add(major + "." + (minor + 1) + ".0");
        versions.add(major + "." + minor + "." + (fix + 1));
        return versions;
    }

    // [impl->dsn~validate-changelog~1]
    private Report validateChangelog(final String changelog, final String version) {
        LOGGER.fine("Validating 'changelog.md' file.");
        final var report = ValidationReport.create();
        final String changelogContent = "[" + version + "](changes_" + version + ".md)";
        if ((changelog == null) || !changelog.contains(changelogContent)) {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-24")
                    .message("The file 'changelog.md' doesn't contain the following link.")
                    .mitigation("Please add {{changelogContent}} to the file.", changelogContent) //
                    .toString());
        } else {
            report.addSuccessfulResult("'changelog.md' file.");
            LOGGER.fine("Validation of 'changelog.md' file was successful.");
        }
        return report;
    }

    private Report validateChanges(final ReleaseLetter releaseLetter, final String version,
            final boolean isDefaultBranch) {
        final var report = ValidationReport.create();
        if (releaseLetter != null) {
            LOGGER.fine("Validating '" + releaseLetter.getFileName() + "' file.");
            report.merge(validateVersionInChanges(releaseLetter, version));
            report.merge(validateDateInChanges(releaseLetter, isDefaultBranch));
            report.merge(validateHasBody(releaseLetter));
        } else {
            report.addFailedResult(
                    ExaError.messageBuilder("E-RD-REP-25").message("The release letter does not exist.").toString());
        }
        return report;
    }

    // [impl->dsn~validate-changes-file-contains-release-version~1]
    private Report validateVersionInChanges(final ReleaseLetter changes, final String version) {
        final var report = ValidationReport.create();
        final Optional<String> releaseLetterVersion = changes.getVersionNumber();
        if (versionNumbersMatch(version, releaseLetterVersion)) {
            report.addSuccessfulResult("'" + changes.getFileName() + "' file.");
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-26")
                    .message("The file {{fileName}} does not mention the current version.", changes.getFileName())
                    .mitigation("Please, follow the changes file's format rules.").toString());
        }
        return report;
    }

    private boolean versionNumbersMatch(final String version, final Optional<String> releaseLetterVersion) {
        if (releaseLetterVersion.isEmpty()) {
            return false;
        }
        final String releaseLetterVersionValue = releaseLetterVersion.get();
        return releaseLetterVersionValue.equals(version) || releaseLetterVersionValue.equals("v" + version);
    }

    private Report validateDateInChanges(final ReleaseLetter changes, final boolean isDefaultBranch) {
        final var report = ValidationReport.create();
        final var dateToday = LocalDate.now();
        final Optional<LocalDate> releaseDate = changes.getReleaseDate();
        if (missingReleaseDate(isDefaultBranch, dateToday, releaseDate)) {
            report.merge(reportWrongDate(changes.getFileName()));
        }
        return report;
    }

    private boolean missingReleaseDate(final boolean isDefaultBranch, final LocalDate dateToday,
            final Optional<LocalDate> releaseDate) {
        return isDefaultBranch && (releaseDate.isEmpty() || !releaseDate.get().equals(dateToday));
    }

    private Report reportWrongDate(final String fileName) {
        final var report = ValidationReport.create();
        report.addSuccessfulResult("The release date in " + fileName
                + " is outdated, but the Release Droid will try to change it automatically. "
                + "If direct commits to the main branch are disabled for this repository, please, "
                + "update the date manually.");
        return report;
    }

    // [impl->dsn~validate-changes-file-contains-release-letter-body~1]
    private Report validateHasBody(final ReleaseLetter changes) {
        final var report = ValidationReport.create();
        if (changes.getBody().isEmpty()) {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-27")
                    .message("Cannot find the {{fileName}} body.", changes.getFileName()) //
                    .mitigation("Please, make sure you added the changes you made to the file.").toString());
        } else {
            report.addSuccessfulResult("Release body in '" + changes.getFileName() + "' file.");
        }
        return report;
    }
}