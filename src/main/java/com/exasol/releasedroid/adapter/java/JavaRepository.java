package com.exasol.releasedroid.adapter.java;

import java.io.*;
import java.util.List;
import java.util.Map;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.adapter.maven.*;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.BaseRepository;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Maven-based Java repository.
 */
public class JavaRepository extends BaseRepository implements MavenRepository {
    private static final String POM_PATH = "pom.xml";
    private final Map<PlatformName, RepositoryValidator> releaseablePlatforms;
    private final List<RepositoryValidator> platformValidators;
    private MavenPom pom;

    public JavaRepository(final RepositoryGate repositoryGate, final GitHubGateway githubGateway) {
        super(repositoryGate);
        this.releaseablePlatforms = Map.of( //
                PlatformName.GITHUB, new GitHubPlatformValidator(this, githubGateway), PlatformName.MAVEN,
                new MavenPlatformValidator(this));
        this.platformValidators = List.of(new GitHubRepositoryValidator(this), new JavaRepositoryValidator(this));
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
                    ExaError.messageBuilder("E-RR-REP-4")
                            .message("Some problem happened during creating a temporary pom file.").toString(),
                    exception);
        }
    }

    @Override
    public String getVersion() {
        if (getMavenPom().hasVersion()) {
            return getMavenPom().getVersion();
        } else {
            throw new RepositoryException(ExaError.messageBuilder("E-RR-REP-5")
                    .message("Cannot find the current version in the repository.").toString());
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