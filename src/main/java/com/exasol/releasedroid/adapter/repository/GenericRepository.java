package com.exasol.releasedroid.adapter.repository;

import java.util.List;
import java.util.Map;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.communityportal.CommunityPlatformValidator;
import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.adapter.github.GitHubPlatformValidator;
import com.exasol.releasedroid.adapter.jira.JiraPlatformValidator;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.BaseRepository;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.ReleasePlatformValidator;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Represents a repository with undefined programming language. Allows providing minimum release functionality for
 * unsupported programming languages.
 */
public class GenericRepository extends BaseRepository {
    private final List<RepositoryValidator> repositoryValidators = List.of(new CommonRepositoryValidator(this));
    private final Map<PlatformName, ReleasePlatformValidator> platformValidators;

    /**
     * Create a new instance of {@link GenericRepository}.
     *
     * @param repositoryGate repository gate
     * @param githubGateway  github gateway
     */
    public GenericRepository(final RepositoryGate repositoryGate, final GitHubGateway githubGateway) {
        super(repositoryGate);
        this.platformValidators = Map.of( //
                PlatformName.GITHUB, new GitHubPlatformValidator(this, githubGateway), //
                PlatformName.COMMUNITY, new CommunityPlatformValidator(this), //
                PlatformName.JIRA, new JiraPlatformValidator(this) //
        );
    }

    @Override
    public String getVersion() {
        final String changelogFile = super.getChangelogFile();
        final int from = changelogFile.indexOf('[');
        final int to = changelogFile.indexOf(']');
        if (from == -1 || to == -1 || to < from) {
            throw new RepositoryException(ExaError.messageBuilder("E-RD-REP-21")
                    .message("Cannot detect the version of the project.")
                    .mitigation("Please make sure you specified the version in the changelog.md file").toString());
        }
        return changelogFile.substring(from + 1, to);
    }

    @Override
    public List<RepositoryValidator> getRepositoryValidators() {
        return this.repositoryValidators;
    }

    @Override
    public Map<PlatformName, ReleasePlatformValidator> getPlatformValidators() {
        return this.platformValidators;
    }
}