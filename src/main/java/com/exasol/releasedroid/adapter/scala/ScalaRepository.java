package com.exasol.releasedroid.adapter.scala;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.communityportal.CommunityPlatformValidator;
import com.exasol.releasedroid.adapter.github.CommonRepositoryValidator;
import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.adapter.github.GitHubPlatformValidator;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.BaseRepository;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.ReleasePlatformValidator;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Sbt-based scala repository.
 */
public class ScalaRepository extends BaseRepository {
    private static final String VERSION_PATTERN = "settings(version";
    protected static final String BUILD_SBT = "build.sbt";
    private final List<RepositoryValidator> repositoryValidators = List.of(new CommonRepositoryValidator(this),
            new ScalaRepositoryValidator(this));
    private final Map<PlatformName, ReleasePlatformValidator> platformValidators;

    public ScalaRepository(final RepositoryGate repositoryGate, final GitHubGateway githubGateway) {
        super(repositoryGate);
        this.platformValidators = Map.of( //
                PlatformName.GITHUB, new GitHubPlatformValidator(this, githubGateway), //
                PlatformName.COMMUNITY, new CommunityPlatformValidator(this) //
        );
    }

    @Override
    public String getVersion() {
        final String buildFile = getSingleFileContentAsString(BUILD_SBT);
        final Optional<String> version = getValueFromBuildFile(buildFile, VERSION_PATTERN);
        return version.orElseThrow(() -> new RepositoryException(ExaError.messageBuilder("E-RD-REP-9")
                .message("Cannot detect Scala's project version in {{filename}} file.") //
                .parameter("filename", BUILD_SBT).toString()));

    }

    private Optional<String> getValueFromBuildFile(final String buildFile, final String pattern) {
        if (buildFile.contains(pattern)) {
            final int moduleName = buildFile.indexOf(pattern);
            final int start = buildFile.indexOf("\"", moduleName);
            final int end = buildFile.indexOf("\"", start + 1);
            return Optional.of(buildFile.substring(start + 1, end));
        } else {
            return Optional.empty();
        }
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