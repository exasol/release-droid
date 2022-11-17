package com.exasol.releasedroid.output.guide;

import static com.exasol.releasedroid.output.guide.ReleaseGuideProperties.error;
import static com.exasol.releasedroid.output.guide.ReleaseGuideTest.ahref;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReleaseGuidePropertiesTest {

    private static final Path SAMPLE_RD_CREDENTIALS_FILE = Path.of("sample-credentials-file");

    @Test
    void targetAudience_Unknown() {
        assertThat(announceChannel(false, ""), equalTo(error("unknown")));
    }

    @Test
    void targetAudience_Unsupported() {
        assertThat(announceChannel(true, "xxx"), equalTo(error("unsupported target audience '%s'", "xxx")));
    }

    @Test
    void targetAudience_UndefinedUrl() {
        assertThat(announceChannel(true, "team"), equalTo(noEntry("team_channel")));
    }

    @Test
    void targetAudience_Valid() {
        final Properties properties = properties(Map.of("team_channel", "https://team-channel"));
        assertThat(announceChannel(properties, true, "team"),
                equalTo("<a href=\"https://team-channel\">#integration</a>"));
    }

    @Test
    void releaseChecklists_NotConfigured() {
        assertThat(testee().releaseChecklists(), equalTo(noEntry("release_checklists")));
    }

    @Test
    void releaseChecklists_Configured() {
        assertThat(testee(properties(Map.of("release_checklists", "URL"))).releaseChecklists(),
                equalTo("Create new checklist on page " + ahref("URL", "Release checklists")));
    }

    @Test
    void teamPlanning_NotConfigured() {
        assertThat(testee().teamPlanning(), equalTo(noEntry("team_planning")));
    }

    @Test
    void teamPlanning_Configured() {
        assertThat(testee(properties(Map.of("team_planning", "URL"))).teamPlanning(), equalTo(ahref("URL")));
    }

    @Test
    void ignoreExceptions() {
        assertDoesNotThrow(() -> ReleaseGuideProperties.from(Path.of("/non/existing/file.txt")));
    }

    @Test
    void itest(@TempDir final Path temp) throws IOException {
        final Path file = temp.resolve("file.properties");
        Files.writeString(file, "team_planning=abc");
        assertThat(ReleaseGuideProperties.from(file).teamPlanning(), equalTo(ahref("abc")));
    }

    // ------------------------------------------------------------

    private String noEntry(final String key) {
        return error("no entry '<code>" + key + "</code>' in file <code>" + SAMPLE_RD_CREDENTIALS_FILE + "</code>");
    }

    private Properties properties(final Map<String, String> properties) {
        final Properties result = new Properties();
        result.putAll(properties);
        return result;
    }

    private String announceChannel(final boolean available, final String name) {
        return testee().announceChannel(targetAudience(available, name));
    }

    private String announceChannel(final Properties properties, final boolean available, final String name) {
        return testee(properties).announceChannel(targetAudience(available, name));
    }

    private ReleaseGuideProperties testee() {
        return testee(new Properties());
    }

    private ReleaseGuideProperties testee(final Properties properties) {
        return new ReleaseGuideProperties(SAMPLE_RD_CREDENTIALS_FILE, properties);
    }

    private TargetAudience targetAudience(final boolean available, final String name) {
        final TargetAudience targetAudience = mock(TargetAudience.class);
        when(targetAudience.available()).thenReturn(available);
        when(targetAudience.display()).thenReturn(name);
        return targetAudience;
    }
}
