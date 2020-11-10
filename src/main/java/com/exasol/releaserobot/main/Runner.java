package com.exasol.releaserobot.main;

import java.util.*;

import com.exasol.releaserobot.github.*;
import com.exasol.releaserobot.maven.*;
import com.exasol.releaserobot.usecases.*;
import com.exasol.releaserobot.usecases.release.*;
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
        createReleaseRobot(userInput).run(userInput);
    }

    private static ReleaseRobot createReleaseRobot(final UserInput userInput) {
        final GithubGateway githubGateway = new GithubAPIAdapter(getGithubUser());
        final Map<PlatformName, ReleaseMaker> releaseMakers = createReleaseMakers(userInput, githubGateway);
        final List<RepositoryValidator> repositoryValidators = createRepositoryValidators(userInput, githubGateway);
        final RepositoryGateway repositoryGateway = new GithubRepositoryGateway(githubGateway);
        final ValidateUseCase validateUseCase = new ValidateInteractor(repositoryValidators, repositoryGateway);
        final ReleaseUseCase releaseUseCase = new ReleaseInteractor(validateUseCase, releaseMakers, repositoryGateway);
        return new ReleaseRobot(releaseUseCase, validateUseCase);
    }

    private static GitHubUser getGithubUser() {
        return CredentialsProvider.getInstance().provideGitHubUserWithCredentials();
    }

    private static List<RepositoryValidator> createRepositoryValidators(final UserInput userInput,
            final GithubGateway githubGateway) {
        final List<RepositoryValidator> repositoryValidators = new ArrayList<>();
        repositoryValidators.add(new GitRepositoryValidator());
        repositoryValidators.add(new MavenRepositoryValidator());
        for (final PlatformName name : userInput.getPlatformNames()) {
            switch (name) {
            case GITHUB:
                repositoryValidators.add(new GitHubPlatformValidator(githubGateway));
                break;
            case MAVEN:
                repositoryValidators.add(new MavenPlatformValidator());
                break;
            default:
                throw new UnsupportedOperationException(
                        "E-RR-RUN-2: Platform '" + name + "' is not supported. Please choose one of: "
                                + PlatformName.availablePlatformNames().toString());
            }
        }
        return repositoryValidators;
    }

    private static Map<PlatformName, ReleaseMaker> createReleaseMakers(final UserInput userInput,
            final GithubGateway githubGateway) {
        final Map<PlatformName, ReleaseMaker> releaseMakers = new HashMap<>();
        for (final PlatformName name : userInput.getPlatformNames()) {
            switch (name) {
            case GITHUB:
                releaseMakers.put(name, new GitHubReleaseMaker(githubGateway));
                break;
            case MAVEN:
                releaseMakers.put(name, new MavenReleaseMaker(githubGateway));
                break;
            default:
                throw new UnsupportedOperationException(
                        "E-RR-RUN-2: Platform '" + name + "' is not supported. Please choose one of: "
                                + PlatformName.availablePlatformNames().toString());
            }
        }
        return releaseMakers;
    }
}