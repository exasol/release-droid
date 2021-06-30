package com.exasol.releasedroid.main;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;

import com.exasol.releasedroid.adapter.ReleaseManagerImpl;
import com.exasol.releasedroid.adapter.RepositoryFactory;
import com.exasol.releasedroid.adapter.communityportal.CommunityPortalAPIAdapter;
import com.exasol.releasedroid.adapter.communityportal.CommunityPortalGateway;
import com.exasol.releasedroid.adapter.communityportal.CommunityPortalReleaseMaker;
import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.adapter.jira.JiraAPIAdapter;
import com.exasol.releasedroid.adapter.jira.JiraReleaseMaker;
import com.exasol.releasedroid.adapter.maven.MavenReleaseMaker;
import com.exasol.releasedroid.formatting.*;
import com.exasol.releasedroid.output.ResponseDiskWriter;
import com.exasol.releasedroid.output.ResponseLogger;
import com.exasol.releasedroid.usecases.PropertyReaderImpl;
import com.exasol.releasedroid.usecases.release.*;
import com.exasol.releasedroid.usecases.repository.RepositoryGateway;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.ValidateInteractor;
import com.exasol.releasedroid.usecases.validate.ValidateUseCase;

/**
 * This class contains main method.
 */
public class Runner {
    private static final String RELEASE_DROID_CREDENTIALS = RELEASE_DROID_DIRECTORY + FILE_SEPARATOR + "credentials";
    private static final Path REPORT_PATH = Paths.get(HOME_DIRECTORY, ".release-droid", "last_report.txt");

    /**
     * Run the Release Droid.
     *
     * @param args arguments
     */
    public static void main(final String[] args) throws IOException {
        setUpLogging();
        createReleaseDroid().run(new UserInputParser().parseUserInput(args));
    }

    private static ReleaseDroid createReleaseDroid() {
        final GitHubGateway githubGateway = new GitHubAPIAdapter(new GitHubConnectorImpl(getPropertyReader()));
        final RepositoryGateway repositoryGateway = new RepositoryFactory(githubGateway);
        final Map<PlatformName, ReleaseMaker> releaseMakers = createReleaseMakers(githubGateway);
        final ReleaseManager releaseManager = new ReleaseManagerImpl(new GitHubRepositoryModifier(), githubGateway);
        final ValidateUseCase validateUseCase = new ValidateInteractor();
        final ReleaseUseCase releaseUseCase = new ReleaseInteractor(validateUseCase, releaseMakers, releaseManager);
        final List<ReleaseDroidResponseConsumer> releaseDroidResponseConsumers = getReportConsumers();
        return new ReleaseDroid(repositoryGateway, validateUseCase, releaseUseCase, releaseDroidResponseConsumers);
    }

    private static List<ReleaseDroidResponseConsumer> getReportConsumers() {
        return List.of( //
                new ResponseDiskWriter(new ReportSummaryFormatter(), new HeaderFormatter(), REPORT_PATH), //
                new ResponseLogger(new ReportLogFormatter()));
    }

    private static PropertyReaderImpl getPropertyReader() {
        return new PropertyReaderImpl(RELEASE_DROID_CREDENTIALS);
    }

    private static void setUpLogging() throws IOException {
        final var classLoader = LogFormatter.class.getClassLoader();
        final InputStream loggingProperties = classLoader.getResourceAsStream("logging.properties");
        LogManager.getLogManager().readConfiguration(loggingProperties);
    }

    private static Map<PlatformName, ReleaseMaker> createReleaseMakers(final GitHubGateway githubGateway) {
        final Map<PlatformName, ReleaseMaker> releaseMakers = new HashMap<>();
        releaseMakers.put(PlatformName.GITHUB, new GitHubReleaseMaker(githubGateway));
        releaseMakers.put(PlatformName.MAVEN, new MavenReleaseMaker(githubGateway));
        final CommunityPortalGateway communityPortalGateway = new CommunityPortalAPIAdapter(getPropertyReader());
        releaseMakers.put(PlatformName.COMMUNITY, new CommunityPortalReleaseMaker(communityPortalGateway));
        releaseMakers.put(PlatformName.JIRA, new JiraReleaseMaker(new JiraAPIAdapter(getPropertyReader())));
        return releaseMakers;
    }
}