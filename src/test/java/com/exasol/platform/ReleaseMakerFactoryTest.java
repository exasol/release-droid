package com.exasol.platform;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.exasol.ReleaseMakerJava;
import com.exasol.ReleasePlatform;

class ReleaseMakerFactoryTest {
    @Test
    void testGetReleaseMaker() {
        assertThat(ReleaseMakerFactory.getReleaseMaker("name", Set.of(ReleasePlatform.GITHUB)),
                instanceOf(ReleaseMakerJava.class));
    }
}