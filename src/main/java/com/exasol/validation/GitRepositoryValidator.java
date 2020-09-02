package com.exasol.validation;

import static com.exasol.ReleaseRobotConstants.VERSION_REGEX;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.git.*;

/**
 * Contains validations for a Git project.
 */
public class GitRepositoryValidator {
    private static final Logger LOGGER = Logger.getLogger(GitRepositoryValidator.class.getName());
    private final GitRepository repository;

    /**
     * Create a new instance of {@link GitRepositoryValidator}.
     *
     * @param repository instance of {@link GitRepository} to validate
     */
    public GitRepositoryValidator(final GitRepository repository) {
        this.repository = repository;
    }

    /**
     * Validate content of a Git-based repository.
     *
     * @param branch name of a branch to validate on
     */
    public void validate(final String branch) {
        LOGGER.fine("Validating Git repository.");
        final GitRepositoryContent content = this.repository.getRepositoryContent(branch);
        final String version = content.getVersion();
        validateNewVersion(version);
        final String changelog = content.getChangelogFile();
        validateChangelog(changelog, version);
        final ReleaseChangesLetter changes = content.getReleaseChangesLetter(version);
        validateChanges(changes, version);
    }

    protected void validateNewVersion(final String newVersion) {
        LOGGER.fine("Validating a new version.");
        validateVersionFormat(newVersion);
        final Optional<String> latestReleaseTag = this.repository.getLatestTag();
        if (latestReleaseTag.isPresent()) {
            validateNewVersionWithPreviousTag(newVersion, latestReleaseTag.get());
        } else {
            validateFirstNewVersion(newVersion);
        }
    }

    private void validateVersionFormat(final String version) {
        if (!version.matches(VERSION_REGEX)) {
            throw new IllegalArgumentException("A version or tag found in this repository has invalid format. "
                    + "The valid format is: <major>.<minor>.<fix>. "
                    + "Please, refer to the user guide to check requirements.");
        }
    }

    private void validateFirstNewVersion(final String newVersion) {
        final Set<String> validFirstTag = Set.of("1.0.0", "0.1.0", "0.0.1");
        if (!validFirstTag.contains(newVersion)) {
            throw new IllegalArgumentException(
                    "A new version has invalid format. Allowed first tags are: " + String.join(", ", validFirstTag));
        }
    }

    private void validateNewVersionWithPreviousTag(final String newTag, final String latestTag) {
        final Set<String> possibleVersions = getPossibleVersions(latestTag);
        if (!possibleVersions.contains(newTag)) {
            throw new IllegalArgumentException(
                    "A new version does not fit the versioning rules. Possible versions for the release are: "
                            + possibleVersions.toString());
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

    protected void validateChangelog(final String changelog, final String version) {
        LOGGER.fine("Validating `changelog.md` file.");
        final String changelogContent = "[" + version + "](changes_" + version + ".md)";
        if (!changelog.contains(changelogContent)) {
            throw new IllegalStateException(
                    "changelog.md file doesn't contain the following link, please add it to the file: "
                            + changelogContent);
        }
        LOGGER.fine("Validation of `changelog.md` file was successful.");
    }

    protected void validateChanges(final ReleaseChangesLetter changes, final String version) {
        LOGGER.fine("Validating " + changes.getFileName() + " file.");
        validateVersionInChanges(changes, version);
        validateDateInChanges(changes);
        validateHasBody(changes);
    }

    private void validateVersionInChanges(final ReleaseChangesLetter changes, final String version) {
        final Optional<String> versionNumber = changes.getVersionNumber();
        if ((versionNumber.isEmpty()) || !(versionNumber.get().equals(version))) {
            throw new IllegalStateException(changes.getFileName()
                    + " file does not mention the current version. Please, follow the changes file's format rules.");
        }
    }

    private void validateDateInChanges(final ReleaseChangesLetter changes) {
        final LocalDate dateToday = LocalDate.now();
        final Optional<LocalDate> releaseDate = changes.getReleaseDate();
        if ((releaseDate.isEmpty()) || !(releaseDate.get().equals(dateToday))) {
            throw new IllegalStateException(changes.getFileName() + " file doesn't contain release's date: "
                    + dateToday.toString() + ". PLease, add or update the release date.");
        }
    }

    private void validateHasBody(final ReleaseChangesLetter changes) {
        if (changes.getBody().isEmpty()) {
            throw new IllegalStateException("Cannot find the " + changes.getFileName()
                    + " body. Please, make sure you added the changes you made to the file.");
        }
    }
}