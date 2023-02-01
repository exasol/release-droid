package com.exasol.releasedroid.output.guide;

import static com.exasol.releasedroid.Lines.lines;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.usecases.repository.RepositoryGate;

class ShortTagTest {
    @Test
    void shortest() {
        final RepositoryGate gate = mock(RepositoryGate.class);
        when(gate.getSingleFileContentAsString(any())).thenReturn(lines( //
                "error-tags:", //
                "  RD:", //
                "    packages:", //
                "      - com.exasol.releasedroid.main", //
                "      - com.exasol.releasedroid.output", //
                "    highest-index: 22", //
                "  RD-VAL:", //
                "    highest-index: 21", //
                "  RD-REP:"));
        final ShortTag testee = new ShortTag(gate);
        assertThat(testee.retrieve(), equalTo("RD"));
    }
}
