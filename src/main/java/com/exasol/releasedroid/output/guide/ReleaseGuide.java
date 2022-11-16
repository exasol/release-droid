package com.exasol.releasedroid.output.guide;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_CREDENTIALS;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.usecases.repository.Repository;

/**
 * Generate a release guide.
 *
 * <p>
 * Making releases still requires some effort, consists of mostly stereotype tasks, is time consuming, error-prone and
 * requires concentration to have all the details correct to a pedantic level. Depending on the project to release data
 * needs to be entered or published into various places. This class therefore aids developers by aggregating the data as
 * far as possible providing as much convenience as possible.
 * </p>
 */
// [impl->dsn~aggregate-data~1]
// [impl->dsn~release-guide-channels~1]
public class ReleaseGuide {

    private static final Logger LOGGER = Logger.getLogger(ReleaseGuide.class.getName());
    private static final UrlBuilder GITHUB_TAG_URL = new UrlBuilder() //
            .prefix("https://github.com/").infix("/releases/tag/");
    private static final String TEMPLATE = "/release-guide-template.html";
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("\\$[a-z_]+", Pattern.CASE_INSENSITIVE);

    /**
     * Create new {@link ReleaseGuide} for specified (presumably local) repository.
     *
     * @param repo repository
     * @return new instance of {@link ReleaseGuide}
     */
    public static ReleaseGuide from(final Repository repo) {
        return from(repo, GITHUB_TAG_URL.build(repo.getName(), repo.getGitTags().get(0)));
    }

    /**
     * Create new {@link ReleaseGuide} for specified repository and HTML URL of release on GitHub.
     *
     * @param repo         repository
     * @param gitHubTagUrl HTML URL of GitHub tag.
     * @return new instance of {@link ReleaseGuide}
     */
    public static ReleaseGuide from(final Repository repo, final String gitHubTagUrl) {
        final ReleaseGuideProperties properties = ReleaseGuideProperties.from(Path.of(RELEASE_DROID_CREDENTIALS));
        final GitHubGateway githubGateway = new GitHubAPIAdapter(new GitHubConnectorImpl(properties));
        return from(repo, githubGateway, properties, gitHubTagUrl);
    }

    public static ReleaseGuide from(final Repository repo, final GitHubGateway githubGateway,
            final ReleaseGuideProperties properties, final String gitHubTagUrl) {
        final String version = repo.getVersion().replace("v", "");
        final String name = removePrefix(repo.getName());
        final String date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        final String releaseLabel = name + " " + version;

        final Publication publication = new Publication(repo.getReleaseConfig());
        final String shortTag = new ShortTag(repo).retrieve();
        final TargetAudience targetAudience = TargetAudience.retrieve(githubGateway, name);
        final ChangesFileParser changesFile = new ChangesFileParser(repo, version);
        return new ReleaseGuide() //
                .replace("$PageTitle", "Releasing " + releaseLabel + " on " + date) //
                .replace("$Date", date) //
                .replace("$ReleaseChecklists", properties.releaseChecklists()) //
                .replace("$ReleaseLabel", releaseLabel) //
                .replace("$ProjectName", name) //
                .replace("$ReleaseVersion", version) //
                .replace("$GitHubTagUrl", gitHubTagUrl) //
                .replace("$MavenUrls", publication.mavenUrls(repo.getName(), version)) //
                .replace("$TargetAudience", targetAudience.display()) //
                .replace("$TeamPlanning", properties.teamPlanning()) //
                .replace("$ProjectShortTag", shortTag) //
                .replace("$AnnounceChannel", properties.announceChannel(targetAudience)) //
                .replace("$PublicationPlatforms", publication.icons()) //
                .replace("$ReleaseContentSummary", changesFile.getSummary());
    }

    private static String removePrefix(final String repoName) {
        final int i = repoName.lastIndexOf("/");
        return i < 0 ? repoName : repoName.substring(i + 1);
    }

    private final Map<String, String> map = new HashMap<>();

    /**
     * Add a variable definition. {@link ReleaseGuide} identifies variables in the template by prefix "$" and replaces
     * each variable with the value provided beforehand. For variables with unspecified value an exception is thrown.
     *
     * @param name  name of the variable
     * @param value value of the variable
     * @return this for fluent programming
     */
    public ReleaseGuide replace(final String name, final String value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * Write release guide to specified destination path.
     *
     * @param destination path to write the release guide to
     */
    public void write(final Path destination) {
        try (BufferedReader reader = reader(TEMPLATE); //
                BufferedWriter writer = Files.newBufferedWriter(destination)) {
            write(reader, writer);
            LOGGER.info(() -> "Generated release guide to file " + destination);
        } catch (final IOException exception) {
            throw new UncheckedIOException(ExaError.messageBuilder("E-RD-22") //
                    .message("Could not write release guide").toString(), //
                    exception);
        }
    }

    void write(final BufferedReader reader, final BufferedWriter writer) throws IOException {
        String line;
        boolean first = true;
        while ((line = reader.readLine()) != null) {
            if (!first) {
                writer.write("\n");
            }
            writer.write(replaceVariables(line));
            first = false;
        }
    }

    private BufferedReader reader(final String resource) {
        return new BufferedReader(new InputStreamReader(ReleaseGuide.class.getResourceAsStream(resource)));
    }

    private String replaceVariables(final String line) {
        final Matcher matcher = REFERENCE_PATTERN.matcher(line);
        final StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while ((i < line.length()) && matcher.find(i)) {
            stringBuilder //
                    .append(line.substring(i, matcher.start())) //
                    .append(replacement(matcher.group()));
            i = matcher.end();
        }
        return stringBuilder //
                .append(line.substring(i)) //
                .toString();
    }

    private String replacement(final String name) {
        final String value = this.map.get(name);
        if (value != null) {
            return value;
        }
        throw new IllegalStateException("No value for variable " + name);
    }
}
