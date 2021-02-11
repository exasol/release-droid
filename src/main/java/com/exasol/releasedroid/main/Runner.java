package com.exasol.releasedroid.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;

import com.exasol.releasedroid.formatting.LogFormatter;
import com.exasol.releasedroid.github.*;
import com.exasol.releasedroid.maven.MavenReleaseMaker;
import com.exasol.releasedroid.repository.RepositoryFactory;
import com.exasol.releasedroid.usecases.*;
import com.exasol.releasedroid.usecases.release.*;
import com.exasol.releasedroid.usecases.validate.ValidateInteractor;
import com.exasol.releasedroid.usecases.validate.ValidateUseCase;

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
        final RepositoryGateway repositoryGateway = new RepositoryFactory(githubGateway);
        final ValidateUseCase validateUseCase = new ValidateInteractor(repositoryGateway);
        if (userInput.hasLocalPath()) {
            return ReleaseDroid.of(validateUseCase);
        } else {
            return createReleaseDroidForGitHub(validateUseCase, repositoryGateway, githubGateway);
        }
    }

    private static void setUpLogging() throws IOException {
        final ClassLoader classLoader = LogFormatter.class.getClassLoader();
        final InputStream loggingProperties = classLoader.getResourceAsStream("logging.properties");
        LogManager.getLogManager().readConfiguration(loggingProperties);
    }

    private static ReleaseDroid createReleaseDroidForGitHub(final ValidateUseCase validateUseCase,
            final RepositoryGateway repositoryGateway, final GithubGateway githubGateway) {
        final Map<PlatformName, ReleaseMaker> releaseMakers = createReleaseMakers(githubGateway);
        final ReleaseManager releaseManager = new ReleaseManagerImpl(new GitHubRepositoryModifier(), githubGateway);
        final ReleaseUseCase releaseUseCase = new ReleaseInteractor(validateUseCase, releaseMakers, repositoryGateway,
                releaseManager);
        return ReleaseDroid.of(validateUseCase, releaseUseCase);
    }

    private static GitHubUser getGithubUser() {
        return CredentialsProvider.getInstance().provideGitHubUserWithCredentials();
    }

    private static Map<PlatformName, ReleaseMaker> createReleaseMakers(final GithubGateway githubGateway) {
        final Map<PlatformName, ReleaseMaker> releaseMakers = new HashMap<>();
        releaseMakers.put(PlatformName.GITHUB, new GitHubReleaseMaker(githubGateway));
        releaseMakers.put(PlatformName.MAVEN, new MavenReleaseMaker(githubGateway));
        return releaseMakers;
    }
}