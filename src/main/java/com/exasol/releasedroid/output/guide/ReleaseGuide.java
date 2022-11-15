package com.exasol.releasedroid.output.guide;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_CREDENTIALS;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.*;

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
        final String version = repo.getVersion().replace("v", "");
        final String name = removePrefix(repo.getName());
        final String date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        final String releaseLabel = name + " " + version;

        final XProperties properties = XProperties.from(Paths.get(RELEASE_DROID_CREDENTIALS));

        final ReleasePlatforms platforms = ReleasePlatforms.from(UserInput.builder().build(), repo);
        final Publication publication = Publication.create(platforms.list().contains(PlatformName.MAVEN), repo);
        final String shortTag = new ShortTag(repo).retrieve();
        final TargetAudience targetAudience = TargetAudience.retrieve(properties, name);
        final ChangesFileParser changesFile = new ChangesFileParser(repo, version);
        return new ReleaseGuide() //
                .var("PageTitle", "Releasing " + releaseLabel + " on " + date) //
                .var("Date", date) //
                .var("ReleaseChecklists", properties.releaseChecklists()) //
                .var("ReleaseLabel", releaseLabel) //
                .var("ProjectName", name) //
                .var("ReleaseVersion", version) //
                .var("GitHubTagUrl", gitHubTagUrl) //
                .var("MavenUrls", publication.mavenUrls(repo.getName(), version)) //
                .var("TargetAudience", targetAudience.display()) //
                .var("TeamPlanning", properties.teamPlanning()) //
                .var("ProjectShortTag", shortTag) //
                .var("AnnounceChannel", properties.announceChannel(targetAudience)) //
                .var("PublicationPlatforms", publication.icons()) //
                .var("ReleaseContentSummary", changesFile.getSummary());
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
    public ReleaseGuide var(final String name, final String value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * Write release guide to specified destination path.
     *
     * @param destination path to write the release guide to
     * @throws IOException if writing fails
     */
    public void write(final Path destination) {
        try (BufferedWriter writer = Files.newBufferedWriter(destination)) {
            write(writer);
            LOGGER.info("Generated release guide to file " + destination);
        } catch (final IOException exception) {
            LOGGER.warning(() -> ExaError.messageBuilder("RD-W-22") //
                    .message("Could not write release guide: {{cause}}", //
                            destination, exception.getMessage()) //
                    .toString());
        }
    }

    /**
     * Write release guide to specified writer.
     *
     * @param writer writer to send output to
     * @throws IOException if writing fails
     */
    public void write(final BufferedWriter writer) throws IOException {
        try (BufferedReader reader = reader(TEMPLATE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(replaceTokens(line));
            }
        }
    }

    private BufferedReader reader(final String resource) {
        return new BufferedReader(new InputStreamReader(ReleaseGuide.class.getResourceAsStream(resource)));
    }

    private String replaceTokens(final String line) {
        final Matcher matcher = REFERENCE_PATTERN.matcher(line);
        final StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while ((i < line.length()) && matcher.find(i)) {
            stringBuilder //
                    .append(line.substring(i, matcher.start())) //
                    .append(replaceReference(matcher.group()));
            i = matcher.end();
        }
        return stringBuilder //
                .append(line.substring(i)) //
                .append("\n") //
                .toString();
    }

    private String replaceReference(final String reference) {
        return Objects.requireNonNull(this.map.get(reference.substring(1)));
    }
}
