package com.exasol.releasedroid.adapter.repository;

import static org.eclipse.jgit.lib.Constants.R_TAGS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LocalRepositoryGateTest {

    @Test
    void latestTagFromRefs() {
        final List<Ref> refs = List.of( //
                mockRef("1.2.3"), //
                mockRef("v1.2.1"));
        final Optional<String> actual = LocalRepositoryGate.latestTagFromRefs(refs);
        assertAll(() -> assertThat(actual.isPresent(), is(true)), //
                () -> assertThat(actual.get(), equalTo("1.2.3")));
    }

    @Test
    void emptyRefs() {
        final Optional<String> actual = LocalRepositoryGate.latestTagFromRefs(List.of());
        assertThat(actual.isPresent(), is(false));
    }

    private Ref mockRef(final String name) {
        final Ref ref = Mockito.mock(Ref.class);
        when(ref.getName()).thenReturn(R_TAGS + name);
        return ref;
    }
}
