package com.exasol.releasedroid.main;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.HOME_DIRECTORY;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_CREDENTIALS;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.fusesource.jansi.AnsiConsole;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.ReleaseManagerImpl;
import com.exasol.releasedroid.adapter.communityportal.*;
import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.adapter.jira.JiraAPIAdapter;
import com.exasol.releasedroid.adapter.jira.JiraReleaseMaker;
import com.exasol.releasedroid.adapter.maven.MavenReleaseMaker;
import com.exasol.releasedroid.adapter.repository.RepositoryFactory;
import com.exasol.releasedroid.formatting.*;
import com.exasol.releasedroid.output.ResponseDiskWriter;
import com.exasol.releasedroid.output.ResponseLogger;
import com.exasol.releasedroid.usecases.PropertyReaderImpl;
import com.exasol.releasedroid.usecases.UseCase;
import com.exasol.releasedroid.usecases.release.*;
import com.exasol.releasedroid.usecases.repository.RepositoryGateway;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.ValidateInteractor;

/**
 * This class contains main method.
 */
public class Runner {

    private static final Logger LOGGER = Logger.getLogger(Runner.class.getName());
    private static final String REPORT_PATH = HOME_DIRECTORY + "/.release-droid";
    private static final String REPORT_NAME = "last_report.txt";
    private static final String USER_GUIDE_URL = "https://github.com/exasol/release-droid/blob/main/doc/user_guide/user_guide.md";

    /**
     * Run the Release Droid.
     *
     * @param args arguments
     * @throws IOException potential exception that could be thrown during execution
     */
    public static void main(final String... args) throws IOException {
        setUpLogging();
        AnsiConsole.systemInstall();
        createReleaseDroid().run(new UserInputParser().parseUserInput(args));
    }

    static ReleaseDroid createReleaseDroid() {
        checkCredentialsFile(Path.of(RELEASE_DROID_CREDENTIALS));
        final GitHubGateway githubGateway = new GitHubAPIAdapter(new GitHubConnectorImpl(getPropertyReader()));
        final RepositoryGateway repositoryGateway = new RepositoryFactory(githubGateway);
        final Map<PlatformName, ReleaseMaker> releaseMakers = createReleaseMakers(githubGateway);
        final ReleaseManager releaseManager = new ReleaseManagerImpl(githubGateway);
        final UseCase validateUseCase = new ValidateInteractor();
        final UseCase releaseUseCase = new ReleaseInteractor(releaseMakers, releaseManager);
        return ReleaseDroid.builder() //
                .repositoryGateway(repositoryGateway) //
                .validateUseCase(validateUseCase) //
                .releaseUseCase(releaseUseCase) //
                .loggerResponseConsumer(new ResponseLogger(new ReportLogFormatter())) //
                .diskWriterResponseConsumer(new ResponseDiskWriter(new ReportSummaryFormatter(), new HeaderFormatter(),
                        REPORT_PATH, REPORT_NAME)) //
                .build();
    }

    static boolean checkCredentialsFile(final Path path) {
        final Path file = path.toAbsolutePath();
        if (Files.exists(file)) {
            return true;
        }
        final String message = ExaError.messageBuilder("W-RD-19").message("No file {{credentials file}}.") //
                .mitigation("Please consider to store your credentials there, see " + USER_GUIDE_URL + ".")
                .parameter("credentials file", file).toString();
        LOGGER.warning(message);
        return false;
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
        final Map<PlatformName, ReleaseMaker> releaseMakers = new EnumMap<>(PlatformName.class);
        releaseMakers.put(PlatformName.GITHUB, new GitHubReleaseMaker(githubGateway));
        releaseMakers.put(PlatformName.MAVEN, new MavenReleaseMaker(githubGateway));
        final CommunityPortalGateway communityPortalGateway = new CommunityPortalAPIAdapter(getPropertyReader());
        releaseMakers.put(PlatformName.COMMUNITY, new CommunityPortalReleaseMaker(communityPortalGateway));
        releaseMakers.put(PlatformName.JIRA, new JiraReleaseMaker(new JiraAPIAdapter(getPropertyReader())));
        return releaseMakers;
    }
}
