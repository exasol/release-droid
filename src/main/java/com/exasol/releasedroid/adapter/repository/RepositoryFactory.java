package com.exasol.releasedroid.adapter.repository;

import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.github.GitHubException;
import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.adapter.github.GitHubRepositoryGate;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;
import com.exasol.releasedroid.usecases.repository.RepositoryGateway;
import com.exasol.releasedroid.usecases.request.Language;
import com.exasol.releasedroid.usecases.request.UserInput;

/**
 * Creates a repository.
 */
public class RepositoryFactory implements RepositoryGateway {
    private static final Logger LOGGER = Logger.getLogger(RepositoryFactory.class.getName());
    private final GitHubGateway githubGateway;

    public RepositoryFactory(final GitHubGateway githubGateway) {
        this.githubGateway = githubGateway;
    }

    @Override
    public Repository getRepository(final UserInput userInput) {
        final var repositoryGate = getRepositoryGate(userInput);
        final var language = getLanguage(userInput);
        switch (language) {
        case JAVA:
            return new JavaRepository(repositoryGate, this.githubGateway);
        case SCALA:
            return new ScalaRepository(repositoryGate, this.githubGateway);
        case GENERIC:
            return new GenericRepository(repositoryGate, this.githubGateway);
        default:
            throw new UnsupportedOperationException(ExaError.messageBuilder("E-RD-REP-10") //
                    .message("Unsupported programming language: {{language}}.", language).toString());
        }
    }

    private RepositoryGate getRepositoryGate(final UserInput userInput) {
        if (userInput.hasLocalPath()) {
            return new LocalRepositoryGate(userInput.getLocalPath(), userInput.getFullRepositoryName());
        } else {
            final String branch = getBranch(userInput);
            return new GitHubRepositoryGate(this.githubGateway, branch, userInput.getFullRepositoryName());
        }
    }

    private String getBranch(final UserInput userInput) {
        if (userInput.hasBranch()) {
            return userInput.getBranch();
        } else {
            try {
                return this.githubGateway.getDefaultBranch(userInput.getFullRepositoryName());
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
            final String repositoryPrimaryLanguage = this.githubGateway
                    .getRepositoryPrimaryLanguage(userInput.getFullRepositoryName());
            validateLanguage(repositoryPrimaryLanguage);
            final var language = Language.getLanguage(repositoryPrimaryLanguage);
            LOGGER.warning(() -> "The repository language was detected automatically: " + language
                    + ". If it was detected incorrectly, please specify it manually using -lg <language> argument.");
            return language;
        } catch (final GitHubException exception) {
            throw new RepositoryException(exception);
        }
    }

    private void validateLanguage(final String repositoryPrimaryLanguage) {
        if (repositoryPrimaryLanguage == null || repositoryPrimaryLanguage.isEmpty()) {
            throw new RepositoryException(ExaError.messageBuilder("E-RD-REP-11") //
                    .message("Repository language cannot be detected automatically.") //
                    .mitigation("Please provide the language via user input (check the user guide).") //
                    .toString());
        }
    }
}