package com.exasol.releasedroid.adapter.github.progress;

import static com.exasol.releasedroid.adapter.github.progress.ProgressFormatter.green;
import static com.exasol.releasedroid.adapter.github.progress.ProgressFormatter.yellow;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.FILE_SEPARATOR;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_DIRECTORY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.kohsuke.github.*;

import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.usecases.PropertyReaderImpl;

class ProgressFormatterTest {

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
    void eta() throws InterruptedException {
        final Duration estimation = Duration.ofMinutes(1).plusSeconds(1);
        final ProgressMonitor monitor = new ProgressMonitor().withEstimation(estimation).start();
        assertThat(monitor.eta(), equalTo(monitor.getStart().plus(estimation)));
        assertThat(monitor.elapsed().toSeconds(), equalTo(0L));
        assertThat(secondsAsDouble(monitor.remaining()), closeTo(61, 0.5));
        Thread.sleep(1 * 1000);
        assertThat(monitor.elapsed().toSeconds(), equalTo(1L));
        assertThat(secondsAsDouble(monitor.remaining()), closeTo(60, 0.5));
    }

    double secondsAsDouble(final Duration duration) {
        return duration.toSeconds() + (duration.toMillisPart() / 1000.0);
    }

    @Test
    void welcomeMessage() throws InterruptedException {
        final Instant start = Instant.parse("2022-01-01T13:00:10Z");
        final Duration duration = Duration.ofHours(1).plusMinutes(2);
        final String timePattern = "HH mm ss";
        final String datePattern = "dd MM YYYY";
        final ProgressFormatter testee = ProgressFormatter.builder() //
                .lastStart(Date.from(start)) //
                .lastEnd(Date.from(start.plus(duration))) //
                .datePattern(datePattern) //
                .timePattern(timePattern) //
                .start();
        final String prefix = "Hello";
        final String expected = String.format(prefix + "\n" //
                + "Last release on %s took ~ 1:02 hours.\n"
                + "If all goes well then the current release will be finished at %s.", format(start, datePattern),
                format(Instant.now().plus(duration), timePattern));
        assertThat(testee.welcomeMessage(prefix), equalTo(expected));
    }

    private String format(final Instant start, final String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(start.atZone(ZoneId.systemDefault()));
    }

    @Test
    void status() throws InterruptedException {
        final Duration estimation = Duration.ofMinutes(1).plusSeconds(1);
        final ProgressFormatter testee = startFormatter(estimation);
        assertThat(testee.status(), containsString(green("0:00:00 elapsed")));
        assertThat(testee.status(), containsString(yellow("~ 1 minute remaining")));
        Thread.sleep(1 * 1000);
        assertThat(testee.status(), containsString(green("0:00:01 elapsed")));
        assertThat(testee.status(), containsString(yellow("59 seconds remaining")));
    }

    @Test
    void timeout() throws InterruptedException {
        final ProgressFormatter formatter = ProgressFormatter.builder() //
                .timeout(Duration.ofMillis(100)) //
                .start();
        assertThat(formatter.timeout(), is(false));
        Thread.sleep(200);
        assertThat(formatter.timeout(), is(true));
    }

//    @Tag("integration")
//    @Test
    void latest() throws IOException, GitHubException, InterruptedException {
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

        final int n = 100;
        final String prefix = testee.startTime() + ": Started GitHub workflow '" + "ci-build.yml" + "': " //
                + run.getHtmlUrl() + "\n" //
                + "The Release Droid is monitoring its progress.\n" //
                + "This can take from a few minutes to a couple of hours depending on the build.";
        System.out.println(testee.welcomeMessage(prefix));
        for (int i = 0; i < 2; i++) {
            Thread.sleep(estimation.dividedBy(n / 2).toMillis());
            fixEclipseConsole();
            System.out.print("\r" + testee.status());
            System.out.flush();
        }
    }

    private GHWorkflowRun lastRun(final GitHubConnectorImpl connector, final String repo, final String workflowName)
            throws IOException {
        final GHRepository repository = connector.connectToGitHub().getRepository(repo);
        final GHWorkflow workflow = repository.getWorkflow("ci-build.yml");
        final GitHubAPIAdapter adapter = new GitHubAPIAdapter(connector);
        return adapter.latestRun(workflow);
    }

    private ProgressFormatter startFormatter(final Duration estimation) {
        final Instant start = Instant.now();
        return startFormatter(Date.from(start), Date.from(start.plus(estimation)));
    }

    private ProgressFormatter startFormatter(final Date start, final Date end) {
        return ProgressFormatter.builder() //
                .lastStart(start) //
                .lastEnd(end) //
                .datePattern("dd.MM.YYYY") //
                .start();
    }

    @Test
    void progress() throws InterruptedException {
        final Duration estimation = Duration.ofSeconds(1);
        final ProgressFormatter testee = startFormatter(estimation);
        final int n = 5;
        final String[][] expected = { { "0:00:00 elapsed", "0 seconds remaining", "0%", "[", ">" },
                { "0:00:01 elapsed", "0 seconds overdue", "100%", "[===================", "|>" },
                { "0:00:01 elapsed", "0 seconds overdue", "100%", "[===================", "|>" },
                { "0:00:02 elapsed", "1 second overdue", "200%", "[=========", "|=========>" },
                { "0:00:02 elapsed", "1 second overdue", "200%", "[=========", "|=========>" } };
        System.out.println(testee.startTime());
        for (int i = 0; i < n; i++) {
            Thread.sleep(estimation.dividedBy(n / 2).toMillis());
            fixEclipseConsole();
            verifyContents(testee.status(), expected[i]);
            System.out.print("\r" + testee.status());
            System.out.flush();
        }
        System.out.println();
    }

    private void verifyContents(final String actual, final String... expected) {
        for (final String e : expected) {
            assertThat(actual, containsString(e));
        }
    }

    private void fixEclipseConsole() {
        if (System.getProperty("sun.java.command").startsWith("org.eclipse")) {
            System.out.println(new String(new char[70]).replace("\0", "\r\n"));
        }
    }
}
