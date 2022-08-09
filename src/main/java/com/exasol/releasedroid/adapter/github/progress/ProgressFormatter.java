package com.exasol.releasedroid.adapter.github.progress;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.ANSI_RESET;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class ProgressFormatter {

    public static Builder builder() {
        return new Builder();
    }

    private static final String ANSI_GREEN = "\u001B[92m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[93m";

    private final ProgressMonitor monitor;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private ZonedDateTime lastStart = null;

    private ProgressFormatter(final ProgressMonitor monitor) {
        this.monitor = monitor;
    }

    public String startTime() {
        return formatTime(this.monitor.getStart());
    }

    public String welcomeMessage(final String prefix) {
        final Optional<Duration> estimation = this.monitor.getEstimation();
        if ((this.lastStart == null) || estimation.isEmpty()) {
            return prefix;
        } else {
            return welcomeMessageWithEstimation(prefix, estimation.get());
        }
    }

    private String welcomeMessageWithEstimation(final String prefix, final Duration estimation) {
        return String.format("%s\nLast release on %s took %s.\n" //
                + "If all goes well then the current release will be finished at %s.", //
                prefix, //
                this.dateFormatter.format(this.lastStart), //
                formatRemaining(estimation), //
                formatTime(this.monitor.eta()));
    }

    public String status() {
        final Duration elapsed = this.monitor.elapsed();
        final Optional<Duration> estimation = this.monitor.getEstimation();
        if (estimation.isEmpty()) {
            return formatElapsed(elapsed) + " elapsed";
        } else {
            return statusWithEstimation(elapsed, estimation.get());
        }
    }

    private String statusWithEstimation(final Duration elapsed, final Duration estimation) {
        final Duration remaining = this.monitor.remaining();
        final double progress = (double) elapsed.toSeconds() / estimation.toSeconds();
        final boolean isOverdue = remaining.isNegative();
        final String remainingText = String.format("%s %s", formatRemaining(remaining), //
                isOverdue ? "overdue" : "remaining");

        return String.format("%s, %32s. %s %s", //
                elapsedColor(isOverdue, formatElapsed(elapsed) + " elapsed"), //
                remainingColor(isOverdue, remainingText), //
                elapsedColor(isOverdue, String.format("%3.0f%%", progress * 100)), //
                progressBar(progress));
    }

    private String progressBar(final double progress) {
        final int len = 20;
        if (progress >= 1) {
            return overdueBar(len - 1, progress);
        }
        final int elapsed = (int) Math.round(progress * len);
        return String.format("[%s] ETA: %s", //
                green(makeString("=", elapsed) + (elapsed < len ? ">" + makeString(" ", len - elapsed - 1) : "")),
                formatTime(this.monitor.eta()));
    }

    private String overdueBar(final int len, final double progress) {
        final int elapsed = (int) (len / progress);
        final int overdue = (int) ((progress - 1) * elapsed);
        return "[" + makeString("=", elapsed) //
                + red("|" + makeString("=", overdue) + ">") //
                + makeString(" ", 20);
    }

    private String elapsedColor(final boolean isOverdue, final String s) {
        return isOverdue ? yellow(s) : green(s);
    }

    private String remainingColor(final boolean isOverdue, final String s) {
        return isOverdue ? red(s) : yellow(s);
    }

    public String formatElapsed() {
        return formatElapsed(this.monitor.elapsed());
    }

    private String formatElapsed(final Duration elapsed) {
        return String.format("%d:%02d:%02d", //
                elapsed.toHours(), //
                elapsed.toMinutesPart(), //
                elapsed.toSecondsPart());
    }

    private String formatTime(final LocalDateTime time) {
        return this.timeFormatter.format(time);
    }

    // ------------------------------------------------

    private static String plural(final long x) {
        return (x == 1 ? "" : "s");
    }

    static String green(final String s) {
        return ANSI_GREEN + s + ANSI_RESET;
    }

    static String red(final String s) {
        return ANSI_RED + s + ANSI_RESET;
    }

    static String yellow(final String s) {
        return ANSI_YELLOW + s + ANSI_RESET;
    }

    static String makeString(final String s, final int repetitions) {
        final StringBuilder builder = new StringBuilder(repetitions);
        for (int i = 0; i < repetitions; i++) {
            builder.append(s);
        }
        return builder.toString();
    }

    static String formatRemaining(final Duration duration) {
        final long h = duration.abs().toHours();
        if (h > 0) {
            return String.format("~ %d:%02d hours", h, duration.toMinutesPart());
        }
        final long m = duration.abs().toMinutes();
        if (m > 0) {
            return String.format("~ %d minute%s", m, plural(m));
        }

        final long s = duration.abs().toSeconds();
        return String.format("%d second%s", s, plural(s));
    }

    static ZonedDateTime zonedDateTime(final Date date) {
        return date.toInstant().atZone(ZoneOffset.UTC);
    }

    // ------------------------------------------------

    public static class Builder {

        private final ProgressFormatter formatter;

        Builder() {
            this(new ProgressMonitor());
        }

        Builder(final ProgressMonitor monitor) {
            this.formatter = new ProgressFormatter(monitor);
        }

        public Builder timeout(final Duration value) {
            this.formatter.monitor.withTimeout(value);
            return this;
        }

        public Builder lastRun(final Date start, final Date end) {
            final ZonedDateTime zonedStart = zonedDateTime(start);
            this.formatter.lastStart = zonedStart;
            this.formatter.monitor.withEstimation( //
                    Duration.between(zonedStart, zonedDateTime(end)));
            return this;
        }

        public Builder timePattern(final String value) {
            this.formatter.timeFormatter = DateTimeFormatter.ofPattern(value);
            return this;
        }

        public Builder datePattern(final String value) {
            this.formatter.dateFormatter = DateTimeFormatter.ofPattern(value);
            return this;
        }

        public ProgressFormatter start() {
            this.formatter.monitor.start();
            return this.formatter;
        }
    }

    public boolean timeout() {
        return this.monitor.isTimeout();
    }
}
