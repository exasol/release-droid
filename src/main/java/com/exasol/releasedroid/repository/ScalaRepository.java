package com.exasol.releasedroid.repository;

import java.util.*;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.github.GitHubPlatformValidator;
import com.exasol.releasedroid.github.GithubGateway;
import com.exasol.releasedroid.usecases.validate.ScalaRepositoryValidator;
import com.exasol.releasedroid.usecases.Language;
import com.exasol.releasedroid.usecases.PlatformName;
import com.exasol.releasedroid.usecases.validate.GitRepositoryValidator;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Sbt-based scala repository.
 */
public class ScalaRepository extends BaseRepository {
    private static final String VERSION_PATTERN = "settings(version";
    protected static final String BUILD_SBT = "build.sbt";
    private final Map<PlatformName, RepositoryValidator> releaseablePlatforms;
    private final List<RepositoryValidator> platformValidators;

    public ScalaRepository(final RepositoryGate repositoryGate, final GithubGateway githubGateway) {
        super(repositoryGate);
        this.releaseablePlatforms = Map.of(PlatformName.GITHUB, new GitHubPlatformValidator(this, githubGateway));
        this.platformValidators = List.of(new GitRepositoryValidator(this), new ScalaRepositoryValidator(this));
    }

    @Override
    public String getVersion() {
        final String buildFile = getSingleFileContentAsString(BUILD_SBT);
        final Optional<String> version = getValueFromBuildFile(buildFile, VERSION_PATTERN);
        return version.orElseThrow(() -> new RepositoryException(ExaError.messageBuilder("E-RR-REP-9")
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
    public Language getRepositoryLanguage() {
        return Language.SCALA;
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