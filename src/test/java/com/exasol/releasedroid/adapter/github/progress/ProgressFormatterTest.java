package com.exasol.releasedroid.adapter.github.progress;

import static com.exasol.releasedroid.formatting.Colorizer.brightGreen;
import static com.exasol.releasedroid.formatting.Colorizer.yellow;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.FILE_SEPARATOR;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_DIRECTORY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.kohsuke.github.*;
import org.mockito.Mockito;

import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.usecases.PropertyReaderImpl;

class ProgressFormatterTest {

    private static final Instant INSTANT = Instant.parse("2022-01-01T13:00:10Z");
    private static final Duration DURATION = Duration.ofHours(1).plusMinutes(1).plusSeconds(1);

    @Test
    void withoutLastRun() {
        final ProgressFormatter testee = ProgressFormatter.builder().start();
        assertThat(testee.formatElapsed(), equalTo("0:00:00"));
        assertThat(testee.status(), equalTo("0:00:00 elapsed"));
        assertThat(testee.welcomeMessage("prefix"), equalTo("prefix"));
    }

    @Test
    void startTime() {
        final String pattern = "HH mm ss";
        final ProgressFormatter testee = formatterBuilder() //
                .timePattern(pattern) //
                .start();
        assertThat(testee.startTime(), equalTo(format(Instant.now(), pattern)));
    }

    @ParameterizedTest
    @CsvSource(value = { //
            "3:20:44, ~ 3:20 hours", //
            "0:20:44, ~ 20 minutes", //
            "0:00:44, 44 seconds" //
    })
    void formatRemaining(final String remaining, final String expected) {
        final Duration duration = Duration.parse(String.format("PT%sH%sM%sS", (Object[]) remaining.split(":")));
        assertThat(ProgressFormatter.formatRemaining(duration), equalTo(expected));
    }

    @Test
    void welcomeMessage() throws InterruptedException {
        final String timePattern = "HH mm ss";
        final String datePattern = "dd MM YYYY";
        final ProgressFormatter testee = ProgressFormatter.builder() //
                .lastRun(Date.from(INSTANT), Date.from(INSTANT.plus(DURATION))) //
                .datePattern(datePattern) //
                .timePattern(timePattern) //
                .start();
        final String prefix = "Hello";
        final String expected = String.format(prefix + "\n" //
                + "Last release on %s took ~ 1:01 hours.\n"
                + "If all goes well then the current release will be finished at %s.", //
                format(INSTANT, datePattern), //
                format(Instant.now().plus(DURATION), timePattern));
        assertThat(testee.welcomeMessage(prefix), equalTo(expected));
    }

    private String format(final Instant start, final String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(start.atZone(ZoneId.systemDefault()));
    }

    @Test
    void status() throws InterruptedException {
        final ProgressMonitor monitor = mockProgressMonitor(DURATION);
        final Duration delta = Duration.ofSeconds(3);

        when(monitor.elapsed()) //
                .thenReturn(Duration.ofMillis(300)) //
                .thenReturn(DURATION.minus(delta));
        when(monitor.remaining()) //
                .thenReturn(DURATION) //
                .thenReturn(delta);
        final ProgressFormatter testee = startFormatter(monitor, DURATION);

        assertThat(testee.status(), allOf( //
                containsString(brightGreen("0:00:00 elapsed")), //
                containsString(yellow("~ 1:01 hours remaining"))));
        // simulate sleeping 1:0:58 hours
        assertThat(testee.status(), allOf( //
                containsString(brightGreen("1:00:58 elapsed")), //
                containsString(yellow("3 seconds remaining"))));
    }

    private ProgressMonitor mockProgressMonitor(final Duration estimation) {
        final ProgressMonitor monitor = Mockito.mock(ProgressMonitor.class);
        when(monitor.getEstimation()).thenReturn(Optional.of(estimation));
        when(monitor.eta()).thenReturn(INSTANT);
        return monitor;
    }

    @Test
    void timeout() throws InterruptedException {
        final Duration timeout = Duration.ofMillis(100);
        final ProgressMonitor monitor = Mockito.mock(ProgressMonitor.class);
        when(monitor.isTimeout())//
                .thenReturn(false) //
                .thenReturn(true);
        final ProgressFormatter formatter = new ProgressFormatter.Builder(monitor) //
                .timeout(timeout) //
                .start();
        assertThat(formatter.timeout(), is(false));
        // simulate sleeping 200 ms, i.e. longer than timeout defined initially 1:58 minutes
        assertThat(formatter.timeout(), is(true));
    }

