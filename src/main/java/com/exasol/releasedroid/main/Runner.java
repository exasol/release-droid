package com.exasol.releasedroid.main;

import static com.exasol.releasedroid.usecases.Repository.Language.JAVA;
import static com.exasol.releasedroid.usecases.Repository.Language.LANGUAGE_INDEPENDENT;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;

import com.exasol.releasedroid.formatting.LogFormatter;
import com.exasol.releasedroid.github.*;
import com.exasol.releasedroid.maven.*;
import com.exasol.releasedroid.repository.RepositoryFactory;
import com.exasol.releasedroid.usecases.*;
import com.exasol.releasedroid.usecases.Repository.Language;
import com.exasol.releasedroid.usecases.release.ReleaseInteractor;
import com.exasol.releasedroid.usecases.release.ReleaseUseCase;
import com.exasol.releasedroid.usecases.validate.*;

/**
 * This class contains main method.
 */
public class Runner {
    /**
     * Run the Release Droid.
     *
     * @param args arguments
     */
    public static void main(final String[] args) throws IOException {
        final UserInput userInput = new UserInputParser().parseUserInput(args);
        setUpLogging();
        createReleaseDroid(userInput).run(userInput);
    }

    private static ReleaseDroid createReleaseDroid(final UserInput userInput) {
        final GithubGateway githubGateway = new GithubAPIAdapter(getGithubUser());
        final Map<PlatformName, ReleasablePlatform> releaseablePlatforms = createReleaseablePlatforms(githubGateway);
        final RepositoryGateway repositoryGateway = new RepositoryFactory(githubGateway);
        final Map<RepositoryValidator, Language> repositoryValidators = createRepositoryValidators();
        final ValidateUseCase validateUseCase = new ValidateInteractor(repositoryValidators, releaseablePlatforms,
                repositoryGateway);
        if (userInput.hasLocalPath()) {
            return createReleaseDroidLocal(validateUseCase);
        } else {
            return createReleaseDroidForGitHub(validateUseCase, repositoryGateway, releaseablePlatforms);
        }
    }

    private static void setUpLogging() throws IOException {
        final ClassLoader classLoader = LogFormatter.class.getClassLoader();
        final InputStream loggingProperties = classLoader.getResourceAsStream("logging.properties");
        LogManager.getLogManager().readConfiguration(loggingProperties);
    }

    private static ReleaseDroid createReleaseDroidLocal(final ValidateUseCase validateUseCase) {
        return ReleaseDroid.of(validateUseCase);
    }

    private static ReleaseDroid createReleaseDroidForGitHub(final ValidateUseCase validateUseCase,
            final RepositoryGateway repositoryGateway,
            final Map<PlatformName, ReleasablePlatform> releaseablePlatforms) {
        final ReleaseUseCase releaseUseCase = new ReleaseInteractor(validateUseCase, releaseablePlatforms,
                repositoryGateway, new GitHubRepositoryModifier());
        return ReleaseDroid.of(validateUseCase, releaseUseCase);
    }

    private static GitHubUser getGithubUser() {
        return CredentialsProvider.getInstance().provideGitHubUserWithCredentials();
    }

    private static Map<RepositoryValidator, Language> createRepositoryValidators() {
        final Map<RepositoryValidator, Language> repositoryValidators = new HashMap<>();
        repositoryValidators.put(new GitRepositoryValidator(), LANGUAGE_INDEPENDENT);
        repositoryValidators.put(new MavenRepositoryValidator(), JAVA);
        return repositoryValidators;
    }

    private static Map<PlatformName, ReleasablePlatform> createReleaseablePlatforms(final GithubGateway githubGateway) {
        final Map<PlatformName, ReleasablePlatform> releaseablePlatforms = new HashMap<>();
        releaseablePlatforms.put(PlatformName.GITHUB, new ReleasablePlatform(new GitHubPlatformValidator(githubGateway),
                new GitHubReleaseMaker(githubGateway)));
        releaseablePlatforms.put(PlatformName.MAVEN,
                new ReleasablePlatform(new MavenPlatformValidator(), new MavenReleaseMaker(githubGateway)));
        return releaseablePlatforms;
    }
}