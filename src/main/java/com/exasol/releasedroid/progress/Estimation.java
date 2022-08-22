package com.exasol.releasedroid.progress;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

// [impl->dsn~estimate-duration~1]
public class Estimation {

    /**
     * @return estimation with undefined duration
     */
    public static Estimation empty() {
        return new Estimation(null, null);
    }

    /**
     * @param duration duration representing the estimation
     * @return new instance of {@link Estimation}
     */
    public static Estimation of(final Duration duration) {
        return new Estimation(null, duration);
    }

    /**
     * Compute estimation defined by period of time between start and end.
     *
     * @param start start of time period to compute estimation from
     * @param end   end of time period to compute estimation from
     * @return duration representing the estimation
     */
    public static Estimation from(final Date start, final Date end) {
        final Instant startInstant = start.toInstant();
        return new Estimation(startInstant, Duration.between(startInstant, end.toInstant()));
    }

    private Instant timestamp;
    private Duration duration;

    Estimation(final Instant timestamp, final Duration duration) {
        this.timestamp = timestamp;
        this.duration = duration;
    }

    /**
     * @return time stamp reference of estimation
     */
    public Instant timestamp() {
        return this.timestamp;
    }

    /**
     * @return duration of estimation
     */
    public Duration duration() {
        return this.duration;
    }

    /**
     * @return {@code true} if current estimation has non-null duration and time stamp
     */
    public boolean isPresent() {
        return (this.duration != null) && (this.timestamp != null);
    }

    /**
     * @param other another estimation to be added to the current one
     * @return this for fluent programming
     */
    public Estimation add(final Estimation other) {
        if (other.duration != null) {
            this.duration = durationOrZero().plus(other.duration);
            if (this.timestamp == null) {
                this.timestamp = other.timestamp();
            }
        }
        return this;
    }

    /**
     * @return duration of estimation or {@code Duration.ZERO}
     */
    public Duration durationOrZero() {
        return this.duration == null ? Duration.ZERO : this.duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.duration, this.timestamp);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Estimation other = (Estimation) obj;
        return Objects.equals(this.duration, other.duration) && Objects.equals(this.timestamp, other.timestamp);
    }

    @Override
    public String toString() {
        final LocalDateTime utc = LocalDateTime.ofInstant(this.timestamp, ZoneId.of("UTC"));
        return String.format("%d:%02d:%02d hours @ %s %s (UTC)", //
                this.duration.toHours(), //
                this.duration.toMinutesPart(), //
                this.duration.toSecondsPart(), //
                DateTimeFormatter.ISO_LOCAL_DATE.format(utc), //
                DateTimeFormatter.ISO_LOCAL_TIME.format(utc));
    }
}
