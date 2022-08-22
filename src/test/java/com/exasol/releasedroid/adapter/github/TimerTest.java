package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TimerTest {

    @Test
    void timeoutFalse() throws InterruptedException {
        final Timer reminder = new Timer().withTimeout(Duration.ofMinutes(1)).start();
        assertThat(reminder.timeout(), is(false));
    }

    @Test
    void timeoutTrue() throws InterruptedException {
        final Timer reminder = new Timer().withTimeout(Duration.ofMinutes(-1)).start();
        assertThat(reminder.timeout(), is(true));
    }

    @ParameterizedTest
    @CsvSource(value = { ",false", "1,false", "-1,true" })
    void inspection(final Integer secondsBeforeNextCallback, final boolean expected) {
        Duration callbackInterval = null;
        if (secondsBeforeNextCallback != null) {
            callbackInterval = Duration.ofMinutes(secondsBeforeNextCallback);
        }
        final Timer reminder = new Timer() //
                .withSnoozeInterval(callbackInterval) //
                .start() //
                .snooze();
        assertThat(reminder.alarm(), is(expected));
    }

    @Test
    void initialInspection() {
        final Timer reminder = new Timer() //
                .withSnoozeInterval(Duration.ofHours(1)) //
                .start();
        assertThat(reminder.alarm(), is(true));
        reminder.snooze();
        assertThat(reminder.alarm(), is(false));
    }

    @ParameterizedTest
    @CsvSource(value = { ", false", "-1, true", "1, false" })
    void timeout(final Integer timeout, final boolean expected) {
        final Timer reminder = new Timer();
        if (timeout != null) {
            reminder.withTimeout(Duration.ofHours(timeout));
        }
        reminder.start();
        assertThat(reminder.timeout(), is(expected));
    }
}
