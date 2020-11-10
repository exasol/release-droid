package com.exasol.releaserobot.main;

import java.util.*;

import com.exasol.releaserobot.github.*;
import com.exasol.releaserobot.maven.*;
import com.exasol.releaserobot.usecases.*;
import com.exasol.releaserobot.usecases.release.ReleaseInteractor;
import com.exasol.releaserobot.usecases.release.ReleaseUseCase;
import com.exasol.releaserobot.usecases.validate.*;

/**
 * This class contains main method.
 */
public class Runner {
    /**
     * Run the Release Robot.
     *
     * @param args arguments
     */
    public static void main(final String[] args) throws GitHubException {
        final UserInput userInput = new UserInputParser().parseUserInput(args);
        createReleaseRobot().run(userInput);
    }

    private static ReleaseRobot createReleaseRobot() {
        final GithubGateway githubGateway = new GithubAPIAdapter(getGithubUser());
        final Map<PlatformName, ReleaseablePlatform> releaseablePlatforms = createReleaseablePlatforms(githubGateway);
        final List<RepositoryValidator> repositoryValidators = createRepositoryValidators();
        final RepositoryGateway repositoryGateway = new GithubRepositoryGateway(githubGateway);
        final ValidateUseCase validateUseCase = new ValidateInteractor(repositoryValidators, releaseablePlatforms,
                repositoryGateway);
        final ReleaseUseCase releaseUseCase = new ReleaseInteractor(validateUseCase, releaseablePlatforms,
                repositoryGateway);
        return new ReleaseRobot(releaseUseCase, validateUseCase);
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

    private static Map<PlatformName, ReleaseablePlatform> createReleaseablePlatforms(
            final GithubGateway githubGateway) {
        final Map<PlatformName, ReleaseablePlatform> releaseablePlatforms = new HashMap<>();
        releaseablePlatforms.put(PlatformName.GITHUB, new ReleaseablePlatform(
                new GitHubPlatformValidator(githubGateway), new GitHubReleaseMaker(githubGateway)));
        releaseablePlatforms.put(PlatformName.MAVEN,
                new ReleaseablePlatform(new MavenPlatformValidator(), new MavenReleaseMaker(githubGateway)));
        return releaseablePlatforms;
    }
}