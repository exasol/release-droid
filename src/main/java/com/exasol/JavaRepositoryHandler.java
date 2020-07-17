package com.exasol;

import java.util.Set;

import com.exasol.platform.GitHubRepository;

public class JavaRepositoryHandler extends AbstractRepositoryHandler {
    /**
     * Create a new instance of {@link JavaRepositoryHandler}.
     *
     * @param repository in instance of {@link GitHubRepository}
     * @param platforms one or more {@link ReleasePlatform}
     */
    public JavaRepositoryHandler(final GitHubRepository repository, final Set<ReleasePlatform> platforms) {
        super(repository, platforms);
    }

    @Override
    public String getVersion() {
        final String pom = this.repository.getSingleFileContentAsString("pom.xml");
        final String projectVersionTag = "<version>";
        final int index = pom.indexOf(projectVersionTag);
        if (index == -1) {
            throw new IllegalStateException("Cannot find a project version in pom.xml file.");
        } else {
            String version = "";
            for (int i = index + projectVersionTag.length(); (i < pom.length()) && (pom.charAt(i) != '<'); i++) {
                version += pom.charAt(i);
            }
            return version;
        }
    }
}