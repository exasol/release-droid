package com.exasol.releasedroid.adapter.github.progress;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class ProgressMonitor {

    public static ProgressMonitor from(final Duration estimation, final Duration timeout) {
        return new ProgressMonitor(Optional.ofNullable(estimation), Optional.ofNullable(timeout));
    }

    LocalDateTime start;
    private final Optional<Duration> estimation;
    private final Optional<Duration> timeout;
    private LocalDateTime eta;

    public ProgressMonitor(final Optional<Duration> estimation, final Optional<Duration> timeout) {
        this.estimation = estimation;
        this.timeout = timeout;
    }

    public ProgressMonitor start() {
        this.start = LocalDateTime.now();
        if (this.estimation.isPresent()) {
            this.eta = this.start.plus(this.estimation.get());
        }
        return this;
    }

    public Duration elapsed() {
        return Duration.between(this.start, LocalDateTime.now());
    }

    public Duration remaining() {
        return this.estimation.get().minus(elapsed());
    }

    public LocalDateTime eta() {
        return this.eta;
    }

    public Optional<Duration> getEstimation() {
        return this.estimation;
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public boolean timeout() {
        if (this.timeout.isEmpty()) {
            return false;
        }
        return elapsed().compareTo(this.timeout.get()) > 0;
    }

}
