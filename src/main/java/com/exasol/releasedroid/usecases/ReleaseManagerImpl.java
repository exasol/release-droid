package com.exasol.releasedroid.usecases;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.exasol.releasedroid.github.GitHubException;
import com.exasol.releasedroid.github.GithubGateway;

public class ReleaseManagerImpl implements ReleaseManager {
    private static final Logger LOGGER = Logger.getLogger(ReleaseManagerImpl.class.getName());
    private final RepositoryModifier repositoryModifier;
    private final GithubGateway githubGateway;

    public ReleaseManagerImpl(final RepositoryModifier repositoryModifier, final GithubGateway githubGateway) {
        this.repositoryModifier = repositoryModifier;
        this.githubGateway = githubGateway;
    }

    @Override
    public void prepareForRelease(final Repository repository) throws GitHubException {
        final List<String> artifactIds = getArtifactIds(repository.getName());
        if (artifactIds.size() > 1) {
            LOGGER.info("There are more than one artifact on the '" + repository.getName() + "' repository.");
            updateRepository(repository);
        } else if (artifactIds.isEmpty()) {
            LOGGER.info("There are no artifacts on the '" + repository.getName() + "' repository.");
            this.repositoryModifier.writeReleaseDate(repository);
            prepareArtifact(repository);
        } else {
            LOGGER.info("Found an artifact on '" + repository.getName() + "' repository.");
            if (!validateCheckSum(artifactIds, repository.getName())) {
                LOGGER.info("Checksum validation for '" + repository.getName() + "' repository failed.");
                updateRepository(repository);
            }
        }
    }

    private void updateRepository(final Repository repository) throws GitHubException {
        cleanUpAfterRelease(repository);
        prepareForRelease(repository);
    }

    private boolean validateCheckSum(final List<String> artifactIds, final String repositoryName)
            throws GitHubException {
        final Map<String, String> checksum = this.githubGateway.downloadChecksumFromArtifactory(repositoryName,
                artifactIds.get(0));
        final Map<String, String> quickChecksum = this.githubGateway.createQuickCheckSum(repositoryName);
        if (checksum.size() != quickChecksum.size()) {
            return false;
        }
        for (final Map.Entry<String, String> entry : checksum.entrySet()) {
            if (!quickChecksum.containsKey(entry.getKey())) {
                return false;
            }
            if (!entry.getKey().contains("javadoc") && !quickChecksum.get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void cleanUpAfterRelease(final Repository repository) throws GitHubException {
        LOGGER.info("Removing all artifacts from '" + repository.getName() + "' repository.");
        this.githubGateway.deleteAllArtifacts(repository.getName());
    }

    private List<String> getArtifactIds(final String repositoryName) throws GitHubException {
        return this.githubGateway.getRepositoryArtifacts(repositoryName);
    }

    private void prepareArtifact(final Repository repository) throws GitHubException {
        LOGGER.info("Preparing a new artifact with a checksum.");
        this.githubGateway.createChecksumArtifact(repository.getName());
    }
}