package com.exasol.releasedroid.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;

import org.kohsuke.github.GitHub;

import com.exasol.releasedroid.adapter.ReleaseManagerImpl;
import com.exasol.releasedroid.adapter.RepositoryFactory;
import com.exasol.releasedroid.adapter.communityportal.CommunityPortalAPIAdapter;
import com.exasol.releasedroid.adapter.communityportal.CommunityPortalGateway;
import com.exasol.releasedroid.adapter.communityportal.CommunityPortalReleaseMaker;
import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.adapter.maven.MavenReleaseMaker;
import com.exasol.releasedroid.formatting.LogFormatter;
import com.exasol.releasedroid.usecases.release.*;
import com.exasol.releasedroid.usecases.repository.RepositoryGateway;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.request.UserInput;
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

    private static ReleaseDroid createReleaseDroid(final UserInput userInput) throws IOException {
        final var githubUser = getGithubUser();
        final GitHubGateway githubGateway = new GitHubAPIAdapter(
                GitHub.connect(githubUser.getUsername(), githubUser.getPassword()));
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
            final RepositoryGateway repositoryGateway, final GitHubGateway githubGateway) {
        final Map<PlatformName, ReleaseMaker> releaseMakers = createReleaseMakers(githubGateway);
        final ReleaseManager releaseManager = new ReleaseManagerImpl(new GitHubRepositoryModifier(), githubGateway);
        final ReleaseUseCase releaseUseCase = new ReleaseInteractor(validateUseCase, releaseMakers, repositoryGateway,
                releaseManager);
        return ReleaseDroid.of(validateUseCase, releaseUseCase);
    }

    private static User getGithubUser() {
        return CredentialsProvider.getInstance().provideGitHubUser();
    }

    private static Map<PlatformName, ReleaseMaker> createReleaseMakers(final GitHubGateway githubGateway) {
        final Map<PlatformName, ReleaseMaker> releaseMakers = new HashMap<>();
        releaseMakers.put(PlatformName.GITHUB, new GitHubReleaseMaker(githubGateway));
        releaseMakers.put(PlatformName.MAVEN, new MavenReleaseMaker(githubGateway));
        final CommunityPortalGateway communityPortalGateway = new CommunityPortalAPIAdapter(getCommunityPortalUser());
        releaseMakers.put(PlatformName.COMMUNITY, new CommunityPortalReleaseMaker(communityPortalGateway));
        return releaseMakers;
    }

    private static User getCommunityPortalUser() {
        return CredentialsProvider.getInstance().provideCommunityPortalUser();
    }
}