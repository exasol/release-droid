package com.exasol.releasedroid.adapter;

import static com.exasol.releasedroid.adapter.github.GitHubConstants.*;

import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.formatting.ChecksumFormatter;
import com.exasol.releasedroid.output.guide.ReleaseGuide;
import com.exasol.releasedroid.progress.Estimation;
import com.exasol.releasedroid.progress.Progress;
import com.exasol.releasedroid.usecases.release.ReleaseManager;
import com.exasol.releasedroid.usecases.repository.Repository;

/**
 * Implementation of ReleaseManager
 */
@SuppressWarnings("java:S1192") // Removing string duplicates here will decrease readability.
public class ReleaseManagerImpl implements ReleaseManager {
    private static final Logger LOGGER = Logger.getLogger(ReleaseManagerImpl.class.getName());
    private final GitHubGateway githubGateway;
    private Progress progress;

    public ReleaseManagerImpl(final GitHubGateway githubGateway) {
        this.githubGateway = githubGateway;
    }

    @Override
    public void prepareForRelease(final Repository repository) {
        if (hasChecksumBuilds(repository)) {
            runChecksumBuildWorkflows(repository);
        }
    }

    // [impl->dsn~estimate-duration~1]
    @Override
    public Progress estimateDuration(final Repository repository, final Estimation platformEstimations) {
        final Estimation estimation = this.githubGateway.estimateDuration(repository.getName(), //
                PREPARE_ORIGINAL_CHECKSUM_WORKFLOW) //
                .plus(platformEstimations);
        this.progress = Progress.builder().estimation(estimation).start();
        final String prefix = this.progress.startTime() + ": Starting release process";
        LOGGER.info(() -> this.progress.welcomeMessage(prefix));
        return this.progress;
    }

    @Override
    public void generateReleaseGuide(final Repository repository, final String gitHubTagUrl, final Path destination) {
        ReleaseGuide.from(repository, gitHubTagUrl).write(destination);
    }

    private boolean hasChecksumBuilds(final Repository repository) {
        return repository.hasFile(PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH)
                && repository.hasFile(PRINT_QUICK_CHECKSUM_WORKFLOW_PATH);
    }

    private void runChecksumBuildWorkflows(final Repository repository) {
        try {
            final List<Long> artifactIds = getArtifactIds(repository.getName());
            if (artifactIds.isEmpty()) {
                createOriginalChecksum(repository);
            } else if (artifactIds.size() == 1) {
                validateOriginalChecksumAgainstQuickChecksum(repository, artifactIds.get(0));
            } else {
                updateOutdatedArtifactory(repository);
            }
        } catch (final GitHubException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private void createOriginalChecksum(final Repository repository) throws GitHubException {
        LOGGER.info("There are no artifacts on the '" + repository.getName() + "' repository.");
        prepareChecksumArtifact(repository);
    }

    private void validateOriginalChecksumAgainstQuickChecksum(final Repository repository, final long artifactId)
            throws GitHubException {
        LOGGER.info("Found an artifact on '" + repository.getName() + "' repository.");
        if (!validateChecksum(artifactId, repository.getName())) {
            LOGGER.info("Checksum validation for '" + repository.getName() + "' repository failed.");
            updateRepository(repository);
        }
    }

    private void updateOutdatedArtifactory(final Repository repository) {
        LOGGER.info("There are more than one artifact on the '" + repository.getName() + "' repository.");
        updateRepository(repository);
    }

    private void updateRepository(final Repository repository) {
        cleanUpAfterRelease(repository);
        prepareForRelease(repository);
    }

    // [impl->dsn~compare-checksum~1]
    private boolean validateChecksum(final long artifactId, final String repositoryName) throws GitHubException {
        final Map<String, String> originalChecksum = getOriginalChecksum(artifactId, repositoryName);
        final Map<String, String> quickChecksum = getQuickChecksum(repositoryName);
        if (originalChecksum.size() != quickChecksum.size()) {
            return false;
        }
        return validateChecksumForEachJar(originalChecksum, quickChecksum);
    }

    private Map<String, String> getOriginalChecksum(final long artifactId, final String repositoryName)
            throws GitHubException {
        final String artifact = this.githubGateway.downloadArtifactAsString(repositoryName, artifactId);
        return ChecksumFormatter.createChecksumMap(artifact);
    }

    private Map<String, String> getQuickChecksum(final String repositoryName) throws GitHubException {
        final String logs = this.githubGateway.executeWorkflowWithLogs( //
                repositoryName, PRINT_QUICK_CHECKSUM_WORKFLOW, //
                new WorkflowOptions().withProgress(this.progress));
        return formatChecksumLogs(logs);
    }

    private Map<String, String> formatChecksumLogs(final String logs) {
        final String[] splittedLogs = logs
                .substring(logs.lastIndexOf("checksum_start=="), logs.lastIndexOf("==checksum_end")).replace("\n", " ")
                .split(" ");
        return ChecksumFormatter
                .createChecksumMap(String.join(" ", Arrays.asList(splittedLogs).subList(2, splittedLogs.length - 1)));
    }

    private boolean validateChecksumForEachJar(final Map<String, String> originalChecksum,
            final Map<String, String> quickChecksum) {
        for (final Map.Entry<String, String> checksumForJar : originalChecksum.entrySet()) {
            final String jarName = checksumForJar.getKey();
            if (!quickChecksum.containsKey(jarName)) {
                return false;
            }
            if (!isJavadoc(jarName) && !checksumsEqual(checksumForJar.getValue(), quickChecksum.get(jarName))) {
                return false;
            }
        }
        return true;
    }

    private boolean isJavadoc(final String jarName) {
        return jarName.contains("javadoc");
    }

    private boolean checksumsEqual(final String originalChecksum, final String quickChecksum) {
        return originalChecksum.equals(quickChecksum);
    }

    @Override
    // [impl->dsn~remove-checksum~1]
    public void cleanUpAfterRelease(final Repository repository) {
        LOGGER.info("Removing all artifacts from '" + repository.getName() + "' repository.");
        try {
            this.githubGateway.deleteAllArtifacts(repository.getName());
        } catch (final GitHubException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private List<Long> getArtifactIds(final String repositoryName) throws GitHubException {
        return this.githubGateway.getRepositoryArtifactsIds(repositoryName);
    }

    // [impl->dsn~prepare-checksum~1]
    private void prepareChecksumArtifact(final Repository repository) throws GitHubException {
        LOGGER.info("Preparing a new artifact with a checksum.");
        this.githubGateway.executeWorkflow(repository.getName(), //
                PREPARE_ORIGINAL_CHECKSUM_WORKFLOW, //
                new WorkflowOptions().withProgress(this.progress));
    }
}