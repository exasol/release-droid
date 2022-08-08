package com.exasol.releasedroid.adapter.github.progress;

import static com.exasol.releasedroid.adapter.github.progress.ProgressFormatter.green;
import static com.exasol.releasedroid.adapter.github.progress.ProgressFormatter.yellow;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.FILE_SEPARATOR;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_DIRECTORY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHWorkflow;

import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.usecases.PropertyReaderImpl;

class ProgressMonitorTest {

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
        final ProgressMonitor monitor = ProgressMonitor.from(estimation, null).start();
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
        final ProgressFormatter formatter = ProgressFormatter.builder().estimation(null) //
                .timeout(Duration.ofMillis(100)) //
                .start();
        assertThat(formatter.timeout(), is(false));
        Thread.sleep(200);
        assertThat(formatter.timeout(), is(true));
    }

    // @Test
    void latest() throws IOException, GitHubException, InterruptedException {
        final String RELEASE_DROID_CREDENTIALS = RELEASE_DROID_DIRECTORY + FILE_SEPARATOR + "credentials";
        final PropertyReaderImpl reader = new PropertyReaderImpl(RELEASE_DROID_CREDENTIALS);
        final GitHubConnectorImpl connector = new GitHubConnectorImpl(reader);
        final Duration estimation = durationOfLastRun(connector, "exasol/release-droid", "ci-build.yml");

        final ProgressFormatter testee = startFormatter(estimation);
        final int n = 100;
        System.out.println(testee.startTime());
        for (int i = 0; i < n; i++) {
            Thread.sleep(estimation.dividedBy(n / 2).toMillis());
            fixEclipseConsole();
            System.out.print("\r" + testee.status());
            System.out.flush();
        }
    }

    private Duration durationOfLastRun(final GitHubConnectorImpl connector, final String repo,
            final String workflowName) throws IOException {
        final GHRepository repository = connector.connectToGitHub().getRepository(repo);
        final GHWorkflow workflow = repository.getWorkflow("ci-build.yml");
        final GitHubAPIAdapter adapter = new GitHubAPIAdapter(connector);
        return adapter.duration(adapter.latestRun(workflow));
    }

    private ProgressFormatter startFormatter(final Duration estimation) {
        return ProgressFormatter.builder().estimation(estimation).pattern("HH:mm:ss").start();
    }

    @Test
    void progress() throws InterruptedException {
        final Duration estimation = Duration.ofSeconds(3);
        final ProgressFormatter testee = startFormatter(estimation);
        final int n = 10;
        System.out.println(testee.startTime());
        for (int i = 0; i < n; i++) {
            Thread.sleep(estimation.dividedBy(n / 2).toMillis());
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
