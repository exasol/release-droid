package com.exasol.releasedroid.adapter.scala;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.communityportal.CommunityPlatformValidator;
import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.adapter.github.GitHubPlatformValidator;
import com.exasol.releasedroid.adapter.github.GitHubRepositoryValidator;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.BaseRepository;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Sbt-based scala repository.
 */
public class ScalaRepository extends BaseRepository {
    private static final String VERSION_PATTERN = "settings(version";
    protected static final String BUILD_SBT = "build.sbt";
    private final Map<PlatformName, RepositoryValidator> releaseablePlatforms;
    private final List<RepositoryValidator> platformValidators;

    public ScalaRepository(final RepositoryGate repositoryGate, final GitHubGateway githubGateway) {
        super(repositoryGate);
        this.releaseablePlatforms = Map.of(PlatformName.GITHUB, new GitHubPlatformValidator(this, githubGateway), //
                PlatformName.COMMUNITY, new CommunityPlatformValidator(this));
        this.platformValidators = List.of(new GitHubRepositoryValidator(this), new ScalaRepositoryValidator(this));
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
    public Map<PlatformName, RepositoryValidator> getValidatorForPlatforms() {
        return this.releaseablePlatforms;
    }

    @Override
    public List<RepositoryValidator> getStructureValidators() {
        return this.platformValidators;
    }
}