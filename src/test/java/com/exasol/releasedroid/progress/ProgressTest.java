package com.exasol.releasedroid.progress;

import static com.exasol.releasedroid.formatting.Colorizer.brightGreen;
import static com.exasol.releasedroid.formatting.Colorizer.yellow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ProgressTest {

    private static final Instant INSTANT = Instant.parse("2022-01-01T13:00:10Z");
    private static final Duration DURATION = Duration.ofHours(1).plusMinutes(1).plusSeconds(1);
    private static final Estimation ESTIMATION = new Estimation(INSTANT, DURATION);

    @Test
    void withoutLastRun() {
        final Progress testee = Progress.builder().start();
        assertThat(testee.formatElapsed(), equalTo("0:00:00"));
        assertThat(testee.status(), equalTo("0:00:00 elapsed"));
        assertThat(testee.welcomeMessage("prefix"), equalTo("prefix"));
    }

    @Test
    @SuppressWarnings("java:S8692") // testing the clock-related behavior is the point here.
    void startTime() {
        final String pattern = "HH mm ss";
        final Progress testee = progressBuilder() //
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
        assertThat(Progress.formatDuration(duration), equalTo(expected));
    }

    @Test
    @SuppressWarnings("java:S8692") // testing the clock-related behavior is the point here.
    void welcomeMessage() {
        final String timePattern = "HH mm ss";
        final String datePattern = "dd MM YYYY";
        final Progress testee = Progress.builder() //
                .estimation(ESTIMATION) //
                .datePattern(datePattern) //
                .timePattern(timePattern) //
                .start();
        final String prefix = "Hello";
        final String expected = "Hello\nLast release on "
                + format(INSTANT, datePattern)
                + " took ~ 1:01 hours.\nIf all goes well then the current release will be finished at "
                + format(Instant.now().plus(DURATION), timePattern)
                + ".";
        assertThat(testee.welcomeMessage(prefix), equalTo(expected));
    }

    private String format(final Instant start, final String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(start.atZone(ZoneId.systemDefault()));
    }

    @Test
    void status() {
        final ProgressMonitor monitor = mockProgressMonitor(ESTIMATION);
        final Duration delta = Duration.ofSeconds(3);
        when(monitor.elapsed()) //
                .thenReturn(Duration.ofMillis(300)) //
                .thenReturn(DURATION.minus(delta));
        when(monitor.remaining()) //
                .thenReturn(DURATION) //
                .thenReturn(delta);
        final Progress testee = startProgress(monitor, DURATION);

        assertThat(testee.status(), allOf( //
                containsString(brightGreen("0:00:00 elapsed")), //
                containsString(yellow("~ 1:01 hours remaining"))));
        // simulate sleeping 1:0:58 hours
        assertThat(testee.status(), allOf( //
                containsString(brightGreen("1:00:58 elapsed")), //
                containsString(yellow("3 seconds remaining"))));
    }

    private ProgressMonitor mockProgressMonitor(final Estimation estimation) {
        final ProgressMonitor monitor = mock(ProgressMonitor.class);
        when(monitor.estimation()).thenReturn(estimation);
        when(monitor.eta()).thenReturn(INSTANT);
        return monitor;
    }

    @Test
    void progress() {
        final Duration estimation = Duration.ofSeconds(1);
        final ProgressMonitor monitor = mockProgressMonitor(new Estimation(INSTANT, estimation));
        when(monitor.elapsed()) //
                .thenReturn(Duration.ofSeconds(0)) //
                .thenReturn(Duration.ofSeconds(1)) //
                .thenReturn(Duration.ofSeconds(2));
        when(monitor.remaining()) //
                .thenReturn(Duration.ofSeconds(1)) //
                .thenReturn(Duration.ofSeconds(0)) //
                .thenReturn(Duration.ofSeconds(-1));

        final Progress testee = startProgress(monitor, estimation);
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

    private Progress.Builder progressBuilder() {
        return Progress.builder().estimation(ESTIMATION);
    }

    private Progress startProgress(final ProgressMonitor monitor, final Duration estimation) {
        return new Progress.Builder(monitor)
                .estimation(Estimation.from(INSTANT, INSTANT.plus(estimation)))
                .datePattern("dd.MM.YYYY")
                .start();
    }
}
