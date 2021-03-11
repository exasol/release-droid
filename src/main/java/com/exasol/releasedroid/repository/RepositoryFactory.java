package com.exasol.releasedroid.repository;

import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.github.GitHubException;
import com.exasol.releasedroid.github.GithubGateway;
import com.exasol.releasedroid.usecases.*;

/**
 * Creates a repository.
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
        final Language language = getLanguage(userInput);
        switch (language) {
        case JAVA:
            return new JavaRepository(repositoryGate, this.githubGateway);
        case SCALA:
            return new ScalaRepository(repositoryGate, this.githubGateway);
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

    private Language getLanguage(final UserInput userInput) {
        if (userInput.hasLanguage()) {
            return userInput.getLanguage();
        } else {
            return detectLanguageAutomatically(userInput);
        }
    }

    private Language detectLanguageAutomatically(final UserInput userInput) {
        try {
            final Language language = Language
                    .getLanguage(this.githubGateway.getRepositoryPrimaryLanguage(userInput.getRepositoryName()));
            LOGGER.warning("The repository language was detected automatically: " + language
                    + ". If it was detected incorrectly, please specify it manually using -lg <language> argument.");
            return language;
        } catch (final GitHubException exception) {
            throw new RepositoryException(exception);
        }
    }
}