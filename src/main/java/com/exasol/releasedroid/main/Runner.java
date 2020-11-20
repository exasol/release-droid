package com.exasol.releasedroid.main;

import com.exasol.releasedroid.github.*;
import com.exasol.releasedroid.maven.*;
import com.exasol.releasedroid.usecases.*;
import com.exasol.releasedroid.usecases.release.ReleaseInteractor;
import com.exasol.releasedroid.usecases.release.ReleaseUseCase;
import com.exasol.releasedroid.usecases.validate.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.LogManager;

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
        createReleaseDroid().run(userInput);
    }

    private static void setUpLogging() throws IOException {
        ClassLoader classLoader = ReleaseDroidFormatter.class.getClassLoader();
        InputStream loggingProperties = classLoader.getResourceAsStream("logging.properties");
        LogManager.getLogManager().readConfiguration(loggingProperties);
    }

    private static ReleaseDroid createReleaseDroid() {
        final GithubGateway githubGateway = new GithubAPIAdapter(getGithubUser());
        final Map<PlatformName, ReleasablePlatform> releaseablePlatforms = createReleaseablePlatforms(githubGateway);
        final List<RepositoryValidator> repositoryValidators = createRepositoryValidators();
        final RepositoryGateway repositoryGateway = new GithubRepositoryGateway(githubGateway);
        final ValidateUseCase validateUseCase = new ValidateInteractor(repositoryValidators, releaseablePlatforms,
                repositoryGateway);
        final ReleaseUseCase releaseUseCase = new ReleaseInteractor(validateUseCase, releaseablePlatforms,
                repositoryGateway);
        return new ReleaseDroid(releaseUseCase, validateUseCase);
    }

    private static GitHubUser getGithubUser() {
        return CredentialsProvider.getInstance().provideGitHubUserWithCredentials();
    }

    private static List<RepositoryValidator> createRepositoryValidators() {
        final List<RepositoryValidator> repositoryValidators = new ArrayList<>();
        repositoryValidators.add(new GitRepositoryValidator());
        repositoryValidators.add(new MavenRepositoryValidator());
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