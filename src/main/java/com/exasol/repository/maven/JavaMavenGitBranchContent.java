package com.exasol.repository.maven;

import java.util.Map;

import org.kohsuke.github.GHRepository;

import com.exasol.repository.AbstractGitHubGitBranchContent;
import com.exasol.repository.GitHubGitRepository;

/**
 * This class represents a Maven-based Java project's content.
 */
public class JavaMavenGitBranchContent extends AbstractGitHubGitBranchContent {
    private static final String POM_PATH = "pom.xml";
    private static final String PATH_TO_TARGET_DIR = "./target/";
    private final MavenPom pom;

    /**
     * Create a new instance of {@link GitHubGitRepository}.
     *
     * @param repository an instance of {@link GHRepository}
     * @param branch name of a branch to get content from
     */
    public JavaMavenGitBranchContent(final GHRepository repository, final String branch) {
        super(repository, branch);
        this.pom = parsePom();
    }

    private MavenPom parsePom() {
        final String pomContent = getSingleFileContentAsString(POM_PATH);
        return new MavenPomParser(pomContent).parse();
    }

    @Override
    public String getVersion() {
        return this.pom.getVersion();
    }

    @Override
    public Map<String, String> getDeliverables() {
        final String assetName = this.pom.getDeliverableName() + ".jar";
        final String assetPath = PATH_TO_TARGET_DIR + assetName;
        return Map.of(assetName, assetPath);
    }
}