package com.exasol.releasedroid.repository;

import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.github.GitHubException;
import com.exasol.releasedroid.github.GithubGateway;
import com.exasol.releasedroid.usecases.*;

/**
 * Creates a repository.
 * 
 */
public class RepositoryFactory implements RepositoryGateway {
    private static final Logger LOGGER = Logger.getLogger(RepositoryFactory.class.getName());
    private final GithubGateway githubGateway;

    public RepositoryFactory(final GithubGateway githubGateway) {
        this.githubGateway = githubGateway;
    }

    @Override
    public Repository getRepository(final UserInput userInput) {
        final RepositoryGate repositoryGate = getRepositoryGate(userInput);
        final String language = getLanguage(userInput).toUpperCase();
        switch (language) {
        case "JAVA":
            LOGGER.info("Java repository detected.");
            return new JavaRepository(repositoryGate);
        case "SCALA":
            LOGGER.info("Scala repository detected.");
            return new ScalaRepository(repositoryGate);
        default:
            throw new UnsupportedOperationException(ExaError.messageBuilder("E-RR-REP-10") //
                    .message("Unsupported programming language: {{language}}.") //
                    .parameter("language", language).toString());
        }
    }

    private RepositoryGate getRepositoryGate(final UserInput userInput) {
        if (userInput.hasLocalPath()) {
            return new LocalRepositoryGate(userInput.getLocalPath(), userInput.getRepositoryName());
        } else {
            final String branch = getBranch(userInput);
            return new GitHubRepositoryGate(this.githubGateway, branch, userInput.getRepositoryName());
        }
    }

    private String getBranch(final UserInput userInput) {
        if (userInput.hasBranch()) {
            return userInput.getBranch();
        } else {
            try {
                return this.githubGateway.getDefaultBranch(userInput.getRepositoryName());
            } catch (final GitHubException exception) {
                throw new RepositoryException(exception);
            }
        }
    }

    private String getLanguage(final UserInput userInput) {
        try {
            return this.githubGateway.getRepositoryPrimaryLanguage(userInput.getRepositoryName());
        } catch (final GitHubException exception) {
            throw new RepositoryException(exception);
        }
    }
}
