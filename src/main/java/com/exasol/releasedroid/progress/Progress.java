package com.exasol.releasedroid.progress;

import static com.exasol.releasedroid.formatting.Colorizer.*;

import java.time.*;
import java.time.format.DateTimeFormatter;

// [impl->dsn~progress-display~1]
public class Progress {

    /**
     * Return a silent progress not printing anything when asked
     */
    public static final Progress SILENT = new SilentProgress();

    public static Builder builder() {
        return new Builder();
    }

    private static final boolean ECLIPSE_CONSOLE = false;
    private final ProgressMonitor monitor;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    private Progress(final ProgressMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * @return time the current progress has been started
     */
    public String startTime() {
        return formatTime(this.monitor.getStart());
    }

    /**
     * @param prefix prepend this prefix to welcome message
     * @return welcome message containing an estimation if available
     */
    public String welcomeMessage(final String prefix) {
        final Estimation estimation = this.monitor.estimation();
        return estimation.isPresent() //
                ? welcomeMessageWithEstimation(prefix, estimation) //
                : prefix;
    }

    private String welcomeMessageWithEstimation(final String prefix, final Estimation estimation) {
        return String.format("%s\nLast release on %s took %s.\n" //
                + "If all goes well then the current release will be finished at %s.", //
                prefix, //
                this.dateFormatter.format(localDateTime(estimation.timestamp())), //
                formatDuration(estimation.duration()), //
                formatTime(this.monitor.eta()));
    }

    String status() {
        final Duration elapsed = this.monitor.elapsed();
        final Estimation estimation = this.monitor.estimation();
        if (estimation.isPresent()) {
            return statusWithEstimation(elapsed, estimation.duration());
        } else {
            return formatElapsed(elapsed) + " elapsed";
        }
    }

    /**
     * Report the current status of the progress to {@code System.out}.
     */
    public void reportStatus() {
        stdoutFlush("\r" + status());
    }

    /**
     * Hide status that as been displayed to {@code System.out} in order to overwrite the previously displayed status
     * with other information.
     */
    public void hideStatus() {
        stdoutFlush("\r" + repeat(" ", 80) + "\r");
    }

    /**
     * Print a new line to {@code System.out} in order to display additional log messages after a status has been
     * displayed previously.
     */
    public void newline() {
        stdoutFlush("\n");
    }

    @SuppressWarnings("java:S106")
    // suppressing warnings for java:S106 - Standard outputs should not be used directly to log anything
    // since GitHubAPIAdapter is intended to print on standard out.
    // Using a logger cannot overwrite the current line.
    void stdoutFlush(final String s) {
        if (ECLIPSE_CONSOLE) {
            System.out.println(new String(new char[70]).replace("\0", "\r\n"));
        }
        System.out.print(s);
        System.out.flush();
    }

    private String statusWithEstimation(final Duration elapsed, final Duration estimation) {
        final Duration remaining = this.monitor.remaining();
        final double progress = (double) elapsed.toSeconds() / estimation.toSeconds();
        final boolean isOverdue = remaining.isNegative();
        final String remainingText = String.format("%s %s", formatDuration(remaining), //
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
                brightGreen(repeat("=", elapsed) + (elapsed < len ? ">" + repeat(" ", len - elapsed - 1) : "")),
                formatTime(this.monitor.eta()));
    }

    private String overdueBar(final int len, final double progress) {
        final int elapsed = (int) (len / progress);
        final int overdue = (int) ((progress - 1) * elapsed);
        return "[" + repeat("=", elapsed) //
                + red("|" + repeat("=", overdue) + ">") //
                + repeat(" ", 20);
    }

    private String elapsedColor(final boolean isOverdue, final String s) {
        return isOverdue ? yellow(s) : brightGreen(s);
    }

    private String remainingColor(final boolean isOverdue, final String s) {
        return isOverdue ? red(s) : yellow(s);
    }

    String formatElapsed() {
        return formatElapsed(this.monitor.elapsed());
    }

    private String formatElapsed(final Duration elapsed) {
        return String.format("%d:%02d:%02d", //
                elapsed.toHours(), //
                elapsed.toMinutesPart(), //
                elapsed.toSecondsPart());
    }

    private String formatTime(final Instant time) {
        return this.timeFormatter.format(localDateTime(time));
    }

    // ------------------------------------------------

    private static String plural(final long x) {
        return (x == 1 ? "" : "s");
    }

    static String repeat(final String s, final int repetitions) {
        final StringBuilder builder = new StringBuilder(repetitions);
        for (int i = 0; i < repetitions; i++) {
            builder.append(s);
        }
        return builder.toString();
    }

    /**
     * @param duration duration to be formatted
     * @return duration formatted in a colloquial way
     */
    public static String formatDuration(final Duration duration) {
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

    private static LocalDateTime localDateTime(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    // ------------------------------------------------

    private static class SilentProgress extends Progress {
        public SilentProgress() {
            super(null);
        }

        @Override
        public String status() {
            return "";
        }

        @Override
        void stdoutFlush(final String s) {
            // do not display anything
        }
    }

    /**
     * Builder for new instances of {@link Progress}
     */
    public static class Builder {

        private final Progress progress;

        Builder() {
            this(new ProgressMonitor());
        }

        Builder(final ProgressMonitor monitor) {
            this.progress = new Progress(monitor);
        }

        /**
         * @param estimation estimation for progress
         * @return this for fluent programming
         */
        public Builder estimation(final Estimation estimation) {
            this.progress.monitor.withEstimation(estimation);
            return this;
        }

        /**
         * @param value pattern to be used by the {@link Progress} to format times.
         * @return this for fluent programming
         */
        public Builder timePattern(final String value) {
            this.progress.timeFormatter = DateTimeFormatter.ofPattern(value);
            return this;
        }

        /**
         * @param value pattern to be used by the {@link Progress} to format dates.
         * @return this for fluent programming
         */
        public Builder datePattern(final String value) {
            this.progress.dateFormatter = DateTimeFormatter.ofPattern(value);
            return this;
        }

        /**
         * @return started progress
         */
        public Progress start() {
            this.progress.monitor.start();
            return this.progress;
        }
    }
}
