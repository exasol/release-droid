package com.exasol.releasedroid.adapter.repository;

import java.io.*;
import java.util.List;
import java.util.Map;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.communityportal.CommunityPlatformValidator;
import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.adapter.github.GitHubPlatformValidator;
import com.exasol.releasedroid.adapter.jira.JiraPlatformValidator;
import com.exasol.releasedroid.adapter.maven.*;
import com.exasol.releasedroid.usecases.repository.BaseRepository;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.ReleasePlatformValidator;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Maven-based Java repository.
 */
public class JavaRepository extends BaseRepository implements MavenRepository {
    private static final String POM_PATH = "pom.xml";
    private final List<RepositoryValidator> repositoryValidators = List.of(new CommonRepositoryValidator(this),
            new JavaRepositoryValidator(this));
    private final Map<PlatformName, ReleasePlatformValidator> platformValidators;
    private MavenPom pom;

    public JavaRepository(final RepositoryGate repositoryGate, final GitHubGateway githubGateway) {
        super(repositoryGate);
        this.platformValidators = Map.of( //
                PlatformName.GITHUB, new GitHubPlatformValidator(this, githubGateway), //
                PlatformName.MAVEN, new MavenPlatformValidator(this), //
                PlatformName.COMMUNITY, new CommunityPlatformValidator(this), //
                PlatformName.JIRA, new JiraPlatformValidator(this) //
        );
    }

    /**
     * Get a parsed Maven pom file.
     *
     * @return instance of {@link MavenPom}
     */
    @Override
    public MavenPom getMavenPom() {
        if (this.pom == null) {
            this.pom = parsePom();
        }
        return this.pom;
    }

    private MavenPom parsePom() {
        final String pomContent = getSingleFileContentAsString(POM_PATH);
        final File temporaryPomFile = createTemporaryPomFile(pomContent);
        return new MavenPomParser(temporaryPomFile).parse();
    }

    private File createTemporaryPomFile(final String pom) {
        try {
            final File tempPomFile = File.createTempFile("pomProjection", null);
            tempPomFile.deleteOnExit();
            try (final BufferedWriter out = new BufferedWriter(new FileWriter(tempPomFile))) {
                out.write(pom);
            }
            return tempPomFile;
        } catch (final IOException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-RD-REP-4")
                            .message("Some problem happened during creating a temporary pom file.").toString(),
                    exception);
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