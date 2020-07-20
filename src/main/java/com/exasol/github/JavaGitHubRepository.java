package com.exasol.github;

import org.kohsuke.github.GHRepository;

/**
 * This class represents a Java-based GitHub repository.
 */
public class JavaGitHubRepository extends AbstractGitHubRepository {
    /**
     * Create a new instance of {@link JavaGitHubRepository}.
     * 
     * @param repository new instance of {@link JavaGitHubRepository}
     */
    public JavaGitHubRepository(final GHRepository repository) {
        super(repository);
    }

    @Override
    public String getVersion() {
        final String versionKey = "version";
        if (!this.filesCache.containsKey(versionKey)) {
            this.filesCache.put(versionKey, getVersionFromPomFile());
        }
        return this.filesCache.get(versionKey);
    }

    private String getVersionFromPomFile() {
        final String pom = getSingleFileContentAsString("pom.xml");
        final String projectVersionTag = "<version>";
        final int index = pom.indexOf(projectVersionTag);
        if (index == -1) {
            throw new GitHubException("Cannot find a project version in pom.xml file. "
                    + "Please, check that the pom.xml file contains <version></version> tag and the tag is not empty.");
        } else {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = index + projectVersionTag.length(); (i < pom.length()) && (pom.charAt(i) != '<'); i++) {
                stringBuilder.append(pom.charAt(i));
            }
            return stringBuilder.toString();
        }
    }
}