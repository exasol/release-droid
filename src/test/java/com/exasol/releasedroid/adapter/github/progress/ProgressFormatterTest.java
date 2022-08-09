package com.exasol.releasedroid.adapter.github.progress;

import static com.exasol.releasedroid.adapter.github.progress.ProgressFormatter.green;
import static com.exasol.releasedroid.adapter.github.progress.ProgressFormatter.yellow;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.FILE_SEPARATOR;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_DIRECTORY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.kohsuke.github.*;
import org.mockito.Mockito;

import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.usecases.PropertyReaderImpl;

class ProgressFormatterTest {

    private static final Instant INSTANT = Instant.parse("2022-01-01T13:00:10Z");

    @Test
    void withoutStart() {
        final ProgressFormatter testee = ProgressFormatter.builder().start();
        assertThat(testee.formatElapsed(), equalTo("0:00:00"));
        assertThat(testee.status(), equalTo("0:00:00 elapsed"));
        assertThat(testee.welcomeMessage("prefix"), equalTo("prefix"));
    }

    @Test
    void welcomeWithoutEstimation() {
        final ProgressFormatter testee = ProgressFormatter.builder() //
                .lastStart(Date.from(Instant.now())) //
                .start();
        assertThat(testee.welcomeMessage("prefix"), equalTo("prefix"));
    }

    @Test
    void startTime() {
        final String pattern = "HH mm ss";
        final ProgressFormatter testee = ProgressFormatter.builder() //
                .timePattern(pattern).lastStart(Date.from(Instant.now())) //
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
        final Duration duration = Duration.ofHours(1).plusMinutes(2);
        final String timePattern = "HH mm ss";
        final String datePattern = "dd MM YYYY";
        final ProgressFormatter testee = ProgressFormatter.builder() //
                .lastStart(Date.from(INSTANT)) //
                .lastEnd(Date.from(INSTANT.plus(duration))) //
                .datePattern(datePattern) //
                .timePattern(timePattern) //
                .start();
        final String prefix = "Hello";
        final String expected = String.format(prefix + "\n" //
                + "Last release on %s took ~ 1:02 hours.\n"
                + "If all goes well then the current release will be finished at %s.", //
                format(INSTANT, datePattern), //
                format(Instant.now().plus(duration), timePattern));
        assertThat(testee.welcomeMessage(prefix), equalTo(expected));
    }

    private String format(final Instant start, final String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(start.atZone(ZoneId.systemDefault()));
    }

    private LocalDateTime toLocal(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Test
    void status() throws InterruptedException {
        final Duration estimation = Duration.ofMinutes(1).plusSeconds(1);
        final ProgressMonitor monitor = mockProgressMonitor(estimation);
        when(monitor.elapsed()) //
                .thenReturn(Duration.ofMillis(300)) //
                .thenReturn(Duration.ofSeconds(1));
        when(monitor.remaining()).thenReturn(estimation) //
                .thenReturn(Duration.ofSeconds(59));
        final ProgressFormatter testee = startFormatter(monitor, estimation);

        final String status1 = testee.status();
        assertThat(status1, containsString(green("0:00:00 elapsed")));
        assertThat(status1, containsString(yellow("~ 1 minute remaining")));
        // sleep 1 s
        final String status2 = testee.status();
        assertThat(status2, containsString(green("0:00:01 elapsed")));
        assertThat(status2, containsString(yellow("59 seconds remaining")));
    }

    private ProgressMonitor mockProgressMonitor(final Duration estimation) {
        final ProgressMonitor monitor = Mockito.mock(ProgressMonitor.class);
        when(monitor.getEstimation()).thenReturn(Optional.of(estimation));
        when(monitor.eta()).thenReturn(toLocal(INSTANT));
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
        // sleep 200 ms
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
            verifyContents(testee.status(), expected[i]);
        }
    }

    private void verifyContents(final String actual, final String... expected) {
        for (final String e : expected) {
            assertThat(actual, containsString(e));
        }
    }

    void manualIntegrationTestWithoutGithub() throws InterruptedException {
        final Duration estimation = Duration.ofSeconds(1);
        final ProgressFormatter testee = startFormatter(new ProgressMonitor(), estimation);
        new ManualExplorer().estimation(estimation).iterations(5).run(testee, "");
    }

    void manualIntegrationTestWithGithub() throws IOException, GitHubException, InterruptedException {
        final String RELEASE_DROID_CREDENTIALS = RELEASE_DROID_DIRECTORY + FILE_SEPARATOR + "credentials";
        final PropertyReaderImpl reader = new PropertyReaderImpl(RELEASE_DROID_CREDENTIALS);
        final GitHubConnectorImpl connector = new GitHubConnectorImpl(reader);
        final GHWorkflowRun run = lastRun(connector, "exasol/release-droid", "ci-build.yml");

        final ProgressFormatter testee = ProgressFormatter.builder() //
                .datePattern("dd.MM.YYYY") //
                .lastStart(run.getCreatedAt()) //
                .lastEnd(run.getUpdatedAt()) //
                .start();
        final Duration estimation = Duration.between( //
                ProgressFormatter.zonedDateTime(run.getCreatedAt()), //
                ProgressFormatter.zonedDateTime(run.getUpdatedAt()));

        final String prefix = testee.startTime() + ": Started GitHub workflow '" + "ci-build.yml" + "': " //
                + run.getHtmlUrl() + "\n" //
                + "The Release Droid is monitoring its progress.\n" //
                + "This can take from a few minutes to a couple of hours depending on the build.";
        new ManualExplorer().estimation(estimation).iterations(3).sleepNumerator(50).run(testee, prefix);
    }

    private GHWorkflowRun lastRun(final GitHubConnectorImpl connector, final String repo, final String workflowName)
            throws IOException {
        final GHRepository repository = connector.connectToGitHub().getRepository(repo);
        final GHWorkflow workflow = repository.getWorkflow("ci-build.yml");
        final GitHubAPIAdapter adapter = new GitHubAPIAdapter(connector);
        return adapter.latestRun(workflow);
    }

    private ProgressFormatter startFormatter(final ProgressMonitor monitor, final Duration estimation) {
        return startFormatter(new ProgressFormatter.Builder(monitor), //
                Date.from(INSTANT), Date.from(INSTANT.plus(estimation)));
    }

    private ProgressFormatter startFormatter(final ProgressFormatter.Builder builder, final Date start,
            final Date end) {
        return builder //
                .lastStart(start) //
                .lastEnd(end) //
                .datePattern("dd.MM.YYYY") //
                .start();
    }

    static class ManualExplorer {
        private int iterations;
        private Duration estimation;
        private int sleepNumerator = -1;

        public ManualExplorer iterations(final int value) {
            this.iterations = value;
            return this;
        }

        public ManualExplorer sleepNumerator(final int value) {
            this.sleepNumerator = value;
            return this;
        }

        public ManualExplorer estimation(final Duration value) {
            this.estimation = value;
            return this;
        }

        // class Visualizer is only used for manual exploration
        // Sonar warnings are suppressed therefore:
        // squid:L73 - replace System.out by a Logger
        // java:S2925 - "Thread.sleep" should not be used in tests
        @java.lang.SuppressWarnings({ "squid:L73", "java:S2925" })
        public void run(final ProgressFormatter testee, final String prefix) throws InterruptedException {
            System.out.println(testee.welcomeMessage(prefix));
            final int numerator = this.sleepNumerator > 0 ? this.sleepNumerator : this.iterations / 2;
            final long sleep = this.estimation.dividedBy(numerator).toMillis();
            for (int i = 0; i < this.iterations; i++) {
                Thread.sleep(sleep);
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
