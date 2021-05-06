package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.adapter.RepositoryValidatorHelper.validateFileExists;
import static com.exasol.releasedroid.adapter.github.GitHubConstants.PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH;
import static com.exasol.releasedroid.adapter.github.GitHubConstants.PRINT_QUICK_CHECKSUM_WORKFLOW_PATH;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.VERSION_REGEX;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationResult;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Contains language-independent validations for a repository.
 */
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
        final var report = Report.validationReport();
        final String version = this.repository.getVersion();
        report.merge(validateVersion(version));
        if (!report.hasFailures()) {
            final String changelog = this.repository.getChangelogFile();
            report.merge(validateChangelog(changelog, version));
            final ReleaseLetter releaseLetter = this.repository.getReleaseLetter(version);
            report.merge(validateChanges(releaseLetter, version, this.repository.isOnDefaultBranch()));
        }
        report.merge(validateWorkflows());
        return report;
    }

    private Report validateWorkflows() {
        final var report = Report.validationReport();
        report.merge(validateFileExists(this.repository, PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH,
                "Workflow for running test and creating checksum."));
        report.merge(validateFileExists(this.repository, PRINT_QUICK_CHECKSUM_WORKFLOW_PATH,
                "Workflow for printing a checksum."));
        return report;
    }

    private Report validateVersion(final String version) {
        LOGGER.fine("Validating a new version.");
        final var report = Report.validationReport();
        report.merge(validateVersionFormat(version));
        if (!report.hasFailures()) {
            report.merge(validateIfNewReleaseTagValid(version));
        }
        return report;
    }

    private Report validateVersionFormat(final String version) {
        final var report = Report.validationReport();
        if (version != null && version.matches(VERSION_REGEX)) {
            report.addResult(ValidationResult.successfulValidation("Version format."));
        } else {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-GH-15")
                    .message("A version or tag found in this repository has invalid format: {{version|uq}}. "
                            + "The valid format is: <major>.<minor>.<fix>.", version)
                    .toString()));
        }
        return report;
    }

    private Report validateIfNewReleaseTagValid(final String newVersion) {
        final var report = Report.validationReport();
        final Optional<String> latestReleaseTag = this.repository.getLatestTag();
        if (latestReleaseTag.isPresent()) {
            report.merge(validateNewVersionWithPreviousTag(newVersion, latestReleaseTag.get()));
        } else {
            report.addResult(ValidationResult.successfulValidation("A new tag. This is the first release."));
        }
        return report;
    }

    // [impl->dsn~validate-release-version-format~1]
    // [impl->dsn~validate-release-version-increased-correctly~1]
    private Report validateNewVersionWithPreviousTag(final String newTag, final String latestTag) {
        final var report = Report.validationReport();
        final Set<String> possibleVersions = getPossibleVersions(latestTag);
        if (possibleVersions.contains(newTag)) {
            report.addResult(ValidationResult.successfulValidation("A new tag."));
        } else {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-GH-16")
                    .message(
                            "The new version {{newTag}} does not fit the versioning rules. "
                                    + "Possible versions for the release are: {{possibleVersions|uq}}",
                            newTag, possibleVersions.toString())
                    .toString()));
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
        final var report = Report.validationReport();
        final String changelogContent = "[" + version + "](changes_" + version + ".md)";
        if (changelog == null || !changelog.contains(changelogContent)) {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-GH-17")
                    .message("The file 'changelog.md' doesn't contain the following link.")
                    .mitigation("Please add {{changelogContent}} to the file.", changelogContent) //
                    .toString()));
        } else {
            report.addResult(ValidationResult.successfulValidation("'changelog.md' file."));
            LOGGER.fine("Validation of 'changelog.md' file was successful.");
        }
        return report;
    }

    private Report validateChanges(final ReleaseLetter releaseLetter, final String version,
            final boolean isDefaultBranch) {
        final var report = Report.validationReport();
        if (releaseLetter != null) {
            LOGGER.fine("Validating '" + releaseLetter.getFileName() + "' file.");
            report.merge(validateVersionInChanges(releaseLetter, version));
            report.merge(validateDateInChanges(releaseLetter, isDefaultBranch));
            report.merge(validateHasBody(releaseLetter));
        } else {
            report.addResult(ValidationResult.failedValidation(
                    ExaError.messageBuilder("E-RD-GH-26").message("The release letter does not exist.").toString()));
        }
        return report;
    }

    // [impl->dsn~validate-changes-file-contains-release-version~1]
    private Report validateVersionInChanges(final ReleaseLetter changes, final String version) {
        final var report = Report.validationReport();
        final Optional<String> versionNumber = changes.getVersionNumber();
        if ((versionNumber.isEmpty()) || !(versionNumber.get().equals(version))) {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-GH-18")
                    .message("The file {{fileName}} does not mention the current version.", changes.getFileName())
                    .mitigation("Please, follow the changes file's format rules.").toString()));
        } else {
            report.addResult(ValidationResult.successfulValidation("'" + changes.getFileName() + "' file."));
        }
        return report;
    }

    private Report validateDateInChanges(final ReleaseLetter changes, final boolean isDefaultBranch) {
        final var report = Report.validationReport();
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
        final var report = Report.validationReport();
        final var warningMessage = ExaError.messageBuilder("W-RD-GH-19").message(
                "The release date in {{fileName}} is outdated. The Release Droid will try to change it automatically. "
                        + "If direct commits to the main branch are disabled for this repository, please, "
                        + "update the date manually.",
                fileName) //
                .toString();
        report.addResult(ValidationResult.successfulValidation(
                "Skipping validation of release date in the '" + fileName + "' file. " + warningMessage));
        LOGGER.warning(warningMessage);
        return report;
    }

    // [impl->dsn~validate-changes-file-contains-release-letter-body~1]
    private Report validateHasBody(final ReleaseLetter changes) {
        final var report = Report.validationReport();
        if (changes.getBody().isEmpty()) {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-GH-20")
                    .message("Cannot find the {{fileName}} body.", changes.getFileName()) //
                    .mitigation("Please, make sure you added the changes you made to the file.").toString()));
        } else {
            report.addResult(
                    ValidationResult.successfulValidation("Release body in '" + changes.getFileName() + "' file."));
        }
        return report;
    }
}