    @Test
    void progress() throws InterruptedException {
        final Duration estimation = Duration.ofSeconds(1);
        final ProgressMonitor monitor = mockProgressMonitor(estimation);
        when(monitor.elapsed()) //
                .thenReturn(Duration.ofSeconds(0)) //
                .thenReturn(Duration.ofSeconds(1)) //
                .thenReturn(Duration.ofSeconds(2));
        when(monitor.remaining()) //
                .thenReturn(Duration.ofSeconds(1)) //
                .thenReturn(Duration.ofSeconds(0)) //
                .thenReturn(Duration.ofSeconds(-1));

        final ProgressFormatter testee = startFormatter(monitor, estimation);
        final String[][] expected = { //
                { "0:00:00 elapsed", "1 second remaining", "0%", "[", ">" },
                { "0:00:01 elapsed", "0 seconds remaining", "100%", "[===================", "|>" },
                { "0:00:02 elapsed", "1 second overdue", "200%", "[=========", "|=========>" } };
        for (int i = 0; i < 3; i++) {
            assertThat(testee.status(), matchers(expected[i]));
        }
    }

    @SuppressWarnings("unchecked")
    private Matcher<String> matchers(final String... expected) {
        return allOf(Arrays.stream(expected).map(Matchers::containsString).toArray(Matcher[]::new));
    }

    void manualIntegrationTestWithoutGithub() throws InterruptedException {
        final Duration estimation = Duration.ofSeconds(4);
        final ProgressFormatter testee = startFormatter(new ProgressMonitor(), estimation);
        new ManualExplorer().estimation(estimation).iterations(5, 3).run(testee, "");
    }

    void manualIntegrationTestWithGithub() throws IOException, GitHubException, InterruptedException {
        final String credentials = RELEASE_DROID_DIRECTORY + FILE_SEPARATOR + "credentials";
        final PropertyReaderImpl reader = new PropertyReaderImpl(credentials);
        final GitHubConnectorImpl connector = new GitHubConnectorImpl(reader);
        final GHWorkflowRun run = lastRun(connector, "exasol/release-droid", "ci-build.yml");

        final ProgressFormatter testee = ProgressFormatter.builder() //
                .datePattern("dd.MM.YYYY") //
                .lastRun(run.getCreatedAt(), run.getUpdatedAt()) //
                .start();
        final Duration estimation = Duration.between( //
                run.getCreatedAt().toInstant(), //
                run.getUpdatedAt().toInstant());

        final String prefix = testee.startTime() + ": Started GitHub workflow 'ci-build.yml': " //
                + run.getHtmlUrl() + "\n" //
                + "The Release Droid is monitoring its progress.\n" //
                + "This can take from a few minutes to a couple of hours depending on the build.";
        new ManualExplorer().estimation(estimation).iterations(3, 50).run(testee, prefix);
    }

    private GHWorkflowRun lastRun(final GitHubConnectorImpl connector, final String repo, final String workflowName)
            throws IOException {
        final GHRepository repository = connector.connectToGitHub().getRepository(repo);
        final GHWorkflow workflow = repository.getWorkflow("ci-build.yml");
        final GitHubAPIAdapter adapter = new GitHubAPIAdapter(connector);
        return adapter.latestRun(workflow);
    }

    private ProgressFormatter.Builder formatterBuilder() {
        return ProgressFormatter.builder() //
                .lastRun(Date.from(INSTANT), Date.from(INSTANT.plus(DURATION)));
    }

    private ProgressFormatter startFormatter(final ProgressMonitor monitor, final Duration estimation) {
        return new ProgressFormatter.Builder(monitor) //
                .lastRun(Date.from(INSTANT), //
                        Date.from(INSTANT.plus(estimation))) //
                .datePattern("dd.MM.YYYY") //
                .start();
    }

    static class ManualExplorer {
        private Duration estimation = Duration.ofSeconds(2);
        private int iterations = 5;
        private int intervals = 3;

        public ManualExplorer estimation(final Duration value) {
            this.estimation = value;
            return this;
        }

        public ManualExplorer iterations(final int iterations, final int intervals) {
            this.iterations = iterations;
            this.intervals = intervals;
            return this;
        }

        // class is only used for manual exploration
        // Sonar warnings are suppressed therefore:
        // java:S2925 - "Thread.sleep" should not be used in tests
        @SuppressWarnings("java:S2925")
        public void run(final ProgressFormatter testee, final String prefix) throws InterruptedException {
            System.out.println(testee.welcomeMessage(prefix));
            for (int i = 0; i < this.iterations; i++) {
                Thread.sleep(this.estimation.dividedBy(this.intervals).toMillis());
                fixEclipseConsole();
                System.out.print("\r" + testee.status());
                System.out.flush();
            }
            System.out.println();
        }

        private void fixEclipseConsole() {
            if (System.getProperty("sun.java.command").startsWith("org.eclipse")) {
                System.out.println(new String(new char[70]).replace("\0", "\r\n"));
            }
        }
    }
}
