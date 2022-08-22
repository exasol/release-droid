package com.exasol.releasedroid.progress;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;

class EstimationTest {

    private static final Duration DURATION = Duration.ofMinutes(1).plusSeconds(2);
    private static final Instant START = Instant.parse("2022-02-28T09:30:59.000Z");
    private static final Date DATE1 = Date.from(START);
    private static final Date DATE2 = Date.from(START.plus(DURATION));
    private static final Estimation ESTIMATION_WITHOUT_TIMESTAMP = Estimation.of(DURATION);
    private static final Estimation ESTIMATION = Estimation.from(DATE1, DATE2);

    @Test
    void empty() {
        final Estimation testee = Estimation.empty();
        assertThat(testee.isPresent(), is(false));
        assertThat(testee.duration(), nullValue());
        assertThat(testee.timestamp(), nullValue());
    }

    @Test
    void withoutTimeStamp() {
        final Estimation testee = ESTIMATION_WITHOUT_TIMESTAMP;
        assertThat(testee.isPresent(), is(false));
        assertThat(testee.duration(), equalTo(DURATION));
        assertThat(testee.timestamp(), nullValue());
    }

    @Test
    void present() {
        final Estimation testee = ESTIMATION;
        assertThat(testee.isPresent(), is(true));
        assertThat(testee.duration(), equalTo(DURATION));
        assertThat(testee.timestamp(), equalTo(START));
    }

    @Test
    void add() {
        verifyAdd(ESTIMATION_WITHOUT_TIMESTAMP, ESTIMATION, new Estimation(START, DURATION.multipliedBy(2)));
        verifyAdd(ESTIMATION, ESTIMATION, new Estimation(START, DURATION.multipliedBy(2)));
        verifyAdd(ESTIMATION, Estimation.empty(), new Estimation(START, DURATION));
    }

    @Test
    void testToString() {
        assertThat(ESTIMATION.toString(), equalTo("0:01:02 hours @ 2022-02-28 09:30:59 (UTC)"));
    }

    private void verifyAdd(final Estimation first, final Estimation second, final Estimation expected) {
        final Estimation actual = instance(first).add(second);
        assertThat(actual.isPresent(), equalTo(first.isPresent() || second.isPresent()));
        assertThat(actual, equalTo(expected));
        // assert add is commutative
        assertThat(actual, equalTo(instance(second).add(first)));
    }

    private Estimation instance(final Estimation other) {
        return new Estimation(other.timestamp(), other.duration());
    }
}
