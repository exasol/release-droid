package com.exasol.validation;

import com.exasol.platform.GitHubRepository;

/**
 * A java specific project validator.
 */
public class ProjectValidatorJava extends AbstractProjectValidator {
    public ProjectValidatorJava(final GitHubRepository repository) {
        super(repository);
    }

    @Override
    protected String getVersion() {
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