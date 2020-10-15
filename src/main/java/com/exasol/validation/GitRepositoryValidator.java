package com.exasol.validation;

import static com.exasol.ReleaseRobotConstants.VERSION_REGEX;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.repository.*;

/**
 * Contains validations for a Git project.
 */
public class GitRepositoryValidator {
    private static final Logger LOGGER = Logger.getLogger(GitRepositoryValidator.class.getName());
    private final GitRepository repository;
    private final ValidationReport validationReport;

    /**
     * Create a new instance of {@link GitRepositoryValidator}.
     *
     * @param repository       instance of {@link GitRepository} to validate
     * @param validationReport instance of {@link ValidationReport}
     */
    public GitRepositoryValidator(final GitRepository repository, final ValidationReport validationReport) {
        this.repository = repository;
        this.validationReport = validationReport;
    }

    /**
     * Validate content of a Git-based repository.
     *
     * @param branch name of a branch to validate on
     */
    public void validate(final String branch) {
        LOGGER.fine("Validating Git repository.");
        final GitBranchContent content = this.repository.getRepositoryContent(branch);
        final String version = content.getVersion();
        final boolean versionIsValid = validateNewVersion(version);
        if (versionIsValid) {
            final String changelog = content.getChangelogFile();
            validateChangelog(changelog, version);
            final ReleaseLetter changes = content.getReleaseLetter(version);
            validateChanges(changes, version, content.isDefaultBranch());
        }
    }

    protected boolean validateNewVersion(final String newVersion) {
        LOGGER.fine("Validating a new version.");
        final boolean correctVersionFormat = validateVersionFormat(newVersion);
        boolean newReleaseTagIsValid = false;
        if (correctVersionFormat) {
            newReleaseTagIsValid = validateIfNewReleaseTagValid(newVersion);
        }
        return correctVersionFormat && newReleaseTagIsValid;
    }

    private boolean validateVersionFormat(final String version) {
        if (version.matches(VERSION_REGEX)) {
            this.validationReport.addSuccessfulValidation("Version format.");
            return true;
        } else {
            this.validationReport.addFailedValidations("E-RR-VAL-3",
                    "A version or tag found in this repository has invalid format. "
                            + "The valid format is: <major>.<minor>.<fix>. "
                            + "Please, refer to the user guide to check requirements.");
            return false;
        }
    }

    private boolean validateIfNewReleaseTagValid(final String newVersion) {
        final Optional<String> latestReleaseTag = this.repository.getLatestTag();
        if (latestReleaseTag.isPresent()) {
            return validateNewVersionWithPreviousTag(newVersion, latestReleaseTag.get());
        } else {
            this.validationReport.addSuccessfulValidation("A new tag. This is the first release.");
            return true;
        }
    }
    // [impl->dsn~validate-release-version-format~1]

    // [impl->dsn~validate-release-version-increased-correctly~1]
    private boolean validateNewVersionWithPreviousTag(final String newTag, final String latestTag) {
        final Set<String> possibleVersions = getPossibleVersions(latestTag);
        if (possibleVersions.contains(newTag)) {
            this.validationReport.addSuccessfulValidation("A new tag.");
            return true;
        } else {
            this.validationReport.addFailedValidations("E-RR-VAL-4",
                    "A new version does not fit the versioning rules. Possible versions for the release are: "
                            + possibleVersions.toString());
            return false;
        }
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
    protected void validateChangelog(final String changelog, final String version) {
        LOGGER.fine("Validating 'changelog.md' file.");
        final String changelogContent = "[" + version + "](changes_" + version + ".md)";
        if (!changelog.contains(changelogContent)) {
            this.validationReport.addFailedValidations("E-RR-VAL-5",
                    "The file 'changelog.md' doesn't contain the following link, please add '" + changelogContent
                            + "' to the file.");
        } else {
            this.validationReport.addSuccessfulValidation("'changelog.md' file.");
            LOGGER.fine("Validation of 'changelog.md' file was successful.");
        }
    }

    protected void validateChanges(final ReleaseLetter changes, final String version, final boolean isDefaultBranch) {
        LOGGER.fine("Validating '" + changes.getFileName() + "' file.");
        validateVersionInChanges(changes, version);
        validateDateInChanges(changes, isDefaultBranch);
        validateHasBody(changes);
    }

    // [impl->dsn~validate-changes-file-contains-release-version~1]
    private void validateVersionInChanges(final ReleaseLetter changes, final String version) {
        final Optional<String> versionNumber = changes.getVersionNumber();
        if ((versionNumber.isEmpty()) || !(versionNumber.get().equals(version))) {
            this.validationReport.addFailedValidations("E-RR-VAL-6", "The file '" + changes.getFileName()
                    + "' does not mention the current version. Please, follow the changes file's format rules.");
        } else {
            this.validationReport.addSuccessfulValidation("'" + changes.getFileName() + "' file.");
        }
    }

    // [impl->dsn~validate-changes-file-contains-release-date~1]
    private void validateDateInChanges(final ReleaseLetter changes, final boolean isDefaultBranch) {
        final LocalDate dateToday = LocalDate.now();
        final Optional<LocalDate> releaseDate = changes.getReleaseDate();
        if ((releaseDate.isEmpty()) || !(releaseDate.get().equals(dateToday))) {
            reportWrongDate(changes.getFileName(), isDefaultBranch, dateToday);
        } else {
            this.validationReport.addSuccessfulValidation("Release date in '" + changes.getFileName() + "' file.");
        }
    }

    private void reportWrongDate(final String fileName, final boolean isDefaultBranch, final LocalDate dateToday) {
        if (isDefaultBranch) {
            this.validationReport.addFailedValidations("E-RR-VAL-7",
                    "The file '" + fileName + "' doesn't contain release's date: " + dateToday.toString()
                            + ". PLease, add or update the release date.");
        } else {
            final String warningMessage = "Don't forget to change the date in the '" + fileName
                    + "' file before you release.";
            this.validationReport.addSuccessfulValidation(
                    "Skipping validation of release date in the '" + fileName + "' file. " + warningMessage);
            LOGGER.warning(warningMessage);
        }
    }

    // [impl->dsn~validate-changes-file-contains-release-letter-body~1]
    private void validateHasBody(final ReleaseLetter changes) {
        if (changes.getBody().isEmpty()) {
            this.validationReport.addFailedValidations("E-RR-VAL-8", "Cannot find the '" + changes.getFileName()
                    + "' body. Please, make sure you added the changes you made to the file.");
        } else {
            this.validationReport.addSuccessfulValidation("Release body in '" + changes.getFileName() + "' file.");

        }
    }
}