package com.exasol.repository;

import java.util.Map;

import org.kohsuke.github.GHRepository;

/**
 * This class represents a Maven-based Java project's content.
 */
public class JavaMavenGitBranchContent extends AbstractGitHubGitBranchContent {
    private final MavenPom pom;

    /**
     * Create a new instance of {@link GitHubGitRepository}.
     *
     * @param repository an instance of {@link GHRepository}
     * @param branch name of a branch to get content from
     */
    protected JavaMavenGitBranchContent(final GHRepository repository, final String branch) {
        super(repository, branch);
        this.pom = parsePom();
    }

    private MavenPom parsePom() {
        final String pomContent = getSingleFileContentAsString("pom.xml");
        return new MavenPomParser(pomContent).parse();
    }

    @Override
    public String getVersion() {
        return this.pom.getVersion();
    }

    @Override
    public Map<String, String> getDeliverables() {
        final String assetName = this.pom.getDeliverableName() + ".jar";
        final String assetPath = "./target/" + assetName;
        return Map.of(assetName, assetPath);
    }
}