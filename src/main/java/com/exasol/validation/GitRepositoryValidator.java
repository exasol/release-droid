package com.exasol.validation;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.git.GitRepository;
import com.exasol.git.GitRepositoryContent;

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
        final String changelog = content.getChangelogFile();
        final String version = content.getVersion();
        final String changes = content.getChangesFile(version);
        validateChangelog(changelog, version);
        validateChanges(changes, version);
        final Optional<String> latestReleaseTag = this.repository.getLatestTag();
        validateVersion(version, latestReleaseTag);
    }

    /**
     * Validate content of a Git-based repository.
     */
    public void validate() {
        validate(this.repository.getDefaultBranchName());
    }

    protected void validateChangelog(final String changelog, final String version) {
        LOGGER.fine("Validating changelog.md file.");
        final String changelogContent = "[" + version + "](changes_" + version + ".md)";
        if (!changelog.contains(changelogContent)) {
            throw new IllegalStateException(
                    "changelog.md file doesn't contain the following link, please add it to the file: "
                            + changelogContent);
        }
    }

    protected void validateChanges(final String changes, final String version) {
        final String changesName = "changes_" + version + ".md";
        LOGGER.fine("Validating " + changesName + " file.");
        validateVersionInChanges(changes, version, changesName);
        validateDateInChanges(changes, version);
        validateMoreThanOneLine(changes);
    }

    private void validateMoreThanOneLine(final String changes) {
        if (changes.indexOf('\n') == -1) {
            throw new IllegalStateException(
                    "The changes file contains 1 or less lines. Please, add the changes you made before the release.");
        }
    }

    private void validateVersionInChanges(final String changes, final String version, final String changesName) {
        if (!changes.contains(version)) {
            throw new IllegalStateException(changesName
                    + " file does not mention the current version. Please add a new entry for this version.");
        }
    }

    private void validateDateInChanges(final String changes, final String version) {
        final String dateToday = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        if (!changes.contains(dateToday)) {
            throw new IllegalStateException("changes_" + version + ".md file doesn't contain release's date: "
                    + dateToday + ". PLease, add or update the release date.");
        }
    }

    protected void validateVersion(final String version, final Optional<String> latestReleaseTag) {
        LOGGER.fine("Validating a release version.");
        if (latestReleaseTag.isPresent()) {
            final Set<String> possibleVersions = getPossibleVersions(latestReleaseTag.get());
            if (!possibleVersions.contains(version)) {
                throw new IllegalStateException(
                        "A new version doesn't fit the versioning rules. Possible versions for the release are: "
                                + possibleVersions.toString());
            }
        } else {
            final String[] versionParts = version.split("\\.");
            if (versionParts.length != 3) {
                throw new IllegalStateException(
                        "The version has invalid format. The valid format is: <major>.<minor>.<fix>");
            }
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
}