package com.exasol.releasedroid.progress;

import java.time.Duration;
import java.time.Instant;

// [impl->dsn~progress-display~1]
public class ProgressMonitor {

    private Instant start;
    private Estimation estimation = Estimation.empty();
    private Instant eta;

    public ProgressMonitor start() {
        this.start = Instant.now();
        if (this.estimation.isPresent()) {
            this.eta = this.start.plus(this.estimation.duration());
        }
        return this;
    }

    public Duration elapsed() {
        return Duration.between(this.start, Instant.now());
    }

    public Duration remaining() {
        return this.estimation.durationOrZero().minus(elapsed());
    }

    public Instant eta() {
        return this.eta;
    }

    public Estimation estimation() {
        return this.estimation;
    }

    public Instant getStart() {
        return this.start;
    }

    public ProgressMonitor withEstimation(final Estimation value) {
        this.estimation = value;
        return this;
    }
}
