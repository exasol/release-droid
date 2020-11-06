package com.exasol.releaserobot.usecases.validate;

import static com.exasol.releaserobot.usecases.ReleaseRobotConstants.VERSION_REGEX;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.releaserobot.repository.*;
import com.exasol.releaserobot.usecases.*;

/**
 * Contains validations for a Git project.
 */
public class GitRepositoryValidator implements RepositoryValidator {
    private static final Logger LOGGER = Logger.getLogger(GitRepositoryValidator.class.getName());

    @Override
    public Report validate(final RepositoryTOGOAWAY repository) {
        final Repository branch = repository.getBranch();
        LOGGER.fine("Validating Git repository on branch '" + branch.getBranchName() + "'.");
        final Report report = ReportImpl.validationReport();
        final String version = branch.getVersion();
        report.merge(validateNewVersion(version, repository));
        if (!report.hasFailures()) {
            final String changelog = branch.getChangelogFile();
            report.merge(validateChangelog(changelog, version));
            final ReleaseLetter changes = branch.getReleaseLetter(version);
            report.merge(validateChanges(changes, version, branch.isDefaultBranch()));
        }
        return report;
    }

    protected Report validateNewVersion(final String newVersion, final RepositoryTOGOAWAY repository) {
        LOGGER.fine("Validating a new version.");
        final Report report = ReportImpl.validationReport();
        report.merge(validateVersionFormat(newVersion));
        if (!report.hasFailures()) {
            report.merge(validateIfNewReleaseTagValid(newVersion, repository));
        }
        return report;
    }

    private Report validateVersionFormat(final String version) {
        final Report report = ReportImpl.validationReport();
        if (version.matches(VERSION_REGEX)) {
            report.addResult(ValidationResult.successfulValidation("Version format."));
        } else {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-3",
                    "A version or tag found in this repository has invalid format: " + version
                            + ". The valid format is: <major>.<minor>.<fix>. "
                            + "Please, refer to the user guide to check requirements."));
        }
        return report;
    }

    private Report validateIfNewReleaseTagValid(final String newVersion, final RepositoryTOGOAWAY repository) {
        final Report report = ReportImpl.validationReport();
        final Optional<String> latestReleaseTag = repository.getLatestTag();
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
        final Report report = ReportImpl.validationReport();
        final Set<String> possibleVersions = getPossibleVersions(latestTag);
        if (possibleVersions.contains(newTag)) {
            report.addResult(ValidationResult.successfulValidation("A new tag."));
        } else {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-4",
                    "A new version does not fit the versioning rules. Possible versions for the release are: "
                            + possibleVersions.toString()));
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
    protected Report validateChangelog(final String changelog, final String version) {
        LOGGER.fine("Validating 'changelog.md' file.");
        final Report report = ReportImpl.validationReport();
        final String changelogContent = "[" + version + "](changes_" + version + ".md)";
        if (!changelog.contains(changelogContent)) {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-5",
                    "The file 'changelog.md' doesn't contain the following link, please add '" + changelogContent
                            + "' to the file."));
        } else {
            report.addResult(ValidationResult.successfulValidation("'changelog.md' file."));
            LOGGER.fine("Validation of 'changelog.md' file was successful.");
        }
        return report;
    }

    protected Report validateChanges(final ReleaseLetter changes, final String version, final boolean isDefaultBranch) {
        LOGGER.fine("Validating '" + changes.getFileName() + "' file.");
        final Report report = ReportImpl.validationReport();
        report.merge(validateVersionInChanges(changes, version));
        report.merge(validateDateInChanges(changes, isDefaultBranch));
        report.merge(validateHasBody(changes));
        return report;
    }

    // [impl->dsn~validate-changes-file-contains-release-version~1]
    private Report validateVersionInChanges(final ReleaseLetter changes, final String version) {
        final Report report = ReportImpl.validationReport();
        final Optional<String> versionNumber = changes.getVersionNumber();
        if ((versionNumber.isEmpty()) || !(versionNumber.get().equals(version))) {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-6", "The file '" + changes.getFileName()
                    + "' does not mention the current version. Please, follow the changes file's format rules."));
        } else {
            report.addResult(ValidationResult.successfulValidation("'" + changes.getFileName() + "' file."));
        }
        return report;
    }

    // [impl->dsn~validate-changes-file-contains-release-date~1]
    private Report validateDateInChanges(final ReleaseLetter changes, final boolean isDefaultBranch) {
        final Report report = ReportImpl.validationReport();
        final LocalDate dateToday = LocalDate.now();
        final Optional<LocalDate> releaseDate = changes.getReleaseDate();
        if ((releaseDate.isEmpty()) || !(releaseDate.get().equals(dateToday))) {
            report.merge(reportWrongDate(changes.getFileName(), isDefaultBranch, dateToday));
        } else {
            report.addResult(
                    ValidationResult.successfulValidation("Release date in '" + changes.getFileName() + "' file."));
        }
        return report;
    }

    private Report reportWrongDate(final String fileName, final boolean isDefaultBranch, final LocalDate dateToday) {
        final Report report = ReportImpl.validationReport();
        if (isDefaultBranch) {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-7",
                    "The file '" + fileName + "' doesn't contain release's date: " + dateToday.toString()
                            + ". PLease, add or update the release date."));
        } else {
            final String warningMessage = "W-RR-VAL-2. Don't forget to change the date in the '" + fileName
                    + "' file before you release.";
            report.addResult(ValidationResult.successfulValidation(
                    "Skipping validation of release date in the '" + fileName + "' file. " + warningMessage));
            LOGGER.warning(warningMessage);
        }
        return report;
    }

    // [impl->dsn~validate-changes-file-contains-release-letter-body~1]
    private Report validateHasBody(final ReleaseLetter changes) {
        final Report report = ReportImpl.validationReport();
        if (changes.getBody().isEmpty()) {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-8", "Cannot find the '" + changes.getFileName()
                    + "' body. Please, make sure you added the changes you made to the file."));
        } else {
            report.addResult(
                    ValidationResult.successfulValidation("Release body in '" + changes.getFileName() + "' file."));
        }
        return report;
    }

}