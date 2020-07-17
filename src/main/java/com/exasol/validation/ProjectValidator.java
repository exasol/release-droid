package com.exasol.validation;

import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.ReleasePlatform;
import com.exasol.platform.GitHubRepository;

/**
 * This class contains a common part for validations.
 */
public class ProjectValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectValidator.class);
    protected final GitHubRepository repository;
    private final String version;

    /**
     * Create a new instance of {@link ProjectValidator}.
     *
     * @param repository repository to validate
     * @param version
     */
    public ProjectValidator(final GitHubRepository repository, final String version) {
        this.repository = repository;
        this.version = version;
    }

    /**
     * Validate common release requirements.
     */
    public void validatePlatformIndependent() {
        final String changelog = this.repository.getSingleFileContentAsString("doc/changes/changelog.md");
        validateChangelog(changelog, this.version);
        final String changes = getChanges(this.version);
        validateChanges(changes, this.version);
    }

    protected void validateChangelog(final String changelog, final String version) {
        LOGGER.info("Validating changelog.md file.");
        final String changelogContent = "[" + version + "](changes-" + version + ".md)";
        if (!changelog.contains(changelogContent)) {
            throw new IllegalStateException(
                    "doc/changes/changelog.md file doesn't contain the following link, please add it to the file: "
                            + changelogContent);
        }
    }

    private String getChanges(final String version) {
        final String changesFileName = "changes-" + version + ".md";
        return this.repository.getSingleFileContentAsString("doc/changes/" + changesFileName);
    }

    protected void validateChanges(final String changes, final String version) {
        final String changesName = "changes-" + version + ".md";
        LOGGER.info("Validating {} file.", changesName);
        if (!changes.contains(version)) {
            throw new IllegalStateException(changesName + " file doesn't contains a new version mentions.");
        }
        final String dateToday = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        if (!changes.contains(dateToday)) {
            throw new IllegalStateException("changes-" + version + ".md file doesn't contain release's date: "
                    + dateToday + ". PLease, add or update the release date.");
        }
    }

    /**
     * Validate release requirements depending on the platform.
     * 
     * @param releasePlatform release platform
     */
    public void validatePlatform(final ReleasePlatform releasePlatform) {
        if (releasePlatform == ReleasePlatform.GITHUB) {
            validateGitHub();
        } else {
            throw new IllegalArgumentException(
                    "Validation for release platform " + releasePlatform + " is not supported");
        }
    }

    protected void validateGitHub() {
        LOGGER.info("Validating github specific requirements.");
        final Optional<String> latestReleaseTag = this.repository.getLatestReleaseVersion();
        validateVersion(this.version, latestReleaseTag);
    }

    protected void validateVersion(final String version, final Optional<String> latestReleaseTag) {
        LOGGER.info("Validating a release version.");
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