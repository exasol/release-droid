package com.exasol.validation;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.RepositoryService;

public class GitHubValidator {
    public void validateReadyForRelease(final String owner, final String repositoryName) {
        final Repository repository = getRepositoryIfExists(owner, repositoryName);
        final String pom = getSingleFileContentAsString(repository, "pom.xml");
        final String version = getVersionFromPom(pom);
    }

    protected Repository getRepositoryIfExists(final String owner, final String repositoryName) {
        final RepositoryService repositoryService = new RepositoryService();
        try {
            return repositoryService.getRepository(owner, repositoryName);
        } catch (final IOException exception) {
            throw new IllegalArgumentException("Repository '" + repositoryName
                    + "' not found in the list of public repositories of the owner '" + owner + "'.");
        }
    }

    private String getSingleFileContentAsString(final Repository repository, final String filePath) {
        final ContentsService contentsService = new ContentsService();
        try {
            final List<RepositoryContents> contents = contentsService.getContents(repository, filePath);
            if (contents.isEmpty()) {
                throw new IllegalArgumentException("File not found.");
            }
            if (contents.size() > 1) {
                throw new IllegalArgumentException("There are multiple file with this name in the repository.");
            }
            final String pomContent = contents.get(0).getContent();
            return new String(Base64.getMimeDecoder().decode(pomContent.getBytes()));
        } catch (final IOException exception) {
            throw new IllegalArgumentException(
                    "Cannot find or read file " + filePath + " in the repository " + repository.getName());
        }
    }

    protected String getVersionFromPom(final String pom) {
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
