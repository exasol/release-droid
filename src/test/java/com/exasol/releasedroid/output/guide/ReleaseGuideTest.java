package com.exasol.releasedroid.output.guide;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_CREDENTIALS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.adapter.repository.GenericRepository;
import com.exasol.releasedroid.adapter.repository.LocalRepositoryGate;
import com.exasol.releasedroid.usecases.repository.*;
import com.exasol.releasedroid.usecases.request.PlatformName;

class ReleaseGuideTest {

    private static final String GITHUB_URL = "https://github.com/sample-repo-github-tag-url";
    private static final String MAVEN_URL = "https://repo1.maven.org/maven2/com/exasol/sample-repo/1.2.3/";
    private static final String TEAM_PLANNING = "http://team_planning";
    private static final String RELEASE_CHECKLISTS = "http://release_checklists";

    @Test
    void test() throws Exception {
        final ReleaseGuide testee = testee();
        final String date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        assertThat(process(testee, "$PageTitle"), equalTo("Releasing sample-repo 1.2.3 on " + date));
        assertThat(process(testee, "$Date"), equalTo("2022-11-16"));
        assertThat(process(testee, "$ReleaseVersion"), equalTo("1.2.3"));
        assertThat(process(testee, "$ReleaseChecklists"), equalTo("Create new checklist on page" //
                + ahref("http://release_checklists", "Release checklists")));
        assertThat(process(testee, "$ReleaseLabel"), equalTo("sample-repo 1.2.3"));
        assertThat(process(testee, "$GitHubTagUrl"), equalTo(GITHUB_URL));
        assertThat(process(testee, "$MavenUrls"), equalTo(ahref(MAVEN_URL)));
        assertThat(process(testee, "$TargetAudience"), equalTo("customer"));
        assertThat(process(testee, "$TeamPlanning"), equalTo(ahref(TEAM_PLANNING)));
        assertThat(process(testee, "$AnnounceChannel"),
                equalTo("<a href=\"http://customer_channel\">#global-product-news</a>"));
        assertThat(process(testee, "$ReleaseContentSummary"), equalTo("Changes"));
    }

    @Test
    void itest(@TempDir final Path tempDir) throws Exception {
        final Path output = tempDir.resolve("output.html");
        testee().write(output);
        assertThat(Files.exists(output), is(true));
    }

    @Test
    void invalidPath() throws Exception {
        final ReleaseGuide testee = testee();
        final Path path = Path.of("/non/existing/path");
        final Exception exception = assertThrows(UncheckedIOException.class, () -> testee.write(path));
        assertThat(exception.getMessage(), startsWith("E-RD-22: Could not write release guide"));
    }

    private ReleaseGuide testee() throws GitHubException {
        final Repository repo = mock(Repository.class);
        when(repo.getVersion()).thenReturn("1.2.3");
        when(repo.getName()).thenReturn("exasol/sample-repo");
        when(repo.getSingleFileContentAsString(ShortTag.FILENAME)).thenReturn(errorCodeConfig("PK"));

        final ReleaseConfig releaseConfig = mock(ReleaseConfig.class);
        when(releaseConfig.getReleasePlatforms()).thenReturn(List.of(PlatformName.GITHUB, PlatformName.MAVEN));
        when(repo.getReleaseConfig()).thenReturn(Optional.of(releaseConfig));
        when(repo.getSingleFileContentAsString(ChangesFileParser.changesFilePath("1.2.3"))).thenReturn(changesFile());

        final Properties properties = new Properties();
        properties.putAll(Map.of( //
                "release_checklists", RELEASE_CHECKLISTS, //
                "team_planning", TEAM_PLANNING, //
                "team_channel", "http://team_channel", //
                "customer_channel", "http://customer_channel" //
        ));
        final ReleaseGuideProperties rgprops = new ReleaseGuideProperties(Path.of(RELEASE_DROID_CREDENTIALS),
                properties);

        final GitHubGateway githubGateway = mock(GitHubGateway.class);
        when(githubGateway.getFileContent(TargetAudience.PROJECT_OVERVIEW_REPO, "main", TargetAudience.INVENTORY))
                .thenReturn(projectOverviewInventory("sample-repo", "customer"));

        return ReleaseGuide.from(repo, githubGateway, rgprops, GITHUB_URL);
    }

    static String ahref(final String url) {
        return ahref(url, url);
    }

    static String ahref(final String url, final String label) {
        return "<a href=\"" + url + "\">" + label + "</a>";
    }

    private String process(final ReleaseGuide testee, final String template) throws IOException {
        final StringWriter writer = new StringWriter();
        try (BufferedReader reader = new BufferedReader(new StringReader(template)); //
                final BufferedWriter bw = new BufferedWriter(writer)) {
            testee.write(reader, bw);
        }
        return writer.toString();
    }

    void manualTest() throws Exception {
        final String folderName = "extension-manager"; // keeper "exasol-testcontainers";
        final RepositoryGate gate = LocalRepositoryGate.from(Path.of("/my-folder/" + folderName));
        final Repository repo = new GenericRepository(gate, gitHubGateway());
        ReleaseGuide.from(repo).write(Path.of("target/sample-guide.html"));
    }

    private GitHubGateway gitHubGateway() {
        final ReleaseGuideProperties properties = ReleaseGuideProperties.from(Path.of(RELEASE_DROID_CREDENTIALS));
        return new GitHubAPIAdapter(new GitHubConnectorImpl(properties));
    }

    private final String errorCodeConfig(final String shortTag) {
        return lines( //
                "error-tags:", //
                "  " + shortTag + ":", //
                "    highest-index: 0");
    }

    private final String changesFile() {
        return lines( //
                "## Summary", //
                "Changes");
    }

    private final InputStream projectOverviewInventory(final String repo, final String targetAudience) {
        final String content = lines( //
                "projects:", //
                "  - id: " + repo, //
                "    target_audience: " + targetAudience);
        return new ByteArrayInputStream(content.getBytes());
    }

    private String lines(final String... string) {
        return Stream.of(string).collect(Collectors.joining("\n"));
    }
}
